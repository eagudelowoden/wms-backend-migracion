-- Hotfix: serializa por serial las llamadas simultaneas (evita duplicate key en empaque_unique).
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_insertpackingwebd(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_palletid integer, p_cajaempaqueid integer, p_estadoid integer, p_nivelid integer, p_usuarioid integer, p_fecha timestamp without time zone, p_loteid integer, p_smartcardid integer, p_smartcard character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
    v_errormessage varchar(4000);
BEGIN
    PERFORM pg_advisory_xact_lock(hashtext('empaque:' || p_serialid::text));
    BEGIN
        DELETE FROM empaque
        WHERE serialid = p_serialid;
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        INSERT INTO Empaque (
              serialid, serial, mac, codigosapid,
              palletid, cajaempaqueid, nivelid,
              usuarioid, fecha, loteid,
              smartcardid, smartcard
        )
        VALUES (
              p_serialid, upper((p_serial)::text), upper((p_mac)::text), p_codigosapid,
              p_palletid, p_cajaempaqueid, p_nivelid,
              p_usuarioid, p_fecha, p_loteid,
              p_smartcardid, p_smartcard
        );
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        UPDATE ingreso AS i
        SET estadoid = p_estadoid,
            palletid = p_palletid,
            palletidempaque = p_palletid,
            cajaempaqueid = p_cajaempaqueid,
            cajadespachoid = 0,
            usuarioidmovimiento = p_usuarioid,
            fechamovimiento = p_fecha,
            loteid = p_loteid
        WHERE (i.serial = upper((p_serial)::text));
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        p_filas := (v_rowcount);
    EXCEPTION WHEN OTHERS THEN
        IF 0 > 0 THEN
            NULL;
        END IF;
        p_filas := 0;
        v_errormessage := SQLERRM;
        RAISE EXCEPTION USING MESSAGE = v_errormessage;
    END;
END;
$function$;
