#!/usr/bin/env python3
"""
Convierte el DDL de SQL Server (export de DataGrip/SSMS: tablas, indices) a PostgreSQL.

Genera en la carpeta de salida:
  01_tablas.sql        tablas (PK, UNIQUE, CHECK, DEFAULT, identity) sin FKs
  02_indices.sql       indices (INCLUDE y filtrados incluidos)
  03_foreign_keys.sql  llaves foraneas (aplicar DESPUES de cargar datos, o no aplicar)
  columnas_bool.txt    columnas bit -> boolean (para corregir_funciones.py --columnas-bool)
  resumen.txt          conteos y avisos

Uso:
    python ddl_sqlserver_a_postgres.py DDL.sql CARPETA_SALIDA ESQUEMA
"""
import os
import re
import sys

entrada, salida, esquema = sys.argv[1], sys.argv[2], sys.argv[3].lower()
texto = open(entrada, encoding="utf-8-sig", errors="replace").read()
bloques = re.split(r"(?im)^go\s*$", texto)
avisos = []


def ident(n):
    return re.sub(r"[^\w]", "_", n.strip("[]")).lower()


def tipo_pg(t):
    t = t.strip().lower()
    m = re.match(r"(\w+)\s*(?:\(\s*([\w, ]+)\s*\))?", t)
    base, arg = m.group(1), (m.group(2) or "").replace(" ", "")
    if base == "int":
        return "integer"
    if base == "bigint":
        return "bigint"
    if base == "smallint":
        return "smallint"
    if base == "tinyint":
        return "smallint"
    if base == "bit":
        return "boolean"
    if base in ("varchar", "nvarchar"):
        return "text" if arg == "max" else "varchar(%s)" % arg
    if base in ("char", "nchar"):
        return "char(%s)" % (arg or "1")
    if base in ("text", "ntext"):
        return "text"
    if base == "datetime":
        return "timestamp(3)"
    if base == "datetime2":
        return "timestamp(%s)" % (arg or "6")
    if base == "smalldatetime":
        return "timestamp(0)"
    if base == "date":
        return "date"
    if base == "time":
        return "time"
    if base in ("decimal", "numeric"):
        return "numeric(%s)" % arg if arg else "numeric"
    if base == "money":
        return "numeric(19,4)"
    if base == "float":
        return "double precision"
    if base == "real":
        return "real"
    if base in ("image", "varbinary", "binary"):
        return "bytea"
    if base == "uniqueidentifier":
        return "uuid"
    avisos.append("tipo no mapeado: %s" % t)
    return "text /* %s */" % t


def valor_pg(v, tipo):
    v = v.strip()
    while v.startswith("(") and v.endswith(")"):
        v = v[1:-1].strip()
    if tipo == "boolean":
        if v in ("0", "1"):
            return "true" if v == "1" else "false"
    low = v.lower()
    if low in ("getdate()", "sysdatetime()", "current_timestamp"):
        return "LOCALTIMESTAMP"
    v = re.sub(r"(?i)^n'", "'", v)
    return v


def partir_coma(s):
    partes, d, ini, q = [], 0, 0, False
    for i, c in enumerate(s):
        if c == "'":
            q = not q
        elif not q:
            if c == "(":
                d += 1
            elif c == ")":
                d -= 1
            elif c == "," and d == 0:
                partes.append(s[ini:i])
                ini = i + 1
    partes.append(s[ini:])
    return [p.strip() for p in partes if p.strip()]


def cols_lista(s):
    return ", ".join(ident(c.split()[0]) for c in s.split(","))


def check_pg(expr):
    e = re.sub(r"\[(\w+)\]", lambda m: m.group(1).lower(), expr)
    return e


def fk_acciones(tail):
    out = ""
    m = re.search(r"(?i)on\s+delete\s+(cascade|set null|set default|no action)", tail)
    if m:
        out += " ON DELETE " + m.group(1).upper()
    m = re.search(r"(?i)on\s+update\s+(cascade|set null|set default|no action)", tail)
    if m:
        out += " ON UPDATE " + m.group(1).upper()
    return out


tablas, fks, indices, bits = [], [], [], set()
nombres_idx = set()
resumen = {"tablas": 0, "indices": 0, "fks": 0}

for b in bloques:
    m = re.match(r"\s*create\s+table\s+dbo\.(\w+)\s*\((.*)\)\s*$", b, re.I | re.S)
    if not m:
        continue
    tabla = ident(m.group(1))
    items = partir_coma(m.group(2))
    defs, extras, ident_col = [], [], None
    for it in items:
        if re.match(r"(?i)constraint\s+\S+\s+(primary key|unique|foreign key|check)", it):
            cn = re.match(r"(?i)constraint\s+(\S+)\s+(.*)$", it, re.S)
            nombre, rest = ident(cn.group(1)), cn.group(2)
            mm = re.match(r"(?i)primary key(?:\s+clustered|\s+nonclustered)?\s*\(([^)]*)\)", rest)
            if mm:
                extras.append("CONSTRAINT %s PRIMARY KEY (%s)" % (nombre, cols_lista(re.sub(r"(?i)\s+(asc|desc)", "", mm.group(1)))))
                continue
            mm = re.match(r"(?i)unique(?:\s+clustered|\s+nonclustered)?\s*\(([^)]*)\)", rest)
            if mm:
                extras.append("CONSTRAINT %s UNIQUE (%s)" % (nombre, cols_lista(re.sub(r"(?i)\s+(asc|desc)", "", mm.group(1)))))
                continue
            mm = re.match(r"(?i)foreign key\s*\(([^)]*)\)\s*references\s+(?:dbo\.)?(\w+)\s*(?:\(([^)]*)\))?(.*)$", rest, re.S)
            if mm:
                fks.append((tabla, nombre, cols_lista(mm.group(1)), ident(mm.group(2)), cols_lista(mm.group(3)) if mm.group(3) else None, fk_acciones(mm.group(4))))
                continue
            mm = re.match(r"(?i)check\s*(\(.*\))\s*$", rest, re.S)
            if mm:
                extras.append("CONSTRAINT %s CHECK %s" % (nombre, check_pg(mm.group(1))))
                continue
            avisos.append("constraint no soportado en %s: %s" % (tabla, it[:80]))
            continue
        mk = re.match(r"(?i)(primary key|unique)\s*(?:clustered\s+|nonclustered\s+)?\(([^)]*)\)\s*$", it)
        if mk:  # PK/UNIQUE de tabla sin nombre
            pref = "pk" if mk.group(1).lower() == "primary key" else "uq"
            extras.append("CONSTRAINT %s_%s %s (%s)" % (pref, tabla, mk.group(1).upper(), cols_lista(re.sub(r"(?i)\s+(asc|desc)", "", mk.group(2)))))
            continue
        mm = re.match(r"(\[?\w+\]?)\s+(\w+(?:\s*\([^)]*\))?)\s*(.*)$", it, re.S)
        if not mm:
            avisos.append("columna no reconocida en %s: %s" % (tabla, it[:80]))
            continue
        col, tipo_sql, tail = ident(mm.group(1)), mm.group(2), mm.group(3)
        tipo = tipo_pg(tipo_sql)
        if tipo == "boolean":
            bits.add(col)
        partes = []
        notnull, default, identidad, pk, uniq = False, None, False, None, None
        t = tail
        if re.search(r"(?i)\bidentity\b", t):
            identidad = True
            t = re.sub(r"(?i)\bidentity(\s*\(\s*\d+\s*,\s*\d+\s*\))?", " ", t)
        # constraints en linea
        for cm in re.finditer(r"(?is)constraint\s+(\S+)\s+(primary key|unique|default\s+(?:\((?:[^()]|\([^()]*\))*\)|'[^']*'|[\w.+\-]+(?:\(\))?)|check\s*\((?:[^()]|\([^()]*\))*\)|references\s+(?:dbo\.)?\w+\s*(?:\([^)]*\))?(?:\s+on\s+(?:update|delete)\s+(?:cascade|set null|set default|no action))*)", t):
            nombre, cuerpo = ident(cm.group(1)), cm.group(2)
            lc = cuerpo.lower()
            if lc.startswith("primary key"):
                pk = nombre
            elif lc.startswith("unique"):
                uniq = nombre
            elif lc.startswith("default"):
                default = valor_pg(cuerpo[7:], tipo)
            elif lc.startswith("check"):
                extras.append("CONSTRAINT %s CHECK %s" % (nombre, check_pg(cuerpo[5:].strip())))
            elif lc.startswith("references"):
                rm = re.match(r"(?is)references\s+(?:dbo\.)?(\w+)\s*(?:\(([^)]*)\))?(.*)$", cuerpo)
                fks.append((tabla, nombre, col, ident(rm.group(1)), cols_lista(rm.group(2)) if rm.group(2) else None, fk_acciones(rm.group(3))))
        t2 = re.sub(r"(?is)constraint\s+\S+\s+(primary key|unique|default\s+(?:\((?:[^()]|\([^()]*\))*\)|'[^']*'|[\w.+\-]+(?:\(\))?)|check\s*\((?:[^()]|\([^()]*\))*\)|references\s+(?:dbo\.)?\w+\s*(?:\([^)]*\))?(?:\s+on\s+(?:update|delete)\s+(?:cascade|set null|set default|no action))*)", " ", t)
        if re.search(r"(?i)\bnot\s+null\b", t2):
            notnull = True
        if re.search(r"(?i)\bprimary\s+key\b", t2):
            pk = pk or "pk_%s" % tabla
        elif re.search(r"(?i)\bunique\b", t2):
            uniq = uniq or "uq_%s_%s" % (tabla, col)
        dm = re.search(r"(?is)\bdefault\s+(\((?:[^()]|\([^()]*\))*\)|'[^']*'|[\w.+\-]+(?:\(\))?)", t2)
        if dm and default is None:
            default = valor_pg(dm.group(1), tipo)
        if re.search(r"(?i)\bas\s*\(", t2) or re.search(r"(?i)^\s*as\b", t2):
            avisos.append("columna calculada en %s.%s: revisar a mano" % (tabla, col))
        linea = "%s %s" % (col, tipo)
        if identidad:
            linea += " GENERATED BY DEFAULT AS IDENTITY"
            ident_col = col
        if default is not None:
            linea += " DEFAULT %s" % default
        if notnull or pk:
            linea += " NOT NULL"
        if pk:
            linea += " CONSTRAINT %s PRIMARY KEY" % pk
        if uniq:
            linea += " CONSTRAINT %s UNIQUE" % uniq
        defs.append(linea)
    tablas.append((tabla, defs + extras))
    resumen["tablas"] += 1

idx_re = re.compile(
    r"(?is)^\s*create\s+(unique\s+)?(?:nonclustered\s+|clustered\s+)?index\s+(\[[^\]]*\]|\w+)\s+on\s+dbo\.(\w+)\s*\(([^)]*)\)\s*(?:include\s*\(([^)]*)\))?\s*(?:where\s+(.*?))?\s*$")
for b in bloques:
    m = idx_re.match(b)
    if not m:
        continue
    uniq, nombre, tabla, cols, inc, where = m.groups()
    nom = ident(nombre)
    nom = re.sub(r"^_+|_+$", "", nom) or "idx"
    if nom in nombres_idx:
        nom = "%s_%s" % (ident(tabla), nom)
    base = nom
    k = 2
    while nom in nombres_idx:
        nom = "%s_%d" % (base, k)
        k += 1
    nombres_idx.add(nom)
    cl = ", ".join(re.sub(r"\[(\w+)\]", lambda x: x.group(1).lower(), c.strip()).lower() for c in cols.split(","))
    s = "CREATE %sINDEX IF NOT EXISTS %s ON %s (%s)" % ("UNIQUE " if uniq else "", nom, ident(tabla), cl)
    if inc:
        s += " INCLUDE (%s)" % ", ".join(ident(c.strip()) for c in inc.split(","))
    if where:
        w = re.sub(r"\[(\w+)\]", lambda x: x.group(1).lower(), where.strip())
        w = re.sub(r"(?i)(\w+)\s*=\s*1\b", r"\1 = true", w) if any(b_ in w.lower() for b_ in bits) else w
        s += " WHERE %s" % w
    indices.append(s + ";")
    resumen["indices"] += 1

os.makedirs(salida, exist_ok=True)
with open(os.path.join(salida, "01_tablas.sql"), "w", encoding="utf-8") as f:
    f.write("-- Tablas de %s (generado desde el DDL de SQL Server)\n" % esquema)
    f.write("CREATE SCHEMA IF NOT EXISTS %s;\nSET search_path TO %s, public;\n\n" % (esquema, esquema))
    for tabla, cuerpo in tablas:
        f.write("CREATE TABLE IF NOT EXISTS %s.%s (\n    %s\n);\n\n" % (esquema, tabla, ",\n    ".join(cuerpo)))
with open(os.path.join(salida, "02_indices.sql"), "w", encoding="utf-8") as f:
    f.write("SET search_path TO %s, public;\n\n" % esquema)
    f.write("\n".join(indices) + "\n")
with open(os.path.join(salida, "03_foreign_keys.sql"), "w", encoding="utf-8") as f:
    f.write("-- Llaves foraneas. Aplicar DESPUES de cargar los datos.\n-- OJO: en SQL Server algunas estaban deshabilitadas (NOCHECK); ver LEEME.md.\nSET search_path TO %s, public;\n\n" % esquema)
    vistos = set()
    for tabla, nombre, cols, ref, refcols, acc in fks:
        if (tabla, nombre) in vistos:
            nombre = nombre + "_2"
        vistos.add((tabla, nombre))
        f.write("ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s%s%s;\n" % (
            tabla, nombre, cols, ref, " (%s)" % refcols if refcols else "", acc))
        resumen["fks"] += 1
open(os.path.join(salida, "columnas_bool.txt"), "w").write(",".join(sorted(bits)) + "\n")
with open(os.path.join(salida, "resumen.txt"), "w", encoding="utf-8") as f:
    f.write("%s\n" % resumen)
    f.write("columnas boolean: %s\n" % ",".join(sorted(bits)))
    f.write("avisos (%d):\n  %s\n" % (len(avisos), "\n  ".join(sorted(set(avisos)))))
print(resumen, "avisos:", len(set(avisos)))
