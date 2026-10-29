#!/usr/bin/env python3
"""
Arma el archivo de funciones de OTRO cliente a partir de las funciones ya corregidas del cliente 85.

  - toma las funciones de los archivos de origen (el ultimo archivo gana si una funcion se repite)
  - se queda solo con las que existen como SP en el DDL del cliente nuevo
  - cambia el esquema (wmswdlegacycostarica -> el nuevo)
  - aplica boolean (col = 1 -> col = true) con las columnas bit del DDL nuevo
  - valida la sintaxis con pglast

Uso:
    python generar_funciones_cliente.py DDL_SQLSERVER.sql SALIDA.sql ESQUEMA_NUEVO COLUMNAS_BOOL_TXT ORIGEN1.sql [ORIGEN2.sql ...]
"""
import re
import sys

import corregir_funciones as cf

ddl, salida, esquema, bools_txt, *origenes = sys.argv[1:]
esquema = esquema.lower()
VIEJO = "wmswdlegacycostarica"

sp = set(m.lower() for m in re.findall(
    r"(?im)^create\s+(?:or\s+alter\s+)?(?:proc(?:edure)?|function)\s+(?:\[?dbo\]?\.)?\[?(\w+)\]?",
    open(ddl, encoding="utf-8-sig", errors="replace").read()))
columnas = [c for c in open(bools_txt).read().strip().split(",") if c]
columnas = sorted(set(columnas) | set(cf.COLUMNAS_BOOL))

funciones = {}
for o in origenes:
    texto = open(o, encoding="utf-8").read()
    for parte in re.split(r"(?=CREATE OR REPLACE FUNCTION )", texto):
        m = re.match(r"CREATE OR REPLACE FUNCTION [\w\"]+\.(\w+)\(", parte)
        if not m:
            continue
        cuerpo = parte
        # quitar la linea de comentario "-- DROP FUNCTION ..." de la siguiente funcion que quedo pegada al final
        cuerpo = re.sub(r"\n--[^\n]*\Z", "", cuerpo.rstrip()) + "\n"
        funciones[m.group(1).lower()] = cuerpo

salida_txt, n_bool, faltan = [], 0, sorted(sp - set(funciones))
for nombre in sorted(funciones):
    if nombre not in sp:
        continue
    f = funciones[nombre].replace(VIEJO, esquema)
    f, n = cf.fix_boolean(f, columnas)
    n_bool += n
    salida_txt.append(f.rstrip() + "\n")

contenido = ("-- Funciones de %s generadas desde las del cliente 85 (%d funciones).\n"
             "-- SET search_path no hace falta: todo va calificado con el esquema.\n\n" % (esquema, len(salida_txt))) \
            + "\n".join(salida_txt)
open(salida, "w", encoding="utf-8").write(contenido)

import pglast
malas = 0
for parte in re.split(r"(?=CREATE OR REPLACE FUNCTION )", contenido):
    if parte.startswith("CREATE OR REPLACE FUNCTION"):
        try:
            pglast.parse_plpgsql(parte.rstrip().rstrip(";") + ";")
        except Exception as e:  # noqa: BLE001
            malas += 1
            print("SINTAXIS?", parte[:90].replace("\n", " "), str(e)[:80])
print("funciones escritas: %d | reemplazos boolean: %d | con problemas de sintaxis: %d" % (len(salida_txt), n_bool, malas))
print("SP del DDL sin funcion en los origenes (%d): %s" % (len(faltan), ", ".join(faltan)))
