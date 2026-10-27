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

-- Serial con estado final SCRAP en Reparacion (Maestro.Codigo/Descripcion empieza por SCRAP)
CREATE OR ALTER FUNCTION dbo.fn_AutogestionEsScrap (@SerialId INT)
RETURNS INT
AS
BEGIN
    RETURN CASE WHEN EXISTS (
        SELECT 1
        FROM Reparacion r
        INNER JOIN Maestro m ON m.Id = r.estadoFinalId
        WHERE r.serialId = @SerialId
          AND (m.Codigo LIKE 'SCRAP%' OR m.Descripcion LIKE 'SCRAP%')
    ) THEN 1 ELSE 0 END;
END
GO

-- Descripcion del Movimiento: "AUTOGESTION Envío a X, Desde: Y"
CREATE OR ALTER FUNCTION dbo.fn_AutogestionDescripcion (@OrigenId INT, @DestinoId INT)
RETURNS VARCHAR(200)
AS
BEGIN
    RETURN 'AUTOGESTION Env' + NCHAR(237) + 'o a '
         + ISNULL((SELECT nombre FROM WmsWdGeneral.dbo.Estado WHERE id = @DestinoId), '?')
         + ', Desde: '
         + ISNULL((SELECT nombre FROM WmsWdGeneral.dbo.Estado WHERE id = @OrigenId), '?');
END
GO

-- 2b) Estados destino permitidos por ambito ----------------------------------
--     SERIAL: a donde se puede mover un serial
--     PALLET: a donde se puede mover un pallet (estados P-* y *-ENTREGA)
--     Es una tabla para poder ajustar la lista sin tocar codigo.
IF OBJECT_ID('dbo.AutogestionEstadoDestino', 'U') IS NULL
CREATE TABLE dbo.AutogestionEstadoDestino (
    Ambito   VARCHAR(10) NOT NULL,   -- SERIAL | PALLET
    EstadoId INT         NOT NULL,
    CONSTRAINT PK_AutogestionEstadoDestino PRIMARY KEY (Ambito, EstadoId)
);
GO

-- Semilla SERIAL (ajustar los nombres si en tu base se llaman distinto)
INSERT INTO dbo.AutogestionEstadoDestino (Ambito, EstadoId)
SELECT 'SERIAL', e.id
FROM WmsWdGeneral.dbo.Estado e
WHERE e.nombre IN ('DIAGNOSTICO', 'EMPAQUE', 'CLASIFICACION', 'REPARACION', 'REPARACION-ASIGNACION',
                   'COSMETICA', 'ETIQUETADO', 'ENSAMBLE', 'AUTOGESTION')
  AND dbo.fn_AutogestionEsProduccion(e.id) = 1
  AND NOT EXISTS (SELECT 1 FROM dbo.AutogestionEstadoDestino d WHERE d.Ambito = 'SERIAL' AND d.EstadoId = e.id);
GO

-- Semilla PALLET: estados que empiezan por P- o terminan en -ENTREGA, de Produccion
INSERT INTO dbo.AutogestionEstadoDestino (Ambito, EstadoId)
SELECT 'PALLET', e.id
FROM WmsWdGeneral.dbo.Estado e
WHERE (e.nombre LIKE 'P-%' OR e.nombre LIKE '%-ENTREGA')
  AND dbo.fn_AutogestionEsProduccion(e.id) = 1
  AND NOT EXISTS (SELECT 1 FROM dbo.AutogestionEstadoDestino d WHERE d.Ambito = 'PALLET' AND d.EstadoId = e.id);
GO

-- 3) Estados destino por ambito ------------------------------------------------
CREATE OR ALTER PROCEDURE dbo.pa_AutogestionEstados
    @Ambito VARCHAR(10) = 'SERIAL'
AS
BEGIN
    SET NOCOUNT ON;
    SELECT e.id, e.nombre
    FROM dbo.AutogestionEstadoDestino d
    INNER JOIN WmsWdGeneral.dbo.Estado e ON e.id = d.EstadoId
    WHERE d.Ambito = @Ambito
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
           dbo.fn_AutogestionEsProduccion(i.estadoId) AS esProduccion,
           dbo.fn_AutogestionEsScrap(i.Id) AS esScrap,
           ISNULL(i.palletIdEmpaque, 0) AS palletIdEmpaque
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
--    ESTADO: mueve a un estado de Produccion; palletId = 0, cajaEmpaqueId = 0 y se borra de Empaque
--    CAJA  : "sacar": palletId = 0, cajaEmpaqueId = 0, estado = P-EMPAQUE y se borra de Empaque
--    PALLET: lo pasa a otro pallet (de Produccion); cajaEmpaqueId = 0 y se borra de Empaque
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

    DECLARE @Id INT, @EstadoOrigen INT, @PalletOrigen INT, @CajaOrigen INT, @PalletEmpaque INT;
    SELECT @Id = Id, @EstadoOrigen = estadoId, @PalletOrigen = palletId, @CajaOrigen = cajaEmpaqueId,
           @PalletEmpaque = palletIdEmpaque
    FROM Ingreso WHERE Serial = @Serial;

    IF @Id IS NULL
        THROW 50011, 'El serial no existe.', 1;

    IF dbo.fn_AutogestionEsProduccion(@EstadoOrigen) = 0
        THROW 50020, 'El serial esta en un modulo de Logistica (o fuera de Produccion): solo se puede consultar.', 1;

    IF dbo.fn_AutogestionEsScrap(@Id) = 1
        THROW 50040, 'No se puede realizar el movimiento: el serial tiene estado final SCRAP en Reparacion.', 1;

    -- Serial ya empacado en un pallet de empaque: solo se puede sacar de la caja (accion CAJA)
    IF ISNULL(@PalletEmpaque, 0) > 0 AND @Accion <> 'CAJA'
        THROW 50041, 'No se puede mover: el serial esta empacado (tiene pallet de empaque).', 1;

    DECLARE @NuevoEstado INT = @EstadoOrigen,
            @NuevoPallet INT = @PalletOrigen,
            @NuevaCaja   INT = @CajaOrigen;

    -- Estado al que vuelve un serial "sacado" de su caja (mismo que usa Empaque al quitar un serial)
    DECLARE @EstadoSacado INT = (SELECT TOP 1 id FROM WmsWdGeneral.dbo.Estado WHERE nombre = 'P-EMPAQUE');

    IF @Accion = 'ESTADO'
    BEGIN
        IF @EstadoDestinoId IS NULL
           OR NOT EXISTS (SELECT 1 FROM dbo.AutogestionEstadoDestino WHERE Ambito = 'SERIAL' AND EstadoId = @EstadoDestinoId)
            THROW 50012, 'El estado destino no esta permitido para seriales.', 1;
        SET @NuevoEstado = @EstadoDestinoId;
        SET @NuevoPallet = 0;
        SET @NuevaCaja   = 0;
    END
    ELSE IF @Accion = 'CAJA'
    BEGIN
        IF ISNULL(@CajaOrigen, 0) = 0
            THROW 50013, 'El serial no esta en una caja.', 1;
        IF @EstadoSacado IS NULL
            THROW 50018, 'No se encontro el estado P-EMPAQUE para dejar el serial.', 1;
        SET @NuevoEstado = @EstadoSacado;
        SET @NuevoPallet = 0;
        SET @NuevaCaja   = 0;
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
        SET @NuevaCaja   = 0;
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

    -- El serial ya no esta en ninguna caja: se elimina de la tabla Empaque (por serialId)
    IF @NuevaCaja = 0
        DELETE FROM Empaque WHERE serialId = @Id;

    IF ISNULL(@NuevoEstado, 0) <> ISNULL(@EstadoOrigen, 0)
        INSERT INTO Movimiento (SerialId, Serial, OrigenId, DestinoId, Descripcion, UsuarioId, Fecha)
        VALUES (@Id, @Serial, @EstadoOrigen, @NuevoEstado, dbo.fn_AutogestionDescripcion(@EstadoOrigen, @NuevoEstado), @UsuarioId, GETDATE());

    INSERT INTO AutogestionLog (UsuarioId, Accion, SerialId, Serial, EstadoOrigenId, EstadoDestinoId,
                                PalletOrigenId, PalletDestinoId, CajaOrigenId, CajaDestinoId, Cantidad, Justificacion)
    VALUES (@UsuarioId, @Accion, @Id, @Serial, @EstadoOrigen, @NuevoEstado,
            NULLIF(@PalletOrigen, 0), NULLIF(@NuevoPallet, 0), NULLIF(@CajaOrigen, 0), NULLIF(@NuevaCaja, 0),
            1, LTRIM(RTRIM(@Justificacion)));

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
           (SELECT COUNT(*) FROM CajaEmpaque ce WHERE ce.palletId = p.id AND ce.activo = 1) AS totalCajas,
           (SELECT COUNT(*) FROM Ingreso i WHERE i.palletId = p.id AND dbo.fn_AutogestionEsScrap(i.Id) = 1) AS serialesScrap,
           (SELECT COUNT(*) FROM Ingreso i WHERE i.palletId = p.id AND ISNULL(i.palletIdEmpaque, 0) > 0) AS serialesEmpacados
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
--    CAJA_VACIAR  : saca los seriales de la caja: palletId = 0, cajaEmpaqueId = 0, estado P-EMPAQUE
--                   y se borran de la tabla Empaque
--    CAJA_ELIMINAR: igual que CAJA_VACIAR y ademas desactiva la caja
--    CAJA_PALLET  : pasa los seriales de la caja a otro pallet de Produccion (sin caja, fuera de Empaque)
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

    -- Pallet con algun serial en SCRAP (Reparacion): ningun movimiento
    IF EXISTS (SELECT 1 FROM Ingreso i WHERE i.palletId = @PalletId AND dbo.fn_AutogestionEsScrap(i.Id) = 1)
        THROW 50040, 'No se puede realizar el movimiento: el pallet tiene seriales con estado final SCRAP en Reparacion.', 1;

    -- Pallet en Empaque o con seriales empacados: ninguna accion (mover, sacar seriales, pasar a pallet, eliminar caja)
    IF (
           EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Estado e WHERE e.id = @EstadoActual AND e.nombre LIKE '%EMPAQUE%')
        OR EXISTS (SELECT 1 FROM Ingreso i WHERE i.palletId = @PalletId AND ISNULL(i.palletIdEmpaque, 0) > 0))
        THROW 50041, 'No se puede mover: el pallet esta en Empaque o tiene seriales empacados.', 1;

    DECLARE @Cantidad INT = 0, @CajaId INT = NULL, @PalletDestinoId INT = NULL, @NuevoEstado INT = @EstadoActual;
    DECLARE @EstadoSacado INT = (SELECT TOP 1 id FROM WmsWdGeneral.dbo.Estado WHERE nombre = 'P-EMPAQUE');

    IF @Accion IN ('CAJA_VACIAR', 'CAJA_ELIMINAR') AND @EstadoSacado IS NULL
        THROW 50018, 'No se encontro el estado P-EMPAQUE para dejar los seriales.', 1;

    IF @Accion = 'MOVER'
    BEGIN
        IF @EstadoDestinoId IS NULL
           OR NOT EXISTS (SELECT 1 FROM dbo.AutogestionEstadoDestino WHERE Ambito = 'PALLET' AND EstadoId = @EstadoDestinoId)
            THROW 50012, 'El estado destino no esta permitido para pallets (solo P-* y *-ENTREGA).', 1;
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
        SELECT i.Id, i.Serial, i.estadoId, @NuevoEstado, dbo.fn_AutogestionDescripcion(i.estadoId, @NuevoEstado), @UsuarioId, GETDATE()
        FROM Ingreso i
        WHERE i.palletId = @PalletId AND ISNULL(i.estadoId, 0) <> @NuevoEstado;

        UPDATE Ingreso
        SET estadoId = @NuevoEstado, usuarioIdMovimiento = @UsuarioId, fechaMovimiento = GETDATE()
        WHERE palletId = @PalletId;
        SET @Cantidad = @@ROWCOUNT;

        UPDATE Pallet
        SET destinoId = @NuevoEstado, usuarioIdModifica = @UsuarioId, fechaModifica = GETDATE()
        WHERE id = @PalletId;
    END
    ELSE
    BEGIN
        -- Seriales de la caja (se toman antes de modificar Ingreso)
        DECLARE @Seriales TABLE (SerialId INT PRIMARY KEY);
        INSERT INTO @Seriales (SerialId)
        SELECT Id FROM Ingreso WHERE cajaEmpaqueId = @CajaId;

        -- Se eliminan de la tabla Empaque (por serialId)
        DELETE FROM Empaque
        WHERE serialId IN (SELECT SerialId FROM @Seriales);

        IF @Accion IN ('CAJA_VACIAR', 'CAJA_ELIMINAR')
        BEGIN
            -- "Sacar": palletId = 0, cajaEmpaqueId = 0 y vuelven a P-EMPAQUE
            INSERT INTO Movimiento (SerialId, Serial, OrigenId, DestinoId, Descripcion, UsuarioId, Fecha)
            SELECT i.Id, i.Serial, i.estadoId, @EstadoSacado, dbo.fn_AutogestionDescripcion(i.estadoId, @EstadoSacado), @UsuarioId, GETDATE()
            FROM Ingreso i
            WHERE i.Id IN (SELECT SerialId FROM @Seriales) AND ISNULL(i.estadoId, 0) <> @EstadoSacado;

            UPDATE Ingreso
            SET estadoId = @EstadoSacado, palletId = 0, cajaEmpaqueId = 0,
                usuarioIdMovimiento = @UsuarioId, fechaMovimiento = GETDATE()
            WHERE Id IN (SELECT SerialId FROM @Seriales);
            SET @Cantidad = @@ROWCOUNT;
            SET @NuevoEstado = @EstadoSacado;
        END
        ELSE
        BEGIN
            -- CAJA_PALLET: pasan al otro pallet sin caja
            UPDATE Ingreso
            SET palletId = @PalletDestinoId, cajaEmpaqueId = 0,
                usuarioIdMovimiento = @UsuarioId, fechaMovimiento = GETDATE()
            WHERE Id IN (SELECT SerialId FROM @Seriales);
            SET @Cantidad = @@ROWCOUNT;
        END

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

/* ---- Revisar las listas de destino que quedaron sembradas ---------------------
SELECT d.Ambito, e.id, e.nombre
FROM dbo.AutogestionEstadoDestino d
JOIN WmsWdGeneral.dbo.Estado e ON e.id = d.EstadoId
ORDER BY d.Ambito, e.nombre;

-- Para agregar o quitar destinos manualmente:
-- INSERT INTO dbo.AutogestionEstadoDestino (Ambito, EstadoId) VALUES ('SERIAL', <id>);
-- DELETE FROM dbo.AutogestionEstadoDestino WHERE Ambito = 'SERIAL' AND EstadoId = <id>;
--------------------------------------------------------------------------------- */
