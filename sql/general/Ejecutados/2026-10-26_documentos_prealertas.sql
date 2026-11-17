/* =====================================================================================
   Plantillas editables por cliente (Administración > Documentos, módulo 47), registro de cierres de prealerta,
   interruptores PREALERTA_CIERRE_MANUAL, PREALERTA_SOBRANTES, INSERT_AUTOMATICO_SMARTCARD y EMPAQUE_CODIGOSAP_PALLET,
   y volumen, pulso y pausa de los sonidos. Base: WmsWdGeneral. No creo reglas de correo: hasta configurarlas en
   Notificaciones no se envía nada. Solo creo lo que falta.
   ===================================================================================== */
USE WmsWdGeneral;
SET NOCOUNT ON;

IF OBJECT_ID('dbo.DocumentoPlantilla', 'U') IS NULL
  CREATE TABLE dbo.DocumentoPlantilla (
    Id        INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_DocumentoPlantilla PRIMARY KEY,
    ClienteId INT           NOT NULL,
    Tipo      VARCHAR(40)   NOT NULL,
    Config    NVARCHAR(MAX) NOT NULL,
    Activo    BIT           NOT NULL CONSTRAINT DF_DocumentoPlantilla_Activo DEFAULT (1),
    Usuario   VARCHAR(150)  NULL,
    Fecha     DATETIME      NOT NULL CONSTRAINT DF_DocumentoPlantilla_Fecha DEFAULT (GETDATE()),
    CONSTRAINT UX_DocumentoPlantilla UNIQUE (ClienteId, Tipo)
  );

IF OBJECT_ID('dbo.PrealertaCierre', 'U') IS NULL
BEGIN
  CREATE TABLE dbo.PrealertaCierre (
    Id          INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PrealertaCierre PRIMARY KEY,
    ClienteId   INT           NOT NULL,
    PrealertaId INT           NOT NULL,
    Prealerta   VARCHAR(50)   NULL,
    Modo        VARCHAR(10)   NOT NULL,
    Esperados   INT           NULL,
    Recibidos   INT           NULL,
    Faltantes   INT           NULL,
    Sobrantes   INT           NULL,
    Novedades   INT           NULL,
    Observacion NVARCHAR(500) NULL,
    Usuario     VARCHAR(150)  NULL,
    Fecha       DATETIME      NOT NULL CONSTRAINT DF_PrealertaCierre_Fecha DEFAULT (GETDATE())
  );
  CREATE INDEX IX_PrealertaCierre_Cliente ON dbo.PrealertaCierre (ClienteId, PrealertaId);
END

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'PREALERTA_CIERRE_MANUAL')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('PREALERTA_CIERRE_MANUAL', 'Botón Cerrar prealerta en Ingreso aunque no se haya recibido el 100 %', 1, GETDATE());

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'PREALERTA_SOBRANTES')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('PREALERTA_SOBRANTES', 'Ingreso sin prealerta: campo Sobrante de para marcar equipos que llegaron con una prealerta sin estar en ella', 1, GETDATE());

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'INSERT_AUTOMATICO_SMARTCARD')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('INSERT_AUTOMATICO_SMARTCARD', 'Ingreso: el Serial3 (smartcard) se crea también en Ingreso y se borra con su equipo si nadie más la usa', 1, GETDATE());

IF OBJECT_ID('dbo.SmartcardAutomatica', 'U') IS NULL
  CREATE TABLE dbo.SmartcardAutomatica (
    ClienteId INT          NOT NULL CONSTRAINT PK_SmartcardAutomatica PRIMARY KEY,
    CodigoSap VARCHAR(50)  NOT NULL,
    Destino   VARCHAR(50)  NOT NULL CONSTRAINT DF_SmartcardAutomatica_Destino DEFAULT ('SMARTCARD'),
    Usuario   VARCHAR(150) NULL,
    Fecha     DATETIME     NOT NULL CONSTRAINT DF_SmartcardAutomatica_Fecha DEFAULT (GETDATE())
  );

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'EMPAQUE_CODIGOSAP_PALLET')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('EMPAQUE_CODIGOSAP_PALLET', 'Empaque: se empacan series de otro código SAP y el empaque queda con el código SAP del pallet (no aplica a MULTIMODELO)', 1, GETDATE());

IF OBJECT_ID('dbo.NotificacionSonido', 'U') IS NOT NULL AND COL_LENGTH('dbo.NotificacionSonido', 'Volumen') IS NULL
  ALTER TABLE dbo.NotificacionSonido ADD Volumen INT NULL, Pulso INT NULL, Pausa INT NULL;

UPDATE dbo.ClienteValidacionTipo
   SET Descripcion = 'Sonido en los avisos y errores del WMS en todos los módulos (se configura en Administración > Notificaciones)'
 WHERE Codigo = 'SONIDO_ALERTAS_INGRESO'
   AND Descripcion <> 'Sonido en los avisos y errores del WMS en todos los módulos (se configura en Administración > Notificaciones)';

IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 47)
BEGIN
  SET IDENTITY_INSERT dbo.Modulo ON;
  INSERT INTO dbo.Modulo (Id, SeccionId, Nombre, Icono, Controlador) VALUES (47, 5, 'Documentos', '', 'documents');
  SET IDENTITY_INSERT dbo.Modulo OFF;
END
ELSE IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 47 AND Nombre = 'Documentos')
  RAISERROR('El Id 47 de Modulo ya lo usa otro módulo: revisar antes de seguir.', 16, 1);

