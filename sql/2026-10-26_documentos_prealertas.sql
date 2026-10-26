/* =====================================================================================
   Administración > Documentos y cierre de prealertas.  Base: WmsWdGeneral.
   - DocumentoPlantilla: plantilla editable por cliente de la boleta de entrega, la boleta de pallet y el correo de
     prealerta (sin fila = el documento sale igual que siempre)
   - PrealertaCierre: registro de cada cierre de prealerta (automático al 100 % o manual) con su resultado
   - Interruptores por cliente (Clientes > Validaciones):
       PREALERTA_CIERRE_MANUAL  botón "Cerrar prealerta" en Ingreso aunque no esté al 100 %
       PREALERTA_SOBRANTES      campo "Sobrante de" en Ingreso (sin prealerta) para marcar equipos sobrantes
   - Módulo 47 "Documentos" (Administración); el menú ADDOC va por base (2026-10-25_instalar_por_base.sql)
   - SONIDO_ALERTAS_INGRESO ahora suena en todos los módulos: se actualiza su descripción
   Los destinatarios de los correos de prealerta se configuran en Administración > Notificaciones (no se crea
   ninguna regla: hasta que se configure no se envía nada).
   Solo crea lo que falta; correrlo dos veces no hace nada.
   ===================================================================================== */
USE WmsWdGeneral;
SET NOCOUNT ON;

IF OBJECT_ID('dbo.DocumentoPlantilla', 'U') IS NULL
  CREATE TABLE dbo.DocumentoPlantilla (
    Id        INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_DocumentoPlantilla PRIMARY KEY,
    ClienteId INT           NOT NULL,
    Tipo      VARCHAR(40)   NOT NULL,          -- BOLETA_ENTREGA | PALLET | CORREO_PREALERTA
    Config    NVARCHAR(MAX) NOT NULL,          -- JSON del editor (logos en base64)
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
    Modo        VARCHAR(10)   NOT NULL,        -- AUTO | MANUAL
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

UPDATE dbo.ClienteValidacionTipo
   SET Descripcion = 'Sonido en los avisos y errores del WMS en todos los módulos (se configura en Administración > Notificaciones)'
 WHERE Codigo = 'SONIDO_ALERTAS_INGRESO'
   AND Descripcion <> 'Sonido en los avisos y errores del WMS en todos los módulos (se configura en Administración > Notificaciones)';

-- Módulo Documentos (Id 47, sección Administración); el backend lo asocia al menú ADDOC
IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 47)
BEGIN
  SET IDENTITY_INSERT dbo.Modulo ON;
  INSERT INTO dbo.Modulo (Id, SeccionId, Nombre, Icono, Controlador) VALUES (47, 5, 'Documentos', '', 'documents');
  SET IDENTITY_INSERT dbo.Modulo OFF;
END
ELSE IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 47 AND Nombre = 'Documentos')
  RAISERROR('El Id 47 de Modulo ya lo usa otro módulo: revisar antes de seguir.', 16, 1);

-- La opción de menú ADDOC "Documentos" se agrega por base con 2026-10-25_instalar_por_base.sql
