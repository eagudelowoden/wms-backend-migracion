
   PARTE B - base de CADA cliente (repetir por cliente)
     B1  Fila PAUT en la tabla Menu
     B2  Cosmetica: columna UsuarioAsignadoId + pa_InsertCosmetica
     B3  Reparacion: pa_GetRepairUser (cruce con WmsWdGeneral.dbo.Usuario)
     B4  Autogestion fase 1   (tablas, funciones, procedimientos de mover/consultar)
     B5  Autogestion aprobacion (solicitudes y enlaces de aprobacion)

   Notas:
   - Todos los scripts son idempotentes (se pueden re-ejecutar), salvo que se indique.
   - Abrir/guardar el archivo como UTF-8 para conservar las tildes.
   - Los scripts SQL que trajo la rama Bernabe (sql/2026-10-*.sql) son de OTRO
     trabajo y NO estan en este archivo.
   - Backend: despues de ejecutar, reiniciar el backend (cambios en MenuModuloMapping
     y AutogestionService).
   ===================================================================== */


/* #####################################################################
   PARTE A - WmsWdGeneral  (una sola vez)
   ##################################################################### */

/* ---------------------------------------------------------------------
   A1) Modulo "Autogestion" asignable desde Gestion de Perfiles
       (el Id 46 debe coincidir con MenuModuloMapping.java: put(46, "PAUT"))
   --------------------------------------------------------------------- */
USE WmsWdGeneral;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 46 OR (SeccionId = 3 AND Nombre = N'Autogestión'))
BEGIN
    IF COLUMNPROPERTY(OBJECT_ID('dbo.Modulo'), 'Id', 'IsIdentity') = 1
    BEGIN
        SET IDENTITY_INSERT dbo.Modulo ON;
        INSERT INTO dbo.Modulo (Id, SeccionId, Nombre, Icono, Controlador)
        VALUES (46, 3, N'Autogestión', '', 'autogestion');
        SET IDENTITY_INSERT dbo.Modulo OFF;
    END
    ELSE
    BEGIN
        INSERT INTO dbo.Modulo (Id, SeccionId, Nombre, Icono, Controlador)
        VALUES (46, 3, N'Autogestión', '', 'autogestion');
    END
END
GO

SELECT * FROM dbo.Modulo WHERE Id = 46;
GO

/* ---------------------------------------------------------------------
   A2) Tipo de perfil TECNICO COSMETICA
       El dropdown "Tecnico Cosmetica" (pa_GetListUser) busca por este TIPO de perfil.
       Sin el, el dropdown solo muestra 'NINGUNO'.
   --------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM dbo.TipoPerfil WHERE Nombre = 'TECNICO COSMETICA')
    INSERT INTO dbo.TipoPerfil (Nombre) VALUES ('TECNICO COSMETICA');
GO

SELECT * FROM dbo.TipoPerfil;
GO

/* Asignarlo al perfil de los tecnicos de cosmetica.
   OJO: 1828 es el id del perfil "Tecnico Comestica" EN LA BASE DONDE SE PROBO.
   Verifica el id del perfil en cada ambiente (SELECT * FROM dbo.Perfil) o asignalo
   desde Administracion > Perfiles > Tipo Perfil > Asignar. */
INSERT INTO dbo.PerfilTipoPerfil (perfilId, tipoPerfilId)
SELECT 1828, t.Id
FROM dbo.TipoPerfil t
WHERE t.Nombre = 'TECNICO COSMETICA'
  AND NOT EXISTS (SELECT 1 FROM dbo.PerfilTipoPerfil x WHERE x.perfilId = 1828 AND x.tipoPerfilId = t.Id);
GO

/* Comprobar (85 = id del cliente de la prueba; cambiar por el del cliente):
   debe devolver los nombreUsuario activos del cliente con ese perfil. */
-- EXEC pa_GetListUser 85, 'TECNICO COSMETICA';

/* (OPCIONAL / YA NO SE USA) Tipo de perfil APROBADOR AUTOGESTION.
   Los aprobadores ahora salen de application.properties
   (autogestion.aprobadores.correos), no de perfiles. No hace falta crearlo.
IF NOT EXISTS (SELECT 1 FROM dbo.TipoPerfil WHERE Nombre = 'APROBADOR AUTOGESTION')
    INSERT INTO dbo.TipoPerfil (Nombre) VALUES ('APROBADOR AUTOGESTION');
*/
GO


/* #####################################################################
   PARTE B - base de CADA cliente  (repetir por cliente)
   Cambiar la base antes de ejecutar, p. ej.:  USE [WmsWdLegacyCostaRica];
   ##################################################################### */
-- USE [NOMBRE_BASE_DEL_CLIENTE];
-- GO

/* ---------------------------------------------------------------------
   B1) Fila PAUT en la tabla Menu (copia la de Cosmetica PCOS, hija de PROD)
       Necesaria SIEMPRE: de aqui salen nombre, ruta e icono del menu,
       con o sin USAR_MENUXPERFILES.
   --------------------------------------------------------------------- */
INSERT INTO Menu (id, descripcion, tipo, orden, accion, estado, icono, id_padre, view_name, controller_name)
SELECT 'PAUT', N'Autogestión', tipo,
       (SELECT MAX(orden) + 1 FROM Menu WHERE id_padre = 'PROD'),
       accion, estado, icono, id_padre, view_name, controller_name
FROM Menu
WHERE id = 'PCOS'
  AND NOT EXISTS (SELECT 1 FROM Menu WHERE id = 'PAUT');
GO

-- Verifica (si PAUT no aparece, el id de Cosmetica no es PCOS en esa base)
SELECT * FROM Menu WHERE id IN ('PCOS', 'PAUT');
GO

/* ---------------------------------------------------------------------
   B2) COSMETICA - tecnico asignado
       Pantalla: Cosmetica > Entrega/Proceso, campo "Tecnico Cosmetica".
       Backend: /client/cosmetica/insertCosmetica recibe usuarioAsignadoId.
   --------------------------------------------------------------------- */
IF COL_LENGTH('dbo.Cosmetica', 'UsuarioAsignadoId') IS NULL
    ALTER TABLE dbo.Cosmetica ADD UsuarioAsignadoId INT NULL;
GO

-- Limpieza de una version anterior descartada (pa_AsignarCosmetica / OrigenProceso)
IF OBJECT_ID('dbo.pa_AsignarCosmetica', 'P') IS NOT NULL DROP PROCEDURE dbo.pa_AsignarCosmetica;
GO
IF COL_LENGTH('dbo.Cosmetica', 'OrigenProceso') IS NOT NULL
BEGIN
    DECLARE @df NVARCHAR(256) = (
        SELECT dc.name FROM sys.default_constraints dc
        JOIN sys.columns c ON c.default_object_id = dc.object_id AND c.object_id = dc.parent_object_id
        WHERE dc.parent_object_id = OBJECT_ID('dbo.Cosmetica') AND c.name = 'OrigenProceso');
    IF @df IS NOT NULL EXEC('ALTER TABLE dbo.Cosmetica DROP CONSTRAINT ' + @df);
    ALTER TABLE dbo.Cosmetica DROP COLUMN OrigenProceso;
END
GO

-- SP de insercion con el tecnico asignado (@UsuarioAsignadoId = NULL si se elige 'NINGUNO')
CREATE OR ALTER PROCEDURE [dbo].[pa_InsertCosmetica]
    @Serial VARCHAR(50),
    @UsuarioId INT,
    @Fecha DATETIME,
    @UsuarioAsignadoId INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO Cosmetica(serialId,serial,mac,serial3,serial4,serial5,codigoSapId,palletId,nivelId,loteId,usuarioId,fecha,UsuarioAsignadoId)
    SELECT Id,Serial,Mac,Serial3,Serial4,Serial5,codigoSapId,palletId,nivelId,loteId,@UsuarioId,@Fecha,@UsuarioAsignadoId
    FROM Ingreso
    WHERE Serial = @Serial;
END
GO

/* ---------------------------------------------------------------------
   B3) REPARACION - pa_GetRepairUser sin depender de UsuarioSys
       Problema: el INNER JOIN con UsuarioSys descartaba el serial cuando el
       tecnico asignado no existia en UsuarioSys del cliente (lista de Asignacion vacia).
       Ahora toma el nombre de WmsWdGeneral.dbo.Usuario (LEFT JOIN) con el mismo
       alias nombre_usuario, asi la pantalla no cambia.
   --------------------------------------------------------------------- */
CREATE OR ALTER PROCEDURE [dbo].[pa_GetRepairUser]
@Perfil VARCHAR(20),
@UsuarioId INT
AS
BEGIN
	set nocount on;
	WITH UltimaReparacion AS (
		SELECT
			r.Id, r.SerialId, r.CodigoSapId, r.TecnicoAsignacionId, r.UsuarioId, r.FechaAsignacion,
			ROW_NUMBER() OVER (PARTITION BY r.SerialId ORDER BY r.FechaAsignacion DESC) AS rn
		FROM Reparacion r
		WHERE @Perfil = 'ADMINISTRADOR' OR r.UsuarioId = @UsuarioId
	)
	SELECT
		i.serial, i.mac, cs.Codigo, cs.descripcion, u.nombreUsuario AS nombre_usuario
	FROM UltimaReparacion ur
	INNER JOIN Ingreso i ON ur.SerialId = i.Id
	INNER JOIN WmsWdGeneral.dbo.Estado e ON i.estadoId = e.Id
	INNER JOIN CodigoSap cs ON ur.CodigoSapId = cs.Id
	LEFT JOIN WmsWdGeneral.dbo.Usuario u ON ur.TecnicoAsignacionId = u.id
	WHERE ur.rn = 1
	  AND e.Nombre = 'REPARACION-PREASIGNADO'
	ORDER BY ur.FechaAsignacion DESC
END
GO

/* PENDIENTE DE VERIFICAR (no se ha corregido): otros procedimientos que tambien
   cruzan con UsuarioSys y pueden dejar vacia la lista de Proceso del tecnico.
   Ejecutar y revisar uno por uno:

   SELECT OBJECT_NAME(object_id) AS procedimiento
   FROM sys.sql_modules
   WHERE definition LIKE '%UsuarioSys%';
*/


/* ---------------------------------------------------------------------
   B4) AUTOGESTION - fase 1  (contenido de autogestion_fase1.sql)
   B5) AUTOGESTION - aprobacion  (contenido de autogestion_aprobacion.sql)
       Ejecutar en ese orden, en la base de cada cliente.
   --------------------------------------------------------------------- */


/* ===================== B4) INICIO: autogestion_fase1.sql ===================== */

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


/* ===================== B5) INICIO: autogestion_aprobacion.sql ===================== */

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

