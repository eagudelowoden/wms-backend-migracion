/* =====================================================================================
   INSTALAR EN TODAS LAS BASES CLIENTE  (versión "recorre todas" de 2026-10-25_instalar_por_base.sql)

   Recorre WmsWdGeneral.dbo.Cliente (dbase LIKE 'WmsWd%') y deja cada base con lo mismo que el script de una sola base,
   SOLO LO QUE FALTA (idempotente):
     1. Catálogo Permiso de Despachado (una vez, WmsWdGeneral) y permisos para los perfiles ADMINISTRADOR de cada base.
     2. Work flow de Despachado (equipos -> NOVEDAD, accesorios -> NOVEDAD-ACCESORIOS).
     3. Menús de Administración ADNOT, ADDOC, ADVAR y los módulos 44, 47 y 48 en los perfiles ADMINISTRADOR.
        3b. Maestro NO CONFORMIDAD en "Tipo Novedad Accesorios" (si el tipo existe).
        3c. Menú PAUT (Autogestión): solo con @CrearMenuAutogestion = 1 (ver OJO).
     4. Columna CAJAS de pa_SearchPalletBoxDispatchPallet.
     5. Tabla AuditoriaNivelEmpaque (si hay trigger y falta la tabla).
     6. Tipos de maestro de parámetros por cliente, campos calculados (CodigoSapRegla) y limpieza de caracteres de Claro Ecuador.
     7. Interruptores opcionales por cliente y columnas de Forecast: SOLO con @SoloBase (son por cliente, no se
        encienden en todas las bases a la vez).
   Además deja en la columna Avisos lo que este script NO crea y la base necesita:
     tipología NO CONFORMIDAD, novedad de accesorios (2026-10-29_novedad_accesorios.sql), Cosmética/Autogestión
     (SP) y la tabla CodigoSapPermitido (2026-10-29_sps_faltantes.sql).

   OJO PAUT: el menú Autogestión se muestra a TODOS los perfiles si el backend publicado no tiene el mapeo
   46 -> PAUT en MenuModuloMapping. Déjalo en 0 hasta desplegar el backend nuevo.

   @Modo      LISTAR (no cambia nada, dice qué falta) | PROBAR (lo hace todo y lo deshace) | APLICAR
   @SoloBase  una sola base (NULL = todas)
   @Excluir   bases separadas por coma que se saltan
   Cada base va en su propia transacción en APLICAR: si algo falla, esa base queda como estaba.
   En PROBAR todo corre dentro de una sola transacción y se revierte al final.
   Se puede ejecutar desde cualquier base: usa nombres de tres partes. Guardar y abrir como UTF-8 (Año, tildes).
   ===================================================================================== */
SET NOCOUNT ON;
SET LOCK_TIMEOUT 5000;

DECLARE @Modo                      VARCHAR(10)   = 'LISTAR';
DECLARE @SoloBase                  SYSNAME       = NULL;
DECLARE @Excluir                   NVARCHAR(MAX) = '';
DECLARE @CrearMenuAutogestion      BIT           = 0;

/* Opcionales por cliente: solo se pueden encender con @SoloBase */
DECLARE @ActivarConsecutivo        BIT = 0;
DECLARE @ActivarSonido             BIT = 0;
DECLARE @ActivarCierreManual       BIT = 0;
DECLARE @ActivarSobrantes          BIT = 0;
DECLARE @ActivarSmartcardAutomatica BIT = 0;
DECLARE @SmartcardCodigoSap        VARCHAR(50) = 'CARD0001';
DECLARE @SmartcardDestino          VARCHAR(50) = 'SMARTCARD';
DECLARE @ActivarEmpaqueCodigoPallet BIT = 0;
DECLARE @AgregarColumnasForecast   BIT = 0;

IF @Modo NOT IN ('LISTAR', 'PROBAR', 'APLICAR')
BEGIN
  RAISERROR('@Modo debe ser LISTAR, PROBAR o APLICAR.', 16, 1);
  RETURN;
END

IF @SoloBase IS NULL AND (@ActivarConsecutivo = 1 OR @ActivarSonido = 1 OR @ActivarCierreManual = 1 OR @ActivarSobrantes = 1
                          OR @ActivarSmartcardAutomatica = 1 OR @ActivarEmpaqueCodigoPallet = 1 OR @AgregarColumnasForecast = 1)
BEGIN
  RAISERROR('Los interruptores opcionales son por cliente: indica @SoloBase para encenderlos.', 16, 1);
  RETURN;
END

DECLARE @moduloDespacho INT = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.Modulo WHERE Nombre = 'Despacho' ORDER BY Id);
IF @moduloDespacho IS NULL
BEGIN
  RAISERROR('No existe el módulo Despacho en WmsWdGeneral.dbo.Modulo.', 16, 1);
  RETURN;
END

/* Resultados en variable de tabla (no se pierde con el ROLLBACK de PROBAR) */
DECLARE @res TABLE (Base SYSNAME, Resultado NVARCHAR(MAX), Avisos NVARCHAR(1000) NULL);

IF OBJECT_ID('tempdb..#pendPerm') IS NOT NULL DROP TABLE #pendPerm;
IF OBJECT_ID('tempdb..#pendMod') IS NOT NULL DROP TABLE #pendMod;
CREATE TABLE #pendPerm (PerfilId INT, PermisoId INT);
CREATE TABLE #pendMod (PerfilId INT, ModuloId INT);

DECLARE @sql NVARCHAR(MAX), @n INT, @q NVARCHAR(300), @exec NVARCHAR(400), @db SYSNAME, @clienteId INT, @tipoVal INT;
DECLARE @falta NVARCHAR(MAX), @avisos NVARCHAR(1000), @sinTablas NVARCHAR(500);
DECLARE @a1 INT, @a2 INT, @a3 INT, @a4 INT;
DECLARE @equiposOk BIT, @accesoriosOk BIT, @hayMenu BIT;
DECLARE @def NVARCHAR(MAX), @pos INT, @csId INT, @estId INT;

DECLARE @Viejo NVARCHAR(400) = N'COALESCE(COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END), 0) AS cajas';
DECLARE @Nuevo NVARCHAR(800) = N'CASE WHEN COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END) > 0 THEN COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END) ELSE (SELECT COUNT(DISTINCT ie.CajaEmpaqueId) FROM Ingreso ie WHERE ie.PalletId = p.id AND ie.CajaEmpaqueId > 0) END AS cajas';

/* Interruptores opcionales pedidos (solo con @SoloBase) */
DECLARE @sw TABLE (Codigo VARCHAR(60));
INSERT INTO @sw (Codigo)
SELECT c FROM (VALUES ('BOLETA_CONSECUTIVO', @ActivarConsecutivo), ('SONIDO_ALERTAS_INGRESO', @ActivarSonido),
                      ('PREALERTA_CIERRE_MANUAL', @ActivarCierreManual), ('PREALERTA_SOBRANTES', @ActivarSobrantes),
                      ('EMPAQUE_CODIGOSAP_PALLET', @ActivarEmpaqueCodigoPallet)) v(c, a)
 WHERE a = 1;

/* Campos calculados que hoy hace el SP de cada base (se pasan a Códigos SAP > Campos calculados) */
DECLARE @reglas TABLE (Base SYSNAME, CodigoSapId INT, Campo VARCHAR(20), Origen VARCHAR(20), Operacion VARCHAR(20),
                       Parametro1 VARCHAR(100), Parametro2 VARCHAR(100), Momento VARCHAR(15));
INSERT INTO @reglas VALUES
  ('WmsWdGlobalLiberty', 153, 'SERIAL3', 'MAC', 'HEX_SUMA', '1', '12', 'INGRESO'),
  ('WmsWdGlobalLiberty', 153, 'SERIAL4', 'MAC', 'HEX_SUMA', '2', '12', 'INGRESO'),
  ('WmsWdTigoNicaragua', 44, 'SERIAL3', 'MAC', 'HEX_SUMA', '1', '12', 'INGRESO'),
  ('WmsWdTigoNicaragua', 44, 'SERIAL4', 'MAC', 'HEX_SUMA', '2', '12', 'INGRESO'),
  ('WmsWdTigoNicaragua', 107, 'SERIAL3', 'MAC', 'HEX_SUMA', '1', '12', 'INGRESO'),
  ('WmsWdTigoNicaragua', 107, 'SERIAL4', 'MAC', 'HEX_SUMA', '2', '12', 'INGRESO'),
  ('WmsWdTigoNicaragua', 108, 'SERIAL3', 'MAC', 'HEX_SUMA', '1', '12', 'INGRESO'),
  ('WmsWdTigoNicaragua', 108, 'SERIAL4', 'MAC', 'HEX_SUMA', '2', '12', 'INGRESO'),
  ('WmsWdTigoCostaRica', 58, 'MAC', 'SERIAL', 'HEX_SUMA', '1', '12', 'INGRESO'),
  ('WmsWdTigoCostaRica', 58, 'SERIAL3', 'SERIAL', 'HEX_SUMA', '2', '12', 'DIAGNOSTICO'),
  ('WmsWdTigoCostaRica', 58, 'SERIAL4', 'SERIAL3', 'ULTIMOS', '6', '', 'DIAGNOSTICO'),
  ('WmsWdTigoCostaRica', 115, 'SERIAL3', 'SERIAL', 'ULTIMOS', '4', '', 'DIAGNOSTICO'),
  ('WmsWdTigoCostaRica', 87, 'SERIAL3', 'SERIAL', 'ULTIMOS', '4', '', 'DIAGNOSTICO'),
  ('WmsWdVentasPanama', 54, 'SERIAL3', 'MAC', 'HEX_SUMA', '1', '12', 'INGRESO'),
  ('WmsWdVentasPanama', 54, 'SERIAL4', 'MAC', 'HEX_SUMA', '2', '12', 'INGRESO');
DECLARE @codigosBase TABLE (Id INT PRIMARY KEY);

/* En PROBAR todo va dentro de una transacción que se revierte al final */
IF @Modo = 'PROBAR'
BEGIN
  BEGIN TRANSACTION;
END

/* ── GENERAL (una vez): catálogo Permiso de Despachado y tipo de validación de sonido ──────── */
DECLARE @permisosFaltan INT = (SELECT COUNT(*) FROM (VALUES ('Opc_Despachado', 'Despacho'), ('Opc_Equipos', 'Despachado'), ('Opc_Accesorios', 'Despachado')) v(n, d)
                                WHERE NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Permiso p WHERE p.ModuloId = @moduloDespacho AND p.Nombre = v.n AND p.Descripcion = v.d));
IF @permisosFaltan > 0
BEGIN
  PRINT CONCAT('GENERAL: faltan ', @permisosFaltan, ' permisos del catálogo de Despachado', IIF(@Modo = 'LISTAR', '', ' (se crean)'));
  IF @Modo <> 'LISTAR'
    INSERT INTO WmsWdGeneral.dbo.Permiso (ModuloId, Nombre, Descripcion)
    SELECT @moduloDespacho, v.n, v.d FROM (VALUES ('Opc_Despachado', 'Despacho'), ('Opc_Equipos', 'Despachado'), ('Opc_Accesorios', 'Despachado')) v(n, d)
     WHERE NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Permiso p WHERE p.ModuloId = @moduloDespacho AND p.Nombre = v.n AND p.Descripcion = v.d);
END

IF NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = 'SONIDO_ALERTAS_INGRESO')
BEGIN
  PRINT CONCAT('GENERAL: falta el tipo de validación SONIDO_ALERTAS_INGRESO', IIF(@Modo = 'LISTAR', '', ' (se crea)'));
  IF @Modo <> 'LISTAR'
    INSERT INTO WmsWdGeneral.dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
    VALUES ('SONIDO_ALERTAS_INGRESO', 'Sonido en los avisos y errores del WMS en todos los módulos (se configura en Administración > Notificaciones)', 1, GETDATE());
END

DECLARE @despachado INT = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.Estado WHERE Nombre = 'DESPACHADO' ORDER BY Id);
DECLARE @novedad INT = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.Estado WHERE Nombre = 'NOVEDAD' ORDER BY Id);
DECLARE @novedadAcc INT = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.Estado WHERE Nombre = 'NOVEDAD-ACCESORIOS' ORDER BY Id);
DECLARE @opcEquipos INT = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.Permiso WHERE ModuloId = @moduloDespacho AND Nombre = 'Opc_Equipos' AND Descripcion = 'Despachado' ORDER BY Id);
DECLARE @opcAccesorios INT = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.Permiso WHERE ModuloId = @moduloDespacho AND Nombre = 'Opc_Accesorios' AND Descripcion = 'Despachado' ORDER BY Id);

DECLARE @mod44 BIT = IIF(EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Modulo WHERE Id = 44 AND Nombre = 'Notificaciones'), 1, 0);
DECLARE @mod47 BIT = IIF(EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Modulo WHERE Id = 47 AND Nombre = 'Documentos'), 1, 0);
DECLARE @mod48 BIT = IIF(EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Modulo WHERE Id = 48 AND Nombre = 'Variables de etiquetas'), 1, 0);
IF @mod44 = 0 PRINT 'AVISO GENERAL: falta el módulo 44 Notificaciones (correr 2026-10-25_notificaciones_correo.sql)';
IF @mod47 = 0 PRINT 'AVISO GENERAL: falta el módulo 47 Documentos (correr 2026-10-26_documentos_prealertas.sql)';
IF @mod48 = 0 PRINT 'AVISO GENERAL: falta el módulo 48 Variables de etiquetas (correr 2026-10-29_parametros_wms.sql)';
IF OBJECT_ID('WmsWdGeneral.dbo.CodigoSapRegla', 'U') IS NULL
  PRINT 'AVISO GENERAL: falta WmsWdGeneral.dbo.CodigoSapRegla (correr 2026-10-29_parametros_wms.sql): se omiten los campos calculados';

/* ── Recorrido por base ───────────────────────────────────────────────────────────────────── */
DECLARE bases CURSOR LOCAL FAST_FORWARD FOR
  SELECT DISTINCT dbase FROM WmsWdGeneral.dbo.Cliente
   WHERE ISNULL(dbase, '') LIKE 'WmsWd%' AND DB_ID(dbase) IS NOT NULL
     AND (@SoloBase IS NULL OR dbase = @SoloBase)
   ORDER BY dbase;
OPEN bases;
FETCH NEXT FROM bases INTO @db;
WHILE @@FETCH_STATUS = 0
BEGIN
  IF CHARINDEX(',' + @db + ',', ',' + REPLACE(ISNULL(@Excluir, ''), ' ', '') + ',') > 0
  BEGIN
    INSERT INTO @res VALUES (@db, 'EXCLUIDA (@Excluir)', NULL);
  END
  ELSE
  BEGIN
    SET @q = QUOTENAME(@db);
    SET @sinTablas = STUFF((SELECT ', ' + t.Nombre
                              FROM (VALUES ('Ingreso'), ('Maestro'), ('TipoMaestro'), ('WorkFlow'), ('WorkFlowDestino')) t(Nombre)
                             WHERE OBJECT_ID(@q + N'.dbo.' + t.Nombre, 'U') IS NULL
                               FOR XML PATH('')), 1, 2, '');
    IF @sinTablas IS NOT NULL
    BEGIN
      INSERT INTO @res VALUES (@db, 'sin tablas base (' + @sinTablas + '): no aplica', NULL);
    END
    ELSE
    BEGIN
      SET @falta = NULL;
      SET @avisos = NULL;
      SET @exec = @q + N'.sys.sp_executesql';
      SET @clienteId = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.Cliente WHERE dbase = @db ORDER BY Id);

      BEGIN TRY
        IF @Modo = 'PROBAR'
        BEGIN
          SAVE TRANSACTION sp_base;
        END
        ELSE IF @Modo = 'APLICAR'
        BEGIN
          BEGIN TRANSACTION;
        END

        /* ── Avisos y qué puede hacer la base (no cambia nada) ── */
        SET @sql = N'SELECT @a1 = CASE WHEN EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Maestro m JOIN ' + @q
                 + N'.dbo.TipoMaestro t ON t.Id = m.TipoMaestroId WHERE t.Nombre = ''Tipologias'' AND m.Codigo = ''NO CONFORMIDAD'' AND m.Activo = 1) THEN 0 ELSE 1 END,
                   @a2 = CASE WHEN COL_LENGTH(''' + @q + N'.dbo.Accesorio'', ''Novedad'') IS NOT NULL AND EXISTS (SELECT 1 FROM ' + @q
                 + N'.sys.objects WHERE name = ''pa_SearchNoveltyAccesoryPallet'') THEN 0 ELSE 1 END,
                   @a3 = CASE WHEN (SELECT COUNT(*) FROM ' + @q + N'.sys.parameters p JOIN ' + @q
                 + N'.sys.objects o ON o.object_id = p.object_id WHERE o.name = ''pa_InsertCosmetica'' AND p.name = ''@UsuarioAsignadoId'')
                              + (SELECT COUNT(*) FROM ' + @q + N'.sys.objects WHERE name = ''pa_AutogestionSerial'') < 2 THEN 1 ELSE 0 END,
                   @a4 = CASE WHEN OBJECT_ID(''' + @q + N'.dbo.CodigoSapPermitido'', ''U'') IS NULL THEN 1 ELSE 0 END';
        EXEC sys.sp_executesql @sql, N'@a1 INT OUTPUT, @a2 INT OUTPUT, @a3 INT OUTPUT, @a4 INT OUTPUT',
             @a1 OUTPUT, @a2 OUTPUT, @a3 OUTPUT, @a4 OUTPUT;
        SET @equiposOk = IIF(@a1 = 0, 1, 0);
        SET @accesoriosOk = IIF(@a2 = 0, 1, 0);
        SET @avisos = CONCAT_WS('; ',
          IIF(@a1 = 1, 'falta tipología NO CONFORMIDAD (Despachado no regresa equipos)', NULL),
          IIF(@a2 = 1, 'falta novedad de accesorios (2026-10-29_novedad_accesorios.sql)', NULL),
          IIF(@a3 = 1, 'falta Cosmética/Autogestión (pa_InsertCosmetica o pa_Autogestion*)', NULL),
          IIF(@a4 = 1, 'falta CodigoSapPermitido (2026-10-29_sps_faltantes.sql)', NULL));

        /* ── 1. Permisos de Despachado para los perfiles ADMINISTRADOR de esta base ── */
        DELETE FROM #pendPerm;
        INSERT INTO #pendPerm (PerfilId, PermisoId)
        SELECT DISTINCT pf.Id, pe.Id
          FROM WmsWdGeneral.dbo.Perfil pf
          JOIN WmsWdGeneral.dbo.Cliente cl ON cl.Id = pf.ClienteId AND cl.dbase = @db
         CROSS JOIN WmsWdGeneral.dbo.Permiso pe
         WHERE pf.Nombre LIKE '%ADMINISTRADOR%'
           AND pe.ModuloId = @moduloDespacho
           AND ((pe.Nombre = 'Opc_Despachado' AND pe.Descripcion = 'Despacho' AND (@equiposOk = 1 OR @accesoriosOk = 1))
             OR (pe.Nombre = 'Opc_Equipos' AND pe.Descripcion = 'Despachado' AND @equiposOk = 1)
             OR (pe.Nombre = 'Opc_Accesorios' AND pe.Descripcion = 'Despachado' AND @accesoriosOk = 1))
           AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.PerfilPermiso x WHERE x.PerfilId = pf.Id AND x.PermisoId = pe.Id);
        SET @n = (SELECT COUNT(*) FROM #pendPerm);
        IF @n > 0
        BEGIN
          SET @falta = CONCAT_WS(', ', @falta, CONCAT(@n, ' permisos de Despachado en perfiles ADMINISTRADOR'));
          IF @Modo <> 'LISTAR'
            INSERT INTO WmsWdGeneral.dbo.PerfilPermiso (PerfilId, PermisoId) SELECT PerfilId, PermisoId FROM #pendPerm;
        END

        /* ── 2. Work flow de Despachado ── */
        IF @despachado IS NULL OR @novedad IS NULL OR @novedadAcc IS NULL
        BEGIN
          SET @avisos = CONCAT_WS('; ', @avisos, 'faltan los estados DESPACHADO/NOVEDAD/NOVEDAD-ACCESORIOS en WmsWdGeneral: work flow omitido');
        END
        ELSE
        BEGIN
          SET @sql = N'SELECT @n = (SELECT COUNT(*) FROM (VALUES (@op1), (@op2)) v(op) WHERE v.op IS NULL OR NOT EXISTS (SELECT 1 FROM '
                   + @q + N'.dbo.WorkFlow w JOIN ' + @q + N'.dbo.WorkFlowDestino d ON d.WorkFlowId = w.Id WHERE w.ModuloId = @m AND w.OrigenId = @o AND w.OpcionId = v.op AND w.TipologiaId = 0))';
          EXEC sys.sp_executesql @sql, N'@op1 INT, @op2 INT, @m INT, @o INT, @n INT OUTPUT',
               @opcEquipos, @opcAccesorios, @moduloDespacho, @despachado, @n OUTPUT;
          IF @n > 0
          BEGIN
            SET @falta = CONCAT_WS(', ', @falta, 'work flow de Despachado');
            IF @Modo <> 'LISTAR'
            BEGIN
              SET @sql = N'
DECLARE @wf INT;
SELECT @wf = Id FROM ' + @q + N'.dbo.WorkFlow WHERE ModuloId = @modulo AND OrigenId = @origen AND OpcionId = @opcion AND TipologiaId = 0;
IF @wf IS NULL
BEGIN
  INSERT INTO ' + @q + N'.dbo.WorkFlow (ModuloId, OrigenId, OpcionId, TipologiaId, nivelId) VALUES (@modulo, @origen, @opcion, 0, 0);
  SET @wf = SCOPE_IDENTITY();
END
IF NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.WorkFlowDestino WHERE WorkFlowId = @wf)
  INSERT INTO ' + @q + N'.dbo.WorkFlowDestino (WorkFlowId, DestinoId) VALUES (@wf, @destino);';
              EXEC sys.sp_executesql @sql, N'@modulo INT, @origen INT, @opcion INT, @destino INT',
                   @moduloDespacho, @despachado, @opcEquipos, @novedad;
              EXEC sys.sp_executesql @sql, N'@modulo INT, @origen INT, @opcion INT, @destino INT',
                   @moduloDespacho, @despachado, @opcAccesorios, @novedadAcc;
            END
          END
        END

        /* ── 3. Menús ADNOT, ADDOC, ADVAR (Administración) ── */
        SET @hayMenu = IIF(OBJECT_ID(@q + N'.dbo.Menu', 'U') IS NOT NULL, 1, 0);
        IF @hayMenu = 1
        BEGIN
          SET @sql = N'SELECT @n = (SELECT COUNT(*) FROM (VALUES (''ADNOT''), (''ADDOC''), (''ADVAR'')) v(Id)
                        WHERE EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Menu WHERE Id = ''ADM'')
                          AND NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Menu m WHERE m.Id = v.Id))';
          EXEC sys.sp_executesql @sql, N'@n INT OUTPUT', @n OUTPUT;
          IF @n > 0
          BEGIN
            SET @falta = CONCAT_WS(', ', @falta, CONCAT(@n, ' menús de Administración (Notificaciones/Documentos/Variables)'));
            IF @Modo <> 'LISTAR'
            BEGIN
              SET @sql = N'INSERT INTO ' + @q + N'.dbo.Menu (Id, Descripcion, Tipo, Orden, Accion, Estado, Icono, Id_padre, View_name, Controller_name)
SELECT v.Id, v.Descripcion, ''item'', v.Orden, v.Accion, ''activo'', '''', ''ADM'', '''', ''''
  FROM (VALUES (''ADNOT'', ''Notificaciones'', 20, ''ADMNOT''), (''ADDOC'', ''Documentos'', 21, ''ADMDOC''),
               (''ADVAR'', ''Variables de etiquetas'', 22, ''ADMVAR'')) v(Id, Descripcion, Orden, Accion)
 WHERE EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Menu WHERE Id = ''ADM'')
   AND NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Menu m WHERE m.Id = v.Id);';
              EXEC sys.sp_executesql @sql;
            END
          END
        END

        /* ── 3a. Módulos 44, 47 y 48 en los perfiles ADMINISTRADOR ── */
        DELETE FROM #pendMod;
        INSERT INTO #pendMod (PerfilId, ModuloId)
        SELECT DISTINCT pf.Id, v.ModuloId
          FROM WmsWdGeneral.dbo.Perfil pf
          JOIN WmsWdGeneral.dbo.Cliente cl ON cl.Id = pf.ClienteId AND cl.dbase = @db
         CROSS JOIN (VALUES (44, @mod44), (47, @mod47), (48, @mod48)) v(ModuloId, Ok)
         WHERE v.Ok = 1 AND pf.Nombre LIKE '%ADMINISTRADOR%'
           AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.PerfilModulo x WHERE x.PerfilId = pf.Id AND x.ModuloId = v.ModuloId);
        SET @n = (SELECT COUNT(*) FROM #pendMod);
        IF @n > 0
        BEGIN
          SET @falta = CONCAT_WS(', ', @falta, CONCAT(@n, ' módulos 44/47/48 en perfiles ADMINISTRADOR'));
          IF @Modo <> 'LISTAR'
            INSERT INTO WmsWdGeneral.dbo.PerfilModulo (PerfilId, ModuloId, Orden) SELECT PerfilId, ModuloId, 0 FROM #pendMod;
        END

        /* ── 3b. NO CONFORMIDAD en Tipo Novedad Accesorios (solo si el tipo existe) ── */
        SET @sql = N'SELECT @n = (SELECT COUNT(*) FROM (SELECT TOP 1 Id FROM ' + @q + N'.dbo.TipoMaestro WHERE Nombre = ''Tipo Novedad Accesorios'' ORDER BY Id) t
                      WHERE NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Maestro m WHERE m.TipoMaestroId = t.Id AND m.Codigo = ''NO CONFORMIDAD''))';
        EXEC sys.sp_executesql @sql, N'@n INT OUTPUT', @n OUTPUT;
        IF @n > 0
        BEGIN
          SET @falta = CONCAT_WS(', ', @falta, 'maestro NO CONFORMIDAD en Tipo Novedad Accesorios');
          IF @Modo <> 'LISTAR'
          BEGIN
            SET @sql = N'INSERT INTO ' + @q + N'.dbo.Maestro (Codigo, Descripcion, TipoMaestroId, Activo, Detalle, adicional)
SELECT ''NO CONFORMIDAD'', ''NO CONFORMIDAD'', t.Id, 1, '''', 0
  FROM (SELECT TOP 1 Id FROM ' + @q + N'.dbo.TipoMaestro WHERE Nombre = ''Tipo Novedad Accesorios'' ORDER BY Id) t
 WHERE NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Maestro m WHERE m.TipoMaestroId = t.Id AND m.Codigo = ''NO CONFORMIDAD'');';
            EXEC sys.sp_executesql @sql;
          END
        END

        /* ── 3c. Menú PAUT (Autogestión): solo con @CrearMenuAutogestion = 1 ── */
        IF @CrearMenuAutogestion = 1 AND @hayMenu = 1
        BEGIN
          SET @sql = N'SELECT @n = CASE WHEN EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Menu WHERE Id = ''PCOS'')
                                         AND NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Menu WHERE Id = ''PAUT'') THEN 1 ELSE 0 END';
          EXEC sys.sp_executesql @sql, N'@n INT OUTPUT', @n OUTPUT;
          IF @n = 1
          BEGIN
            SET @falta = CONCAT_WS(', ', @falta, 'menú Autogestión (PAUT)');
            IF @Modo <> 'LISTAR'
            BEGIN
              SET @sql = N'INSERT INTO ' + @q + N'.dbo.Menu (Id, Descripcion, Tipo, Orden, Accion, Estado, Icono, Id_padre, View_name, Controller_name)
SELECT ''PAUT'', N''Autogesti'' + NCHAR(243) + N''n'', Tipo,
       (SELECT MAX(Orden) + 1 FROM ' + @q + N'.dbo.Menu WHERE Id_padre = ''PROD''),
       ''PRODAUT'', Estado, Icono, Id_padre, View_name, Controller_name
  FROM ' + @q + N'.dbo.Menu WHERE Id = ''PCOS'';';
              EXEC sys.sp_executesql @sql;
            END
          END
        END

        /* ── 4. Columna CAJAS de Despacho > Proceso ── */
        SET @def = NULL;
        SET @sql = N'SELECT @def = m.definition FROM ' + @q + N'.sys.sql_modules m JOIN ' + @q
                 + N'.sys.objects o ON o.object_id = m.object_id WHERE o.name = ''pa_SearchPalletBoxDispatchPallet''';
        EXEC sys.sp_executesql @sql, N'@def NVARCHAR(MAX) OUTPUT', @def OUTPUT;
        IF @def IS NULL
        BEGIN
          SET @avisos = CONCAT_WS('; ', @avisos, 'CAJAS: la base no tiene pa_SearchPalletBoxDispatchPallet');
        END
        ELSE IF CHARINDEX(@Viejo, @def) > 0
        BEGIN
          SET @pos = CHARINDEX(N'CREATE PROCEDURE', @def);
          IF @pos = 0 SET @pos = CHARINDEX(N'CREATE   PROCEDURE', @def);
          IF @pos = 0
          BEGIN
            SET @avisos = CONCAT_WS('; ', @avisos, 'CAJAS: no se encontró CREATE PROCEDURE; revisar a mano');
          END
          ELSE
          BEGIN
            SET @falta = CONCAT_WS(', ', @falta, 'columna CAJAS de pa_SearchPalletBoxDispatchPallet');
            IF @Modo <> 'LISTAR'
            BEGIN
              SET @def = STUFF(REPLACE(@def, @Viejo, @Nuevo), @pos, 6, N'ALTER');
              EXEC @exec @def;
            END
          END
        END

        /* ── 5. AuditoriaNivelEmpaque (si hay trigger y falta la tabla) ── */
        SET @sql = N'SELECT @n = CASE WHEN EXISTS (SELECT 1 FROM ' + @q + N'.sys.triggers WHERE name = ''trg_AuditoriaNivelIdEmpaque'')
                                       AND OBJECT_ID(''' + @q + N'.dbo.AuditoriaNivelEmpaque'', ''U'') IS NULL THEN 1 ELSE 0 END';
        EXEC sys.sp_executesql @sql, N'@n INT OUTPUT', @n OUTPUT;
        IF @n = 1
        BEGIN
          SET @falta = CONCAT_WS(', ', @falta, 'tabla AuditoriaNivelEmpaque');
          IF @Modo <> 'LISTAR'
            EXEC @exec N'CREATE TABLE dbo.AuditoriaNivelEmpaque (
    Id INT IDENTITY(1,1) NOT NULL PRIMARY KEY CLUSTERED, EmpaqueId INT NULL, SerialId INT NULL,
    NivelIdAnterior INT NULL, NivelIdNuevo INT NULL, UsuarioId INT NULL, FechaCambio DATETIME NULL DEFAULT (GETDATE()));';
        END

        /* ── 6e. Tipos de maestro de los parámetros por cliente ── */
        SET @sql = N'SELECT @n = (SELECT COUNT(*) FROM (VALUES (''SmartCard Ingreso''), (''Limpieza Caracteres''), (''Diagnostico Serial3''), (''Responsable Op''), (''Tipo Empaque'')) v(n)
                      WHERE NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.TipoMaestro t WHERE t.Nombre = v.n))';
        EXEC sys.sp_executesql @sql, N'@n INT OUTPUT', @n OUTPUT;
        IF @n > 0
        BEGIN
          SET @falta = CONCAT_WS(', ', @falta, CONCAT(@n, ' tipos de maestro de parámetros'));
          IF @Modo <> 'LISTAR'
          BEGIN
            SET @sql = N'INSERT INTO ' + @q + N'.dbo.TipoMaestro (Nombre, Descripcion, Activo)
SELECT v.n, v.n, 1 FROM (VALUES (''SmartCard Ingreso''), (''Limpieza Caracteres''), (''Diagnostico Serial3''), (''Responsable Op''), (''Tipo Empaque'')) v(n)
 WHERE NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.TipoMaestro t WHERE t.Nombre = v.n);';
            EXEC sys.sp_executesql @sql;
          END
        END

        /* ── 6h. Campos calculados que hoy hace el SP de la base ── */
        DELETE FROM @codigosBase;
        IF EXISTS (SELECT 1 FROM @reglas WHERE Base = @db) AND OBJECT_ID('WmsWdGeneral.dbo.CodigoSapRegla', 'U') IS NOT NULL
        BEGIN
          SET @sql = N'SELECT Id FROM ' + @q + N'.dbo.CodigoSap';
          INSERT INTO @codigosBase EXEC sys.sp_executesql @sql;
          SET @n = (SELECT COUNT(*) FROM @reglas r JOIN WmsWdGeneral.dbo.Cliente cl ON cl.dbase = r.Base
                     WHERE r.Base = @db
                       AND EXISTS (SELECT 1 FROM @codigosBase c WHERE c.Id = r.CodigoSapId)
                       AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.CodigoSapRegla x
                                        WHERE x.ClienteId = cl.Id AND x.CodigoSapId = r.CodigoSapId AND x.Campo = r.Campo));
          IF @n > 0
          BEGIN
            SET @falta = CONCAT_WS(', ', @falta, CONCAT(@n, ' campos calculados (Códigos SAP)'));
            IF @Modo <> 'LISTAR'
              INSERT INTO WmsWdGeneral.dbo.CodigoSapRegla (ClienteId, CodigoSapId, Campo, Origen, Operacion, Parametro1, Parametro2,
                                                           Momento, Activo, Usuario, Fecha)
              SELECT cl.Id, r.CodigoSapId, r.Campo, r.Origen, r.Operacion, r.Parametro1, NULLIF(r.Parametro2, ''), r.Momento, 1,
                     'script 2026-11-17_instalar_por_base_todas', GETDATE()
                FROM @reglas r JOIN WmsWdGeneral.dbo.Cliente cl ON cl.dbase = r.Base
               WHERE r.Base = @db
                 AND EXISTS (SELECT 1 FROM @codigosBase c WHERE c.Id = r.CodigoSapId)
                 AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.CodigoSapRegla x
                                  WHERE x.ClienteId = cl.Id AND x.CodigoSapId = r.CodigoSapId AND x.Campo = r.Campo);
          END
        END

        /* ── 6i. Limpieza de caracteres de Claro Ecuador (maestro + interruptor) ── */
        IF @db = N'WmsWdClaroEcuador'
        BEGIN
          SET @sql = N'SELECT @n = (SELECT COUNT(*) FROM (VALUES (1, ''+1C''), (2, CHAR(209)), (3, '':'')) v(Orden, Texto)
                        WHERE EXISTS (SELECT 1 FROM ' + @q + N'.dbo.TipoMaestro WHERE Nombre = ''Limpieza Caracteres'')
                          AND NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Maestro m JOIN ' + @q + N'.dbo.TipoMaestro t ON t.Id = m.TipoMaestroId
                                           WHERE t.Nombre = ''Limpieza Caracteres'' AND m.Codigo = v.Texto))';
          EXEC sys.sp_executesql @sql, N'@n INT OUTPUT', @n OUTPUT;
          IF @n > 0
          BEGIN
            SET @falta = CONCAT_WS(', ', @falta, CONCAT(@n, ' filas del maestro Limpieza Caracteres'));
            IF @Modo <> 'LISTAR'
            BEGIN
              SET @sql = N'INSERT INTO ' + @q + N'.dbo.Maestro (Codigo, Descripcion, TipoMaestroId, Activo, Detalle, adicional)
SELECT v.Texto, CONCAT(''Quitar '', v.Texto, '' de la MAC''), t.Id, 1, ''MAC'', 0
  FROM (VALUES (1, ''+1C''), (2, CHAR(209)), (3, '':'')) v(Orden, Texto)
 CROSS APPLY (SELECT TOP 1 Id FROM ' + @q + N'.dbo.TipoMaestro WHERE Nombre = ''Limpieza Caracteres'' ORDER BY Id) t
 WHERE NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Maestro m WHERE m.TipoMaestroId = t.Id AND m.Codigo = v.Texto)
 ORDER BY v.Orden;';
              EXEC sys.sp_executesql @sql;
            END
          END

          SET @tipoVal = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = 'LIMPIEZA_CARACTERES_INGRESO' ORDER BY Id);
          IF @tipoVal IS NULL
          BEGIN
            SET @avisos = CONCAT_WS('; ', @avisos, 'LIMPIEZA_CARACTERES_INGRESO: falta correr 2026-10-29_parametros_wms.sql');
          END
          ELSE
          BEGIN
            SET @n = (SELECT COUNT(*) FROM WmsWdGeneral.dbo.Cliente cl
                       WHERE cl.dbase = @db
                         AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion cv
                                          WHERE cv.ClienteId = cl.Id AND cv.ValidacionTipoId = @tipoVal AND ISNULL(cv.Activo, 0) = 1));
            IF @n > 0
            BEGIN
              SET @falta = CONCAT_WS(', ', @falta, 'interruptor LIMPIEZA_CARACTERES_INGRESO');
              IF @Modo <> 'LISTAR'
              BEGIN
                UPDATE cv SET Activo = 1
                  FROM WmsWdGeneral.dbo.ClienteValidacion cv JOIN WmsWdGeneral.dbo.Cliente cl ON cl.Id = cv.ClienteId
                 WHERE cl.dbase = @db AND cv.ValidacionTipoId = @tipoVal AND ISNULL(cv.Activo, 0) = 0;
                INSERT INTO WmsWdGeneral.dbo.ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha)
                SELECT cl.Id, @tipoVal, 1, GETDATE() FROM WmsWdGeneral.dbo.Cliente cl
                 WHERE cl.dbase = @db
                   AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion cv WHERE cv.ClienteId = cl.Id AND cv.ValidacionTipoId = @tipoVal);
              END
            END
          END
        END

        /* ── 6 / 6b / 6c. Interruptores opcionales por cliente (solo con @SoloBase) ── */
        IF @clienteId IS NOT NULL AND EXISTS (SELECT 1 FROM @sw)
        BEGIN
          IF EXISTS (SELECT 1 FROM @sw s WHERE NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacionTipo t WHERE t.Codigo = s.Codigo))
            SET @avisos = CONCAT_WS('; ', @avisos, 'falta algún tipo de validación pedido (correr los scripts generales: boleta_reservas_consecutivo / documentos_prealertas)');
          SET @n = (SELECT COUNT(*) FROM @sw s JOIN WmsWdGeneral.dbo.ClienteValidacionTipo t ON t.Codigo = s.Codigo
                     WHERE NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion cv
                                        WHERE cv.ClienteId = @clienteId AND cv.ValidacionTipoId = t.Id AND ISNULL(cv.Activo, 0) = 1));
          IF @n > 0
          BEGIN
            SET @falta = CONCAT_WS(', ', @falta, CONCAT(@n, ' interruptores por cliente'));
            IF @Modo <> 'LISTAR'
            BEGIN
              UPDATE cv SET Activo = 1
                FROM WmsWdGeneral.dbo.ClienteValidacion cv
                JOIN WmsWdGeneral.dbo.ClienteValidacionTipo t ON t.Id = cv.ValidacionTipoId
                JOIN @sw s ON s.Codigo = t.Codigo
               WHERE cv.ClienteId = @clienteId AND ISNULL(cv.Activo, 0) = 0;
              INSERT INTO WmsWdGeneral.dbo.ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha)
              SELECT @clienteId, t.Id, 1, GETDATE()
                FROM @sw s JOIN WmsWdGeneral.dbo.ClienteValidacionTipo t ON t.Codigo = s.Codigo
               WHERE NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion cv WHERE cv.ClienteId = @clienteId AND cv.ValidacionTipoId = t.Id);
            END
          END
        END

        /* ── 6d. Smartcard automática (opcional, solo con @SoloBase) ── */
        IF @ActivarSmartcardAutomatica = 1 AND @clienteId IS NOT NULL
        BEGIN
          SET @csId = NULL;
          SET @estId = NULL;
          SET @sql = N'SELECT @csId = (SELECT TOP 1 Id FROM ' + @q + N'.dbo.CodigoSap WHERE Codigo = @cod),
                              @estId = (SELECT TOP 1 Id FROM ' + @q + N'.dbo.Estado WHERE Nombre = @des)';
          EXEC sys.sp_executesql @sql, N'@cod VARCHAR(50), @des VARCHAR(50), @csId INT OUTPUT, @estId INT OUTPUT',
               @SmartcardCodigoSap, @SmartcardDestino, @csId OUTPUT, @estId OUTPUT;
          SET @tipoVal = (SELECT TOP 1 Id FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = 'INSERT_AUTOMATICO_SMARTCARD' ORDER BY Id);
          IF @csId IS NULL OR @estId IS NULL OR @tipoVal IS NULL
          BEGIN
            SET @avisos = CONCAT_WS('; ', @avisos, CONCAT('SMARTCARD no se activó: falta el código SAP ', @SmartcardCodigoSap,
                                    ', el estado ', @SmartcardDestino, ' o el tipo INSERT_AUTOMATICO_SMARTCARD'));
          END
          ELSE
          BEGIN
            SET @n = (SELECT COUNT(*) FROM WmsWdGeneral.dbo.ClienteValidacion cv
                       WHERE cv.ClienteId = @clienteId AND cv.ValidacionTipoId = @tipoVal AND ISNULL(cv.Activo, 0) = 1);
            IF @n = 0
            BEGIN
              SET @falta = CONCAT_WS(', ', @falta, 'smartcard automática');
              IF @Modo <> 'LISTAR'
              BEGIN
                SET @sql = N'
DECLARE @tm INT = (SELECT TOP 1 Id FROM ' + @q + N'.dbo.TipoMaestro WHERE Nombre = ''SmartCard Ingreso'');
IF @tm IS NULL
BEGIN
  INSERT INTO ' + @q + N'.dbo.TipoMaestro (Nombre, Descripcion, Activo) VALUES (''SmartCard Ingreso'', ''SmartCard Ingreso'', 1);
  SET @tm = SCOPE_IDENTITY();
END
IF NOT EXISTS (SELECT 1 FROM ' + @q + N'.dbo.Maestro WHERE TipoMaestroId = @tm AND Activo = 1)
  INSERT INTO ' + @q + N'.dbo.Maestro (Codigo, Descripcion, TipoMaestroId, Activo, Detalle, adicional)
  SELECT TOP 1 Codigo, Descripcion, @tm, 1, @des, 0 FROM ' + @q + N'.dbo.CodigoSap WHERE Codigo = @cod;';
                EXEC sys.sp_executesql @sql, N'@cod VARCHAR(50), @des VARCHAR(50)', @SmartcardCodigoSap, @SmartcardDestino;
                IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoVal)
                  UPDATE WmsWdGeneral.dbo.ClienteValidacion SET Activo = 1 WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoVal;
                ELSE
                  INSERT INTO WmsWdGeneral.dbo.ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha) VALUES (@clienteId, @tipoVal, 1, GETDATE());
              END
            END
          END
        END

        /* ── 6g. Columnas de Forecast en Etiquetado (opcional, solo con @SoloBase) ── */
        IF @AgregarColumnasForecast = 1
        BEGIN
          SET @n = IIF(OBJECT_ID(@q + N'.dbo.Etiquetado', 'U') IS NOT NULL AND COL_LENGTH(@q + N'.dbo.Etiquetado', 'TipoEmpaque') IS NULL, 1, 0);
          IF @n = 1
          BEGIN
            SET @falta = CONCAT_WS(', ', @falta, 'columnas Forecast en Etiquetado');
            IF @Modo <> 'LISTAR'
            BEGIN
              SET @sql = N'ALTER TABLE ' + @q + N'.dbo.Etiquetado ADD TipoEmpaque VARCHAR(50) NULL, Lote INT NULL, [Año] VARCHAR(20) NULL, FechaLote DATETIME NULL;';
              EXEC sys.sp_executesql @sql;
            END
          END
        END

        IF @Modo = 'APLICAR'
        BEGIN
          COMMIT TRANSACTION;
        END

        INSERT INTO @res VALUES (@db,
          CASE WHEN @falta IS NULL THEN 'completo'
               WHEN @Modo = 'LISTAR' THEN 'falta: ' + @falta
               WHEN @Modo = 'PROBAR' THEN 'probado OK (revertido): ' + @falta
               ELSE 'aplicado: ' + @falta END,
          @avisos);
      END TRY
      BEGIN CATCH
        IF @Modo = 'PROBAR'
        BEGIN
          IF XACT_STATE() = 1
          BEGIN
            ROLLBACK TRANSACTION sp_base;
          END
          ELSE IF XACT_STATE() = -1
          BEGIN
            ROLLBACK TRANSACTION;
            BEGIN TRANSACTION;
          END
        END
        ELSE IF @Modo = 'APLICAR' AND @@TRANCOUNT > 0
        BEGIN
          ROLLBACK TRANSACTION;
        END
        INSERT INTO @res VALUES (@db, 'ERROR (base sin cambios): ' + LEFT(ERROR_MESSAGE(), 800), @avisos);
      END CATCH
    END
  END
  FETCH NEXT FROM bases INTO @db;
END
CLOSE bases;
DEALLOCATE bases;

IF @Modo = 'PROBAR' AND @@TRANCOUNT > 0
BEGIN
  ROLLBACK TRANSACTION;
END

/* Resumen: cuántas bases hay por resultado */
SELECT CASE @Modo WHEN 'LISTAR' THEN 'LISTAR: NO se cambió nada'
                  WHEN 'PROBAR' THEN 'PROBAR: se hizo y se revirtió'
                  ELSE               'APLICAR: cambios guardados' END AS Modo,
       Resultado, COUNT(*) AS Bases
  FROM (SELECT CASE WHEN Resultado LIKE 'ERROR%' THEN 'ERROR'
                    WHEN Resultado = 'completo' THEN 'completo'
                    WHEN Resultado LIKE 'sin tablas%' THEN 'no aplica'
                    WHEN Resultado LIKE 'EXCLUIDA%' THEN 'excluida'
                    ELSE LEFT(Resultado, CHARINDEX(':', Resultado + ':') - 1) END AS Resultado
          FROM @res) x
 GROUP BY Resultado ORDER BY COUNT(*) DESC;

/* Detalle por base (errores primero) */
SELECT Base, Resultado, Avisos FROM @res ORDER BY CASE WHEN Resultado LIKE 'ERROR%' THEN 0 ELSE 1 END, Base;

/* Bases con avisos de cosas que este script no crea */
SELECT Base, Avisos FROM @res WHERE Avisos IS NOT NULL ORDER BY Base;
