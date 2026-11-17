/* =====================================================================================
   Menu de Autogestion (fila PAUT en la tabla Menu) en CADA base cliente.
   Copia la fila de Cosmetica (PCOS, hija de PROD) con descripcion "Autogestion" y la deja al final de PROD.
   Solo inserta si PAUT no existe y PCOS si existe (si no hay PCOS no hay de donde copiar). No modifica nada mas.

   OJO: el menu solo muestra PAUT a los perfiles con el modulo 46 (Autogestion) cuando el backend tiene el
   mapeo 46 -> PAUT (backend nuevo). Con el backend viejo, PAUT se mostraria a TODOS los perfiles y abriria la
   pantalla de Cosmetica. Corre APLICAR en las bases cuyo backend ya es el nuevo.

   @Modo      LISTAR (no cambia nada) | PROBAR (inserta y deshace) | APLICAR
   @SoloBase  una sola base (NULL = todas)
   @Excluir   bases separadas por coma que se saltan
   Se puede ejecutar desde cualquier base: usa nombres de tres partes. Cada base va en su propia transaccion.
   ===================================================================================== */
SET NOCOUNT ON;

DECLARE @Modo     VARCHAR(10)   = 'APLICAR';
DECLARE @SoloBase SYSNAME       = NULL;
DECLARE @Excluir  NVARCHAR(MAX) = '';

IF @Modo NOT IN ('LISTAR', 'PROBAR', 'APLICAR')
BEGIN
  RAISERROR('@Modo debe ser LISTAR, PROBAR o APLICAR.', 16, 1);
  RETURN;
END

IF OBJECT_ID('tempdb..#res') IS NOT NULL DROP TABLE #res;
CREATE TABLE #res (Base SYSNAME, Resultado NVARCHAR(1000));

DECLARE @db SYSNAME, @exec NVARCHAR(300), @sql NVARCHAR(MAX);
DECLARE @hayPaut BIT, @hayPcos BIT;

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
  ELSE IF OBJECT_ID(QUOTENAME(@db) + N'.dbo.Menu', 'U') IS NULL
  BEGIN
    INSERT INTO #res VALUES (@db, 'sin tabla Menu: no aplica');
  END
  ELSE
  BEGIN
    SET @sql = N'SELECT @p = CASE WHEN EXISTS (SELECT 1 FROM ' + QUOTENAME(@db) + N'.dbo.Menu WHERE id = ''PAUT'') THEN 1 ELSE 0 END,
                        @c = CASE WHEN EXISTS (SELECT 1 FROM ' + QUOTENAME(@db) + N'.dbo.Menu WHERE id = ''PCOS'') THEN 1 ELSE 0 END';
    EXEC sys.sp_executesql @sql, N'@p BIT OUTPUT, @c BIT OUTPUT', @hayPaut OUTPUT, @hayPcos OUTPUT;

    IF @hayPaut = 1
      INSERT INTO #res VALUES (@db, 'completo');
    ELSE IF @hayPcos = 0
      INSERT INTO #res VALUES (@db, 'FALTA la fila PCOS (origen de la copia): no se puede crear PAUT');
    ELSE IF @Modo = 'LISTAR'
      INSERT INTO #res VALUES (@db, 'falta la fila PAUT en Menu');
    ELSE
    BEGIN
      SET @exec = QUOTENAME(@db) + N'.sys.sp_executesql';
      SET @sql = N'INSERT INTO dbo.Menu (id, descripcion, tipo, orden, accion, estado, icono, id_padre, view_name, controller_name)
SELECT ''PAUT'', N''Autogesti'' + NCHAR(243) + N''n'', tipo,
       ISNULL((SELECT MAX(orden) FROM dbo.Menu WHERE id_padre = ''PROD''), 0) + 1,
       accion, estado, icono, id_padre, view_name, controller_name
FROM dbo.Menu
WHERE id = ''PCOS''
  AND NOT EXISTS (SELECT 1 FROM dbo.Menu WHERE id = ''PAUT'');';
      BEGIN TRY
        BEGIN TRAN;
        EXEC @exec @sql;
        IF @Modo = 'PROBAR'
        BEGIN
          ROLLBACK TRAN;
          INSERT INTO #res VALUES (@db, 'probado OK (revertido): se crearia PAUT');
        END
        ELSE
        BEGIN
          COMMIT TRAN;
          INSERT INTO #res VALUES (@db, 'aplicado: fila PAUT creada');
        END
      END TRY
      BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRAN;
        INSERT INTO #res VALUES (@db, 'ERROR (base sin cambios): ' + LEFT(ERROR_MESSAGE(), 800));
      END CATCH
    END
  END
  FETCH NEXT FROM bases INTO @db;
END
CLOSE bases;
DEALLOCATE bases;

-- Resumen: cuantas bases hay por resultado
SELECT CASE @Modo WHEN 'LISTAR'  THEN 'LISTAR: NO se cambio nada'
                  WHEN 'PROBAR'  THEN 'PROBAR: se inserto y se revirtio'
                  ELSE                'APLICAR: cambios guardados' END AS Modo,
       LEFT(Resultado, CHARINDEX(':', Resultado + ':') - 1) AS Resultado, COUNT(*) AS Bases
  FROM #res GROUP BY LEFT(Resultado, CHARINDEX(':', Resultado + ':') - 1) ORDER BY COUNT(*) DESC;
-- Detalle por base (los errores primero)
SELECT Base, Resultado FROM #res ORDER BY CASE WHEN Resultado LIKE 'ERROR%' THEN 0 ELSE 1 END, Base;
