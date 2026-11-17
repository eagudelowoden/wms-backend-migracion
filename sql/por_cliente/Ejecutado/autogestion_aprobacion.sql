/* =====================================================================================
   AUTOGESTION - APROBACION POR ENLACE  (recorre TODAS las bases cliente)

   Flujo: el usuario solicita un movimiento (no se mueve nada todavia); cada aprobador recibe un correo con su
   enlace de un solo uso; al abrirlo autoriza o rechaza; ese clic quema los tokens y, si autoriza, ejecuta el
   movimiento en la misma transaccion. Los aprobadores salen de WmsWdGeneral.dbo.AutogestionAprobador
   (2026-11-07_autogestion_aprobadores.sql), ya no de perfiles.

   Crea en cada base, SOLO LO QUE FALTA (no modifica tablas ni SP existentes):
     Tablas: AutogestionSolicitud, AutogestionSolicitudToken
     SP    : pa_AutogestionSolicitudCrear, pa_AutogestionSolicitudToken, pa_AutogestionSolicitudPorToken,
             pa_AutogestionSolicitudResolver, pa_AutogestionSolicitudes, pa_AutogestionSolicitudCancelar
   No incluye pa_AutogestionAprobadores (ya no se usa) ni la limpieza de solicitudes pendientes: no toca datos.

   REQUISITO: autogestion_fase1.sql ya corrido en la base (los SP llaman a pa_AutogestionSerial y
   pa_AutogestionPallet). Si falta en una base, esa base se omite y se avisa.
   Cada base va en una sola transaccion: si algo falla, esa base queda como estaba.

   @Modo      LISTAR (no cambia nada, dice que falta) | PROBAR (lo hace y lo deshace) | APLICAR
   @SoloBase  una sola base (NULL = todas)
   @Excluir   bases separadas por coma que se saltan
   @Reemplazar 1 = vuelve a crear (CREATE OR ALTER) los 6 SP aunque existan; 0 = solo los que faltan
   Se puede ejecutar desde cualquier base: usa nombres de tres partes.
   ===================================================================================== */
SET NOCOUNT ON;

DECLARE @Modo       VARCHAR(10)   = 'LISTAR';
DECLARE @SoloBase   SYSNAME       = NULL;
DECLARE @Excluir    NVARCHAR(MAX) = '';
DECLARE @Reemplazar BIT           = 0;

IF @Modo NOT IN ('LISTAR', 'PROBAR', 'APLICAR')
BEGIN
  RAISERROR('@Modo debe ser LISTAR, PROBAR o APLICAR.', 16, 1);
  RETURN;
END

IF OBJECT_ID('tempdb..#sp') IS NOT NULL DROP TABLE #sp;
IF OBJECT_ID('tempdb..#res') IS NOT NULL DROP TABLE #res;
CREATE TABLE #sp (Orden INT PRIMARY KEY, Nombre SYSNAME, Def NVARCHAR(MAX));
CREATE TABLE #res (Base SYSNAME, Resultado NVARCHAR(1000));

/* Las definiciones se escriben con ` en lugar de comilla simple y se convierten al ejecutarlas. */
DECLARE @tSol NVARCHAR(MAX), @tTok NVARCHAR(MAX), @d NVARCHAR(MAX);

SET @tSol = N'CREATE TABLE dbo.AutogestionSolicitud (
    Id               INT IDENTITY(1,1) PRIMARY KEY,
    Fecha            DATETIME      NOT NULL CONSTRAINT DF_AutogestionSolicitud_Fecha DEFAULT GETDATE(),
    Ambito           VARCHAR(10)   NOT NULL,
    Accion           VARCHAR(20)   NOT NULL,
    Objetivo         VARCHAR(50)   NOT NULL,
    EstadoDestinoId  INT           NULL,
    CajaNumero       VARCHAR(50)   NULL,
    PalletDestino    VARCHAR(50)   NULL,
    SolicitanteId    INT           NOT NULL,
    Justificacion    VARCHAR(500)  NOT NULL,
    Estado           VARCHAR(12)   NOT NULL CONSTRAINT DF_AutogestionSolicitud_Estado DEFAULT `PENDIENTE`,
    VenceEn          DATETIME      NOT NULL,
    FechaResolucion  DATETIME      NULL,
    ResueltaPor      VARCHAR(150)  NULL,
    Resultado        VARCHAR(500)  NULL
)';

SET @tTok = N'CREATE TABLE dbo.AutogestionSolicitudToken (
    Id           INT IDENTITY(1,1) PRIMARY KEY,
    SolicitudId  INT          NOT NULL REFERENCES dbo.AutogestionSolicitud(Id),
    AprobadorId  INT          NOT NULL,
    Nombre       VARCHAR(150) NULL,
    Correo       VARCHAR(200) NULL,
    TokenHash    CHAR(64)     NOT NULL,
    Usado        BIT          NOT NULL CONSTRAINT DF_AutogestionSolicitudToken_Usado DEFAULT 0,
    FechaUso     DATETIME     NULL,
    CONSTRAINT UQ_AutogestionSolicitudToken_Hash UNIQUE (TokenHash)
)';

-- 1) Crear solicitud (valida en seco: ejecuta la accion y hace ROLLBACK) ---------------------------
SET @d = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudCrear
    @Ambito          VARCHAR(10),
    @Accion          VARCHAR(20),
    @Objetivo        VARCHAR(50),
    @EstadoDestinoId INT = NULL,
    @CajaNumero      VARCHAR(50) = NULL,
    @PalletDestino   VARCHAR(50) = NULL,
    @SolicitanteId   INT,
    @Justificacion   VARCHAR(500),
    @Horas           INT = 48
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF @Ambito NOT IN (`SERIAL`, `PALLET`)
        THROW 50031, `Ambito no valido.`, 1;

    IF LEN(LTRIM(RTRIM(ISNULL(@Justificacion, ``)))) < 10
        THROW 50010, `La justificacion es obligatoria (minimo 10 caracteres).`, 1;

    IF EXISTS (SELECT 1 FROM AutogestionSolicitud
               WHERE Ambito = @Ambito AND Objetivo = @Objetivo AND Estado = `PENDIENTE` AND VenceEn > GETDATE())
        THROW 50030, `Ya existe una solicitud pendiente de aprobacion para este elemento.`, 1;

    BEGIN TRY
        BEGIN TRAN;
        IF @Ambito = `SERIAL`
            EXEC dbo.pa_AutogestionSerial @Serial = @Objetivo, @Accion = @Accion,
                 @EstadoDestinoId = @EstadoDestinoId, @PalletDestino = @PalletDestino,
                 @UsuarioId = @SolicitanteId, @Justificacion = @Justificacion;
        ELSE
            EXEC dbo.pa_AutogestionPallet @Pallet = @Objetivo, @Accion = @Accion,
                 @EstadoDestinoId = @EstadoDestinoId, @CajaNumero = @CajaNumero,
                 @PalletDestino = @PalletDestino, @UsuarioId = @SolicitanteId,
                 @Justificacion = @Justificacion;
        ROLLBACK TRAN;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRAN;
        THROW;
    END CATCH

    INSERT INTO AutogestionSolicitud (Ambito, Accion, Objetivo, EstadoDestinoId, CajaNumero, PalletDestino,
                                      SolicitanteId, Justificacion, VenceEn)
    VALUES (@Ambito, @Accion, @Objetivo, @EstadoDestinoId, @CajaNumero, @PalletDestino,
            @SolicitanteId, LTRIM(RTRIM(@Justificacion)), DATEADD(HOUR, @Horas, GETDATE()));

    SELECT CAST(SCOPE_IDENTITY() AS INT);
END';
INSERT INTO #sp VALUES (1, 'pa_AutogestionSolicitudCrear', @d);

-- 2) Guardar el token (hash) de un aprobador ---------------------------------------------------------
SET @d = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudToken
    @SolicitudId INT,
    @AprobadorId INT,
    @Nombre      VARCHAR(150),
    @Correo      VARCHAR(200),
    @TokenHash   CHAR(64)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO AutogestionSolicitudToken (SolicitudId, AprobadorId, Nombre, Correo, TokenHash)
    VALUES (@SolicitudId, @AprobadorId, @Nombre, @Correo, @TokenHash);
END';
INSERT INTO #sp VALUES (2, 'pa_AutogestionSolicitudToken', @d);

-- 3) Detalle de una solicitud a partir del token (NO consume el token) -------------------------------
SET @d = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudPorToken
    @TokenHash CHAR(64)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT s.Id, s.Ambito, s.Accion, s.Objetivo, ed.nombre AS estadoDestino, s.CajaNumero, s.PalletDestino,
           u.nombreUsuario AS solicitante, s.Fecha, s.Justificacion,
           CASE WHEN s.Estado = `PENDIENTE` AND s.VenceEn <= GETDATE() THEN `VENCIDA` ELSE s.Estado END AS estado,
           s.VenceEn, t.Nombre AS aprobador,
           CASE WHEN s.Estado = `PENDIENTE` AND s.VenceEn > GETDATE() AND t.Usado = 0 THEN 1 ELSE 0 END AS vigente
    FROM AutogestionSolicitudToken t
    INNER JOIN AutogestionSolicitud s ON s.Id = t.SolicitudId
    LEFT JOIN WmsWdGeneral.dbo.Estado ed ON ed.id = s.EstadoDestinoId
    LEFT JOIN WmsWdGeneral.dbo.Usuario u ON u.id = s.SolicitanteId
    WHERE t.TokenHash = @TokenHash;
END';
INSERT INTO #sp VALUES (3, 'pa_AutogestionSolicitudPorToken', @d);

-- 4) Resolver: autorizar / rechazar (un solo uso, atomico) ------------------------------------------
SET @d = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudResolver
    @TokenHash CHAR(64),
    @Decision  VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF @Decision NOT IN (`APROBAR`, `RECHAZAR`)
    BEGIN
        SELECT `INVALIDA` AS resultado, `Decision no valida.` AS mensaje;
        RETURN;
    END

    DECLARE @SolId INT, @Ambito VARCHAR(10), @Accion VARCHAR(20), @Objetivo VARCHAR(50),
            @EstadoDestinoId INT, @CajaNumero VARCHAR(50), @PalletDestino VARCHAR(50),
            @SolicitanteId INT, @Justificacion VARCHAR(500), @Aprobador VARCHAR(150);

    SELECT @SolId = s.Id, @Ambito = s.Ambito, @Accion = s.Accion, @Objetivo = s.Objetivo,
           @EstadoDestinoId = s.EstadoDestinoId, @CajaNumero = s.CajaNumero, @PalletDestino = s.PalletDestino,
           @SolicitanteId = s.SolicitanteId, @Justificacion = s.Justificacion, @Aprobador = t.Nombre
    FROM AutogestionSolicitudToken t
    INNER JOIN AutogestionSolicitud s ON s.Id = t.SolicitudId
    WHERE t.TokenHash = @TokenHash AND t.Usado = 0;

    IF @SolId IS NULL
    BEGIN
        SELECT `INVALIDA` AS resultado, `El enlace no es valido o ya fue utilizado.` AS mensaje;
        RETURN;
    END

    BEGIN TRY
        BEGIN TRAN;

        UPDATE AutogestionSolicitud
        SET Estado = CASE WHEN @Decision = `APROBAR` THEN `APROBADA` ELSE `RECHAZADA` END,
            FechaResolucion = GETDATE(),
            ResueltaPor = @Aprobador
        WHERE Id = @SolId AND Estado = `PENDIENTE` AND VenceEn > GETDATE();

        IF @@ROWCOUNT = 0
        BEGIN
            ROLLBACK TRAN;
            UPDATE AutogestionSolicitud SET Estado = `VENCIDA`
            WHERE Id = @SolId AND Estado = `PENDIENTE` AND VenceEn <= GETDATE();
            UPDATE AutogestionSolicitudToken SET Usado = 1, FechaUso = GETDATE() WHERE SolicitudId = @SolId AND Usado = 0;
            SELECT `INVALIDA` AS resultado, `La solicitud ya fue resuelta o esta vencida.` AS mensaje;
            RETURN;
        END
';
SET @d += N'
        UPDATE AutogestionSolicitudToken SET Usado = 1, FechaUso = GETDATE() WHERE SolicitudId = @SolId AND Usado = 0;

        IF @Decision = `APROBAR`
        BEGIN
            DECLARE @JustFinal VARCHAR(500) =
                LEFT(@Justificacion + ` | Solicitud #` + CAST(@SolId AS VARCHAR(12)) + ` aprobada por ` + ISNULL(@Aprobador, `?`), 500);

            IF @Ambito = `SERIAL`
                EXEC dbo.pa_AutogestionSerial @Serial = @Objetivo, @Accion = @Accion,
                     @EstadoDestinoId = @EstadoDestinoId, @PalletDestino = @PalletDestino,
                     @UsuarioId = @SolicitanteId, @Justificacion = @JustFinal;
            ELSE
                EXEC dbo.pa_AutogestionPallet @Pallet = @Objetivo, @Accion = @Accion,
                     @EstadoDestinoId = @EstadoDestinoId, @CajaNumero = @CajaNumero,
                     @PalletDestino = @PalletDestino, @UsuarioId = @SolicitanteId,
                     @Justificacion = @JustFinal;

            UPDATE AutogestionSolicitud SET Resultado = `Movimiento ejecutado.` WHERE Id = @SolId;
        END
        ELSE
            UPDATE AutogestionSolicitud SET Resultado = `Solicitud rechazada.` WHERE Id = @SolId;

        COMMIT TRAN;

        SELECT CASE WHEN @Decision = `APROBAR` THEN `APROBADA` ELSE `RECHAZADA` END AS resultado,
               CASE WHEN @Decision = `APROBAR` THEN `Movimiento autorizado y ejecutado correctamente.`
                    ELSE `Solicitud rechazada. No se realizo ningun movimiento.` END AS mensaje;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRAN;
        DECLARE @Msg VARCHAR(500) = LEFT(ERROR_MESSAGE(), 500);

        UPDATE AutogestionSolicitud
        SET Estado = `ERROR`, FechaResolucion = GETDATE(), ResueltaPor = @Aprobador, Resultado = @Msg
        WHERE Id = @SolId AND Estado = `PENDIENTE`;
        UPDATE AutogestionSolicitudToken SET Usado = 1, FechaUso = GETDATE() WHERE SolicitudId = @SolId AND Usado = 0;

        SELECT `ERROR` AS resultado, @Msg AS mensaje;
    END CATCH
END';
INSERT INTO #sp VALUES (4, 'pa_AutogestionSolicitudResolver', @d);

-- 5) Listado de solicitudes (pestana Solicitudes) ---------------------------------------------------
SET @d = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudes
    @Limite INT = 100
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP (@Limite)
           s.Id, s.Fecha, s.Ambito, s.Accion, s.Objetivo, ed.nombre AS estadoDestino,
           u.nombreUsuario AS solicitante,
           CASE WHEN s.Estado = `PENDIENTE` AND s.VenceEn <= GETDATE() THEN `VENCIDA` ELSE s.Estado END AS estado,
           s.VenceEn, s.ResueltaPor, s.FechaResolucion, s.Resultado, s.Justificacion
    FROM AutogestionSolicitud s
    LEFT JOIN WmsWdGeneral.dbo.Estado ed ON ed.id = s.EstadoDestinoId
    LEFT JOIN WmsWdGeneral.dbo.Usuario u ON u.id = s.SolicitanteId
    ORDER BY s.Fecha DESC, s.Id DESC;
END';
INSERT INTO #sp VALUES (5, 'pa_AutogestionSolicitudes', @d);

-- 6) Cancelar una solicitud que no pudo completarse (la libera para reintentar) ----------------------
SET @d = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudCancelar
    @SolicitudId INT,
    @Motivo      VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE AutogestionSolicitud
    SET Estado = `ERROR`, FechaResolucion = GETDATE(), Resultado = LEFT(@Motivo, 500)
    WHERE Id = @SolicitudId AND Estado = `PENDIENTE`;
    UPDATE AutogestionSolicitudToken SET Usado = 1, FechaUso = GETDATE() WHERE SolicitudId = @SolicitudId AND Usado = 0;
END';
INSERT INTO #sp VALUES (6, 'pa_AutogestionSolicitudCancelar', @d);

-- Recorrido por base ------------------------------------------------------------------------------------
DECLARE @db SYSNAME, @exec NVARCHAR(300), @def NVARCHAR(MAX), @nom SYSNAME, @crudo NVARCHAR(MAX), @i INT;
DECLARE @fase1 BIT, @hayA BIT, @hayB BIT, @faltan NVARCHAR(1000), @falta NVARCHAR(1500);

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
    SET @fase1 = CASE WHEN OBJECT_ID(QUOTENAME(@db) + N'.dbo.pa_AutogestionSerial', 'P') IS NOT NULL
                       AND OBJECT_ID(QUOTENAME(@db) + N'.dbo.pa_AutogestionPallet', 'P') IS NOT NULL THEN 1 ELSE 0 END;
    SET @hayA  = CASE WHEN OBJECT_ID(QUOTENAME(@db) + N'.dbo.AutogestionSolicitud', 'U') IS NULL THEN 0 ELSE 1 END;
    SET @hayB  = CASE WHEN OBJECT_ID(QUOTENAME(@db) + N'.dbo.AutogestionSolicitudToken', 'U') IS NULL THEN 0 ELSE 1 END;
    SET @faltan = STUFF((SELECT ', ' + Nombre FROM #sp
                          WHERE OBJECT_ID(QUOTENAME(@db) + N'.dbo.' + Nombre, 'P') IS NULL
                          ORDER BY Orden FOR XML PATH('')), 1, 2, '');
    SET @falta = CONCAT_WS(', ',
      IIF(@hayA = 0, 'tabla AutogestionSolicitud', NULL),
      IIF(@hayB = 0, 'tabla AutogestionSolicitudToken', NULL),
      @faltan);

    IF @fase1 = 0
      INSERT INTO #res VALUES (@db, 'FALTA correr autogestion_fase1.sql: no se aplica');
    ELSE IF ISNULL(@falta, '') = '' AND @Reemplazar = 0
      INSERT INTO #res VALUES (@db, 'completo');
    ELSE IF @Modo = 'LISTAR'
      INSERT INTO #res VALUES (@db, 'falta: ' + ISNULL(NULLIF(@falta, ''), '(solo reemplazo de SP)'));
    ELSE
    BEGIN
      SET @exec = QUOTENAME(@db) + N'.sys.sp_executesql';
      BEGIN TRY
        BEGIN TRAN;

        IF @hayA = 0
        BEGIN
          SET @def = REPLACE(@tSol, '`', '''');
          EXEC @exec @def;
        END
        IF @hayB = 0
        BEGIN
          SET @def = REPLACE(@tTok, '`', '''');
          EXEC @exec @def;
        END

        SET @i = 1;
        WHILE @i <= 6
        BEGIN
          SELECT @nom = Nombre, @crudo = Def FROM #sp WHERE Orden = @i;
          IF @Reemplazar = 1 OR OBJECT_ID(QUOTENAME(@db) + N'.dbo.' + @nom, 'P') IS NULL
          BEGIN
            SET @def = REPLACE(@crudo, '`', '''');
            EXEC @exec @def;
          END
          SET @i += 1;
        END

        IF @Modo = 'PROBAR'
        BEGIN
          ROLLBACK TRAN;
          INSERT INTO #res VALUES (@db, 'probado OK (revertido): ' + ISNULL(NULLIF(@falta, ''), 'reemplazo de SP'));
        END
        ELSE
        BEGIN
          COMMIT TRAN;
          INSERT INTO #res VALUES (@db, 'aplicado: ' + ISNULL(NULLIF(@falta, ''), 'reemplazo de SP'));
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

SELECT Base, Resultado FROM #res ORDER BY Base;
