# Producción (rama develop, 30-oct-2026) → PostgreSQL

Cambios traídos de `develop` del proyecto de producción (32 commits, 73 archivos: boletas con consecutivo, Despachado,
notificaciones por correo, documentos/plantillas, cierre de prealertas, smartcard automática, campos calculados por
código SAP, variables de etiquetas, base Almacén, reemplazo de serial, etc.).

## Orden de ejecución

| # | Archivo | Dónde | Qué hace |
|---|---|---|---|
| 1 | `01_general_boletas_y_reversa.sql` | `general`, una vez | Boletas + consecutivo, respaldo de Despachado, función `general.col_length` |
| 2 | `02_general_notificaciones_documentos.sql` | `general`, una vez | Notificaciones, plantillas, cierres de prealerta, smartcard automática, módulos 44 y 47 |
| 3 | `03_general_parametros.sql` | `general`, una vez | Interruptores nuevos (apagados), campos calculados, ajustes, base Almacén, variables de etiquetas, módulo 48, códigos de depreciación |
| 4 | `04_cliente_por_esquema.sql` | **cada esquema de cliente** | Permisos y work flow de Despachado, menús ADNOT/ADDOC/ADVAR/PAUT, novedad de accesorios, tipos de maestro. Editar la 1.ª línea (esquema) y `v_clienteid` |
| 5 | `05_funciones.sql` | cada esquema (cambiar el nombre del esquema) | `pa_searchpalletboxdispatchpallet` (columna CAJAS) y `pa_getrepairuser` |

Además, ya existentes en `db/`: `autogestion_postgres.sql`, `autogestion_modulo_postgres.sql` y `cambios_produccion_postgres.sql`.
Todos son re-ejecutables. Ejecutar completos como script (DBeaver: Alt+X).

## Qué se convirtió en el código (Java)

Las consultas de los servicios nuevos pasaron de T-SQL a PostgreSQL: `TOP n` → `LIMIT`, `WITH (NOLOCK)` fuera, `UPDLOCK` → `FOR UPDATE`,
`OUTPUT inserted.X` → `RETURNING`, `MERGE` → `INSERT … ON CONFLICT`, `FOR JSON PATH` → `json_agg`, `CONVERT(varchar, f, 120)` →
`to_char`, `OBJECT_ID` → `to_regclass`, `COL_LENGTH` → `general.col_length`, `sys.columns` → `information_schema`,
`SET IDENTITY_INSERT` fuera, `DB_NAME()` → `current_schema()`, bit `= 1/0` → `= true/false`, `WmsWdGeneral.dbo.` → `general.`.
Lo hizo la herramienta `db/herramientas/tsql_a_postgres_java.py` (parte mecánica) y revisión a mano del resto.

Cambios de comportamiento a tener presentes:
* **Ingreso con smartcard automática**: antes era un lote T-SQL de una sola ida; ahora son 3 consultas en la misma conexión
  (verificar tarjeta, `pa_insertentry`, crear la smartcard). Si falla la smartcard, el equipo queda ingresado (igual que antes).
* **Empaque con código SAP del pallet**: `executeInsertPacking` es ahora un método `default` del repositorio (llama a la función y,
  si corresponde, actualiza `ingreso.codigosapid`) en una transacción.
* **Despachado de accesorios**: la consulta de las salidas ya no bloquea filas (`FOR UPDATE` no admite `GROUP BY`); el resto de la
  operación sigue en una transacción.
* **Variables de etiqueta con "Consulta"**: es SQL escrito por el administrador y se ejecuta en solo lectura. Debe estar en
  **SQL de PostgreSQL**; `@serial`, `@mac` y `@codigoSap` se reemplazan por parámetros. Las consultas ya guardadas en T-SQL hay que reescribirlas.
* **Base Almacén**: vive en `general.basealmacen` (antes `WmsWdGeneral.dbo.BaseAlmacen`).

## No se portó (era herramienta de despliegue de SQL Server)

Estos scripts recorrían muchas bases copiando SPs o datos y no tienen sentido en PostgreSQL (una sola base, un esquema por cliente):
`2026-10-25_instalar_por_base.sql` (su parte útil está en `04`), `2026-10-29_sps_faltantes.sql`, `2026-10-29_novedad_accesorios.sql`
(columnas y maestro en `04`), `2026-10-30_maestros_por_base.sql`, `2026-10-29_legacy_salvador_sin_bloqueo_pqr.sql` (una sola base) y
`scripts_a_ejecutar_V2_6_2.sql` (su contenido ya está en `autogestion_postgres.sql`, `cambios_produccion_postgres.sql`, `04` y `05`).
Para otros clientes: generar sus funciones con `db/herramientas/generar_funciones_cliente.py`.
Los datos específicos de un cliente (reglas de campos calculados de Global Liberty, Tigo, Ventas Panamá; limpieza de MAC de Claro Ecuador;
smartcard de DirecTV Ecuador) están en `sql/2026-10-30_maestros_por_base.sql` y se cargan por la pantalla de cada cliente.

## Pendiente

* Definición en SQL Server de `pa_SearchQualityEntryExtras`, `pa_SearchAuditoriaInSituEntry(@Extras)`, `pa_UpdateQualityExtrasBySerial` y
  `pa_UpdateEstadoCalidadBySerial` (el backend ya las llama; hasta que existan fallan solo esas pantallas de Calidad).
* La tabla `depreciados` de `cambios_produccion_postgres.sql` es una suposición (solo se sabe que tiene `SerialNumber`).
* Probar con datos: boletas, Despachado (equipos y accesorios), notificaciones, cierre de prealerta, reemplazo de serial.
