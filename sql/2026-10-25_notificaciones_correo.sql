/* =====================================================================================
   Administración > Notificaciones: reglas de correo por evento y cliente + bitácora de envíos.
   - Tablas en WmsWdGeneral (ClienteId NULL = todos los clientes)
   - Regla inicial: NOVEDAD_ERROR_SERIAL_MAC para todos los clientes -> nsandino@woden.com.co
   - Opción de menú ADNOT "Notificaciones" bajo Administración en cada base cliente que tenga el menú ADM
     (si el cliente usa Menú por perfiles, asignarla a los perfiles que correspondan)
   El SMTP no va en la base: el backend lo lee de las variables de entorno
   SMTP_SERVER, SMTP_PORT, SMTP_USER, SMTP_PASSWORD, SMTP_FROM_NAME (cuenta noreplyservices).
   Solo crea lo que falta; correrlo dos veces no hace nada.
   ===================================================================================== */
USE WmsWdGeneral;
SET NOCOUNT ON;

IF OBJECT_ID('dbo.NotificacionCorreo', 'U') IS NULL
  CREATE TABLE dbo.NotificacionCorreo (
    Id            INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_NotificacionCorreo PRIMARY KEY,
    Evento        VARCHAR(60)    NOT NULL,
    ClienteId     INT            NULL,          -- NULL = todos los clientes
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

-- Opción de menú en cada base cliente (dbo.Menu con el menú Administración 'ADM')
DECLARE @db SYSNAME, @sql NVARCHAR(MAX);
DECLARE c CURSOR LOCAL FAST_FORWARD FOR
  SELECT name FROM sys.databases WHERE name LIKE 'WmsWd%' AND name <> 'WmsWdGeneral' AND state = 0 ORDER BY name;
OPEN c;
FETCH NEXT FROM c INTO @db;
WHILE @@FETCH_STATUS = 0
BEGIN
  SET @sql = N'
    IF OBJECT_ID(''' + QUOTENAME(@db) + N'.dbo.Menu'', ''U'') IS NOT NULL
       AND EXISTS (SELECT 1 FROM ' + QUOTENAME(@db) + N'.dbo.Menu WHERE Id = ''ADM'')
       AND NOT EXISTS (SELECT 1 FROM ' + QUOTENAME(@db) + N'.dbo.Menu WHERE Id = ''ADNOT'')
    BEGIN
      INSERT INTO ' + QUOTENAME(@db) + N'.dbo.Menu (Id, Descripcion, Tipo, Orden, Accion, Estado, Icono, Id_padre, View_name, Controller_name)
      VALUES (''ADNOT'', ''Notificaciones'', ''item'', 20, ''ADMNOT'', ''activo'', '''', ''ADM'', '''', '''');
      PRINT ''' + @db + N': menú Notificaciones agregado'';
    END';
  BEGIN TRY
    EXEC sys.sp_executesql @sql;
  END TRY
  BEGIN CATCH
    PRINT @db + ': ' + ERROR_MESSAGE();
  END CATCH
  FETCH NEXT FROM c INTO @db;
END
CLOSE c;
DEALLOCATE c;
