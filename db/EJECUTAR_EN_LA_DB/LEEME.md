# Qué ejecutar en la base `WmsWden`

En PostgreSQL ya no existe `WmsWdGeneral`: es el esquema **`general`**, y cada cliente es su propio esquema
(por ejemplo `wmswdlegacycostarica` = cliente 85). Por eso hay dos carpetas:

```
EJECUTAR_EN_LA_DB/
├── 1_GENERAL_una_sola_vez/          → esquema general. Se corre UNA vez, sin importar cuántos clientes haya
└── 2_POR_CLIENTE_repetir/           → esquema de cada cliente. Se corre una vez POR CADA cliente
    ├── cliente_85_wmswdlegacycostarica/   (listo para correr)
    └── generar_para_otro_cliente.py       (crea la misma carpeta para otro cliente)
```

El esquema `dbo` que se ve en DBeaver no se usa con estos scripts; solo `general` y los esquemas de cliente.
Cada archivo se ejecuta **completo como script** (DBeaver: abrirlo y **Alt+X**; con Ctrl+Enter solo corre la sentencia del cursor).
Todos se pueden repetir sin dañar nada.

## Paso 1 — `1_GENERAL_una_sola_vez/` (una vez)

| Archivo | Qué crea en `general` |
|---|---|
| `01_modulo_autogestion.sql` | Módulo 46 "Autogestión" en Gestión de Perfiles |
| `02_boletas_y_reversa.sql` | Boletas con consecutivo, respaldo de Despachado y la función `general.col_length` |
| `03_notificaciones_documentos.sql` | Notificaciones por correo, plantillas de documentos, cierres de prealerta, smartcard automática, módulos 44 y 47 |
| `04_parametros.sql` | Interruptores nuevos (apagados), campos calculados por código SAP, ajustes por cliente, base Almacén, variables de etiquetas, módulo 48, códigos de depreciación |
| `99_verificar_general.sql` | Comprueba lo anterior (cada fila debe decir OK) |

## Paso 2 — `2_POR_CLIENTE_repetir/cliente_85_wmswdlegacycostarica/` (por cada cliente)

Correr **después** del paso 1.

| Archivo | Qué hace en el esquema del cliente |
|---|---|
| `01_cosmetica_calidad_despacho.sql` | Cosmética (técnico asignado), extras de Calidad, `despacho.modeloid` + `pa_insertdispatch`, tabla `depreciados` |
| `02_autogestion.sql` | Módulo Autogestión completo (reglas SCRAP y de seriales empacados). **Reemplaza la versión anterior** |
| `03_permisos_menus_maestros.sql` | Permisos y work flow de Despachado, menús ADNOT/ADDOC/ADVAR/PAUT, novedad de accesorios, tipos de maestro. También escribe en `general` (permisos y módulos de los perfiles ADMINISTRADOR de ese cliente) |
| `04_funciones_modificadas.sql` | `pa_searchpalletboxdispatchpallet` (columna CAJAS) y `pa_getrepairuser` |
| `99_verificar_cliente.sql` | Comprueba lo anterior (cada fila debe decir OK) |

### Para otro cliente
Con Python:

```bash
cd db/EJECUTAR_EN_LA_DB/2_POR_CLIENTE_repetir
python generar_para_otro_cliente.py wmswdtelefonicacolombiabog 12
```

(`12` = id del cliente: `SELECT id, nombre FROM general.cliente;`). Crea `cliente_12_wmswdtelefonicacolombiabog/` con los mismos archivos.

Sin Python: copia la carpeta del 85 y, en los 5 archivos, reemplaza `wmswdlegacycostarica` por el esquema nuevo; en `03_permisos_menus_maestros.sql` cambia además `v_clienteid := 85`.

Requisito: el esquema del cliente debe tener ya sus tablas y funciones (para Telefónica: `db/DLLTelefonica`).

## Qué NO está incluido (pendiente)
El backend ya llama a estas cuatro funciones de Calidad, pero no existen en Postgres porque faltan sus definiciones de SQL Server. Hasta entonces solo fallan esas pantallas (extras de Calidad y auditoría in-situ):
`pa_searchqualityentryextras`, `pa_searchauditoriainsituentry(integer)`, `pa_updatequalityextrasbyserial`, `pa_updateestadocalidadbyserial`.

La tabla `depreciados` (archivo 01 del cliente) es una suposición: solo se sabe que tiene `serialnumber`. Si la real tiene más columnas, reemplázala por tu DDL.

## Después de ejecutar
1. Corre los dos `99_verificar_*.sql`.
2. Reinicia el backend y vuelve a iniciar sesión (los menús y módulos nuevos se ven al reingresar).
3. Los interruptores nuevos quedan **apagados**; se encienden por cliente en Administración > Clientes > Validaciones.
4. Variables de entorno del correo (la contraseña nunca va en un archivo del repositorio):
   * Notificaciones y documentos: `SMTP_SERVER`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`, `SMTP_FROM_NAME`.
   * Enlaces de aprobación de Autogestión: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PASS`.
