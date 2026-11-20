-- Vistas del esquema de Telefonica.
-- En SQL Server eran vistas hacia WmsWdGeneral.dbo.*; en Postgres apuntan al esquema "general".
SET search_path TO wmswdtelefonicacolombiabog, public;

CREATE OR REPLACE VIEW cliente AS
SELECT id, nombre, colorcorporativo, activo FROM general.cliente;

CREATE OR REPLACE VIEW estado AS
SELECT id, nombre, descripcion, moduloid, icono, activo, padreinventario FROM general.estado;

CREATE OR REPLACE VIEW modulo AS
SELECT id, seccionid, nombre, icono, controlador FROM general.modulo;

CREATE OR REPLACE VIEW permiso AS
SELECT id, moduloid, nombre, descripcion FROM general.permiso;

CREATE OR REPLACE VIEW usuario AS
SELECT id, identificacion, nombres, apellidos, nombreusuario, clave, fechanacimiento, correo, cargoid, areaid,
       fechacreacion, fechaultimoacceso, ip, sedeid, activo
FROM general.usuario;

CREATE OR REPLACE VIEW vw_ingresovsempaque AS
SELECT COALESCE(i.id, e.serialid) AS serialid,
       i.id AS ingresoid,
       i.serial AS serialingreso,
       e.serialid AS empaqueserialid,
       e.serial AS serialempaque,
       i.palletid,
       i.cajaempaqueid,
       CASE WHEN i.id IS NULL THEN 'Solo Empaque'
            WHEN e.serialid IS NULL THEN 'Solo Ingreso'
            ELSE 'OK' END AS estado
FROM ingreso i
FULL OUTER JOIN empaque e
  ON i.id = e.serialid AND i.palletid = e.palletid AND i.cajaempaqueid = e.cajaempaqueid;
