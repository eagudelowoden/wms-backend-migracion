/* =====================================================================================
   AUTOGESTION - FASE 1 (seriales, pallets y cajas)  -  recorre TODAS las bases cliente

   REGLA: solo se puede modificar lo que esta en un modulo de PRODUCCION. Si el serial o pallet esta en
   LOGISTICA (u otra seccion) solo se consulta. La seccion de un estado sale de Estado.moduloId -> Modulo.seccionId
   -> Seccion.nombre (en WmsWdGeneral); Produccion = nombre que empieza por 'Prod'.

   Crea en cada base, SOLO LO QUE FALTA:
     Tablas   : AutogestionLog, AutogestionEstadoDestino (+ columna Cantidad, columna Accion de 30 y su indice)
     Funciones: fn_AutogestionSeccion, fn_AutogestionEsProduccion, fn_AutogestionEsScrap, fn_AutogestionDescripcion
     SP       : pa_AutogestionEstados, pa_AutogestionBuscarSerial, pa_AutogestionSerial,
                pa_AutogestionBuscarPallet, pa_AutogestionPalletCajas, pa_AutogestionPallet, pa_AutogestionLogs
     Datos    : semilla de estados destino (SERIAL y PALLET) en AutogestionEstadoDestino, solo los que falten
   Todos son objetos nuevos de Autogestion: no se modifica ninguna tabla ni SP del WMS. Los SP mueven datos solo
   cuando alguien usa la pantalla (Autogestion), nunca al ejecutar este script.

   Requisito: la base debe tener las tablas Ingreso, Pallet, CajaEmpaque, Movimiento, Empaque, Reparacion, Maestro
   y CodigoSap. Si falta alguna, esa base se omite y se avisa. Si el esquema difiere (por ejemplo una columna que no
   existe) esa base da ERROR y queda como estaba.
   Cada base va en una sola transaccion.

   @Modo       LISTAR (no cambia nada, dice que falta) | PROBAR (lo hace y lo deshace) | APLICAR
   @SoloBase   una sola base (NULL = todas)
   @Excluir    bases separadas por coma que se saltan
   @Reemplazar 1 = vuelve a crear (CREATE OR ALTER) las funciones y SP aunque existan; 0 = solo los que faltan
   Se puede ejecutar desde cualquier base: usa nombres de tres partes.
   Despues de esta fase se corre autogestion_aprobacion.sql.
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

IF OBJECT_ID('tempdb..#lotes') IS NOT NULL DROP TABLE #lotes;
IF OBJECT_ID('tempdb..#res') IS NOT NULL DROP TABLE #res;
CREATE TABLE #lotes (Orden INT PRIMARY KEY, Tipo VARCHAR(10), Objeto SYSNAME NULL, Sql NVARCHAR(MAX));
CREATE TABLE #res (Base SYSNAME, Resultado NVARCHAR(1000));

DECLARE @s NVARCHAR(MAX);

/* ---- Lotes (generados desde autogestion_fase1.sql, en el mismo orden; cada uno equivale a un bloque GO) ---- */

-- Lote 1: TABLA AutogestionLog
SET @s = N'IF OBJECT_ID(''dbo.AutogestionLog'', ''U'') IS NULL
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
);';
INSERT INTO #lotes VALUES (1, 'TABLA', 'AutogestionLog', @s);

-- Lote 2: AJUSTE
SET @s = N'IF COL_LENGTH(''dbo.AutogestionLog'', ''Cantidad'') IS NULL
    ALTER TABLE dbo.AutogestionLog ADD Cantidad INT NULL;';
INSERT INTO #lotes VALUES (2, 'AJUSTE', NULL, @s);

-- Lote 3: AJUSTE
SET @s = N'IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(''dbo.AutogestionLog'') AND name = ''Accion'' AND max_length < 30)
    ALTER TABLE dbo.AutogestionLog ALTER COLUMN Accion VARCHAR(30) NOT NULL;';
INSERT INTO #lotes VALUES (3, 'AJUSTE', NULL, @s);

-- Lote 4: AJUSTE
SET @s = N'IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = ''IX_AutogestionLog_Serial'')
    CREATE INDEX IX_AutogestionLog_Serial ON dbo.AutogestionLog (Serial, Fecha DESC);';
INSERT INTO #lotes VALUES (4, 'AJUSTE', NULL, @s);

-- Lote 5: FN fn_AutogestionSeccion
SET @s = N'CREATE OR ALTER FUNCTION dbo.fn_AutogestionSeccion (@EstadoId INT)
RETURNS VARCHAR(100)
AS
BEGIN
    RETURN (SELECT TOP 1 s.nombre
            FROM WmsWdGeneral.dbo.Estado e
            INNER JOIN WmsWdGeneral.dbo.Modulo m ON m.id = e.moduloId
            INNER JOIN WmsWdGeneral.dbo.Seccion s ON s.id = m.seccionId
            WHERE e.id = @EstadoId);
END';
INSERT INTO #lotes VALUES (5, 'FN', 'fn_AutogestionSeccion', @s);

-- Lote 6: FN fn_AutogestionEsProduccion
SET @s = N'CREATE OR ALTER FUNCTION dbo.fn_AutogestionEsProduccion (@EstadoId INT)
RETURNS INT
AS
BEGIN
    RETURN CASE WHEN ISNULL(dbo.fn_AutogestionSeccion(@EstadoId), '''') LIKE ''Prod%'' THEN 1 ELSE 0 END;
END';
INSERT INTO #lotes VALUES (6, 'FN', 'fn_AutogestionEsProduccion', @s);

-- Lote 7: FN fn_AutogestionEsScrap
SET @s = N'CREATE OR ALTER FUNCTION dbo.fn_AutogestionEsScrap (@SerialId INT)
RETURNS INT
AS
BEGIN
    RETURN CASE WHEN EXISTS (
        SELECT 1
        FROM Reparacion r
        INNER JOIN Maestro m ON m.Id = r.estadoFinalId
        WHERE r.serialId = @SerialId
          AND (m.Codigo LIKE ''SCRAP%'' OR m.Descripcion LIKE ''SCRAP%'')
    ) THEN 1 ELSE 0 END;
END';
INSERT INTO #lotes VALUES (7, 'FN', 'fn_AutogestionEsScrap', @s);

-- Lote 8: FN fn_AutogestionDescripcion
SET @s = N'CREATE OR ALTER FUNCTION dbo.fn_AutogestionDescripcion (@OrigenId INT, @DestinoId INT)
RETURNS VARCHAR(200)
AS
BEGIN
    RETURN ''AUTOGESTION Env'' + NCHAR(237) + ''o a ''
         + ISNULL((SELECT nombre FROM WmsWdGeneral.dbo.Estado WHERE id = @DestinoId), ''?'')
         + '', Desde: ''
         + ISNULL((SELECT nombre FROM WmsWdGeneral.dbo.Estado WHERE id = @OrigenId), ''?'');
END';
INSERT INTO #lotes VALUES (8, 'FN', 'fn_AutogestionDescripcion', @s);

-- Lote 9: TABLA AutogestionEstadoDestino
SET @s = N'IF OBJECT_ID(''dbo.AutogestionEstadoDestino'', ''U'') IS NULL
CREATE TABLE dbo.AutogestionEstadoDestino (
    Ambito   VARCHAR(10) NOT NULL,   -- SERIAL | PALLET
    EstadoId INT         NOT NULL,
    CONSTRAINT PK_AutogestionEstadoDestino PRIMARY KEY (Ambito, EstadoId)
);';
INSERT INTO #lotes VALUES (9, 'TABLA', 'AutogestionEstadoDestino', @s);

-- Lote 10: SEMILLA
SET @s = N'INSERT INTO dbo.AutogestionEstadoDestino (Ambito, EstadoId)
SELECT ''SERIAL'', e.id
FROM WmsWdGeneral.dbo.Estado e
WHERE e.nombre IN (''DIAGNOSTICO'', ''EMPAQUE'', ''CLASIFICACION'', ''REPARACION'', ''REPARACION-ASIGNACION'',
                   ''COSMETICA'', ''ETIQUETADO'', ''ENSAMBLE'', ''AUTOGESTION'')
  AND dbo.fn_AutogestionEsProduccion(e.id) = 1
  AND NOT EXISTS (SELECT 1 FROM dbo.AutogestionEstadoDestino d WHERE d.Ambito = ''SERIAL'' AND d.EstadoId = e.id);';
INSERT INTO #lotes VALUES (10, 'SEMILLA', NULL, @s);

-- Lote 11: SEMILLA
SET @s = N'INSERT INTO dbo.AutogestionEstadoDestino (Ambito, EstadoId)
SELECT ''PALLET'', e.id
FROM WmsWdGeneral.dbo.Estado e
WHERE (e.nombre LIKE ''P-%'' OR e.nombre LIKE ''%-ENTREGA'')
  AND dbo.fn_AutogestionEsProduccion(e.id) = 1
  AND NOT EXISTS (SELECT 1 FROM dbo.AutogestionEstadoDestino d WHERE d.Ambito = ''PALLET'' AND d.EstadoId = e.id);';
INSERT INTO #lotes VALUES (11, 'SEMILLA', NULL, @s);

-- Lote 12: SP pa_AutogestionEstados
SET @s = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionEstados
    @Ambito VARCHAR(10) = ''SERIAL''
AS
BEGIN
    SET NOCOUNT ON;
    SELECT e.id, e.nombre
    FROM dbo.AutogestionEstadoDestino d
    INNER JOIN WmsWdGeneral.dbo.Estado e ON e.id = d.EstadoId
    WHERE d.Ambito = @Ambito
    ORDER BY e.nombre;
END';
INSERT INTO #lotes VALUES (12, 'SP', 'pa_AutogestionEstados', @s);

-- Lote 13: SP pa_AutogestionBuscarSerial
SET @s = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionBuscarSerial
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
END';
INSERT INTO #lotes VALUES (13, 'SP', 'pa_AutogestionBuscarSerial', @s);

-- Lote 14: SP pa_AutogestionSerial
SET @s = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionSerial
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

    IF LEN(LTRIM(RTRIM(ISNULL(@Justificacion, '''')))) < 10
        THROW 50010, ''La justificacion es obligatoria (minimo 10 caracteres).'', 1;

    DECLARE @Id INT, @EstadoOrigen INT, @PalletOrigen INT, @CajaOrigen INT, @PalletEmpaque INT;
    SELECT @Id = Id, @EstadoOrigen = estadoId, @PalletOrigen = palletId, @CajaOrigen = cajaEmpaqueId,
           @PalletEmpaque = palletIdEmpaque
    FROM Ingreso WHERE Serial = @Serial;

    IF @Id IS NULL
        THROW 50011, ''El serial no existe.'', 1;

    IF dbo.fn_AutogestionEsProduccion(@EstadoOrigen) = 0
        THROW 50020, ''El serial esta en un modulo de Logistica (o fuera de Produccion): solo se puede consultar.'', 1;

    IF dbo.fn_AutogestionEsScrap(@Id) = 1
        THROW 50040, ''No se puede realizar el movimiento: el serial tiene estado final SCRAP en Reparacion.'', 1;

    IF ISNULL(@PalletEmpaque, 0) > 0 AND @Accion <> ''CAJA''
        THROW 50041, ''No se puede mover: el serial esta empacado (tiene pallet de empaque).'', 1;

    DECLARE @NuevoEstado INT = @EstadoOrigen,
            @NuevoPallet INT = @PalletOrigen,
            @NuevaCaja   INT = @CajaOrigen;

    DECLARE @EstadoSacado INT = (SELECT TOP 1 id FROM WmsWdGeneral.dbo.Estado WHERE nombre = ''P-EMPAQUE'');

    IF @Accion = ''ESTADO''
    BEGIN
        IF @EstadoDestinoId IS NULL
           OR NOT EXISTS (SELECT 1 FROM dbo.AutogestionEstadoDestino WHERE Ambito = ''SERIAL'' AND EstadoId = @EstadoDestinoId)
            THROW 50012, ''El estado destino no esta permitido para seriales.'', 1;
        SET @NuevoEstado = @EstadoDestinoId;
        SET @NuevoPallet = 0;
        SET @NuevaCaja   = 0;
    END
    ELSE IF @Accion = ''CAJA''
    BEGIN
        IF ISNULL(@CajaOrigen, 0) = 0
            THROW 50013, ''El serial no esta en una caja.'', 1;
        IF @EstadoSacado IS NULL
            THROW 50018, ''No se encontro el estado P-EMPAQUE para dejar el serial.'', 1;
        SET @NuevoEstado = @EstadoSacado;
        SET @NuevoPallet = 0;
        SET @NuevaCaja   = 0;
    END
    ELSE IF @Accion = ''PALLET''
    BEGIN
        DECLARE @PalletDestinoId INT, @PalletDestinoEstado INT;
        SELECT @PalletDestinoId = id, @PalletDestinoEstado = destinoId FROM Pallet WHERE numero = @PalletDestino;
        IF @PalletDestinoId IS NULL
            THROW 50014, ''El pallet destino no existe.'', 1;
        IF dbo.fn_AutogestionEsProduccion(@PalletDestinoEstado) = 0
            THROW 50020, ''El pallet destino esta en Logistica (o fuera de Produccion).'', 1;
        SET @NuevoPallet = @PalletDestinoId;
        SET @NuevaCaja   = 0;
    END
    ELSE
        THROW 50015, ''Accion no valida.'', 1;

    BEGIN TRAN;

    UPDATE Ingreso
    SET estadoId = @NuevoEstado,';
SET @s += NCHAR(10) + N'        palletId = @NuevoPallet,
        cajaEmpaqueId = @NuevaCaja,
        usuarioIdMovimiento = @UsuarioId,
        fechaMovimiento = GETDATE()
    WHERE Id = @Id;

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
END';
INSERT INTO #lotes VALUES (14, 'SP', 'pa_AutogestionSerial', @s);

-- Lote 15: SP pa_AutogestionBuscarPallet
SET @s = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionBuscarPallet
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
END';
INSERT INTO #lotes VALUES (15, 'SP', 'pa_AutogestionBuscarPallet', @s);

-- Lote 16: SP pa_AutogestionPalletCajas
SET @s = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionPalletCajas
    @PalletId INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT ce.id, ce.numero,
           (SELECT COUNT(*) FROM Ingreso i WHERE i.cajaEmpaqueId = ce.id) AS cantidad
    FROM CajaEmpaque ce
    WHERE ce.palletId = @PalletId AND ce.activo = 1
    ORDER BY ce.numero;
END';
INSERT INTO #lotes VALUES (16, 'SP', 'pa_AutogestionPalletCajas', @s);

-- Lote 17: SP pa_AutogestionPallet
SET @s = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionPallet
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

    IF LEN(LTRIM(RTRIM(ISNULL(@Justificacion, '''')))) < 10
        THROW 50010, ''La justificacion es obligatoria (minimo 10 caracteres).'', 1;

    DECLARE @PalletId INT, @EstadoActual INT;
    SELECT @PalletId = id, @EstadoActual = destinoId FROM Pallet WHERE numero = @Pallet;

    IF @PalletId IS NULL
        THROW 50021, ''El pallet no existe.'', 1;

    IF dbo.fn_AutogestionEsProduccion(@EstadoActual) = 0
        THROW 50020, ''El pallet esta en un modulo de Logistica (o fuera de Produccion): solo se puede consultar.'', 1;

    IF EXISTS (SELECT 1 FROM Ingreso i WHERE i.palletId = @PalletId AND dbo.fn_AutogestionEsScrap(i.Id) = 1)
        THROW 50040, ''No se puede realizar el movimiento: el pallet tiene seriales con estado final SCRAP en Reparacion.'', 1;

    IF (
           EXISTS (SELECT 1 FROM WmsWdGeneral.dbo.Estado e WHERE e.id = @EstadoActual AND e.nombre LIKE ''%EMPAQUE%'')
        OR EXISTS (SELECT 1 FROM Ingreso i WHERE i.palletId = @PalletId AND ISNULL(i.palletIdEmpaque, 0) > 0))
        THROW 50041, ''No se puede mover: el pallet esta en Empaque o tiene seriales empacados.'', 1;

    DECLARE @Cantidad INT = 0, @CajaId INT = NULL, @PalletDestinoId INT = NULL, @NuevoEstado INT = @EstadoActual;
    DECLARE @EstadoSacado INT = (SELECT TOP 1 id FROM WmsWdGeneral.dbo.Estado WHERE nombre = ''P-EMPAQUE'');

    IF @Accion IN (''CAJA_VACIAR'', ''CAJA_ELIMINAR'') AND @EstadoSacado IS NULL
        THROW 50018, ''No se encontro el estado P-EMPAQUE para dejar los seriales.'', 1;

    IF @Accion = ''MOVER''
    BEGIN
        IF @EstadoDestinoId IS NULL
           OR NOT EXISTS (SELECT 1 FROM dbo.AutogestionEstadoDestino WHERE Ambito = ''PALLET'' AND EstadoId = @EstadoDestinoId)
            THROW 50012, ''El estado destino no esta permitido para pallets (solo P-* y *-ENTREGA).'', 1;
        SET @NuevoEstado = @EstadoDestinoId;
    END
    ELSE IF @Accion IN (''CAJA_VACIAR'', ''CAJA_ELIMINAR'', ''CAJA_PALLET'')
    BEGIN
        SELECT @CajaId = id FROM CajaEmpaque WHERE palletId = @PalletId AND numero = @CajaNumero AND activo = 1;
        IF @CajaId IS NULL
            THROW 50016, ''La caja no existe en este pallet.'', 1;

        IF @Accion = ''CAJA_PALLET''
        BEGIN
            DECLARE @EstadoPalletDestino INT;
            SELECT @PalletDestinoId = id, @EstadoPalletDestino = destinoId FROM Pallet WHERE numero = @PalletDestino;
            IF @PalletDestinoId IS NULL
                THROW 50014, ''El pallet destino no existe.'', 1;
            IF @PalletDestinoId = @PalletId
                THROW 50017, ''El pallet destino debe ser distinto al actual.'', 1;
            IF dbo.fn_AutogestionEsProduccion(@EstadoPalletDestino) = 0';
SET @s += NCHAR(10) + N'                THROW 50020, ''El pallet destino esta en Logistica (o fuera de Produccion).'', 1;
        END
    END
    ELSE
        THROW 50015, ''Accion no valida.'', 1;

    BEGIN TRAN;

    IF @Accion = ''MOVER''
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
        DECLARE @Seriales TABLE (SerialId INT PRIMARY KEY);
        INSERT INTO @Seriales (SerialId)
        SELECT Id FROM Ingreso WHERE cajaEmpaqueId = @CajaId;

        DELETE FROM Empaque
        WHERE serialId IN (SELECT SerialId FROM @Seriales);

        IF @Accion IN (''CAJA_VACIAR'', ''CAJA_ELIMINAR'')
        BEGIN
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
            UPDATE Ingreso
            SET palletId = @PalletDestinoId, cajaEmpaqueId = 0,
                usuarioIdMovimiento = @UsuarioId, fechaMovimiento = GETDATE()
            WHERE Id IN (SELECT SerialId FROM @Seriales);
            SET @Cantidad = @@ROWCOUNT;
        END

        IF @Accion = ''CAJA_ELIMINAR''
            UPDATE CajaEmpaque SET activo = 0 WHERE id = @CajaId;
    END

    INSERT INTO AutogestionLog (UsuarioId, Accion, EstadoOrigenId, EstadoDestinoId,
                                PalletOrigenId, PalletDestinoId, CajaOrigenId, Cantidad, Justificacion)
    VALUES (@UsuarioId, ''PALLET_'' + @Accion, @EstadoActual, @NuevoEstado,
            @PalletId, ISNULL(@PalletDestinoId, @PalletId), @CajaId, @Cantidad, LTRIM(RTRIM(@Justificacion)));

    COMMIT TRAN;
END';
INSERT INTO #lotes VALUES (17, 'SP', 'pa_AutogestionPallet', @s);

-- Lote 18: SP pa_AutogestionLogs
SET @s = N'CREATE OR ALTER PROCEDURE dbo.pa_AutogestionLogs
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
END';
INSERT INTO #lotes VALUES (18, 'SP', 'pa_AutogestionLogs', @s);


/* ---- Recorrido por base ---- */
DECLARE @db SYSNAME, @exec NVARCHAR(300), @lote NVARCHAR(MAX), @tipo VARCHAR(10), @obj SYSNAME;
DECLARE @i INT, @max INT, @sinTablas NVARCHAR(500), @faltan NVARCHAR(1500);
SELECT @max = MAX(Orden) FROM #lotes;

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
    SET @sinTablas = STUFF((SELECT ', ' + t.Nombre
                              FROM (VALUES ('Ingreso'), ('Pallet'), ('CajaEmpaque'), ('Movimiento'), ('Empaque'),
                                           ('Reparacion'), ('Maestro'), ('CodigoSap')) t(Nombre)
                             WHERE OBJECT_ID(QUOTENAME(@db) + N'.dbo.' + t.Nombre, 'U') IS NULL
                               FOR XML PATH('')), 1, 2, '');
    SET @faltan = STUFF((SELECT ', ' + Objeto FROM #lotes
                          WHERE Tipo IN ('TABLA', 'FN', 'SP')
                            AND OBJECT_ID(QUOTENAME(@db) + N'.dbo.' + Objeto) IS NULL
                          ORDER BY Orden FOR XML PATH('')), 1, 2, '');
    -- Tabla de log de una version anterior: le falta la columna Cantidad
    IF OBJECT_ID(QUOTENAME(@db) + N'.dbo.AutogestionLog', 'U') IS NOT NULL
       AND COL_LENGTH(QUOTENAME(@db) + N'.dbo.AutogestionLog', 'Cantidad') IS NULL
      SET @faltan = CONCAT_WS(', ', @faltan, 'columna AutogestionLog.Cantidad');

    IF @sinTablas IS NOT NULL
      INSERT INTO #res VALUES (@db, 'sin tablas base (' + @sinTablas + '): no aplica');
    ELSE IF @faltan IS NULL AND @Reemplazar = 0
      INSERT INTO #res VALUES (@db, 'completo');
    ELSE IF @Modo = 'LISTAR'
      INSERT INTO #res VALUES (@db, 'falta: ' + ISNULL(@faltan, '(solo reemplazo de funciones y SP)'));
    ELSE
    BEGIN
      SET @exec = QUOTENAME(@db) + N'.sys.sp_executesql';
      BEGIN TRY
        BEGIN TRAN;

        SET @i = 1;
        WHILE @i <= @max
        BEGIN
          SELECT @tipo = Tipo, @obj = Objeto, @lote = Sql FROM #lotes WHERE Orden = @i;
          -- Tablas, ajustes y semillas son idempotentes (llevan su propia guarda); funciones y SP solo si faltan
          IF @tipo NOT IN ('FN', 'SP') OR @Reemplazar = 1 OR OBJECT_ID(QUOTENAME(@db) + N'.dbo.' + @obj) IS NULL
            EXEC @exec @lote;
          SET @i += 1;
        END

        IF @Modo = 'PROBAR'
        BEGIN
          ROLLBACK TRAN;
          INSERT INTO #res VALUES (@db, 'probado OK (revertido): ' + ISNULL(@faltan, 'reemplazo de funciones y SP'));
        END
        ELSE
        BEGIN
          COMMIT TRAN;
          INSERT INTO #res VALUES (@db, 'aplicado: ' + ISNULL(@faltan, 'reemplazo de funciones y SP'));
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
                  WHEN 'PROBAR'  THEN 'PROBAR: se hizo y se revirtio'
                  ELSE                'APLICAR: cambios guardados' END AS Modo,
       LEFT(Resultado, CHARINDEX(':', Resultado + ':') - 1) AS Resultado, COUNT(*) AS Bases
  FROM #res GROUP BY LEFT(Resultado, CHARINDEX(':', Resultado + ':') - 1) ORDER BY COUNT(*) DESC;
-- Detalle por base (los errores primero)
SELECT Base, Resultado FROM #res ORDER BY CASE WHEN Resultado LIKE 'ERROR%' THEN 0 ELSE 1 END, Base;
