/* =====================================================================================
   Cosmética: agrega la columna UsuarioAsignadoId (técnico cosmética) a dbo.Cosmetica en CADA base cliente.
   SOLO la columna: INT NULL, no reescribe la tabla y el código publicado no la usa. No toca ningún SP.
   Recorre WmsWdGeneral.dbo.Cliente (dbase LIKE 'WmsWd%') y la agrega donde falta.

   @Modo: LISTAR (no cambia nada, solo dice qué base necesita la columna) o APLICAR.
   @SoloBase: una sola base (NULL = todas).
   @Excluir: bases separadas por coma que se saltan (por defecto la que está llena).
   Ejecutar en cualquier base: usa nombres de tres partes.
   ===================================================================================== */
SET NOCOUNT ON;

DECLARE @Modo     VARCHAR(10)    = 'LISTAR';
DECLARE @SoloBase SYSNAME        = NULL;
DECLARE @Excluir  NVARCHAR(MAX)  = 'WmsWdTelefonicaColombiaBOG';

IF @Modo NOT IN ('LISTAR', 'APLICAR')
BEGIN
  RAISERROR('@Modo debe ser LISTAR o APLICAR.', 16, 1);
  RETURN;
END

IF OBJECT_ID('tempdb..#res') IS NOT NULL DROP TABLE #res;
CREATE TABLE #res (Base SYSNAME, Resultado NVARCHAR(1000));

DECLARE @db SYSNAME, @sql NVARCHAR(MAX), @hayTabla BIT, @hayColumna BIT;

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
    INSERT INTO #res VALUES (@db, 'EXCLUIDA (@Excluir)');
  END
  ELSE
  BEGIN
    SET @sql = N'SELECT @t = CASE WHEN OBJECT_ID(''' + QUOTENAME(@db) + N'.dbo.Cosmetica'', ''U'') IS NULL THEN 0 ELSE 1 END,
                        @c = CASE WHEN COL_LENGTH(''' + QUOTENAME(@db) + N'.dbo.Cosmetica'', ''UsuarioAsignadoId'') IS NULL THEN 0 ELSE 1 END';
    EXEC sys.sp_executesql @sql, N'@t BIT OUTPUT, @c BIT OUTPUT', @hayTabla OUTPUT, @hayColumna OUTPUT;

    IF @hayTabla = 0
      INSERT INTO #res VALUES (@db, 'sin tabla Cosmetica: no aplica');
    ELSE IF @hayColumna = 1
      INSERT INTO #res VALUES (@db, 'completo');
    ELSE IF @Modo = 'LISTAR'
      INSERT INTO #res VALUES (@db, 'falta la columna UsuarioAsignadoId');
    ELSE
    BEGIN
      BEGIN TRY
        SET @sql = N'ALTER TABLE ' + QUOTENAME(@db) + N'.dbo.Cosmetica ADD UsuarioAsignadoId INT NULL;';
        EXEC sys.sp_executesql @sql;
        INSERT INTO #res VALUES (@db, 'columna agregada');
      END TRY
      BEGIN CATCH
        INSERT INTO #res VALUES (@db, 'ERROR: ' + LEFT(ERROR_MESSAGE(), 900));
      END CATCH
    END
  END
  FETCH NEXT FROM bases INTO @db;
END
CLOSE bases;
DEALLOCATE bases;

SELECT Base, Resultado FROM #res ORDER BY Base;
