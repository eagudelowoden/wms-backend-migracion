/* =====================================================================================
   Notificaciones por correo por evento y cliente (ClienteId NULL = todos), bitácora de envíos, módulo 44 y sonidos
   por cliente. Base: WmsWdGeneral. Dejo una regla inicial de NOVEDAD_ERROR_SERIAL_MAC a nsandino@woden.com.co.
   El SMTP lo lee el backend de las variables de entorno SMTP_*; el menú ADNOT lo agrego por base con
   2026-10-25_instalar_por_base.sql. Solo creo lo que falta.
   ===================================================================================== */
USE WmsWdGeneral;
SET NOCOUNT ON;

IF OBJECT_ID('dbo.NotificacionCorreo', 'U') IS NULL
  CREATE TABLE dbo.NotificacionCorreo (
    Id            INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_NotificacionCorreo PRIMARY KEY,
    Evento        VARCHAR(60)    NOT NULL,
    ClienteId     INT            NULL,
    Para          NVARCHAR(MAX)  NOT NULL,
    Cc            NVARCHAR(MAX)  NULL,
    Activo        BIT            NOT NULL CONSTRAINT DF_NotificacionCorreo_Activo DEFAULT (1),
    Usuario       VARCHAR(150)   NULL,
    Fecha         DATETIME       NOT NULL CONSTRAINT DF_NotificacionCorreo_Fecha DEFAULT (GETDATE()),
    FechaModifica DATETIME       NULL
  );

IF OBJECT_ID('dbo.NotificacionCorreoEnvio', 'U') IS NULL
BEGIN
  CREATE TABLE dbo.NotificacionCorreoEnvio (
    Id            INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_NotificacionCorreoEnvio PRIMARY KEY,
    Evento        VARCHAR(80)    NOT NULL,
    ClienteId     INT            NULL,
    Referencia    VARCHAR(100)   NULL,
    Destinatarios NVARCHAR(MAX)  NULL,
    Asunto        NVARCHAR(300)  NULL,
    Ok            BIT            NOT NULL CONSTRAINT DF_NotificacionCorreoEnvio_Ok DEFAULT (0),
    Error         NVARCHAR(1000) NULL,
    Usuario       VARCHAR(150)   NULL,
    Fecha         DATETIME       NOT NULL CONSTRAINT DF_NotificacionCorreoEnvio_Fecha DEFAULT (GETDATE())
  );
  CREATE INDEX IX_NotificacionCorreoEnvio_Cliente ON dbo.NotificacionCorreoEnvio (ClienteId, Fecha DESC);
END

IF NOT EXISTS (SELECT 1 FROM dbo.NotificacionCorreo WHERE Evento = 'NOVEDAD_ERROR_SERIAL_MAC')
  INSERT INTO dbo.NotificacionCorreo (Evento, ClienteId, Para, Cc, Activo, Usuario, Fecha)
  VALUES ('NOVEDAD_ERROR_SERIAL_MAC', NULL, 'nsandino@woden.com.co', NULL, 1, 'script 2026-10-25', GETDATE());

IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 44)
BEGIN
  SET IDENTITY_INSERT dbo.Modulo ON;
  INSERT INTO dbo.Modulo (Id, SeccionId, Nombre, Icono, Controlador) VALUES (44, 5, 'Notificaciones', '', 'notifications');
  SET IDENTITY_INSERT dbo.Modulo OFF;
END
ELSE IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 44 AND Nombre = 'Notificaciones')
  RAISERROR('El Id 44 de Modulo ya lo usa otro módulo: revisar antes de seguir.', 16, 1);

IF OBJECT_ID('dbo.NotificacionSonido', 'U') IS NULL
  CREATE TABLE dbo.NotificacionSonido (
    ClienteId     INT          NOT NULL CONSTRAINT PK_NotificacionSonido PRIMARY KEY,
    SonidoError   VARCHAR(30)  NOT NULL,
    SonidoDuplicado VARCHAR(30) NOT NULL,
    SonidoFallo   VARCHAR(30)  NOT NULL,
    SonidoOk      VARCHAR(30)  NOT NULL,
    Repetir       BIT          NOT NULL CONSTRAINT DF_NotificacionSonido_Repetir DEFAULT (1),
    Usuario       VARCHAR(150) NULL,
    Fecha         DATETIME     NOT NULL CONSTRAINT DF_NotificacionSonido_Fecha DEFAULT (GETDATE())
  );

