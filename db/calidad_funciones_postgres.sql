/* =====================================================================================
   CALIDAD (auditoria in-situ y campos extra) - funciones para PostgreSQL. POR ESQUEMA DE CLIENTE.
   Reemplazan a pa_SearchAuditoriaInSituEntry, pa_SearchQualityEntryExtras, pa_UpdateQualityExtrasBySerial y
   pa_UpdateEstadoCalidadBySerial. Requiere las columnas calidad.personal/dni/voltaje/observaciones (script 01).

   OJO: las definiciones de SQL Server no estaban en ningun archivo del repositorio, asi que estas funciones se
   reconstruyeron a partir de lo que el backend y la pantalla esperan (columnas, parametros y uso). Confirmar contra los
   SP originales; los criterios que se asumieron estan marcados con "ASUMIDO".
   Se puede re-ejecutar. Ejecutar completo como script (DBeaver: Alt+X).
   ===================================================================================== */
SET search_path TO wmswdlegacycostarica, public;

-- 1) Seriales por calificar (pantalla Calidad > Seriales). Igual que pa_searchqualityentry y, con p_extras = 1,
--    agrega personal, dni, voltaje y observaciones del ultimo registro de calidad del serial.
DROP FUNCTION IF EXISTS pa_searchqualityentryextras(integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchqualityentryextras(p_extras integer DEFAULT 0)
 RETURNS TABLE(serial text, mac text, pallet text, caja text, smartcard text,
               personal text, dni text, voltaje text, observaciones text)
 LANGUAGE plpgsql
 STABLE
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT i.serial::text, i.mac::text, p.numero::text, ce.numero::text, i.smartcard::text,
           x.personal::text, x.dni::text, x.voltaje::text, x.observaciones::text
    FROM ingreso i
    INNER JOIN pallet p ON i.palletid = p.id
    INNER JOIN cajaempaque ce ON i.cajaempaqueid = ce.id
    INNER JOIN general.estado e ON i.estadoid = e.id
    LEFT JOIN LATERAL (
        SELECT c.personal, c.dni, c.voltaje, c.observaciones
        FROM calidad c
        WHERE p_extras = 1 AND c.serialid = i.id
        ORDER BY c.id DESC
        LIMIT 1
    ) x ON true
    WHERE e.nombre = 'CALIFICADO';
END;
$function$;

-- 2) Listado de la auditoria in-situ (Calidad > Auditoria). Columnas, en el orden que lee el backend:
--    serial, mac, pallet, caja, smartcard, codigosap, codigosapid, estado (localizacion), falla funcional (codigo),
--    falla cosmetica (codigo), estado final (codigo) y, con p_extras = 1, personal, dni, voltaje, observaciones.
--    ASUMIDO: la lista son los seriales en estado CALIFICADO (la misma cola de Calidad > Seriales), sin exigir pallet ni
--    caja (in-situ: el equipo puede no estar en una caja), con los datos del ultimo registro de calidad si existe.
--    Para auditar otros estados, cambiar el WHERE.
DROP FUNCTION IF EXISTS pa_searchauditoriainsituentry(integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchauditoriainsituentry(p_extras integer DEFAULT 0)
 RETURNS TABLE(serial text, mac text, pallet text, caja text, smartcard text, codigosap text, codigosapid text,
               estado text, fallafuncionalcodigo text, fallacosmeticacodigo text, estadofinalcodigo text,
               personal text, dni text, voltaje text, observaciones text)
 LANGUAGE plpgsql
 STABLE
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT i.serial::text, i.mac::text, p.numero::text, ce.numero::text, i.smartcard::text,
           cs.codigo::text, i.codigosapid::text, e.nombre::text,
           mf.codigo::text, mc.codigo::text,
           (CASE WHEN COALESCE(c.estadofinalid, 0) = 0 THEN NULL ELSE me.codigo END)::text,
           (CASE WHEN p_extras = 1 THEN c.personal END)::text,
           (CASE WHEN p_extras = 1 THEN c.dni END)::text,
           (CASE WHEN p_extras = 1 THEN c.voltaje END)::text,
           (CASE WHEN p_extras = 1 THEN c.observaciones END)::text
    FROM ingreso i
    INNER JOIN general.estado e ON i.estadoid = e.id
    LEFT JOIN pallet p ON i.palletid = p.id
    LEFT JOIN cajaempaque ce ON i.cajaempaqueid = ce.id
    LEFT JOIN codigosap cs ON cs.id = i.codigosapid
    LEFT JOIN LATERAL (
        SELECT c1.fallafuncionalid, c1.fallacosmeticaid, c1.estadofinalid,
               c1.personal, c1.dni, c1.voltaje, c1.observaciones
        FROM calidad c1
        WHERE c1.serialid = i.id
        ORDER BY c1.id DESC
        LIMIT 1
    ) c ON true
    LEFT JOIN maestro mf ON mf.id = c.fallafuncionalid
    LEFT JOIN maestro mc ON mc.id = c.fallacosmeticaid
    LEFT JOIN maestro me ON me.id = c.estadofinalid
    WHERE e.nombre = 'CALIFICADO'
    ORDER BY i.id;
END;
$function$;

-- 3) Guarda los campos extra en el ultimo registro de calidad del serial. Devuelve las filas actualizadas (0 o 1).
DROP FUNCTION IF EXISTS pa_updatequalityextrasbyserial(character varying, character varying, character varying, character varying, character varying);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updatequalityextrasbyserial(
    p_serial character varying, p_personal character varying, p_dni character varying,
    p_voltaje character varying, p_observaciones character varying)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_filas integer;
BEGIN
    UPDATE calidad
    SET personal = p_personal,
        dni = p_dni,
        voltaje = p_voltaje,
        observaciones = p_observaciones
    WHERE id = (SELECT max(c.id) FROM calidad c WHERE c.serial = p_serial);
    GET DIAGNOSTICS v_filas = ROW_COUNT;
    RETURN v_filas;
END;
$function$;

-- 4) Estado de calidad (estado final) del ultimo registro de calidad del serial.
--    ASUMIDO: estadoCalidadId es el id del maestro que va en calidad.estadofinalid (el mismo que se lee como
--    "estadoFinalCodigo" en getAuditoriaBySerial).
DROP FUNCTION IF EXISTS pa_updateestadocalidadbyserial(character varying, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updateestadocalidadbyserial(p_serial character varying, p_estadocalidadid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE calidad
    SET estadofinalid = p_estadocalidadid
    WHERE id = (SELECT max(c.id) FROM calidad c WHERE c.serial = p_serial);
END;
$function$;
