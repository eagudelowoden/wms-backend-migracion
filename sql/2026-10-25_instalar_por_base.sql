/* =====================================================================================
   Instalación por base cliente de los ajustes WMS (rama Bernabe-ajustes).
   Correr DESPUÉS de los scripts generales (WmsWdGeneral):
     1) 2026-10-24_boleta_reservas_consecutivo.sql
     2) 2026-10-25_despachado_reversa.sql
     3) 2026-10-25_notificaciones_correo.sql
     4) 2026-10-26_documentos_prealertas.sql
   y luego este, una vez por cada base, cambiando @Db (empezar con Legacy).

   Hace en la base indicada (solo lo que falta; correrlo dos veces no hace nada):
     - Catálogo Permiso (general, una sola vez): pestaña Despachado (Opc_Despachado) y sus secciones
       Equipos / Accesorios (Opc_Equipos, Opc_Accesorios). Se asignan a los perfiles ADMINISTRADOR del cliente;
       a los demás perfiles se les da desde Administración > Perfiles.
     - Work flow de Despachado: origen DESPACHADO -> NOVEDAD (equipos) y NOVEDAD-ACCESORIOS (accesorios).
       Se cambia cuando se quiera desde Administración > Work flow.
     - Menú: Administración > Notificaciones (ADNOT) y el módulo 44 a los perfiles ADMINISTRADOR del cliente.
     - Menú: Administración > Documentos (ADDOC) y el módulo 47 a los perfiles ADMINISTRADOR del cliente.
     - Tipo de novedad de accesorios NO CONFORMIDAD (si la base tiene la novedad de accesorios).
     - Columna CAJAS de Despacho > Proceso: si el pallet no tiene cajas de despacho cuenta las de empaque.
     - AuditoriaNivelEmpaque si la base tiene el trigger pero no la tabla.
     - Interruptor BOLETA_CONSECUTIVO del cliente (boletas guardadas + consecutivo) si @ActivarConsecutivo = 1.
     - Interruptor SONIDO_ALERTAS_INGRESO (catálogo, una vez) y para el cliente si @ActivarSonido = 1.
     - Interruptores PREALERTA_CIERRE_MANUAL / PREALERTA_SOBRANTES para el cliente si @ActivarCierreManual /
       @ActivarSobrantes = 1.
     - INSERT_AUTOMATICO_SMARTCARD y EMPAQUE_CODIGOSAP_PALLET si @ActivarSmartcardAutomatica /
       @ActivarEmpaqueCodigoPallet = 1. La smartcard se configura en el maestro "SmartCard Ingreso" de la base (Código =
       código SAP, Descripción, Detalle = destino): si no tiene filas se crea una con @SmartcardCodigoSap /
       @SmartcardDestino (se valida que existan en la base).
     - (2026-10-29, correr antes 2026-10-29_parametros_wms.sql) Tipos de maestro "SmartCard Ingreso", "Limpieza
       Caracteres", "Diagnostico Serial3" y "Responsable Op" (vacíos: se llenan en Administración > Maestros), menú
       Administración > Variables de etiquetas (ADVAR) con el módulo 48 para los perfiles ADMINISTRADOR y, con
       @AgregarColumnasForecast = 1, las columnas del Forecast en Etiquetado para probar ETIQUETADO_FORECAST.
   Los interruptores también se encienden o apagan desde Administración > Clientes > Validaciones; el usuario ve el
   cambio al volver a iniciar sesión.
     - Avisa si falta la tipología NO CONFORMIDAD o la novedad de accesorios (Despachado de accesorios la necesita).
   ===================================================================================== */
SET NOCOUNT ON;

DECLARE @Db SYSNAME = 'WmsWdLegacyCostaRica';
DECLARE @ActivarConsecutivo BIT = 0;     -- 1 = boletas guardadas con documento consecutivo AAAA-NNN para este cliente
DECLARE @ActivarSonido BIT = 0;          -- 1 = sonido en los avisos y errores del WMS para este cliente
DECLARE @ActivarCierreManual BIT = 0;    -- 1 = botón Cerrar prealerta en Ingreso
DECLARE @ActivarSobrantes BIT = 0;       -- 1 = campo Sobrante de en Ingreso
DECLARE @ActivarSmartcardAutomatica BIT = 0;          -- 1 = el Serial3 (smartcard) se crea también en Ingreso
DECLARE @SmartcardCodigoSap VARCHAR(50) = 'CARD0001'; -- código SAP de la smartcard en esta base
DECLARE @SmartcardDestino VARCHAR(50) = 'SMARTCARD';  -- estado (destino) con el que queda la smartcard
DECLARE @ActivarEmpaqueCodigoPallet BIT = 0;          -- 1 = el empaque toma el código SAP del pallet
DECLARE @AgregarColumnasForecast BIT = 0;             -- 1 = TipoEmpaque, Lote, Año y FechaLote en Etiquetado (pruebas)

DECLARE @sql NVARCHAR(MAX), @n INT, @clienteId INT;

IF DB_ID(@Db) IS NULL
BEGIN
  RAISERROR('La base %s no existe.', 16, 1, @Db);
  RETURN;
END

SELECT @clienteId = Id FROM WmsWdGeneral.dbo.Cliente WHERE dbase = @Db;
PRINT CONCAT('Base ', @Db, ' · ClienteId ', ISNULL(CAST(@clienteId AS VARCHAR(10)), '(no encontrado)'));

/* ── 1. Catálogo Permiso (pestaña y secciones de Despachado) ─────────────────────── */
DECLARE @moduloDespacho INT = (SELECT Id FROM WmsWdGeneral.dbo.Modulo WHERE Nombre = 'Despacho');
IF @moduloDespacho IS NULL
BEGIN
  RAISERROR('No existe el módulo Despacho en WmsWdGeneral.dbo.Modulo.', 16, 1);
  RETURN;
END

IF NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Permiso WHERE ModuloId = @moduloDespacho AND Nombre = 'Opc_Despachado' AND Descripcion = 'Despacho')
  INSERT INTO WmsWdGeneral.dbo.Permiso (ModuloId, Nombre, Descripcion) VALUES (@moduloDespacho, 'Opc_Despachado', 'Despacho');
IF NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Permiso WHERE ModuloId = @moduloDespacho AND Nombre = 'Opc_Equipos' AND Descripcion = 'Despachado')
  INSERT INTO WmsWdGeneral.dbo.Permiso (ModuloId, Nombre, Descripcion) VALUES (@moduloDespacho, 'Opc_Equipos', 'Despachado');
IF NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Permiso WHERE ModuloId = @moduloDespacho AND Nombre = 'Opc_Accesorios' AND Descripcion = 'Despachado')
  INSERT INTO WmsWdGeneral.dbo.Permiso (ModuloId, Nombre, Descripcion) VALUES (@moduloDespacho, 'Opc_Accesorios', 'Despachado');

-- perfiles ADMINISTRADOR del cliente
INSERT INTO WmsWdGeneral.dbo.PerfilPermiso (PerfilId, PermisoId)
SELECT pf.Id, pe.Id
  FROM WmsWdGeneral.dbo.Perfil pf
 CROSS JOIN WmsWdGeneral.dbo.Permiso pe
 WHERE pf.ClienteId = @clienteId
   AND pf.Nombre LIKE '%ADMINISTRADOR%'
   AND pe.ModuloId = @moduloDespacho
   AND ((pe.Nombre = 'Opc_Despachado' AND pe.Descripcion = 'Despacho')
     OR (pe.Nombre IN ('Opc_Equipos', 'Opc_Accesorios') AND pe.Descripcion = 'Despachado'))
   AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.PerfilPermiso x WHERE x.PerfilId = pf.Id AND x.PermisoId = pe.Id);
PRINT CONCAT('Permisos de Despachado asignados a perfiles ADMINISTRADOR: ', @@ROWCOUNT);

/* ── 2. Work flow de Despachado (destinos al guardar) ─────────────────────────────── */
DECLARE @despachado INT = (SELECT Id FROM WmsWdGeneral.dbo.Estado WHERE Nombre = 'DESPACHADO');
DECLARE @novedad INT = (SELECT Id FROM WmsWdGeneral.dbo.Estado WHERE Nombre = 'NOVEDAD');
DECLARE @novedadAcc INT = (SELECT Id FROM WmsWdGeneral.dbo.Estado WHERE Nombre = 'NOVEDAD-ACCESORIOS');
DECLARE @opcEquipos INT = (SELECT Id FROM WmsWdGeneral.dbo.Permiso WHERE ModuloId = @moduloDespacho AND Nombre = 'Opc_Equipos' AND Descripcion = 'Despachado');
DECLARE @opcAccesorios INT = (SELECT Id FROM WmsWdGeneral.dbo.Permiso WHERE ModuloId = @moduloDespacho AND Nombre = 'Opc_Accesorios' AND Descripcion = 'Despachado');

SET @sql = N'
DECLARE @wf INT;
SELECT @wf = Id FROM ' + QUOTENAME(@Db) + N'.dbo.WorkFlow WHERE ModuloId = @modulo AND OrigenId = @origen AND OpcionId = @opcion AND TipologiaId = 0;
IF @wf IS NULL
BEGIN
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.WorkFlow (ModuloId, OrigenId, OpcionId, TipologiaId, nivelId) VALUES (@modulo, @origen, @opcion, 0, 0);
  SET @wf = SCOPE_IDENTITY();
END
IF NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.WorkFlowDestino WHERE WorkFlowId = @wf)
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.WorkFlowDestino (WorkFlowId, DestinoId) VALUES (@wf, @destino);';
EXEC sys.sp_executesql @sql, N'@modulo INT, @origen INT, @opcion INT, @destino INT',
     @moduloDespacho, @despachado, @opcEquipos, @novedad;
EXEC sys.sp_executesql @sql, N'@modulo INT, @origen INT, @opcion INT, @destino INT',
     @moduloDespacho, @despachado, @opcAccesorios, @novedadAcc;
PRINT 'Work flow de Despachado listo (equipos -> NOVEDAD, accesorios -> NOVEDAD-ACCESORIOS)';

/* ── 3. Menú Administración > Notificaciones ─────────────────────────────────────── */
SET @sql = N'
IF OBJECT_ID(''' + QUOTENAME(@Db) + N'.dbo.Menu'', ''U'') IS NOT NULL
   AND EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Menu WHERE Id = ''ADM'')
   AND NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Menu WHERE Id = ''ADNOT'')
BEGIN
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.Menu (Id, Descripcion, Tipo, Orden, Accion, Estado, Icono, Id_padre, View_name, Controller_name)
  VALUES (''ADNOT'', ''Notificaciones'', ''item'', 20, ''ADMNOT'', ''activo'', '''', ''ADM'', '''', '''');
  PRINT ''Menú Notificaciones agregado'';
END';
EXEC sys.sp_executesql @sql;

-- módulo Notificaciones (44) a los perfiles ADMINISTRADOR del cliente (el resto se asigna en Perfiles)
IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Modulo WHERE Id = 44 AND Nombre = 'Notificaciones')
BEGIN
  INSERT INTO WmsWdGeneral.dbo.PerfilModulo (PerfilId, ModuloId, Orden)
  SELECT pf.Id, 44, 0
    FROM WmsWdGeneral.dbo.Perfil pf
   WHERE pf.ClienteId = @clienteId AND pf.Nombre LIKE '%ADMINISTRADOR%'
     AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.PerfilModulo x WHERE x.PerfilId = pf.Id AND x.ModuloId = 44);
  PRINT CONCAT('Módulo Notificaciones asignado a perfiles ADMINISTRADOR: ', @@ROWCOUNT);
END
ELSE
  PRINT 'AVISO: falta el módulo 44 Notificaciones (correr 2026-10-25_notificaciones_correo.sql)';

/* ── 3a. Menú Administración > Documentos ────────────────────────────────────────── */
SET @sql = N'
IF OBJECT_ID(''' + QUOTENAME(@Db) + N'.dbo.Menu'', ''U'') IS NOT NULL
   AND EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Menu WHERE Id = ''ADM'')
   AND NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Menu WHERE Id = ''ADDOC'')
BEGIN
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.Menu (Id, Descripcion, Tipo, Orden, Accion, Estado, Icono, Id_padre, View_name, Controller_name)
  VALUES (''ADDOC'', ''Documentos'', ''item'', 21, ''ADMDOC'', ''activo'', '''', ''ADM'', '''', '''');
  PRINT ''Menú Documentos agregado'';
END';
EXEC sys.sp_executesql @sql;

IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Modulo WHERE Id = 47 AND Nombre = 'Documentos')
BEGIN
  INSERT INTO WmsWdGeneral.dbo.PerfilModulo (PerfilId, ModuloId, Orden)
  SELECT pf.Id, 47, 0
    FROM WmsWdGeneral.dbo.Perfil pf
   WHERE pf.ClienteId = @clienteId AND pf.Nombre LIKE '%ADMINISTRADOR%'
     AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.PerfilModulo x WHERE x.PerfilId = pf.Id AND x.ModuloId = 47);
  PRINT CONCAT('Módulo Documentos asignado a perfiles ADMINISTRADOR: ', @@ROWCOUNT);
END
ELSE
  PRINT 'AVISO: falta el módulo 47 Documentos (correr 2026-10-26_documentos_prealertas.sql)';

/* ── 3b. Tipo de novedad de accesorios NO CONFORMIDAD (Despachado de accesorios) ──── */
SET @sql = N'
DECLARE @tm INT = (SELECT TOP 1 Id FROM ' + QUOTENAME(@Db) + N'.dbo.TipoMaestro WHERE Nombre = ''Tipo Novedad Accesorios'');
IF @tm IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Maestro WHERE TipoMaestroId = @tm AND Codigo = ''NO CONFORMIDAD'')
BEGIN
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.Maestro (Codigo, Descripcion, TipoMaestroId, Activo, Detalle, adicional)
  VALUES (''NO CONFORMIDAD'', ''NO CONFORMIDAD'', @tm, 1, '''', 0);
  PRINT ''Tipo de novedad de accesorios NO CONFORMIDAD agregado'';
END';
EXEC sys.sp_executesql @sql;

/* ── 4. Columna CAJAS de Despacho > Proceso ──────────────────────────────────────── */
DECLARE @Viejo NVARCHAR(400) = N'COALESCE(COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END), 0) AS cajas';
DECLARE @Nuevo NVARCHAR(800) = N'CASE WHEN COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END) > 0 THEN COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END) ELSE (SELECT COUNT(DISTINCT ie.CajaEmpaqueId) FROM Ingreso ie WHERE ie.PalletId = p.id AND ie.CajaEmpaqueId > 0) END AS cajas';
DECLARE @def NVARCHAR(MAX), @pos INT, @exec NVARCHAR(400);
SET @sql = N'SELECT @def = m.definition FROM ' + QUOTENAME(@Db) + N'.sys.sql_modules m JOIN ' + QUOTENAME(@Db)
         + N'.sys.objects o ON o.object_id = m.object_id WHERE o.name = ''pa_SearchPalletBoxDispatchPallet''';
EXEC sys.sp_executesql @sql, N'@def NVARCHAR(MAX) OUTPUT', @def OUTPUT;
IF @def IS NULL
  PRINT 'CAJAS: la base no tiene pa_SearchPalletBoxDispatchPallet';
ELSE IF CHARINDEX(@Viejo, @def) = 0
  PRINT 'CAJAS: sin cambios (ya aplicado o versión distinta)';
ELSE
BEGIN
  SET @pos = CHARINDEX(N'CREATE PROCEDURE', @def);
  IF @pos = 0 SET @pos = CHARINDEX(N'CREATE   PROCEDURE', @def);
  IF @pos = 0
    PRINT 'CAJAS: no se encontró CREATE PROCEDURE en la definición; revisar a mano';
  ELSE
  BEGIN
    SET @def = STUFF(REPLACE(@def, @Viejo, @Nuevo), @pos, 6, N'ALTER');
    SET @exec = QUOTENAME(@Db) + N'.sys.sp_executesql';
    EXEC @exec @def;
    PRINT 'CAJAS: aplicado';
  END
END

/* ── 5. AuditoriaNivelEmpaque (si hay trigger y falta la tabla) ──────────────────── */
SET @sql = N'
IF EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.sys.triggers WHERE name = ''trg_AuditoriaNivelIdEmpaque'')
   AND OBJECT_ID(''' + QUOTENAME(@Db) + N'.dbo.AuditoriaNivelEmpaque'', ''U'') IS NULL
BEGIN
  EXEC ' + QUOTENAME(@Db) + N'.sys.sp_executesql N''CREATE TABLE dbo.AuditoriaNivelEmpaque (
    Id INT IDENTITY(1,1) NOT NULL PRIMARY KEY CLUSTERED, EmpaqueId INT NULL, SerialId INT NULL,
    NivelIdAnterior INT NULL, NivelIdNuevo INT NULL, UsuarioId INT NULL, FechaCambio DATETIME NULL DEFAULT (GETDATE()));'';
  PRINT ''AuditoriaNivelEmpaque creada'';
END';
EXEC sys.sp_executesql @sql;

/* ── 6. Interruptor BOLETA_CONSECUTIVO (opcional) ────────────────────────────────── */
IF @ActivarConsecutivo = 1 AND @clienteId IS NOT NULL
BEGIN
  DECLARE @tipoConsecutivo INT = (SELECT Id FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = 'BOLETA_CONSECUTIVO');
  IF @tipoConsecutivo IS NULL
    PRINT 'BOLETA_CONSECUTIVO: falta correr 2026-10-24_boleta_reservas_consecutivo.sql';
  ELSE IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoConsecutivo)
    UPDATE WmsWdGeneral.dbo.ClienteValidacion SET Activo = 1 WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoConsecutivo;
  ELSE
    INSERT INTO WmsWdGeneral.dbo.ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha) VALUES (@clienteId, @tipoConsecutivo, 1, GETDATE());
  PRINT 'BOLETA_CONSECUTIVO activo para el cliente';
END

/* ── 6b. Interruptor SONIDO_ALERTAS_INGRESO ──────────────────────────────────────── */
IF NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = 'SONIDO_ALERTAS_INGRESO')
  INSERT INTO WmsWdGeneral.dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('SONIDO_ALERTAS_INGRESO', 'Sonido en los avisos y errores del WMS en todos los módulos (se configura en Administración > Notificaciones)', 1, GETDATE());
IF @ActivarSonido = 1 AND @clienteId IS NOT NULL
BEGIN
  DECLARE @tipoSonido INT = (SELECT Id FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = 'SONIDO_ALERTAS_INGRESO');
  IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoSonido)
    UPDATE WmsWdGeneral.dbo.ClienteValidacion SET Activo = 1 WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoSonido;
  ELSE
    INSERT INTO WmsWdGeneral.dbo.ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha) VALUES (@clienteId, @tipoSonido, 1, GETDATE());
  PRINT 'SONIDO_ALERTAS_INGRESO activo para el cliente';
END

/* ── 6c. Interruptores de prealertas (opcionales) ────────────────────────────────── */
DECLARE @codigo VARCHAR(60), @tipoVal INT;
DECLARE interruptores CURSOR LOCAL FAST_FORWARD FOR
  SELECT c FROM (VALUES ('PREALERTA_CIERRE_MANUAL', @ActivarCierreManual), ('PREALERTA_SOBRANTES', @ActivarSobrantes)) v(c, a)
   WHERE a = 1;
OPEN interruptores;
FETCH NEXT FROM interruptores INTO @codigo;
WHILE @@FETCH_STATUS = 0
BEGIN
  SET @tipoVal = (SELECT Id FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = @codigo);
  IF @tipoVal IS NULL OR @clienteId IS NULL
    PRINT CONCAT(@codigo, ': falta correr 2026-10-26_documentos_prealertas.sql o no se encontró el cliente');
  ELSE
  BEGIN
    IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoVal)
      UPDATE WmsWdGeneral.dbo.ClienteValidacion SET Activo = 1 WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoVal;
    ELSE
      INSERT INTO WmsWdGeneral.dbo.ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha) VALUES (@clienteId, @tipoVal, 1, GETDATE());
    PRINT CONCAT(@codigo, ' activo para el cliente');
  END
  FETCH NEXT FROM interruptores INTO @codigo;
END
CLOSE interruptores;
DEALLOCATE interruptores;

/* ── 6d. Smartcard automática y empaque con el código SAP del pallet (opcionales) ── */
IF @ActivarSmartcardAutomatica = 1 AND @clienteId IS NOT NULL
BEGIN
  DECLARE @csId INT, @estId INT;
  SET @sql = N'SELECT @csId = (SELECT TOP 1 Id FROM ' + QUOTENAME(@Db) + N'.dbo.CodigoSap WHERE Codigo = @cod),
                      @estId = (SELECT TOP 1 Id FROM ' + QUOTENAME(@Db) + N'.dbo.Estado WHERE Nombre = @des)';
  EXEC sys.sp_executesql @sql, N'@cod VARCHAR(50), @des VARCHAR(50), @csId INT OUTPUT, @estId INT OUTPUT',
       @SmartcardCodigoSap, @SmartcardDestino, @csId OUTPUT, @estId OUTPUT;
  IF @csId IS NULL OR @estId IS NULL
    PRINT CONCAT('SMARTCARD: no se activó: en ', @Db, ' no existe el código SAP ', @SmartcardCodigoSap,
                 ' o el estado ', @SmartcardDestino);
  ELSE
  BEGIN
    SET @sql = N'
DECLARE @tm INT = (SELECT TOP 1 Id FROM ' + QUOTENAME(@Db) + N'.dbo.TipoMaestro WHERE Nombre = ''SmartCard Ingreso'');
IF @tm IS NULL
BEGIN
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.TipoMaestro (Nombre, Descripcion, Activo) VALUES (''SmartCard Ingreso'', ''SmartCard Ingreso'', 1);
  SET @tm = SCOPE_IDENTITY();
END
IF NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Maestro WHERE TipoMaestroId = @tm AND Activo = 1)
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.Maestro (Codigo, Descripcion, TipoMaestroId, Activo, Detalle, adicional)
  SELECT TOP 1 Codigo, Descripcion, @tm, 1, @des, 0 FROM ' + QUOTENAME(@Db) + N'.dbo.CodigoSap WHERE Codigo = @cod;';
    EXEC sys.sp_executesql @sql, N'@cod VARCHAR(50), @des VARCHAR(50)', @SmartcardCodigoSap, @SmartcardDestino;
    SET @tipoVal = (SELECT Id FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = 'INSERT_AUTOMATICO_SMARTCARD');
    IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoVal)
      UPDATE WmsWdGeneral.dbo.ClienteValidacion SET Activo = 1 WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoVal;
    ELSE
      INSERT INTO WmsWdGeneral.dbo.ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha) VALUES (@clienteId, @tipoVal, 1, GETDATE());
    PRINT CONCAT('INSERT_AUTOMATICO_SMARTCARD activo: ', @SmartcardCodigoSap, ' (Id ', @csId, ') -> ', @SmartcardDestino, ' (Id ', @estId, ')');
  END
END

IF @ActivarEmpaqueCodigoPallet = 1 AND @clienteId IS NOT NULL
BEGIN
  SET @tipoVal = (SELECT Id FROM WmsWdGeneral.dbo.ClienteValidacionTipo WHERE Codigo = 'EMPAQUE_CODIGOSAP_PALLET');
  IF @tipoVal IS NULL
    PRINT 'EMPAQUE_CODIGOSAP_PALLET: falta correr 2026-10-26_documentos_prealertas.sql';
  ELSE
  BEGIN
    IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.ClienteValidacion WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoVal)
      UPDATE WmsWdGeneral.dbo.ClienteValidacion SET Activo = 1 WHERE ClienteId = @clienteId AND ValidacionTipoId = @tipoVal;
    ELSE
      INSERT INTO WmsWdGeneral.dbo.ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha) VALUES (@clienteId, @tipoVal, 1, GETDATE());
    PRINT 'EMPAQUE_CODIGOSAP_PALLET activo para el cliente';
  END
END

/* ── 6e. Tipos de maestro de los parámetros por cliente (2026-10-29) ─────────────── */
SET @sql = N'
DECLARE @nombre VARCHAR(50);
DECLARE tipos CURSOR LOCAL FAST_FORWARD FOR
  SELECT n FROM (VALUES (''SmartCard Ingreso''), (''Limpieza Caracteres''), (''Diagnostico Serial3''), (''Responsable Op'')) v(n);
OPEN tipos;
FETCH NEXT FROM tipos INTO @nombre;
WHILE @@FETCH_STATUS = 0
BEGIN
  IF NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.TipoMaestro WHERE Nombre = @nombre)
  BEGIN
    INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.TipoMaestro (Nombre, Descripcion, Activo) VALUES (@nombre, @nombre, 1);
    PRINT CONCAT(''Tipo de maestro creado: '', @nombre);
  END
  FETCH NEXT FROM tipos INTO @nombre;
END
CLOSE tipos;
DEALLOCATE tipos;';
EXEC sys.sp_executesql @sql;

/* ── 6f. Menú Administración > Variables de etiquetas (módulo 48) ──────────────────── */
SET @sql = N'
IF OBJECT_ID(''' + QUOTENAME(@Db) + N'.dbo.Menu'', ''U'') IS NOT NULL
   AND EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Menu WHERE Id = ''ADM'')
   AND NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Menu WHERE Id = ''ADVAR'')
BEGIN
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.Menu (Id, Descripcion, Tipo, Orden, Accion, Estado, Icono, Id_padre, View_name, Controller_name)
  VALUES (''ADVAR'', ''Variables de etiquetas'', ''item'', 22, ''ADMVAR'', ''activo'', '''', ''ADM'', '''', '''');
  PRINT ''Menú Variables de etiquetas agregado'';
END';
EXEC sys.sp_executesql @sql;

IF EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Modulo WHERE Id = 48 AND Nombre = 'Variables de etiquetas')
BEGIN
  INSERT INTO WmsWdGeneral.dbo.PerfilModulo (PerfilId, ModuloId, Orden)
  SELECT pf.Id, 48, 0
    FROM WmsWdGeneral.dbo.Perfil pf
   WHERE pf.ClienteId = @clienteId AND pf.Nombre LIKE '%ADMINISTRADOR%'
     AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.PerfilModulo x WHERE x.PerfilId = pf.Id AND x.ModuloId = 48);
  PRINT CONCAT('Módulo Variables de etiquetas asignado a perfiles ADMINISTRADOR: ', @@ROWCOUNT);
END
ELSE
  PRINT 'AVISO: falta el módulo 48 Variables de etiquetas (correr 2026-10-29_parametros_wms.sql)';

/* ── 6g. Columnas de Forecast en Etiquetado (opcional, solo para probar ETIQUETADO_FORECAST) ── */
IF @AgregarColumnasForecast = 1
BEGIN
  SET @sql = N'
IF COL_LENGTH(''' + QUOTENAME(@Db) + N'.dbo.Etiquetado'', ''TipoEmpaque'') IS NULL
  ALTER TABLE ' + QUOTENAME(@Db) + N'.dbo.Etiquetado ADD TipoEmpaque VARCHAR(50) NULL, Lote INT NULL, [Año] VARCHAR(20) NULL, FechaLote DATETIME NULL;';
  EXEC sys.sp_executesql @sql;
  PRINT 'Columnas TipoEmpaque, Lote, Año y FechaLote en Etiquetado';
END

/* ── 7. Avisos ───────────────────────────────────────────────────────────────────── */
SET @sql = N'SELECT @n = COUNT(*) FROM ' + QUOTENAME(@Db) + N'.dbo.Maestro m JOIN ' + QUOTENAME(@Db)
         + N'.dbo.TipoMaestro t ON t.Id = m.TipoMaestroId WHERE t.Nombre = ''Tipologias'' AND m.Codigo = ''NO CONFORMIDAD'' AND m.Activo = 1';
EXEC sys.sp_executesql @sql, N'@n INT OUTPUT', @n OUTPUT;
IF @n = 0 PRINT 'AVISO: falta la tipología NO CONFORMIDAD (Despachado no podrá regresar equipos)';

SET @sql = N'SELECT @n = CASE WHEN COL_LENGTH(''' + QUOTENAME(@Db) + N'.dbo.Accesorio'', ''Novedad'') IS NULL THEN 0 ELSE 1 END
            + (SELECT COUNT(*) FROM ' + QUOTENAME(@Db) + N'.sys.objects WHERE name = ''pa_SearchNoveltyAccesoryPallet'')';
EXEC sys.sp_executesql @sql, N'@n INT OUTPUT', @n OUTPUT;
IF @n < 2 PRINT 'AVISO: falta la novedad de accesorios (Despachado de accesorios no funcionará en esta base)';

PRINT 'Listo.';
