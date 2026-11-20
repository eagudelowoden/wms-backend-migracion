#!/usr/bin/env python3
"""
Crea la carpeta de scripts de OTRO cliente a partir de la del cliente 85.

Uso:
    python generar_para_otro_cliente.py ESQUEMA CLIENTE_ID
Ejemplo:
    python generar_para_otro_cliente.py wmswdtelefonicacolombiabog 12

Genera  cliente_<CLIENTE_ID>_<ESQUEMA>/  con los mismos archivos, cambiando:
  - el nombre del esquema (wmswdlegacycostarica -> ESQUEMA), y
  - el id del cliente (v_clienteid := 85 -> CLIENTE_ID).
El id del cliente es general.cliente.id (SELECT id, nombre FROM general.cliente).
"""
import os
import re
import sys

if len(sys.argv) != 3:
    sys.exit(__doc__)
esquema, cliente_id = sys.argv[1].lower(), int(sys.argv[2])
if not re.fullmatch(r"[a-z0-9_]+", esquema):
    sys.exit("El esquema solo puede tener letras minusculas, numeros y _")

aqui = os.path.dirname(os.path.abspath(__file__))
origen = os.path.join(aqui, "cliente_85_wmswdlegacycostarica")
destino = os.path.join(aqui, "cliente_%d_%s" % (cliente_id, esquema))
os.makedirs(destino, exist_ok=True)

for nombre in sorted(os.listdir(origen)):
    if not nombre.endswith(".sql"):
        continue
    t = open(os.path.join(origen, nombre), encoding="utf-8", newline="").read()
    t = t.replace("wmswdlegacycostarica", esquema)
    t = re.sub(r"v_clienteid(\s+)integer := 85;", "v_clienteid\\1integer := %d;" % cliente_id, t)
    open(os.path.join(destino, nombre), "w", encoding="utf-8", newline="").write(t)
    print("creado", os.path.join(os.path.basename(destino), nombre))

print("\nListo. Ejecuta los archivos 01 a 04 en ese orden y luego 99_verificar_cliente.sql.")
print("Recuerda: el esquema de ese cliente debe tener ya sus funciones (db/DLLTelefonica o el generador de funciones).")
