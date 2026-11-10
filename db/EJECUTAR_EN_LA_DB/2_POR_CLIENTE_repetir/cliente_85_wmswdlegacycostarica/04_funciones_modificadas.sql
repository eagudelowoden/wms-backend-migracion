-- PRODUCCION nov-2026 -> PostgreSQL, parte 5: funciones modificadas (esquema wmswdlegacycostarica; para otro cliente reemplaza el nombre).
--  * pa_searchpalletboxdispatchpallet: columna CAJAS cuenta las cajas de empaque cuando no hay cajas de despacho.
--  * pa_getrepairuser: el nombre del tecnico sale de general.usuario (LEFT JOIN); ya no se pierde el serial cuando el
--    tecnico no esta en usuariosys.
-- Las funciones de Autogestion estan en db/autogestion_postgres.sql; pa_insertcosmetica en db/cambios_produccion_postgres.sql.

CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_searchpalletboxdispatchpallet(p_numero character varying)
 RETURNS TABLE(id text, numero text, "Codigo" text, "Descripcion" text, cajas text, "Cantidad Equip." text, "Cantidad Acc." text, "Cantidad SmartC." text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
        select p.id,p.numero,cs.codigo,cs.descripcion,CASE WHEN COUNT(CASE WHEN cd.activo = true THEN cd.id END) > 0 THEN COUNT(CASE WHEN cd.activo = true THEN cd.id END) ELSE (SELECT COUNT(DISTINCT ie.cajaempaqueid) FROM ingreso ie WHERE ie.palletid = p.id AND ie.cajaempaqueid > 0) END AS cajas,((SELECT COALESCE((Select COALESCE(count(i.palletid),0) from pallet pp
                                                                                                        inner join ingreso i on pp.id = i.palletid
                                                                                                        inner join general.estado d on pp.destinoid=d.id
                                                                                                        inner join codigosap cs On i.codigosapid = cs.id
                                                                                                        inner join maestro m ON cs.tipoid = m.id
                                                                                                        where d.nombre='DESPACHO' and pp.activo=true and m.codigo <> 'SMARTCARD'
                                                                                                        and pp.id = p.id
                                                                                                        group by i.palletid),0))) AS "cantidad equip.",(SELECT COALESCE((Select count(a.palletid) from pallet pl
                                                                            inner join accesorio a on pl.id = a.palletid
                                                                            inner join general.estado d on pl.destinoid=d.id
                                                                            where d.nombre='DESPACHO' and pl.activo=true
                                                                            and pl.id = p.id
                                                                            group by a.palletid),0)) AS "cantidad acc.",((SELECT COALESCE((Select COALESCE(count(i.palletid),0) from pallet pp
                                                                                                        inner join ingreso i on pp.id = i.palletid
                                                                                                        inner join general.estado d on pp.destinoid=d.id
                                                                                                        inner join codigosap cs On i.codigosapid = cs.id
                                                                                                        inner join maestro m ON cs.tipoid = m.id
                                                                                                        where d.nombre='DESPACHO' and pp.activo=true and m.codigo = 'SMARTCARD'
                                                                                                        and pp.id = p.id
                                                                                                        group by i.palletid),0))) AS "cantidad smartc.",
        t.codigo AS tipologia from pallet p
        left join cajadespacho cd on p.id=cd.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado d on p.destinoid=d.id
        inner join codigosap cs on p.codigosapid = cs.id
        where d.nombre='DESPACHO' and p.activo=true
        group by p.id,p.numero,cs.codigo,cs.descripcion,t.codigo
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
        select p.id,p.numero,cs.codigo,cs.descripcion,CASE WHEN COUNT(CASE WHEN cd.activo = true THEN cd.id END) > 0 THEN COUNT(CASE WHEN cd.activo = true THEN cd.id END) ELSE (SELECT COUNT(DISTINCT ie.cajaempaqueid) FROM ingreso ie WHERE ie.palletid = p.id AND ie.cajaempaqueid > 0) END AS cajas,((SELECT COALESCE((Select COALESCE(count(i.palletid),0) from pallet pp
                                                                                                        inner join ingreso i on pp.id = i.palletid
                                                                                                        inner join general.estado d on pp.destinoid=d.id
                                                                                                        inner join codigosap cs On i.codigosapid = cs.id
                                                                                                        inner join maestro m ON cs.tipoid = m.id
                                                                                                        where d.nombre='DESPACHO' and pp.activo=true and m.codigo <> 'SMARTCARD'
                                                                                                        and pp.id = p.id
                                                                                                        group by i.palletid),0))) AS "cantidad equip.",(SELECT COALESCE((Select count(a.palletid) from pallet pl
                                                                            inner join accesorio a on pl.id = a.palletid
                                                                            inner join general.estado d on pl.destinoid=d.id
                                                                            where d.nombre='DESPACHO' and pl.activo=true
                                                                            and pl.id = p.id
                                                                            group by a.palletid),0)) AS "cantidad acc.",((SELECT COALESCE((Select COALESCE(count(i.palletid),0) from pallet pp
                                                                                                        inner join ingreso i on pp.id = i.palletid
                                                                                                        inner join general.estado d on pp.destinoid=d.id
                                                                                                        inner join codigosap cs On i.codigosapid = cs.id
                                                                                                        inner join maestro m ON cs.tipoid = m.id
                                                                                                        where d.nombre='DESPACHO' and pp.activo=true and m.codigo = 'SMARTCARD'
                                                                                                        and pp.id = p.id
                                                                                                        group by i.palletid),0))) AS "cantidad smartc.",
        t.codigo AS tipologia from pallet p
        left join cajadespacho cd on p.id=cd.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado d on p.destinoid=d.id
        inner join codigosap cs on p.codigosapid = cs.id
        where p.numero=p_numero and d.nombre='DESPACHO' and p.activo=true
        group by p.id,p.numero,cs.codigo,cs.descripcion,t.codigo
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_getrepairuser(p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE(serial text, mac text, "Codigo" text, descripcion text, nombre_usuario text, truckrollid text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
    WITH ultimareparacion AS (
        SELECT
            r.id, r.serialid, r.codigosapid, r.tecnicoasignacionid, r.usuarioid, r.fechaasignacion,
            ROW_NUMBER() OVER (PARTITION BY r.serialid ORDER BY r.fechaasignacion DESC) AS rn
        FROM reparacion r
        WHERE LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR r.usuarioid = p_usuarioid
    )
    SELECT
        i.serial, i.mac, cs.codigo, cs.descripcion, u.nombreusuario, i.truckrollid
    FROM ultimareparacion ur
    INNER JOIN ingreso i ON ur.serialid = i.id
    INNER JOIN general.estado e ON i.estadoid = e.id
    INNER JOIN codigosap cs ON ur.codigosapid = cs.id
    LEFT JOIN general.usuario u ON ur.tecnicoasignacionid = u.id
    WHERE ur.rn = 1
      AND e.nombre = 'REPARACION-PREASIGNADO'
    ORDER BY ur.fechaasignacion DESC
    ) AS q(c1, c2, c3, c4, c5, c6);
END;
$function$
;
