-- Funciones del esquema de cliente que NO vinieron en la exportacion (procedimientos de SQL Server sin convertir).
-- Convertidas a mano desde sys.sql_modules. Una copia por cliente: cambiar 'wmswdlegacycostarica' por el esquema.
-- IMPORTANTE: cada funcion lleva un DROP IF EXISTS previo porque Postgres no permite cambiar el tipo de
--   retorno con CREATE OR REPLACE si ya existe una version con otra forma de resultado.
-- Notas de conversion:
--   * WmsWdGeneral.dbo.Estado  -> general.estado ;  Estado (sin prefijo) -> estado del esquema del cliente.
--   * GETDATE() -> LOCALTIMESTAMP ; TOP n -> LIMIT n ; OUTER APPLY -> LEFT JOIN LATERAL ; ISNULL -> COALESCE.
--   * Los bucles por lotes con WAITFOR DELAY (pa_SendNoveltyEntry) se resuelven con un UPDATE unico.
--   * Las funciones devuelven tipos reales (integer, timestamp...), no text.
--   * pa_searchsapcodefailurecomponent ya existe en la base (conversion automatica): no se incluye.

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_countavailableaccesory(integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_countavailableaccesory(p_codigosapid integer)
 RETURNS TABLE(cantidad integer)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT COUNT(a.id)::integer
    FROM accesorio a
    INNER JOIN general.estado e ON a.estadoid = e.id
    WHERE COALESCE(a.palletid, 0) = 0
      AND a.codigosapid = p_codigosapid
      AND e.nombre = 'LIMPIEZA';
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_getdiasultimoingreso(character varying);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_getdiasultimoingreso(p_serial character varying)
 RETURNS TABLE(fechaingreso text, dias integer)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT to_char(i.fecha, 'DD/MM/YYYY'),
           (LOCALTIMESTAMP::date - i.fecha::date)::integer      -- DATEDIFF(DAY, ...)
    FROM ingreso i
    WHERE i.serial = upper(p_serial)
    ORDER BY i.id DESC
    LIMIT 1;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_getetiquetadouser(integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_getetiquetadouser(p_usuarioidmovimiento integer)
 RETURNS TABLE(id integer, serial text, mac text, serial3 text, serial4 text, serial5 text, codigosap text,
               descripcion text, estado text, modelo text, fecha timestamp without time zone, rn bigint)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    WITH ultimos AS (
        SELECT i.id::integer AS id, i.serial::text AS serial, i.mac::text AS mac, i.serial3::text AS serial3,
               i.serial4::text AS serial4, i.serial5::text AS serial5,
               cs.codigo::text AS codigosap, cs.descripcion::text AS descripcion,
               es.nombre::text AS estado, ml.codigo::text AS modelo, i.fecha,
               ROW_NUMBER() OVER (PARTITION BY i.id ORDER BY i.id DESC) AS rn
        FROM ingreso i
        INNER JOIN estado es   ON i.estadoid = es.id
        INNER JOIN codigosap cs ON i.codigosapid = cs.id
        INNER JOIN usuariosys u ON i.usuarioid = u.id
        LEFT JOIN maestro ml    ON ml.id = i.modeloid
        WHERE es.nombre = 'ETIQUETADO-PREASIGNADO'
          AND i.usuarioidmovimiento = p_usuarioidmovimiento
    )
    SELECT u.id, u.serial, u.mac, u.serial3, u.serial4, u.serial5, u.codigosap, u.descripcion, u.estado,
           u.modelo, u.fecha, u.rn
    FROM ultimos u
    WHERE u.rn = 1
    ORDER BY u.id DESC;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_getsmartcardasinguserweb(integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_getsmartcardasinguserweb(p_usuarioid integer)
 RETURNS TABLE(id integer, serial text, mac text, codigosap text, descripcion text, usuarioasignado text,
               nivelid integer, loteid integer, lote text, modelo text, fecha timestamp without time zone, rn bigint)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    WITH ultimos AS (
        SELECT i.id::integer AS id, i.serial::text AS serial, i.mac::text AS mac, cs.codigo::text AS codigosap,
               cs.descripcion::text AS descripcion, u2.nombre_usuario::text AS usuarioasignado,
               i.nivelid::integer AS nivelid, i.loteid::integer AS loteid, le.nombre::text AS lote, ml.codigo::text AS modelo, i.fecha,
               ROW_NUMBER() OVER (PARTITION BY i.serial ORDER BY i.id DESC) AS rn
        FROM ingreso i
        INNER JOIN estado e     ON i.estadoid = e.id
        INNER JOIN codigosap cs ON i.codigosapid = cs.id
        INNER JOIN usuariosys u ON i.usuarioid = u.id
        LEFT JOIN maestro ml    ON ml.id = i.modeloid
        LEFT JOIN lote le       ON i.loteid = le.id
        LEFT JOIN usuariosys u2 ON i.usuarioid = u2.id
        WHERE e.nombre = 'SMARTCARD-PREASIGNADO'
          AND i.usuarioid = p_usuarioid
    )
    SELECT u.id, u.serial, u.mac, u.codigosap, u.descripcion, u.usuarioasignado, u.nivelid, u.loteid,
           u.lote, u.modelo, u.fecha, u.rn
    FROM ultimos u
    WHERE u.rn = 1
    ORDER BY u.id DESC;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_insertsmartcardweb(integer, character varying, integer, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_insertsmartcardweb(p_serialid integer, p_serial character varying,
                                                                      p_codigosapid integer, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO smartcard (serialid, serial, codigosapid, estadofinalid, fallaid, usuarioid, fecha)
    VALUES (p_serialid, p_serial, p_codigosapid, 0, 0, p_usuarioid, LOCALTIMESTAMP);
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_searchaccesorynovedadpallet(character varying);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchaccesorynovedadpallet(p_numero character varying)
 RETURNS TABLE(id integer, numero text, codigosap text, descripcion text, cantidad integer, tipologia text, estado text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT p.id::integer, p.numero::text, cs.codigo::text, cs.descripcion::text, COUNT(a.id)::integer,
           t.codigo::text, e.nombre::text
    FROM pallet p
    INNER JOIN codigosap cs    ON p.codigosapid = cs.id
    INNER JOIN maestro m       ON cs.tipoid = m.id
    INNER JOIN maestro t       ON p.tipologiaid = t.id
    INNER JOIN general.estado e ON p.destinoid = e.id
    LEFT JOIN accesorio a      ON p.id = a.palletid
    WHERE p.numero = p_numero
      AND p.activo = true
      AND m.codigo = 'ACCESORIOS'
    GROUP BY p.id, p.numero, cs.codigo, cs.descripcion, t.codigo, e.nombre;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_searchboxentryquality(integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchboxentryquality(p_cajaempaqueid integer)
 RETURNS TABLE(serial text, mac text, codigo text, descripcion text, tipologia text, smartcard text,
               fallafuncional text, fallafuncionaldescripcion text, fallacosmetica text, fallacosmeticadescripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT i.serial::text, i.mac::text, cs.codigo::text, cs.descripcion::text, t.codigo::text, i.smartcard::text,
           COALESCE(mf.codigo, '')::text, COALESCE(mf.descripcion, '')::text,
           COALESCE(mc.codigo, '')::text, COALESCE(mc.descripcion, '')::text
    FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid = cs.id
    INNER JOIN maestro t    ON i.tipologiaid = t.id
    LEFT JOIN LATERAL (
        SELECT q.fallafuncionalid, q.fallacosmeticaid
        FROM calidad q
        WHERE q.cajaempaqueid = p_cajaempaqueid
          AND q.serial = i.serial
        ORDER BY q.fecha DESC, q.id DESC
        LIMIT 1
    ) q ON true
    LEFT JOIN maestro mf ON mf.id = q.fallafuncionalid
    LEFT JOIN maestro mc ON mc.id = q.fallacosmeticaid
    WHERE i.cajaempaqueid = p_cajaempaqueid;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_searchinventory(character varying);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchinventory(p_pallet character varying)
 RETURNS TABLE(serial text, estado text, codigosap text, pallet text, estadosap text, estadorr text, ajuste text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT i.serial::text, e.nombre::text, i.codigosap::text, i.pallet::text, i.estadosap::text,
           i.estadorr::text, i.ajuste::text
    FROM inventario i
    INNER JOIN general.estado e ON i.estadoid = e.id
    WHERE i.pallet = p_pallet;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_searchnoveltyaccesory(integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchnoveltyaccesory(p_palletid integer)
 RETURNS TABLE(palletid integer, codigosapid integer, codigo text, descripcion text, tipoaccesorio text,
               origen text, documento text, guia text, caja integer)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT a.palletid::integer, a.codigosapid::integer, cs.codigo::text, cs.descripcion::text, a.tipoaccesorio::text,
           o.codigo::text, a.documento::text, a.guia::text, a.caja::integer
    FROM accesorio a
    INNER JOIN codigosap cs ON a.codigosapid = cs.id
    INNER JOIN maestro o    ON a.origenid = o.id
    WHERE a.palletid = p_palletid;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_searchnoveltyaccesorypallet(character varying);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchnoveltyaccesorypallet(p_tiponovedad character varying)
 RETURNS TABLE(id integer, numero text, codigosap text, descripcion text, cantidad integer, tipologia text, novedad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT p.id::integer, p.numero::text, cs.codigo::text, cs.descripcion::text, COUNT(a.id)::integer,
           t.codigo::text, a.novedad::text
    FROM pallet p
    INNER JOIN codigosap cs    ON p.codigosapid = cs.id
    INNER JOIN maestro m       ON cs.tipoid = m.id
    INNER JOIN maestro t       ON p.tipologiaid = t.id
    INNER JOIN general.estado e ON p.destinoid = e.id
    INNER JOIN accesorio a     ON p.id = a.palletid
    WHERE e.nombre = 'NOVEDAD-ACCESORIOS'
      AND a.novedad = p_tiponovedad
      AND m.codigo = 'ACCESORIOS'
      AND p.activo = true
    GROUP BY p.id, p.numero, cs.codigo, cs.descripcion, t.codigo, a.novedad;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_searchserialesbycajamaster(integer, character varying);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchserialesbycajamaster(p_palletid integer, p_numerocaja character varying)
 RETURNS TABLE(serial text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_cajaempaqueid integer;
BEGIN
    SELECT ce.id INTO v_cajaempaqueid
    FROM cajaempaque ce
    WHERE ce.palletid = p_palletid
      AND ce.numero::text = p_numerocaja
      AND ce.activo = true
    ORDER BY ce.id
    LIMIT 1;

    -- Si no existe la caja devuelve un conjunto vacio (no error)
    IF v_cajaempaqueid IS NULL THEN
        RETURN;
    END IF;

    RETURN QUERY
    SELECT i.serial::text
    FROM ingreso i
    WHERE i.cajaempaqueid = v_cajaempaqueid;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_searchsettings(character varying);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchsettings(p_pallet character varying)
 RETURNS TABLE(serial text, codigosap text, codigosapreal text, pallet text, palletreal text, estado text,
               estadosap text, estadorr text, ajuste text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT i.serial::text, i.codigosap::text, i.codigosapreal::text, i.pallet::text, i.palletreal::text,
           e.nombre::text, i.estadosap::text, i.estadorr::text, i.ajuste::text
    FROM inventario i
    INNER JOIN general.estado e ON i.estadoid = e.id
    WHERE i.pallet = p_pallet;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
-- El original recorria en lotes de 200 con WAITFOR; un UPDATE unico es equivalente (misma condicion).
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_sendnoveltyentry(integer, integer, integer, integer, integer, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_sendnoveltyentry(p_estadoid integer, p_tipologiaid integer,
        p_usuarioid integer, p_palletid integer, p_opcion integer, INOUT p_filas integer DEFAULT NULL)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_codigo text;
    v_rows   bigint;
BEGIN
    SELECT cs.codigo INTO v_codigo
    FROM pallet p
    INNER JOIN codigosap cs ON p.codigosapid = cs.id
    WHERE p.id = p_palletid;

    IF v_codigo <> 'MULTIMODELO' THEN
        IF p_opcion = 0 THEN
            UPDATE ingreso
            SET estadoid = p_estadoid, tipologiaid = p_tipologiaid, usuarioidmovimiento = p_usuarioid,
                fechamovimiento = LOCALTIMESTAMP, novedad = '0'
            WHERE palletid = p_palletid AND estadoid <> p_estadoid;
        ELSE
            UPDATE ingreso
            SET estadoid = p_estadoid, tipologiaid = p_tipologiaid, usuarioidmovimiento = p_usuarioid,
                fechamovimiento = LOCALTIMESTAMP, palletid = 0, novedad = '0'
            WHERE palletid = p_palletid AND estadoid <> p_estadoid;
        END IF;
    ELSE
        IF p_opcion = 0 THEN
            UPDATE ingreso
            SET estadoid = p_estadoid, usuarioidmovimiento = p_usuarioid,
                fechamovimiento = LOCALTIMESTAMP, novedad = '0'
            WHERE palletid = p_palletid AND estadoid <> p_estadoid;
        ELSE
            UPDATE ingreso
            SET estadoid = p_estadoid, usuarioidmovimiento = p_usuarioid,
                fechamovimiento = LOCALTIMESTAMP, palletid = 0, novedad = '0'
            WHERE palletid = p_palletid AND estadoid <> p_estadoid;
        END IF;
    END IF;
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    p_filas := v_rows;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_updateaccesorynovedad(integer, integer, integer, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updateaccesorynovedad(p_palletid integer, p_codigosapid integer,
        p_usuarioid integer, INOUT p_filas integer DEFAULT NULL)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rows bigint;
BEGIN
    UPDATE pallet
    SET codigosapid = p_codigosapid,
        usuarioidmodifica = p_usuarioid,
        fechamodifica = LOCALTIMESTAMP
    WHERE id = p_palletid AND activo = true;
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    p_filas := v_rows;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_updateaccesorynovedadcantidad(integer, integer, integer, character varying, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updateaccesorynovedadcantidad(p_palletid integer, p_codigosapid integer,
        p_cantidad integer, p_accion character varying, p_usuarioid integer)
 RETURNS TABLE(resultado integer)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_estadolimpiezaid integer;
    v_novedad text;
BEGIN
    SELECT e.id INTO v_estadolimpiezaid FROM general.estado e WHERE e.nombre = 'LIMPIEZA';

    -- Novedad actual del pallet, para asignarsela a los accesorios que se agreguen
    SELECT a.novedad INTO v_novedad
    FROM accesorio a
    WHERE a.palletid = p_palletid AND a.novedad IS NOT NULL
    LIMIT 1;

    BEGIN
        IF p_accion = 'REDUCIR' THEN
            UPDATE accesorio
            SET palletid = 0, novedad = NULL, estadoid = v_estadolimpiezaid
            WHERE id IN (SELECT a2.id FROM accesorio a2 WHERE a2.palletid = p_palletid LIMIT p_cantidad);
        ELSIF p_accion = 'AUMENTAR' THEN
            UPDATE accesorio a
            SET palletid = p_palletid, usuarioidmovimiento = p_usuarioid, novedad = v_novedad
            FROM (
                SELECT a2.id
                FROM accesorio a2
                INNER JOIN general.estado e ON a2.estadoid = e.id
                WHERE COALESCE(a2.palletid, 0) = 0
                  AND a2.codigosapid = p_codigosapid
                  AND e.nombre = 'LIMPIEZA'
                LIMIT p_cantidad
            ) sel
            WHERE a.id = sel.id;
        END IF;
        RETURN QUERY SELECT 1;
    EXCEPTION WHEN OTHERS THEN
        -- el bloque revierte sus cambios solo (igual que el ROLLBACK del original)
        RETURN QUERY SELECT -1;
    END;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_updateentrydispatchweb(integer, integer, integer, character varying, integer, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updateentrydispatchweb(p_estadoid integer, p_palletid integer,
        p_usuarioid integer, p_serial character varying, p_loteid integer, INOUT p_filas integer DEFAULT NULL)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rows bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid, palletid = p_palletid, cajadespachoid = 0,
        usuarioidmovimiento = p_usuarioid, fechamovimiento = LOCALTIMESTAMP
    WHERE serial = upper(p_serial);
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    p_filas := v_rows;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
-- 114 = SMARTCARD-PREASIGNADO (id fijo en el original)
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_updateestadosmartcardasign(character varying, integer, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updateestadosmartcardasign(p_serial character varying,
        p_usuarioid integer, INOUT p_filas integer DEFAULT NULL)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rows bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = 114, usuarioid = p_usuarioid, fecha = LOCALTIMESTAMP
    WHERE serial = upper(p_serial);
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    p_filas := v_rows;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_updatenoveltyallaccesory(integer, integer, character varying, character varying, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updatenoveltyallaccesory(p_palletid integer, p_estadoid integer,
        p_observaciones character varying, p_novedad character varying, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE pallet
    SET destinoid = p_estadoid,
        usuarioidmodifica = p_usuarioid,
        fechamodifica = LOCALTIMESTAMP
    WHERE id = p_palletid;

    UPDATE accesorio
    SET estadoid = p_estadoid,
        observacion = p_observaciones,
        novedad = p_novedad,
        usuarioidmovimiento = p_usuarioid,
        fecha_movimiento = LOCALTIMESTAMP
    WHERE palletid = p_palletid;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_updatesapcodepallet(integer, integer, integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updatesapcodepallet(p_palletid integer, p_codigosapid integer,
        INOUT p_filas integer DEFAULT NULL)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rows bigint;
BEGIN
    UPDATE pallet SET codigosapid = p_codigosapid WHERE id = p_palletid;
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    p_filas := v_rows;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_validarreingresoscrap(character varying);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_validarreingresoscrap(p_serial character varying)
 RETURNS TABLE(descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_ultmov text;
    v_serial text := upper(p_serial);
BEGIN
    SELECT m.descripcion INTO v_ultmov
    FROM movimiento m
    WHERE m.serial = v_serial
      AND m.descripcion ILIKE '%Tipologia%'
    ORDER BY m.id DESC
    LIMIT 1;

    IF v_ultmov IS NOT NULL AND upper(v_ultmov) LIKE '%A: SCRAP%' THEN
        RETURN QUERY SELECT v_ultmov;
        RETURN;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM despacho d
        INNER JOIN maestro mo ON mo.id = d.tipologiaid
        WHERE d.serial = v_serial
          AND upper(mo.codigo) = 'SCRAP'
          AND d.id = (SELECT MAX(d2.id) FROM despacho d2 WHERE d2.serial = v_serial)
    ) THEN
        RETURN QUERY SELECT 'Despacho con Tipologia: SCRAP'::text;
        RETURN;
    END IF;

    RETURN QUERY SELECT v_ultmov;
END;
$function$;

-- ---------------------------------------------------------------------------------------------------
-- Si la columna calidad.estadoproceso no existe en tu esquema, esta funcion fallara al ejecutarse.
DROP FUNCTION IF EXISTS wmswdlegacycostarica.pa_validatequalitybypallet(integer);
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_validatequalitybypallet(p_palletid integer)
 RETURNS TABLE(resultado integer)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF EXISTS (SELECT 1 FROM calidad c WHERE c.palletid = p_palletid AND c.estadoproceso = 2) THEN
        RETURN QUERY SELECT 1;   -- hay seriales sin verificar
    ELSE
        RETURN QUERY SELECT 0;   -- todo ok
    END IF;
END;
$function$;
