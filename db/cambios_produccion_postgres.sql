/* =====================================================================
   Cambios de PRODUCCION (octubre 2026) llevados a PostgreSQL.
   Ejecutar por cada esquema de cliente (cambiar la primera linea).
   Ver tambien: db/autogestion_postgres.sql (modulo Autogestion completo).

   PENDIENTE (hay que traer la definicion desde SQL Server, ver LEEME de la migracion):
     - pa_SearchAuditoriaInSituEntry   (auditoria in-situ: lista de seriales)
     - pa_UpdateQualityExtrasBySerial  (campos extra de calidad)
     - pa_UpdateEstadoCalidadBySerial  (estado de calidad por serial)
   Mientras no existan, el backend ya llama a pa_searchauditoriainsituentry(),
   pa_updatequalityextrasbyserial(...) y pa_updateestadocalidadbyserial(...).
   ===================================================================== */
SET search_path TO wmswdlegacycostarica, public;

-- 1) Cosmetica: nuevo campo "usuario asignado" (como Clasificacion.usuarioasignadoid) ---------
ALTER TABLE cosmetica ADD COLUMN IF NOT EXISTS usuarioasignadoid integer;

DROP FUNCTION IF EXISTS pa_insertcosmetica(character varying, integer, timestamp without time zone);
DROP FUNCTION IF EXISTS pa_insertcosmetica(character varying, integer, timestamp without time zone, integer);
CREATE OR REPLACE FUNCTION pa_insertcosmetica(
    p_serial character varying, p_usuarioid integer, p_fecha timestamp without time zone,
    p_usuarioasignadoid integer DEFAULT NULL)
RETURNS void
LANGUAGE plpgsql
SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO Cosmetica(serialid,serial,mac,serial3,serial4,serial5,codigosapid,palletid,nivelid,loteid,usuarioid,fecha,usuarioasignadoid)
            SELECT id,serial,mac,serial3,serial4,serial5,codigosapid,palletid,nivelid,loteid,p_usuarioid,p_fecha,p_usuarioasignadoid FROM ingreso
            WHERE serial =p_serial;
END;
$function$;

-- ============================================================================
-- 2) Equipos depreciados + extras de calidad + modelo de ingreso en despacho
--    (cambios de produccion del 27-28 de octubre)
-- ============================================================================

-- 2a) Calidad: campos extra (Personal, DNI, Voltaje, Observaciones)
ALTER TABLE calidad ADD COLUMN IF NOT EXISTS personal      varchar(100);
ALTER TABLE calidad ADD COLUMN IF NOT EXISTS dni           varchar(50);
ALTER TABLE calidad ADD COLUMN IF NOT EXISTS voltaje       varchar(50);
ALTER TABLE calidad ADD COLUMN IF NOT EXISTS observaciones varchar(500);

-- 2b) Despacho: modelo de ingreso
ALTER TABLE despacho ADD COLUMN IF NOT EXISTS modeloid integer;

DROP FUNCTION IF EXISTS pa_insertdispatch(integer, character varying, character varying, integer, integer, integer, integer, integer, integer, integer, integer, integer, character varying, character varying, character varying, character varying, integer, integer, character varying, integer, timestamp without time zone, character varying, integer, character varying, integer, character varying, integer, character varying, integer, integer, character varying, character varying);
CREATE OR REPLACE FUNCTION pa_insertdispatch(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_palletid integer, p_palletidingreso integer, p_cajadespachoid integer, p_estadoid integer, p_tipoorigenid integer, p_origenid integer, p_tipologiaid integer, p_nivelid integer, p_tramite character varying, p_documento character varying, p_guia character varying, p_falla character varying, p_prealertaid integer, p_cruce integer, p_novedad character varying, p_usuarioid integer, p_fechaingreso timestamp without time zone, p_pedidosap character varying, p_smartcardid integer, p_smartcard character varying, p_loteid integer, p_serial3 character varying, p_cajaingresoid integer, p_numerosmartcard character varying, p_fallacosmeticaid integer, p_fallafuncionalid integer, p_causa character varying, p_observaciones character varying DEFAULT NULL::character varying, p_modeloid integer DEFAULT NULL)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Despacho(serialid,serial,mac, codigosapid, palletid, palletidingreso, cajadespachoid, estadoid, tipoorigenid, origenid, tipologiaid, nivelid, tramite, documento, guia, falla, prealertaid, cruce, novedad, usuarioid, fechaingreso, fecha, pedidosap, smartcardid, smartcard, loteid, serial3, cajaingresoid, numerosmartcard, fallacosmeticaid, fallafuncionalid, causa, modeloid)
    VALUES(p_serialid, p_serial, p_mac, p_codigosapid, p_palletid, p_palletidingreso, p_cajadespachoid, p_estadoid,
           p_tipoorigenid, p_origenid, p_tipologiaid, p_nivelid, p_tramite, p_documento, p_guia, p_falla, p_prealertaid,
           (p_cruce)::boolean, p_novedad, p_usuarioid, p_fechaingreso, LOCALTIMESTAMP, p_pedidosap, p_smartcardid,
           p_smartcard, p_loteid, p_serial3, p_cajaingresoid, p_numerosmartcard, p_fallacosmeticaid, p_fallafuncionalid,
           p_causa, p_modeloid);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$;

-- 2c) Depreciados: ASUMIDA (el DDL real de SQL Server no esta en el repo). Reemplazar por el DDL real si difiere.
CREATE TABLE IF NOT EXISTS depreciados (
    serialnumber varchar(100) NOT NULL
);
CREATE INDEX IF NOT EXISTS ix_depreciados_serialnumber ON depreciados (serialnumber);

-- 2d) Registrar en general los codigos de validacion de depreciacion (una sola vez, ver columnas reales de
--     general.clientevalidaciontipo; ejemplo comentado):
-- INSERT INTO general.clientevalidaciontipo (codigo, descripcion, activo)
-- VALUES ('MODO_DEPRECIACION_CUALQUIERA', 'Depreciar si esta en la lista O tiene 3+ reingresos', true),
--        ('MODO_DEPRECIACION_AMBAS',      'Depreciar si esta en la lista Y tiene 3+ reingresos', true);

-- 2e) PENDIENTES (definicion en SQL Server): pa_searchqualityentryextras(integer) y
--     pa_searchauditoriainsituentry(integer). El backend ya las llama.
