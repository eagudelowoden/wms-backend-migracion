#!/usr/bin/env python3
"""
Pasada MECANICA T-SQL -> PostgreSQL sobre el SQL embebido en archivos Java (cadenas de consultas nativas / JDBC).
Solo toca modismos que se traducen sin ambiguedad. Lo demas (TOP, EXEC, CONVERT, DATEADD, OUTPUT, #temp, MERGE...)
se lista al final para revision manual.

Uso:  python tsql_a_postgres_java.py archivo1.java [archivo2.java ...]
"""
import re
import sys

COLUMNAS_BOOL = ["activo", "cruce", "garantiafabricante", "garantiawoden", "sobrante", "usuarioactivo", "bajas", "altas",
                 "smartcard", "validacion", "reimpresion", "asignacionacc", "asignacionfalla", "multimodelo", "gaveta",
                 "fuente", "confirmado", "garantia", "componenteobligatorio", "asignacion_confirmada"]

BAL = r"((?:[^()]|\((?:[^()]|\([^()]*\))*\))*)"  # contenido con parentesis balanceados (2 niveles)
BAL3 = r"((?:[^()]|\((?:[^()]|\((?:[^()]|\([^()]*\))*\))*\))*)"
EXPR = r"((?:[^(),]|\((?:[^()]|\([^()]*\))*\))+)"  # expresion sin comas de primer nivel

REGLAS = [
    (re.compile(r"\s+WITH\s*\(\s*(?:NOLOCK|READUNCOMMITTED)\s*\)", re.I), ""),
    (re.compile(r"\(\s*NOLOCK\s*\)", re.I), ""),
    (re.compile(r"(?<![\w.])COL_LENGTH\s*\(", re.I), "general.col_length("),  # funcion auxiliar: db/produccion_nov2026_postgres.sql
    (re.compile(r"\bSET\s+IDENTITY_INSERT\s+\w+\s+(?:ON|OFF)\s*;\s*", re.I), ""),
    # Estado/Usuario/Modulo/Permiso/Cliente eran vistas del esquema del cliente hacia WmsWdGeneral: ahora van a general.*
    (re.compile(r"\b(FROM|JOIN)(\s+)(Estado|Usuario|Modulo|Permiso|Cliente)\b(?!\s*\.)(?!\w)", re.I), r"\1\2general.\3"),
    (re.compile(r"\bISNULL\s*\(", re.I), "COALESCE("),
    (re.compile(r"\bGETDATE\s*\(\s*\)", re.I), "LOCALTIMESTAMP"),
    (re.compile(r"\bSYSDATETIME\s*\(\s*\)", re.I), "LOCALTIMESTAMP"),
    (re.compile(r"\[WmsWdGeneral\]\s*\.\s*\[?dbo\]?\s*\.\s*", re.I), "general."),
    (re.compile(r"\bWmsWdGeneral\s*\.\s*dbo\s*\.\s*", re.I), "general."),
    (re.compile(r"\bWmsWdGeneral\s*\.\.\s*", re.I), "general."),
    (re.compile(r"\[dbo\]\s*\.\s*", re.I), ""),
    (re.compile(r'(?<![\w.])dbo\.(?=[A-Za-z_\[])', re.I), ""),
    (re.compile(r"\bLTRIM\s*\(\s*RTRIM\s*\(" + BAL + r"\)\s*\)", re.I), r"TRIM(\1)"),
    (re.compile(r"\bRTRIM\s*\(\s*LTRIM\s*\(" + BAL + r"\)\s*\)", re.I), r"TRIM(\1)"),
    (re.compile(r"\bLTRIM\s*\(", re.I), "LTRIM("),
    (re.compile(r"\bLEN\s*\(", re.I), "LENGTH("),
    (re.compile(r"(?<![\w$])N'"), "'"),
    (re.compile(r"(?<![\w\])\.])\[([A-Za-z_]\w*)\]"), r"\1"),
    (re.compile(r"\bNCHAR\s*\(\s*(\d+)\s*\)", re.I), lambda m: "chr(%s)" % m.group(1)),
    (re.compile(r"\bCONVERT\s*\(\s*n?varchar\s*\(\s*(\d+)\s*\)\s*,\s*" + EXPR + r"\s*,\s*(?:120|121|23)\s*\)", re.I),
     lambda m: "to_char(%s, '%s')" % (m.group(2), {"16": "YYYY-MM-DD HH24:MI", "19": "YYYY-MM-DD HH24:MI:SS",
                                                   "10": "YYYY-MM-DD"}.get(m.group(1), "YYYY-MM-DD HH24:MI:SS"))),
    (re.compile(r"\bCONVERT\s*\(\s*date\s*,\s*" + EXPR + r"\s*\)", re.I), r"CAST(\1 AS date)"),
    (re.compile(r"\bCONVERT\s*\(\s*n?varchar\s*\(\s*\d+\s*\)\s*,\s*" + EXPR + r"\s*\)", re.I), r"CAST(\1 AS varchar)"),
    (re.compile(r"\bCONVERT\s*\(\s*int\s*,\s*" + EXPR + r"\s*\)", re.I), r"CAST(\1 AS integer)"),
    (re.compile(r"\bOBJECT_ID\s*\(\s*'([\w.]+)'\s*(?:,\s*'U'\s*)?\)\s+IS\s+(NOT\s+)?NULL", re.I),
     lambda m: "to_regclass('%s') IS %sNULL" % (m.group(1), m.group(2) or "")),
    (re.compile(r"\bCROSS\s+APPLY\s*\(", re.I), "CROSS JOIN LATERAL ("),
    (re.compile(r"\bOUTER\s+APPLY\s*\(" + BAL3 + r"\)\s+(\w+)", re.I), r"LEFT JOIN LATERAL (\1) \2 ON true"),
    (re.compile(r"\bSELECT\s+TOP\s+\(?\s*1\s*\)?\s+1\b", re.I), "SELECT 1"),  # SELECT TOP 1 1 -> SELECT 1 (falta LIMIT 1, ver aviso)
]

PAT_BOOL = re.compile(
    r"(?<![\w$])((?:[A-Za-z_][A-Za-z0-9_]*\.)?(?:%s))(\s*)(=|<>|!=)(\s*)([01])(?![\w.])" % "|".join(COLUMNAS_BOOL), re.I)

AVISOS = re.compile(
    r"\bEXEC(?:UTE)?\b|\bTOP\s*\(?\s*[\d@:?]|\bCONVERT\s*\(|\bDATEADD\s*\(|\bDATEDIFF\s*\(|\bOUTPUT\b|\bOPENJSON\b|"
    r"\bMERGE\s+(?:INTO\s+)?\w|#\w+|\bSET\s+NOCOUNT\b|\bDECLARE\s+@|\bOBJECT_ID\b|\bSCOPE_IDENTITY\b|@@|\bSTRING_AGG\b|"
    r"\bFOR\s+XML\b|\bCROSS\s+APPLY\b|\bOUTER\s+APPLY\b|\bOFFSET\s+[^\n]*\bROWS\b|\bIIF\s*\(|\bCHARINDEX\b|\bFORMAT\s*\(|"
    r"\bSTUFF\s*\(|\bNEWID\s*\(|\bBEGIN\s+TRAN|\bCOLLATE\b|\bDATENAME\b|\bDATEPART\b|\bEOMONTH\b|\bREPLICATE\b|\bSPACE\s*\(|"
    r"\bUPDLOCK\b|\bHOLDLOCK\b|\bsys\.\w+|\bsp_\w+|\bTRY_CONVERT\b|\bPATINDEX\b|\bTRY_CAST\b|\bTRY_CONVERT\b|\bFETCH\s+NEXT\b|\bUPDATE\s+\w+\s+SET\b[^\n]*\bFROM\b",
    re.I)

total = {}
for ruta in sys.argv[1:]:
    texto = open(ruta, encoding="utf-8", newline="").read()
    original = texto
    cambios = 0
    for pat, rep in REGLAS:
        texto, n = pat.subn(rep, texto)
        cambios += n
    texto, n = PAT_BOOL.subn(lambda m: "%s%s%s%s%s" % (m.group(1), m.group(2), m.group(3), m.group(4),
                                                        "true" if m.group(5) == "1" else "false"), texto)
    cambios += n
    if texto != original:
        open(ruta, "w", encoding="utf-8", newline="").write(texto)
    total[ruta] = cambios
    restos = []
    for i, linea in enumerate(texto.splitlines(), 1):
        s = linea.strip()
        if s.startswith("//") or s.startswith("*") or s.startswith("/*") or s.startswith("import "):
            continue
        if AVISOS.search(linea):
            restos.append((i, s[:170]))
    print("== %s: %d cambios, %d lineas para revisar a mano" % (ruta.split("\\")[-1].split("/")[-1], cambios, len(restos)))
    for i, s in restos:
        print("   %5d: %s" % (i, s))
