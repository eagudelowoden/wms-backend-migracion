#!/usr/bin/env python3
"""
Corrige un archivo de funciones exportado de PostgreSQL (convertido desde SQL Server) para que se comporte
como el sistema original. Aplica, funcion por funcion:

  1. (0)::timestamp en dateadd/datediff        -> date_trunc('unidad', ...)
  2. 'FROM' || v_db / 'JOIN' || v_db (sin espacio) -> 'FROM ' || v_db
  3. columna_boolean = 1 / = 0                  -> = true / = false
  4. columna = p_param (nombre, codigo, ...)    -> LOWER(col) = LOWER(p_param)      (SQL Server no distingue mayusculas)
  5. p_param = 'texto' / IN ('a','b')           -> LOWER(p_param) = LOWER('texto')
  6. pa_insertpackingwebd / webd2               -> candado por serial (evita duplicate key en empaque_unique)

Uso:
    python corregir_funciones.py ENTRADA.sql SALIDA.sql [--columnas-bool activo,cruce,...]

Revisa SIEMPRE el resumen que imprime y prueba primero en una copia. No reemplaza la revision manual de:
funciones que no vinieron en la exportacion, SQL dinamico con '@variable' y funciones cuyas columnas no
coinciden con lo que lee el backend (ver docs/MIGRACION_OTROS_CLIENTES.md).
"""
import argparse
import collections
import re
import sys

COLUMNAS_BOOL = ["v_esmultimodelo", "activo", "sobrante", "cruce", "usuarioactivo", "bajas",
                 "asignacionacc", "asignacionfalla", "multimodelo", "smartcard", "validacion", "reimpresion"]

# ---------------------------------------------------------------- 1. (0)::timestamp
_HEAD = re.compile(r"mssql\.dateadd\(\s*'(\w+)'\s*,\s*mssql\.datediff\(\s*'\1'\s*,\s*\(\s*0\s*\)::timestamp\s*,", re.I)
_TAIL = re.compile(r"\s*,\s*\(\s*0\s*\)::timestamp\s*\)")


def _balanceada(s, i):
    d, j = 0, i
    while j < len(s):
        c = s[j]
        if c == "'":
            j = s.index("'", j + 1)
        elif c == "(":
            d += 1
        elif c == ")":
            if d == 0:
                return s[i:j], j
            d -= 1
        j += 1
    raise ValueError("parentesis sin cerrar")


def fix_date_trunc(texto):
    out, pos, n = [], 0, 0
    for m in _HEAD.finditer(texto):
        if m.start() < pos:
            continue
        expr, j = _balanceada(texto, m.end())
        tail = _TAIL.match(texto, j + 1)
        if not tail:
            continue
        out.append(texto[pos:m.start()])
        out.append("date_trunc('%s', (%s)::timestamp)" % (m.group(1).lower(), expr.strip()))
        pos = tail.end()
        n += 1
    out.append(texto[pos:])
    return "".join(out), n


# ---------------------------------------------------------------- 2. espacio FROM/JOIN
def fix_espacios(texto):
    return re.subn(r"\b(FROM|JOIN)' \|\|", r"\1 ' ||", texto)


# ---------------------------------------------------------------- 3. boolean = 1/0
def fix_boolean(texto, columnas):
    cols = "(?:" + "|".join(re.escape(c) for c in columnas) + ")"
    pat = re.compile(r"(?<![\w$])((?:[a-z_][a-z0-9_]*\.)?" + cols + r")(\s*)(=|<>|!=)(\s*)([01])(?![\w.])", re.I)
    n = 0

    def rp(m):
        nonlocal n
        n += 1
        return "%s%s%s%s%s" % (m.group(1), m.group(2), m.group(3), m.group(4), "true" if m.group(5) == "1" else "false")
    return pat.sub(rp, texto), n


# ---------------------------------------------------------------- 4. columna = parametro
_COL = r"(?:nombre|codigo|descripcion|tipo|tipoaccesorio|detalle|name)"
_PAT_COL = re.compile(r"(?i)\b(WHERE|AND|OR|ON|WHEN|IF|ELSIF|NOT)(\s+|\s*\(\s*)((?:\w+\.)?" + _COL + r")(\s*)=(\s*)(p_\w+)(?![\w(])")


def fix_lower_columna(texto):
    n = 0

    def rp(m):
        nonlocal n
        n += 1
        return "%s%sLOWER(%s) = LOWER(%s)" % (m.group(1), m.group(2), m.group(3), m.group(6))
    return _PAT_COL.sub(rp, texto), n


# ---------------------------------------------------------------- 5. parametro = 'literal'
_LIT = re.compile(r"(?<![\w$.])(p_\w+)(\s*)(=|<>|!=)(\s*)('(?:[^']|'')+')")
_IN = re.compile(r"(?<![\w$.])(p_\w+)(\s+)(NOT\s+IN|IN)(\s*)\(((?:\s*'(?:[^']|'')*'\s*,?)+)\)", re.I)


def fix_lower_literal(texto):
    n = 0

    def r1(m):
        nonlocal n
        n += 1
        return "LOWER(%s)%s%s%sLOWER(%s)" % (m.group(1), m.group(2), m.group(3), m.group(4), m.group(5))

    def r2(m):
        nonlocal n
        n += 1
        items = re.findall(r"'(?:[^']|'')*'", m.group(5))
        return "LOWER(%s)%s%s%s(%s)" % (m.group(1), m.group(2), m.group(3), m.group(4),
                                         ", ".join("LOWER(%s)" % i for i in items))
    texto = _IN.sub(r2, texto)
    texto = _LIT.sub(r1, texto)
    return texto, n


# ---------------------------------------------------------------- 6. candado de empaque
def fix_candado(texto):
    nl = chr(10)
    nuevo = texto.replace(nl + "BEGIN" + nl,
                          nl + "BEGIN" + nl + "    PERFORM pg_advisory_xact_lock(hashtext('empaque:' || p_serialid::text));" + nl, 1)
    return nuevo, int(nuevo != texto)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("entrada")
    ap.add_argument("salida")
    ap.add_argument("--columnas-bool", help="columnas boolean separadas por coma (por defecto, la lista del cliente 85)")
    a = ap.parse_args()
    columnas = [c.strip() for c in a.columnas_bool.split(",")] if a.columnas_bool else COLUMNAS_BOOL

    texto = open(a.entrada, encoding="utf-8").read()
    partes = re.split(r"(?=CREATE OR REPLACE FUNCTION )", texto)
    log = collections.defaultdict(collections.Counter)
    salida = []
    for parte in partes:
        m = re.match(r"CREATE OR REPLACE FUNCTION [\w\"]+\.(\w+)\(", parte)
        if not m:
            salida.append(parte)
            continue
        fn = m.group(1)
        for etiqueta, f in (("date_trunc", fix_date_trunc), ("espacio FROM/JOIN", fix_espacios),
                            ("boolean 0/1", lambda t: fix_boolean(t, columnas)),
                            ("LOWER columna", fix_lower_columna), ("LOWER literal", fix_lower_literal)):
            parte, n = f(parte)
            if n:
                log[fn][etiqueta] += n
        if fn in ("pa_insertpackingwebd", "pa_insertpackingwebd2"):
            parte, n = fix_candado(parte)
            if n:
                log[fn]["candado empaque"] += n
        salida.append(parte)
    open(a.salida, "w", encoding="utf-8").write("".join(salida))

    total = collections.Counter()
    for c in log.values():
        total.update(c)
    print("Funciones modificadas: %d de %d" % (len(log), sum(1 for p in partes if p.startswith("CREATE OR REPLACE FUNCTION"))))
    print("Reemplazos por tipo:", dict(total))
    try:  # validacion opcional de sintaxis si esta instalado pglast (pip install pglast)
        import pglast
        malas = 0
        for parte in re.split(r"(?=CREATE OR REPLACE FUNCTION )", "".join(salida)):
            m = re.match(r"CREATE OR REPLACE FUNCTION [\w\"]+\.(\w+)\(", parte)
            if m and m.group(1) in log:
                try:
                    pglast.parse_plpgsql(parte.split("\n;\n")[0].rstrip().rstrip(";") + ";")
                except Exception as e:  # noqa: BLE001
                    malas += 1
                    print("  SINTAXIS?", m.group(1), str(e)[:80])
        print("Validacion de sintaxis (pglast): %d con problemas" % malas)
    except ImportError:
        print("(instala 'pip install pglast' para validar la sintaxis de las funciones modificadas)")


if __name__ == "__main__":
    sys.exit(main())
