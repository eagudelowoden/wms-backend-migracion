#!/usr/bin/env python3
"""
Revisa la sintaxis PostgreSQL de TODAS las consultas nativas (@Query ... nativeQuery = true) de los repositorios.
Detecta restos de SQL Server (EXEC, TOP, WITH(NOLOCK), corchetes...) que PostgreSQL no entiende.

Uso (desde la raiz del backend):  python db/herramientas/validar_queries.py
Requiere: pip install pglast
"""
import glob
import re

import pglast

LITERAL = re.compile(r'"((?:[^"\\]|\\.)*)"')
INICIO = re.compile(r"@Query\s*\(\s*value\s*=\s*")
PARAM = re.compile(r"(?<![:\w]):(\w+)")

total = malas = 0
for ruta in sorted(glob.glob("src/main/java/**/repositories/**/*.java", recursive=True)):
    texto = open(ruta, encoding="utf-8", errors="replace").read()
    for m in INICIO.finditer(texto):
        # recorre literales "..." concatenados con + hasta llegar a ", nativeQuery"
        pos, partes = m.end(), []
        while True:
            lit = re.compile(r"\s*\+?\s*").match(texto, pos)
            pos = lit.end()
            ml = LITERAL.match(texto, pos)
            if not ml:
                break
            partes.append(ml.group(1))
            pos = ml.end()
        resto = texto[pos:pos + 40]
        if "nativeQuery = true" not in resto:
            continue  # JPQL u otra cosa
        sql = "".join(partes).replace('\\"', '"')
        sql = PARAM.sub("NULL", sql)
        total += 1
        try:
            pglast.parse_sql(sql)
        except Exception as e:  # noqa: BLE001
            malas += 1
            print("%s: %s | %s" % (ruta.split("\\")[-1].split("/")[-1], str(e)[:80], sql[:110]))
print("%d consultas nativas revisadas, %d con problemas" % (total, malas))
