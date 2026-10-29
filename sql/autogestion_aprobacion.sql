/* =====================================================================
   AUTOGESTION - APROBACION POR ENLACE (opcion C)
   Ejecutar DESPUES de autogestion_fase1.sql, en la base de CADA cliente.
   Script idempotente (se puede re-ejecutar).

   Flujo:
   1) El usuario solicita un movimiento (no se mueve nada todavia).
   2) Cada aprobador recibe un correo con SU enlace de un solo uso.
   3) Al abrir el enlace ve el detalle y pulsa Autorizar o Rechazar.
   4) Ese clic quema el token (y los de los demas aprobadores) y, si autoriza,
      ejecuta el movimiento en la misma transaccion.

   Los aprobadores son los usuarios activos del cliente, con correo, cuyo perfil
   tenga el TIPO DE PERFIL 'APROBADOR AUTOGESTION'
   (crear en TipoPerfil y asignar al perfil igual que TECNICO COSMETICA).
   ===================================================================== */

-- 1) Tablas ---------------------------------------------------------------
IF OBJECT_ID('dbo.AutogestionSolicitud', 'U') IS NULL
CREATE TABLE dbo.AutogestionSolicitud (
    Id               INT IDENTITY(1,1) PRIMARY KEY,
    Fecha            DATETIME      NOT NULL CONSTRAINT DF_AutogestionSolicitud_Fecha DEFAULT GETDATE(),
    Ambito           VARCHAR(10)   NOT NULL,   -- SERIAL | PALLET
    Accion           VARCHAR(20)   NOT NULL,
    Objetivo         VARCHAR(50)   NOT NULL,   -- serial o numero de pallet
    EstadoDestinoId  INT           NULL,
    CajaNumero       VARCHAR(50)   NULL,
    PalletDestino    VARCHAR(50)   NULL,
    SolicitanteId    INT           NOT NULL,
    Justificacion    VARCHAR(500)  NOT NULL,
    Estado           VARCHAR(12)   NOT NULL CONSTRAINT DF_AutogestionSolicitud_Estado DEFAULT 'PENDIENTE',
                                   -- PENDIENTE | APROBADA | RECHAZADA | VENCIDA | ERROR
    VenceEn          DATETIME      NOT NULL,
    FechaResolucion  DATETIME      NULL,
    ResueltaPor      VARCHAR(150)  NULL,
    Resultado        VARCHAR(500)  NULL
);
GO

IF OBJECT_ID('dbo.AutogestionSolicitudToken', 'U') IS NULL
CREATE TABLE dbo.AutogestionSolicitudToken (
    Id           INT IDENTITY(1,1) PRIMARY KEY,
    SolicitudId  INT          NOT NULL REFERENCES dbo.AutogestionSolicitud(Id),
    AprobadorId  INT          NOT NULL,
    Nombre       VARCHAR(150) NULL,
    Correo       VARCHAR(200) NULL,
    TokenHash    CHAR(64)     NOT NULL,   -- SHA-256 del token (el token real solo viaja en el correo)
    Usado        BIT          NOT NULL CONSTRAINT DF_AutogestionSolicitudToken_Usado DEFAULT 0,
    FechaUso     DATETIME     NULL,
    CONSTRAINT UQ_AutogestionSolicitudToken_Hash UNIQUE (TokenHash)
);
GO

-- 2) Aprobadores del cliente ----------------------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionAprobadores
    @ClienteId INT,
    @ExcluirUsuarioId INT = 0
AS
BEGIN
    SET NOCOUNT ON;
    SELECT DISTINCT u.id, u.nombreUsuario, u.Correo
    FROM WmsWdGeneral.dbo.UsuarioClientePerfil ucp
    INNER JOIN WmsWdGeneral.dbo.UsuarioCliente uc ON ucp.usuarioClienteId = uc.id
    INNER JOIN WmsWdGeneral.dbo.Usuario u ON uc.usuarioId = u.id
    INNER JOIN WmsWdGeneral.dbo.Perfil p ON ucp.perfilId = p.id
    INNER JOIN WmsWdGeneral.dbo.PerfilTipoPerfil ptp ON p.id = ptp.perfilId
    INNER JOIN WmsWdGeneral.dbo.TipoPerfil tp ON ptp.tipoPerfilId = tp.id
    WHERE uc.clienteId = @ClienteId
      AND tp.nombre = 'APROBADOR AUTOGESTION'
      AND u.Activo = 1
      AND u.id <> @ExcluirUsuarioId
      AND ISNULL(u.Correo, '') <> '';
END
GO

-- 3) Crear solicitud (valida en seco: ejecuta la accion y hace ROLLBACK) -----
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudCrear
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

    IF @Ambito NOT IN ('SERIAL', 'PALLET')
        THROW 50031, 'Ambito no valido.', 1;

    IF LEN(LTRIM(RTRIM(ISNULL(@Justificacion, '')))) < 10
        THROW 50010, 'La justificacion es obligatoria (minimo 10 caracteres).', 1;

    IF EXISTS (SELECT 1 FROM AutogestionSolicitud
               WHERE Ambito = @Ambito AND Objetivo = @Objetivo AND Estado = 'PENDIENTE' AND VenceEn > GETDATE())
        THROW 50030, 'Ya existe una solicitud pendiente de aprobacion para este elemento.', 1;

    -- Validacion en seco: se ejecuta la accion real dentro de una transaccion que se revierte,
    -- asi se aplican exactamente las mismas reglas (Produccion, destinos permitidos, etc.).
    BEGIN TRY
        BEGIN TRAN;
        IF @Ambito = 'SERIAL'
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
END
GO

-- 4) Guardar el token (hash) de un aprobador --------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudToken
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
END
GO

-- 5) Detalle de una solicitud a partir del token (NO consume el token) ----------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudPorToken
    @TokenHash CHAR(64)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT s.Id, s.Ambito, s.Accion, s.Objetivo, ed.nombre AS estadoDestino, s.CajaNumero, s.PalletDestino,
           u.nombreUsuario AS solicitante, s.Fecha, s.Justificacion,
           CASE WHEN s.Estado = 'PENDIENTE' AND s.VenceEn <= GETDATE() THEN 'VENCIDA' ELSE s.Estado END AS estado,
           s.VenceEn, t.Nombre AS aprobador,
           CASE WHEN s.Estado = 'PENDIENTE' AND s.VenceEn > GETDATE() AND t.Usado = 0 THEN 1 ELSE 0 END AS vigente
    FROM AutogestionSolicitudToken t
    INNER JOIN AutogestionSolicitud s ON s.Id = t.SolicitudId
    LEFT JOIN WmsWdGeneral.dbo.Estado ed ON ed.id = s.EstadoDestinoId
    LEFT JOIN WmsWdGeneral.dbo.Usuario u ON u.id = s.SolicitanteId
    WHERE t.TokenHash = @TokenHash;
END
GO

-- 6) Resolver: autorizar / rechazar (un solo uso, atomico) ---------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudResolver
    @TokenHash CHAR(64),
    @Decision  VARCHAR(10)      -- APROBAR | RECHAZAR
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF @Decision NOT IN ('APROBAR', 'RECHAZAR')
    BEGIN
        SELECT 'INVALIDA' AS resultado, 'Decision no valida.' AS mensaje;
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
        SELECT 'INVALIDA' AS resultado, 'El enlace no es valido o ya fue utilizado.' AS mensaje;
        RETURN;
    END

    BEGIN TRY
        BEGIN TRAN;

        -- Reclama la solicitud de forma atomica: solo una persona puede resolverla
        UPDATE AutogestionSolicitud
        SET Estado = CASE WHEN @Decision = 'APROBAR' THEN 'APROBADA' ELSE 'RECHAZADA' END,
            FechaResolucion = GETDATE(),
            ResueltaPor = @Aprobador
        WHERE Id = @SolId AND Estado = 'PENDIENTE' AND VenceEn > GETDATE();

        IF @@ROWCOUNT = 0
        BEGIN
            ROLLBACK TRAN;
            UPDATE AutogestionSolicitud SET Estado = 'VENCIDA'
            WHERE Id = @SolId AND Estado = 'PENDIENTE' AND VenceEn <= GETDATE();
            UPDATE AutogestionSolicitudToken SET Usado = 1, FechaUso = GETDATE() WHERE SolicitudId = @SolId AND Usado = 0;
            SELECT 'INVALIDA' AS resultado, 'La solicitud ya fue resuelta o esta vencida.' AS mensaje;
            RETURN;
        END

        -- Quema TODOS los tokens de la solicitud (el enlace queda de un solo uso)
        UPDATE AutogestionSolicitudToken SET Usado = 1, FechaUso = GETDATE() WHERE SolicitudId = @SolId AND Usado = 0;

        IF @Decision = 'APROBAR'
        BEGIN
            DECLARE @JustFinal VARCHAR(500) =
                LEFT(@Justificacion + ' | Solicitud #' + CAST(@SolId AS VARCHAR(12)) + ' aprobada por ' + ISNULL(@Aprobador, '?'), 500);

            IF @Ambito = 'SERIAL'
                EXEC dbo.pa_AutogestionSerial @Serial = @Objetivo, @Accion = @Accion,
                     @EstadoDestinoId = @EstadoDestinoId, @PalletDestino = @PalletDestino,
                     @UsuarioId = @SolicitanteId, @Justificacion = @JustFinal;
            ELSE
                EXEC dbo.pa_AutogestionPallet @Pallet = @Objetivo, @Accion = @Accion,
                     @EstadoDestinoId = @EstadoDestinoId, @CajaNumero = @CajaNumero,
                     @PalletDestino = @PalletDestino, @UsuarioId = @SolicitanteId,
                     @Justificacion = @JustFinal;

            UPDATE AutogestionSolicitud SET Resultado = 'Movimiento ejecutado.' WHERE Id = @SolId;
        END
        ELSE
            UPDATE AutogestionSolicitud SET Resultado = 'Solicitud rechazada.' WHERE Id = @SolId;

        COMMIT TRAN;

        SELECT CASE WHEN @Decision = 'APROBAR' THEN 'APROBADA' ELSE 'RECHAZADA' END AS resultado,
               CASE WHEN @Decision = 'APROBAR' THEN 'Movimiento autorizado y ejecutado correctamente.'
                    ELSE 'Solicitud rechazada. No se realizo ningun movimiento.' END AS mensaje;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRAN;
        DECLARE @Msg VARCHAR(500) = LEFT(ERROR_MESSAGE(), 500);

        -- El token queda consumido igual; la solicitud queda en ERROR con el motivo
        UPDATE AutogestionSolicitud
        SET Estado = 'ERROR', FechaResolucion = GETDATE(), ResueltaPor = @Aprobador, Resultado = @Msg
        WHERE Id = @SolId AND Estado = 'PENDIENTE';
        UPDATE AutogestionSolicitudToken SET Usado = 1, FechaUso = GETDATE() WHERE SolicitudId = @SolId AND Usado = 0;

        SELECT 'ERROR' AS resultado, @Msg AS mensaje;
    END CATCH
END
GO

-- 7) Listado de solicitudes (pestana Solicitudes) -------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudes
    @Limite INT = 100
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP (@Limite)
           s.Id, s.Fecha, s.Ambito, s.Accion, s.Objetivo, ed.nombre AS estadoDestino,
           u.nombreUsuario AS solicitante,
           CASE WHEN s.Estado = 'PENDIENTE' AND s.VenceEn <= GETDATE() THEN 'VENCIDA' ELSE s.Estado END AS estado,
           s.VenceEn, s.ResueltaPor, s.FechaResolucion, s.Resultado, s.Justificacion
    FROM AutogestionSolicitud s
    LEFT JOIN WmsWdGeneral.dbo.Estado ed ON ed.id = s.EstadoDestinoId
    LEFT JOIN WmsWdGeneral.dbo.Usuario u ON u.id = s.SolicitanteId
    ORDER BY s.Fecha DESC, s.Id DESC;
END
GO

-- 8) Cancelar una solicitud que no pudo completarse (la libera para reintentar) ----
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSolicitudCancelar
    @SolicitudId INT,
    @Motivo      VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE AutogestionSolicitud
    SET Estado = 'ERROR', FechaResolucion = GETDATE(), Resultado = LEFT(@Motivo, 500)
    WHERE Id = @SolicitudId AND Estado = 'PENDIENTE';
    UPDATE AutogestionSolicitudToken SET Usado = 1, FechaUso = GETDATE() WHERE SolicitudId = @SolicitudId AND Usado = 0;
END
GO

-- Limpieza unica: solicitudes pendientes que quedaron sin ningun enlace (intento fallido)
UPDATE s
SET Estado = 'ERROR', FechaResolucion = GETDATE(), Resultado = 'Solicitud sin enlaces (intento fallido).'
FROM AutogestionSolicitud s
WHERE s.Estado = 'PENDIENTE'
  AND NOT EXISTS (SELECT 1 FROM AutogestionSolicitudToken t WHERE t.SolicitudId = s.Id);
GO
