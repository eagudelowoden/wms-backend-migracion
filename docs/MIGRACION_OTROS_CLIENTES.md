# Guía para migrar otro cliente (esquema) a PostgreSQL

Documento de trabajo con lo aprendido migrando el cliente 85 (`wmswdlegacycostarica`) y la base `general`.
Sirve como lista de verificación para cada cliente que falte. **Siempre probar primero en una copia y con respaldo.**

> Reemplaza `wmswdlegacycostarica` por el esquema del cliente (el nombre de su base en minúsculas, ej. `wmswdclaro`).

---

## 1. Orden recomendado por cliente

| # | Paso | Dónde | Archivo / herramienta |
|---|---|---|---|
| 1 | Exportar las funciones del esquema del cliente (DDL) | DBeaver → Funciones → Generar SQL | — |
| 2 | Aplicar correcciones automáticas al archivo exportado | terminal | `db/herramientas/corregir_funciones.py` |
| 3 | Crear las funciones y procedimientos que **no** vienen en la exportación | Postgres | `db/funciones_faltantes_cliente.sql` + `db/fn_getdescripcionmovimiento.sql` |
| 4 | Quitar las llaves foráneas que estaban **deshabilitadas** en SQL Server | SQL Server → Postgres | `db/quitar_fks_deshabilitadas.sql` |
| 5 | Ejecutar el archivo corregido del paso 2 | Postgres | salida de la herramienta |
| 6 | Verificar tipos y funciones (consultas de la sección 8) | Postgres | — |
| 7 | Probar las pantallas principales | aplicación | sección 9 |

Archivos del repositorio relacionados:

| Archivo | Para qué sirve |
|---|---|
| `db/herramientas/corregir_funciones.py` | Corrige automáticamente un archivo de funciones exportado (booleanos, mayúsculas, fechas, SQL dinámico, candado de empaque) |
| `db/funciones_faltantes_cliente.sql` | 21 funciones convertidas a mano (procedimientos que la exportación no trajo) |
| `db/fn_getdescripcionmovimiento.sql` | Función usada por el trigger de movimientos (`trg_updatemovement_fn`) |
| `db/quitar_fks_deshabilitadas.sql` | Quita las llaves que SQL Server tenía apagadas |
| `db/hotfix_*.sql` | Atajos con una sola función (ya incluidos en el archivo grande) |
| `db/FuncionesPostgress_parches.sql` | **Obsoleto**: ya está incluido en la corrección de `general` |

---

## 2. Booleanos: qué convertir y qué NO

### 2.1 Regla
SQL Server guardaba los flags como `bit` y el código los comparaba con `= 1` / `= 0`. En Postgres esas columnas son
`boolean`, y `boolean = integer` **no existe** (error `operator does not exist: boolean = integer`).
**No hace falta cambiar tipos de columna**: ya llegan como `boolean`. Lo que hay que corregir son las
**comparaciones** en funciones y consultas.

### 2.2 Columnas que ya son `boolean` (cliente 85 y `general`)
Esquema del cliente: `activo` (en `cajadespacho`, `cajaempaque`, `cajaingreso`, `ciclicopallet`, `ciudad`, `cliente`,
`codigosap`, `estado`, `etiqueta`, `forecast`, `hojavida`, `lote`, `maestro`, `manodeobra`, `pallet`, `palletlog`, `posicion`,
`prealerta`, `tipomaestro`, `transportadora`, `usuario`), `cruce` (`accesorio`, `despacho`, `ingreso`, `inventariofaltante`,
`logdespacho`), `sobrante` (`inventario`, `inventarioterminado`), `bajas` y `usuarioactivo` (`manodeobrausuario`),
`reimpresion` (`etiquetado`), y en `codigosap`: `asignacionacc`, `asignacionfalla`, `multimodelo`, `smartcard`, `validacion`.

Esquema `general`: `activo` (`cliente`, `clientevalidacion`, `clientevalidaciontipo`, `estado`, `usuario`, `sede`, `informe`,
`notificacion`, `seccion`, `subcliente`...) y en `cliente`: `adicionprealertaon`, `baseempaqueon`, `baseingresoon`,
`basenodisponibleon`, `componenteon`, `etiquetaunitariaon`, `kitingresoon`, `loteempaqueon`, `nivelclasificacionon`,
`odoopqrson`, `recogidaon`, `serialmastercalidadon`, `smartcardinfoon`, `tipoorigenusuarioon`.

### 2.3 Columnas que se quedan como número (NO convertir)

| Tabla | Columna | Tipo | Motivo |
|---|---|---|---|
| `general.cliente` | `calidadon`, `bloqueoreimpresionon` | `integer` | El código las usa como número |
| `codigosap` | `reingreso` | `integer` | Puede tener más de dos valores |
| `codigosap` | `recorte` | `smallint` | El código ya la convierte |
| `etiqueta` | `impresion` | `integer` | Número |
| `pallet` | `estadoinventario` | `integer` | Se compara con 1 y 0 en funciones: **dejar** `= 1` / `= 0` |
| `general.controlcierresctes` | `activo` | `integer` | No participa en flujos que fallen |

### 2.4 Funciones donde hay que cambiar `= 1` / `= 0` por `= true` / `= false`
(El cambio lo hace `corregir_funciones.py`; esta lista es para verificar.)

**Esquema del cliente** (cliente 85):

| Columna / variable | N.º funciones | Funciones |
|---|---|---|
| `activo` | 41 | `pa_boxentry`, `pa_getbatchlist`, `pa_getboxpallet`, `pa_getdescriptionmaster`, `pa_getincomebyusersoudesnumber`, `pa_getlevelsclasification`, `pa_getlistdescriptionsapcode`, `pa_getlistdescriptionsapcodenoserial`, `pa_getlistdescriptionsapcodeserial`, `pa_getlistfailurescodigosapnoid`, `pa_getlistfailuresmaster`, `pa_getlistlabel`, `pa_getlistmaster`, `pa_getlistprealert`, `pa_getmodelmaster`, `pa_getoriginsmaster`, `pa_gettipologymaster`, `pa_getunreadablereceiptbypallet`, `pa_layout`, `pa_searchaccesorypallet`, `pa_searchallcomponents`, `pa_searchcomponents`, `pa_searchcyclicpallet`, `pa_searchdispatch`, `pa_searchentrypallet`, `pa_searchgeneralinventorypallet`, `pa_searchlabeled`, `pa_searchpackingdeliverypallet`, `pa_searchpalletboxdispatchpallet`, `pa_searchpalletboxpallet`, `pa_searchpalletboxpalletweb`, `pa_searchprocessboxpacking`, `pa_searchqualitydeliverypallet`, `pa_searchreceivepallet`, `pa_searchreceivepartspallet`, `pa_searchstoragegrouppallet`, `pa_searchstoragegrouppalletaccesory`, `pa_searchstoragegrouppalletaccesorycreated`, `pa_searchstoragepallet`, `pa_searchtransitpallet`, `pa_updateforecast` |
| `sobrante` | 5 | `pa_getcountdiff`, `pa_getcountdiffsap`, `pa_getcountsurplus`, `pa_getpercentdiff`, `pa_getpercentdiffsap` |
| `v_esmultimodelo` | 3 | `pa_enviarpallet`, `pa_searchseparatepalletaccesory`, `pa_sendentry` |
| `usuarioactivo` | 2 | `pa_getcountlabour`, `pa_searchlabourclient` |
| `bajas` | 2 | `pa_getcountlabour`, `pa_searchlabourclient` |
| `cruce` | 2 | `pa_getdifferenceprealert`, `pa_getfirstalertcount` |
| `asignacionacc` | 1 | `pa_searchsapcode` |
| `asignacionfalla` | 1 | `pa_searchsapcode` |

**Esquema `general`:**

| Columna / variable | N.º funciones | Funciones |
|---|---|---|
| `activo` | 11 | `informepqrsapexapi`, `informetruckrollventastotalapex`, `pa_getcountnotification`, `pa_getlistnotification`, `pa_getlistuser`, `pa_getmodulesbyusercustomer`, `pa_getvalidacionescliente`, `pa_getvalidaciontiposdisponibles`, `pa_searchactiveuser`, `rep_informeingresoswmswodenaccesorioswip`, `sp_reconstruirclienteproyecto` |
| `reimpresion` | 1 | `rep_informeclasificacioncosmeticawmswoden` |

### 2.5 Cómo detectar los mismos casos en otro cliente
En **SQL Server** (base del cliente): columnas `bit`, que son las candidatas a `boolean` en Postgres:

```sql
SELECT t.name AS tabla, c.name AS columna
FROM sys.columns c
JOIN sys.tables t ON t.object_id = c.object_id
JOIN sys.types ty ON ty.user_type_id = c.user_type_id
WHERE ty.name = 'bit'
ORDER BY t.name, c.name;
```

En **Postgres**: columnas `boolean` del esquema, y qué funciones las comparan con 0/1 (se genera solo):

```sql
-- columnas boolean
SELECT table_name, column_name FROM information_schema.columns
WHERE table_schema = 'wmswdlegacycostarica' AND data_type = 'boolean' ORDER BY 1, 2;

-- funciones que comparan una columna boolean con 0/1
WITH b AS (SELECT DISTINCT column_name FROM information_schema.columns
           WHERE table_schema = 'wmswdlegacycostarica' AND data_type = 'boolean')
SELECT p.proname AS funcion, b.column_name AS columna
FROM pg_proc p
JOIN pg_namespace n ON n.oid = p.pronamespace
JOIN b ON p.prosrc ~* ('\m' || b.column_name || '\s*(=|<>|!=)\s*[01]\M')
WHERE n.nspname = 'wmswdlegacycostarica'
ORDER BY 2, 1;
```

Si las columnas boolean del otro cliente tienen nombres distintos, pásalas a la herramienta:
`python db/herramientas/corregir_funciones.py entrada.sql salida.sql --columnas-bool activo,cruce,otra_columna`

### 2.6 Parte del backend (ya hecha, aplica a todos los clientes)
- Entidades con flags: `ClienteModel` y `CodigoSapModel` usan `Boolean` en los campos que apuntan a columnas `boolean`, y se
  serializan como `1/0` hacia el frontend (`BooleanAsIntSerializer`), porque el frontend compara `=== 1`.
- `TolerantIntegerJdbcType` hace que cualquier campo `Integer` lea bien una columna `boolean`.
- En consultas Java: `CAST(col AS boolean)` / `CAST(col AS integer)`, `= true`, `SET activo = (CAST(:x AS integer) <> 0)`.
- Conexión: `stringtype=unspecified` (los `String` se convierten al tipo de la columna: fechas, booleanos).

---

## 3. Funciones y procedimientos que NO vienen en la exportación

La herramienta de migración no convirtió estos procedimientos. Hay que crearlos (ya están hechos para el cliente 85).

### 3.1 Convertidos en `db/funciones_faltantes_cliente.sql` (21)
`pa_countavailableaccesory`, `pa_getdiasultimoingreso`, `pa_getetiquetadouser`, `pa_getsmartcardasinguserweb`,
`pa_insertsmartcardweb`, `pa_searchaccesorynovedadpallet`, `pa_searchboxentryquality`, `pa_searchinventory`,
`pa_searchnoveltyaccesory`, `pa_searchnoveltyaccesorypallet`, `pa_searchserialesbycajamaster`, `pa_searchsettings`,
`pa_sendnoveltyentry`, `pa_updateaccesorynovedad`, `pa_updateaccesorynovedadcantidad`, `pa_updateentrydispatchweb`,
`pa_updateestadosmartcardasign`, `pa_updatenoveltyallaccesory`, `pa_updatesapcodepallet`, `pa_validarreingresoscrap`,
`pa_validatequalitybypallet`.
(`pa_searchsapcodefailurecomponent` ya existe como conversión automática: no se incluye.)

### 3.2 Función escalar de SQL Server
`fn_getdescripcionmovimiento`, en `db/fn_getdescripcionmovimiento.sql`. La usa el trigger de movimientos; sin ella falla `enviarPallet`.

### 3.3 Reemplazados por SQL directo en el backend (no necesitan función)
`pa_GetListCodigoSapPermitido`, `pa_InsertCodigoSapPermitido`, `pa_DeleteCodigoSapPermitido`
(multimodelo restringido; la tabla `CodigoSapPermitido` solo existe en algunos clientes).

### 3.4 Cómo encontrar procedimientos que falten en otro cliente
Lista en **SQL Server**:

```sql
SELECT name FROM sys.procedures WHERE name LIKE 'pa[_]%' OR name LIKE 'sp[_]%' ORDER BY name;
SELECT name, type_desc FROM sys.objects WHERE type IN ('FN','IF','TF') ORDER BY name;   -- funciones escalares
```

Lista en **Postgres**:

```sql
SELECT p.proname FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace
WHERE n.nspname = 'wmswdlegacycostarica'
  AND (p.proname LIKE 'pa\_%' OR p.proname LIKE 'sp\_%' OR p.proname LIKE 'fn\_%')
ORDER BY 1;
```

Los que estén en SQL Server y no en Postgres (comparando en minúsculas) son los que hay que convertir. Definición de cada uno:

```sql
SELECT o.name, OBJECT_DEFINITION(o.object_id) AS definicion
FROM sys.objects o WHERE o.type = 'P' AND o.name IN ('nombre1', 'nombre2');
```

---

## 4. Llaves foráneas deshabilitadas en SQL Server

En SQL Server había llaves **deshabilitadas** (no validaban nada); el sistema guarda `0` como "sin valor". En Postgres
la herramienta las creó activas y rechazan esos `0` (errores como `fk_pallet_posicion`, `fk_ingreso_cajadespacho`).

En **SQL Server** (base del cliente), obtén las llaves a quitar:

```sql
SELECT 'ARRAY[' + STRING_AGG('''' + LOWER(name) + '''', ', ') + '];' AS llaves
FROM sys.foreign_keys WHERE is_disabled = 1;
```

Pega el resultado en la variable `llaves` de `db/quitar_fks_deshabilitadas.sql`, cambia el esquema en `esquemas` y ejecútalo
en **Postgres**. Las llaves que estaban activas (aunque "sin confianza") se dejan.

---

## 5. Mayúsculas y minúsculas

SQL Server no distingue mayúsculas al comparar texto; Postgres sí. Ejemplos reales: `'Empaque'` frente a `'EMPAQUE'`
(caja sin seriales) y `'tipologias'` frente a `Tipologias` (`tipologiaId = 0`).

La herramienta aplica `LOWER()` en:
- comparaciones `columna = p_parametro` de catálogos (`nombre`, `codigo`, `descripcion`, `tipo`, `tipoaccesorio`, `detalle`, `name`);
- comparaciones `p_parametro = 'texto'` y `p_parametro IN ('a','b')`.

Funciones afectadas: **109** (cliente 85) y **119** (`general`).
En el backend y el frontend, los nombres de tipo de maestro deben escribirse como en la tabla
(`Tipologias`, `Origenes`, `Tipo Origenes`, `Niveles`, `Modelos`...).

Si aparece lentitud en una búsqueda por código o nombre en una tabla grande, crear un índice funcional:
`CREATE INDEX ON codigosap (LOWER(codigo));`

---

## 6. Otras correcciones que hace la herramienta

| Corrección | Dónde se vio | Funciones del cliente 85 | Funciones de `general` |
|---|---|---|---|
| `(0)::timestamp` en `dateadd/datediff` → `date_trunc` (error `cannot cast type integer to timestamp`) | `pa_incrementarconsecutivopallet`, `sp_logisticaregional`, `sp_produccionregional` | 0 | `pa_incrementarconsecutivopallet`, `sp_logisticaregional`, `sp_produccionregional` |
| `'FROM' \|\| v_db` sin espacio en SQL dinámico | menús por perfil, tipo de origen, `spinformelabreverse` | 0 | `pa_getassignedgruposforprofile`, `pa_getassigneditemsforprofile`, `pa_getavailablegruposforprofile`, `pa_getavailableitemsforprofile`, `pa_getlistusuariotipoorigen`, `pa_searchaggregatesusuariotipoorigen`, `pa_searchavailableusuariotipoorigen`, `spinformelabreverse` |
| Candado por serial (evita `duplicate key` en `empaque_unique`) | empaque rápido | `pa_insertpackingwebd`, `pa_insertpackingwebd2` | — |

**No automáticas** (revisar a mano en cada cliente): placeholders de SQL Server (`@m`, `@p`) dentro de SQL dinámico
(`pa_insertmenuperfil` y `pa_syncmenuperfil` de `general`, ya reescritas en `General_corregido.sql`), y comparaciones tipo
`smartcard = 0` sobre columnas `varchar`.

---

## 7. Pendientes conocidos: funciones cuyo resultado no coincide con lo que lee el backend

No se tocaron para no adivinar cuál lado tiene la razón. Decidir por función si se corrige la función o el servicio.

| Función | Problema |
|---|---|
| `pa_getmodelbase` (despacho, separación) | Devuelve 5 columnas sin `id`; el servicio lee desde el índice 1 |
| `pa_getscrapuser` | Falta la columna `usuarioAsignado` (índice 8) |
| `pa_searchreceivepartspallet` | Devuelve 5 columnas; el servicio lee 7 |
| `pa_searchgeneralinventorypallet`, `pa_searchgeneralsettingspallet` | Devuelven 4 columnas; el servicio lee 7 |
| `pa_searchallpackedaccesory` | Trae `familia` en la posición 2; el servicio espera `Nuevos` |
| `pa_updateuser` (`general`) | `p_identificacion` es `integer` y la columna es `bigint` (cédulas grandes) |
| `pa_getlistprofiletype` (`general`) | Devuelve solo `nombre`; el servicio necesita `id` (hoy consulta la tabla directo) |
| `pa_validatequalitybypallet` | Usa `calidad.estadoproceso`: confirmar que la columna existe |

---

## 8. Consultas de verificación (Postgres)

```sql
-- 8.1 Las funciones clave quedaron actualizadas (deben dar true)
SELECT
  (SELECT prosrc LIKE '%pg_advisory_xact_lock%' FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace
     WHERE n.nspname='wmswdlegacycostarica' AND p.proname='pa_insertpackingwebd2')   AS candado_empaque,
  (SELECT prosrc ILIKE '%lower(p_estado)%' FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace
     WHERE n.nspname='wmswdlegacycostarica' AND p.proname='pa_searchpalletboxentry') AS mayusculas_ok,
  EXISTS (SELECT 1 FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace
     WHERE n.nspname='wmswdlegacycostarica' AND p.proname='fn_getdescripcionmovimiento') AS funcion_movimientos;

-- 8.2 Llaves foráneas que quedan (deben ser solo las que estaban activas en SQL Server)
SELECT conname, conrelid::regclass AS tabla, convalidated
FROM pg_constraint
WHERE contype = 'f' AND connamespace = 'wmswdlegacycostarica'::regnamespace
ORDER BY conrelid::regclass::text, conname;

-- 8.3 Columnas citext (en este cliente solo login: usuario.nombreusuario / correo)
SELECT table_name, column_name FROM information_schema.columns
WHERE table_schema = 'wmswdlegacycostarica' AND udt_name = 'citext';
```

---

## 9. Pantallas para probar después de migrar un cliente

- **Ingreso:** crear pallet, ingresar seriales, enviar pallet (usa el trigger de movimientos).
- **Diagnóstico:** procesar un serial; ver la descripción del movimiento.
- **Empaque:** serializar rápido, ver seriales de una caja, caja master, cerrar pallet.
- **Calidad y despacho:** pantalla de proceso y despacho (columnas `Cantidad Equip.`, `Cantidad Acc.`, `Cantidad SmartC.`).
- **Accesorios y novedades**, **inventario**, **smartcard**.
- **Administración:** usuarios, perfiles, menús por perfil, clientes (flags `*ON`).

## 10. Advertencias

- La conexión de prueba apunta a un servidor de producción: guardar un respaldo del DDL de las funciones antes de ejecutar
  archivos completos (clic derecho en el esquema → Generar SQL → DDL).
- Cada cliente nuevo necesita su propio esquema con sus funciones, sus llaves y su copia de las funciones faltantes.
- Si el otro repositorio cambia un procedimiento ya convertido, hay que volver a exportar y convertir esa función.
