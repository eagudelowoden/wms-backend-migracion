/* =====================================================================
   AUTOGESTION (seriales + pallets + cajas)  -  script idempotente
   Ejecutar en la base de datos de CADA cliente. Se puede re-ejecutar.

   REGLA: solo se puede modificar lo que esta en un modulo de PRODUCCION.
   Si el serial/pallet esta en LOGISTICA (u otra seccion) solo se consulta.
   La seccion de un estado se obtiene de: Estado.moduloId -> Modulo.seccionId
   -> Seccion.nombre (en WmsWdGeneral). Produccion = nombre que empieza por 'Prod'.

   Supuestos a verificar: Ingreso, Pallet, CajaEmpaque, CodigoSap, Movimiento
   en dbo; Pallet.destinoId = estado donde esta el pallet.
   ===================================================================== */

-- 1) Tabla de logs -------------------------------------------------------
IF OBJECT_ID('dbo.AutogestionLog', 'U') IS NULL
CREATE TABLE dbo.AutogestionLog (
    Id               INT IDENTITY(1,1) PRIMARY KEY,
    Fecha            DATETIME      NOT NULL CONSTRAINT DF_AutogestionLog_Fecha DEFAULT GETDATE(),
    UsuarioId        INT           NOT NULL,
    Accion           VARCHAR(30)   NOT NULL,
    SerialId         INT           NULL,
    Serial           VARCHAR(50)   NULL,
    EstadoOrigenId   INT           NULL,
    EstadoDestinoId  INT           NULL,
    PalletOrigenId   INT           NULL,
    PalletDestinoId  INT           NULL,
    CajaOrigenId     INT           NULL,
    CajaDestinoId    INT           NULL,
    Cantidad         INT           NULL,
    Justificacion    VARCHAR(500)  NOT NULL
);
GO
-- Si la tabla ya existia de la version anterior
IF COL_LENGTH('dbo.AutogestionLog', 'Cantidad') IS NULL
    ALTER TABLE dbo.AutogestionLog ADD Cantidad INT NULL;
GO
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.AutogestionLog') AND name = 'Accion' AND max_length < 30)
    ALTER TABLE dbo.AutogestionLog ALTER COLUMN Accion VARCHAR(30) NOT NULL;
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_AutogestionLog_Serial')
    CREATE INDEX IX_AutogestionLog_Serial ON dbo.AutogestionLog (Serial, Fecha DESC);
GO

-- 2) Funciones de seccion -------------------------------------------------
CREATE OR ALTER FUNCTION dbo.fn_AutogestionSeccion (@EstadoId INT)
RETURNS VARCHAR(100)
AS
BEGIN
    RETURN (SELECT TOP 1 s.nombre
            FROM WmsWdGeneral.dbo.Estado e
            INNER JOIN WmsWdGeneral.dbo.Modulo m ON m.id = e.moduloId
            INNER JOIN WmsWdGeneral.dbo.Seccion s ON s.id = m.seccionId
            WHERE e.id = @EstadoId);
END
GO

CREATE OR ALTER FUNCTION dbo.fn_AutogestionEsProduccion (@EstadoId INT)
RETURNS INT
AS
BEGIN
    RETURN CASE WHEN ISNULL(dbo.fn_AutogestionSeccion(@EstadoId), '') LIKE 'Prod%' THEN 1 ELSE 0 END;
END
GO

-- 3) Estados destino: solo Produccion -------------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionEstados
AS
BEGIN
    SET NOCOUNT ON;
    SELECT e.id, e.nombre
    FROM WmsWdGeneral.dbo.Estado e
    WHERE dbo.fn_AutogestionEsProduccion(e.id) = 1
    ORDER BY e.nombre;
END
GO

-- 4) Buscar serial: donde esta --------------------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionBuscarSerial
    @Serial VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT i.Id, i.Serial, i.Mac, cs.codigo, cs.descripcion,
           i.estadoId, es.nombre AS estado,
           i.palletId, p.numero AS pallet,
           i.cajaEmpaqueId, ce.numero AS cajaEmpaque,
           m.nombre AS modulo, s.nombre AS seccion,
           dbo.fn_AutogestionEsProduccion(i.estadoId) AS esProduccion
    FROM Ingreso i
    LEFT JOIN CodigoSap cs ON cs.id = i.codigoSapId
    LEFT JOIN WmsWdGeneral.dbo.Estado es ON es.id = i.estadoId
    LEFT JOIN WmsWdGeneral.dbo.Modulo m ON m.id = es.moduloId
    LEFT JOIN WmsWdGeneral.dbo.Seccion s ON s.id = m.seccionId
    LEFT JOIN Pallet p ON p.id = i.palletId
    LEFT JOIN CajaEmpaque ce ON ce.id = i.cajaEmpaqueId
    WHERE i.Serial = @Serial;
END
GO

-- 5) Accion sobre un serial -------------------------------------------------
--    ESTADO: mueve a un estado de Produccion y lo saca del pallet y de la caja
--    CAJA  : lo saca de la caja de empaque
--    PALLET: lo pasa a otro pallet (de Produccion) y lo saca de la caja
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSerial
    @Serial          VARCHAR(50),
    @Accion          VARCHAR(20),
    @EstadoDestinoId INT = NULL,
    @PalletDestino   VARCHAR(50) = NULL,
    @UsuarioId       INT,
    @Justificacion   VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF LEN(LTRIM(RTRIM(ISNULL(@Justificacion, '')))) < 10
        THROW 50010, 'La justificacion es obligatoria (minimo 10 caracteres).', 1;

    DECLARE @Id INT, @EstadoOrigen INT, @PalletOrigen INT, @CajaOrigen INT;
    SELECT @Id = Id, @EstadoOrigen = estadoId, @PalletOrigen = palletId, @CajaOrigen = cajaEmpaqueId
    FROM Ingreso WHERE Serial = @Serial;

    IF @Id IS NULL
        THROW 50011, 'El serial no existe.', 1;

    IF dbo.fn_AutogestionEsProduccion(@EstadoOrigen) = 0
        THROW 50020, 'El serial esta en un modulo de Logistica (o fuera de Produccion): solo se puede consultar.', 1;

    DECLARE @NuevoEstado INT = @EstadoOrigen,
            @NuevoPallet INT = @PalletOrigen,
            @NuevaCaja   INT = @CajaOrigen;

    IF @Accion = 'ESTADO'
    BEGIN
        IF @EstadoDestinoId IS NULL OR dbo.fn_AutogestionEsProduccion(@EstadoDestinoId) = 0
            THROW 50012, 'El estado destino debe ser un modulo de Produccion.', 1;
        SET @NuevoEstado = @EstadoDestinoId;
        SET @NuevoPallet = NULL;
        SET @NuevaCaja   = NULL;
    END
    ELSE IF @Accion = 'CAJA'
    BEGIN
        IF ISNULL(@CajaOrigen, 0) = 0
            THROW 50013, 'El serial no esta en una caja.', 1;
        SET @NuevaCaja = NULL;
    END
    ELSE IF @Accion = 'PALLET'
    BEGIN
        DECLARE @PalletDestinoId INT, @PalletDestinoEstado INT;
        SELECT @PalletDestinoId = id, @PalletDestinoEstado = destinoId FROM Pallet WHERE numero = @PalletDestino;
        IF @PalletDestinoId IS NULL
            THROW 50014, 'El pallet destino no existe.', 1;
        IF dbo.fn_AutogestionEsProduccion(@PalletDestinoEstado) = 0
            THROW 50020, 'El pallet destino esta en Logistica (o fuera de Produccion).', 1;
        SET @NuevoPallet = @PalletDestinoId;
        SET @NuevaCaja   = NULL;
    END
    ELSE
        THROW 50015, 'Accion no valida.', 1;

    BEGIN TRAN;

    UPDATE Ingreso
    SET estadoId = @NuevoEstado,
        palletId = @NuevoPallet,
        cajaEmpaqueId = @NuevaCaja,
        usuarioIdMovimiento = @UsuarioId,
        fechaMovimiento = GETDATE()
    WHERE Id = @Id;

    IF ISNULL(@NuevoEstado, 0) <> ISNULL(@EstadoOrigen, 0)
        INSERT INTO Movimiento (SerialId, Serial, OrigenId, DestinoId, Descripcion, UsuarioId, Fecha)
        VALUES (@Id, @Serial, @EstadoOrigen, @NuevoEstado, 'AUTOGESTION', @UsuarioId, GETDATE());

    INSERT INTO AutogestionLog (UsuarioId, Accion, SerialId, Serial, EstadoOrigenId, EstadoDestinoId,
                                PalletOrigenId, PalletDestinoId, CajaOrigenId, CajaDestinoId, Cantidad, Justificacion)
    VALUES (@UsuarioId, @Accion, @Id, @Serial, @EstadoOrigen, @NuevoEstado,
            @PalletOrigen, @NuevoPallet, @CajaOrigen, @NuevaCaja, 1, LTRIM(RTRIM(@Justificacion)));

    COMMIT TRAN;
END
GO

-- 6) Buscar pallet: donde esta ----------------------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionBuscarPallet
    @Pallet VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT p.id, p.numero, cs.codigo, cs.descripcion,
           p.destinoId, es.nombre AS estado,
           m.nombre AS modulo, s.nombre AS seccion,
           dbo.fn_AutogestionEsProduccion(p.destinoId) AS esProduccion,
           (SELECT COUNT(*) FROM Ingreso i WHERE i.palletId = p.id) AS totalSeriales,
           (SELECT COUNT(*) FROM CajaEmpaque ce WHERE ce.palletId = p.id AND ce.activo = 1) AS totalCajas
    FROM Pallet p
    LEFT JOIN CodigoSap cs ON cs.id = p.codigoSapId
    LEFT JOIN WmsWdGeneral.dbo.Estado es ON es.id = p.destinoId
    LEFT JOIN WmsWdGeneral.dbo.Modulo m ON m.id = es.moduloId
    LEFT JOIN WmsWdGeneral.dbo.Seccion s ON s.id = m.seccionId
    WHERE p.numero = @Pallet;
END
GO

-- 7) Cajas del pallet -----------------------------------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionPalletCajas
    @PalletId INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT ce.id, ce.numero,
           (SELECT COUNT(*) FROM Ingreso i WHERE i.cajaEmpaqueId = ce.id) AS cantidad
    FROM CajaEmpaque ce
    WHERE ce.palletId = @PalletId AND ce.activo = 1
    ORDER BY ce.numero;
END
GO

-- 8) Accion sobre un pallet / sus cajas -------------------------------------------
--    MOVER        : el pallet y TODOS sus seriales (y cajas) pasan a un estado de Produccion
--    CAJA_VACIAR  : saca los seriales de la caja (quedan en el pallet sin caja)
--    CAJA_ELIMINAR: saca los seriales y desactiva la caja
--    CAJA_PALLET  : pasa los seriales de la caja a otro pallet de Produccion
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionPallet
    @Pallet          VARCHAR(50),
    @Accion          VARCHAR(20),
    @EstadoDestinoId INT = NULL,
    @CajaNumero      VARCHAR(50) = NULL,
    @PalletDestino   VARCHAR(50) = NULL,
    @UsuarioId       INT,
    @Justificacion   VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF LEN(LTRIM(RTRIM(ISNULL(@Justificacion, '')))) < 10
        THROW 50010, 'La justificacion es obligatoria (minimo 10 caracteres).', 1;

    DECLARE @PalletId INT, @EstadoActual INT;
    SELECT @PalletId = id, @EstadoActual = destinoId FROM Pallet WHERE numero = @Pallet;

    IF @PalletId IS NULL
        THROW 50021, 'El pallet no existe.', 1;

    IF dbo.fn_AutogestionEsProduccion(@EstadoActual) = 0
        THROW 50020, 'El pallet esta en un modulo de Logistica (o fuera de Produccion): solo se puede consultar.', 1;

    DECLARE @Cantidad INT = 0, @CajaId INT = NULL, @PalletDestinoId INT = NULL, @NuevoEstado INT = @EstadoActual;

    IF @Accion = 'MOVER'
    BEGIN
        IF @EstadoDestinoId IS NULL OR dbo.fn_AutogestionEsProduccion(@EstadoDestinoId) = 0
            THROW 50012, 'El estado destino debe ser un modulo de Produccion.', 1;
        SET @NuevoEstado = @EstadoDestinoId;
    END
    ELSE IF @Accion IN ('CAJA_VACIAR', 'CAJA_ELIMINAR', 'CAJA_PALLET')
    BEGIN
        SELECT @CajaId = id FROM CajaEmpaque WHERE palletId = @PalletId AND numero = @CajaNumero AND activo = 1;
        IF @CajaId IS NULL
            THROW 50016, 'La caja no existe en este pallet.', 1;

        IF @Accion = 'CAJA_PALLET'
        BEGIN
            DECLARE @EstadoPalletDestino INT;
            SELECT @PalletDestinoId = id, @EstadoPalletDestino = destinoId FROM Pallet WHERE numero = @PalletDestino;
            IF @PalletDestinoId IS NULL
                THROW 50014, 'El pallet destino no existe.', 1;
            IF @PalletDestinoId = @PalletId
                THROW 50017, 'El pallet destino debe ser distinto al actual.', 1;
            IF dbo.fn_AutogestionEsProduccion(@EstadoPalletDestino) = 0
                THROW 50020, 'El pallet destino esta en Logistica (o fuera de Produccion).', 1;
        END
    END
    ELSE
        THROW 50015, 'Accion no valida.', 1;

    BEGIN TRAN;

    IF @Accion = 'MOVER'
    BEGIN
        INSERT INTO Movimiento (SerialId, Serial, OrigenId, DestinoId, Descripcion, UsuarioId, Fecha)
        SELECT i.Id, i.Serial, i.estadoId, @NuevoEstado, 'AUTOGESTION', @UsuarioId, GETDATE()
        FROM Ingreso i
        WHERE i.palletId = @PalletId AND ISNULL(i.estadoId, 0) <> @NuevoEstado;

        UPDATE Ingreso
        SET estadoId = @NuevoEstado, usuarioIdMovimiento = @UsuarioId, fechaMovimiento = GETDATE()
        WHERE palletId = @PalletId;
        SET @Cantidad = @@ROWCOUNT;

        UPDATE CajaEmpaque SET estadoId = @NuevoEstado WHERE palletId = @PalletId AND activo = 1;

        UPDATE Pallet
        SET destinoId = @NuevoEstado, usuarioIdModifica = @UsuarioId, fechaModifica = GETDATE()
        WHERE id = @PalletId;
    END
    ELSE
    BEGIN
        UPDATE Ingreso
        SET cajaEmpaqueId = NULL,
            palletId = CASE WHEN @Accion = 'CAJA_PALLET' THEN @PalletDestinoId ELSE palletId END,
            usuarioIdMovimiento = @UsuarioId,
            fechaMovimiento = GETDATE()
        WHERE cajaEmpaqueId = @CajaId;
        SET @Cantidad = @@ROWCOUNT;

        IF @Accion = 'CAJA_ELIMINAR'
            UPDATE CajaEmpaque SET activo = 0 WHERE id = @CajaId;
    END

    INSERT INTO AutogestionLog (UsuarioId, Accion, EstadoOrigenId, EstadoDestinoId,
                                PalletOrigenId, PalletDestinoId, CajaOrigenId, Cantidad, Justificacion)
    VALUES (@UsuarioId, 'PALLET_' + @Accion, @EstadoActual, @NuevoEstado,
            @PalletId, ISNULL(@PalletDestinoId, @PalletId), @CajaId, @Cantidad, LTRIM(RTRIM(@Justificacion)));

    COMMIT TRAN;
END
GO

-- 9) Consulta de logs ---------------------------------------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionLogs
    @Serial VARCHAR(50) = NULL,
    @Limite INT = 200
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP (@Limite)
           l.Id, l.Fecha, u.nombreUsuario, l.Accion, l.Serial,
           eo.nombre AS estadoOrigen, ed.nombre AS estadoDestino,
           po.numero AS palletOrigen, pd.numero AS palletDestino,
           co.numero AS cajaOrigen, l.Justificacion, l.Cantidad
    FROM AutogestionLog l
    LEFT JOIN WmsWdGeneral.dbo.Usuario u ON u.id = l.UsuarioId
    LEFT JOIN WmsWdGeneral.dbo.Estado eo ON eo.id = l.EstadoOrigenId
    LEFT JOIN WmsWdGeneral.dbo.Estado ed ON ed.id = l.EstadoDestinoId
    LEFT JOIN Pallet po ON po.id = l.PalletOrigenId
    LEFT JOIN Pallet pd ON pd.id = l.PalletDestinoId
    LEFT JOIN CajaEmpaque co ON co.id = l.CajaOrigenId
    WHERE @Serial IS NULL OR l.Serial = @Serial OR po.numero = @Serial OR pd.numero = @Serial
    ORDER BY l.Fecha DESC, l.Id DESC;
END
GO

/* ---- Verificacion previa (ejecutar y revisar) ---------------------------------
SELECT e.id, e.nombre AS estado, m.nombre AS modulo, s.nombre AS seccion,
       dbo.fn_AutogestionEsProduccion(e.id) AS esProduccion
FROM WmsWdGeneral.dbo.Estado e
LEFT JOIN WmsWdGeneral.dbo.Modulo m ON m.id = e.moduloId
LEFT JOIN WmsWdGeneral.dbo.Seccion s ON s.id = m.seccionId
ORDER BY s.nombre, m.nombre, e.nombre;
--------------------------------------------------------------------------------- */
