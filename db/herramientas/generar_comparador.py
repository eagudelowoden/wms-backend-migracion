#!/usr/bin/env python3
"""
Genera una consulta que, ejecutada en PostgreSQL, compara el esquema ESPERADO (lo convertido desde SQL Server con
ddl_sqlserver_a_postgres.py) contra el esquema REAL del cliente y lista lo que falta o difiere.

Uso:
    python generar_comparador.py 01_tablas.sql ESQUEMA SALIDA.sql
"""
import re
import sys

entrada, esquema, salida = sys.argv[1], sys.argv[2].lower(), sys.argv[3]
texto = open(entrada, encoding="utf-8").read()

NORMAL = [
    (r"^integer$", "integer"), (r"^bigint$", "bigint"), (r"^smallint$", "smallint"), (r"^boolean$", "boolean"),
    (r"^varchar", "character varying"), (r"^char", "character"), (r"^text", "text"),
    (r"^timestamp", "timestamp without time zone"), (r"^date$", "date"), (r"^time$", "time without time zone"),
    (r"^numeric", "numeric"), (r"^double precision$", "double precision"), (r"^real$", "real"),
    (r"^bytea$", "bytea"), (r"^uuid$", "uuid"),
]


def normal(t):
    t = t.strip().lower()
    for pat, val in NORMAL:
        if re.match(pat, t):
            return val
    return t


filas = []
for m in re.finditer(r"CREATE TABLE IF NOT EXISTS \w+\.(\w+) \(\n(.*?)\n\);", texto, re.S):
    tabla = m.group(1)
    for linea in m.group(2).split("\n"):
        linea = linea.strip().rstrip(",")
        if not linea or linea.startswith("CONSTRAINT"):
            continue
        partes = linea.split(None, 2)
        col, tipo = partes[0], partes[1]
        tipo = tipo.rstrip(",")
        if "/* as */" in linea:
            tipo = "calculada"
        filas.append((tabla, col, normal(tipo)))

valores = ",\n".join("        ('%s', '%s', '%s')" % f for f in filas)
sql = """-- Comparacion del esquema %(esq)s: ESPERADO (desde SQL Server) contra REAL (esta base).
-- Generado con db/herramientas/generar_comparador.py. Ejecutar completo; cada fila es algo por revisar.
--   TABLA FALTA      : la tabla no existe en Postgres
--   COLUMNA FALTA    : la tabla existe pero le falta la columna
--   TIPO DISTINTO    : la columna existe con otro tipo (ojo con boolean vs integer: el backend espera boolean en las banderas)
--   (columnas calculadas de SQL Server aparecen como 'calculada': se resuelven a mano)
WITH esperado(tabla, columna, tipo) AS (
    VALUES
%(valores)s
), real AS (
    SELECT lower(table_name) AS tabla, lower(column_name) AS columna, data_type AS tipo
    FROM information_schema.columns WHERE table_schema = '%(esq)s'
), tablas_reales AS (
    SELECT lower(table_name) AS tabla FROM information_schema.tables WHERE table_schema = '%(esq)s'
)
SELECT 'TABLA FALTA' AS problema, e.tabla, '' AS columna, '' AS esperado, '' AS real_
FROM (SELECT DISTINCT tabla FROM esperado) e
WHERE NOT EXISTS (SELECT 1 FROM tablas_reales t WHERE t.tabla = e.tabla)
UNION ALL
SELECT 'COLUMNA FALTA', e.tabla, e.columna, e.tipo, ''
FROM esperado e
WHERE EXISTS (SELECT 1 FROM tablas_reales t WHERE t.tabla = e.tabla)
  AND NOT EXISTS (SELECT 1 FROM real r WHERE r.tabla = e.tabla AND r.columna = e.columna)
UNION ALL
SELECT 'TIPO DISTINTO', e.tabla, e.columna, e.tipo, r.tipo
FROM esperado e JOIN real r ON r.tabla = e.tabla AND r.columna = e.columna
WHERE e.tipo <> 'calculada' AND e.tipo <> r.tipo
ORDER BY 1, 2, 3;
""" % {"esq": esquema, "valores": valores}
open(salida, "w", encoding="utf-8").write(sql)
print("tablas:", len({f[0] for f in filas}), "| columnas:", len(filas), "->", salida)
