/* =====================================================================================
   Legacy Salvador: pa_InsertEntry sin el bloqueo "Serial pertenece a una PQR Tigo".
   - Quita la consulta a PQRS (id_stage = 2) y el RAISERROR que impedía ingresar el serial salvo tipología 620.
   - El CATCH devuelve los códigos de siempre (1001 serial repetido, 1002 MAC repetida, 1003 otro error con su
     mensaje). Antes, cualquier otro error también salía como "Serial pertenece a una PQR Tigo".
   - Se deja igual el insert (con CajaEmpaqueId) y el vínculo automático con App_PQRS_Tickets.
   - Antes de cambiarlo guarda la versión actual en WMS_Local_TEMP.dbo.Bk_SP_Definiciones (para volver atrás:
     tomar la Definicion de esa tabla y ejecutarla cambiando CREATE por ALTER).
   ===================================================================================== */
USE WmsWdLegacySalvador;
SET NOCOUNT ON;
GO

IF OBJECT_ID('WMS_Local_TEMP.dbo.Bk_SP_Definiciones', 'U') IS NULL
  CREATE TABLE WMS_Local_TEMP.dbo.Bk_SP_Definiciones (
    Id         INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Bk_SP_Definiciones PRIMARY KEY,
    Base       SYSNAME        NOT NULL,
    Nombre     SYSNAME        NOT NULL,
    Definicion NVARCHAR(MAX)  NULL,
    Motivo     NVARCHAR(200)  NULL,
    Fecha      DATETIME       NOT NULL CONSTRAINT DF_Bk_SP_Definiciones_Fecha DEFAULT (GETDATE())
  );

INSERT INTO WMS_Local_TEMP.dbo.Bk_SP_Definiciones (Base, Nombre, Definicion, Motivo)
VALUES (DB_NAME(), 'pa_InsertEntry', OBJECT_DEFINITION(OBJECT_ID('dbo.pa_InsertEntry')),
        'Antes de quitar el bloqueo PQR Tigo (2026-10-29)');
GO

ALTER PROCEDURE [dbo].[pa_InsertEntry]
@Serial varchar(50),
@Mac varchar(50),
@Serial3 varchar(50),
@Serial4 varchar(50),
@Serial5 varchar(50),
@CodigoSapId int,
@PalletId int,
@EstadoId int,
@TipoOrigenId int,
@OrigenId int,
@TipologiaId int,
@NivelId int,
@Tramite varchar(30),
@Documento varchar(30),
@Guia varchar(30),
@Caja int,
@Falla varchar(100),
@TecnicoCliente varchar(50),
@PrealertaId int,
@Cruce int,
@Novedad varchar(50),
@GarantiaFabricante int,
@UsuarioId int,
@Observaciones varchar(50),
@EstadoCliente varchar(20),
@LoteId int,
@CajaIngresoId int,
@ModeloId INT,
@codigoError INT = 0 OUTPUT,
@mensajeError NVARCHAR(4000) = N'' OUTPUT
AS
BEGIN
    BEGIN TRY
        BEGIN TRANSACTION
            INSERT INTO Ingreso (
                serial, mac, serial3, serial4, serial5, codigoSapId, palletId, palletIdIngreso,
                estadoId, tipoOrigenId, origenId, tipologiaId, nivelId, tramite, documento, guia, caja, falla,
                tecnicoCliente, prealertaId, cruce, novedad, garantiaFabricante, usuarioId, fecha, usuarioIdMovimiento,
                fechaMovimiento, observaciones, estadoCliente, loteId, cajaIngresoId, modeloId, CajaEmpaqueId
            )
            VALUES (
                UPPER(@Serial), UPPER(@Mac), UPPER(@Serial3), UPPER(@Serial4), UPPER(@Serial5), @CodigoSapId, @PalletId, @PalletId,
                @EstadoId, @TipoOrigenId, @OrigenId, @TipologiaId, @NivelId, @Tramite, @Documento, @Guia, @Caja, @Falla,
                @TecnicoCliente, @PrealertaId, @Cruce, @Novedad, @GarantiaFabricante, @UsuarioId, GETDATE(), @UsuarioId,
                GETDATE(), @Observaciones, @EstadoCliente, @LoteId, @CajaIngresoId, @ModeloId, @CajaIngresoId
            )

            BEGIN TRY
                DECLARE @IngresoId INT = SCOPE_IDENTITY();
                DECLARE @TicketId INT = NULL;
                DECLARE @EtapaAnterior INT = NULL;

                SELECT TOP 1
                    @TicketId = id,
                    @EtapaAnterior = id_etapa
                FROM App_PQRS_Tickets
                WHERE UPPER(mac) = UPPER(@Serial)
                  AND id_etapa IN (1, 2)
                  AND (id_ingreso IS NULL OR id_ingreso = 0)
                ORDER BY id DESC;

                IF @TicketId IS NOT NULL
                BEGIN
                    UPDATE App_PQRS_Tickets
                    SET id_ingreso  = @IngresoId,
                        id_etapa    = 3,
                        updated_at  = DATEADD(HOUR, -5, GETDATE())
                    WHERE id = @TicketId;

                    INSERT INTO App_PQRS_StatusLog
                        (id_ticket, id_etapa_anterior, id_etapa_nueva, id_usuario, comentario, auto_change)
                    VALUES
                        (@TicketId, @EtapaAnterior, 3, @UsuarioId,
                         'Ingreso WMS detectado - Serial: ' + UPPER(@Serial), 1);
                END
            END TRY
            BEGIN CATCH
            END CATCH

        COMMIT TRANSACTION
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        SET @mensajeError = ERROR_MESSAGE();
        IF @mensajeError LIKE '%UNIQUE%' AND @mensajeError LIKE '%Serial%'
            SET @codigoError = 1001
        ELSE IF @mensajeError LIKE '%UNIQUE%' AND @mensajeError LIKE '%MAC%'
            SET @codigoError = 1002
        ELSE
            SET @codigoError = 1003
        RETURN;
    END CATCH
END
GO
