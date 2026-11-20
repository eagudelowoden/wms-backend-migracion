/* =====================================================================================
   Boletas de movimiento guardadas en Despacho (Reservar / Despachar / Cancelar) y su documento consecutivo por
   cliente, con el interruptor BOLETA_CONSECUTIVO. Base: WmsWdGeneral. Solo creo lo que falta.
   ===================================================================================== */
USE WmsWdGeneral;
SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'BOLETA_CONSECUTIVO')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('BOLETA_CONSECUTIVO', 'Boletas guardadas en Despacho (Reservar, Despachar y lista) con documento consecutivo AAAA-NNN que se reinicia cada año', 1, GETDATE());

IF OBJECT_ID('dbo.BoletaConsecutivo', 'U') IS NULL
  CREATE TABLE dbo.BoletaConsecutivo (
    ClienteId INT NOT NULL,
    Anio      INT NOT NULL,
    Ultimo    INT NOT NULL,
    CONSTRAINT PK_BoletaConsecutivo PRIMARY KEY (ClienteId, Anio)
  );

IF OBJECT_ID('dbo.BoletaMovimiento', 'U') IS NULL
  CREATE TABLE dbo.BoletaMovimiento (
    Id                   INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_BoletaMovimiento PRIMARY KEY,
    ClienteId            INT           NOT NULL,
    Documento            VARCHAR(20)   NOT NULL,
    Anio                 INT           NULL,
    Consecutivo          INT           NULL,
    FechaEntrega         DATE          NULL,
    Origen               VARCHAR(100)  NULL,
    Destino              VARCHAR(100)  NULL,
    Observaciones        VARCHAR(1000) NULL,
    Estado               VARCHAR(20)   NOT NULL CONSTRAINT DF_BoletaMovimiento_Estado DEFAULT ('RESERVADA'),
    UsuarioId            INT           NULL,
    UsuarioNombre        VARCHAR(150)  NULL,
    Fecha                DATETIME      NOT NULL CONSTRAINT DF_BoletaMovimiento_Fecha DEFAULT (GETDATE()),
    UsuarioDespachoId    INT           NULL,
    FechaDespacho        DATETIME      NULL,
    UsuarioCancelaId     INT           NULL,
    FechaCancela         DATETIME      NULL
  );

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_BoletaMovimiento_Cliente' AND object_id = OBJECT_ID('dbo.BoletaMovimiento'))
  CREATE INDEX IX_BoletaMovimiento_Cliente ON dbo.BoletaMovimiento (ClienteId, Estado, Fecha DESC);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_BoletaMovimiento_Consecutivo' AND object_id = OBJECT_ID('dbo.BoletaMovimiento'))
  CREATE UNIQUE INDEX UX_BoletaMovimiento_Consecutivo ON dbo.BoletaMovimiento (ClienteId, Anio, Consecutivo)
  WHERE Consecutivo IS NOT NULL;

IF OBJECT_ID('dbo.BoletaMovimientoPallet', 'U') IS NULL
  CREATE TABLE dbo.BoletaMovimientoPallet (
    Id          INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_BoletaMovimientoPallet PRIMARY KEY,
    BoletaId    INT           NOT NULL CONSTRAINT FK_BoletaMovimientoPallet_Boleta REFERENCES dbo.BoletaMovimiento (Id),
    ClienteId   INT           NOT NULL,
    PalletId    INT           NOT NULL,
    NoPallet    VARCHAR(50)   NULL,
    CodigoSap   VARCHAR(50)   NULL,
    Descripcion VARCHAR(200)  NULL,
    Cantidad    INT           NULL,
    Tipologia   VARCHAR(100)  NULL,
    Activo      BIT           NOT NULL CONSTRAINT DF_BoletaMovimientoPallet_Activo DEFAULT (1),
    Despachado  BIT           NOT NULL CONSTRAINT DF_BoletaMovimientoPallet_Despachado DEFAULT (0)
  );

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_BoletaMovimientoPallet_Boleta' AND object_id = OBJECT_ID('dbo.BoletaMovimientoPallet'))
  CREATE INDEX IX_BoletaMovimientoPallet_Boleta ON dbo.BoletaMovimientoPallet (BoletaId);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_BoletaMovimientoPallet_Activo' AND object_id = OBJECT_ID('dbo.BoletaMovimientoPallet'))
  CREATE UNIQUE INDEX UX_BoletaMovimientoPallet_Activo ON dbo.BoletaMovimientoPallet (ClienteId, PalletId)
  WHERE Activo = 1;
