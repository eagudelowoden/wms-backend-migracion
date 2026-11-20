/* =====================================================================================
   Novedad de accesorios en todas las bases (la que usa Novedades > Accesorios y Despacho > Despachado). Copio de
   WmsWdLegacyCostaRica, solo donde falta:
     1. Columnas Novedad VARCHAR(50) NULL y UsuarioIdMovimiento INT NULL en Accesorio (sin valor por defecto: no
        reescriben la tabla).
     2. pa_SearchNoveltyAccesoryPallet, pa_UpdateAccesoryNovedadCantidad y pa_UpdateNoveltyAllAccesory.
     3. Tipo de maestro "Tipo Novedad Accesorios" con ERROR CODIGO SAP, ERROR CANTIDAD y NO CONFORMIDAD.
   No instalo MovimientoAccesorio ni sus triggers: la novedad no los usa.
   Cada base va en una sola transacción: si algo falla, esa base queda como estaba.
   @Modo: LISTAR (no cambia nada), PROBAR (hace todo y lo deshace) o APLICAR. @SoloBase: una sola base.
   ===================================================================================== */
SET NOCOUNT ON;
SET LOCK_TIMEOUT 5000;

DECLARE @Modo VARCHAR(10) = 'LISTAR';
DECLARE @SoloBase SYSNAME = NULL;
DECLARE @Fuente SYSNAME = 'WmsWdLegacyCostaRica';

IF @Modo NOT IN ('LISTAR', 'PROBAR', 'APLICAR')
BEGIN
  RAISERROR('@Modo debe ser LISTAR, PROBAR o APLICAR.', 16, 1);
  RETURN;
END

IF OBJECT_ID('tempdb..#sps') IS NOT NULL DROP TABLE #sps;
IF OBJECT_ID('tempdb..#maestro') IS NOT NULL DROP TABLE #maestro;
IF OBJECT_ID('tempdb..#res') IS NOT NULL DROP TABLE #res;
CREATE TABLE #sps (Nombre SYSNAME PRIMARY KEY, Definicion NVARCHAR(MAX));
CREATE TABLE #maestro (Codigo VARCHAR(50) PRIMARY KEY, Descripcion VARCHAR(200));
CREATE TABLE #res (Base SYSNAME, Resultado NVARCHAR(1000));

DECLARE @sql NVARCHAR(MAX) = N'
INSERT INTO #sps (Nombre, Definicion)
SELECT o.name, m.definition FROM ' + QUOTENAME(@Fuente) + N'.sys.sql_modules m
  JOIN ' + QUOTENAME(@Fuente) + N'.sys.objects o ON o.object_id = m.object_id
 WHERE o.name IN (''pa_SearchNoveltyAccesoryPallet'', ''pa_UpdateAccesoryNovedadCantidad'', ''pa_UpdateNoveltyAllAccesory'');
INSERT INTO #maestro (Codigo, Descripcion)
SELECT m.Codigo, m.Descripcion FROM ' + QUOTENAME(@Fuente) + N'.dbo.Maestro m
  JOIN ' + QUOTENAME(@Fuente) + N'.dbo.TipoMaestro t ON t.Id = m.TipoMaestroId
 WHERE t.Nombre = ''Tipo Novedad Accesorios'' AND m.Activo = 1;';
EXEC sys.sp_executesql @sql;

IF (SELECT COUNT(*) FROM #sps) <> 3 OR NOT EXISTS (SELECT 1 FROM #maestro)
BEGIN
  RAISERROR('La base fuente no tiene los 3 SPs o el maestro Tipo Novedad Accesorios.', 16, 1);
  RETURN;
END

DECLARE @db SYSNAME, @exec NVARCHAR(300), @nombre SYSNAME, @def NVARCHAR(MAX), @falta NVARCHAR(1000), @n INT;
DECLARE @novedad BIT, @usuario BIT, @tipo BIT, @filas INT, @spsFaltan NVARCHAR(400);

DECLARE bases CURSOR LOCAL FAST_FORWARD FOR
  SELECT DISTINCT dbase FROM WmsWdGeneral.dbo.Cliente
   WHERE ISNULL(dbase, '') LIKE 'WmsWd%' AND DB_ID(dbase) IS NOT NULL AND dbase <> @Fuente
     AND (@SoloBase IS NULL OR dbase = @SoloBase)
   ORDER BY dbase;
OPEN bases;
FETCH NEXT FROM bases INTO @db;
WHILE @@FETCH_STATUS = 0
BEGIN
  SET @sql = N'SELECT @nov = CASE WHEN COL_LENGTH(''' + QUOTENAME(@db) + N'.dbo.Accesorio'', ''Novedad'') IS NULL THEN 1 ELSE 0 END,
    @usu = CASE WHEN COL_LENGTH(''' + QUOTENAME(@db) + N'.dbo.Accesorio'', ''UsuarioIdMovimiento'') IS NULL THEN 1 ELSE 0 END,
    @tip = CASE WHEN EXISTS (SELECT 1 FROM ' + QUOTENAME(@db) + N'.dbo.TipoMaestro WHERE Nombre = ''Tipo Novedad Accesorios'') THEN 0 ELSE 1 END,
    @fil = (SELECT COUNT(*) FROM #maestro x WHERE NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@db) + N'.dbo.Maestro m
            JOIN ' + QUOTENAME(@db) + N'.dbo.TipoMaestro t ON t.Id = m.TipoMaestroId
            WHERE t.Nombre = ''Tipo Novedad Accesorios'' AND m.Codigo = x.Codigo)),
    @sps = STUFF((SELECT '', '' + s.Nombre FROM #sps s
            WHERE OBJECT_ID(''' + QUOTENAME(@db) + N'.dbo.'' + s.Nombre, ''P'') IS NULL FOR XML PATH('''')), 1, 2, '''')';
  EXEC sys.sp_executesql @sql, N'@nov BIT OUTPUT, @usu BIT OUTPUT, @tip BIT OUTPUT, @fil INT OUTPUT, @sps NVARCHAR(400) OUTPUT',
       @novedad OUTPUT, @usuario OUTPUT, @tipo OUTPUT, @filas OUTPUT, @spsFaltan OUTPUT;

  SET @falta = CONCAT_WS(', ',
    IIF(@novedad = 1, 'columna Novedad', NULL),
    IIF(@usuario = 1, 'columna UsuarioIdMovimiento', NULL),
    IIF(@spsFaltan IS NOT NULL, @spsFaltan, NULL),
    IIF(@tipo = 1, 'tipo de maestro', NULL),
    IIF(@filas > 0, CONCAT(@filas, ' filas del maestro'), NULL));

  IF @falta = ''
    INSERT INTO #res VALUES (@db, 'completo');
  ELSE IF @Modo = 'LISTAR'
    INSERT INTO #res VALUES (@db, 'falta: ' + @falta);
  ELSE
  BEGIN
    SET @exec = QUOTENAME(@db) + N'.sys.sp_executesql';
    BEGIN TRY
      BEGIN TRANSACTION;
      IF @novedad = 1 EXEC @exec N'ALTER TABLE dbo.Accesorio ADD Novedad VARCHAR(50) NULL;';
      IF @usuario = 1 EXEC @exec N'ALTER TABLE dbo.Accesorio ADD UsuarioIdMovimiento INT NULL;';

      DECLARE sps CURSOR LOCAL FAST_FORWARD FOR SELECT Nombre, Definicion FROM #sps;
      OPEN sps;
      FETCH NEXT FROM sps INTO @nombre, @def;
      WHILE @@FETCH_STATUS = 0
      BEGIN
        SET @sql = N'SELECT @x = CASE WHEN OBJECT_ID(''dbo.' + @nombre + N''', ''P'') IS NULL THEN 1 ELSE 0 END';
        EXEC @exec @sql, N'@x INT OUTPUT', @n OUTPUT;
        IF @n = 1 EXEC @exec @def;
        FETCH NEXT FROM sps INTO @nombre, @def;
      END
      CLOSE sps;
      DEALLOCATE sps;

      SET @sql = N'
DECLARE @tm INT = (SELECT TOP 1 Id FROM dbo.TipoMaestro WHERE Nombre = ''Tipo Novedad Accesorios'');
IF @tm IS NULL
BEGIN
  INSERT INTO dbo.TipoMaestro (Nombre, Descripcion, Activo) VALUES (''Tipo Novedad Accesorios'', ''Tipo Novedad Accesorios'', 1);
  SET @tm = SCOPE_IDENTITY();
END
INSERT INTO dbo.Maestro (Codigo, Descripcion, TipoMaestroId, Activo, Detalle, adicional)
SELECT x.Codigo, x.Descripcion, @tm, 1, '''', 0 FROM #maestro x
 WHERE NOT EXISTS (SELECT 1 FROM dbo.Maestro m WHERE m.TipoMaestroId = @tm AND m.Codigo = x.Codigo);';
      EXEC @exec @sql;

      IF @Modo = 'PROBAR' ROLLBACK TRANSACTION; ELSE COMMIT TRANSACTION;
      INSERT INTO #res VALUES (@db, IIF(@Modo = 'PROBAR', 'compila: ', 'aplicado: ') + @falta);
    END TRY
    BEGIN CATCH
      IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
      IF CURSOR_STATUS('local', 'sps') >= 0 CLOSE sps;
      IF CURSOR_STATUS('local', 'sps') >= -1 DEALLOCATE sps;
      INSERT INTO #res VALUES (@db, 'ERROR: ' + ERROR_MESSAGE());
    END CATCH
  END
  FETCH NEXT FROM bases INTO @db;
END
CLOSE bases;
DEALLOCATE bases;

SELECT Resultado = CASE WHEN Resultado LIKE 'ERROR%' THEN 'ERROR' WHEN Resultado = 'completo' THEN 'completo'
                        ELSE LEFT(Resultado, CHARINDEX(':', Resultado) - 1) END, COUNT(*) AS Bases
  FROM #res GROUP BY CASE WHEN Resultado LIKE 'ERROR%' THEN 'ERROR' WHEN Resultado = 'completo' THEN 'completo'
                          ELSE LEFT(Resultado, CHARINDEX(':', Resultado) - 1) END;
SELECT Base, Resultado FROM #res ORDER BY CASE WHEN Resultado LIKE 'ERROR%' THEN 0 ELSE 1 END, Base;
