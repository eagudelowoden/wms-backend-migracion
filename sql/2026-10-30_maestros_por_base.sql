/* =====================================================================================
   Dejo listos desde ya los maestros y parámetros que usará el backend nuevo, para que al desplegar no falte nada.
   El backend publicado no los lee: crearlos no cambia nada de lo que hoy funciona. No enciendo interruptores ni
   toco SPs (eso va con 2026-10-25_instalar_por_base.sql al desplegar).
     1. En todas las bases, los tipos de maestro SmartCard Ingreso, Limpieza Caracteres, Diagnostico Serial3,
        Responsable Op y Tipo Empaque (vacíos si la base no tiene una regla propia).
     2. Lo que hoy hace el SP de cada cliente:
        - Claro Ecuador: Limpieza Caracteres '+1C', 'Ñ' y ':' en la MAC (su pa_InsertEntry).
        - DirecTV Ecuador: SmartCard Ingreso CARD0001 -> SMARTCARD (su pa_InsertEntry crea la smartcard del Serial3)
          y Tipo Empaque FULL y SERVICE (su Forecast).
        - Global Liberty, Tigo Costa Rica, Tigo Nicaragua y Ventas Panamá: Códigos SAP > Campos calculados con lo que
          calculan su pa_InsertEntry y su pa_InsertDiagnostic (los mismos de la sección 6h del script por base).
   Solo creo lo que falta. Cada base va en una sola transacción.
   @Modo: LISTAR (no cambia nada), PROBAR (hace todo y lo deshace) o APLICAR. @SoloBase: una sola base.
   ===================================================================================== */
SET NOCOUNT ON;
SET LOCK_TIMEOUT 5000;

DECLARE @Modo VARCHAR(10) = 'LISTAR';
DECLARE @SoloBase SYSNAME = NULL;

IF @Modo NOT IN ('LISTAR', 'PROBAR', 'APLICAR')
BEGIN
  RAISERROR('@Modo debe ser LISTAR, PROBAR o APLICAR.', 16, 1);
  RETURN;
END

IF OBJECT_ID('tempdb..#tipos') IS NOT NULL DROP TABLE #tipos;
IF OBJECT_ID('tempdb..#filas') IS NOT NULL DROP TABLE #filas;
IF OBJECT_ID('tempdb..#reglas') IS NOT NULL DROP TABLE #reglas;
IF OBJECT_ID('tempdb..#res') IS NOT NULL DROP TABLE #res;
CREATE TABLE #tipos (Nombre VARCHAR(50) PRIMARY KEY);
CREATE TABLE #filas (Base SYSNAME, Tipo VARCHAR(50), Orden INT, Codigo VARCHAR(50), Descripcion VARCHAR(200), Detalle VARCHAR(100),
                     CodigoSap VARCHAR(50) NULL);
CREATE TABLE #reglas (Base SYSNAME, CodigoSapId INT, Campo VARCHAR(20), Origen VARCHAR(20), Operacion VARCHAR(20),
                      Parametro1 VARCHAR(100), Parametro2 VARCHAR(100), Momento VARCHAR(15));
CREATE TABLE #res (Base SYSNAME, Resultado NVARCHAR(1000));

INSERT INTO #tipos VALUES ('SmartCard Ingreso'), ('Limpieza Caracteres'), ('Diagnostico Serial3'), ('Responsable Op'), ('Tipo Empaque');

INSERT INTO #filas VALUES
  ('WmsWdClaroEcuador', 'Limpieza Caracteres', 1, '+1C', 'Quitar +1C de la MAC', 'MAC', NULL),
  ('WmsWdClaroEcuador', 'Limpieza Caracteres', 2, CHAR(209), 'Quitar ' + CHAR(209) + ' de la MAC', 'MAC', NULL),
  ('WmsWdClaroEcuador', 'Limpieza Caracteres', 3, ':', 'Quitar : de la MAC', 'MAC', NULL),
  ('WmsWdDirectvEcuador', 'SmartCard Ingreso', 1, 'CARD0001', 'CARD', 'SMARTCARD', 'CARD0001'),
  ('WmsWdDirectvEcuador', 'Tipo Empaque', 1, 'FULL', 'FULL', '', NULL),
  ('WmsWdDirectvEcuador', 'Tipo Empaque', 2, 'SERVICE', 'SERVICE', '', NULL);

INSERT INTO #reglas VALUES
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

IF OBJECT_ID('WmsWdGeneral.dbo.CodigoSapRegla', 'U') IS NULL
BEGIN
  RAISERROR('Falta WmsWdGeneral.dbo.CodigoSapRegla (correr 2026-10-29_parametros_wms.sql).', 16, 1);
  RETURN;
END

DECLARE @db SYSNAME, @exec NVARCHAR(300), @sql NVARCHAR(MAX), @falta NVARCHAR(1000);
DECLARE @tiposFaltan INT, @filasFaltan INT, @reglasFaltan INT;

DECLARE bases CURSOR LOCAL FAST_FORWARD FOR
  SELECT DISTINCT dbase FROM WmsWdGeneral.dbo.Cliente
   WHERE ISNULL(dbase, '') LIKE 'WmsWd%' AND DB_ID(dbase) IS NOT NULL AND (@SoloBase IS NULL OR dbase = @SoloBase)
   ORDER BY dbase;
OPEN bases;
FETCH NEXT FROM bases INTO @db;
WHILE @@FETCH_STATUS = 0
BEGIN
  SET @exec = QUOTENAME(@db) + N'.sys.sp_executesql';
  SET @sql = N'
SELECT @t = (SELECT COUNT(*) FROM #tipos x WHERE NOT EXISTS (SELECT 1 FROM dbo.TipoMaestro t WHERE t.Nombre = x.Nombre)),
       @f = (SELECT COUNT(*) FROM #filas x
              WHERE x.Base = DB_NAME()
                AND (x.CodigoSap IS NULL OR EXISTS (SELECT 1 FROM dbo.CodigoSap c WHERE c.Codigo = x.CodigoSap))
                AND NOT EXISTS (SELECT 1 FROM dbo.Maestro m JOIN dbo.TipoMaestro t ON t.Id = m.TipoMaestroId
                                 WHERE t.Nombre = x.Tipo AND m.Codigo = x.Codigo)),
       @r = (SELECT COUNT(*) FROM #reglas x
               JOIN WmsWdGeneral.dbo.Cliente cl ON cl.dbase = x.Base
              WHERE x.Base = DB_NAME()
                AND EXISTS (SELECT 1 FROM dbo.CodigoSap c WHERE c.Id = x.CodigoSapId)
                AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.CodigoSapRegla g
                                 WHERE g.ClienteId = cl.Id AND g.CodigoSapId = x.CodigoSapId AND g.Campo = x.Campo));';
  EXEC @exec @sql, N'@t INT OUTPUT, @f INT OUTPUT, @r INT OUTPUT', @tiposFaltan OUTPUT, @filasFaltan OUTPUT, @reglasFaltan OUTPUT;

  SET @falta = CONCAT_WS(', ',
    IIF(@tiposFaltan > 0, CONCAT(@tiposFaltan, ' tipos de maestro'), NULL),
    IIF(@filasFaltan > 0, CONCAT(@filasFaltan, ' filas de maestro'), NULL),
    IIF(@reglasFaltan > 0, CONCAT(@reglasFaltan, ' campos calculados'), NULL));

  IF @falta = ''
    INSERT INTO #res VALUES (@db, 'completo');
  ELSE IF @Modo = 'LISTAR'
    INSERT INTO #res VALUES (@db, 'falta: ' + @falta);
  ELSE
  BEGIN
    BEGIN TRY
      BEGIN TRANSACTION;
      SET @sql = N'
INSERT INTO dbo.TipoMaestro (Nombre, Descripcion, Activo)
SELECT x.Nombre, x.Nombre, 1 FROM #tipos x WHERE NOT EXISTS (SELECT 1 FROM dbo.TipoMaestro t WHERE t.Nombre = x.Nombre);

INSERT INTO dbo.Maestro (Codigo, Descripcion, TipoMaestroId, Activo, Detalle, adicional)
SELECT x.Codigo, x.Descripcion, t.Id, 1, x.Detalle, 0
  FROM #filas x
  CROSS APPLY (SELECT TOP 1 Id FROM dbo.TipoMaestro WHERE Nombre = x.Tipo ORDER BY Id) t
 WHERE x.Base = DB_NAME()
   AND (x.CodigoSap IS NULL OR EXISTS (SELECT 1 FROM dbo.CodigoSap c WHERE c.Codigo = x.CodigoSap))
   AND NOT EXISTS (SELECT 1 FROM dbo.Maestro m JOIN dbo.TipoMaestro tm ON tm.Id = m.TipoMaestroId
                    WHERE tm.Nombre = x.Tipo AND m.Codigo = x.Codigo)
 ORDER BY x.Tipo, x.Orden;

INSERT INTO WmsWdGeneral.dbo.CodigoSapRegla (ClienteId, CodigoSapId, Campo, Origen, Operacion, Parametro1, Parametro2,
                                             Momento, Activo, Usuario, Fecha)
SELECT cl.Id, x.CodigoSapId, x.Campo, x.Origen, x.Operacion, x.Parametro1, NULLIF(x.Parametro2, ''''), x.Momento, 1,
       ''script 2026-10-30_maestros_por_base'', GETDATE()
  FROM #reglas x
  JOIN WmsWdGeneral.dbo.Cliente cl ON cl.dbase = x.Base
 WHERE x.Base = DB_NAME()
   AND EXISTS (SELECT 1 FROM dbo.CodigoSap c WHERE c.Id = x.CodigoSapId)
   AND NOT EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.CodigoSapRegla g
                    WHERE g.ClienteId = cl.Id AND g.CodigoSapId = x.CodigoSapId AND g.Campo = x.Campo);';
      EXEC @exec @sql;
      IF @Modo = 'PROBAR' ROLLBACK TRANSACTION; ELSE COMMIT TRANSACTION;
      INSERT INTO #res VALUES (@db, IIF(@Modo = 'PROBAR', 'compila: ', 'aplicado: ') + @falta);
    END TRY
    BEGIN CATCH
      IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
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
