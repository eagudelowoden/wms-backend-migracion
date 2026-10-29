/* =====================================================================================
   Boleta de entrega con equipos, accesorios y smartcards por separado, y maestro Tipo Empaque del Forecast.
     1. WmsWdGeneral: columnas Equipos, Accesorios y Smartcards en BoletaMovimientoPallet. Sin ellas las boletas
        guardadas solo conservan la cantidad de siempre.
     2. Base @Db: tipo de maestro "Tipo Empaque" con FULL y SERVICE. Lo corro solo en las bases que usan
        ETIQUETADO_FORECAST; sin filas, Etiquetado ofrece los tipos que ya tiene la tabla Etiquetado.
   Los largos de los campos de etiqueta no necesitan script. Solo creo lo que falta.
   ===================================================================================== */
SET NOCOUNT ON;

DECLARE @Db SYSNAME = 'WmsWdLegacyCostaRica';

IF COL_LENGTH('WmsWdGeneral.dbo.BoletaMovimientoPallet', 'Smartcards') IS NULL
  ALTER TABLE WmsWdGeneral.dbo.BoletaMovimientoPallet ADD Equipos INT NULL, Accesorios INT NULL, Smartcards INT NULL;
PRINT 'BoletaMovimientoPallet: Equipos, Accesorios y Smartcards';

IF DB_ID(@Db) IS NULL
BEGIN
  RAISERROR('La base %s no existe.', 16, 1, @Db);
  RETURN;
END

DECLARE @sql NVARCHAR(MAX) = N'
DECLARE @tm INT = (SELECT TOP 1 Id FROM ' + QUOTENAME(@Db) + N'.dbo.TipoMaestro WHERE Nombre = ''Tipo Empaque'');
IF @tm IS NULL
BEGIN
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.TipoMaestro (Nombre, Descripcion, Activo) VALUES (''Tipo Empaque'', ''Tipo Empaque'', 1);
  SET @tm = SCOPE_IDENTITY();
  PRINT ''Tipo de maestro creado: Tipo Empaque'';
END
IF NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@Db) + N'.dbo.Maestro WHERE TipoMaestroId = @tm)
BEGIN
  INSERT INTO ' + QUOTENAME(@Db) + N'.dbo.Maestro (Codigo, Descripcion, TipoMaestroId, Activo, Detalle, adicional)
  VALUES (''FULL'', ''FULL'', @tm, 1, '''', 0), (''SERVICE'', ''SERVICE'', @tm, 1, '''', 0);
  PRINT ''Tipo Empaque: FULL y SERVICE'';
END';
EXEC sys.sp_executesql @sql;

PRINT 'Listo.';
