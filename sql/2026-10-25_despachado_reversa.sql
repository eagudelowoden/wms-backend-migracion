/* =====================================================================================
   Despacho > Despachado: respaldo de lo que se devuelve a Novedades (rechazo de cliente).
   Lo borrado de Despacho / Empaque / DespachoAccesorio queda aqui en JSON.  Base: WmsWdGeneral.
   Solo crea lo que falta; correrlo dos veces no hace nada.
   ===================================================================================== */
USE WmsWdGeneral;
SET NOCOUNT ON;

IF OBJECT_ID('dbo.DespachoReversa', 'U') IS NULL
BEGIN
  CREATE TABLE dbo.DespachoReversa (
    Id             INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_DespachoReversa PRIMARY KEY,
    ClienteId      INT            NOT NULL,
    Db             NVARCHAR(128)  NOT NULL,
    Serial         VARCHAR(100)   NOT NULL,
    SerialId       INT            NOT NULL,
    DespachoId     INT            NOT NULL,
    PedidoSap      VARCHAR(50)    NULL,
    FechaDespacho  DATETIME       NULL,
    PalletDespacho VARCHAR(50)    NULL,
    Motivo         VARCHAR(300)   NOT NULL,
    DespachoJson   NVARCHAR(MAX)  NULL,
    EmpaqueJson    NVARCHAR(MAX)  NULL,
    UsuarioId      INT            NULL,
    UsuarioNombre  VARCHAR(150)   NULL,
    Fecha          DATETIME       NOT NULL CONSTRAINT DF_DespachoReversa_Fecha DEFAULT (GETDATE())
  );
  CREATE INDEX IX_DespachoReversa_Cliente ON dbo.DespachoReversa (ClienteId, Serial);
END

IF OBJECT_ID('dbo.DespachoReversaAcc', 'U') IS NULL
BEGIN
  CREATE TABLE dbo.DespachoReversaAcc (
    Id             INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_DespachoReversaAcc PRIMARY KEY,
    ClienteId      INT            NOT NULL,
    Db             NVARCHAR(128)  NOT NULL,
    PedidoSap      VARCHAR(50)    NOT NULL,
    CodigoSap      VARCHAR(50)    NULL,
    Modelo         VARCHAR(200)   NULL,
    TipoAccesorio  VARCHAR(50)    NULL,
    EstadoLimpieza VARCHAR(50)    NULL,
    Cantidad       INT            NOT NULL,
    AccesorioIds   NVARCHAR(MAX)  NULL,
    Motivo         VARCHAR(300)   NOT NULL,
    DespachoJson   NVARCHAR(MAX)  NULL,
    PalletNovedad  VARCHAR(50)    NULL,
    UsuarioId      INT            NULL,
    UsuarioNombre  VARCHAR(150)   NULL,
    Fecha          DATETIME       NOT NULL CONSTRAINT DF_DespachoReversaAcc_Fecha DEFAULT (GETDATE())
  );
  CREATE INDEX IX_DespachoReversaAcc_Cliente ON dbo.DespachoReversaAcc (ClienteId, PedidoSap);
END
