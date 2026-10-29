/* =====================================================================================
   Parámetros por cliente que reemplazan reglas metidas en SPs de algunas bases.  Base: WmsWdGeneral.
   Interruptores nuevos (Clientes > Parámetros), apagados para todos hasta que se activen por cliente:
     LIMPIEZA_CARACTERES_INGRESO  quita en Serial, MAC y Serial3 los textos del maestro "Limpieza Caracteres"
     APP_PQRS_INGRESO             al ingresar, vincula el ticket PQRS pendiente del serial y lo pasa a En proceso
     DIAGNOSTICO_REPORTE34        Diagnóstico exige el serial en BaseEtiquetado y Serial3 en los códigos del
                                  maestro "Diagnostico Serial3"
     ETIQUETADO_FORECAST          Etiquetado muestra tipo de empaque, lote, año y fecha del Forecast (los cambia
                                  administrador o técnico de etiquetado)
     SERIAL_DISTINTO_MAC          Ingreso no acepta serial igual a la MAC (salvo smartcards)
     MODELOID_DESDE_CODIGOSAP     Ingreso sin modelo toma el ModeloId del código SAP (Claro Ecuador)
     SCRAP_MULTIMODELO_ENVIO      al enviar un pallet MULTIMODELO con tipología SCRAP sus equipos quedan SCRAP
                                  (pa_SendEntry de Colombia)
   Tablas nuevas:
     CodigoSapRegla    campos calculados por código SAP (MAC, Serial3-5 al ingresar; Variable1-4 en etiquetado)
     ClienteConfig     ajustes simples por cliente (modo de la Base Almacén, etiquetas con lectura variable)
     BaseAlmacen       base SAP de Almacén por cliente (Cargues > BASE ALMACEN)
     VariableEtiqueta  variables propias de las etiquetas (Administración > Variables de etiquetas)
   Módulo 48 "Variables de etiquetas" (Administración); el menú ADVAR va por base (2026-10-25_instalar_por_base.sql).
   Solo crea lo que falta; correrlo dos veces no hace nada.
   ===================================================================================== */
USE WmsWdGeneral;
SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'LIMPIEZA_CARACTERES_INGRESO')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('LIMPIEZA_CARACTERES_INGRESO', 'Ingreso: quita en Serial, MAC y Serial3 los textos del maestro Limpieza Caracteres', 1, GETDATE());

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'APP_PQRS_INGRESO')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('APP_PQRS_INGRESO', 'Ingreso: vincula el ticket PQRS pendiente del serial (App_PQRS_Tickets) y lo pasa a En proceso', 1, GETDATE());

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'DIAGNOSTICO_REPORTE34')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('DIAGNOSTICO_REPORTE34', 'Diagnóstico: el serial debe estar en BaseEtiquetado (Reporte34) y los códigos del maestro Diagnostico Serial3 exigen Serial3', 1, GETDATE());

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'ETIQUETADO_FORECAST')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('ETIQUETADO_FORECAST', 'Etiquetado: tipo de empaque, lote, año y fecha del Forecast en la ventana (los cambia administrador o técnico de etiquetado)', 1, GETDATE());

IF OBJECT_ID('dbo.CodigoSapRegla', 'U') IS NULL
BEGIN
  CREATE TABLE dbo.CodigoSapRegla (
    Id          INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_CodigoSapRegla PRIMARY KEY,
    ClienteId   INT           NOT NULL,
    CodigoSapId INT           NOT NULL,
    Campo       VARCHAR(20)   NOT NULL,
    Origen      VARCHAR(20)   NOT NULL,
    Operacion   VARCHAR(20)   NOT NULL,
    Parametro1  VARCHAR(100)  NULL,
    Parametro2  VARCHAR(100)  NULL,
    Momento     VARCHAR(15)   NOT NULL CONSTRAINT DF_CodigoSapRegla_Momento DEFAULT ('INGRESO'),
    Activo      BIT           NOT NULL CONSTRAINT DF_CodigoSapRegla_Activo DEFAULT (1),
    Usuario     VARCHAR(150)  NULL,
    Fecha       DATETIME      NOT NULL CONSTRAINT DF_CodigoSapRegla_Fecha DEFAULT (GETDATE())
  );
  CREATE UNIQUE INDEX UX_CodigoSapRegla ON dbo.CodigoSapRegla (ClienteId, CodigoSapId, Campo);
END
ELSE IF COL_LENGTH('dbo.CodigoSapRegla', 'Momento') IS NULL
  ALTER TABLE dbo.CodigoSapRegla ADD Momento VARCHAR(15) NOT NULL CONSTRAINT DF_CodigoSapRegla_Momento DEFAULT ('INGRESO');

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'SERIAL_DISTINTO_MAC')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('SERIAL_DISTINTO_MAC', 'Ingreso: el serial no puede ser igual a la MAC (no aplica a smartcards)', 1, GETDATE());

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'MODELOID_DESDE_CODIGOSAP')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('MODELOID_DESDE_CODIGOSAP', 'Ingreso: si el equipo llega sin modelo toma el ModeloId del código SAP', 1, GETDATE());

IF NOT EXISTS (SELECT 1 FROM dbo.ClienteValidacionTipo WHERE Codigo = 'SCRAP_MULTIMODELO_ENVIO')
  INSERT INTO dbo.ClienteValidacionTipo (Codigo, Descripcion, Activo, Fecha)
  VALUES ('SCRAP_MULTIMODELO_ENVIO', 'Envío de Ingreso: un pallet MULTIMODELO con tipología SCRAP deja sus equipos en SCRAP', 1, GETDATE());

IF OBJECT_ID('dbo.ClienteConfig', 'U') IS NULL
  CREATE TABLE dbo.ClienteConfig (
    ClienteId INT           NOT NULL,
    Clave     VARCHAR(80)   NOT NULL,
    Valor     NVARCHAR(MAX) NULL,
    Usuario   VARCHAR(150)  NULL,
    Fecha     DATETIME      NOT NULL CONSTRAINT DF_ClienteConfig_Fecha DEFAULT (GETDATE()),
    CONSTRAINT PK_ClienteConfig PRIMARY KEY (ClienteId, Clave)
  );

IF OBJECT_ID('dbo.BaseAlmacen', 'U') IS NULL
BEGIN
  CREATE TABLE dbo.BaseAlmacen (
    Id        INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_BaseAlmacen PRIMARY KEY,
    ClienteId INT           NOT NULL,
    Serial    VARCHAR(100)  NOT NULL,
    CodigoSap VARCHAR(100)  NULL,
    EstadoSap VARCHAR(100)  NULL,
    EstadoRR  VARCHAR(100)  NULL,
    Lote      VARCHAR(100)  NULL,
    Fecha     DATETIME      NOT NULL CONSTRAINT DF_BaseAlmacen_Fecha DEFAULT (GETDATE())
  );
  CREATE INDEX IX_BaseAlmacen_Serial ON dbo.BaseAlmacen (ClienteId, Serial);
END

IF OBJECT_ID('dbo.VariableEtiqueta', 'U') IS NULL
BEGIN
  CREATE TABLE dbo.VariableEtiqueta (
    Id          INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_VariableEtiqueta PRIMARY KEY,
    ClienteId   INT            NOT NULL,
    Nombre      VARCHAR(50)    NOT NULL,
    Descripcion NVARCHAR(200)  NULL,
    Aplica      VARCHAR(20)    NOT NULL,
    Origen      VARCHAR(20)    NOT NULL,
    Operacion   VARCHAR(20)    NOT NULL,
    Parametro1  VARCHAR(100)   NULL,
    Parametro2  VARCHAR(100)   NULL,
    Consulta    NVARCHAR(MAX)  NULL,
    Activo      BIT            NOT NULL CONSTRAINT DF_VariableEtiqueta_Activo DEFAULT (1),
    Usuario     VARCHAR(150)   NULL,
    Fecha       DATETIME       NOT NULL CONSTRAINT DF_VariableEtiqueta_Fecha DEFAULT (GETDATE())
  );
  CREATE UNIQUE INDEX UX_VariableEtiqueta ON dbo.VariableEtiqueta (ClienteId, Nombre);
END

IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 48)
BEGIN
  SET IDENTITY_INSERT dbo.Modulo ON;
  INSERT INTO dbo.Modulo (Id, SeccionId, Nombre, Icono, Controlador) VALUES (48, 5, 'Variables de etiquetas', '', 'labelvariables');
  SET IDENTITY_INSERT dbo.Modulo OFF;
END
ELSE IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 48 AND Nombre = 'Variables de etiquetas')
  RAISERROR('El Id 48 de Modulo ya lo usa otro módulo: revisar antes de seguir.', 16, 1);
