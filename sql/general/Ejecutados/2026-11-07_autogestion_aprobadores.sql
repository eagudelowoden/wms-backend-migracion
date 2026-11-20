/* =====================================================================================
   Aprobadores de Autogestión: correos que reciben el enlace para autorizar/rechazar una solicitud de movimiento.
   Tabla propia (no se mezcla con NotificacionCorreo). ClienteId NULL = todos los clientes.
   Base: WmsWdGeneral (una sola vez). Deja como aprobador inicial el que estaba en application.properties.
   ===================================================================================== */
USE WmsWdGeneral;
SET NOCOUNT ON;

IF OBJECT_ID('dbo.AutogestionAprobador', 'U') IS NULL
BEGIN
  CREATE TABLE dbo.AutogestionAprobador (
    Id            INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_AutogestionAprobador PRIMARY KEY,
    ClienteId     INT           NULL,
    Correo        VARCHAR(200)  NOT NULL,
    Nombre        VARCHAR(150)  NULL,
    Activo        BIT           NOT NULL CONSTRAINT DF_AutogestionAprobador_Activo DEFAULT (1),
    Usuario       VARCHAR(150)  NULL,
    Fecha         DATETIME      NOT NULL CONSTRAINT DF_AutogestionAprobador_Fecha DEFAULT (GETDATE()),
    FechaModifica DATETIME      NULL
  );
  CREATE INDEX IX_AutogestionAprobador_Cliente ON dbo.AutogestionAprobador (ClienteId, Activo);
END

IF NOT EXISTS (SELECT 1 FROM dbo.AutogestionAprobador WHERE Correo = 'eagudelo@woden.com.co' AND ClienteId IS NULL)
  INSERT INTO dbo.AutogestionAprobador (ClienteId, Correo, Nombre, Activo, Usuario)
  VALUES (NULL, 'eagudelo@woden.com.co', 'Daniel Agudelo', 1, 'script');
