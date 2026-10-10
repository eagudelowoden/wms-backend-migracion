-- Hotfix: pa_searchpalletboxentry ahora ignora mayusculas en p_estado ('EMPAQUE' ya no cae en la rama de despacho).
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchpalletboxentry(p_estado character varying, p_palletid integer, p_cajaempaqueid integer)
 RETURNS TABLE(serial text, mac text, "smartCard" text, "numeroSmartcard" text, "Serial3" text, "Serial4" text, codigo text, descripcion text, nivel text, "Lote" text, modelo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_estado)=LOWER('Empaque')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text FROM (
        SELECT i.serial,i.mac,e.smartcard,(SELECT COALESCE((SELECT numerosmartcard FROM ingreso WHERE id = e.smartcardid), '0')) AS numerosmartcard,i.serial3,i.serial4,cs.codigo,cs.descripcion,n.codigo AS nivel,l.nombre AS lote, md.codigo AS modelo FROM ingreso i
        INNER JOIN codigosap cs ON i.codigosapid=cs.id
        INNER JOIN maestro n ON i.nivelid=n.id
        INNER JOIN empaque e ON i.id = e.serialid
        LEFT JOIN lote l ON i.loteid = l.id
        LEFT JOIN maestro md ON i.modeloid = md.id
        WHERE i.palletid=p_palletid and i.cajaempaqueid=p_cajaempaqueid
        ORDER BY i.fechamovimiento
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text FROM (
        SELECT i.serial,i.mac,e.smartcard,(SELECT COALESCE((SELECT numerosmartcard FROM ingreso WHERE id = e.smartcardid), '0')) AS numerosmartcard,i.serial3,i.serial4,cs.codigo,cs.descripcion,n.codigo AS nivel,l.nombre AS lote, md.codigo AS modelo FROM ingreso i
        INNER JOIN codigosap cs ON i.codigosapid=cs.id
        INNER JOIN maestro n ON i.nivelid=n.id
        INNER JOIn maestro m ON cs.tipoid = m.id
        LEFT JOIN empaque e ON i.id = e.serialid
        LEFT JOIN lote l ON i.loteid = l.id
         LEFT JOIN maestro md ON i.modeloid = md.id
        WHERE i.palletid=p_palletid and i.cajadespachoid=p_cajaempaqueid and m.codigo = 'SERIALIZADOS'
        ORDER BY i.fechamovimiento
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11);
    END IF;
END;
$function$;
