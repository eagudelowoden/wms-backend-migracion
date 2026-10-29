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
