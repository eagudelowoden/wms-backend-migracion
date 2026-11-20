# Revisión del esquema Legacy (SQL Server) contra PostgreSQL

Origen: `nuevo4.txt` (DataGrip: 97 tablas + 27 triggers) y `procedures.sql` (DBeaver: 102 tablas; **no trae procedimientos**, a pesar del nombre).
Cliente de referencia: `WmsWdLegacyCostaRica` = esquema `wmswdlegacycostarica` (cliente 85).

## Qué hacer, en orden

| # | Archivo | Dónde | Para qué |
|---|---|---|---|
| 1 | `01_comparar_tablas_y_columnas.sql` | **Postgres** | Lista tablas y columnas que **faltan** o tienen **tipo distinto** respecto a SQL Server (1168 columnas de 97 tablas). Pásame el resultado |
| 2 | `02_triggers_verificar.sql` | **Postgres** | Para cada uno de los 29 triggers: ¿existe la función?, ¿está asociado a la tabla?, ¿deshabilitado? |
| 3 | `03_triggers_faltantes.sql` | **Postgres** | Crea 4 funciones de trigger que no existían y **solo asocia los triggers que falten** (no toca los que ya estén). Correrlo después del 2 |
| 4 | `00_exportar_procedimientos_sqlserver.sql` | **SQL Server** | Para sacar los procedimientos y ver cuáles cambiaron recientemente. Pásame el resultado |

`esperado/` tiene el DDL de PostgreSQL convertido desde SQL Server (tablas, índices y FKs) para consultar o aplicar lo que falte;
`01_comparar...` se generó de ahí (`db/herramientas/generar_comparador.py`).

## Lo que ya se vio en los archivos

* **`depreciados`**: el DDL real tiene 9 columnas (`id bigint`, `serialnumber varchar(60)`, `acquisitiondate`, `lifeyearmonth`, `fechafindepreciacion`, `cost`, `nbvatperiodend`, `description2`, `fechacarga`). Ya está corregido en los scripts de `db/EJECUTAR_EN_LA_DB`.
* **`calidad`** tiene muchas columnas más que las que se habían creado (Wifi, Check*, `estadoproceso`, `personal`, `dni`, `voltaje`, `observaciones`…). El script 1 dirá cuáles faltan en Postgres.
* **`despacho`** trae `modeloid`, `areaorigen`, `areadestino`, `clienteorigenid`, `truckrollid`, `clienteparametroid`, `truckrolllocalid`, `serial4`.
* **Triggers**: 27 en SQL Server. En Postgres había funciones para la mayoría; no existían estos cuatro y se convirtieron en `03_triggers_faltantes.sql`:
  * `TR_Ingreso_PreventInconsistent` (bloquea el cambio de estado 66→38 si el serial sigue empacado) y
  * `TR_Ingreso_ValidarSerialVsMac` (el serial no puede existir como MAC de otra fila y viceversa),
  * `trg_Clasificacion_Reparacion` (al pasar de estado 112 a 14 crea un Diagnóstico «Envío a reparación»),
  * `trg_DeleteNoExistePrealerta` (borra el ingreso con novedad `NO EXISTE PREALERTA`).
  El primero necesita que `pa_UpdatePackingAllEntry` marque la sesión (lo hacía con `SESSION_CONTEXT`); `03` ya trae esa función actualizada con `set_config('wms.sourcesp', …)`.
* **Procedimientos**: el SQL Server ya tiene cambios de comportamiento que no están en Postgres (por ejemplo `pa_UpdatePackingAllEntry` ahora marca `SourceSP`). Para saber cuáles hace falta el archivo del paso 4.

## Cuidado
* Corre **02 antes que 03**. Si el 02 muestra «FALTA EL TRIGGER» en muchos, es que los triggers no estaban asociados: el 03 los creará todos y la base empezará a ejecutar sus efectos (movimientos, fechas de cierre, etc.). Es lo esperado, pero conviene probarlo.
* Los triggers de `tgr_fechacierre_fn` tienen el esquema `wmswdlegacycostarica` escrito dentro de la función (consulta `general.controlcierresctes`); para otro cliente hay que reemplazarlo.
