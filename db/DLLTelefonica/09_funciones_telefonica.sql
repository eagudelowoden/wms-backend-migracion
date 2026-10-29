-- Funciones de wmswdtelefonicacolombiabog generadas desde las del cliente 85 (441 funciones).
-- SET search_path no hace falta: todo va calificado con el esquema.

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.fn_getdescripcionmovimiento(
    p_estadodestino text,
    p_estadoorigen text,
    p_novedad text,
    p_observaciones text,
    p_posicion text,
    p_pallet text,
    p_nivel text,
    p_tecnico text,
    p_estadoreparacion text,
    p_falladiagnostico text,
    p_cajaempaque text,
    p_cajaempaqueanterior text,
    p_palletempaqueanterior text,
    p_tipologia text,
    p_codigosap text,
    p_codigosapanterior text,
    p_serial text
)
 RETURNS text
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_variable
DECLARE
    v_descripcion text;
BEGIN
    IF p_estadodestino <> p_estadoorigen THEN
        IF p_estadodestino='INGRESO' OR p_estadodestino='ILEGIBLE' OR p_estadodestino='SERIALIZAR' THEN
            IF p_estadoorigen='TRANSITO' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Pallet Abierto');
            ELSIF p_estadoorigen='NOVEDAD' THEN
                v_descripcion := 'Ingreso de Serial Regularizado';
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='P-INGRESO' THEN
            v_descripcion := concat('Envío a P-INGRESO, Serial Regularizado: ', p_observaciones);
        ELSIF p_estadodestino='TRANSITO' THEN
            IF p_estadoorigen='INGRESO' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Pallet Cerrado');
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='P-UNIFICACION' THEN
            IF p_estadoorigen='SEPARACION' OR p_estadoorigen='ILEGIBLE' OR p_estadoorigen='SERIALIZAR' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Posición: ', p_posicion);
            ELSIF p_estadoorigen='UNIFICACION' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen, ', Posición: ', p_posicion);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='UNIFICACION' THEN
            IF p_estadoorigen='UNIFICACION-ENTREGA' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='UNIFICACION-ENTREGA' THEN
            IF p_estadoorigen='UNIFICACION' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='SEPARACION-NOVEDAD' THEN
            IF p_estadoorigen='SEPARACION-ENTREGA' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSE
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Novedad: ', p_novedad);
            END IF;
        ELSIF p_estadodestino='SEPARACION-ENTREGA' THEN
            IF p_estadoorigen='SEPARACION-NOVEDAD' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='INVENTARIO' THEN
            v_descripcion := concat('Envío a ', p_estadodestino, ', Pallet Abierto');
        ELSIF p_estadodestino='ALMACENAMIENTO' THEN
            IF p_estadoorigen='INVENTARIO' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Pallet Cerrado');
            ELSIF p_estadoorigen='P-ALMACENAMIENTO' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Posición: ', p_posicion);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='ALMACENAMIENTO-NOVEDAD' THEN
            IF p_estadoorigen='ALMACENAMIENTO-ENTREGA' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSE
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Novedad: ', p_novedad);
            END IF;
        ELSIF p_estadodestino='ALMACENAMIENTO-ENTREGA' THEN
            IF p_estadoorigen='ALMACENAMIENTO-NOVEDAD' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='NOVEDAD-TERMINADO' THEN
            v_descripcion := concat('Serial regularizado, ', p_observaciones);
        ELSIF p_estadodestino='CLASIFICACION' THEN
            IF p_estadoorigen='EMPACADO' OR p_estadoorigen='EMPAQUE' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', por Reemplazo');
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='CLASIFICACION-REPARACION' THEN
            IF p_estadoorigen='CLASIFICACION' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Nivel: ', p_nivel);
            ELSIF p_estadoorigen='CLASIFICACION-ENTREGA-REPARACION' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='CLASIFICACION-ENTREGA-REPARACION' THEN
            IF p_estadoorigen='CLASIFICACION-REPARACION' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet, ' Mal estado, Falla:OXIDO');
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='ENSAMBLE' THEN
            IF p_estadoorigen='CLASIFICACION' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Nivel: ', p_nivel);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='COSMETICA' THEN
            IF p_estadoorigen='COSMETICA-ENTREGA' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='COSMETICA-ENTREGA' THEN
            IF p_estadoorigen='COSMETICA' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='ENSAMBLE-ENTREGA' THEN
            IF p_estadoorigen='ENSAMBLE' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='ETIQUETADO' THEN
            IF p_estadoorigen='CLASIFICACION' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Nivel: ', p_nivel);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='REPARACION-ASIGNADO' THEN
            v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Tecnico Asignado: ', p_tecnico);
        ELSIF p_estadodestino='REPARACION' THEN
            v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Estado Reparacion: ', p_estadoreparacion);
        ELSIF p_estadodestino='REPARACION-TERMINADO' THEN
            IF p_estadoorigen='REPARACION-ENTREGA' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSIF p_estadoorigen='REPARACION' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Reparación Terminada');
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='REPARACION-ENTREGA' THEN
            IF p_estadoorigen='REPARACION-TERMINADO' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='DIAGNOSTICO' THEN
            IF p_estadoorigen='EMPACADO' OR p_estadoorigen='EMPAQUE' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', por Reemplazo');
            ELSIF p_estadoorigen='CLASIFICACION' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Nivel: ', p_nivel);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='DIAGNOSTICO-TERMINADO' THEN
            IF p_estadoorigen='DIAGNOSTICO-ENTREGA' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSIF p_estadoorigen='DIAGNOSTICO' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Mal Estado, Falla: ', p_falladiagnostico);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='DIAGNOSTICO-ENTREGA' THEN
            IF p_estadoorigen='DIAGNOSTICO-TERMINADO' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='EMPAQUE' THEN
            IF p_estadoorigen='DIAGNOSTICO' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Buen Estado');
            ELSIF p_estadoorigen='EMPACADO' THEN
                v_descripcion := concat('Equipo Desempacado, Pallet: ', p_palletempaqueanterior, ', Caja: ', p_cajaempaqueanterior);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='EMPACADO' THEN
            IF p_estadoorigen='EMPAQUE' THEN
                v_descripcion := concat('Equipo Empacado, Pallet: ', p_pallet, ', Caja: ', p_cajaempaque);
            ELSIF p_estadoorigen IN ('EMPAQUE-ENTREGA', 'EMPAQUE-RECHAZO COSMETICO', 'EMPAQUE-RECHAZO FUNCIONAL', 'EMPAQUE-APROBADO') THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Pallet Abierto');
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='EMPAQUE-ENTREGA' THEN
            IF p_estadoorigen='EMPAQUE' THEN
                v_descripcion := 'Envío a EMPAQUE-ENTREGA, Desde: EMPAQUE, Pallet Cerrado';
            ELSIF p_estadoorigen='CALIDAD-ENTREGA' THEN
                IF p_tipologia='EMPAQUE APROBADO' THEN
                    v_descripcion := concat('Envío a EMPAQUE-ENTREGA, Desde: ', p_estadoorigen, ', Pallet Aprobado');
                ELSIF p_tipologia='EMPAQUE RECHAZO FUNCIONAL' THEN
                    v_descripcion := concat('Envío a EMPAQUE-ENTREGA, Desde: ', p_estadoorigen, ', Rechazo Funcional');
                ELSIF p_tipologia='EMPAQUE RECHAZO COSMETICO' THEN
                    v_descripcion := concat('Envío a EMPAQUE-ENTREGA, Desde: ', p_estadoorigen, ', Rechazo Cosmetico');
                ELSE
                    v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
                END IF;
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='P-CALIDAD' THEN
            IF p_estadoorigen='CALIDAD' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='CALIFICADO' THEN
            v_descripcion := 'Ingreso en CALIDAD';
        ELSIF p_estadodestino='CALIDAD-ENTREGA' THEN
            IF p_estadoorigen='CALIFICADO' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='PREDESPACHO' THEN
            IF p_estadoorigen IN ('PREDESPACHO-ENTREGA', 'DESPACHO-ENTREGA') THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSIF p_estadoorigen='PREDESPACHADO' THEN
                v_descripcion := concat('Equipo Eliminado, Pallet Destino: ', p_pallet, ', Caja Destino: ', p_cajaempaque);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='PREDESPACHO-ENTREGA' THEN
            IF p_estadoorigen='PREDESPACHO' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSIF p_estadoorigen='DESPACHO' THEN
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Pallet Abierto');
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='DESPACHO' THEN
            v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen, ', Pallet: ', p_pallet);
        ELSIF p_estadodestino='DESPACHO-ENTREGA' THEN
            IF p_estadoorigen='PREDESPACHO' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='NOVEDAD-TERMINADO' THEN
            IF p_estadoorigen='NOVEDAD-ENTREGA' THEN
                v_descripcion := concat('Equipo Removido de: ', p_estadoorigen);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSIF p_estadodestino='NOVEDAD-ENTREGA' THEN
            IF p_estadoorigen='NOVEDAD-TERMINADO' THEN
                v_descripcion := concat('Ingreso en ', p_estadodestino, ', Pallet: ', p_pallet);
            ELSE
                v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
            END IF;
        ELSE
            v_descripcion := concat('Envío a ', p_estadodestino, ', Desde: ', p_estadoorigen);
        END IF;
    ELSE
        IF p_estadodestino='ALMACENAMIENTO' AND p_estadoorigen='ALMACENAMIENTO' THEN
            v_descripcion := concat('Equipo Agrupado, Pallet: ', p_pallet, ', Posicion: ', p_posicion);
        ELSIF p_estadoorigen='DIAGNOSTICO' AND p_codigosap <> p_codigosapanterior THEN
            v_descripcion := concat('Actualizacion de firmware, de: ', p_codigosapanterior, ' a: ', p_codigosap);
        ELSIF p_estadodestino='TRANSITO' AND p_estadoorigen='TRANSITO' THEN
            v_descripcion := concat('Serial Regularizado: ', p_observaciones);
        ELSIF p_estadodestino='DIAGNOSTICO-TERMINADO' AND p_estadoorigen='DIAGNOSTICO-TERMINADO' THEN
            v_descripcion := concat('Actualizacion de Serial Trocado, Serial Real: ', p_serial);
        ELSE
            v_descripcion := concat('Actualización en ', p_estadodestino);
        END IF;
    END IF;
    RETURN left(v_descripcion, 500);
END;
$function$;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.inserttousuariosys()
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO UsuarioSys (id,nombres,nombre_usuario,perfil_id,identificacion)
    select DISTINCT a.id,a.nombres,a.nombreusuario,a.perfilid, a.identificacion from (
        select u.id,u.nombres || ' ' || u.apellidos AS nombres, u.nombreusuario,ucp.perfilid, u.identificacion from general.usuarioclienteperfil ucp
        inner join general.usuariocliente uc on ucp.usuarioclienteid=uc.id
        right join general.usuario u on uc.usuarioid=u.id
        where ucp.perfilid IS NULL OR (nombreusuario = 'adminCreador' AND uc.clienteid = (select id FROM general.cliente where dbase = (SELECT current_database())))) a left join usuariosys b on a.id = b.id where b.nombre_usuario is null;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_addciclicpallet(p_ciclicoid integer, p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO CiclicoPallet(ciclicoid,palletid,activo)
            VALUES(p_ciclicoid,p_palletid, (1)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_addcountboxfamily(p_value character varying, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE maestro
    SET adicional = p_value
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_addcountpalletfamily(p_value character varying, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE maestro
    SET detalle = p_value
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_backcleanentry(p_id character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE wmswdtelefonicacolombiabog.accesorio
    SET estadoid = 95,
        estadolimpiezaid = 0,
        fecha_movimiento = LOCALTIMESTAMP,
        fechalimpieza = null,
        usuariolimpiezaid = null
    WHERE (id =p_id);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_backrepairedentry(p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
    v_estadoasignadoid integer;
    v_estadoiding integer;
    v_nivelorigenid integer;
BEGIN
    SELECT id INTO v_estadoasignadoid FROM maestro
    WHERE codigo = 'ASIGNADO' LIMIT 1;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    SELECT id INTO v_estadoiding FROM estado
    WHERE nombre = 'REPARACION-ASIGNADO' LIMIT 1;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    SELECT id INTO v_nivelorigenid FROM maestro
    WHERE codigo = '1' AND tipomaestroid = (SELECT id FROM tipomaestro WHERE nombre = 'Niveles' LIMIT 1) LIMIT 1;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    UPDATE wmswdtelefonicacolombiabog.reparacion
    SET estadofinalid = v_estadoasignadoid,
        falla1id = 0,
        falla2id = 0,
        falla3id = 0,
        partescambiadas = NULL,
        tecnicoreparacionid = 0,
        fechareparacion = NULL,
        estadocalidadid = 0,
        motivoscrapid = 0
    WHERE (id = (SELECT MAX(r.id) FROM wmswdtelefonicacolombiabog.reparacion r WHERE r.serial = p_serial));
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := v_rowcount;
    UPDATE wmswdtelefonicacolombiabog.ingreso
    SET estadoid = v_estadoiding,
        nivelid = v_nivelorigenid,
        truckrollid = CASE
                WHEN truckrollid IN (4, 5) THEN 3
                WHEN truckrollid IN (10, 11, 12, 13) THEN 9
                ELSE truckrollid
            END
    WHERE (serial = p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_boxentry(p_palletid integer)
 RETURNS TABLE(id text, numero text, seriales text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT ci.id,ci.numero,count(ing.id) AS seriales FROM cajaingreso ci
            LEFT JOIN ingreso ing ON ci.id=ing.cajaingresoid
            WHERE ci.palletid=p_palletid and ci.activo=true
            GROUP BY ci.id,ci.numero
            ORDER BY ci.numero
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_bulkinsertentry(p_json text)
 RETURNS TABLE("Insertados" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    INSERT INTO Ingreso (
        serial, mac, serial3, serial4, serial5, codigosapid,
        palletid, palletidingreso, estadoid, tipoorigenid, origenid,
        tipologiaid, nivelid, tramite, documento, guia, causa, caja, falla,
        tecnicocliente, prealertaid, cruce, novedad, garantiafabricante,
        usuarioid, fecha, usuarioidmovimiento, fechamovimiento,
        observaciones, estadocliente, loteid, cajaingresoid, modeloid, smartcard
    )
    SELECT
        upper((d.serial)::text),
        CASE WHEN COALESCE(d.mac, '') IN ('', '0') THEN '0' ELSE upper((d.mac)::text) END,
        upper((COALESCE(d.serial3, ''))::text),
        upper((COALESCE(d.serial4, ''))::text), upper((COALESCE(d.serial5, ''))::text),
        d.codigosapid, d.palletid, d.palletid,
        d.estadoid, d.tipoorigenid, d.origenid,
        d.tipologiaid, d.nivelid, d.tramite, d.documento, d.guia,
        d.causa, 0, NULL, NULL, 0, ( 0)::boolean, NULL, ( 0)::boolean,
        d.usuarioid, LOCALTIMESTAMP, d.usuarioid, LOCALTIMESTAMP,
        NULL, d.estadocliente, d.loteid, NULL, d.modeloid, d.smartcard
    FROM jsonb_array_elements(CASE WHEN jsonb_typeof((p_json)::jsonb) = 'array' THEN (p_json)::jsonb ELSE jsonb_build_array((p_json)::jsonb) END) WITH ORDINALITY AS j(value, key)
    CROSS JOIN LATERAL (SELECT (e ->> 'serial')::varchar(50) AS serial, (e ->> 'mac')::varchar(50) AS mac, (e ->> 'serial3')::varchar(50) AS serial3, (e ->> 'serial4')::varchar(50) AS serial4, (e ->> 'serial5')::varchar(50) AS serial5, (e ->> 'codigoSapId')::integer AS codigosapid, (e ->> 'palletId')::integer AS palletid, (e ->> 'estadoId')::integer AS estadoid, (e ->> 'tipoOrigenId')::integer AS tipoorigenid, (e ->> 'origenId')::integer AS origenid, (e ->> 'tipologiaId')::integer AS tipologiaid, (e ->> 'nivelId')::integer AS nivelid, (e ->> 'tramite')::varchar(30) AS tramite, (e ->> 'documento')::varchar(30) AS documento, (e ->> 'guia')::varchar(30) AS guia, (e ->> 'causa')::varchar(50) AS causa, (e ->> 'usuarioId')::integer AS usuarioid, (e ->> 'estadoCliente')::varchar(20) AS estadocliente, (e ->> 'loteId')::integer AS loteid, (e ->> 'modeloId')::integer AS modeloid, (e ->> 'smartCard')::varchar(50) AS smartcard FROM jsonb_array_elements(CASE WHEN jsonb_typeof((j.value)::jsonb) = 'array' THEN (j.value)::jsonb ELSE jsonb_build_array((j.value)::jsonb) END) AS e) AS d
    WHERE NOT EXISTS (SELECT 1 FROM ingreso i WHERE i.serial = d.serial)
    ORDER BY ( j.key)::integer;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT v_rowcount AS insertados
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_confirmunreadable(p_consecutivoserial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ilegible
    SET confirmado = (1)::boolean
    WHERE (consecutivoserial=p_consecutivoserial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteacccodigosap(p_codigosapid integer, p_accesorioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from codigosapaccesorio where codigosapid=p_codigosapid and accesorioid=p_accesorioid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteaccesory(p_palletid integer, p_cantidad integer, p_codigosapid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM accesorio where id in(select a.id from accesorio a
    where a.palletid=p_palletid and a.codigosapid=p_codigosapid LIMIT p_cantidad);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteallcomponentsasig(p_serialid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM serialcomponente WHERE serialid = p_serialid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteallprealertserial(p_prealertaid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from prealertaserial where prealertaid=p_prealertaid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteassemble(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ensamble
    WHERE id = (SELECT max(e.id)
                FROM ensamble e
                INNER JOIN ingreso i ON e.serialid=i.id
                WHERE i.serial = p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletebase(p_base character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_base)=LOWER('BaseDespacho')) THEN
        DELETE FROM wmswdtelefonicacolombiabog.basedespacho;
    ELSIF (LOWER(p_base)=LOWER('BaseEmpaque')) THEN
        DELETE FROM wmswdtelefonicacolombiabog.baseempaque;
    ELSIF (LOWER(p_base)=LOWER('BaseInventario')) THEN
        DELETE FROM wmswdtelefonicacolombiabog.baseinventario;
    ELSIF (LOWER(p_base)=LOWER('BaseSeparacion')) THEN
        DELETE FROM wmswdtelefonicacolombiabog.baseseparacion;
    ELSIF (LOWER(p_base)=LOWER('BaseIngreso')) THEN
        DELETE FROM wmswdtelefonicacolombiabog.baseingreso;
    END IF;
    IF (LOWER(p_base)=LOWER('BaseEtiquetado')) THEN
        DELETE FROM wmswdtelefonicacolombiabog.baseetiquetado;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteboxdispatch(p_cajadespachoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM cajadespacho
    WHERE id=p_cajadespachoid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteboxentry(p_cajaingresoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM cajaingreso
        WHERE id=p_cajaingresoid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteboxpacking(p_cajaempaqueid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM cajaempaque
    WHERE id=p_cajaempaqueid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletebypallet(p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ingreso WHERE palletid = p_palletid;
    DELETE FROM pallet WHERE id = p_palletid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteciclic(p_ciclicoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ciclico
    WHERE id=p_ciclicoid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteciudad(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ciudad
                WHERE id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteclasificacion(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM clasificacion
    WHERE id=(SELECT max(id) AS id FROM clasificacion
    WHERE serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletecleaning(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM limpieza
    WHERE id = (SELECT MAX(c.id)
                FROM limpieza c
                INNER JOIN ingreso i ON c.serialid = i.id
                WHERE i.serial = p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletecodigosapfalla(p_codigosapid integer, p_fallaid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM codigosapfalla
    WHERE codigosapid = p_codigosapid AND fallaid = p_fallaid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletecomponentsasig(p_serial character varying, p_componenteid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM serialcomponente
        WHERE serialid = (SELECT id FROM ingreso WHERE serial = p_serial)
        AND componenteid = p_componenteid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteconfirmunreadable(p_serialconfirmacion character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ilegible
    SET confirmado = (0)::boolean
    WHERE (serialconfirmacion=p_serialconfirmacion);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletediagnostic(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM diagnostico
    WHERE id=(SELECT max(id) AS id FROM diagnostico
    WHERE serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletedispatch(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM despacho
    WHERE serialid in(SELECT id FROM ingreso
    WHERE serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteentries(p_serialesjson text)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ingreso i USING (SELECT (e #>> '{}')::varchar(50) AS serial FROM jsonb_array_elements(CASE WHEN jsonb_typeof((p_serialesjson)::jsonb) = 'array' THEN (p_serialesjson)::jsonb ELSE jsonb_build_array((p_serialesjson)::jsonb) END) AS e) AS s WHERE (i.serial = s.serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteentry(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_ingresoid integer;
    v_ticketid integer;
    v_etapaanterior integer;
    v_nuevaetapa integer;
    v_errormsg varchar(4000);
BEGIN
    BEGIN
        v_ingresoid := NULL;
        SELECT id INTO v_ingresoid FROM ingreso WHERE serial = p_serial LIMIT 1;
        IF v_ingresoid IS NOT NULL THEN
            BEGIN
                v_ticketid := NULL;
                v_etapaanterior := NULL;
                SELECT id, id_etapa INTO v_ticketid, v_etapaanterior
                    FROM app_pqrs_tickets
                    WHERE id_ingreso = v_ingresoid LIMIT 1;
                IF v_ticketid IS NOT NULL THEN
                    v_nuevaetapa := 2;
                    IF NOT EXISTS (SELECT 1 FROM app_pqrs_tickets WHERE id = v_ticketid AND asignado_a IS NOT NULL AND asignado_a > 0) THEN
                        v_nuevaetapa := 1;
                    END IF;
                    UPDATE app_pqrs_tickets
                    SET id_ingreso = NULL,
                        id_etapa = v_nuevaetapa,
                        updated_at = mssql.dateadd('hour', -5, ( LOCALTIMESTAMP)::timestamp)
                    WHERE (id = v_ticketid);
                    INSERT INTO App_PQRS_StatusLog
                            (id_ticket, id_etapa_anterior, id_etapa_nueva, id_usuario, comentario, auto_change)
                        VALUES
                            (v_ticketid, v_etapaanterior, v_nuevaetapa, 0,
                             'Ingreso eliminado del WMS - Serial: ' || upper((p_serial)::text) || ' (reversión automática)', ( 1)::boolean);
                END IF;
            EXCEPTION WHEN OTHERS THEN
                NULL;
            END;
        END IF;
        DELETE FROM ingreso WHERE serial = p_serial;
    EXCEPTION WHEN OTHERS THEN
        v_errormsg := SQLERRM;
        RAISE EXCEPTION USING MESSAGE = v_errormsg;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteforecast(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM forecast WHERE id = p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletelabel(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM etiqueta
    WHERE id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletelabelfield(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from etiquetacampo where id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletelote(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM lote
                WHERE id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletemanodeobra(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM manodeobra WHERE id = p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletemaster(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM maestro
    WHERE id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletemastertype(p_tipomaestroid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from tipomaestro where id=p_tipomaestroid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletepacking(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM empaque
    WHERE serialid in(SELECT id FROM ingreso
    WHERE serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletepallet(p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from pallet
    where id=p_palletid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletepart(p_parteid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from parte where id=p_parteid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteprealert(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from prealerta where id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteprealertserial(p_prealertaid integer, p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from prealertaserial where prealertaid=p_prealertaid and id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletepresupuesto(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM presupuesto
                WHERE id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletequality(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM calidad
    WHERE id=(SELECT max(c.id) AS id FROM calidad c
    INNER JOIN ingreso i ON c.serialid=i.id
    WHERE i.serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleterepair(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from reparacion where id=(select max(id) AS id from reparacion where serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletesapcode(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM codigosap
    WHERE id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletesmartcard(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM smartcard
            WHERE id=(SELECT max(id) AS id FROM smartcard
            WHERE serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletetables()
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM accesorio WHERE id > 0;
    DELETE FROM basedespacho WHERE id > 0;
    DELETE FROM baseempaque WHERE id > 0;
    DELETE FROM baseinventario WHERE id > 0;
    DELETE FROM baseseparacion WHERE id > 0;
    DELETE FROM basexlog WHERE id > 0;
    DELETE FROM cajadespacho WHERE id > 0;
    DELETE FROM cajaempaque WHERE id > 0;
    DELETE FROM calidad WHERE id > 0;
    DELETE FROM ciclico WHERE id > 0;
    DELETE FROM ciclicopallet WHERE id > 0;
    DELETE FROM ciclicoserial WHERE id > 0;
    DELETE FROM despacho WHERE id > 0;
    DELETE FROM diagnostico WHERE id > 0;
    DELETE FROM empaque WHERE id > 0;
    DELETE FROM ilegible WHERE id > 0;
    DELETE FROM ingreso WHERE id > 0;
    DELETE FROM inventario WHERE id > 0;
    DELETE FROM movimiento WHERE id > 0;
    DELETE FROM pallet WHERE id > 0;
    DELETE FROM palletlog WHERE id > 0;
    DELETE FROM parte WHERE id > 0;
    DELETE FROM prealerta WHERE id > 0;
    DELETE FROM prealertaserial WHERE id > 0;
    DELETE FROM reparacion WHERE id > 0;
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.accesorio', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.basedespacho', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.baseempaque', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.baseinventario', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.baseseparacion', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.basexlog', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.cajadespacho', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.cajaempaque', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.calidad', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.ciclico', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.ciclicopallet', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.ciclicoserial', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.despacho', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.diagnostico', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.empaque', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.ilegible', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.ingreso', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.inventario', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.movimiento', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.pallet', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.parte', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.prealerta', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.prealertaserial', 0);
    PERFORM mssql.checkident_reseed('wmswdtelefonicacolombiabog.reparacion', 0);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deletetransportadora(p_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM transportadora
                WHERE id=p_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteunreadable(p_consecutivoserial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ilegible
    WHERE consecutivoserial=p_consecutivoserial;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteworflow(p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from workflow where id=p_usuarioid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_deleteworflowdestiny(p_workflowid integer, p_destinoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    delete from workflowdestino where workflowid=p_workflowid and destinoid=p_destinoid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getalertbycustomer(p_clienteid integer, p_alertid integer)
 RETURNS TABLE(id text, customerid text, customer text, "position" text, date text, number text, source text, destiny text, typology text, codesap text, model text, card text, username text, quantity text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_cliente varchar(20);
    v_quantity integer;
    v_origen varchar(30);
    v_destino varchar(30);
    v_destinoid integer;
    v_cantcode integer;
BEGIN
    DROP TABLE IF EXISTS tmp_tempends;
    CREATE TEMP TABLE tmp_tempends (id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL, customerid integer, customer varchar(30), position varchar(30), date timestamp(3), number varchar(20), source char(30), destiny char(30), typology char(30), codesap varchar(20), model varchar(40), card varchar(20), username varchar(20), quantity integer);
    DROP TABLE IF EXISTS tmp_temp;
    CREATE TEMP TABLE tmp_temp AS SELECT p.id,p.nombre AS numero,ps.codigosap AS codigo, ps.descripcion AS descripcion,'' AS tipologia,o.codigo AS origen,'' AS destino,u.nombreusuario,p.fecha,'' AS posicion, p.guia AS tarjeta
FROM prealerta p
INNER JOIN prealertaserial ps ON p.id = ps.prealertaid
INNER JOIN maestro o on p.origenid=o.id
INNER JOIN general.usuario u on p.usuarioid=u.id
WHERE p.id = p_alertid LIMIT 1;
    SELECT nombre INTO v_cliente FROM general.cliente WHERE id = p_clienteid;
    SELECT origen INTO v_origen FROM tmp_temp;
    SELECT COALESCE(COUNT(cantidad),0) INTO v_quantity FROM prealertaserial WHERE prealertaid = p_alertid;
    DROP TABLE IF EXISTS tmp_tempc;
    CREATE TEMP TABLE tmp_tempc AS SELECT
    codigosap, Count(*) AS cant
FROM prealerta p
INNER JOIN prealertaserial ps ON p.id = ps.prealertaid
WHERE p.id = p_alertid
GROUP BY codigosap;
    SELECT count(*) INTO v_cantcode FROM tmp_tempc;
    IF (v_cantcode=1) THEN
        INSERT INTO tmp_tempends (customerid, customer, Position, date, number, Source, destiny, typology, codesap, model, card, username, quantity)
SELECT p_clienteid, v_cliente, posicion, fecha, numero, origen, destino, tipologia, codigo, descripcion, tarjeta, nombreusuario, v_quantity FROM tmp_temp;
    ELSE
        INSERT INTO tmp_tempends (customerid, customer, Position, date, number, Source, destiny, typology, codesap, model, card, username, quantity)
SELECT p_clienteid, v_cliente, posicion, fecha, numero, origen, destino, tipologia, 'MULTIMODELO', 'MULTIMODELO', tarjeta, nombreusuario, v_quantity FROM tmp_temp;
    END IF;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text FROM (
    SELECT * FROM tmp_tempends
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);
    DROP TABLE IF EXISTS tmp_temp;
    DROP TABLE IF EXISTS tmp_tempc;
    DROP TABLE IF EXISTS tmp_tempends;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getassembleduser(p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE(id text, serial text, mac text, serial3 text, serial4 text, serial5 text, codigosap text, descripcion text, usuarioasignado text, nivelid text, loteid text, lote text, modelo text, fecha text, rn text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text FROM (
    WITH ultimos AS (
        SELECT e.id,e.serial,e.mac,e.serial3,e.serial4,e.serial5,cs.codigo AS codigosap,cs.descripcion,u2.nombre_usuario AS usuarioasignado,e.nivelid,
        e.loteid,le.nombre AS lote,ml.codigo AS modelo,e.fecha,ROW_NUMBER() OVER (PARTITION BY e.serialid ORDER BY e.id DESC) AS rn
        FROM ensamble e
        INNER JOIN ingreso i ON e.serialid = i.id
        INNER JOIN estado es ON i.estadoid = es.id
        INNER JOIN codigosap cs ON e.codigosapid = cs.id
        INNER JOIN usuariosys u ON e.usuarioid = u.id
        LEFT JOIN maestro ml ON ml.id =i.modeloid
        LEFT JOIN lote le ON e.loteid = le.id
        LEFT JOIN usuariosys u2 ON e.usuarioidasignado = u2.id
        WHERE es.nombre = 'ENSAMBLE-PREASIGNADO'
          AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR e.usuarioid = p_usuarioid))
    SELECT * FROM ultimos
    WHERE rn = 1
    ORDER BY id DESC
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getassignedtechnicianrepair(p_serial character varying)
 RETURNS TABLE("tecnicoAsignacion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select t.nombreusuario AS tecnicoasignacion from reparacion r
    inner join maestro e on r.estadofinalid=e.id
    inner join general.usuario t on r.tecnicoasignacionid=t.id
    where r.id=(select max(id) AS id from reparacion
    where serial=p_serial)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getbatchlist()
 RETURNS TABLE(nombre text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT nombre FROM lote
            WHERE activo = true
            ORDER BY nombre
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getbatchname(p_id integer)
 RETURNS TABLE(nombre text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT nombre FROM lote
            WHERE id = p_id
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getboxpallet(p_id integer, p_estado character varying)
 RETURNS TABLE(cajas text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_estado)=LOWER('Empaque')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        select count(ce.id) AS cajas from cajaempaque ce
        full outer join pallet p on ce.palletid=p.id
        where p.id=p_id
        ) AS q(c1);
    ELSIF (LOWER(p_estado) = LOWER('Despacho')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        select count(cd.id) AS cajas from cajadespacho cd
        full outer join pallet p on cd.palletid=p.id
        where p.id=p_id and cd.activo=true
        ) AS q(c1);
    ELSIF (LOWER(p_estado) = LOWER('Ingreso')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        select count(ci.id) AS cajas from cajaingreso ci
        full outer join pallet p on ci.palletid=p.id
        where p.id=p_id and ci.activo=true
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getclassifieduser(p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE(id text, serialid text, serial text, mac text, codigosap text, descripcion text, usuarioasignado text, nivelid text, loteid text, lote text, modelo text, fecha text, rn text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text FROM (
    WITH ultimos AS (
        SELECT c.id,c.serialid,c.serial,c.mac,cs.codigo AS codigosap,cs.descripcion,u2.nombre_usuario AS usuarioasignado,c.nivelid,
        c.loteid,le.nombre AS lote,ml.codigo AS modelo,c.fecha,ROW_NUMBER() OVER (PARTITION BY c.serialid ORDER BY c.id DESC) AS rn
        FROM clasificacion c
        INNER JOIN ingreso i ON c.serialid = i.id
        INNER JOIN estado e ON i.estadoid = e.id
        INNER JOIN codigosap cs ON c.codigosapid = cs.id
        INNER JOIN usuariosys u ON c.usuarioid = u.id
        LEFT JOIN maestro ml ON ml.id =i.modeloid
        LEFT JOIN lote le ON c.loteid = le.id
        LEFT JOIN usuariosys u2 ON c.usuarioasignadoid = u2.id
        WHERE e.nombre = 'CLASIFICACION-PREASIGNADO'
          AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR c.usuarioid = p_usuarioid))
    SELECT * FROM ultimos
    WHERE rn = 1
    ORDER BY id DESC
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getconsecutiveunreadable(p_cliente character varying)
 RETURNS TABLE("consecutivoSerial" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT consecutivoserial FROM ilegible
    WHERE consecutivoserial ILIKE 'ILE-%' and id=(SELECT max(id) FROM ilegible)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountbase(p_base character varying)
 RETURNS TABLE(cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_base)=LOWER('BaseDespacho')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT count(id) AS cantidad FROM wmswdtelefonicacolombiabog.basedespacho
        ) AS q(c1);
    ELSIF (LOWER(p_base)=LOWER('BaseEmpaque')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT count(id) AS cantidad FROM wmswdtelefonicacolombiabog.baseempaque
        ) AS q(c1);
    ELSIF (LOWER(p_base)=LOWER('BaseInventario')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT count(id) AS cantidad FROM wmswdtelefonicacolombiabog.baseinventario
        ) AS q(c1);
    ELSIF (LOWER(p_base)=LOWER('BaseSeparacion')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT count(id) AS cantidad FROM wmswdtelefonicacolombiabog.baseseparacion
        ) AS q(c1);
    ELSIF (LOWER(p_base)=LOWER('BaseIngreso')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT count(id) AS cantidad FROM wmswdtelefonicacolombiabog.baseingreso
        ) AS q(c1);
    ELSIF (LOWER(p_base)=LOWER('BaseEtiquetado')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT count(id) AS cantidad FROM wmswdtelefonicacolombiabog.baseetiquetado
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountboxdispatch(p_cajadespachoid integer)
 RETURNS TABLE(cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT count(i.serial) AS cantidad FROM ingreso i
    INNER JOIN cajadespacho cd ON i.cajadespachoid=cd.id
    WHERE cd.id=p_cajadespachoid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountboxentry(p_cajaingresoid integer)
 RETURNS TABLE(cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT count(i.serial) AS cantidad FROM ingreso i
            INNER JOIN cajaingreso ci ON i.cajaingresoid=ci.id
            WHERE ci.id=p_cajaingresoid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountboxpacking(p_cajaempaqueid integer)
 RETURNS TABLE(cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT count(i.serial) AS cantidad FROM ingreso i
    INNER JOIN cajaempaque ce ON i.cajaempaqueid=ce.id
    WHERE ce.id=p_cajaempaqueid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountdiff()
 RETURNS TABLE("Faltante" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT
    (SELECT COUNT(id) FROM ingreso) - (SELECT COUNT(id) FROM inventario WHERE sobrante = false) AS faltante
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountdiffsap(p_codigosapid integer)
 RETURNS TABLE("Faltante" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT
        (SELECT COUNT(id) FROM ingreso WHERE codigosapid = p_codigosapid) -
        (SELECT COUNT(inv.id)
         FROM inventario inv
         INNER JOIN codigosap cs ON cs.codigo = inv.codigosap
         WHERE inv.sobrante = true AND cs.id = p_codigosapid) AS faltante
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountentry()
 RETURNS TABLE(col1 text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT COUNT(id) FROM ingreso
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountentrysap(p_codigosapid integer)
 RETURNS TABLE("Conteo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT COUNT(id) AS conteo
    FROM ingreso
    WHERE codigosapid = p_codigosapid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountinventory()
 RETURNS TABLE("Conteo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT COUNT(id) AS conteo
    FROM inventario
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountinventorysap(p_codigosapid integer)
 RETURNS TABLE("Conteo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT COUNT(inv.id) AS conteo
    FROM inventario inv
    INNER JOIN codigosap cs ON cs.codigo = inv.codigosap
    WHERE cs.id = p_codigosapid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountmasterprintingbybox(p_cajaempaqueid integer, INOUT p_totalcount integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    SELECT COUNT(*) INTO p_totalcount
    FROM masterimpresas
    WHERE cajaempaqueid = p_cajaempaqueid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountmastertype(p_tipomaestroid integer)
 RETURNS TABLE(cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select count(id) AS cantidad from maestro where tipomaestroid=p_tipomaestroid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountpallet(p_id integer, p_tabla character varying)
 RETURNS TABLE(cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_tabla)=LOWER('INGRESO')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT COUNT(serial) AS cantidad FROM wmswdtelefonicacolombiabog.ingreso
        WHERE palletid=p_id
        ) AS q(c1);
    ELSIF (LOWER(p_tabla)=LOWER('ACCESORIO')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT COUNT(id) AS cantidad FROM wmswdtelefonicacolombiabog.accesorio
        WHERE palletid=p_id
        ) AS q(c1);
    ELSIF (LOWER(p_tabla)=LOWER('PARTES')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT COUNT(id) AS cantidad FROM wmswdtelefonicacolombiabog.parte
        WHERE palletid=p_id
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountsapcode(p_codigoid integer)
 RETURNS TABLE(cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT count(cs.id) AS cantidad FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid=cs.id
    INNER JOIN despacho d ON cs.id=d.codigosapid
    WHERE cs.id=p_codigoid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountstate(p_estado character varying)
 RETURNS TABLE(cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT COUNT(i.serial) AS cantidad FROM wmswdtelefonicacolombiabog.ingreso i
            INNER JOIN estado e ON i.estadoid = e.id
            WHERE e.nombre IN (SELECT nombre FROM estado
                                WHERE padreinventario = p_estado)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getcountsurplus()
 RETURNS TABLE(col1 text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT COUNT(serial) FROM inventario
            WHERE sobrante = true
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getdaywarranty(p_serialid integer)
 RETURNS TABLE(dias text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_fdespacho timestamp(3);
    v_enddate timestamp(6);
BEGIN
    SELECT fecha INTO v_fdespacho from despacho where serialid = p_serialid;
    v_enddate := LOCALTIMESTAMP;
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT mssql.datediff('day', ( v_fdespacho)::timestamp, ( v_enddate)::timestamp) AS dias
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getdescriptionmaster(p_codigo character varying)
 RETURNS TABLE(descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT descripcion
    FROM maestro
    WHERE LOWER(codigo) = LOWER(p_codigo)
      AND activo = true
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getdiagnoseduser(p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE("Id" text, "SerialId" text, "Serial" text, "Mac" text, "CodigoSap" text, "Descripcion" text, "Fallaid" text, "EstadoFinalid" text, "Usuario" text, "LoteId" text, "Lote" text, "PQRS" text, "Fecha" text, "TruckRollId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text FROM (
    WITH ultimos AS (
        SELECT
            dg.id, dg.serialid, dg.serial, dg.mac, cs.codigo AS codigosap, cs.descripcion,
            0 AS fallaid, 0 AS estadofinalid, u.nombre_usuario AS usuario,
            i.loteid, le.nombre AS lote,
            CASE WHEN EXISTS (SELECT 1 FROM pqrs q WHERE q.serialid = dg.serialid) THEN 'SI' ELSE 'NO' END AS pqrs,
            dg.fecha, i.truckrollid,
            ROW_NUMBER() OVER (PARTITION BY dg.serialid ORDER BY dg.id DESC) AS rn
        FROM diagnostico dg
        INNER JOIN ingreso i ON dg.serialid = i.id
        INNER JOIN estado e ON i.estadoid = e.id
        INNER JOIN codigosap cs ON dg.codigosapid = cs.id
        INNER JOIN usuariosys u ON dg.usuarioid = u.id
        LEFT JOIN lote le ON i.loteid = le.id
        WHERE e.nombre = 'DIAGNOSTICO-PREASIGNADO'
          AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR i.usuarioidmovimiento = p_usuarioid)
    )
    SELECT dg.id, dg.serialid, dg.serial, dg.mac, dg.codigosap, dg.descripcion,
        dg.fallaid, dg.estadofinalid, dg.usuario, dg.loteid, dg.lote, dg.pqrs, dg.fecha, dg.truckrollid
    FROM ultimos dg
    WHERE dg.rn = 1
    ORDER BY dg.id DESC
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getdiagnosticvariables(p_serial character varying)
 RETURNS TABLE(variable1 text, variable2 text, variable3 text, variable4 text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    SELECT d.variable1, d.variable2, d.variable3, d.variable4 FROM diagnostico d WHERE d.serial = p_serial
    ) AS q(c1, c2, c3, c4);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getdifferenceprealert(p_id integer)
 RETURNS TABLE(total text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select count(serial) AS total from ingreso where prealertaid=p_id and cruce=true
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getdiffreport()
 RETURNS TABLE("CodigoSap" text, "Descripcion" text, "Pallet" text, "Principal" text, "Secundario" text, "Caja" text, "Estado" text, "Localizacion" text, "Familia" text, "Tipologia" text, "Posicion" text, "Fecha" text, "FechaMovimiento" text, "TipoOrigen" text, "Origen" text, "OrigenPallet" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text FROM (
    SELECT cs.codigo AS codigosap,
           cs.descripcion,
           p.numero AS pallet,
           i.serial AS principal,
           i.mac AS secundario,
           i.caja,
           tip.codigo AS estado,
           loc.nombre AS localizacion,
           f.codigo AS familia,
           c.codigo AS tipologia,
           po.numero AS posicion,
           i.fecha,
           i.fechamovimiento,
           tor.codigo AS tipoorigen,
           o.codigo AS origen,
           op.nombre AS origenpallet
    FROM ingreso i
    LEFT JOIN codigosap cs ON i.codigosapid = cs.id
    LEFT JOIN pallet p ON i.palletid = p.id
    LEFT JOIN estado loc ON i.estadoid = loc.id
    LEFT JOIN maestro tip ON i.tipologiaid = tip.id
    LEFT JOIN maestro f ON cs.familiaid=f.id
    LEFT JOIN maestro c ON cs.clasificacionid=c.id
    LEFT JOIN posicion po ON p.posicionid=po.id
    LEFT JOIN maestro tor ON i.tipoorigenid=tor.id
    LEFT JOIN maestro o ON i.origenid=o.id
    LEFT JOIN estado op ON p.origenid=op.id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getdispatchboxid(p_palletid integer, p_numero integer)
 RETURNS TABLE("Id" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT id
            FROM cajadespacho
            WHERE palletid=p_palletid and numero=p_numero
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getentryboxid(p_palletid integer, p_numero integer)
 RETURNS TABLE("Id" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT id
            FROM cajaingreso
            WHERE palletid=p_palletid and numero=p_numero
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getentryprogress(p_palletid integer, p_estadoid integer)
 RETURNS TABLE("totalCount" text, "totalProcessed" text, "totalRemaining" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_totalcount integer;
    v_totalremaining integer;
    v_totalprocessed integer;
BEGIN
    SELECT COUNT(*) INTO v_totalcount
    FROM ingreso
    WHERE palletid = p_palletid;
    SELECT COUNT(*) INTO v_totalremaining
    FROM ingreso
    WHERE palletid = p_palletid AND estadoid <> p_estadoid;
    v_totalprocessed := v_totalcount - v_totalremaining;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT
        v_totalcount AS totalcount,
        v_totalprocessed AS totalprocessed,
        v_totalremaining AS totalremaining
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getfailurediagnostic(p_serial character varying)
 RETURNS TABLE(falla text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    SELECT m.codigo AS falla,m.descripcion FROM diagnostico d
    INNER JOIN maestro m ON d.fallaid=m.id
    WHERE d.id=(SELECT max(id) AS id FROM diagnostico
    WHERE serial=p_serial)
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getfailureiddiagnostic(p_serialid integer)
 RETURNS TABLE("FallaId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT COALESCE(fallaid, 0) AS fallaid
    FROM wmswdtelefonicacolombiabog.diagnostico
    WHERE serialid = p_serialid
    ORDER BY id DESC LIMIT 1
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getfamilyacronyms(p_codigosap character varying, p_destino character varying)
 RETURNS TABLE(siglas text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_destino) = LOWER('INGRESO') OR LOWER(p_destino) = LOWER('ALMACENAMIENTO')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT m.descripcion AS siglas FROM maestro m
                WHERE m.codigo = 'LECTURA'
        ) AS q(c1);
    ELSE
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT m.descripcion AS siglas FROM maestro m
                INNER JOIN codigosap cs ON cs.familiaid = m.id
                WHERE LOWER(cs.codigo) = LOWER(p_codigosap)
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getfamilymaster(p_codigosap character varying)
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.codigo FROM maestro m
    INNER JOIN codigosap cs ON m.id=cs.familiaid
    WHERE LOWER(cs.codigo) = LOWER(p_codigosap)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getfamilynumberbox(p_codigosap character varying, p_destino character varying)
 RETURNS TABLE(scaja text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_destino) = LOWER('INGRESO') OR LOWER(p_destino) = LOWER('ALMACENAMIENTO')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT (m.adicional)::integer AS scaja FROM maestro m
            WHERE m.codigo = 'LECTURA'
        ) AS q(c1);
    ELSE
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT (m.adicional)::integer AS scaja FROM maestro m
            INNER JOIN codigosap cs ON cs.familiaid = m.id
            WHERE LOWER(cs.codigo) = LOWER(p_codigosap)
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getfamilynumberpallet(p_codigosap character varying, p_destino character varying)
 RETURNS TABLE(spallet text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_destino) = LOWER('INGRESO') OR LOWER(p_destino) = LOWER('ALMACENAMIENTO')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT (m.detalle)::integer AS spallet FROM maestro m
                WHERE m.codigo = 'LECTURA'
        ) AS q(c1);
    ELSE
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT (m.detalle)::integer AS spallet FROM maestro m
                INNER JOIN codigosap cs ON cs.familiaid = m.id
                WHERE LOWER(cs.codigo) = LOWER(p_codigosap)
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getfirstalertcount(p_firstalertid integer DEFAULT 0)
 RETURNS TABLE("Id" text, "Nombre" text, "Cantidad" text, "Cruzados" text, "Total" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_cantidad integer;
    v_cruzados integer;
    v_total integer;
BEGIN
    select COUNT(serial) INTO v_cantidad from prealertaserial where prealertaid=p_firstalertid;
    select COUNT(serial) INTO v_cruzados from ingreso where prealertaid=p_firstalertid and cruce=true;
    v_total := v_cantidad - v_cruzados;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
    select id, nombre, v_cantidad AS cantidad, v_cruzados AS cruzados, v_total AS total from prealerta where id=p_firstalertid
    ) AS q(c1, c2, c3, c4, c5);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getheadquartersource(p_headquarterid integer, p_typemastrerid integer, p_description character varying)
 RETURNS TABLE("Id" text, "Codigo" text, "Descripcion" text, "TipoMaestroId" text, "Activo" text, "Detalle" text, adicional text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
    SELECT m.* FROM maestro m
INNER JOIN sedeorigen so ON m.id = origenid
WHERE so.sedeid = p_headquarterid AND LOWER(descripcion) = LOWER(p_description)
    ) AS q(c1, c2, c3, c4, c5, c6, c7);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_gethistoricrepair(p_serial character varying)
 RETURNS TABLE("Serial" text, "Mac" text, "Codigo" text, "Descripcion" text, "estadoFinal" text, falla text, "tecnicoReparacion" text, "FechaReparacion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
    select r.serial,r.mac,cs.codigo,cs.descripcion,e.codigo AS estadofinal,f.codigo || ' | '|| f.descripcion AS falla,u.nombreusuario AS tecnicoreparacion,r.fechareparacion from reparacion r
    inner join codigosap cs on r.codigosapid=cs.id
    inner join maestro e on r.estadofinalid=e.id
    inner join maestro f on r.falla1id=f.id
    inner join general.usuario u on r.tecnicoreparacionid=u.id
    where r.serial=p_serial
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidbatch(p_nombre character varying)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT id FROM lote
            WHERE LOWER(nombre) = LOWER(p_nombre)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidentry(p_serial character varying)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT id FROM ingreso
    WHERE serial=p_serial
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidlevelrepairmaster()
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.id FROM maestro m
    INNER JOIN tipomaestro tm ON m.tipomaestroid = tm.id
    WHERE tm.nombre = 'Niveles'
    AND codigo = (SELECT ( MAX(( codigo)::integer) - 1)::varchar
                FROM maestro
                WHERE tipomaestroid = tm.id AND mssql.try_int(( codigo)::text) IS NOT NULL)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidmaster(p_codigo character varying, p_tipomaestro character varying)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_tipomaestro)=LOWER('Niveles') AND LOWER(p_codigo)=LOWER('0')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT min(m.id) AS id FROM maestro m
        INNER JOIN tipomaestro tm ON m.tipomaestroid=tm.id
        WHERE LOWER(tm.nombre) = LOWER(p_tipomaestro)
        ) AS q(c1);
    ELSE
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT m.id FROM maestro m
        INNER JOIN tipomaestro tm ON m.tipomaestroid=tm.id
        WHERE LOWER(m.codigo) = LOWER(p_codigo) and LOWER(tm.nombre) = LOWER(p_tipomaestro)
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidmastertype(p_nombre character varying)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select id from tipomaestro where LOWER(nombre) = LOWER(p_nombre)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidpallet(p_numero character varying)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select id from pallet
    where numero=p_numero
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidprealert(p_nombre character varying)
 RETURNS TABLE("Id" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select id from prealerta where LOWER(nombre) = LOWER(p_nombre)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidsapcode(p_codigo character varying)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT id FROM codigosap
    WHERE LOWER(codigo) = LOWER(p_codigo)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidsapcodebydescription(p_codigo character varying, p_descripcion character varying)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT id FROM codigosap
            WHERE LOWER(codigo) = LOWER(p_codigo) AND LOWER(descripcion) = LOWER(p_descripcion)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidsapcodebydescriptiontype(p_codigo character varying, p_descripcion character varying, p_tipo integer)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT id FROM codigosap
            WHERE LOWER(codigo) = LOWER(p_codigo) AND LOWER(descripcion) = LOWER(p_descripcion) AND tipoid = p_tipo
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getidworflowdestiny(p_opcion character varying, p_origen character varying, p_descripcion character varying, p_tipologiaid integer)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_opcion)=LOWER('NINGUNA')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        select d.id from workflowdestino wd
        inner join workflow w on wd.workflowid=w.id
        inner join general.estado d on wd.destinoid=d.id
        inner join general.estado o on w.origenid=o.id
        where LOWER(o.nombre) = LOWER(p_origen) and w.opcionid=0 and w.tipologiaid=p_tipologiaid
        ) AS q(c1);
    ELSE
        RETURN QUERY SELECT q.c1::text FROM (
        select d.id from workflowdestino wd
        inner join workflow w on wd.workflowid=w.id
        inner join general.estado d on wd.destinoid=d.id
        inner join general.estado o on w.origenid=o.id
        left join general.permiso op on w.opcionid=op.id
        where LOWER(o.nombre) = LOWER(p_origen) and LOWER(op.nombre) = LOWER(p_opcion) and LOWER(op.descripcion) = LOWER(p_descripcion) and w.tipologiaid=p_tipologiaid
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getincomebyusersoudesnumber(p_userid integer DEFAULT 0, p_source character varying DEFAULT NULL::character varying, p_destiny character varying DEFAULT NULL::character varying, p_number character varying DEFAULT NULL::character varying)
 RETURNS TABLE("Id" text, "Numero" text, "Cantidad" text, "Origen" text, "CodigoSapId" text, "Codigo" text, "Fecha" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_userid = 0) THEN
        IF (p_number is null) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
            select p.id, p.numero, COUNT(i.serial) AS cantidad, o.nombre AS origen, cs.id AS codigosapid, cs.codigo, p.fecha
        from wmswdtelefonicacolombiabog.pallet p
        full outer join wmswdtelefonicacolombiabog.ingreso i on p.id = i.palletid
        inner join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid = cs.id
        inner join wmswdtelefonicacolombiabog.estado o on p.origenid = o.id
        inner join wmswdtelefonicacolombiabog.estado d on p.destinoid = d.id
        inner join wmswdtelefonicacolombiabog.maestro m on cs.tipoid = m.id
        where LOWER(o.nombre) = LOWER(p_source) and LOWER(d.nombre) = LOWER(p_destiny) and p.activo=true and m.codigo= 'SERIALIZADOS'
        group by p.id, p.numero, o.nombre, cs.id, cs.codigo, p.fecha
        union
        select p.id, p.numero, COUNT(*) AS cantidad, o.nombre AS origen, cs.id AS codigosapid, cs.codigo, p.fecha
        from wmswdtelefonicacolombiabog.pallet p
        full outer join wmswdtelefonicacolombiabog.accesorio i on p.id = i.palletid
        inner join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid = cs.id
        inner join wmswdtelefonicacolombiabog.estado o on p.origenid = o.id
        inner join wmswdtelefonicacolombiabog.estado d on p.destinoid = d.id
        inner join wmswdtelefonicacolombiabog.maestro m on cs.tipoid = m.id
        where LOWER(o.nombre) = LOWER(p_source) and LOWER(d.nombre) = LOWER(p_destiny) and p.activo=true and m.codigo= 'ACCESORIOS'
        group by p.id, p.numero, o.nombre, cs.id, cs.codigo, p.fecha
            ) AS q(c1, c2, c3, c4, c5, c6, c7);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
            select p.id, p.numero, COUNT(i.serial) AS cantidad, o.nombre AS origen, cs.id AS codigosapid, cs.codigo, p.fecha
        from wmswdtelefonicacolombiabog.pallet p
        full outer join ingreso i on p.id = i.palletid
        inner join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid = cs.id
        inner join wmswdtelefonicacolombiabog.estado o on p.origenid = o.id
        inner join wmswdtelefonicacolombiabog.estado d on p.destinoid = d.id
        inner join wmswdtelefonicacolombiabog.maestro m on cs.tipoid = m.id
        where LOWER(o.nombre) = LOWER(p_source) and LOWER(d.nombre) = LOWER(p_destiny) and p.numero = p_number and p.activo = true and m.codigo= 'SERIALIZADOS'
        group by p.id, p.numero, o.nombre, cs.id, cs.codigo, p.fecha
        union
        select p.id, p.numero, COUNT(*) AS cantidad, o.nombre AS origen, cs.id AS codigosapid, cs.codigo, p.fecha
        from wmswdtelefonicacolombiabog.pallet p
        full outer join accesorio i on p.id = i.palletid
        inner join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid = cs.id
        inner join wmswdtelefonicacolombiabog.estado o on p.origenid = o.id
        inner join wmswdtelefonicacolombiabog.estado d on p.destinoid = d.id
        inner join wmswdtelefonicacolombiabog.maestro m on cs.tipoid = m.id
        where LOWER(o.nombre) = LOWER(p_source) and LOWER(d.nombre) = LOWER(p_destiny) and p.numero = p_number and p.activo = true and m.codigo= 'ACCESORIOS'
        group by p.id, p.numero, o.nombre, cs.id, cs.codigo, p.fecha
            ) AS q(c1, c2, c3, c4, c5, c6, c7);
        END IF;
    ELSIF (p_number is null) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
        select p.id, p.numero, COUNT(i.serial) AS cantidad, o.nombre AS origen, cs.id AS codigosapid, cs.codigo, p.fecha
        from wmswdtelefonicacolombiabog.pallet p
        full outer join wmswdtelefonicacolombiabog.ingreso i on p.id = i.palletid
        inner join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid = cs.id
        inner join wmswdtelefonicacolombiabog.estado o on p.origenid = o.id
        inner join wmswdtelefonicacolombiabog.estado d on p.destinoid = d.id
        inner join wmswdtelefonicacolombiabog.maestro m on cs.tipoid = m.id
        where LOWER(o.nombre) = LOWER(p_source) and LOWER(d.nombre) = LOWER(p_destiny) and p.usuarioid = p_userid and p.activo = true and m.codigo= 'SERIALIZADOS'
        group by p.id, p.numero, o.nombre, cs.id, cs.codigo, p.fecha
        union
        select p.id, p.numero, COUNT(*) AS cantidad, o.nombre AS origen, cs.id AS codigosapid, cs.codigo, p.fecha
        from wmswdtelefonicacolombiabog.pallet p
        full outer join wmswdtelefonicacolombiabog.accesorio i on p.id = i.palletid
        inner join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid = cs.id
        inner join wmswdtelefonicacolombiabog.estado o on p.origenid = o.id
        inner join wmswdtelefonicacolombiabog.estado d on p.destinoid = d.id
        inner join wmswdtelefonicacolombiabog.maestro m on cs.tipoid = m.id
        where LOWER(o.nombre) = LOWER(p_source) and LOWER(d.nombre) = LOWER(p_destiny) and p.usuarioid = p_userid and p.activo = true and m.codigo= 'ACCESORIOS'
        group by p.id, p.numero, o.nombre, cs.id, cs.codigo, p.fecha
        ) AS q(c1, c2, c3, c4, c5, c6, c7);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
        select p.id, p.numero, COUNT(i.serial) AS cantidad, o.nombre AS origen, cs.id AS codigosapid, cs.codigo, p.fecha
        from wmswdtelefonicacolombiabog.pallet p
        full outer join wmswdtelefonicacolombiabog.ingreso i on p.id = i.palletid
        inner join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid = cs.id
        inner join wmswdtelefonicacolombiabog.estado o on p.origenid = o.id
        inner join wmswdtelefonicacolombiabog.estado d on p.destinoid = d.id
        inner join wmswdtelefonicacolombiabog.maestro m on cs.tipoid = m.id
        where LOWER(o.nombre) = LOWER(p_source) and LOWER(d.nombre) = LOWER(p_destiny) and p.numero = p_number and p.usuarioid = p_userid and p.activo = true and m.codigo= 'SERIALIZADOS'
        group by p.id, p.numero, o.nombre, cs.id, cs.codigo, p.fecha
        union
        select p.id, p.numero, COUNT(*) AS cantidad, o.nombre AS origen, cs.id AS codigosapid, cs.codigo, p.fecha
        from wmswdtelefonicacolombiabog.pallet p
        full outer join wmswdtelefonicacolombiabog.accesorio i on p.id = i.palletid
        inner join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid = cs.id
        inner join wmswdtelefonicacolombiabog.estado o on p.origenid = o.id
        inner join wmswdtelefonicacolombiabog.estado d on p.destinoid = d.id
        inner join wmswdtelefonicacolombiabog.maestro m on cs.tipoid = m.id
        where LOWER(o.nombre) = LOWER(p_source) and LOWER(d.nombre) = LOWER(p_destiny) and p.numero = p_number and p.usuarioid = p_userid and p.activo = true and m.codigo= 'ACCESORIOS'
        group by p.id, p.numero, o.nombre, cs.id, cs.codigo, p.fecha
        ) AS q(c1, c2, c3, c4, c5, c6, c7);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlastboxdispatch(p_palletid integer)
 RETURNS TABLE("Numero" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT MAX((numero)::integer) AS numero FROM cajadespacho
    WHERE palletid=p_palletid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlastboxentry(p_palletid integer)
 RETURNS TABLE("Numero" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT MAX(numero) AS numero FROM cajaingreso
            WHERE palletid=p_palletid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlastboxpacking(p_palletid integer)
 RETURNS TABLE("Numero" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT
    MAX((numero)::integer) AS numero FROM cajaempaque
    WHERE palletid=p_palletid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlastmovimient(p_palletid integer)
 RETURNS TABLE("origenId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT origenid FROM movimiento
WHERE id = (SELECT MAX(m.id) FROM movimiento m
            INNER JOIN ingreso i ON m.serialid = i.id
            WHERE i.palletid = p_palletid
            AND m.origenid <> m.destinoid)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlastmovimientaccesory(p_palletid integer)
 RETURNS TABLE("origenId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT origenid FROM movimientoaccesorio
WHERE id = (SELECT MAX(m.id) FROM movimientoaccesorio m
            INNER JOIN accesorio a ON m.accesorioid = a.id
            WHERE a.palletid = p_palletid
            AND m.origenid <> m.destinoid)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlastmovimientaux(p_palletid integer)
 RETURNS TABLE("origenId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT origenid FROM movimiento
WHERE id = (SELECT MAX(m.id) FROM movimiento m
            INNER JOIN ingreso i ON m.serialid = i.id
            WHERE i.palletid = p_palletid
            AND m.origenid <> m.destinoid)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlastrepair(p_serial character varying)
 RETURNS TABLE("estadoFinal" text, tecnicoreparacion text, "fechaReparacion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    select e.codigo AS estadofinal,t.nombreusuario AS tecnicoreparacion,r.fechareparacion from reparacion r
    inner join maestro e on r.estadofinalid=e.id
    inner join general.usuario t on r.tecnicoreparacionid=t.id
    where r.id=(select max(r.id) AS id from reparacion r
    inner join maestro e on r.estadofinalid=e.id
    inner join general.usuario t on r.tecnicoreparacionid=t.id
    where r.serial=p_serial)
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlevelentry(p_serial character varying)
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.codigo FROM ingreso i
    INNER JOIN maestro m ON i.nivelid=m.id
    WHERE i.serial=p_serial
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlevelsclasification()
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.codigo
    FROM maestro m
    INNER JOIN tipomaestro tm
        ON m.tipomaestroid = tm.id
    WHERE tm.nombre = 'Niveles'
      AND m.codigo < '4'
      AND m.activo = true
    ORDER BY m.codigo
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistdescriptionsapcode()
 RETURNS TABLE(codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    SELECT codigo,
           descripcion
    FROM codigosap
    WHERE activo = true
    ORDER BY codigo
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistdescriptionsapcodenoserial(p_id integer)
 RETURNS TABLE(codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    SELECT c.codigo,
           c.descripcion
    FROM codigosap c
    WHERE c.activo = true
      AND (
            c.tipoid = p_id
         OR c.codigo = 'MULTIMODELO'
      )
    ORDER BY codigo
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistdescriptionsapcodeserial(p_id integer)
 RETURNS TABLE(codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    SELECT c.codigo,
           c.descripcion
    FROM codigosap c
    INNER JOIN maestro tcs
        ON c.tipoid = tcs.id
    WHERE c.activo = true
      AND (
            c.tipoid = p_id
         OR tcs.codigo = 'SMARTCARD'
      )
    ORDER BY codigo
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistfailurescodigosap(p_codigosapid integer)
 RETURNS TABLE(id text, codigo text, descripcion text, "ComponenteObligatorio" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    SELECT DISTINCT m.id, m.codigo, m.descripcion,csf.componenteobligatorio
    FROM codigosap cs
    INNER JOIN codigosapfalla csf ON cs.id = csf.codigosapid
    INNER JOIN maestro m ON csf.fallaid = m.id
    WHERE cs.id = p_codigosapid
    ) AS q(c1, c2, c3, c4);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistfailurescodigosapnoid(p_codigo character varying)
 RETURNS TABLE(codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    SELECT
        m.codigo AS codigo,
        m.descripcion AS descripcion
    FROM codigosap cs
    INNER JOIN codigosapfalla csf
        ON cs.id = csf.codigosapid
    INNER JOIN maestro m
        ON csf.fallaid = m.id
    WHERE LOWER(cs.codigo) = LOWER(p_codigo)
      AND m.activo = true
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistfailuresmaster(p_nombre character varying)
 RETURNS TABLE(codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    SELECT m.codigo,
           m.descripcion
    FROM maestro m
    INNER JOIN tipomaestro tm
        ON m.tipomaestroid = tm.id
    WHERE LOWER(tm.nombre) = LOWER(p_nombre)
      AND m.activo = true
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistlabel(p_tipo character varying)
 RETURNS TABLE(nombre text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT nombre
    FROM etiqueta
    WHERE LOWER(tipo) = LOWER(p_tipo)
      AND activo = true
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistlabelfield(p_etiquetaid integer)
 RETURNS TABLE(nombre text, valor text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    SELECT nombre,valor from etiquetacampo where etiquetaid=p_etiquetaid
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistmaster(p_tipomaestro character varying)
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.codigo
    FROM maestro m
    INNER JOIN tipomaestro tm
        ON m.tipomaestroid = tm.id
    WHERE LOWER(tm.nombre) = LOWER(p_tipomaestro)
      AND m.activo = true
    ORDER BY m.codigo
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistmastertype()
 RETURNS TABLE(nombre text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select nombre from tipomaestro order by nombre
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistpallet(p_destino character varying)
 RETURNS TABLE(numero text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select p.numero from pallet p
    inner join general.estado e on p.destinoid=e.id
    where LOWER(e.nombre) = LOWER(p_destino)
    order by p.id
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistprealert()
 RETURNS TABLE(nombre text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select nombre from prealerta
    where activo=true
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistsapcode()
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT codigo FROM codigosap
    WHERE activo = '1'
    ORDER BY codigo
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getliststateinventory()
 RETURNS TABLE("padreInventario" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT e.padreinventario FROM estado e
            INNER JOIN ingreso i ON e.id = i.estadoid
            WHERE e.padreinventario IS NOT NULL
            GROUP BY e.padreinventario
            ORDER BY e.padreinventario
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmasterdescriptionbyid(p_id integer)
 RETURNS TABLE("Descripcion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT descripcion FROM maestro
        WHERE id = p_id
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmasterdetailbyid(p_id integer)
 RETURNS TABLE("Detalle" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT detalle FROM maestro
        WHERE id = p_id
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmasternamebyid(p_id integer)
 RETURNS TABLE("Codigo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT codigo FROM maestro
        WHERE id = p_id
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelbase(p_base character varying, p_serial character varying)
 RETURNS TABLE(serial text, "codigoSap" text, "estadoSap" text, "estadoRR" text, "Lote" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_base)=LOWER('BaseDespacho')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT serial,codigosap,estadosap,estadorr,lote FROM wmswdtelefonicacolombiabog.basedespacho
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4, c5);
    ELSIF (LOWER(p_base)=LOWER('BaseEmpaque')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT serial,codigosap,estadosap,estadorr,lote FROM wmswdtelefonicacolombiabog.baseempaque
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4, c5);
    ELSIF (LOWER(p_base)=LOWER('BaseInventario')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, NULL::text FROM (
        SELECT serial,codigosap,estadosap,estadorr FROM wmswdtelefonicacolombiabog.baseinventario
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4);
    ELSIF (LOWER(p_base)=LOWER('BaseSeparacion')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, NULL::text FROM (
        SELECT serial,codigosap,estadosap,estadorr FROM wmswdtelefonicacolombiabog.baseseparacion
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4);
    ELSIF (LOWER(p_base)=LOWER('BaseIngreso')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT serial,codigosap,estadosap,estadorr,lote FROM wmswdtelefonicacolombiabog.baseingreso
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
    IF (LOWER(p_base)=LOWER('BaseEtiquetado')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, NULL::text FROM (
        SELECT serial,codigosap,estadosap,estadorr FROM wmswdtelefonicacolombiabog.baseetiquetado
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelbaseweb(p_base character varying, p_serial character varying)
 RETURNS TABLE(serial text, "codigoSap" text, "estadoSap" text, "estadoRR" text, "Lote" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_base)=LOWER('BaseDespacho')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT serial,codigosap,estadosap,estadorr,lote FROM wmswdtelefonicacolombiabog.basedespacho
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4, c5);
    ELSIF (LOWER(p_base)=LOWER('BaseEmpaque')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT serial,codigosap,estadosap,estadorr,lote FROM wmswdtelefonicacolombiabog.baseempaque
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4, c5);
    ELSIF (LOWER(p_base)=LOWER('BaseInventario')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, NULL::text FROM (
        SELECT serial,codigosap,estadosap,estadorr FROM wmswdtelefonicacolombiabog.baseinventario
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4);
    ELSIF (LOWER(p_base)=LOWER('BaseSeparacion')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, NULL::text FROM (
        SELECT serial,codigosap,estadosap,estadorr FROM wmswdtelefonicacolombiabog.baseseparacion
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4);
    ELSIF (LOWER(p_base)=LOWER('BaseIngreso')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT serial,codigosap,estadosap,estadorr,lote FROM wmswdtelefonicacolombiabog.baseingreso
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
    IF (LOWER(p_base)=LOWER('BaseEtiquetado')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, NULL::text FROM (
        SELECT serial,codigosap,estadosap,estadorr FROM wmswdtelefonicacolombiabog.baseetiquetado
        WHERE serial=p_serial
        ) AS q(c1, c2, c3, c4);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodeldiagnostic(p_serial character varying)
 RETURNS TABLE("serialId" text, serial text, mac text, variable1 text, variable2 text, variable3 text, variable4 text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
    SELECT serialid, serial, mac, variable1, variable2, variable3, variable4
                FROM diagnostico
                WHERE serial = p_serial
                ORDER BY serialid DESC LIMIT 1
    ) AS q(c1, c2, c3, c4, c5, c6, c7);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodeldispatch(p_pallet integer, p_cajaid integer)
 RETURNS TABLE("Id" text, "Serial" text, "Mac" text, "CodigoSapId" text, "PalletId" text, "PalletIdIngreso" text, "CajaDespachoId" text, "TipoOrigenId" text, "OrigenId" text, "TipologiaId" text, "NivelId" text, "Tramite" text, "Documento" text, "Guia" text, "Falla" text, "PrealertaId" text, "Cruce" text, "Novedad" text, "Fecha" text, "SmartCard" text, "smartCardId" text, "loteId" text, serial3 text, "cajaIngresoId" text, "numeroSmartcard" text, "FallaCosmeticaId" text, "FallaFuncionalId" text, "Causa" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_cajaid=0) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text, q.c20::text, q.c21::text, q.c22::text, q.c23::text, q.c24::text, q.c25::text, q.c26::text, q.c27::text, q.c28::text FROM (
        SELECT id,serial,mac,
                     codigosapid,
                     palletid,
                     palletidingreso,
                     cajadespachoid,
                     tipoorigenid,
                     origenid,
                     tipologiaid,
                     nivelid,
                     tramite,
                     documento,
                     guia,
                     falla,
                     prealertaid,
                     cruce,
                     novedad,
                     fecha,
                     smartcard,
                     smartcardid,
                     loteid,
                     serial3,
                     cajaingresoid,
                     numerosmartcard,
                     fallacosmeticaid,
                     fallafuncionalid,
                     causa
    FROM ingreso
    WHERE palletid=p_pallet
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25, c26, c27, c28);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text, q.c20::text, q.c21::text, q.c22::text, q.c23::text, q.c24::text, q.c25::text, q.c26::text, q.c27::text, q.c28::text FROM (
        SELECT id,serial,mac,
                     codigosapid,
                     palletid,
                     palletidingreso,
                     cajadespachoid,
                     tipoorigenid,
                     origenid,
                     tipologiaid,
                     nivelid,
                     tramite,
                     documento,
                     guia,
                     falla,
                     prealertaid,
                     cruce,
                     novedad,
                     fecha,
                     smartcard,
                     smartcardid,
                     loteid,
                     serial3,
                     cajaingresoid,
                     numerosmartcard,
                     fallacosmeticaid,
                     fallafuncionalid,
                     causa
    FROM ingreso
    WHERE palletid=p_pallet
      AND cajadespachoid=p_cajaid
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25, c26, c27, c28);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodeldispatchaccesory(p_pallet integer)
 RETURNS TABLE("Id" text, "CodigoSapid" text, "TipoAccesorio" text, "TipoOrigenId" text, "OrigenId" text, "PalletId" text, "EstadoLimpiezaId" text, "Documento" text, "Observacion" text, "Guia" text, "Fecha" text, "SerialEmpaque" text, "Caja" text, "fechaLimpieza" text, "fechaEmpaque" text, "UsuarioLimpiezaId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text FROM (
    SELECT id, codigosapid, tipoaccesorio, tipoorigenid, origenid, palletid, estadolimpiezaid, documento, observacion, guia, fecha, serialempaque, caja, fechalimpieza, fechaempaque, usuariolimpiezaid
    FROM accesorio
    WHERE palletid = p_pallet
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodeldispatchboxaccesory(p_pallet integer, p_cajadespachoid integer)
 RETURNS TABLE("Id" text, "CodigoSapid" text, "TipoAccesorio" text, "TipoOrigenId" text, "OrigenId" text, "PalletId" text, "EstadoLimpiezaId" text, "Documento" text, "Observacion" text, "Guia" text, "Fecha" text, "SerialEmpaque" text, "Caja" text, "fechaLimpieza" text, "fechaEmpaque" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text FROM (
    SELECT a.id,a.codigosapid,a.tipoaccesorio,a.tipoorigenid,a.origenid,a.palletid,a.estadolimpiezaid,a.documento,a.observacion,a.guia,a.fecha,a.serialempaque,a.caja,a.fechalimpieza,a.fechaempaque
            FROM accesorio a
            INNER JOIN ingreso i ON a.serialempaque = i.serial
            WHERE a.palletid=p_pallet AND i.cajadespachoid = p_cajadespachoid
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelentry(p_serial character varying)
 RETURNS TABLE(id text, serial text, mac text, "Serial3" text, "Serial4" text, "Serial5" text, codigo text, "codigoSapId" text, descripcion text, estado text, "estadoId" text, nombreusuario text, "cajaEmpaqueId" text, caja text, "cajaDespacho" text, pallet text, "palletId" text, posicion text, nivel text, "nivelId" text, "tipoOrigenId" text, "origenId" text, "tipologiaId" text, tipologia text, "palletIdIngreso" text, fecha text, "garantiaFabricante" text, falla text, "loteId" text, "PalletIdEmpaque" text, lote text, "smartCardId" text, "SmartCard" text, "cajaIngresoId" text, "cajaIngreso" text, "modeloId" text, modelo text, "truckRollId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text, q.c20::text, q.c21::text, q.c22::text, q.c23::text, q.c24::text, q.c25::text, q.c26::text, q.c27::text, q.c28::text, q.c29::text, q.c30::text, q.c31::text, q.c32::text, q.c33::text, q.c34::text, q.c35::text, q.c36::text, q.c37::text, q.c38::text FROM (
    SELECT i.id,i.serial,i.mac,i.serial3,i.serial4,i.serial5,cs.codigo,cs.id AS codigosapid,cs.descripcion,e.nombre AS estado,e.id AS estadoid,u.nombreusuario,
    i.cajaempaqueid,ce.numero AS caja,cd.numero AS cajadespacho,p.numero AS pallet,p.id AS palletid,po.numero AS posicion,n.codigo AS nivel,n.id AS nivelid,
    i.tipoorigenid,i.origenid,i.tipologiaid,t.codigo AS tipologia,i.palletidingreso,i.fecha,i.garantiafabricante,i.falla,i.loteid, i.palletidempaque,
    l.nombre AS lote,i.smartcardid,i.smartcard,i.cajaingresoid,ci.numero AS cajaingreso,md.id AS modeloid,md.codigo AS modelo, i.truckrollid
    FROM ingreso i
    INNER join codigosap cs ON i.codigosapid=cs.id
    INNER join general.usuario u ON i.usuarioid=u.id
    INNER join general.estado e ON i.estadoid=e.id
    INNER join maestro n ON i.nivelid=n.id
    INNER join maestro t ON i.tipologiaid=t.id
    left join pallet p ON i.palletid=p.id
    left join posicion po ON p.posicionid=po.id
    left join cajaempaque ce ON i.cajaempaqueid=ce.id
    left join cajadespacho cd ON i.cajadespachoid=cd.id
    left join cajaingreso ci ON i.cajaingresoid = ci.id
    left join lote l ON i.loteid = l.id
    LEFT JOIN maestro md ON i.modeloid = md.id
    WHERE i.serial=p_serial
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25, c26, c27, c28, c29, c30, c31, c32, c33, c34, c35, c36, c37, c38);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelentryclasificacionentrega(p_serial character varying, p_usuarioid integer, p_perfil character varying)
 RETURNS TABLE(id text, serial text, mac text, codigo text, "codigoSapId" text, descripcion text, estado text, "estadoId" text, nombreusuario text, "cajaEmpaqueId" text, caja text, "cajaDespacho" text, pallet text, "palletId" text, posicion text, nivel text, "nivelId" text, "tipoOrigenId" text, "origenId" text, "tipologiaId" text, tipologia text, "palletIdIngreso" text, fecha text, "garantiaFabricante" text, falla text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text, q.c20::text, q.c21::text, q.c22::text, q.c23::text, q.c24::text, q.c25::text FROM (
    SELECT i.id,i.serial,i.mac,cs.codigo,cs.id AS codigosapid,cs.descripcion,e.nombre AS estado,e.id AS estadoid,u.nombreusuario,i.cajaempaqueid,ce.numero AS caja,cd.numero AS cajadespacho,p.numero AS pallet,p.id AS palletid,po.numero AS posicion,n.codigo AS nivel,n.id AS nivelid,i.tipoorigenid,i.origenid,i.tipologiaid,t.codigo AS tipologia,i.palletidingreso,i.fecha,i.garantiafabricante,i.falla
        FROM ingreso i
        INNER join codigosap cs ON i.codigosapid=cs.id
        INNER join general.usuario u ON i.usuarioid=u.id
        INNER join general.estado e ON i.estadoid=e.id
        INNER join maestro n ON i.nivelid=n.id
        INNER join maestro t ON i.tipologiaid=t.id
        left join pallet p ON i.palletid=p.id
        left join posicion po ON p.posicionid=po.id
        left join cajaempaque ce ON i.cajaempaqueid=ce.id
        left join cajadespacho cd ON i.cajadespachoid=cd.id
        WHERE i.serial= p_serial
        AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR (i.usuarioidmovimiento = p_usuarioid AND LOWER(p_perfil) <> LOWER('ADMINISTRADOR')))
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelfinishinventory()
 RETURNS TABLE(id text, serial text, "codigoSap" text, "codigoSapReal" text, pallet text, "palletReal" text, "estadoId" text, "estadoSap" text, "estadoRR" text, ajuste text, fecha text, "usuarioId" text, "serialId" text, mac text, sobrante text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text FROM (
    SELECT id,serial,codigosap,codigosapreal,pallet,palletreal,estadoid,estadosap,estadorr,ajuste,fecha,usuarioid,serialid,mac,sobrante
        FROM inventario
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodellabel(p_nombre character varying)
 RETURNS TABLE(id text, nombre text, tipo text, impresion text, "codigoSapId" text, codigo text, descripcion text, activo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
    SELECT e.id,e.nombre,e.tipo,e.impresion,e.codigosapid,cs.codigo,cs.descripcion, e.activo
    FROM etiqueta e
    LEFT JOIN codigosap cs ON e.codigosapid = cs.id
    WHERE LOWER(e.nombre) = LOWER(p_nombre)
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelmaster(p_codigosap character varying)
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.codigo
    FROM maestro m
    INNER JOIN codigosap cs
        ON m.id = cs.modeloid
    WHERE LOWER(cs.codigo) = LOWER(p_codigosap)
      AND m.activo = true
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelpallet(p_id integer)
 RETURNS TABLE(id text, numero text, "posicionId" text, posicion text, "tipologiaId" text, tipologia text, "codigoSapId" text, "codigoSap" text, descripcion text, "origenId" text, origen text, "destinoId" text, activo text, "nombreUsuario" text, "loteId" text, fecha text, multimodelo text, "cantidadCaja" text, smartcard text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text FROM (
    select p.id,p.numero,po.id AS posicionid,po.numero AS posicion,t.id AS tipologiaid,t.codigo AS tipologia,cs.id AS codigosapid,cs.codigo AS codigosap,cs.descripcion,o.id AS origenid,o.nombre AS origen,d.id AS destinoid,p.activo,u.nombreusuario,p.loteid,to_char(( p.fecha)::timestamp, 'YYYY-MM-DD') AS fecha,cs.multimodelo,cs.cantidadcaja,cs.smartcard from pallet p
    left join posicion po on p.posicionid=po.id
    inner join maestro t on p.tipologiaid=t.id
    left join codigosap cs on p.codigosapid=cs.id
    left join general.estado o on p.origenid=o.id
    left join general.estado d on p.destinoid=d.id
    inner join general.usuario u on p.usuarioid=u.id
    where p.id=p_id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelpalletboxdispatch(p_pallet integer, p_cajaid integer)
 RETURNS TABLE("Id" text, "Serial" text, "Mac" text, "CodigoSapId" text, "PalletId" text, "PalletIdEmpaque" text, "CajaDespachoId" text, "TipoOrigenId" text, "OrigenId" text, "TipologiaId" text, "NivelId" text, "Tramite" text, "Documento" text, "Guia" text, "Falla" text, "PrealertaId" text, "Cruce" text, "Novedad" text, "Fecha" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text FROM (
    SELECT i.id,i.serial,i.mac,i.codigosapid,i.palletid,i.palletidempaque,i.cajadespachoid,i.tipoorigenid,i.origenid,i.tipologiaid,i.nivelid,i.tramite,i.documento,i.guia,i.falla,i.prealertaid,i.cruce,i.novedad,i.fecha
            FROM ingreso i
            INNER JOIN pallet p ON i.palletid = p.id
            INNER JOIN estado d ON p.destinoid=d.id
            WHERE i.palletid=p_pallet and i.cajadespachoid=p_cajaid and d.nombre = 'PREDESPACHO'
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelpalletboxpacking(p_pallet integer, p_cajaid integer)
 RETURNS TABLE("Id" text, "Serial" text, "Mac" text, "CodigoSapId" text, "PalletId" text, "PalletIdEmpaque" text, "CajaEmpaqueId" text, "TipoOrigenId" text, "OrigenId" text, "TipologiaId" text, "NivelId" text, "Tramite" text, "Documento" text, "Guia" text, "Falla" text, "PrealertaId" text, "Cruce" text, "Novedad" text, "Fecha" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text FROM (
    SELECT id,serial,mac,codigosapid,palletid,palletidempaque,cajaempaqueid,tipoorigenid,origenid,tipologiaid,nivelid,tramite,documento,guia,falla,prealertaid,cruce,novedad,fecha
            FROM ingreso
            WHERE palletidempaque=p_pallet and cajaempaqueid=p_cajaid
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelpalletbynumber(p_number character varying)
 RETURNS TABLE(id text, numero text, "posicionId" text, posicion text, "tipologiaId" text, tipologia text, "codigoSapId" text, "codigoSap" text, descripcion text, "origenId" text, origen text, "destinoId" text, activo text, "nombreUsuario" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text FROM (
    SELECT DISTINCT p.id,p.numero,po.id AS posicionid,po.numero AS posicion,t.id AS tipologiaid,t.codigo AS tipologia,cs.id AS codigosapid,cs.codigo AS codigosap,cs.descripcion,o.id AS origenid,o.nombre AS origen,d.id AS destinoid,p.activo,u.nombreusuario
    FROM wmswdtelefonicacolombiabog.pallet p
    left join wmswdtelefonicacolombiabog.posicion po on p.posicionid=po.id
    inner join wmswdtelefonicacolombiabog.maestro t on p.tipologiaid=t.id
    left join wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid=cs.id
    left join wmswdtelefonicacolombiabog.estado o on p.origenid=o.id
    left join wmswdtelefonicacolombiabog.estado d on p.destinoid=d.id
    inner join wmswdtelefonicacolombiabog.usuario u on p.usuarioid=u.id
    WHERE p.numero=p_number
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelpqrs(p_serial character varying, p_stage integer)
 RETURNS TABLE(id text, id_company text, company_name text, id_team text, team_name text, id_user text, user_name text, create_date text, x_studio_area_responsable_id text, x_studio_area_responsable_name text, tipo_de_cliente_pqr_id text, tipo_de_cliente_pqr_name text, x_studio_placa text, refurbish_pqr_id text, refurbish_pqr_name text, tecnologia_pqr_id text, tecnologia_pqr_name text, ticket_ref text, description text, name text, x_studio_cantidad_de_equipos text, clasificacion_refurbish_id text, clasificacion_refurbish_name text, x_studio_metodo_de_contacto text, x_studio_fecha_llegada_unidades text, x_studio_fecha_reporte text, x_studio_metodo_de_contacto_final text, x_studio_tramite text, x_studio_nivel_pqr text, partner_id text, partner_name text, partner_email text, attachment_url text, id_stage text, stage_name text, tag_ids text, x_studio_pais text, x_studio_observaciones text, x_studio_diagnstico_tcnico_woden text, "Id_diagnostico" text, "SerialId" text, "CodigosapId" text, "FalladiagnosticoId" text, "EstadoDiagnosticoId" text, "FechaDiagnostico" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text, q.c20::text, q.c21::text, q.c22::text, q.c23::text, q.c24::text, q.c25::text, q.c26::text, q.c27::text, q.c28::text, q.c29::text, q.c30::text, q.c31::text, q.c32::text, q.c33::text, q.c34::text, q.c35::text, q.c36::text, q.c37::text, q.c38::text, q.c39::text, q.c40::text, q.c41::text, q.c42::text, q.c43::text, q.c44::text, q.c45::text FROM (
    SELECT
                id,
                id_company,
                company_name,
                id_team,
                team_name,
                id_user,
                user_name,
                create_date,
                x_studio_area_responsable_id,
                x_studio_area_responsable_name,
                tipo_de_cliente_pqr_id,
                tipo_de_cliente_pqr_name,
                x_studio_placa,
                refurbish_pqr_id,
                refurbish_pqr_name,
                tecnologia_pqr_id,
                tecnologia_pqr_name,
                ticket_ref,
                description,
                name,
                x_studio_cantidad_de_equipos,
                clasificacion_refurbish_id,
                clasificacion_refurbish_name,
                x_studio_metodo_de_contacto,
                x_studio_fecha_llegada_unidades,
                x_studio_fecha_reporte,
                x_studio_metodo_de_contacto_final,
                x_studio_tramite,
                x_studio_nivel_pqr,
                partner_id,
                partner_name,
                partner_email,
                attachment_url,
                id_stage,
                stage_name,
                tag_ids,
                x_studio_pais,
                x_studio_observaciones,
                x_studio_diagnstico_tcnico_woden,
                id_diagnostico,
                serialid,
                codigosapid,
                falladiagnosticoid,
                estadodiagnosticoid,
                fechadiagnostico
            FROM
                wmswdtelefonicacolombiabog.pqrs
            WHERE
                LOWER(name) = LOWER(p_serial)
            AND
                id_stage = p_stage
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25, c26, c27, c28, c29, c30, c31, c32, c33, c34, c35, c36, c37, c38, c39, c40, c41, c42, c43, c44, c45);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelprealert(p_nombre character varying)
 RETURNS TABLE(id text, nombre text, "tipoOrigen" text, origen text, guia text, "tipologiaId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
    select p.id,p.nombre,m.codigo AS tipoorigen,ma.codigo AS origen,p.guia,p.tipologiaid from prealerta p
    inner join maestro m on p.tipoorigenid=m.id inner join maestro ma on p.origenid=ma.id
    where LOWER(p.nombre) = LOWER(p_nombre)
    ) AS q(c1, c2, c3, c4, c5, c6);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelprealertserial(p_prealertaid integer, p_serial character varying)
 RETURNS TABLE(id text, serial text, codigosap text, tramite text, pedido text, caja text, falla text, "tecnicoCliente" text, novedad text, garantia text, recogida text, mac text, "loteId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text FROM (
    select id,serial,codigosap,tramite,pedido,caja,falla,tecnicocliente,novedad,garantia,COALESCE(recogida,0) AS recogida,mac,loteid from prealertaserial
    where prealertaid=p_prealertaid and serial=p_serial
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelprealertserialbycodsap(p_prealertaid integer, p_codigosap character varying)
 RETURNS TABLE(id text, serial text, codigosap text, tramite text, pedido text, caja text, falla text, "tecnicoCliente" text, novedad text, garantia text, recogida text, mac text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text FROM (
    select id,serial,codigosap,tramite,pedido,caja,falla,tecnicocliente,novedad,garantia,COALESCE(recogida,0) AS recogida,mac from prealertaserial
    where prealertaid=p_prealertaid and codigosap=p_codigosap
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getmodelsapcode(p_codigo character varying)
 RETURNS TABLE(id text, codigo text, descripcion text, "familiaId" text, familia text, "tipoId" text, tipo text, validacion text, direccion text, largos text, recorte text, reingreso text, "numSerial" text, "AsignacionAcc" text, "LargosMac" text, "LargosSerial3" text, "LargosSerial4" text, "LargosSerial5" text, clasificacion text, "validacionMac" text, "direccionMac" text, "recorteMac" text, "AsignacionFalla" text, multimodelo text, "cantidadCaja" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text, q.c20::text, q.c21::text, q.c22::text, q.c23::text, q.c24::text, q.c25::text FROM (
    select cs.id,cs.codigo,cs.descripcion,f.id AS familiaid,f.codigo AS familia,t.id AS tipoid,t.codigo AS tipo,cs.validacion,cs.direccion,cs.largos,cs.recorte,cs.reingreso,cs.numserial,cs.asignacionacc,cs.largosmac,cs.largosserial3,cs.largosserial4,cs.largosserial5,c.codigo AS clasificacion,cs.validacionmac,cs.direccionmac,cs.recortemac,cs.asignacionfalla,cs.multimodelo,cs.cantidadcaja from codigosap cs
    inner join maestro f on cs.familiaid=f.id
    inner join maestro t on cs.tipoid=t.id
    inner join maestro c on cs.clasificacionid = c.id
    where LOWER(cs.codigo) = LOWER(p_codigo)
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getnameworflowdestiny(p_opcion character varying, p_origen character varying, p_descripcion character varying, p_tipologiaid integer)
 RETURNS TABLE("Nombre" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_opcion)=LOWER('NINGUNA')) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        select d.nombre from workflowdestino wd
        inner join workflow w on wd.workflowid=w.id
        inner join general.estado d on wd.destinoid=d.id
        inner join general.estado o on w.origenid=o.id
        where LOWER(o.nombre) = LOWER(p_origen) and w.opcionid=0 and w.tipologiaid=p_tipologiaid
        ) AS q(c1);
    ELSE
        RETURN QUERY SELECT q.c1::text FROM (
        select d.nombre from workflowdestino wd
        inner join workflow w on wd.workflowid=w.id
        inner join general.estado d on wd.destinoid=d.id
        inner join general.estado o on w.origenid=o.id
        left join general.permiso op on w.opcionid=op.id
        where LOWER(o.nombre) = LOWER(p_origen) and LOWER(op.nombre) = LOWER(p_opcion) and LOWER(op.descripcion) = LOWER(p_descripcion) and w.tipologiaid=p_tipologiaid
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getoriginsmaster(p_tipo character varying)
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.codigo
    FROM maestro m
    INNER JOIN tipomaestro tm
        ON m.tipomaestroid = tm.id
    WHERE tm.nombre = 'Origenes'
      AND LOWER(m.descripcion) = LOWER(p_tipo)
      AND m.activo = true
    ORDER BY m.codigo
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getpackedaccesoriesserials(p_codigosap character varying, p_tipoaccesorio character varying, p_palletid integer)
 RETURNS TABLE("SerialEmpaque" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT serialempaque
    FROM accesorio
    WHERE codigosapid IN (SELECT id FROM codigosap WHERE LOWER(codigo) = LOWER(p_codigosap))
      AND LOWER(tipoaccesorio) = LOWER(p_tipoaccesorio)
      AND palletid = p_palletid
      AND serialempaque <> '0'
      AND estadoid IN (SELECT id FROM estado WHERE nombre = 'EMPACADO')
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getpalletbycustomer(p_clienteid integer, p_palletid integer)
 RETURNS TABLE(id text, customerid text, customer text, "position" text, date text, number text, source text, destiny text, typology text, codesap text, model text, card text, username text, quantity text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_cliente varchar(20);
    v_quantity integer;
    v_origen varchar(30);
    v_destino varchar(30);
    v_destinoid integer;
BEGIN
    DROP TABLE IF EXISTS tmp_tempends;
    CREATE TEMP TABLE tmp_tempends (id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL, customerid integer, customer varchar(30), position varchar(30), date timestamp(3), number varchar(20), source char(30), destiny char(30), typology char(30), codesap varchar(20), model varchar(40), card varchar(20), username varchar(20), quantity integer);
    DROP TABLE IF EXISTS tmp_temp;
    CREATE TEMP TABLE tmp_temp AS SELECT p.id,p.numero,cs.codigo,cs.descripcion,t.codigo AS tipologia,o.nombre AS origen,d.nombre AS destino,u.nombreusuario,p.fecha,po.numero AS posicion,'' AS tarjeta
FROM wmswdtelefonicacolombiabog.pallet p
FULL OUTER JOIN wmswdtelefonicacolombiabog.posicion po on p.posicionid=po.id
INNER JOIN wmswdtelefonicacolombiabog.codigosap cs on p.codigosapid=cs.id
INNER JOIN wmswdtelefonicacolombiabog.maestro t on p.tipologiaid=t.id
INNER JOIN wmswdtelefonicacolombiabog.estado o on p.origenid=o.id
INNER JOIN wmswdtelefonicacolombiabog.estado d on p.destinoid=d.id
INNER JOIN wmswdtelefonicacolombiabog.usuario u on p.usuarioid=u.id
WHERE p.id = p_palletid LIMIT 1;
    SELECT nombre INTO v_cliente FROM wmswdtelefonicacolombiabog.cliente WHERE id = p_clienteid;
    SELECT origen INTO v_origen FROM tmp_temp;
    IF (v_origen = 'SERIALIZADO') THEN
        SELECT COALESCE(COUNT(*),0) INTO v_quantity FROM serializado WHERE palletid = p_palletid;
    END IF;
    IF (v_origen = 'INGRESO') THEN
        SELECT COALESCE(COUNT(*),0) INTO v_quantity FROM ingreso WHERE palletid = p_palletid;
    END IF;
    INSERT INTO tmp_tempends (customerid, customer, Position, date, number, Source, destiny, typology, codesap, model, card, username, quantity)
SELECT p_clienteid, v_cliente, posicion, fecha, numero, origen, destino, tipologia, codigo, descripcion, tarjeta, nombreusuario, v_quantity FROM tmp_temp;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text FROM (
    SELECT * FROM tmp_tempends
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);
    DROP TABLE IF EXISTS tmp_temp;
    DROP TABLE IF EXISTS tmp_tempends;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getpalletnumero(p_numeropallet character varying)
 RETURNS TABLE("Existe" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pallet
        WHERE numero = p_numeropallet
    ) THEN
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT (1)::boolean AS existe
        ) AS q(c1);
    ELSE
        RETURN QUERY SELECT q.c1::text FROM (
        SELECT (0)::boolean AS existe
        ) AS q(c1);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getpalletsbyposicion(p_posicionid integer)
 RETURNS TABLE("Id" text, "Numero" text, cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT p.id,p.numero,count(i.palletid) AS cantidad FROM pallet p LEFT JOIN ingreso i ON p.id=i.palletid WHERE posicionid=p_posicionid GROUP BY p.id,p.numero
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getpeportsquery(p_clienteid integer, p_id integer, p_where character varying DEFAULT ''::character varying)
 RETURNS TABLE(resultado text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_count integer;
    v_counter integer;
    v_counterf integer;
    v_item varchar(200);
    v_cquery text;
    v_query text;
    v_fechaini varchar(10);
    v_fechafin varchar(10);
    v_variable varchar(20);
    v_valor varchar(20);
    v_variablef varchar(20);
    v_valorf varchar(20);
    v_fecha varchar(10);
    v_ccampo varchar(100);
    v_campo varchar(100);
    v_idcampo integer;
    v_tfiltro integer;
    v_exis integer;
BEGIN
    v_count := 1;
    v_counter := 1;
    v_counterf := 1;
    v_ccampo := '';
    v_campo := '';
    v_idcampo := 0;
    v_tfiltro := 0;
    DROP TABLE IF EXISTS tmp_tempcampo;
    CREATE TEMP TABLE tmp_tempcampo AS SELECT informeid,campo FROM wmswdtelefonicacolombiabog.informecampo WHERE informeid = p_id;
    ALTER TABLE tmp_tempcampo ADD COLUMN orden integer GENERATED BY DEFAULT AS IDENTITY;
    DROP TABLE IF EXISTS tmp_tempreporte;
    CREATE TEMP TABLE tmp_tempreporte (id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY);
    WHILE v_count <= (SELECT COUNT(*) FROM tmp_tempcampo) LOOP
        v_exis := 0;
        SELECT campo INTO v_campo FROM tmp_tempcampo WHERE orden = v_count;
        v_exis := (mssql.patindex('fech%', (lower((ltrim((rtrim((v_campo)::text))::text))::text))::text));
        IF (v_exis > 0) THEN
            v_cquery := 'ALTER TABLE tmp_tempreporte ADD ' || lower((v_campo)::text) || ' DATE NULL';
            EXECUTE v_cquery;
            v_campo := lower((v_campo)::text);
        ELSE
            v_cquery := 'ALTER TABLE tmp_tempreporte ADD ' || lower((v_campo)::text) || ' VARCHAR(100) NULL';
            EXECUTE v_cquery;
            v_campo := lower((v_campo)::text);
        END IF;
        IF (v_ccampo='') THEN
            v_ccampo := lower((v_campo)::text);
        ELSE
            v_ccampo := v_ccampo || ','|| lower((v_campo)::text);
        END IF;
        v_count := v_count + 1;
    END LOOP;
    SELECT COUNT(*) INTO v_tfiltro FROM wmswdtelefonicacolombiabog.filtro WHERE id IN (SELECT filtroid FROM wmswdtelefonicacolombiabog.informefiltro WHERE informeid = p_id);
    IF (p_where <> '' AND v_tfiltro > 0) THEN
        DROP TABLE IF EXISTS tmp_temptfiltro;
        CREATE TEMP TABLE tmp_temptfiltro (id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL, variable varchar(20), valor varchar(20));
        DROP TABLE IF EXISTS tmp_tempfiltro;
        CREATE TEMP TABLE tmp_tempfiltro AS SELECT * FROM wmswdtelefonicacolombiabog.filtro WHERE id IN (SELECT filtroid FROM wmswdtelefonicacolombiabog.informefiltro WHERE informeid = p_id);
        DROP TABLE IF EXISTS tmp_tempwhere;
        CREATE TEMP TABLE tmp_tempwhere AS SELECT * FROM unnest(string_to_array(rtrim((ltrim((p_where)::text))::text), ',')) AS item;
        ALTER TABLE tmp_tempwhere ADD COLUMN orden integer GENERATED BY DEFAULT AS IDENTITY;
        WHILE v_counter <= (SELECT COUNT(*) FROM tmp_tempwhere) LOOP
            SELECT rtrim((ltrim((item)::text))::text) INTO v_item FROM tmp_tempwhere WHERE orden = v_counter;
            v_variable := (substring((v_item)::text, 1, mssql.charindex(':', ( v_item)::text)-1));
            IF EXISTS(SELECT * FROM tmp_tempfiltro WHERE rtrim((ltrim((nombre)::text))::text) = rtrim((ltrim((v_variable)::text))::text)) THEN
                v_valor := substring((v_item)::text, mssql.charindex(':', ( v_item)::text)+1, (mssql.len((v_item)::text) - mssql.charindex(':', ( v_item)::text)+1));
                INSERT INTO tmp_temptfiltro(variable, valor) VALUES(v_variable,v_valor);
            END IF;
            v_counter := v_counter + 1;
        END LOOP;
        SELECT consulta INTO v_query FROM wmswdtelefonicacolombiabog.informe WHERE id = p_id AND clienteid = p_clienteid;
        WHILE v_counterf <= (SELECT COUNT(*) FROM tmp_temptfiltro) LOOP
            SELECT rtrim((ltrim((variable)::text))::text), valor INTO v_variablef, v_valorf FROM tmp_temptfiltro WHERE id = v_counterf;
            IF EXISTS(SELECT * FROM tmp_temptfiltro WHERE rtrim((ltrim((variable)::text))::text) = rtrim((ltrim((v_variablef)::text))::text)) THEN
                v_fecha := chr(39) || v_valorf || chr(39);
                v_query := replace((v_query)::text, (v_variablef)::text, (v_fecha)::text);
            END IF;
            v_counterf := v_counterf + 1;
        END LOOP;
        RAISE NOTICE '%', v_query;
        EXECUTE 'INSERT INTO tmp_tempreporte' || ' (' || v_ccampo || ') ' || v_query;
        DROP TABLE IF EXISTS tmp_temptfiltro;
        DROP TABLE IF EXISTS tmp_tempfiltro;
        DROP TABLE IF EXISTS tmp_tempwhere;
    ELSE
        IF (v_tfiltro = 0) THEN
            SELECT consulta INTO v_query FROM wmswdtelefonicacolombiabog.informe WHERE id = p_id AND clienteid = p_clienteid;
            EXECUTE 'INSERT INTO tmp_tempreporte' || ' (' || v_ccampo || ') ' || v_query;
        END IF;
    END IF;
    RETURN QUERY SELECT to_jsonb(t)::text FROM tmp_tempreporte t;  -- columnas dinámicas: cada fila como JSON
    DROP TABLE IF EXISTS tmp_tempcampo;
    DROP TABLE IF EXISTS tmp_tempreporte;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getpercentdiff()
 RETURNS TABLE("Resultado" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT
        ((SELECT COUNT(id) FROM inventario where sobrante = false))::double precision /
        (COUNT(i.id))::double precision AS resultado
    FROM
        ingreso i
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getpercentdiffsap(p_codigosapid integer)
 RETURNS TABLE("Resultado" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_ingresototal double precision;
    v_inventariototal double precision;
BEGIN
    v_ingresototal := (
        SELECT COUNT(id)
        FROM ingreso
        WHERE codigosapid = p_codigosapid
    );
    v_inventariototal := (
        SELECT COUNT(inv.id)
        FROM inventario inv
        INNER JOIN codigosap cs ON cs.codigo = inv.codigosap
        WHERE inv.sobrante = true AND cs.id = p_codigosapid
    );
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT CASE WHEN v_ingresototal = 0 THEN 0 ELSE (v_inventariototal / v_ingresototal) END AS resultado
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getproactiverepairall()
 RETURNS TABLE("Serial" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    WITH ultimodiagnostico AS ( SELECT dg.serialid, dg.estadofinalid,
                            ROW_NUMBER() OVER (PARTITION BY dg.serialid ORDER BY dg.serialid DESC) AS rn FROM diagnostico dg)
SELECT d.serial FROM despacho d
INNER JOIN ultimodiagnostico ud ON d.serialid = ud.serialid AND ud.rn = 1
INNER JOIN maestro ef ON ud.estadofinalid = ef.id
WHERE ef.codigo = 'REPARACION PROACTIVA' AND d.fecha >= mssql.dateadd('day', -180, ( LOCALTIMESTAMP)::timestamp)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getrepairuser(p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE(serial text, mac text, "Codigo" text, descripcion text, nombre_usuario text, truckrollid text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
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
        i.serial, i.mac, cs.codigo, cs.descripcion, u.nombre_usuario, i.truckrollid
    FROM ultimareparacion ur
    INNER JOIN ingreso i ON ur.serialid = i.id
    INNER JOIN general.estado e ON i.estadoid = e.id
    INNER JOIN codigosap cs ON ur.codigosapid = cs.id
    INNER JOIN usuariosys u ON ur.tecnicoasignacionid = u.id
    WHERE ur.rn = 1
      AND e.nombre = 'REPARACION-PREASIGNADO'
    ORDER BY ur.fechaasignacion DESC
    ) AS q(c1, c2, c3, c4, c5, c6);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getsapcodefailureasig(p_codigosap character varying)
 RETURNS TABLE("Asignacionfalla" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT asignacionfalla
    FROM codigosap
    WHERE LOWER(codigo) = LOWER(p_codigosap)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getscrapuser(p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE(id text, serial text, mac text, serial3 text, serial4 text, serial5 text, codigosap text, descripcion text, nivelid text, loteid text, lote text, modelo text, fecha text, rn text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text FROM (
    WITH ultimos AS (
        SELECT i.id,i.serial,i.mac,i.serial3,i.serial4,i.serial5,cs.codigo AS codigosap,cs.descripcion,i.nivelid,
        i.loteid,le.nombre AS lote,ml.codigo AS modelo,i.fecha,ROW_NUMBER() OVER (PARTITION BY i.id ORDER BY i.id DESC) AS rn
        FROM ingreso i
        INNER JOIN estado es ON i.estadoid = es.id
        INNER JOIN codigosap cs ON i.codigosapid = cs.id
        INNER JOIN usuariosys u ON i.usuarioid = u.id
        LEFT JOIN maestro ml ON ml.id =i.modeloid
        LEFT JOIN lote le ON i.loteid = le.id
        WHERE es.nombre = 'SCRAP-PREASIGNADO'
          AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR i.usuarioidmovimiento = p_usuarioid))
    SELECT * FROM ultimos
    WHERE rn = 1
    ORDER BY id DESC
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getserialbymac(p_mac character varying)
 RETURNS TABLE(serial text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT i.serial
            FROM ingreso i
            WHERE i.mac=p_mac
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getsourceincome(p_ntype character varying, p_nstate character varying)
 RETURNS TABLE("Id" text, "Origen" text, "Cantidad" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_status varchar(100);
BEGIN
    v_status := '%' || p_nstate || '%';
    IF (LOWER(p_ntype) = LOWER('ESTADO')) THEN
        DROP TABLE IF EXISTS tmp_temptablee;
        CREATE TEMP TABLE tmp_temptablee AS SELECT (row_number() OVER ())::integer AS id, s.* FROM (SELECT e.nombre AS origen, COUNT(*) AS cantidad
        FROM ingreso i
        INNER JOIN pallet p ON i.palletid = p.id
        INNER JOIN general.estado e ON p.origenid = e.id
        WHERE e.nombre ILIKE v_status
        GROUP BY e.nombre) AS s;
    ALTER TABLE tmp_temptablee ALTER COLUMN id SET NOT NULL, ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY;
    PERFORM setval(pg_get_serial_sequence('tmp_temptablee', 'id'), COALESCE((SELECT max(id) FROM tmp_temptablee), 0) + 1, false);
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
        SELECT id, origen, cantidad FROM tmp_temptablee
        ) AS q(c1, c2, c3);
        DROP TABLE IF EXISTS tmp_temptablee;
    ELSIF (LOWER(p_ntype) = LOWER('CODIGOSAP')) THEN
        DROP TABLE IF EXISTS tmp_temptablec;
        CREATE TEMP TABLE tmp_temptablec AS SELECT (row_number() OVER ())::integer AS id, s.* FROM (SELECT c.codigo AS origen, COUNT(*) AS cantidad
            FROM ingreso i
            INNER JOIN pallet p ON i.palletid = p.id
            INNER JOIN codigosap c ON i.codigosapid = c.id
            INNER JOIN general.estado e ON p.origenid = e.id
            WHERE e.nombre ILIKE v_status
            GROUP BY c.codigo) AS s;
    ALTER TABLE tmp_temptablec ALTER COLUMN id SET NOT NULL, ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY;
    PERFORM setval(pg_get_serial_sequence('tmp_temptablec', 'id'), COALESCE((SELECT max(id) FROM tmp_temptablec), 0) + 1, false);
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
        SELECT id, origen, cantidad FROM tmp_temptablec
        ) AS q(c1, c2, c3);
        DROP TABLE IF EXISTS tmp_temptablec;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getstateincome(p_ntype character varying, p_nstate character varying)
 RETURNS TABLE("Id" text, "Origen" text, "Cantidad" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_status varchar(100);
BEGIN
    v_status := '%' || p_nstate || '%';
    IF (LOWER(p_ntype) = LOWER('ESTADO')) THEN
        DROP TABLE IF EXISTS tmp_temptablee;
        CREATE TEMP TABLE tmp_temptablee AS SELECT (row_number() OVER ())::integer AS id, s.* FROM (SELECT e.nombre AS origen, COUNT(*) AS cantidad
        FROM ingreso i
        INNER JOIN general.estado e ON i.estadoid = e.id
        WHERE e.nombre ILIKE v_status
        GROUP BY e.nombre) AS s;
    ALTER TABLE tmp_temptablee ALTER COLUMN id SET NOT NULL, ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY;
    PERFORM setval(pg_get_serial_sequence('tmp_temptablee', 'id'), COALESCE((SELECT max(id) FROM tmp_temptablee), 0) + 1, false);
        IF EXISTS (SELECT * from tmp_temptablee) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            SELECT id, origen, cantidad FROM tmp_temptablee
            ) AS q(c1, c2, c3);
        ELSE
            INSERT INTO tmp_temptablee (origen, cantidad) VALUES ('ESTADO', 0);
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            SELECT id, origen, cantidad FROM tmp_temptablee
            ) AS q(c1, c2, c3);
        END IF;
        DROP TABLE IF EXISTS tmp_temptablee;
    ELSIF (LOWER(p_ntype) = LOWER('CODIGOSAP')) THEN
        DROP TABLE IF EXISTS tmp_temptablec;
        CREATE TEMP TABLE tmp_temptablec AS SELECT (row_number() OVER ())::integer AS id, s.* FROM (SELECT c.codigo AS origen, COUNT(*) AS cantidad
            FROM ingreso i
            INNER JOIN codigosap c ON i.codigosapid = c.id
            INNER JOIN general.estado e ON i.estadoid = e.id
            WHERE e.nombre ILIKE v_status
            GROUP BY c.codigo) AS s;
    ALTER TABLE tmp_temptablec ALTER COLUMN id SET NOT NULL, ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY;
    PERFORM setval(pg_get_serial_sequence('tmp_temptablec', 'id'), COALESCE((SELECT max(id) FROM tmp_temptablec), 0) + 1, false);
        IF EXISTS (SELECT * from tmp_temptablec) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            SELECT id, origen, cantidad FROM tmp_temptablec
            ) AS q(c1, c2, c3);
        ELSE
            INSERT INTO tmp_temptablec (origen, cantidad) VALUES ('CODIGOSAP', 0);
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            SELECT id, origen, cantidad FROM tmp_temptablec
            ) AS q(c1, c2, c3);
        END IF;
        DROP TABLE IF EXISTS tmp_temptablec;
    ELSIF (LOWER(p_ntype) = LOWER('FAMILIA')) THEN
        DROP TABLE IF EXISTS tmp_temptablef;
        CREATE TEMP TABLE tmp_temptablef AS SELECT (row_number() OVER ())::integer AS id, s.* FROM (SELECT m.codigo AS origen, COUNT(*) AS cantidad
                FROM ingreso i
                INNER JOIN codigosap c ON i.codigosapid = c.id
                INNER JOIN maestro m ON c.familiaid = m.id
                INNER JOIN general.estado e ON i.estadoid = e.id
                WHERE e.nombre ILIKE v_status
                GROUP BY m.codigo) AS s;
    ALTER TABLE tmp_temptablef ALTER COLUMN id SET NOT NULL, ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY;
    PERFORM setval(pg_get_serial_sequence('tmp_temptablef', 'id'), COALESCE((SELECT max(id) FROM tmp_temptablef), 0) + 1, false);
        IF EXISTS (SELECT * from tmp_temptablef) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            SELECT id, origen, cantidad FROM tmp_temptablef
            ) AS q(c1, c2, c3);
        ELSE
            INSERT INTO tmp_temptablef (origen, cantidad) VALUES ('FAMILIA', 0);
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            SELECT id, origen, cantidad FROM tmp_temptablef
            ) AS q(c1, c2, c3);
        END IF;
        DROP TABLE IF EXISTS tmp_temptablef;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_gettipologymaster(p_descripcion1 character varying, p_descripcion2 character varying, p_descripcion3 character varying, p_descripcion4 character varying)
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT codigo
    FROM maestro
    WHERE activo = true
      AND (
            LOWER(descripcion) = LOWER(p_descripcion1)
         OR LOWER(descripcion) = LOWER(p_descripcion2)
         OR LOWER(descripcion) = LOWER(p_descripcion3)
         OR LOWER(descripcion) = LOWER(p_descripcion4)
      )
    ORDER BY codigo
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_gettipoorigen(p_tipoorigenid integer)
 RETURNS TABLE("TipoOrigen" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT codigo AS tipoorigen FROM maestro WHERE id=p_tipoorigenid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_gettotalprealert(p_id integer)
 RETURNS TABLE(total text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select count(serial) AS total from prealertaserial where prealertaid=p_id
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_gettrackingserie(p_serial character varying)
 RETURNS TABLE(id text, ciclo text, serialid text, serial text, fecha text, fechamovimiento text, estado text, origen text, destino text, nombreusuario text, descripcion text, icono text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_serialid integer;
    v_ciclo integer;
    v_id integer;
    v_cur integer;
    insincome CURSOR FOR SELECT serialid FROM tmp_tempciclo ORDER BY serialid;
BEGIN
    DROP TABLE IF EXISTS tmp_tempciclo;
    CREATE TEMP TABLE tmp_tempciclo AS SELECT DISTINCT serialid
FROM movimiento
WHERE serial=p_serial
GROUP BY serialid;
    SELECT DISTINCT COUNT(serialid) INTO v_ciclo FROM tmp_tempciclo;
    DROP TABLE IF EXISTS tmp_tempmovs;
    CREATE TEMP TABLE tmp_tempmovs (id integer, ciclo integer, serialid integer, serial varchar(50), fecha date, fechamovimiento timestamp(3), estado varchar(50), origen varchar(50), destino varchar(50), nombreusuario varchar(20), descripcion varchar(100), icono varchar(50));
    IF (v_ciclo = 1) THEN
        SELECT DISTINCT serialid INTO v_serialid FROM movimiento WHERE serial=p_serial GROUP BY serialid;
        DROP TABLE IF EXISTS tmp_temptablem;
        CREATE TEMP TABLE tmp_temptablem AS SELECT (row_number() OVER ())::integer AS id, s.* FROM (SELECT DISTINCT v_ciclo AS ciclo, m.serialid,m.serial,to_date((m.fecha)::text, 'MM/DD/YYYY') AS fecha,m.fecha AS fechamovimiento,upper((md.nombre)::text) AS estado,o.nombre AS origen,d.nombre AS destino,u.nombreusuario,m.descripcion, md.icono
    FROM movimiento m
    INNER JOIN general.estado o on m.origenid=o.id
    INNER JOIN general.estado d on m.destinoid=d.id
    INNER JOIN general.usuario u on m.usuarioid=u.id
    INNER JOIN general.modulo md on d.moduloid = md.id
    WHERE m.serialid=v_serialid
    GROUP BY m.fecha, m.id, m.serialid, m.serial, md.nombre, o.nombre, d.nombre, u.nombreusuario, m.descripcion, md.icono) AS s;
    ALTER TABLE tmp_temptablem ALTER COLUMN id SET NOT NULL, ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY;
    PERFORM setval(pg_get_serial_sequence('tmp_temptablem', 'id'), COALESCE((SELECT max(id) FROM tmp_temptablem), 0) + 1, false);
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text FROM (
    SELECT * FROM tmp_temptablem
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12);
        DROP TABLE IF EXISTS tmp_temptablem;
    ELSE
        v_id := 1;
        v_cur := 1;
        OPEN insincome;
        FETCH insincome INTO v_serialid;
        WHILE (FOUND) LOOP
            INSERT INTO tmp_tempmovs
            SELECT DISTINCT v_id AS id, v_cur AS ciclo, m.serialid,m.serial,to_date((m.fecha)::text, 'MM/DD/YYYY') AS fecha,m.fecha AS fechamovimiento,
            upper((md.nombre)::text) AS estado,o.nombre AS origen,d.nombre AS destino,u.nombreusuario,m.descripcion, md.icono
            FROM movimiento m
            INNER JOIN general.estado o on m.origenid=o.id
            INNER JOIN general.estado d on m.destinoid=d.id
            INNER JOIN general.usuario u on m.usuarioid=u.id
            INNER JOIN general.modulo md on d.moduloid = md.id
            WHERE m.serialid=v_serialid
            GROUP BY m.fecha, m.id, m.serialid, m.serial, md.nombre, o.nombre, d.nombre, u.nombreusuario, m.descripcion, md.icono;
            v_id := v_id + 1;
            FETCH insincome INTO v_serialid;
            v_cur := v_cur + 1;
        END LOOP;
        CLOSE insincome;
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text FROM (
    SELECT * FROM tmp_tempmovs
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12);
    END IF;
    DROP TABLE IF EXISTS tmp_tempciclo;
    DROP TABLE IF EXISTS tmp_tempmovs;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getunreadablereceiptbypallet(p_pallet character varying DEFAULT NULL::character varying)
 RETURNS TABLE("Id" text, "Numero" text, "Codigo" text, "Descripcion" text, "Cantidad" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_pallet is null) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id, p.numero, cs.codigo, cs.descripcion, COUNT(i.serial) AS cantidad
    from pallet p
    inner join ingreso i on p.id=i.palletid
    inner join codigosap cs on cs.id=p.codigosapid
    inner join general.estado d on p.destinoid=d.id
    where d.nombre='P-CONFIRMACION' and p.activo=true
    group by p.id,p.numero,cs.codigo,cs.descripcion
        ) AS q(c1, c2, c3, c4, c5);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id, p.numero, cs.codigo, cs.descripcion, COUNT(i.serial) AS cantidad
    from pallet p
    inner join ingreso i on p.id=i.palletid
    inner join codigosap cs on cs.id=p.codigosapid
    inner join general.estado d on p.destinoid=d.id
    where p.numero= p_pallet and d.nombre='P-CONFIRMACION' and p.activo=true
    group by p.id,p.numero,cs.codigo,cs.descripcion
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getvalidastateinventory(p_estado character varying, p_padreinventario character varying)
 RETURNS TABLE("Nombre" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT nombre FROM estado
            WHERE LOWER(nombre) = LOWER(p_estado) AND padreinventario = p_padreinventario
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getwarrantymaster()
 RETURNS TABLE(codigo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.codigo FROM maestro m
    INNER JOIN tipomaestro tm ON m.tipomaestroid=tm.id
    WHERE tm.nombre='Garantia'
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_inactivateallboxdispatch(p_cajadespachoid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE cajadespacho
    SET activo = (0)::boolean
    WHERE (palletid=p_cajadespachoid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_inactivateallboxentry(p_palletid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE cajaingreso
    SET activo = (0)::boolean
    WHERE (palletid=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_inactivateboxdispatch(p_cajadespachoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE cajadespacho
    SET activo = (0)::boolean
    WHERE (id=p_cajadespachoid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_inactivateboxpacking(p_cajaempaqueid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE cajaempaque
    SET activo = (0)::boolean
    WHERE (id=p_cajaempaqueid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_informemillicomreverselabprocessed()
 RETURNS TABLE(country text, processed_date text, model_code text, model_description text, cpe_category text, dx_type text, serial_1 text, serial_2 text, serial_3 text, lab_phase text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DROP TABLE IF EXISTS tmp_salvador;
    CREATE TEMP TABLE tmp_salvador AS select 'EL SALVADOR' AS country, (i.fechamovimiento)::date AS processed_date,cs.codigo AS model_code, replace((cs.descripcion)::text, '|', ' ') AS model_description,f.codigo AS cpe_category,
    o.codigo AS dx_type, replace((i.serial)::text, '|', ' ') AS serial_1, replace((i.mac)::text, '|', ' ') AS serial_2, CASE WHEN i.serial3 IS NULL THEN '' ELSE replace((i.serial3)::text, '|', ' ') END AS serial_3, e.nombre AS lab_phase
    from ingreso i
    left join codigosap cs on i.codigosapid=cs.id
    left join maestro f on cs.familiaid=f.id
    left join pallet p on i.palletid=p.id
    left join posicion po on p.posicionid=po.id
    left join maestro o on i.origenid=o.id
    left join estado e on i.estadoid=e.id
    left join(select serial,sum(cantidad) AS totalingresos from
    (select serial,count(serial) AS cantidad from ingreso group by serial union all
    select serial,count(serial) AS cantidad from despacho group by serial)as tabla group by serial)as tabla2 on i.serial=tabla2.serial;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text FROM (
    SELECT * FROM tmp_salvador
    ORDER BY country, processed_date ASC
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
    DROP TABLE IF EXISTS tmp_salvador;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_informemillicomreverselabproduced()
 RETURNS TABLE(country text, production_date text, model_code text, model_description text, cpe_category text, dx_type text, serial_1 text, serial_2 text, serial_3 text, repair_level text, diagnostic text, repair_cost text, currency text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DROP TABLE IF EXISTS tmp_empaquesal;
    CREATE TEMP TABLE tmp_empaquesal AS select 'EL SALVADOR' AS country, (e.fecha)::date AS production_date,cs.codigo AS model_code, replace((cs.descripcion)::text, '|', ' ') AS model_description, f.codigo AS cpe_category, ('')::varchar(50) AS dx_type, replace((e.serial)::text, '|', ' ') AS serial_1, replace((e.mac)::text, '|', ' ') AS serial_2, CASE WHEN COALESCE(i.serial3, '')='' THEN replace((i.serial3)::text, '|', ' ') END AS serial_3,
    (concat('NIVEL ',(n.codigo)::integer-1))::varchar(20) AS repair_level,('')::varchar(100) AS diagnostic,
    COALESCE(t.precio,0) AS repair_cost, 'LC, USD' AS currency
    from wmswdtigosal.empaque e
    left join wmswdtigosal.maestro n on e.nivelid = n.id
    inner join wmswdtigosal.codigosap cs on e.codigosapid=cs.id
    inner join wmswdtigosal.maestro f on cs.familiaid=f.id
    left join wmswdtigosal.tarifa t on e.nivelid=t.nivelid
    left join wmswdtigosal.ingreso i on e.serial=i.serial
    where e.fecha between ( '2023-07-01 00:00:00.001')::timestamp(3) and ('2023-07-15 23:59:59.001')::timestamp(3)
    ORDER BY e.fecha ASC;
    DROP TABLE IF EXISTS tmp_serializadoscrapsal;
    CREATE TEMP TABLE tmp_serializadoscrapsal AS select 'EL SALVADOR' AS country, (r.fechareparacion)::date AS production_date,cs.codigo AS model_code, replace((cs.descripcion)::text, '|', ' ') AS model_description, f.codigo AS cpe_category, case when COALESCE(oi.codigo,'')='' then od.codigo else oi.codigo end AS dx_type,replace((r.serial)::text, '|', ' ') AS serial_1, replace((r.mac)::text, '|', ' ') AS serial_2, CASE WHEN COALESCE(i.serial3, '')='' THEN replace((i.serial3)::text, '|', ' ') END AS serial_3,
    'SCRAP' AS repair_level,ms.descripcion AS diagnostic,
    case when COALESCE(ti.precio,'0')='0' then td.precio else ti.precio end AS repair_cost, 'LC, USD' AS currency
    from wmswdtigosal.reparacion r
    inner join wmswdtigosal.codigosap cs on r.codigosapid=cs.id
    inner join wmswdtigosal.maestro ms on r.motivoscrapid=ms.id
    inner join wmswdtigosal.maestro f on cs.familiaid=f.id
    left join wmswdtigosal.ingreso i on r.serialid=i.id
    left join wmswdtigosal.maestro oi on i.origenid=oi.id
    left join wmswdtigosal.tarifa ti on i.nivelid=ti.nivelid
    left join wmswdtigosal.despacho d on r.serialid=d.serialid
    left join wmswdtigosal.maestro od on d.origenid=od.id
    left join wmswdtigosal.tarifa td on d.nivelid=td.nivelid
    left join wmswdtigosal.ingreso ing on r.serial=ing.serial
    where r.fechareparacion between ( '2023-07-01 00:00:00.001')::timestamp(3) and ('2023-07-15 23:59:59.001')::timestamp(3)
    ORDER BY r.fechareparacion ASC;
    UPDATE tmp_empaquesal AS e
    SET dx_type = COALESCE(
        (SELECT m.codigo FROM wmswdtigosal.maestro m WHERE m.id = i.origenid),
        (SELECT md.codigo FROM wmswdtigosal.despacho d JOIN wmswdtigosal.maestro md ON d.origenid = md.id
          WHERE e.serial_1 = d.serial LIMIT 1))
    FROM wmswdtigosal.ingreso i
    WHERE (e.serial_1 = i.serial);
    DROP TABLE IF EXISTS tmp_diagnosticosal;
    CREATE TEMP TABLE tmp_diagnosticosal AS select e.serial_1,MAX(fecha) AS fecha,fd.codigo,
    CASE WHEN (fd.codigo='') OR (fd.codigo=NULL) THEN 'NO ENCIENDE' ELSE fd.descripcion END AS falla from tmp_empaquesal e
    inner join wmswdtigosal.diagnostico d on e.serial_1=d.serial
    inner join wmswdtigosal.maestro fd on d.fallaid=fd.id
    where e.repair_level in('NIVEL 0','NIVEL 1') and d.fecha=(select max(fecha) from wmswdtigosal.diagnostico where serial=e.serial_1)
    group by e.serial_1,fd.codigo,fd.descripcion
    order by e.serial_1;
    UPDATE tmp_empaquesal AS e
    SET diagnostic = d.falla
    FROM tmp_diagnosticosal d
    WHERE (e.serial_1=d.serial_1);
    DROP TABLE IF EXISTS tmp_reparacionsal;
    CREATE TEMP TABLE tmp_reparacionsal AS select e.serial_1,MAX(r.fechareparacion) AS fecha,
    CASE WHEN (e.repair_level='NIVEL 2')
        THEN
        CASE WHEN (fr.codigo='SF')
            THEN 'CAIDA DE SEÑAL'
            ELSE fr.descripcion END
        ELSE
        CASE WHEN (fr.descripcion='') OR (fr.descripcion=NULL)
            THEN 'NO ENCIENDE'
            ELSE fr.descripcion END
    END AS falla from tmp_empaquesal e
    inner join wmswdtigosal.reparacion r on e.serial_1=r.serial
    inner join wmswdtigosal.maestro fr on r.falla1id=fr.id
    where e.repair_level ='NIVEL 2' and r.fechareparacion=(select max(fechareparacion) from wmswdtigosal.reparacion where serial=e.serial_1)
    group by e.serial_1,e.repair_level,fr.codigo,fr.descripcion
    order by e.serial_1;
    UPDATE tmp_empaquesal AS e
    SET diagnostic = r.falla
    FROM tmp_reparacionsal r
    WHERE (e.serial_1=r.serial_1);
    UPDATE tmp_empaquesal
    SET diagnostic = 'CAIDA DE SEÑAL'
    WHERE (diagnostic='' AND repair_level='NIVEL 2');
    UPDATE tmp_empaquesal
    SET diagnostic = 'SIN FALLA'
    WHERE (diagnostic='' AND (repair_level='NIVEL 0' OR repair_level='NIVEL 1'));
    UPDATE tmp_empaquesal
    SET serial_2 = ''
    WHERE (serial_2 IS NULL);
    UPDATE tmp_empaquesal
    SET serial_3 = ''
    WHERE (serial_3 IS NULL);
    UPDATE tmp_serializadoscrapsal
    SET serial_2 = ''
    WHERE (serial_2 IS NULL);
    UPDATE tmp_serializadoscrapsal
    SET serial_3 = ''
    WHERE (serial_3 IS NULL);
    UPDATE tmp_empaquesal
    SET diagnostic = 'FALLA HDMI'
    WHERE (diagnostic ILIKE '%HDMI%');
    UPDATE tmp_serializadoscrapsal
    SET diagnostic = 'FALLA HDMI'
    WHERE (diagnostic ILIKE '%HDMI%');
    UPDATE tmp_empaquesal
    SET diagnostic = 'FALLA WIFI'
    WHERE (diagnostic ILIKE '%WIFI%');
    UPDATE tmp_serializadoscrapsal
    SET diagnostic = 'FALLA WIFI'
    WHERE (diagnostic ILIKE '%WIFI%');
    UPDATE tmp_empaquesal
    SET diagnostic = 'DOBLE COMPONENTE'
    WHERE (diagnostic ILIKE '%COMPONENTE%');
    UPDATE tmp_serializadoscrapsal
    SET diagnostic = 'DOBLE COMPONENTE'
    WHERE (diagnostic ILIKE '%COMPONENTE%');
    UPDATE tmp_empaquesal
    SET diagnostic = 'CAIDA DE SEÑAL'
    WHERE (diagnostic ILIKE '%SEÑAL%');
    UPDATE tmp_serializadoscrapsal
    SET diagnostic = 'CAIDA DE SEÑAL'
    WHERE (diagnostic ILIKE '%SEÑAL%');
    UPDATE tmp_empaquesal
    SET diagnostic = 'RECEPCION REMOTA'
    WHERE (diagnostic ILIKE '%REMOTA%');
    UPDATE tmp_serializadoscrapsal
    SET diagnostic = 'RECEPCION REMOTA'
    WHERE (diagnostic ILIKE '%REMOTA%');
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text FROM (
    select * from tmp_empaquesal union all select * from tmp_serializadoscrapsal
    ORDER BY country, production_date
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13);
    DROP TABLE IF EXISTS tmp_empaquesal;
    DROP TABLE IF EXISTS tmp_serializadoscrapsal;
    DROP TABLE IF EXISTS tmp_diagnosticosal;
    DROP TABLE IF EXISTS tmp_reparacionsal;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivateciudad(p_id integer, p_estado integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ciudad
    SET activo = (p_estado)::boolean
    WHERE (id=p_id);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivatelote(p_id integer, p_estado integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE lote
    SET activo = (p_estado)::boolean
    WHERE (id=p_id);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivatemaster(p_estado integer, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE maestro
    SET activo = (p_estado)::boolean
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivatemastertype(p_tipomaestroid integer, p_estado integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE tipomaestro
    SET activo = (p_estado)::boolean
    WHERE (id=p_tipomaestroid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivatepallet(p_palletid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE pallet
    SET activo = (0)::boolean
    WHERE (id=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivateprealert(p_id integer, p_estado integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE prealerta
    SET activo = (p_estado)::boolean
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivatesapcode(p_id integer, p_estado integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE codigosap
    SET activo = (p_estado)::boolean
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivatesapcodefailure(p_codigosapid integer, p_fallaid integer, p_estado integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE codigosapfalla
    SET componenteobligatorio = (p_estado)::boolean
    WHERE (codigosapid=p_codigosapid AND fallaid = p_fallaid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_innactivatetransportadora(p_id integer, p_estado integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE transportadora
    SET activo = (p_estado)::boolean
    WHERE (id=p_id);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertacccodigosap(p_codigosapid integer, p_accesorioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        insert into CodigoSapAccesorio(codigosapid,accesorioid)values(p_codigosapid,p_accesorioid);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertaccesory(p_codigosapid integer, p_tipoaccesorio character varying, p_tipoorigenid integer, p_origenid integer, p_palletid integer, p_estadoid integer, p_documento character varying, p_observacion character varying, p_guia character varying, p_usuarioid integer, p_caja integer, p_prealertaid integer, p_cruce boolean)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Accesorio(codigosapid,tipoaccesorio,tipoorigenid,origenid,palletid,estadoid,documento,observacion,guia,usuarioid,fecha,caja,prealertaid,cruce)
            VALUES(p_codigosapid,p_tipoaccesorio,p_tipoorigenid,p_origenid,p_palletid,p_estadoid,p_documento,p_observacion,p_guia,p_usuarioid,LOCALTIMESTAMP,p_caja,p_prealertaid, (p_cruce)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertassemble(p_serialid integer, p_serial character varying, p_mac character varying, p_serial3 character varying, p_serial4 character varying, p_serial5 character varying, p_codigosapid integer, p_palletid integer, p_estadoid integer, p_tipologiaid integer, p_nivelid integer, p_usuarioid integer, p_loteid integer, p_smartcard character varying, p_usuarioidasignado integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO wmswdtelefonicacolombiabog.ensamble (
                serialid,
                serial,
                mac,
                serial3,
                serial4,
                serial5,
                codigosapid,
                palletid,
                estadoid,
                tipologiaid,
                nivelid,
                usuarioid,
                usuarioidasignado,
                fecha,
                loteid,
                smartcard
            )
            VALUES (
                p_serialid,
                p_serial,
                p_mac,
                p_serial3,
                p_serial4,
                p_serial5,
                p_codigosapid,
                p_palletid,
                p_estadoid,
                p_tipologiaid,
                p_nivelid,
                p_usuarioid,
                p_usuarioidasignado,
                LOCALTIMESTAMP,
                p_loteid,
                p_smartcard
            );
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertboxdispatch(p_numero character varying, p_palletid integer, p_usuarioid integer, p_fecha timestamp without time zone)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO CajaDespacho(numero,palletid,usuarioid,fecha,activo)
        VALUES(p_numero,p_palletid,p_usuarioid,p_fecha, (1)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertboxentry(p_numero character varying, p_palletid integer, p_usuarioid integer, p_fecha timestamp without time zone)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO CajaIngreso(numero,palletid,usuarioid,fecha,activo)
                VALUES(p_numero,p_palletid,p_usuarioid,p_fecha, (1)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertboxpacking(p_numero character varying, p_palletid integer, p_estadoid integer, p_usuarioid integer, p_fecha timestamp without time zone)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO CajaEmpaque(numero,palletid,estadoid,usuarioid,fecha,activo)
        VALUES(p_numero,p_palletid,p_estadoid,p_usuarioid,p_fecha, (1)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertciclic(p_nombre character varying, p_usuarioid integer, p_estado character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Ciclico(nombre,usuarioid,estado)
            VALUES(p_nombre,p_usuarioid,p_estado);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertciudad(p_nombre character varying, p_codigociudad character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Ciudad(nombre,codigociudad,activo)
                    VALUES(p_nombre,p_codigociudad, (1)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertclasification(p_serial character varying, p_estadoenviado character varying, p_usuarioid integer, p_fecha timestamp without time zone, p_usuarioasignadoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO Clasificacion(serialid,serial,mac, serial3, serial4, serial5, codigosapid, nivelid, loteid, estadoenviado, usuarioid, fecha, usuarioasignadoid)
    SELECT id,serial,mac,
                        serial3,
                        serial4,
                        serial5,
                        codigosapid,
                        nivelid,
                        loteid,
                        p_estadoenviado,
                        p_usuarioid,
                        p_fecha,
                        p_usuarioasignadoid
    FROM ingreso
    WHERE serial = p_serial;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertclasificationweb(p_serial character varying, p_usuarioid integer, p_usuarioasignadoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO Clasificacion(serialid,serial,mac,serial3,serial4,serial5,codigosapid,nivelid,estadoenviado,loteid,usuarioid,fecha,usuarioasignadoid)
    SELECT id,serial,mac,serial3,serial4,serial5,codigosapid,0,0,loteid,p_usuarioid,LOCALTIMESTAMP,p_usuarioasignadoid
    FROM ingreso
    WHERE serial=p_serial;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertcleaning(p_serialid integer, p_serial character varying, p_mac character varying, p_serial3 character varying, p_codigosapid integer, p_usuarioid integer, p_usuarioasignadoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO wmswdtelefonicacolombiabog.limpieza (
                serialid,
                serial,
                mac,
                serial3,
                codigosapid,
                usuarioid,
                usuarioasignadoid,
                fecha
            )
            VALUES (
                p_serialid,
                p_serial,
                p_mac,
                p_serial3,
                p_codigosapid,
                p_usuarioid,
                p_usuarioasignadoid,
                LOCALTIMESTAMP
            );
    EXCEPTION WHEN OTHERS THEN
        RAISE;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertcodigosapfalla(p_codigosapid integer, p_fallaid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO CodigoSapFalla (codigosapid, fallaid)
            VALUES (p_codigosapid, p_fallaid);
    EXCEPTION WHEN OTHERS THEN
        RAISE;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertcomponentsasig(p_serial character varying, p_componenteid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO SerialComponente(serialid,serial,componenteid)VALUES((SELECT id FROM ingreso WHERE serial = p_serial),p_serial,p_componenteid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertcosmetica(p_serial character varying, p_usuarioid integer, p_fecha timestamp without time zone)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO Cosmetica(serialid,serial,mac,serial3,serial4,serial5,codigosapid,palletid,nivelid,loteid,usuarioid,fecha)
            SELECT id,serial,mac,serial3,serial4,serial5,codigosapid,palletid,nivelid,loteid,p_usuarioid,p_fecha FROM ingreso
            WHERE serial =p_serial;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertdiagnostic(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_variable1 character varying, p_variable2 character varying, p_variable3 character varying, p_variable4 character varying, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Diagnostico(serialid,serial,mac,codigosapid,variable1,variable2,variable3,variable4,estadofinalid,fallaid,usuarioid,fecha)
            VALUES(p_serialid,p_serial,p_mac,p_codigosapid,p_variable1,p_variable2,p_variable3,p_variable4,0,0,p_usuarioid,LOCALTIMESTAMP);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertdiagnosticweb(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        IF p_codigosapid=50 THEN
            INSERT INTO Diagnostico(serialid,serial,mac,codigosapid,estadofinalid,fallaid,usuarioid,fecha)
            VALUES(p_serialid,substring((p_serial)::text,14,12),p_mac,p_codigosapid,0,0,p_usuarioid,LOCALTIMESTAMP);
        ELSE
            INSERT INTO Diagnostico(serialid,serial,mac,codigosapid,estadofinalid,fallaid,usuarioid,fecha)
            VALUES(p_serialid,p_serial,p_mac,p_codigosapid,0,0,p_usuarioid,LOCALTIMESTAMP);
        END IF;
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertdispatch(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_palletid integer, p_palletidingreso integer, p_cajadespachoid integer, p_estadoid integer, p_tipoorigenid integer, p_origenid integer, p_tipologiaid integer, p_nivelid integer, p_tramite character varying, p_documento character varying, p_guia character varying, p_falla character varying, p_prealertaid integer, p_cruce integer, p_novedad character varying, p_usuarioid integer, p_fechaingreso timestamp without time zone, p_pedidosap character varying, p_smartcardid integer, p_smartcard character varying, p_loteid integer, p_serial3 character varying, p_cajaingresoid integer, p_numerosmartcard character varying, p_fallacosmeticaid integer, p_fallafuncionalid integer, p_causa character varying, p_observaciones character varying DEFAULT NULL::character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Despacho(serialid,serial,mac, codigosapid, palletid, palletidingreso, cajadespachoid, estadoid, tipoorigenid, origenid, tipologiaid, nivelid, tramite, documento, guia, falla, prealertaid, cruce, novedad, usuarioid, fechaingreso, fecha, pedidosap, smartcardid, smartcard, loteid, serial3, cajaingresoid, numerosmartcard, fallacosmeticaid, fallafuncionalid,causa)
    VALUES(p_serialid,
           p_serial,
           p_mac,
           p_codigosapid,
           p_palletid,
           p_palletidingreso,
           p_cajadespachoid,
           p_estadoid,
           p_tipoorigenid,
           p_origenid,
           p_tipologiaid,
           p_nivelid,
           p_tramite,
           p_documento,
           p_guia,
           p_falla,
           p_prealertaid, (
           p_cruce)::boolean,
           p_novedad,
           p_usuarioid,
           p_fechaingreso,
           LOCALTIMESTAMP,
           p_pedidosap,
           p_smartcardid,
           p_smartcard,
           p_loteid,
           p_serial3,
           p_cajaingresoid,
           p_numerosmartcard,
           p_fallacosmeticaid,
           p_fallafuncionalid,
           p_causa);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertdispatchaccesory(p_accesoryid integer, p_codigosapid integer, p_tipoaccesorio character varying, p_tipoorigenid integer, p_origenid integer, p_palletid integer, p_estadoid integer, p_estadolimpiezaid integer, p_documento character varying, p_observacion character varying, p_guia character varying, p_usuarioid integer, p_fechaingreso timestamp without time zone, p_serialempaque character varying, p_caja integer, p_pedidosap character varying, p_fechalimpieza timestamp without time zone, p_fechaempaque timestamp without time zone, p_usuariolimpiezaid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO DespachoAccesorio(accesoryid, codigosapid, tipoaccesorio, tipoorigenid, origenid, palletid, estadoid, estadolimpiezaid, documento, observacion, guia, usuarioid, fechaingreso, serialempaque, caja, fecha, pedidosap, fechalimpieza, fechaempaque, usuariolimpiezaid)
            VALUES(p_accesoryid, p_codigosapid, p_tipoaccesorio, p_tipoorigenid, p_origenid, p_palletid, p_estadoid, p_estadolimpiezaid, p_documento, p_observacion, p_guia, p_usuarioid, p_fechaingreso, p_serialempaque, p_caja, LOCALTIMESTAMP, p_pedidosap, p_fechalimpieza, p_fechaempaque, p_usuariolimpiezaid);
    EXCEPTION WHEN OTHERS THEN
        RAISE;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertdispatchboxfrompackedbox(p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO CajaDespacho(numero,palletid,usuarioid,fecha,activo)
        SELECT numero,palletid,usuarioid,LOCALTIMESTAMP, (activo)::boolean
        FROM cajaempaque Where palletid =p_palletid;
    UPDATE ingreso AS i
    SET cajadespachoid = cajadespacho.id
    FROM cajaempaque INNER JOIN cajadespacho ON cajaempaque.numero = cajadespacho.numero
    WHERE (i.cajaempaqueid = cajaempaque.id) AND (i.palletid = p_palletid AND cajadespacho.palletid = p_palletid);
    UPDATE ingreso
    SET estadoid = (SELECT id FROM estado WHERE nombre = 'PREDESPACHADO')
    WHERE (palletid = p_palletid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertentry(p_serial character varying, p_mac character varying, p_serial3 character varying, p_serial4 character varying, p_serial5 character varying, p_codigosapid integer, p_palletid integer, p_estadoid integer, p_tipoorigenid integer, p_origenid integer, p_tipologiaid integer, p_nivelid integer, p_tramite character varying, p_documento character varying, p_guia character varying, p_caja integer, p_falla character varying, p_tecnicocliente character varying, p_prealertaid integer, p_cruce integer, p_novedad character varying, p_garantiafabricante integer, p_usuarioid integer, p_observaciones character varying, p_estadocliente character varying, p_loteid integer, p_cajaingresoid integer, p_modeloid integer, INOUT p_codigoerror integer, INOUT p_mensajeerror character varying)
 RETURNS record
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_ingresoid integer;
    v_ticketid integer;
    v_etapaanterior integer;
BEGIN
    BEGIN
        INSERT INTO Ingreso(
                serial, mac, serial3, serial4, serial5, codigosapid, palletid, palletidingreso,
                estadoid, tipoorigenid, origenid, tipologiaid, nivelid, tramite, documento, guia,
                caja, falla, tecnicocliente, prealertaid, cruce, novedad, garantiafabricante,
                usuarioid, fecha, usuarioidmovimiento, fechamovimiento, observaciones,
                estadocliente, loteid, cajaingresoid, modeloid
            )
            VALUES (
                upper((p_serial)::text), upper((p_mac)::text), p_serial3, upper((p_serial4)::text), upper((p_serial5)::text),
                p_codigosapid, p_palletid, p_palletid, p_estadoid, p_tipoorigenid, p_origenid,
                p_tipologiaid, p_nivelid, p_tramite, p_documento, p_guia, p_caja, p_falla,
                p_tecnicocliente, p_prealertaid, ( p_cruce)::boolean, p_novedad, ( p_garantiafabricante)::boolean,
                p_usuarioid, LOCALTIMESTAMP, p_usuarioid, LOCALTIMESTAMP, p_observaciones,
                p_estadocliente, p_loteid, p_cajaingresoid, p_modeloid
            );
        BEGIN
            v_ingresoid := lastval();
            v_ticketid := NULL;
            v_etapaanterior := NULL;
            SELECT id, id_etapa INTO v_ticketid, v_etapaanterior
                FROM app_pqrs_tickets
                WHERE upper((mac)::text) = upper((p_serial)::text)
                  AND id_etapa IN (1, 2)
                  AND (id_ingreso IS NULL OR id_ingreso = 0)
                ORDER BY id DESC LIMIT 1;
            IF v_ticketid IS NOT NULL THEN
                UPDATE app_pqrs_tickets
                SET id_ingreso = v_ingresoid,
                    id_etapa = 3,
                    updated_at = mssql.dateadd('hour', -5, ( LOCALTIMESTAMP)::timestamp)
                WHERE (id = v_ticketid);
                INSERT INTO App_PQRS_StatusLog
                        (id_ticket, id_etapa_anterior, id_etapa_nueva, id_usuario, comentario, auto_change)
                    VALUES
                        (v_ticketid, v_etapaanterior, 3, p_usuarioid,
                         'Ingreso WMS detectado - Serial: ' || upper((p_serial)::text), ( 1)::boolean);
            END IF;
        EXCEPTION WHEN OTHERS THEN
            NULL;
        END;
    EXCEPTION WHEN OTHERS THEN
        p_mensajeerror := SQLERRM;
        IF p_mensajeerror ILIKE '%UNIQUE%' AND p_mensajeerror ILIKE '%Serial%' THEN
            p_codigoerror := '1001';
        ELSIF p_mensajeerror ILIKE '%UNIQUE%' AND p_mensajeerror ILIKE '%MAC%' THEN
            p_codigoerror := '1002';
        ELSE
            p_codigoerror := '1003';
        END IF;
        RETURN;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertetiquetado(p_serial character varying, p_mac character varying, p_variable1 character varying, p_variable2 character varying, p_variable3 character varying, p_variable4 character varying, p_reimpresion boolean, p_usuarioid integer, p_fecha timestamp without time zone)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_serialid integer;
    v_codigosapid varchar(50);
    v_nivelid integer;
    v_loteid integer;
    v_estadoid integer;
BEGIN
    SELECT id, codigosapid, nivelid, loteid, estadoid INTO v_serialid, v_codigosapid, v_nivelid, v_loteid, v_estadoid
        FROM ingreso
        WHERE serial = p_serial;
    IF v_serialid IS NOT NULL THEN
        INSERT INTO Etiquetado(serialid,serial,mac,codigosapid,nivelid,loteid,variable1,variable2,variable3,variable4,estadoid,reimpresion,usuarioid,fecha)
            VALUES (v_serialid, p_serial, p_mac, v_codigosapid, v_nivelid, v_loteid, p_variable1, p_variable2, p_variable3, p_variable4, v_estadoid, (p_reimpresion)::boolean, p_usuarioid, p_fecha);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertforecast(p_linea_negocio character varying DEFAULT NULL::character varying, p_fecha date DEFAULT NULL::date, p_fecha_entrega date DEFAULT NULL::date, p_forecast_und numeric DEFAULT 0, p_jornada character varying DEFAULT 'LV'::character varying, p_dias_habiles integer DEFAULT NULL::integer, p_observaciones character varying DEFAULT NULL::character varying, p_usuario character varying DEFAULT NULL::character varying, p_familias_json text DEFAULT NULL::text)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    INSERT INTO Forecast (linea_negocio, fecha, fecha_entrega, forecast_und, jornada, dias_habiles, observaciones, usuario, familias_json) VALUES (p_linea_negocio, p_fecha, p_fecha_entrega, p_forecast_und, p_jornada, p_dias_habiles, p_observaciones, p_usuario, p_familias_json);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertinventariofaltante(p_usuarioid integer, p_documento character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    INSERT INTO InventarioFaltante (
                serialid, serial, mac, serial3, serial4, serial5, smartcard, codigosapid, palletid,
                palletidingreso, palletidalmacen, palletidempaque, cajaempaqueid, cajadespachoid, estadoid,
                tipoorigenid, origenid, tipologiaid, nivelid, tramite, documento, empresatransportadoraid,
                guia, caja, falla, causa, gaveta, fuente, tecnicocliente, prealertaid, cruce, novedad,
                garantiafabricante, garantiawoden, observacionid, observaciones, usuarioid, fecha, bodega,
                estadocliente, estadoequipo, estadoactual, usuarioidmovimiento, fechamovimiento, transportadoraid,
                ciudadid, loteid, smartcardid, cajaingresoid, numerosmartcard, fechainventariotermina, documentoinventariotermina,usuarioidtermina
            )
            SELECT
                i.id, i.serial, i.mac, i.serial3, i.serial4, i.serial5, i.smartcard, i.codigosapid, i.palletid,
                i.palletidingreso, i.palletidalmacen, i.palletidempaque, i.cajaempaqueid, i.cajadespachoid, i.estadoid,
                i.tipoorigenid, i.origenid, i.tipologiaid, i.nivelid, i.tramite, i.documento, i.empresatransportadoraid,
                i.guia, i.caja, i.falla, i.causa, ( i.gaveta)::boolean, ( i.fuente)::boolean, i.tecnicocliente, i.prealertaid, ( i.cruce)::boolean, i.novedad, (
                i.garantiafabricante)::boolean, ( i.garantiawoden)::boolean, i.observacionid, i.observaciones, i.usuarioid, i.fecha, i.bodega,
                i.estadocliente, i.estadoequipo, i.estadoactual, i.usuarioidmovimiento, i.fechamovimiento, i.transportadoraid,
                i.ciudadid, i.loteid, i.smartcardid, i.cajaingresoid, i.numerosmartcard, LOCALTIMESTAMP, p_documento,p_usuarioid
            FROM ingreso i LEFT JOIN inventarioterminado t ON i.id = t.serialid
            WHERE t.serialid IS NULL;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertlabel(p_nombre character varying, p_tipo character varying, p_impresion integer, p_codigosapid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Etiqueta(nombre,tipo,impresion,codigosapid)
            VALUES(p_nombre,p_tipo,p_impresion,p_codigosapid);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertlabelfield(p_etiquetaid integer, p_nombre character varying, p_valor character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO EtiquetaCampo(etiquetaid,nombre,valor)
            VALUES(p_etiquetaid,p_nombre,p_valor);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertlote(p_nombre character varying, p_descripcion character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Lote(nombre,descripcion,activo)
                    VALUES(p_nombre,p_descripcion, (1)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertmanodeobra(p_fecha date, p_detalle character varying DEFAULT NULL::character varying, p_observaciones character varying DEFAULT NULL::character varying, p_mano_obra_json text DEFAULT NULL::text, p_usuario character varying DEFAULT NULL::character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO ManoDeObra (fecha, detalle, observaciones, mano_obra_json, activo, usuario, created_at, updated_at) VALUES (p_fecha, p_detalle, p_observaciones, p_mano_obra_json, ( 1)::boolean, p_usuario, LOCALTIMESTAMP, LOCALTIMESTAMP);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertmaster(p_codigo character varying, p_descripcion character varying, p_detalle character varying, p_adicional character varying, p_tipomaestroid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Maestro(codigo,descripcion,detalle,adicional,tipomaestroid)
            VALUES(p_codigo,p_descripcion,p_detalle,p_adicional,p_tipomaestroid);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertmasterprinting(p_cajaempaqueid integer, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO MasterImpresas(cajaempaqueid, usuarioid)
            VALUES (p_cajaempaqueid, p_usuarioid);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertmastertype(p_nombre character varying, p_descripcion character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        insert into TipoMaestro(nombre,descripcion)
            values(p_nombre,p_descripcion);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertpacking(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_palletid integer, p_cajaempaqueid integer, p_nivelid integer, p_usuarioid integer, p_fecha timestamp without time zone, p_loteid integer, p_smartcardid integer, p_smartcard character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    BEGIN
        INSERT INTO Empaque(serialid,serial,mac,codigosapid,palletid,cajaempaqueid,nivelid,usuarioid,fecha,loteid,smartcardid,smartcard)
            VALUES(p_serialid,upper((p_serial)::text),upper((p_mac)::text),p_codigosapid,p_palletid,p_cajaempaqueid,p_nivelid,p_usuarioid,p_fecha,p_loteid,p_smartcardid,p_smartcard);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        p_filas := (v_rowcount);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertpackingwebd(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_palletid integer, p_cajaempaqueid integer, p_estadoid integer, p_nivelid integer, p_usuarioid integer, p_fecha timestamp without time zone, p_loteid integer, p_smartcardid integer, p_smartcard character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
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

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertpackingwebd2(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_palletid integer, p_cajaempaqueid integer, p_estadoid integer, p_nivelid integer, p_usuarioid integer, p_fecha timestamp without time zone, p_loteid integer, p_smartcardid integer, p_smartcard character varying, p_estadosmartcard integer DEFAULT NULL::integer, p_smartcardcode character varying DEFAULT NULL::character varying, INOUT p_filas integer DEFAULT NULL::integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
    v_errormessage varchar(4000);
BEGIN
    PERFORM pg_advisory_xact_lock(hashtext('empaque:' || p_serialid::text));
    p_serial := upper((p_serial)::text);
    p_mac := upper((COALESCE(p_mac, ''))::text);
    p_smartcard := upper((COALESCE(p_smartcard, ''))::text);
    p_smartcardcode := upper((COALESCE(p_smartcardcode, ''))::text);
    BEGIN
        DELETE FROM empaque WHERE serialid = p_serialid;
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        INSERT INTO Empaque (
            serialid, serial, mac, codigosapid,
            palletid, cajaempaqueid, nivelid,
            usuarioid, fecha, loteid,
            smartcardid, smartcard
        )
        VALUES (
            p_serialid, p_serial, p_mac, p_codigosapid,
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
            loteid = p_loteid,
            smartcardid = CASE WHEN p_smartcardid <> 0 THEN p_smartcardid ELSE i.smartcardid END,
            smartcard = CASE WHEN p_smartcard <> '' THEN p_smartcard ELSE i.smartcard END
        WHERE (i.serial = p_serial);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        p_filas := (v_rowcount);
        IF p_filas = 0 THEN
            RAISE EXCEPTION USING MESSAGE = format('No se encontró el serial %s en Ingreso. No se registró el empaque.', p_serial);
            RETURN;
        END IF;
        IF p_smartcardcode <> '' AND LOWER(p_smartcardcode) <> LOWER('0') AND p_estadosmartcard IS NOT NULL THEN
            UPDATE ingreso AS i
            SET estadoid = p_estadosmartcard,
                palletid = p_palletid,
                palletidempaque = p_palletid,
                cajaempaqueid = p_cajaempaqueid,
                usuarioidmovimiento = p_usuarioid,
                fechamovimiento = p_fecha,
                loteid = p_loteid,
                smartcardid = CASE WHEN p_smartcardid <> 0 THEN p_smartcardid ELSE i.smartcardid END,
                smartcard = CASE WHEN p_smartcard <> '' THEN p_smartcard ELSE i.smartcard END
            WHERE (i.serial = p_smartcardcode);
            GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        END IF;
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

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertpallet(p_numero character varying, p_posicionid integer, p_codigosapid integer, p_tipologiaid integer, p_origenid integer, p_destinoid integer, p_usuarioid integer, p_loteid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
    v_fechabase date;
    v_fecha char(6);
    v_consecutivo integer;
BEGIN
    v_fechabase := (LOCALTIMESTAMP)::date;
    v_fecha := right((( mssql.datepart('year', (v_fechabase)::timestamp))::text)::text, 2) +
right(('0' || (mssql.datepart('month', (v_fechabase)::timestamp))::varchar)::text, 2) +
right(('0' || (mssql.datepart('day', (v_fechabase)::timestamp))::varchar)::text, 2);
    UPDATE general.consecutivopallet
    SET ultimonumero = ultimonumero + 1
    WHERE (fecha = v_fecha)
    RETURNING ultimonumero INTO v_consecutivo;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    IF v_rowcount = 0 THEN
        INSERT INTO general.consecutivopallet (fecha, ultimonumero)
    VALUES (v_fecha, 1);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        v_consecutivo := 1;
    END IF;
    p_numero := (v_fecha || right(('000000' || (v_consecutivo)::varchar)::text, 6))::bigint;
    BEGIN
        insert into Pallet(numero,posicionid,codigosapid,tipologiaid,origenid,destinoid,usuarioid,fecha,usuarioidmodifica,fechamodifica,activo,loteid)
            values (p_numero,p_posicionid,p_codigosapid,p_tipologiaid,p_origenid,p_destinoid,p_usuarioid,LOCALTIMESTAMP,p_usuarioid,LOCALTIMESTAMP, (1)::boolean,p_loteid);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertpart(p_parteid integer, p_codigosapid integer, p_estadoid integer, p_fecha timestamp without time zone, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        insert into parte(parteid,codigosapid,estadoid,fecha,usuarioid,usuarioidmovimiento,fechamovimiento)
            values(p_parteid,p_codigosapid,p_estadoid,p_fecha,p_usuarioid,p_usuarioid,p_fecha);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertprealert(p_nombre character varying, p_tipoorigenid integer, p_origenid integer, p_guia character varying, p_usuarioid integer, p_fecha timestamp without time zone, p_idresponsable integer, p_estado character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        insert into Prealerta(nombre,tipoorigenid,origenid,guia,usuarioid,fecha,idresponsable,estado)
            values(p_nombre,p_tipoorigenid,p_origenid,p_guia,p_usuarioid,p_fecha,p_idresponsable,p_estado);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertprealertaserial(p_prealertaid character varying, p_serial character varying, p_mac character varying, p_codigosap character varying, p_descripcion character varying, p_cantidad integer, p_caja integer, p_falla character varying, p_tecnicocliente character varying, p_pedido character varying, p_tramite character varying, p_novedad character varying, p_garantia boolean, p_en_recogida smallint, p_recogida smallint, p_cantidad_recogida integer, p_tipo character varying, p_loteid integer, p_observaciones character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO wmswdtelefonicacolombiabog.prealertaserial(prealertaid,serial,mac,codigosap,descripcion,cantidad,caja,falla,tecnicocliente,pedido,tramite,novedad,garantia,en_recogida,recogida,cantidad_recogida,tipo,loteid,observaciones)
                VALUES(p_prealertaid,upper((p_serial)::text),upper((p_mac)::text),(SELECT codigo FROM codigosap WHERE LOWER(codigo) = LOWER(p_codigosap)),(SELECT descripcion FROM codigosap WHERE LOWER(codigo) = LOWER(p_codigosap)),p_cantidad,p_caja,p_falla,p_tecnicocliente,p_pedido,p_tramite,p_novedad, (p_garantia)::boolean,p_en_recogida,p_recogida,p_cantidad_recogida,p_tipo,p_loteid,p_observaciones);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertprealertserial(p_prealertaid integer, p_serial character varying, p_mac character varying, p_codigosap character varying, p_descripcion character varying, p_cantidad integer, p_caja integer, p_falla character varying, p_tecnicocliente character varying, p_pedido character varying, p_tramite character varying, p_novedad character varying, p_garantia integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        insert into PrealertaSerial(prealertaid,serial,mac,codigosap,descripcion,cantidad,caja,falla,tecnicocliente,pedido,tramite,novedad,garantia)
            values(p_prealertaid,p_serial,p_mac,p_codigosap,p_descripcion,p_cantidad,p_caja,p_falla,p_tecnicocliente,p_pedido,p_tramite,p_novedad, (p_garantia)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertpresupuesto(p_fecha date, p_segmentoid integer, p_nivelid integer, p_unidades character varying, p_presupuesto character varying, p_reclamo character varying, p_dias character varying, p_tipo character varying, p_detalle character varying, p_observaciones character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Presupuesto(fecha,segmentoid,nivelid,unidades,presupuesto,reclamo,diashabiles,tipo,detalle,observaciones)
                    VALUES(p_fecha,p_segmentoid,p_nivelid,p_unidades,p_presupuesto,p_reclamo,p_dias,p_tipo,p_detalle,p_observaciones);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertquality(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_palletid integer, p_cajaempaqueid integer, p_usuarioid integer, p_fecha timestamp without time zone)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Calidad(serialid,serial,mac,codigosapid,palletid,cajaempaqueid,usuarioid,fecha)
            VALUES(p_serialid,p_serial,p_mac,p_codigosapid,p_palletid,p_cajaempaqueid,p_usuarioid,p_fecha);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertrepair(p_serialid integer, p_serial character varying, p_mac character varying, p_codigosapid integer, p_estadofinalid integer, p_falladxid integer, p_tecnicoasignacionid integer, p_fechaasignacion timestamp without time zone, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Reparacion(serialid,serial,mac, codigosapid, estadofinalid, falladxid, tecnicoasignacionid, fechaasignacion, usuarioid)
    VALUES(p_serialid,
           p_serial,
           p_mac,
           p_codigosapid,
           p_estadofinalid,
           p_falladxid,
           p_tecnicoasignacionid,
           p_fechaasignacion,
           p_usuarioid);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertsapcode(p_codigo character varying, p_descripcion character varying, p_familiaid integer, p_tipoid integer, p_validacion integer, p_direccion character varying, p_largos character varying, p_recorte integer, p_reingreso integer, p_clasificacionid integer, p_numserial integer, p_asignacion integer, p_largosmac character varying, p_largosserial3 character varying, p_largosserial4 character varying, p_largosserial5 character varying, p_tipoequipoid integer, p_areaid integer, p_modeloid integer, p_proveedorid integer, p_validacionmac integer, p_direccionmac character varying, p_recortemac integer, p_asignacionfalla integer, p_multimodelo integer, p_cantidadcaja integer, p_smartcard integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO CodigoSap(codigo,descripcion,familiaid,tipoid,validacion,direccion,largos,recorte,reingreso,clasificacionid,numserial,asignacionacc,largosmac,largosserial3,largosserial4,largosserial5,tipoequipoid,areaid,modeloid,proveedorid,validacionmac,direccionmac,recortemac,asignacionfalla,multimodelo,cantidadcaja,smartcard)
            VALUES(p_codigo,p_descripcion,p_familiaid,p_tipoid, (p_validacion)::boolean,p_direccion,p_largos,p_recorte,p_reingreso,p_clasificacionid,p_numserial, (p_asignacion)::boolean,p_largosmac,p_largosserial3,p_largosserial4,p_largosserial5,p_tipoequipoid,p_areaid,p_modeloid,p_proveedorid, (p_validacionmac)::boolean,p_direccionmac,p_recortemac, (p_asignacionfalla)::boolean, (p_multimodelo)::boolean,p_cantidadcaja, (p_smartcard)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertsmartcard(p_serialid integer, p_serial character varying, p_codigosapid integer, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO SmartCard(serialid,serial,codigosapid,estadofinalid,fallaid,usuarioid,fecha)
                        VALUES(p_serialid,p_serial,p_codigosapid,0,0,p_usuarioid,LOCALTIMESTAMP);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_inserttransportadora(p_nombre character varying, p_direccion character varying, p_telefono character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Transportadora(nombre,direccion,telefono,activo)
                    VALUES(p_nombre,p_direccion,p_telefono, (1)::boolean);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertunreadable(p_consecutivoserial character varying, p_consecutivomac character varying, p_serialid integer, p_fecha timestamp without time zone)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO Ilegible(consecutivoserial,consecutivomac,serialid,confirmado,fecha)
            VALUES(p_consecutivoserial,p_consecutivomac,p_serialid, (0)::boolean,p_fecha);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertworflow(p_moduloid integer, p_origenid integer, p_opcionid integer, p_tipologiaid integer, p_nivelid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        INSERT INTO WorkFlow (moduloid, origenid, opcionid, tipologiaid, nivelid)
            VALUES (p_moduloid, p_origenid, p_opcionid, p_tipologiaid, p_nivelid);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_insertworflowdestiny(p_workflowid integer, p_destinoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        insert into WorkFlowDestino(workflowid,destinoid)
            values(p_workflowid,p_destinoid);
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_layout()
 RETURNS TABLE(id text, "Numero" text, "Estado" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT id,numero,'LIBRES' AS estado FROM posicion WHERE id NOT IN (SELECT p.posicionid FROM pallet p INNER JOIN estado e on p.destinoid=e.id WHERE e.nombre='ALMACENAMIENTO' AND p.activo=true)UNION
    SELECT id,numero,'OCUPADAS' AS estado FROM posicion WHERE id IN (SELECT p.posicionid FROM pallet p INNER JOIN estado e on p.destinoid=e.id WHERE e.nombre='ALMACENAMIENTO' AND p.activo=true) order by numero
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_nivelbefore(p_serial character varying, p_nivel integer)
 RETURNS TABLE(col1 text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_codigoid integer;
BEGIN
    v_codigoid := (COALESCE((SELECT MAX(nivelid) FROM despacho WHERE serial=p_serial AND fecha BETWEEN (LOCALTIMESTAMP-184) AND LOCALTIMESTAMP),'0'));
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT v_codigoid
    ) AS q(c1);
    IF v_codigoid <> '0' THEN
        SELECT codigo INTO p_nivel FROM maestro WHERE id
        =(SELECT MAX(nivelid) FROM despacho WHERE serial=p_serial AND fecha BETWEEN (LOCALTIMESTAMP-184) AND LOCALTIMESTAMP);
    ELSE
        p_nivel := ('0');
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_obtenerprealertabyid(p_id integer)
 RETURNS TABLE("Id" text, "Nombre" text, "TipoOrigenId" text, "OrigenId" text, "Guia" text, "UsuarioId" text, "Fecha" text, "Estado" text, "Activo" text, "IdResponsable" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text FROM (
    select
          COALESCE(id,0) AS id,
          COALESCE(nombre,'') AS nombre,
          COALESCE(tipoorigenid,0) AS tipoorigenid,
          COALESCE(origenid,0) AS origenid,
          COALESCE(guia,'') AS guia,
          COALESCE(usuarioid,0) AS usuarioid,
          COALESCE((fecha)::text, '') AS fecha,
          COALESCE(estado,'') AS estado,
          COALESCE((activo)::text, '') AS activo,
          COALESCE(idresponsable,0) AS idresponsable
 from prealerta where
   id = p_id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_packingboxid(p_palletid integer, p_numero integer)
 RETURNS TABLE("Id" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT id
            FROM cajaempaque
            WHERE palletid=p_palletid and numero=p_numero
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_packoffboxdispatch(p_palletid integer, p_cajadespachoid integer, p_estadoid integer, p_documento character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE despacho
    SET estadoid = p_estadoid,
        pedidosap = p_documento,
        fecha = LOCALTIMESTAMP
    WHERE (palletid=p_palletid and cajadespachoid=p_cajadespachoid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_packoffboxentry(p_palletid integer, p_cajadespachoid integer, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ingreso
    WHERE palletid=p_palletid and cajadespachoid=p_cajadespachoid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_packoffpalletaccesory(p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM accesorio
            WHERE palletid=p_palletid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_packoffpalletaccesorybox(p_palletid integer, p_cajadespachoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM accesorio a USING ingreso i WHERE (a.serialempaque = i.serial) AND (a.palletid=p_palletid AND i.cajadespachoid = p_cajadespachoid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_packoffpalletdispatch(p_palletid integer, p_estadoid integer, p_documento character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE despacho
    SET estadoid = p_estadoid,
        pedidosap = p_documento,
        fecha = LOCALTIMESTAMP
    WHERE (palletid=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_packoffpalletentry(p_palletid integer, p_usuarioid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ingreso
    WHERE palletid=p_palletid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_queryreport(p_consulta text)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    EXECUTE p_consulta;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_reingresos(p_serial character varying, INOUT p_cantidad integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    SELECT Count(d.serial) INTO p_cantidad FROM despacho d
    WHERE d.serial = p_serial;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_removeciclicpallet(p_ciclicoid integer, p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DELETE FROM ciclicopallet
    WHERE ciclicoid=p_ciclicoid AND palletid=p_palletid;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_saveclasificacion(p_datos text)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DROP TABLE IF EXISTS tmp_lote;
    CREATE TEMP TABLE tmp_lote AS SELECT serial, estadoid, nivelid, usuarioid, estado
    FROM (SELECT (e ->> 'serial')::varchar(50) AS serial, (e ->> 'estadoId')::integer AS estadoid, (e ->> 'nivelId')::integer AS nivelid, (e ->> 'usuarioId')::integer AS usuarioid, (e ->> 'estado')::varchar(30) AS estado FROM jsonb_array_elements(CASE WHEN jsonb_typeof((p_datos)::jsonb) = 'array' THEN (p_datos)::jsonb ELSE jsonb_build_array((p_datos)::jsonb) END) AS e) AS oj;
    UPDATE ingreso AS i
    SET estadoid = l.estadoid,
        nivelid = l.nivelid,
        usuarioidmovimiento = l.usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    FROM tmp_lote l
    WHERE (l.serial = i.serial);
    UPDATE clasificacion AS c
    SET estadoenviado = l.estado,
        nivelid = l.nivelid,
        fecha = LOCALTIMESTAMP
    FROM (
            SELECT serial, MAX(id) AS maxid
            FROM clasificacion
            GROUP BY serial
        ) ult, tmp_lote l
    WHERE (ult.maxid = c.id) AND (l.serial = c.serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_savediagnostic(p_datos text)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    BEGIN
        UPDATE ingreso AS i
        SET estadoid = d.estadoid,
            usuarioidmovimiento = d.usuarioid,
            fechamovimiento = LOCALTIMESTAMP
        FROM (SELECT (e ->> 'serial')::varchar(50) AS serial, (e ->> 'estadoId')::integer AS estadoid, (e ->> 'usuarioId')::integer AS usuarioid FROM jsonb_array_elements(CASE WHEN jsonb_typeof((p_datos)::jsonb) = 'array' THEN (p_datos)::jsonb ELSE jsonb_build_array((p_datos)::jsonb) END) AS e) AS d
        WHERE (i.serial = d.serial);
        UPDATE diagnostico AS dia
        SET estadofinalid = d.estadofinalid,
            fallaid = d.fallaid,
            fecha = LOCALTIMESTAMP,
            estadocalidadid = d.estadocalidadid
        FROM (SELECT (e ->> 'serial')::varchar(50) AS serial, (e ->> 'estadoFinalId')::integer AS estadofinalid, (e ->> 'fallaId')::integer AS fallaid, (e ->> 'estadoCalidadId')::integer AS estadocalidadid FROM jsonb_array_elements(CASE WHEN jsonb_typeof((p_datos)::jsonb) = 'array' THEN (p_datos)::jsonb ELSE jsonb_build_array((p_datos)::jsonb) END) AS e) AS d
        WHERE (dia.id = (
              SELECT MAX(id) FROM diagnostico WHERE serial = d.serial
            ));
    EXCEPTION WHEN OTHERS THEN
        IF 0 > 0 THEN
            NULL;
        END IF;
        RAISE;
    END;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchaccesory(p_palletid integer)
 RETURNS TABLE("Codigo" text, "Descripcion" text, "TipoAccesorio" text, origen text, "Documento" text, "Guia" text, "Caja" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
    SELECT cs.codigo,cs.descripcion,a.tipoaccesorio,o.codigo AS origen,a.documento,a.guia,a.caja from accesorio a
    inner join codigosap cs on a.codigosapid=cs.id
    inner join maestro o on a.origenid=o.id
    where a.palletid=p_palletid
    ) AS q(c1, c2, c3, c4, c5, c6, c7);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchaccesorypallet(p_numero character varying, p_estado character varying, p_usuarioid integer)
 RETURNS TABLE(id text, numero text, codigo text, descripcion text, cantidad text, tipologia text, lote text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
        select p.id,p.numero,cs.codigo,cs.descripcion,count(a.id) AS cantidad,t.codigo AS tipologia,lo.nombre AS lote from pallet p
        left join accesorio a on p.id=a.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado e on p.destinoid=e.id
        inner join codigosap cs ON p.codigosapid = cs.id
        left join lote lo ON p.loteid = lo.id
        where p.usuarioid=p_usuarioid and LOWER(e.nombre) = LOWER(p_estado) and p.activo=true
        group by p.id,p.numero,cs.codigo,cs.descripcion,t.codigo,lo.nombre
        ) AS q(c1, c2, c3, c4, c5, c6, c7);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
        select p.id,p.numero,cs.codigo,cs.descripcion,count(a.id) AS cantidad,t.codigo AS tipologia,lo.nombre AS lote from pallet p
        left join accesorio a on p.id=a.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado e on p.destinoid=e.id
        inner join codigosap cs ON p.codigosapid = cs.id
        left join lote lo ON p.loteid = lo.id
        where p.numero=p_numero and p.usuarioid=p_usuarioid and LOWER(e.nombre) = LOWER(p_estado) and p.activo=true
        group by p.id,p.numero,cs.codigo,cs.descripcion,t.codigo,lo.nombre
        ) AS q(c1, c2, c3, c4, c5, c6, c7);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchallpackedaccesory(p_estado character varying)
 RETURNS TABLE(codigo text, descripcion text, familia text, "Nuevos" text, "Reacondicionados" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DROP TABLE IF EXISTS tmp_tempaccesorios;
    CREATE TEMP TABLE tmp_tempaccesorios AS SELECT
        cs.codigo,
        cs.descripcion,
        m.codigo AS familia,
        SUM(CASE WHEN a.tipoaccesorio = 'Nuevo' THEN 1 ELSE 0 END) AS nuevos,
        SUM(CASE WHEN a.tipoaccesorio = 'Reacondicionado' THEN 1 ELSE 0 END) AS reacondicionados
    FROM accesorio a
    INNER JOIN codigosap cs ON a.codigosapid = cs.id
    INNER JOIN wmswdtelefonicacolombiabog.estado e ON a.estadoid = e.id
    INNER JOIN maestro m ON cs.familiaid = m.id
    WHERE
        a.serialempaque IN (SELECT serialempaque FROM tempseriales)
        AND LOWER(e.nombre) = LOWER(p_estado)
    GROUP BY
        cs.codigo,
        cs.descripcion,
        m.codigo;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
    SELECT
        codigo,
        descripcion,
        familia,
        COALESCE(nuevos, 0) AS nuevos,
        COALESCE(reacondicionados, 0) AS reacondicionados
    FROM tmp_tempaccesorios
    ) AS q(c1, c2, c3, c4, c5);
    DROP TABLE IF EXISTS tmp_tempaccesorios;
    DELETE FROM tempseriales;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchallpackingaccesory(p_estado character varying)
 RETURNS TABLE(codigo text, descripcion text, "Nuevos" text, "Reacondicionados" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DROP TABLE IF EXISTS tmp_tempaccenuevo;
    CREATE TEMP TABLE tmp_tempaccenuevo AS SELECT cs.codigo, cs.descripcion, COALESCE(count(a.id), 0) AS cantidad
            FROM accesorio a
            INNER JOIN codigosap cs ON a.codigosapid = cs.id
            INNER JOIN wmswdtelefonicacolombiabog.estado e ON a.estadoid = e.id
            WHERE LOWER(e.nombre) = LOWER(p_estado) AND cs.codigo NOT IN (SELECT cs.codigo FROM accesorio a INNER JOIN codigosap cs ON a.codigosapid = cs.id WHERE a.serialempaque IN (SELECT serialempaque FROM tempseriales)) AND a.tipoaccesorio = 'Nuevo'
            GROUP BY cs.codigo, cs.descripcion;
    DROP TABLE IF EXISTS tmp_tempaccereacon;
    CREATE TEMP TABLE tmp_tempaccereacon AS SELECT cs.codigo, cs.descripcion, COALESCE(count(a.id), 0) AS cantidad
            FROM accesorio a
            INNER JOIN codigosap cs ON a.codigosapid = cs.id
            INNER JOIN wmswdtelefonicacolombiabog.estado e ON a.estadoid = e.id
            WHERE LOWER(e.nombre) = LOWER(p_estado) AND cs.codigo NOT IN (SELECT cs.codigo FROM accesorio a INNER JOIN codigosap cs ON a.codigosapid = cs.id WHERE a.serialempaque IN (SELECT serialempaque FROM tempseriales)) AND a.tipoaccesorio = 'Reacondicionado'
            GROUP BY cs.codigo, cs.descripcion;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    SELECT COALESCE(a.codigo, n.codigo) AS codigo, COALESCE(a.descripcion, n.descripcion) AS descripcion, COALESCE(n.cantidad, 0) AS nuevos, COALESCE(a.cantidad, 0) AS reacondicionados
            FROM tmp_tempaccenuevo n
            FULL OUTER JOIN tmp_tempaccereacon a ON n.codigo = a.codigo
    ) AS q(c1, c2, c3, c4);
    DROP TABLE IF EXISTS tmp_tempaccenuevo;
    DROP TABLE IF EXISTS tmp_tempaccereacon;
    DELETE FROM tempseriales;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchallsapcode()
 RETURNS TABLE(codigo text, descripcion text, familia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT cs.codigo,cs.descripcion,f.codigo AS familia FROM codigosap cs
    INNER JOIN maestro f ON cs.familiaid=f.id
    ORDER BY cs.codigo
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchassignedrepair(p_perfil character varying, p_tecnicoasignacionid integer, p_tipo character varying)
 RETURNS TABLE(serial text, "fechaAsignacion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_tipo)=LOWER('SERIALES')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
        select r.serial,(r.fechaasignacion)::date AS fechaasignacion from reparacion r
        LEFT JOIN LATERAL ( SELECT MAX(r2.fechaasignacion) AS maxfecha FROM reparacion r2
        WHERE r2.serial = r.serial AND r2.tecnicoasignacionid = p_tecnicoasignacionid) t ON true
        inner join ingreso i on r.serialid=i.id
        inner join general.estado e on i.estadoid=e.id
        inner join maestro m on r.estadofinalid=m.id
        where r.serial not ILIKE 'ILE%' and e.nombre='REPARACION-ASIGNADO' and m.codigo='ASIGNADO'
        and (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR r.tecnicoasignacionid=p_tecnicoasignacionid)
        and r.fechaasignacion = t.maxfecha
        ) AS q(c1, c2);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
        select r.serial,(r.fechaasignacion)::date AS fechaasignacion from reparacion r
        LEFT JOIN LATERAL ( SELECT MAX(r2.fechaasignacion) AS maxfecha FROM reparacion r2
        WHERE r2.serial = r.serial AND r2.tecnicoasignacionid = p_tecnicoasignacionid) t ON true
        inner join ingreso i on r.serialid=i.id
        inner join general.estado e on i.estadoid=e.id
        inner join maestro m on r.estadofinalid=m.id
        where r.serial ILIKE 'ILE%' and e.nombre='REPARACION-ASIGNADO' and m.codigo='ASIGNADO'
        and (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR r.tecnicoasignacionid=p_tecnicoasignacionid)
        and r.fechaasignacion = t.maxfecha
        ) AS q(c1, c2);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchbase(p_base character varying)
 RETURNS TABLE("Id" text, "Serial" text, "CodigoSap" text, "EstadoSap" text, "EstadoRR" text, "Lote" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_base)=LOWER('BaseDespacho')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        SELECT * FROM basedespacho
        ) AS q(c1, c2, c3, c4, c5, c6);
    ELSIF (LOWER(p_base)=LOWER('BaseEmpaque')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        SELECT * FROM baseempaque
        ) AS q(c1, c2, c3, c4, c5, c6);
    ELSIF (LOWER(p_base)=LOWER('BaseInventario')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, NULL::text FROM (
        SELECT * FROM baseinventario
        ) AS q(c1, c2, c3, c4, c5);
    ELSIF (LOWER(p_base)=LOWER('BaseSeparacion')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, NULL::text FROM (
        SELECT * FROM baseseparacion
        ) AS q(c1, c2, c3, c4, c5);
    ELSIF (LOWER(p_base)=LOWER('BaseIngreso')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        SELECT * FROM baseingreso
        ) AS q(c1, c2, c3, c4, c5, c6);
    END IF;
    IF (LOWER(p_base)=LOWER('BaseEtiquetado')) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, NULL::text FROM (
        SELECT * FROM baseetiquetado
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchbatch(p_lote character varying)
 RETURNS TABLE(id text, nombre text, descripcion text, activo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_lote = '') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        SELECT id,
               nombre,
               descripcion,
               activo
        FROM lote
        ORDER BY nombre
        ) AS q(c1, c2, c3, c4);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        SELECT id,
               nombre,
               descripcion,
               activo
        FROM lote
        WHERE LOWER(nombre) = LOWER(p_lote)
        ORDER BY nombre
        ) AS q(c1, c2, c3, c4);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchboxentry(p_cajaempaqueid integer)
 RETURNS TABLE(serial text, mac text, codigo text, descripcion text, tipologia text, "SmartCard" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
    SELECT i.serial,i.mac,cs.codigo,cs.descripcion,t.codigo AS tipologia,i.smartcard FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid=cs.id
    INNER JOIN maestro t ON i.tipologiaid=t.id
    WHERE i.cajaempaqueid=p_cajaempaqueid
    ) AS q(c1, c2, c3, c4, c5, c6);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchboxentryreingreso(p_palletid integer, p_cajaingresoid integer)
 RETURNS TABLE(serial text, mac text, "Serial3" text, "Serial4" text, "Serial5" text, codigosap text, descripcion text, tipologia text, "tipoOrigen" text, fecha text, "estadoCliente" text, lote text, modelo text, "Reingreso" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text FROM (
    SELECT i.serial,i.mac,i.serial3,i.serial4,i.serial5,cs.codigo AS codigosap,cs.descripcion,m.codigo AS tipologia,mto.codigo AS tipoorigen,i.fecha,i.estadocliente,l.nombre AS lote,md.codigo AS modelo, (SELECT COUNT(serial) FROM despacho WHERE serial=i.serial ) AS reingreso FROM ingreso i
            INNER JOIN codigosap cs ON i.codigosapid=cs.id
            INNER JOIN maestro m ON i.tipologiaid = m.id
            INNER JOIN maestro mto ON i.tipoorigenid = mto.id
            LEFT JOIN lote l ON i.loteid = l.id
            LEFT JOIN maestro md ON i.modeloid = md.id
            WHERE i.palletid=p_palletid AND i.cajaingresoid = p_cajaingresoid
            GROUP BY i.serial,i.mac,i.serial3,i.serial4,i.serial5,cs.codigo,cs.descripcion,m.codigo,mto.codigo,i.fecha,i.estadocliente,l.nombre,md.codigo
            ORDER BY i.fecha
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchciclic(p_nombre character varying)
 RETURNS TABLE(id text, nombre text, nombreusuario text, estado text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_nombre='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        SELECT c.id,c.nombre,u.nombreusuario,c.estado FROM ciclico c
        INNER JOIN general.usuario u ON c.usuarioid=u.id
        ORDER BY nombre
        ) AS q(c1, c2, c3, c4);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        SELECT c.id,c.nombre,u.nombreusuario,c.estado FROM ciclico c
        INNER JOIN general.usuario u ON c.usuarioid=u.id
        WHERE LOWER(nombre) = LOWER(p_nombre)
        ) AS q(c1, c2, c3, c4);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchciclicpallet(p_ciclicoid integer)
 RETURNS TABLE(id text, numero text, cantidad text, tipologia text, posicion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
    SELECT p.id,p.numero,count(i.serial) AS cantidad,t.codigo AS tipologia,po.numero AS posicion FROM pallet p
    INNER JOIN ingreso i ON p.id=i.palletid
    LEFT JOIN posicion po ON p.posicionid=po.id
    INNER JOIN maestro t ON p.tipologiaid=t.id
    WHERE p.id IN
    (SELECT palletid FROM ciclicopallet
    WHERE ciclicoid=p_ciclicoid)
    GROUP BY p.id,p.numero,t.codigo,po.numero
    ) AS q(c1, c2, c3, c4, c5);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchclasificationentry()
 RETURNS TABLE(serial text, mac text, codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    SELECT DISTINCT i.serial,i.mac,cs.codigo,cs.descripcion FROM ingreso i
 INNER JOIN codigosap cs ON i.codigosapid=cs.id
 INNER JOIN general.estado es ON i.estadoid=es.id
 WHERE ((es.nombre='CLASIFICACION-REPARACION'))
    ) AS q(c1, c2, c3, c4);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchcleaningentry()
 RETURNS TABLE(serial text, mac text, codigosap text, descripcion text, usuarioasignado text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
    SELECT
        CONCAT(i.serial, ', ', i.serial3) AS serial,
        i.mac,
        cs.codigo AS codigosap,
        cs.descripcion AS descripcion,
        u.nombreusuario AS usuarioasignado
    FROM
        ingreso i
        INNER JOIN codigosap cs ON i.codigosapid = cs.id
        INNER JOIN general.estado e ON i.estadoid = e.id
        INNER JOIN limpieza l ON i.id = l.serialid
        INNER JOIN general.usuario u ON u.id = l.usuarioasignadoid
    WHERE
        e.nombre = 'LIMPIEZA-TERMINADO'
        AND l.id = (SELECT MAX(l2.id) FROM limpieza l2 WHERE l2.serialid = i.id)
    ) AS q(c1, c2, c3, c4, c5);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchcodesap_acc(p_codigosapid integer)
 RETURNS TABLE(id text, codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT id,codigo,descripcion FROM codigosap
    WHERE id IN(SELECT accesorioid FROM codigosapaccesorio
    WHERE codigosapid=p_codigosapid)
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchcomponents(p_serial character varying, p_falla character varying)
 RETURNS TABLE(id text, codigo text, nivel text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT c.id, c.codigo, c.detalle AS nivel
        FROM maestro c
        INNER JOIN tipomaestro tm ON c.tipomaestroid = tm.id
        WHERE tm.nombre = 'Componentes'
        AND c.id NOT IN (SELECT m.id FROM maestro m
                        INNER JOIN serialcomponente sc ON m.id = sc.componenteid
                        WHERE sc.serialid = (SELECT id FROM ingreso WHERE serial = p_serial)
                        )
        AND LOWER(c.descripcion) = LOWER(p_falla)
        AND c.activo =true
        ORDER BY c.codigo
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchcomponentsasig(p_serial character varying, p_falla character varying, p_filas integer)
 RETURNS TABLE(id text, codigo text, nivel text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT m.id,m.codigo,m.detalle AS nivel FROM maestro m
            INNER JOIN serialcomponente sc ON m.id = sc.componenteid
            WHERE sc.serialid = (SELECT id FROM ingreso WHERE serial = p_serial)
            AND LOWER(m.descripcion) = LOWER(p_falla)
            ORDER BY m.codigo
    ) AS q(c1, c2, c3);
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchcomponentsasigserial(p_serialid integer, p_filas integer)
 RETURNS TABLE(serial text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT serial FROM serialcomponente WHERE serialid = p_serialid
    ) AS q(c1);
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchcosmeticaentry(p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE(serial text, mac text, codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    SELECT DISTINCT i.serial,i.mac,cs.codigo,cs.descripcion FROM ingreso i
 INNER JOIN codigosap cs ON i.codigosapid=cs.id
 INNER JOIN general.estado es ON i.estadoid=es.id
 WHERE ((es.nombre='COSMETICA')) AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR i.usuarioidmovimiento = p_usuarioid)
    ) AS q(c1, c2, c3, c4);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchcyclicpallet(p_numero character varying)
 RETURNS TABLE(id text, numero text, cantidad text, tipologia text, posicion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,p.numero,count(i.serial) AS cantidad,t.codigo AS tipologia,po.numero AS posicion from pallet p
        left join ingreso i on p.id=i.palletid
        inner join maestro t on p.tipologiaid=t.id
        left join posicion po on p.posicionid=po.id
        inner join general.estado e on p.destinoid=e.id
        where e.nombre='ALMACENAMIENTO' and p.activo=true
        group by p.id,p.numero,t.codigo,po.numero
        ) AS q(c1, c2, c3, c4, c5);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,p.numero,count(i.serial) AS cantidad,t.codigo AS tipologia,po.numero AS posicion from pallet p
        left join ingreso i on p.id=i.palletid
        inner join maestro t on p.tipologiaid=t.id
        left join posicion po on p.posicionid=po.id
        inner join general.estado e on p.destinoid=e.id
        where p.numero=p_numero and e.nombre='ALMACENAMIENTO' and p.activo=true
        group by p.id,p.numero,t.codigo,po.numero
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchdeliveryaccesory(p_palletid integer)
 RETURNS TABLE(id text, codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    select a.id,cs.codigo,cs.descripcion from accesorio a
    inner join codigosap cs on a.codigosapid=cs.id
    where a.palletid=p_palletid
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchdeliveryentry(p_pallet character varying)
 RETURNS TABLE(serial text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT i.serial FROM ingreso i
    INNER JOIN pallet p ON i.palletid=p.id
    WHERE p.numero=p_pallet
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchdeliverypart(p_palletid integer)
 RETURNS TABLE(id text, parte text, "Codigo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    select p.id,m.codigo AS parte,cs.codigo from parte p
    inner join codigosap cs on p.codigosapid=cs.id
    inner join maestro m on p.parteid=m.id
    where p.palletid=p_palletid
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchdiagnosticentry(p_estadofinal character varying, p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE(serial text, mac text, codigo text, descripcion text, falla text, estado text, truckrollid text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
    SELECT DISTINCT i.serial,i.mac,cs.codigo,cs.descripcion,f.descripcion AS falla,e.descripcion AS estado, i.truckrollid FROM ingreso i
    INNER JOIN diagnostico d ON i.id=d.serialid
    INNER JOIN codigosap cs ON i.codigosapid=cs.id
    INNER JOIN maestro f ON d.fallaid=f.id
    INNER JOIN maestro e ON d.estadofinalid=e.id
    INNER JOIN general.estado es ON i.estadoid=es.id
    WHERE ((es.nombre='DIAGNOSTICO-TERMINADO') and (LOWER(e.codigo) = LOWER(p_estadofinal))) and ((d.fecha)::timestamp(3) >= (mssql.dateadd('minute',-5, (i.fechamovimiento)::timestamp))::timestamp(3))
    AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR (i.usuarioidmovimiento = p_usuarioid AND LOWER(p_perfil) <> LOWER('ADMINISTRADOR')))
    ) AS q(c1, c2, c3, c4, c5, c6, c7);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchdispatch(p_palletid integer)
 RETURNS TABLE(id text, numero text, seriales text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT cd.id,cd.numero,count(ing.id) AS seriales FROM cajadespacho cd
    LEFT JOIN (select i.id,i.cajadespachoid FROM ingreso i
                INNER JOIN codigosap cs ON i.codigosapid = cs.id
                INNER JOIn maestro m ON cs.tipoid = m.id
                WHERE m.codigo = 'SERIALIZADOS')ing ON cd.id=ing.cajadespachoid
    WHERE cd.palletid=p_palletid and cd.activo=true
    GROUP BY cd.id,cd.numero
    ORDER BY cd.numero
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchentry(p_palletid integer)
 RETURNS TABLE(serial text, mac text, codigosap text, descripcion text, guia text, documento text, fecha text, "Reingreso" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
    SELECT CONCAT(i.serial,', ',i.serial3,', ',i.serial4,', ',i.serial5) AS serial,i.mac,cs.codigo AS codigosap,cs.descripcion,i.guia,i.documento,i.fecha, (SELECT COUNT(serial) FROM despacho WHERE serial=i.serial ) AS reingreso FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid=cs.id
    WHERE i.palletid=p_palletid
    GROUP BY i.serial,i.serial3,i.serial4,i.serial5,i.mac,cs.codigo,cs.descripcion,i.guia,i.documento,i.fecha
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchentrypallet(p_numero character varying, p_estado character varying, p_usuarioid integer)
 RETURNS TABLE("Id" text, "Numero" text, "Codigo" text, "Descripcion" text, "Cantidad" text, "Tipologia" text, "Lote" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero = '') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
        SELECT p.id,
                p.numero,
                cs.codigo,
                cs.descripcion,
                COUNT(i.serial) AS cantidad,
                t.codigo AS tipologia,
                lo.nombre AS lote
        FROM pallet p
        LEFT JOIN ingreso i ON p.id = i.palletid
        INNER JOIN maestro t ON p.tipologiaid = t.id
        INNER JOIN general.estado e ON p.destinoid = e.id
        INNER JOIN codigosap cs ON p.codigosapid = cs.id
        LEFT JOIN lote lo ON p.loteid = lo.id
        WHERE p.usuarioid = p_usuarioid
          AND LOWER(e.nombre) = LOWER(p_estado)
          AND p.activo = true
        GROUP BY p.id, p.numero, cs.codigo, cs.descripcion, t.codigo, lo.nombre
        ) AS q(c1, c2, c3, c4, c5, c6, c7);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
        SELECT p.id,
                p.numero,
                cs.codigo,
                cs.descripcion,
                COUNT(i.serial) AS cantidad,
                t.codigo AS tipologia,
                lo.nombre AS lote
        FROM pallet p
        LEFT JOIN ingreso i ON p.id = i.palletid
        INNER JOIN maestro t ON p.tipologiaid = t.id
        INNER JOIN general.estado e ON p.destinoid = e.id
        INNER JOIN codigosap cs ON p.codigosapid = cs.id
        LEFT JOIN lote lo ON p.loteid = lo.id
        WHERE p.numero = p_numero
          AND p.usuarioid = p_usuarioid
          AND LOWER(e.nombre) = LOWER(p_estado)
          AND p.activo = true
        GROUP BY p.id, p.numero, cs.codigo, cs.descripcion, t.codigo, lo.nombre
        ) AS q(c1, c2, c3, c4, c5, c6, c7);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchentryreingreso(p_palletid integer)
 RETURNS TABLE(serial text, mac text, "Serial3" text, "Serial4" text, "Serial5" text, codigosap text, descripcion text, tipologia text, "tipoOrigen" text, fecha text, "estadoCliente" text, lote text, modelo text, "Reingreso" text, "TruckRollId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text FROM (
    SELECT i.serial,i.mac,i.serial3,i.serial4,i.serial5,cs.codigo AS codigosap,cs.descripcion,m.codigo AS tipologia,mto.codigo AS tipoorigen,
    i.fecha,i.estadocliente,l.nombre AS lote,md.codigo AS modelo, (SELECT COUNT(serial) FROM despacho WHERE serial=i.serial ) AS reingreso, i.truckrollid FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid=cs.id
    INNER JOIN maestro m ON i.tipologiaid = m.id
    INNER JOIN maestro mto ON i.tipoorigenid = mto.id
    LEFT JOIN lote l ON i.loteid = l.id
    LEFT JOIN maestro md ON i.modeloid = md.id
    WHERE i.palletid=p_palletid
    GROUP BY i.serial,i.mac,i.serial3,i.serial4,i.serial5,cs.codigo,cs.descripcion,m.codigo,mto.codigo,
    i.fecha,i.estadocliente,l.nombre,md.codigo,i.truckrollid
    ORDER BY i.fecha
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchforecast(p_forecast character varying DEFAULT ''::character varying)
 RETURNS TABLE(id text, linea_negocio text, fecha text, fecha_entrega text, forecast_und text, jornada text, dias_habiles text, observaciones text, activo text, usuario text, created_at text, updated_at text, familias_json text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text FROM (
    SELECT f.id, f.linea_negocio, f.fecha, f.fecha_entrega, f.forecast_und, f.jornada, f.dias_habiles, f.observaciones, f.activo, f.usuario, f.created_at, f.updated_at, f.familias_json FROM forecast f ORDER BY f.fecha DESC
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchgeneralinventorypallet(p_numero character varying, p_estado character varying, p_usuarioid integer)
 RETURNS TABLE(id text, numero text, cantidad text, posicion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_estado) = LOWER('ALMACENAMIENTO')) THEN
        IF (p_numero='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,po.numero AS posicion from pallet p
                left join ingreso i on p.id=i.palletid
                left join posicion po on p.posicionid=po.id
                inner join general.estado e on p.destinoid=e.id
                where LOWER(e.nombre) = LOWER(p_estado) and estadoinventario=1 and p.activo =true
                group by p.id,p.numero,po.numero
            ) AS q(c1, c2, c3, c4);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,po.numero AS posicion from pallet p
                left join ingreso i on p.id=i.palletid
                left join posicion po on p.posicionid=po.id
                inner join general.estado e on p.destinoid=e.id
                where p.numero=p_numero and LOWER(e.nombre) = LOWER(p_estado) and estadoinventario=1 and p.activo =true
                group by p.id,p.numero,po.numero
            ) AS q(c1, c2, c3, c4);
        END IF;
    ELSE
        IF (p_numero='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,po.numero AS posicion from pallet p
                left join inventario i on p.numero=i.pallet
                left join posicion po on p.posicionid=po.id
                inner join general.estado e on p.destinoid=e.id
                where LOWER(e.nombre) = LOWER(p_estado) and estadoinventario=1 and p.activo =true AND p.usuarioid = p_usuarioid
                group by p.id,p.numero,po.numero
            ) AS q(c1, c2, c3, c4);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,po.numero AS posicion from pallet p
                left join inventario i on p.numero=i.pallet
                left join posicion po on p.posicionid=po.id
                inner join general.estado e on p.destinoid=e.id
                where p.numero=p_numero and LOWER(e.nombre) = LOWER(p_estado) and estadoinventario=1 and p.activo =true AND p.usuarioid = p_usuarioid
                group by p.id,p.numero,po.numero
            ) AS q(c1, c2, c3, c4);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchgeneralsettingspallet(p_numero character varying, p_estado character varying)
 RETURNS TABLE(id text, numero text, cantidad text, posicion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_estado) = LOWER('ALMACENAMIENTO')) THEN
        IF (p_numero='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,po.numero AS posicion from pallet p
                left join ingreso i on p.id=i.palletid
                left join posicion po on p.posicionid=po.id
                inner join general.estado e on p.destinoid=e.id
                where LOWER(e.nombre) = LOWER(p_estado) and estadoinventario=0
                group by p.id,p.numero,po.numero
            ) AS q(c1, c2, c3, c4);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,po.numero AS posicion from pallet p
                left join ingreso i on p.id=i.palletid
                left join posicion po on p.posicionid=po.id
                inner join general.estado e on p.destinoid=e.id
                where p.numero=p_numero and LOWER(e.nombre) = LOWER(p_estado) and estadoinventario=0
                group by p.id,p.numero,po.numero
            ) AS q(c1, c2, c3, c4);
        END IF;
    ELSE
        IF (p_numero='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,po.numero AS posicion from pallet p
                left join inventario i on p.numero=i.pallet
                left join posicion po on p.posicionid=po.id
                inner join general.estado e on p.destinoid=e.id
                where estadoinventario=0
                group by p.id,p.numero,po.numero
            ) AS q(c1, c2, c3, c4);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,po.numero AS posicion from pallet p
                left join inventario i on p.numero=i.pallet
                left join posicion po on p.posicionid=po.id
                inner join general.estado e on p.destinoid=e.id
                where p.numero=p_numero and LOWER(e.nombre) = LOWER(p_estado) and estadoinventario=0
                group by p.id,p.numero,po.numero
            ) AS q(c1, c2, c3, c4);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchgroupaccesory(p_codigo character varying, p_estado character varying, p_tipo character varying)
 RETURNS TABLE(codigo text, descripcion text, cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_codigo='') THEN
        IF (p_tipo='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            select cs.codigo,cs.descripcion,count(a.id) AS cantidad from accesorio a
            inner join codigosap cs on a.codigosapid=cs.id
            inner join general.estado e on a.estadoid=e.id
            where LOWER(e.nombre) = LOWER(p_estado)
            group by cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            select cs.codigo,cs.descripcion,count(a.id) AS cantidad from accesorio a
            inner join codigosap cs on a.codigosapid=cs.id
            inner join general.estado e on a.estadoid=e.id
            where LOWER(e.nombre) = LOWER(p_estado) and LOWER(a.tipoaccesorio) = LOWER(p_tipo)
            group by cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3);
        END IF;
    ELSE
        IF (p_tipo='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            SELECT cs.codigo, cs.descripcion, (SELECT COUNT(a.id)
                    FROM accesorio a
                    LEFT JOIN general.estado e ON a.estadoid = e.id
                    WHERE a.codigosapid = cs.id AND LOWER(e.nombre) = LOWER(p_estado)) AS cantidad
            FROM codigosap cs
            WHERE LOWER(cs.codigo) = LOWER(p_codigo)
            ) AS q(c1, c2, c3);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
            SELECT cs.codigo, cs.descripcion, (SELECT COUNT(a.id)
                    FROM accesorio a
                    LEFT JOIN general.estado e ON a.estadoid = e.id
                    WHERE a.codigosapid = cs.id AND LOWER(e.nombre) = LOWER(p_estado) AND LOWER(a.tipoaccesorio) = LOWER(p_tipo)) AS cantidad
            FROM codigosap cs
            WHERE LOWER(cs.codigo) = LOWER(p_codigo)
            ) AS q(c1, c2, c3);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchgrouppart(p_referencia character varying, p_parte character varying, p_estado character varying)
 RETURNS TABLE(parte text, cantidad text, codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_referencia='') THEN
        IF (p_parte='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select m.codigo AS parte,count(p.id) AS cantidad,cs.codigo,cs.descripcion from parte p
            inner join maestro m on p.parteid=m.id
            inner join codigosap cs on p.codigosapid=cs.id
            inner join general.estado e on p.estadoid=e.id
            where LOWER(e.nombre) = LOWER(p_estado)
            group by m.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select m.codigo AS parte,count(p.id) AS cantidad,cs.codigo,cs.descripcion from parte p
            inner join maestro m on p.parteid=m.id
            inner join codigosap cs on p.codigosapid=cs.id
            inner join general.estado e on p.estadoid=e.id
            where LOWER(e.nombre) = LOWER(p_estado) and LOWER(m.codigo) = LOWER(p_parte)
            group by m.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4);
        END IF;
    ELSE
        IF (p_parte='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select m.codigo AS parte,count(p.id) AS cantidad,cs.codigo,cs.descripcion from parte p
            inner join maestro m on p.parteid=m.id
            inner join codigosap cs on p.codigosapid=cs.id
            inner join general.estado e on p.estadoid=e.id
            where LOWER(e.nombre) = LOWER(p_estado) and LOWER(cs.codigo) = LOWER(p_referencia)
            group by m.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
            select m.codigo AS parte,count(p.id) AS cantidad,cs.codigo,cs.descripcion from parte p
            inner join maestro m on p.parteid=m.id
            inner join codigosap cs on p.codigosapid=cs.id
            inner join general.estado e on p.estadoid=e.id
            where LOWER(e.nombre) = LOWER(p_estado) and LOWER(cs.codigo) = LOWER(p_referencia) and LOWER(m.codigo) = LOWER(p_parte)
            group by m.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchlabel(p_nombre character varying)
 RETURNS TABLE("Id" text, "Nombre" text, "Tipo" text, "Impresion" text, "codigoSapId" text, "Activo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_nombre='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        SELECT * FROM etiqueta
        ORDER BY nombre
        ) AS q(c1, c2, c3, c4, c5, c6);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        SELECT * FROM etiqueta
        WHERE LOWER(nombre) = LOWER(p_nombre)
        ) AS q(c1, c2, c3, c4, c5, c6);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchlabeled(p_nombre character varying, p_tipo character varying)
 RETURNS TABLE("Id" text, "Nombre" text, "Tipo" text, "Impresion" text, "codigoSapId" text, "Activo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_nombre = '') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        SELECT *
        FROM etiqueta
        WHERE LOWER(tipo) = LOWER(p_tipo)
          AND activo = true
        ORDER BY nombre
        ) AS q(c1, c2, c3, c4, c5, c6);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        SELECT *
        FROM etiqueta
        WHERE LOWER(tipo) = LOWER(p_tipo)
          AND activo = true
          AND LOWER(nombre) = LOWER(p_nombre)
        ) AS q(c1, c2, c3, c4, c5, c6);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchlabelfield(p_etiquetaid integer)
 RETURNS TABLE(id text, nombre text, valor text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    select id,nombre,valor from etiquetacampo where etiquetaid=p_etiquetaid
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchlevel(p_idnivel integer)
 RETURNS TABLE("Codigo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.codigo FROM maestro m
            INNER JOIN tipomaestro tm ON m.tipomaestroid=tm.id
            WHERE tm.nombre='Niveles' AND m.id = p_idnivel
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchlevelcomponentsasig(p_serial character varying)
 RETURNS TABLE(nivel text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT MAX(m.detalle) AS nivel FROM maestro m
            INNER JOIN serialcomponente sc ON m.id = sc.componenteid
            WHERE sc.serialid = (SELECT id FROM ingreso WHERE serial = p_serial)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchlevelid(p_nivel character varying)
 RETURNS TABLE("Id" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT m.id FROM maestro m
            INNER JOIN tipomaestro tm ON m.tipomaestroid=tm.id
            WHERE tm.nombre='Niveles' AND LOWER(m.codigo) = LOWER(p_nivel)
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchmandatorycomponent(p_codigosapid integer, p_fallaid integer)
 RETURNS TABLE("ComponenteObligatorio" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT componenteobligatorio FROM codigosapfalla
            WHERE codigosapid = p_codigosapid AND fallaid = p_fallaid
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchmanodeobra()
 RETURNS TABLE(id text, fecha text, detalle text, observaciones text, mano_obra_json text, activo text, usuario text, created_at text, updated_at text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
    SELECT m.id, m.fecha, m.detalle, m.observaciones, m.mano_obra_json, m.activo, m.usuario, m.created_at, m.updated_at FROM manodeobra m ORDER BY m.fecha DESC
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchmaster(p_tipomaestro character varying)
 RETURNS TABLE(id text, codigo text, descripcion text, detalle text, adicional text, "tipoMaestro" text, activo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
    SELECT m.id,m.codigo,m.descripcion,m.detalle,m.adicional,tm.nombre AS tipomaestro,m.activo FROM maestro m
    INNER JOIN tipomaestro tm on m.tipomaestroid=tm.id
    WHERE LOWER(tm.nombre) = LOWER(p_tipomaestro)
    ORDER BY m.codigo
    ) AS q(c1, c2, c3, c4, c5, c6, c7);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchmastertype()
 RETURNS TABLE("Id" text, "Nombre" text, "Descripcion" text, "Activo" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    select * from tipomaestro order by nombre
    ) AS q(c1, c2, c3, c4);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchmodelspallet(p_palletid integer)
 RETURNS TABLE(codigo text, cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    select cs.codigo,count(i.serial) AS cantidad from ingreso i
    inner join codigosap cs on i.codigosapid=cs.id
    where i.palletid=p_palletid
    group by cs.codigo
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchnoveltyentry(p_tiponovedad character varying)
 RETURNS TABLE(id text, serial text, mac text, codigo text, descripcion text, novedad text, observaciones text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
    SELECT i.id,CONCAT(i.serial,', ',i.serial3,', ',i.serial4,', ',i.serial5) AS serial,i.mac,cs.codigo,cs.descripcion,i.novedad,i.observaciones FROM ingreso i
    LEFT JOIN codigosap cs ON i.codigosapid=cs.id
    INNER JOIN maestro t ON i.tipologiaid=t.id
    INNER JOIN general.estado e ON i.estadoid=e.id
    WHERE e.nombre='NOVEDAD' and LOWER(t.codigo) = LOWER(p_tiponovedad)
    ) AS q(c1, c2, c3, c4, c5, c6, c7);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchnoveltyentryweb(p_perfil character varying, p_usuarioid integer, p_tiponovedad character varying)
 RETURNS TABLE(serial text, mac text, codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    SELECT DISTINCT i.serial,i.mac,cs.codigo,cs.descripcion FROM ingreso i
 INNER JOIN codigosap cs ON i.codigosapid=cs.id
 INNER JOIN general.estado es ON i.estadoid=es.id
 WHERE ((es.nombre='NOVEDAD-TERMINADO'))
 AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR (i.usuarioidmovimiento = p_usuarioid AND LOWER(p_perfil) <> LOWER('ADMINISTRADOR')))
 AND i.novedad =p_tiponovedad
    ) AS q(c1, c2, c3, c4);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpackedaccesory(p_estado character varying, p_serial character varying)
 RETURNS TABLE(codigo text, descripcion text, "Nuevos" text, "Reacondicionados" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DROP TABLE IF EXISTS tmp_tempaccenuevo;
    CREATE TEMP TABLE tmp_tempaccenuevo AS select cs.codigo,cs.descripcion,COALESCE(count(a.id),0) AS cantidad
    from accesorio a
    inner join codigosap cs on a.codigosapid=cs.id
    inner join wmswdtelefonicacolombiabog.estado e on a.estadoid=e.id
    where LOWER(e.nombre) = LOWER(p_estado) and cs.codigo in(select cs.codigo from accesorio a inner join codigosap cs on a.codigosapid=cs.id where a.serialempaque=p_serial) and a.tipoaccesorio = 'Nuevo'
    group by cs.codigo,cs.descripcion;
    DROP TABLE IF EXISTS tmp_tempaccereacon;
    CREATE TEMP TABLE tmp_tempaccereacon AS select cs.codigo,cs.descripcion,COALESCE(count(a.id),0) AS cantidad
    from accesorio a
    inner join codigosap cs on a.codigosapid=cs.id
    inner join wmswdtelefonicacolombiabog.estado e on a.estadoid=e.id
    where LOWER(e.nombre) = LOWER(p_estado) and cs.codigo in(select cs.codigo from accesorio a inner join codigosap cs on a.codigosapid=cs.id where a.serialempaque=p_serial) and a.tipoaccesorio = 'Reacondicionado'
    group by cs.codigo,cs.descripcion;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    Select COALESCE(a.codigo,n.codigo) AS codigo, COALESCE(a.descripcion,n.descripcion) AS descripcion, COALESCE(n.cantidad,0) AS nuevos, COALESCE(a.cantidad,0) AS reacondicionados from tmp_tempaccenuevo n
    full outer join tmp_tempaccereacon a ON n.codigo = a.codigo
    ) AS q(c1, c2, c3, c4);
    DROP TABLE IF EXISTS tmp_tempaccenuevo;
    DROP TABLE IF EXISTS tmp_tempaccereacon;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpacking(p_palletid integer)
 RETURNS TABLE(id text, numero text, seriales text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT ce.id,ce.numero,count(ing.id) AS seriales
    FROM cajaempaque ce
    LEFT JOIN (select i.id,i.cajaempaqueid FROM ingreso i
                INNER JOIN codigosap cs ON i.codigosapid = cs.id
                INNER JOIn maestro m ON cs.tipoid = m.id
                WHERE m.codigo = 'SERIALIZADOS')ing ON ce.id=ing.cajaempaqueid
    WHERE ce.palletid= p_palletid
    GROUP BY ce.id,ce.numero
    ORDER BY ce.id
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpackingaccesory(p_estado character varying, p_serial character varying)
 RETURNS TABLE(codigo text, descripcion text, "Nuevos" text, "Reacondicionados" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DROP TABLE IF EXISTS tmp_tempaccenuevo;
    CREATE TEMP TABLE tmp_tempaccenuevo AS select cs.codigo,cs.descripcion,COALESCE(count(a.id),0) AS cantidad
    from accesorio a
    inner join codigosap cs on a.codigosapid=cs.id
    inner join wmswdtelefonicacolombiabog.estado e on a.estadoid=e.id
    where LOWER(e.nombre) = LOWER(p_estado) and cs.codigo not in(select cs.codigo from accesorio a inner join codigosap cs on a.codigosapid=cs.id where a.serialempaque=p_serial) and a.tipoaccesorio = 'Nuevo'
    group by cs.codigo,cs.descripcion;
    DROP TABLE IF EXISTS tmp_tempaccereacon;
    CREATE TEMP TABLE tmp_tempaccereacon AS select cs.codigo,cs.descripcion,COALESCE(count(a.id),0) AS cantidad
    from accesorio a
    inner join codigosap cs on a.codigosapid=cs.id
    inner join wmswdtelefonicacolombiabog.estado e on a.estadoid=e.id
    where LOWER(e.nombre) = LOWER(p_estado) and cs.codigo not in(select cs.codigo from accesorio a inner join codigosap cs on a.codigosapid=cs.id where a.serialempaque=p_serial) and a.tipoaccesorio = 'Reacondicionado'
    group by cs.codigo,cs.descripcion;
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    Select COALESCE(a.codigo,n.codigo) AS codigo, COALESCE(a.descripcion,n.descripcion) AS descripcion, COALESCE(n.cantidad,0) AS nuevos, COALESCE(a.cantidad,0) AS reacondicionados from tmp_tempaccenuevo n
    full outer join tmp_tempaccereacon a ON n.codigo = a.codigo
    ) AS q(c1, c2, c3, c4);
    DROP TABLE IF EXISTS tmp_tempaccenuevo;
    DROP TABLE IF EXISTS tmp_tempaccereacon;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpackingcodigosapaccesory(p_estado character varying, p_palletid integer, p_codigosapid integer)
 RETURNS TABLE(codigo text, descripcion text, familia text, "Nuevos" text, "Reacondicionados" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_usecodigosapaccesorio boolean;
BEGIN
    SELECT cs.asignacionacc INTO v_usecodigosapaccesorio
    FROM pallet p
    INNER JOIN codigosap cs ON p.codigosapid = cs.id
    WHERE p.id = p_palletid;
    IF (v_usecodigosapaccesorio = 0) THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT
            cs.codigo,
            cs.descripcion,
            m.codigo AS familia,
            SUM(CASE WHEN a.tipoaccesorio = 'Nuevo' THEN 1 ELSE 0 END) AS nuevos,
            SUM(CASE WHEN a.tipoaccesorio = 'Reacondicionado' THEN 1 ELSE 0 END) AS reacondicionados
        FROM accesorio a
        INNER JOIN codigosap cs ON a.codigosapid = cs.id
        INNER JOIN wmswdtelefonicacolombiabog.estado e ON a.estadoid = e.id
        INNER JOIN maestro m ON cs.familiaid = m.id
        WHERE LOWER(e.nombre) = LOWER(p_estado)
        GROUP BY cs.codigo, cs.descripcion, m.codigo
        ) AS q(c1, c2, c3, c4, c5);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT
            cs.codigo,
            cs.descripcion,
            m.codigo AS familia,
            SUM(CASE WHEN a.tipoaccesorio = 'Nuevo' THEN 1 ELSE 0 END) AS nuevos,
            SUM(CASE WHEN a.tipoaccesorio = 'Reacondicionado' THEN 1 ELSE 0 END) AS reacondicionados
        FROM accesorio a
        INNER JOIN codigosap cs ON a.codigosapid = cs.id
        INNER JOIN wmswdtelefonicacolombiabog.estado e ON a.estadoid = e.id
        INNER JOIN maestro m ON cs.familiaid = m.id
        INNER JOIN codigosapaccesorio csa ON a.codigosapid = csa.accesorioid
        WHERE LOWER(e.nombre) = LOWER(p_estado)
          AND csa.codigosapid = p_codigosapid
        GROUP BY cs.codigo, cs.descripcion, m.codigo
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
    DELETE FROM tempseriales;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpackingdeliverypallet(p_numero character varying, p_tipologia character varying, p_tipo character varying)
 RETURNS TABLE(id text, numero text, cantidad text, origen text, tipologia text, "CodigoSap" text, "Descripcion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
        select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap, cs.descripcion AS descripcion from pallet p
        inner join ingreso i on p.id=i.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        inner join codigosap cs ON i.codigosapid = cs.id
        inner join maestro m ON cs.tipoid = m.id
        where (e.nombre ='EMPAQUE-ENTREGA') and LOWER(t.codigo) = LOWER(p_tipologia) and p.activo=true AND LOWER(m.codigo) = LOWER(p_tipo)
        group by p.id,p.numero,o.nombre,t.codigo, cs.codigo, cs.descripcion
        ) AS q(c1, c2, c3, c4, c5, c6, c7);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
        select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia, cs.codigo AS codigosap, cs.descripcion AS descripcion from pallet p
        inner join ingreso i on p.id=i.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        inner join codigosap cs ON i.codigosapid = cs.id
        inner join maestro m ON cs.tipoid = m.id
        where p.numero=p_numero and (e.nombre ='EMPAQUE-ENTREGA') and LOWER(t.codigo) = LOWER(p_tipologia) and p.activo=true AND LOWER(m.codigo) = LOWER(p_tipo)
        group by p.id,p.numero,o.nombre,t.codigo, cs.codigo, cs.descripcion
        ) AS q(c1, c2, c3, c4, c5, c6, c7);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpalletboxdispatchdeliverypallet(p_numero character varying)
 RETURNS TABLE(id text, numero text, registros text, origen text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,p.numero,count(i.id) AS registros,o.nombre AS origen,t.codigo AS tipologia from pallet p
        full outer join ingreso i on i.palletid=p.id
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        where e.nombre='PREDESPACHO' and o.nombre = 'EMPAQUE'
        group by p.id,p.numero,o.nombre,t.codigo
        ) AS q(c1, c2, c3, c4, c5);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,p.numero,count(i.id) AS registros,o.nombre AS origen,t.codigo AS tipologia from pallet p
        full outer join ingreso i on i.palletid=p.id
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        where p.numero=p_numero and e.nombre='PREDESPACHO' and o.nombre = 'EMPAQUE'
        group by p.id,p.numero,o.nombre,t.codigo
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpalletboxdispatchpallet(p_numero character varying)
 RETURNS TABLE(id text, numero text, "Codigo" text, "Descripcion" text, cajas text, "Cantidad Equip." text, "Cantidad Acc." text, "Cantidad SmartC." text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
        select p.id,p.numero,cs.codigo,cs.descripcion,COALESCE(COUNT(CASE WHEN cd.activo = true THEN cd.id END), 0) AS cajas,((SELECT COALESCE((Select COALESCE(count(i.palletid),0) from pallet pp
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
        select p.id,p.numero,cs.codigo,cs.descripcion,COALESCE(COUNT(CASE WHEN cd.activo = true THEN cd.id END), 0) AS cajas,((SELECT COALESCE((Select COALESCE(count(i.palletid),0) from pallet pp
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

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpalletboxentry(p_estado character varying, p_palletid integer, p_cajaempaqueid integer)
 RETURNS TABLE(serial text, mac text, "smartCard" text, "numeroSmartcard" text, "Serial3" text, "Serial4" text, codigo text, descripcion text, nivel text, "Lote" text, modelo text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
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

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpalletboxpallet(p_numero character varying, p_destino character varying)
 RETURNS TABLE(id text, numero text, cajas text, tipologia text, "Descripcion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_destino)=LOWER('PREDESPACHO')) THEN
        IF (p_numero='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, NULL::text FROM (
            SELECT p.id, p.numero, COALESCE(COUNT(CASE WHEN cd.activo = true THEN cd.id END), 0) AS cajas, t.codigo AS tipologia from pallet p
            left join cajadespacho cd on p.id=cd.palletid
            inner join maestro t on p.tipologiaid=t.id
            inner join general.estado d on p.destinoid=d.id
            where LOWER(d.nombre) = LOWER(p_destino) and p.activo=true
            group by p.id,p.numero,t.codigo
            ) AS q(c1, c2, c3, c4);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, NULL::text FROM (
            SELECT p.id, p.numero, COALESCE(COUNT(CASE WHEN cd.activo = true THEN cd.id END), 0) AS cajas, t.codigo AS tipologia from pallet p
            left join cajadespacho cd on p.id=cd.palletid
            inner join maestro t on p.tipologiaid=t.id
            inner join general.estado d on p.destinoid=d.id
            where p.numero=p_numero and LOWER(d.nombre) = LOWER(p_destino) and p.activo=true
            group by p.id,p.numero,t.codigo
            ) AS q(c1, c2, c3, c4);
        END IF;
    ELSE
        IF (p_numero='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            select p.id,p.numero,count(ce.id) AS cajas,t.codigo AS tipologia,right((it.descripcion)::text,11) AS descripcion from pallet p
            left join cajaempaque ce on p.id=ce.palletid
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap it on p.codigosapid=it.id
            inner join general.estado d on p.destinoid=d.id
            where LOWER(d.nombre) = LOWER(p_destino) and p.activo=true
            group by p.id,p.numero,t.codigo,it.descripcion
            ) AS q(c1, c2, c3, c4, c5);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            select p.id,p.numero,count(ce.id) AS cajas,t.codigo AS tipologia,right((it.descripcion)::text,11) AS descripcion from pallet p
            left join cajaempaque ce on p.id=ce.palletid
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap it on p.codigosapid=it.id
            inner join general.estado d on p.destinoid=d.id
            where p.numero=p_numero and LOWER(d.nombre) = LOWER(p_destino) and p.activo=true
            group by p.id,p.numero,t.codigo,it.descripcion
            ) AS q(c1, c2, c3, c4, c5);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpalletboxpalletweb(p_numero character varying, p_destino character varying)
 RETURNS TABLE(id text, "Numero" text, cajas text, "Descripcion" text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_destino)=LOWER('PREDESPACHO')) THEN
        IF (p_numero='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            SELECT p.id, p.numero AS numero, COALESCE(COUNT(CASE WHEN cd.activo = true THEN cd.id END), 0) AS cajas,it.descripcion AS descripcion, t.codigo AS tipologia from pallet p
            left join cajadespacho cd on p.id=cd.palletid
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap it on p.codigosapid=it.id
            inner join general.estado d on p.destinoid=d.id
            where LOWER(d.nombre) = LOWER(p_destino) and p.activo=true
            group by p.id,p.numero,t.codigo,it.descripcion
            ORDER BY p.id DESC
            ) AS q(c1, c2, c3, c4, c5);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            SELECT p.id,p.numero AS numero, COALESCE(COUNT(CASE WHEN cd.activo = true THEN cd.id END), 0) AS cajas,it.descripcion AS descripcion, t.codigo AS tipologia from pallet p
            left join cajadespacho cd on p.id=cd.palletid
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap it on p.codigosapid=it.id
            inner join general.estado d on p.destinoid=d.id
            where p.numero=p_numero and LOWER(d.nombre) = LOWER(p_destino) and p.activo=true
            group by p.id,p.numero,t.codigo,it.descripcion
            ORDER BY p.id DESC
            ) AS q(c1, c2, c3, c4, c5);
        END IF;
    ELSE
        IF (p_numero='') THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            select p.id,p.numero AS numero,count(ce.id) AS cajas,it.descripcion AS descripcion,t.codigo AS tipologia from pallet p
            left join cajaempaque ce on p.id=ce.palletid
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap it on p.codigosapid=it.id
            inner join general.estado d on p.destinoid=d.id
            where LOWER(d.nombre) = LOWER(p_destino) and
            p.activo=true
            group by p.id,p.numero,t.codigo,it.descripcion
            ORDER BY p.id DESC
            ) AS q(c1, c2, c3, c4, c5);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            select p.id,p.numero AS numero,count(ce.id) AS cajas,it.descripcion AS descripcion,t.codigo AS tipologia from pallet p
            left join cajaempaque ce on p.id=ce.palletid
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap it on p.codigosapid=it.id
            inner join general.estado d on p.destinoid=d.id
            where p.numero=p_numero and LOWER(d.nombre) = LOWER(p_destino) and p.activo=true
            group by p.id,p.numero,t.codigo,it.descripcion
            ORDER BY p.id DESC
            ) AS q(c1, c2, c3, c4, c5);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpalletboxvalidate(p_estado character varying, p_palletid integer, p_cajaempaqueid integer DEFAULT 0)
 RETURNS TABLE(serial text, mac text, "smartCard" text, "numeroSmartcard" text, "Serial3" text, "Serial4" text, "codigoSap" text, "descripcionSap" text, nivel text, lote text, modelo text, "existeEnEmpaque" text, "numPalletIngreso" text, "numCajaIngreso" text, "numPalletEmpaque" text, "numCajaEmpaque" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text FROM (
    SELECT
        i.serial,
        i.mac,
        e.smartcard,
        COALESCE(i_sc.numerosmartcard, '0') AS numerosmartcard,
        i.serial3,
        i.serial4,
        cs.codigo AS codigosap,
        cs.descripcion AS descripcionsap,
        n.codigo AS nivel,
        l.nombre AS lote,
        md.codigo AS modelo,
        CASE
            WHEN e.serialid IS NOT NULL
                 AND e.palletid = i.palletid
                 AND e.cajaempaqueid = i.cajaempaqueid
            THEN 1
            ELSE 0
        END AS existeenempaque,
        p_ing.numero AS numpalletingreso,
        ce_ing.numero AS numcajaingreso,
        COALESCE(p_emp.numero, 'N/A') AS numpalletempaque,
        COALESCE(ce_emp.numero, 'N/A') AS numcajaempaque
    FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid = cs.id
    INNER JOIN maestro n ON i.nivelid = n.id
    LEFT JOIN empaque e ON i.id = e.serialid
    LEFT JOIN lote l ON i.loteid = l.id
    LEFT JOIN maestro md ON i.modeloid = md.id
    LEFT JOIN ingreso i_sc ON e.smartcardid = i_sc.id
    LEFT JOIN pallet p_ing ON i.palletid = p_ing.id
    LEFT JOIN cajaempaque ce_ing ON i.cajaempaqueid = ce_ing.id
    LEFT JOIN pallet p_emp ON e.palletid = p_emp.id
    LEFT JOIN cajaempaque ce_emp ON e.cajaempaqueid = ce_emp.id
    WHERE i.palletid = p_palletid
      AND (i.cajaempaqueid = p_cajaempaqueid OR p_cajaempaqueid = 0 OR p_cajaempaqueid IS NULL)
      AND i.estadoid = 66
    ORDER BY i.fechamovimiento
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpart(p_parte character varying, p_estado character varying)
 RETURNS TABLE(id text, parte text, "Codigo" text, "Descripcion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_parte='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        select p.id,m.codigo AS parte,cs.codigo,cs.descripcion from parte p
        inner join codigosap cs on p.codigosapid=cs.id
        inner join maestro m on p.parteid=m.id
        inner join general.estado e on p.estadoid=e.id
        where LOWER(e.nombre) = LOWER(p_estado)
        ) AS q(c1, c2, c3, c4);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        select p.id,m.codigo AS parte,cs.codigo,cs.descripcion from parte p
        inner join codigosap cs on p.codigosapid=cs.id
        inner join maestro m on p.parteid=m.id
        inner join general.estado e on p.estadoid=e.id
        where LOWER(e.nombre) = LOWER(p_estado) and LOWER(m.codigo) = LOWER(p_parte)
        ) AS q(c1, c2, c3, c4);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchprealert(p_nombre character varying)
 RETURNS TABLE(id text, nombre text, "tipoOrigen" text, origen text, guia text, activo text, "Estado" text, "Responsable" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_nombre='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
        select p.id,p.nombre,t.codigo AS tipoorigen,o.codigo AS origen,p.guia,p.activo,p.estado,ur.nombres || ' ' || ur.apellidos AS responsable from prealerta p
        inner join maestro t on p.tipoorigenid=t.id
        inner join maestro o on p.origenid=o.id
        inner join general.usuario ur on p.idresponsable = ur.id
        order by p.nombre
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
        select p.id,p.nombre,t.codigo AS tipoorigen,o.codigo AS origen,p.guia,p.activo,p.estado,ur.nombres || ' ' || ur.apellidos AS responsable from prealerta p
        inner join maestro t on p.tipoorigenid=t.id
        inner join maestro o on p.origenid=o.id
        inner join general.usuario ur on p.idresponsable = ur.id
        where LOWER(p.nombre) = LOWER(p_nombre)
        order by p.nombre
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchprealertserial(p_prealertaid integer)
 RETURNS TABLE(serial text, mac text, "codigoSap" text, descripcion text, cantidad text, caja text, falla text, "tecnicoCliente" text, pedido text, tramite text, novedad text, garantia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text FROM (
    select serial,mac,codigosap,descripcion,cantidad,caja,falla,tecnicocliente,pedido,tramite,novedad,garantia from prealertaserial
    where prealertaid=p_prealertaid
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpredispatch(p_palletid integer)
 RETURNS TABLE(id text, numero text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    SELECT id,numero FROM cajaempaque
    WHERE palletid=p_palletid
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchpredispatchpallet(p_numero character varying)
 RETURNS TABLE(id text, numero text, cajas text, origen text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,p.numero,count(ce.id) AS cajas,o.nombre AS origen,t.codigo AS tipologia from pallet p
        inner join cajaempaque ce on ce.palletid=p.id
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        where e.nombre='PREDESPACHO' and o.nombre = 'EMPAQUE'
        group by p.id,p.numero,o.nombre,t.codigo
        ) AS q(c1, c2, c3, c4, c5);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,p.numero,count(ce.id) AS cajas,o.nombre AS origen,t.codigo AS tipologia from pallet p
        inner join cajaempaque ce on ce.palletid=p.id
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        where p.numero=p_numero and e.nombre='PREDESPACHO' and o.nombre = 'EMPAQUE'
        group by p.id,p.numero,o.nombre,t.codigo
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchprocessaccesory(p_estadolimpieza character varying)
 RETURNS TABLE(id text, codigo text, descripcion text, "estadoLimpieza" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_estadolimpieza='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        select a.id,cs.codigo,cs.descripcion,el.codigo AS estadolimpieza from accesorio a
        inner join codigosap cs on a.codigosapid=cs.id
        inner join maestro el on a.estadolimpiezaid=el.id
        inner join general.estado e on a.estadoid=e.id
        where e.nombre='LIMPIEZA-TERMINADO'
        ) AS q(c1, c2, c3, c4);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        select a.id,cs.codigo,cs.descripcion,el.codigo AS estadolimpieza from accesorio a
        inner join codigosap cs on a.codigosapid=cs.id
        inner join maestro el on a.estadolimpiezaid=el.id
        inner join general.estado e on a.estadoid=e.id
        where e.nombre='LIMPIEZA-TERMINADO' and LOWER(el.codigo) = LOWER(p_estadolimpieza)
        ) AS q(c1, c2, c3, c4);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchprocessboxpacking(p_palletid integer, p_cajaempaqueid integer, p_estado character varying)
 RETURNS TABLE(id text, numero text, seriales text, estado text, pallet text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_palletid=0) THEN
        IF (p_cajaempaqueid=0) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            SELECT ce.id,ce.numero,count(i.id) AS seriales,e.nombre AS estado,p.numero AS pallet FROM cajaempaque ce
            FULL OUTER JOIN ingreso i ON ce.id=i.cajaempaqueid
            INNER JOIN pallet p ON ce.palletid=p.id
            INNER JOIN general.estado e ON ce.estadoid=e.id
            WHERE LOWER(e.nombre) = LOWER(p_estado) and ce.activo=true
            GROUP BY ce.id,ce.numero,e.nombre,p.numero
            ORDER BY ce.numero
            ) AS q(c1, c2, c3, c4, c5);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            SELECT ce.id,ce.numero,count(i.id) AS seriales,e.nombre AS estado,p.numero AS pallet FROM cajaempaque ce
            FULL OUTER JOIN ingreso i ON ce.id=i.cajaempaqueid
            INNER JOIN pallet p ON ce.palletid=p.id
            INNER JOIN general.estado e ON ce.estadoid=e.id
            WHERE LOWER(e.nombre) = LOWER(p_estado) and ce.id=p_cajaempaqueid and ce.activo=true
            GROUP BY ce.id,ce.numero,e.nombre,p.numero
            ORDER BY ce.numero
            ) AS q(c1, c2, c3, c4, c5);
        END IF;
    ELSE
        IF (p_cajaempaqueid=0) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            SELECT ce.id,ce.numero,count(i.id) AS seriales,e.nombre AS estado,p.numero AS pallet FROM cajaempaque ce
            FULL OUTER JOIN ingreso i ON ce.id=i.cajaempaqueid
            INNER JOIN pallet p ON ce.palletid=p.id
            INNER JOIN general.estado e ON ce.estadoid=e.id
            WHERE ce.palletid=p_palletid and LOWER(e.nombre) = LOWER(p_estado) and ce.activo=true
            GROUP BY ce.id,ce.numero,e.nombre,p.numero
            ORDER BY ce.numero
            ) AS q(c1, c2, c3, c4, c5);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
            SELECT ce.id,ce.numero,count(i.id) AS seriales,e.nombre AS estado,p.numero AS pallet FROM cajaempaque ce
            FULL OUTER JOIN ingreso i ON ce.id=i.cajaempaqueid
            INNER JOIN pallet p ON ce.palletid=p.id
            INNER JOIN general.estado e ON ce.estadoid=e.id
            WHERE ce.palletid=p_palletid and LOWER(e.nombre) = LOWER(p_estado) and ce.numero=p_cajaempaqueid and ce.activo=true
            GROUP BY ce.id,ce.numero,e.nombre,p.numero
            ORDER BY ce.numero
            ) AS q(c1, c2, c3, c4, c5);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchprocesspart(p_estadocosmetica character varying, p_parte character varying)
 RETURNS TABLE(id text, parte text, estado text, codigosap text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_parte='' AND p_estadocosmetica <>'') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,m.codigo AS parte,e.codigo AS estado,cs.codigo AS codigosap,cs.descripcion from parte p
        inner join maestro m on p.parteid=m.id
        inner join general.estado es on p.estadoid=es.id
        inner join maestro e on p.estadocosmeticaid=e.id
        inner join codigosap cs on p.codigosapid=cs.id
        where es.nombre='COSMETICA-PROCESO' and LOWER(e.codigo) = LOWER(p_estadocosmetica)
        ) AS q(c1, c2, c3, c4, c5);
    ELSIF (p_parte <>'' AND p_estadocosmetica <>'') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,m.codigo AS parte,e.codigo AS estado,cs.codigo AS codigosap,cs.descripcion from parte p
        inner join maestro m on p.parteid=m.id
        inner join general.estado es on p.estadoid=es.id
        inner join maestro e on p.estadocosmeticaid=e.id
        inner join codigosap cs on p.codigosapid=cs.id
        where es.nombre='COSMETICA-PROCESO' and LOWER(e.codigo) = LOWER(p_estadocosmetica) and LOWER(m.codigo) = LOWER(p_parte)
        ) AS q(c1, c2, c3, c4, c5);
    ELSIF (p_parte ='' AND p_estadocosmetica='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,m.codigo AS parte,e.codigo AS estado,cs.codigo AS codigosap,cs.descripcion from parte p
        inner join maestro m on p.parteid=m.id
        inner join general.estado es on p.estadoid=es.id
        inner join maestro e on p.estadocosmeticaid=e.id
        inner join codigosap cs on p.codigosapid=cs.id
        where es.nombre='COSMETICA-PROCESO'
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchproductsapcode(p_codigo character varying)
 RETURNS TABLE(id text, codigo text, "Descripcion" text, familia text, stock text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_codigo='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT cs.id,cs.codigo,cs.descripcion,f.codigo AS familia,(SELECT count(i.serial) FROM ingreso i
        INNER JOIN general.estado e ON i.estadoid=e.id
        WHERE i.codigosapid=cs.id AND e.nombre='ALMACENAMIENTO') AS stock FROM codigosap cs
        LEFT JOIN maestro f ON cs.familiaid=f.id
        GROUP BY cs.id,cs.codigo,cs.descripcion,f.codigo
        ) AS q(c1, c2, c3, c4, c5);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        SELECT cs.id,cs.codigo,cs.descripcion,f.codigo AS familia,(SELECT count(i.serial) FROM ingreso i
        INNER JOIN general.estado e ON i.estadoid=e.id
        WHERE i.codigosapid=cs.id AND e.nombre='ALMACENAMIENTO') AS stock FROM codigosap cs
        LEFT JOIN maestro f ON cs.familiaid=f.id
        WHERE LOWER(cs.codigo) = LOWER(p_codigo)
        GROUP BY cs.id,cs.codigo,cs.descripcion,f.codigo
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchqualitydeliverypallet()
 RETURNS TABLE(id text, numero text, cajas text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    select p.id,p.numero,count(ce.id) AS cajas from pallet p
    left join cajaempaque ce on p.id=ce.palletid
    inner join general.estado d on p.destinoid=d.id
    where d.nombre='CALIDAD-ENTREGA' and p.activo=true
    group by p.id,p.numero
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchqualityentry()
 RETURNS TABLE(serial text, mac text, pallet text, caja text, "SmartCard" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
    SELECT i.serial,i.mac,p.numero AS pallet,ce.numero AS caja,i.smartcard FROM ingreso i
    INNER JOIN pallet p ON i.palletid=p.id
    INNER JOIN cajaempaque ce ON i.cajaempaqueid=ce.id
    INNER JOIN general.estado e ON i.estadoid=e.id
    WHERE e.nombre='CALIFICADO'
    ) AS q(c1, c2, c3, c4, c5);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchreceivepacking(p_estado character varying, p_pallet character varying)
 RETURNS TABLE(id text, numero text, pallet text, cantidad text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_pallet='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        SELECT ce.id,ce.numero,p.numero AS pallet,count(i.serial) AS cantidad FROM cajaempaque ce
        INNER JOIN ingreso i ON ce.id=i.cajaempaqueid
        INNER JOIN pallet p ON ce.palletid=p.id
        INNER JOIN general.estado e on ce.estadoid=e.id
        WHERE LOWER(e.nombre) = LOWER(p_estado)
        GROUP BY ce.id,ce.numero,p.numero
        ) AS q(c1, c2, c3, c4);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        SELECT ce.id,ce.numero,p.numero AS pallet,count(i.serial) AS cantidad FROM cajaempaque ce
        INNER JOIN ingreso i ON ce.id=i.cajaempaqueid
        INNER JOIN pallet p ON ce.palletid=p.id
        INNER JOIN general.estado e on ce.estadoid=e.id
        WHERE LOWER(e.nombre) = LOWER(p_estado) AND p.numero=p_pallet
        GROUP BY ce.id,ce.numero,p.numero
        ) AS q(c1, c2, c3, c4);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchreceivepallet(p_numero character varying, p_destino character varying, p_tipo character varying)
 RETURNS TABLE(id text, numero text, cantidad text, origen text, tipologia text, "CodigoSap" text, "Descripcion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        IF (LOWER(p_tipo)=LOWER('PALLETS')) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap, cs.descripcion AS descripcion from pallet p
                inner join ingreso i on p.id=i.palletid
   inner join maestro t on p.tipologiaid=t.id
                inner join general.estado o on p.origenid=o.id
                inner join general.estado e on p.destinoid=e.id
                inner join codigosap cs ON p.codigosapid = cs.id
                inner join maestro m ON cs.tipoid = m.id
                where LOWER(e.nombre) = LOWER(p_destino) and p.activo=true AND m.codigo ='SERIALIZADOS'
                group by p.id,p.numero,o.nombre,t.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7);
        ELSIF (LOWER(p_tipo)=LOWER('SMARTCARD')) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap, cs.descripcion AS descripcion from pallet p
                inner join ingreso i on p.id=i.palletid
                inner join maestro t on p.tipologiaid=t.id
                inner join general.estado o on p.origenid=o.id
                inner join general.estado e on p.destinoid=e.id
                inner join codigosap cs ON p.codigosapid = cs.id
                inner join maestro m ON cs.tipoid = m.id
                where LOWER(e.nombre) = LOWER(p_destino) and p.activo=true AND m.codigo ='SMARTCARD'
                group by p.id,p.numero,o.nombre,t.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
            select p.id,p.numero,count(a.id) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap, cs.descripcion AS descripcion from pallet p
                inner join accesorio a on p.id=a.palletid
                inner join maestro t on p.tipologiaid=t.id
                inner join general.estado o on p.origenid=o.id
                inner join general.estado e on p.destinoid=e.id
                inner join codigosap cs ON p.codigosapid = cs.id
                where LOWER(e.nombre) = LOWER(p_destino) and p.activo=true
                group by p.id,p.numero,o.nombre,t.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7);
        END IF;
    ELSE
        IF (LOWER(p_tipo)=LOWER('PALLETS')) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap, cs.descripcion AS descripcion from pallet p
                inner join ingreso i on p.id=i.palletid
                inner join maestro t on p.tipologiaid=t.id
                inner join general.estado o on p.origenid=o.id
                inner join general.estado e on p.destinoid=e.id
                inner join codigosap cs ON p.codigosapid = cs.id
                inner join maestro m ON cs.tipoid = m.id
                where p.numero=p_numero and LOWER(e.nombre) = LOWER(p_destino) and p.activo=true AND m.codigo ='SERIALIZADOS'
                group by p.id,p.numero,o.nombre,t.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7);
        ELSIF (LOWER(p_tipo)=LOWER('SMARTCARD')) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap, cs.descripcion AS descripcion from pallet p
                inner join ingreso i on p.id=i.palletid
         inner join maestro t on p.tipologiaid=t.id
                inner join general.estado o on p.origenid=o.id
                inner join general.estado e on p.destinoid=e.id
                inner join codigosap cs ON p.codigosapid = cs.id
                inner join maestro m ON cs.tipoid = m.id
                where p.numero=p_numero and LOWER(e.nombre) = LOWER(p_destino) and p.activo=true AND m.codigo ='SMARTCARD'
        group by p.id,p.numero,o.nombre,t.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
            select p.id,p.numero,count(a.id) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap, cs.descripcion AS descripcion from pallet p
                inner join accesorio a on p.id=a.palletid
                inner join maestro t on p.tipologiaid=t.id
                inner join general.estado o on p.origenid=o.id
                inner join general.estado e on p.destinoid=e.id
                inner join codigosap cs ON p.codigosapid = cs.id
                where p.numero=p_numero and LOWER(e.nombre) = LOWER(p_destino) and p.activo=true
                group by p.id,p.numero,o.nombre,t.codigo,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchreceivepartspallet(p_numero character varying, p_destino character varying)
 RETURNS TABLE(id text, numero text, cantidad text, origen text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,p.numero,count(pa.id) AS cantidad,o.nombre AS origen,t.codigo AS tipologia from pallet p
        inner join parte pa on p.id=pa.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        where LOWER(e.nombre) = LOWER(p_destino) and p.activo=true
        group by p.id,p.numero,o.nombre,t.codigo
        ) AS q(c1, c2, c3, c4, c5);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
        select p.id,p.numero,count(pa.id) AS cantidad,o.nombre AS origen,t.codigo AS tipologia from pallet p
        inner join parte pa on p.id=pa.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        where p.numero=p_numero and LOWER(e.nombre) = LOWER(p_destino) and p.activo=true
        group by p.id,p.numero,o.nombre,t.codigo
        ) AS q(c1, c2, c3, c4, c5);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchrepairedrepair(p_tecnicoreparacionid integer, p_estado character varying)
 RETURNS TABLE(serial text, "fechaReparacion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    select r.serial,MAX((r.fechareparacion)::date) AS fechareparacion from reparacion r
    inner join ingreso i on r.serialid=i.id
    inner join maestro m on r.estadofinalid=m.id
    inner join general.estado e on i.estadoid=e.id
    where LOWER(m.codigo) = LOWER(p_estado) and e.nombre='REPARACION' and r.tecnicoreparacionid=p_tecnicoreparacionid
    group by r.serial
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchrepairentry(p_estadofinal character varying, p_perfil character varying, p_usuarioid integer)
 RETURNS TABLE("Serial" text, "Mac" text, "Codigo" text, "Descripcion" text, "Estado" text, "TruckRollId" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
    SELECT DISTINCT i.serial, i.mac, cs.codigo, cs.descripcion, e.codigo AS estado, i.truckrollid
    FROM ingreso i
    CROSS JOIN LATERAL (SELECT r2.id, r2.estadofinalid FROM reparacion r2 WHERE r2.serialid = i.id ORDER BY r2.id DESC LIMIT 1) ult
    INNER JOIN codigosap cs ON i.codigosapid = cs.id
    INNER JOIN maestro e ON ult.estadofinalid = e.id
    INNER JOIN general.estado es ON i.estadoid = es.id
    WHERE es.nombre = 'REPARACION-TERMINADO'
      AND LOWER(e.codigo) = LOWER(p_estadofinal)
      AND (LOWER(p_perfil) = LOWER('ADMINISTRADOR') OR LOWER(p_perfil) = LOWER('ASIGNADOR REP') OR (i.usuarioidmovimiento = p_usuarioid AND LOWER(p_perfil) <> LOWER('ADMINISTRADOR')))
    GROUP BY i.serial, i.mac, cs.codigo, cs.descripcion, e.codigo, i.truckrollid
    ) AS q(c1, c2, c3, c4, c5, c6);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchsapcode(p_codigo character varying)
 RETURNS TABLE(id text, codigo text, descripcion text, familia text, validacion text, direccion text, largos text, recorte text, reingreso text, clasificacion text, "numSerial" text, activo text, tipo text, "Asignacion" text, "AsignacionFa" text, "LargosMac" text, "LargosSerial3" text, "LargosSerial4" text, "LargosSerial5" text, "tipoEquipo" text, area text, modelo text, proveedor text, "validacionMac" text, "direccionMac" text, "recorteMac" text, multimodelo text, "cantidadCaja" text, smartcard text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_codigo='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text, q.c20::text, q.c21::text, q.c22::text, q.c23::text, q.c24::text, q.c25::text, q.c26::text, q.c27::text, q.c28::text, q.c29::text FROM (
        SELECT cs.id,cs.codigo,cs.descripcion,f.codigo AS familia,cs.validacion,cs.direccion,cs.largos,cs.recorte,cs.reingreso,c.codigo AS clasificacion,cs.numserial,cs.activo,t.codigo AS tipo,
        CASE
            WHEN cs.asignacionacc = false THEN 'NO'
            WHEN cs.asignacionacc = true THEN 'SI'
        END AS asignacion,
        CASE
            WHEN cs.asignacionfalla = false or cs.asignacionfalla is null THEN 'NO'
            WHEN cs.asignacionfalla = true THEN 'SI'
        END AS asignacionfa,cs.largosmac,cs.largosserial3,cs.largosserial4,cs.largosserial5,te.codigo AS tipoequipo, a.codigo AS area,m.codigo AS modelo,p.codigo AS proveedor,cs.validacionmac,cs.direccionmac,cs.recortemac,cs.multimodelo,cs.cantidadcaja,cs.smartcard FROM codigosap cs
        INNER JOIN maestro f ON cs.familiaid=f.id
        INNER JOIN maestro c ON cs.clasificacionid=c.id
        INNER JOIN maestro t ON cs.tipoid=t.id
        LEFT JOIN maestro te ON cs.tipoequipoid = te.id
        LEFT JOIN maestro a ON cs.areaid = a.id
        LEFT JOIN maestro m ON cs.modeloid = m.id
        LEFT JOIN maestro p ON cs.proveedorid = p.id
        ORDER BY cs.codigo
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25, c26, c27, c28, c29);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text, q.c19::text, q.c20::text, q.c21::text, q.c22::text, q.c23::text, q.c24::text, q.c25::text, q.c26::text, q.c27::text, q.c28::text, q.c29::text FROM (
        SELECT cs.id,cs.codigo,cs.descripcion,f.codigo AS familia,cs.validacion,cs.direccion,cs.largos,cs.recorte,cs.reingreso,c.codigo AS clasificacion,cs.numserial,cs.activo,t.codigo AS tipo,
        CASE
            WHEN cs.asignacionacc = false THEN 'NO'
            WHEN cs.asignacionacc = true THEN 'SI'
        END AS asignacion,
        CASE
            WHEN cs.asignacionfalla = false or cs.asignacionfalla is null THEN 'NO'
            WHEN cs.asignacionfalla = true THEN 'SI'
        END AS asignacionfa, cs.largosmac,cs.largosserial3,cs.largosserial4,cs.largosserial5,te.codigo AS tipoequipo, a.codigo AS area,m.codigo AS modelo,p.codigo AS proveedor,cs.validacionmac,cs.direccionmac,cs.recortemac,cs.multimodelo,cs.cantidadcaja,cs.smartcard FROM codigosap cs
        INNER JOIN maestro f ON cs.familiaid=f.id
        INNER JOIN maestro c ON cs.clasificacionid=c.id
        INNER JOIN maestro t ON cs.tipoid=t.id
        LEFT JOIN maestro te ON cs.tipoequipoid = te.id
        LEFT JOIN maestro a ON cs.areaid = a.id
        LEFT JOIN maestro m ON cs.modeloid = m.id
        LEFT JOIN maestro p ON cs.proveedorid = p.id
        WHERE LOWER(cs.codigo) = LOWER(p_codigo)
        ORDER BY cs.codigo
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25, c26, c27, c28, c29);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchscrapentry(p_novedad character varying)
 RETURNS TABLE(serial text, mac text, codigosap text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    SELECT i.serial,i.mac,cs.codigo AS codigosap,cs.descripcion FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid=cs.id
    INNER JOIN general.estado e ON i.estadoid=e.id
    WHERE e.nombre='SCRAP-TERMINADO' and i.novedad=p_novedad
    ) AS q(c1, c2, c3, c4);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchseparatepalletaccesory(p_cantidad integer, p_palletid integer, p_palletdestinoid integer)
 RETURNS TABLE(id text, "Codigo" text, "Descripcion" text, "Tipologia" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_codigosaporigen varchar(255);
    v_esmultimodelo boolean;
BEGIN
    SELECT cs.codigo INTO v_codigosaporigen
    FROM pallet p
    INNER JOIN codigosap cs ON p.codigosapid = cs.id
    WHERE p.id = p_palletid LIMIT 1;
    v_esmultimodelo := CASE
        WHEN v_codigosaporigen ILIKE '%MULTIMODELO%' THEN 1
        ELSE 0
    END;
    IF v_esmultimodelo = true THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        SELECT a.id, cs.codigo, cs.descripcion, m.codigo AS tipologia
        FROM accesorio a
        INNER JOIN codigosap cs ON a.codigosapid = cs.id
        INNER JOIN pallet p ON a.palletid = p.id
        INNER JOIN maestro m ON p.tipologiaid = m.id
        WHERE a.palletid = p_palletid
          AND cs.codigo = (SELECT cs_destino.codigo
                           FROM pallet p_destino
                           INNER JOIN codigosap cs_destino ON p_destino.codigosapid = cs_destino.id
                           WHERE p_destino.id = p_palletdestinoid LIMIT 1) LIMIT p_cantidad
        ) AS q(c1, c2, c3, c4);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
        SELECT a.id, cs.codigo, cs.descripcion, m.codigo AS tipologia
        FROM accesorio a
        INNER JOIN codigosap cs ON a.codigosapid = cs.id
        INNER JOIN pallet p ON a.palletid = p.id
        INNER JOIN maestro m ON p.tipologiaid = m.id
        WHERE a.palletid = p_palletid LIMIT p_cantidad
        ) AS q(c1, c2, c3, c4);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchsimplicodesap()
 RETURNS TABLE(id text, codigo text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text FROM (
    SELECT cs.id,cs.codigo,cs.descripcion FROM codigosap cs
    INNER JOIN maestro t ON cs.tipoid = t.id
    WHERE t.codigo = 'ACCESORIOS'
    ORDER BY cs.descripcion
    ) AS q(c1, c2, c3);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchsmartcardentry(p_estadofinal character varying)
 RETURNS TABLE(serial text, codigo text, descripcion text, falla text, estado text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
    SELECT DISTINCT i.serial,cs.codigo,cs.descripcion,f.codigo AS falla,e.codigo AS estado FROM ingreso i
                INNER JOIN smartcard d ON i.id=d.serialid
                INNER JOIN codigosap cs ON i.codigosapid=cs.id
                INNER JOIN maestro f ON d.fallaid=f.id
                INNER JOIN maestro e ON d.estadofinalid=e.id
                INNER JOIN general.estado es ON i.estadoid=es.id
                WHERE ((es.nombre='SMARTCARD-TERMINADO') and (LOWER(e.codigo) = LOWER(p_estadofinal))) and ((d.fecha)::timestamp(3) >= (mssql.dateadd('minute',-5, (i.fechamovimiento)::timestamp))::timestamp(3))
    ) AS q(c1, c2, c3, c4, c5);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchstoragegrouppallet(p_numero character varying, p_tipoequipo character varying)
 RETURNS TABLE(id text, numero text, cantidad text, "codigoSap" text, "Descripcion" text, tipologia text, posicion text, origen text, lote text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
        select p.id,p.numero,count(i.serial) AS cantidad,cs.codigo AS codigosap,cs.descripcion AS descripcion,t.codigo AS tipologia,po.numero AS posicion,o.nombre AS origen,lo.nombre AS lote from pallet p
        left join ingreso i on p.id=i.palletid
        inner join posicion po on p.posicionid=po.id
        inner join maestro t on p.tipologiaid=t.id
        inner join codigosap cs on p.codigosapid=cs.id
        inner join general.estado e on p.destinoid=e.id
        inner join estado o on p.origenid=o.id
        inner join maestro m on cs.tipoid = m.id
        LEFT join codigosap ci ON i.codigosapid = ci.id
        LEFT join maestro mi on ci.tipoid = mi.id
        left join lote lo on p.loteid = lo.id
        where e.nombre='ALMACENAMIENTO' and p.activo=true and LOWER(m.codigo) = LOWER(p_tipoequipo)
        group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.nombre,lo.nombre,cs.descripcion
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
        select p.id,p.numero,count(i.serial) AS cantidad,cs.codigo AS codigosap,cs.descripcion AS descripcion,t.codigo AS tipologia,po.numero AS posicion,o.nombre AS origen,lo.nombre AS lote from pallet p
        left join ingreso i on p.id=i.palletid
        inner join posicion po on p.posicionid=po.id
        inner join maestro t on p.tipologiaid=t.id
        inner join codigosap cs on p.codigosapid=cs.id
        inner join general.estado e on p.destinoid=e.id
        inner join estado o on p.origenid=o.id
        inner join maestro m on cs.tipoid = m.id
        LEFT join codigosap ci ON i.codigosapid = ci.id
        LEFT join maestro mi on ci.tipoid = mi.id
        left join lote lo on p.loteid = lo.id
        where p.numero=p_numero
        and e.nombre='ALMACENAMIENTO' and p.activo=true and LOWER(m.codigo) = LOWER(p_tipoequipo)
        group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.nombre,lo.nombre,cs.descripcion
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchstoragegrouppalletaccesory(p_numero character varying, p_tipoaccesorio character varying)
 RETURNS TABLE(id text, numero text, cantidad text, "codigoSap" text, "Descripcion" text, tipologia text, posicion text, origen text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
        select p.id,p.numero,count(i.palletid) AS cantidad,cs.codigo AS codigosap,cs.descripcion AS descripcion,t.codigo AS tipologia,po.numero AS posicion,o.nombre AS origen from pallet p
            left join accesorio i on p.id=i.palletid
            inner join posicion po on p.posicionid=po.id
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap cs on p.codigosapid=cs.id
            inner join general.estado e on p.destinoid=e.id
            inner join estado o on p.origenid=o.id
            inner join maestro m on cs.tipoid = m.id
            where e.nombre='ALMACENAMIENTO' and p.activo=true and m.codigo = 'ACCESORIOS' and LOWER(i.tipoaccesorio) = LOWER(p_tipoaccesorio)
            group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.nombre,cs.descripcion
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
        select p.id,p.numero,count(i.palletid) AS cantidad,cs.codigo AS codigosap,cs.descripcion AS descripcion,t.codigo AS tipologia,po.numero AS posicion,o.nombre AS origen from pallet p
            left join accesorio i on p.id=i.palletid
            inner join posicion po on p.posicionid=po.id
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap cs on p.codigosapid=cs.id
            inner join general.estado e on p.destinoid=e.id
            inner join estado o on p.origenid=o.id
            inner join maestro m on cs.tipoid = m.id
            where p.numero=p_numero and e.nombre='ALMACENAMIENTO' and p.activo=true and m.codigo = 'ACCESORIOS' and LOWER(i.tipoaccesorio) = LOWER(p_tipoaccesorio)
            group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.nombre,cs.descripcion
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchstoragegrouppalletaccesorycreated(p_numero character varying, p_tipoaccesorio character varying)
 RETURNS TABLE(id text, numero text, cantidad text, "codigoSap" text, tipologia text, posicion text, origen text, lote text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
        select p.id,p.numero,count(i.palletid) AS cantidad,cs.codigo AS codigosap,t.codigo AS tipologia,po.numero AS posicion,o.nombre AS origen,lo.nombre AS lote from pallet p
            left join accesorio i on p.id=i.palletid
            inner join posicion po on p.posicionid=po.id
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap cs on p.codigosapid=cs.id
            inner join general.estado e on p.destinoid=e.id
            inner join estado o on p.origenid=o.id
            inner join maestro m on cs.tipoid = m.id
            left join lote lo on p.loteid = lo.id
            where e.nombre='ALMACENAMIENTO' and p.activo=true and m.codigo = 'ACCESORIOS' and (LOWER(i.tipoaccesorio) = LOWER(p_tipoaccesorio) or i.tipoaccesorio is null)
            group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.nombre,lo.nombre
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text FROM (
        select p.id,p.numero,count(i.palletid) AS cantidad,cs.codigo AS codigosap,t.codigo AS tipologia,po.numero AS posicion,o.nombre AS origen,lo.nombre AS lote from pallet p
            left join accesorio i on p.id=i.palletid
            inner join posicion po on p.posicionid=po.id
            inner join maestro t on p.tipologiaid=t.id
            inner join codigosap cs on p.codigosapid=cs.id
            inner join general.estado e on p.destinoid=e.id
            inner join estado o on p.origenid=o.id
            inner join maestro m on cs.tipoid = m.id
            left join lote lo on p.loteid = lo.id
            where p.numero=p_numero and e.nombre='ALMACENAMIENTO' and p.activo=true and m.codigo = 'ACCESORIOS' and (LOWER(i.tipoaccesorio) = LOWER(p_tipoaccesorio) or i.tipoaccesorio is null)
            group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.nombre,lo.nombre
        ) AS q(c1, c2, c3, c4, c5, c6, c7, c8);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchstoragepallet(p_numero character varying, p_tipo character varying, p_tipoaccesorio character varying)
 RETURNS TABLE(id text, numero text, cantidad text, origen text, tipologia text, "codigoSap" text, descripcion text, lote text, posicion text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        IF (LOWER(p_tipo)=LOWER('PALLETS')) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap,cs.descripcion AS descripcion,lo.nombre AS lote,po.numero AS posicion from pallet p
            inner join ingreso i on p.id=i.palletid
            inner join posicion po on p.posicionid=po.id
            inner join maestro t on p.tipologiaid=t.id
            inner join general.estado o on p.origenid=o.id
            inner join general.estado e on p.destinoid=e.id
            inner join codigosap cs ON p.codigosapid = cs.id
            inner join maestro m ON cs.tipoid = m.id
            left join lote lo ON p.loteid = lo.id
            where e.nombre='ALMACENAMIENTO' and p.activo=true and m.codigo ='SERIALIZADOS'
            group by p.id,p.numero,o.nombre,t.codigo,lo.nombre,po.numero,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
            select p.id,p.numero,count(a.id) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap,cs.descripcion AS descripcion,lo.nombre AS lote,po.numero AS posicion from pallet p
            inner join accesorio a on p.id=a.palletid
            inner join posicion po on p.posicionid=po.id
            inner join maestro t on p.tipologiaid=t.id
            inner join general.estado o on p.origenid=o.id
            inner join general.estado e on p.destinoid=e.id
            inner join codigosap cs ON p.codigosapid = cs.id
            left join lote lo ON p.loteid = lo.id
            where e.nombre='ALMACENAMIENTO' and p.activo=true and LOWER(a.tipoaccesorio) = LOWER(p_tipoaccesorio)
            group by p.id,p.numero,o.nombre,t.codigo,lo.nombre,po.numero,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
        END IF;
    ELSE
        IF (LOWER(p_tipo)=LOWER('PALLETS')) THEN
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
            select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap,cs.descripcion AS descripcion,lo.nombre AS lote,po.numero AS posicion from pallet p
            inner join ingreso i on p.id=i.palletid
            inner join posicion po on p.posicionid=po.id
            inner join maestro t on p.tipologiaid=t.id
            inner join general.estado o on p.origenid=o.id
            inner join general.estado e on p.destinoid=e.id
            inner join codigosap cs ON p.codigosapid = cs.id
            inner join maestro m ON cs.tipoid = m.id
            left join lote lo ON p.loteid = lo.id
            where p.numero=p_numero and e.nombre='ALMACENAMIENTO' and p.activo=true and m.codigo ='SERIALIZADOS'
            group by p.id,p.numero,o.nombre,t.codigo,lo.nombre,po.numero,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
        ELSE
            RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
            select p.id,p.numero,count(a.id) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,cs.codigo AS codigosap,cs.descripcion AS descripcion,lo.nombre AS lote,po.numero AS posicion from pallet p
            inner join accesorio a on p.id=a.palletid
            inner join posicion po on p.posicionid=po.id
            inner join maestro t on p.tipologiaid=t.id
            inner join general.estado o on p.origenid=o.id
            inner join general.estado e on p.destinoid=e.id
            inner join codigosap cs ON p.codigosapid = cs.id
            left join lote lo ON p.loteid = lo.id
            where p.numero=p_numero and e.nombre='ALMACENAMIENTO' and p.activo=true and LOWER(a.tipoaccesorio) = LOWER(p_tipoaccesorio)
            group by p.id,p.numero,o.nombre,t.codigo,lo.nombre,po.numero,cs.codigo,cs.descripcion
            ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
        END IF;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchtransitentry(p_palletid integer)
 RETURNS TABLE(serial text, mac text, codigosap text, descripcion text, tipologia text, lote text, guia text, documento text, fecha text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
    SELECT i.serial,i.mac,cs.codigo AS codigosap,cs.descripcion,t.codigo AS tipologia,lo.nombre AS lote,i.guia,i.documento,i.fecha FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid=cs.id
    INNER JOIN maestro t ON i.tipologiaid=t.id
    LEFT JOIN lote lo ON i.loteid = lo.id
    INNER JOIN maestro tcs ON cs.tipoid = tcs.id
    WHERE i.palletid=p_palletid and tcs.codigo = 'SERIALIZADOS'
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchtransitpallet(p_numero character varying)
 RETURNS TABLE(id text, numero text, cantidad text, origen text, tipologia text, lote text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_numero='') THEN
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,lo.nombre AS lote from pallet p
        left join ingreso i on p.id=i.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        left join lote lo ON p.loteid = lo.id
        where e.nombre='TRANSITO' and p.activo=true
        group by p.id,p.numero,o.nombre,t.codigo,lo.nombre
        ) AS q(c1, c2, c3, c4, c5, c6);
    ELSE
        RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text FROM (
        select p.id,p.numero,count(i.serial) AS cantidad,o.nombre AS origen,t.codigo AS tipologia,lo.nombre AS lote from pallet p
        left join ingreso i on p.id=i.palletid
        inner join maestro t on p.tipologiaid=t.id
        inner join general.estado o on p.origenid=o.id
        inner join general.estado e on p.destinoid=e.id
        left join lote lo ON p.loteid = lo.id
        where p.numero=p_numero and e.nombre='TRANSITO' and p.activo=true
        group by p.id,p.numero,o.nombre,t.codigo,lo.nombre
        ) AS q(c1, c2, c3, c4, c5, c6);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchunificationentry(p_usuarioid integer)
 RETURNS TABLE(serial text, mac text, codigo text, descripcion text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text FROM (
    SELECT i.serial,i.mac,cs.codigo,cs.descripcion,t.codigo AS tipologia FROM ingreso i
    INNER JOIN codigosap cs ON i.codigosapid=cs.id
    INNER JOIN maestro t ON i.tipologiaid=t.id
    INNER JOIN general.estado e ON i.estadoid=e.id
    WHERE e.nombre='UNIFICACION' and i.usuarioidmovimiento=p_usuarioid
    ) AS q(c1, c2, c3, c4, c5);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchunreadableentry(p_palletid integer)
 RETURNS TABLE(serial text, mac text, "consecutivoSerial" text, "consecutivoMac" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text FROM (
    SELECT CONCAT(i.serial,', ',i.serial3,', ',i.serial4,', ',i.serial5) AS serial,i.mac,il.consecutivoserial,il.consecutivomac FROM ingreso i
    INNER JOIN ilegible il ON i.id=il.serialid
    WHERE i.palletid=p_palletid
    ) AS q(c1, c2, c3, c4);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchupdatepart(p_cantidad integer, p_estadoid integer, p_parte character varying, p_codigosap character varying)
 RETURNS TABLE(id text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    select p.id from parte p
    inner join codigosap cs on p.codigosapid=cs.id
    inner join maestro m on p.parteid=m.id
    where p.estadoid=p_estadoid and LOWER(m.codigo) = LOWER(p_parte) and LOWER(cs.codigo) = LOWER(p_codigosap) LIMIT p_cantidad
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchworflow()
 RETURNS TABLE(id text, modulo text, origen text, opcion text, descripcion text, tipologia text, nivel text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text FROM (
    SELECT w.id, m.nombre AS modulo, o.nombre AS origen, COALESCE(p.nombre, 'NINGUNA') AS opcion, p.descripcion, COALESCE(t.codigo, 'NINGUNA') AS tipologia, COALESCE(n.codigo, 'NINGUNA') AS nivel
    FROM workflow w
    INNER JOIN general.modulo m ON w.moduloid = m.id
    INNER JOIN general.estado o ON w.origenid = o.id
    LEFT JOIN general.permiso p ON w.opcionid = p.id
    LEFT JOIN maestro t ON w.tipologiaid = t.id
    LEFT JOIN maestro n ON w.nivelid = n.id
    ) AS q(c1, c2, c3, c4, c5, c6, c7);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_searchworflowdestiny(p_workflowid integer)
 RETURNS TABLE(id text, nombre text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text FROM (
    select e.id,e.nombre from workflowdestino w
    inner join general.estado e on w.destinoid=e.id
    where w.workflowid=p_workflowid
    order by e.nombre
    ) AS q(c1, c2);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendaccesory(p_palletid integer, p_estadoid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE accesorio
    SET estadoid = p_estadoid
    WHERE (palletid=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendallpallet(p_destinoid integer, p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE pallet
    SET destinoid = p_destinoid
    WHERE (id=p_palletid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendcosmeticentry(p_estadoid integer, p_tipologiaid integer, p_usuarioid integer, p_palletid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        tipologiaid = p_tipologiaid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (palletid=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendentry(p_estadoid integer, p_tipologiaid integer, p_usuarioid integer, p_palletid integer, p_opcion integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
    v_batchsize integer;
    v_processedrows integer;
    v_currentbatch integer;
    v_esmultimodelo boolean;
    v_pallettipologiaid integer;
    v_tipologiafinal integer;
BEGIN
    v_batchsize := 500;
    v_processedrows := 0;
    SELECT CASE WHEN cs.codigo = 'MULTIMODELO' THEN 1 ELSE 0 END, p.tipologiaid INTO v_esmultimodelo, v_pallettipologiaid
    FROM pallet p
    INNER JOIN codigosap cs ON p.codigosapid = cs.id
    WHERE p.id = p_palletid;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    IF v_esmultimodelo = true AND EXISTS (
        SELECT 1
        FROM maestro m
        WHERE m.id = v_pallettipologiaid
        AND m.codigo = 'SCRAP'
    ) THEN
        v_tipologiafinal := v_pallettipologiaid;
    ELSE
        v_tipologiafinal := p_tipologiaid;
    END IF;
    p_filas := 0;
    WHILE 1 = 1 LOOP
        BEGIN
            UPDATE ingreso SET estadoid = p_estadoid,
                tipologiaid = CASE
                                WHEN v_esmultimodelo = false THEN v_tipologiafinal
                                ELSE v_tipologiafinal
                            END,
                usuarioidmovimiento = p_usuarioid,
                fechamovimiento = LOCALTIMESTAMP,
                palletid = CASE WHEN p_opcion = 1 THEN 0 ELSE palletid END
            WHERE ctid IN (SELECT ctid FROM ingreso WHERE (palletid = p_palletid
                            AND estadoid <> p_estadoid) LIMIT v_batchsize);
            GET DIAGNOSTICS v_rowcount = ROW_COUNT;
            v_currentbatch := v_rowcount;
            p_filas := p_filas + v_currentbatch;
            IF v_currentbatch = 0 OR v_currentbatch < v_batchsize THEN
                EXIT;
            END IF;
            PERFORM pg_sleep(extract(epoch FROM ('00:00:00.010')::interval));
        EXCEPTION WHEN OTHERS THEN
            IF 0 > 0 THEN
                NULL;
            END IF;
            RAISE EXCEPTION USING MESSAGE = 'Error procesando batch';
            RETURN;
        END;
    END LOOP;
    RETURN;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendmodelsentry(p_estadoid integer, p_usuarioid integer, p_palletiddestino integer, p_palletid integer, p_codigosapid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP,
        palletid = p_palletiddestino
    WHERE (palletid=p_palletid and codigosapid=p_codigosapid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendnoveltyentry(p_estadoid integer, p_tipologiaid integer,
        p_usuarioid integer, p_palletid integer, p_opcion integer, INOUT p_filas integer DEFAULT NULL)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
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
DROP FUNCTION IF EXISTS wmswdtelefonicacolombiabog.pa_updateaccesorynovedad(integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendpallet(p_destinoid integer, p_tipologiaid integer, p_posicionid integer, p_estado integer, p_palletid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE pallet
    SET destinoid = p_destinoid,
        tipologiaid = p_tipologiaid,
        posicionid = p_posicionid,
        activo = (p_estado)::boolean
    WHERE (id=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendpart(p_opcion integer, p_estadoid integer, p_usuarioid integer, p_palletid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    IF (p_opcion=0) THEN
        UPDATE parte
        SET estadoid = p_estadoid,
            usuarioidmovimiento = p_usuarioid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (palletid=p_palletid);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    ELSE
        UPDATE parte
        SET estadoid = p_estadoid,
            palletid = 0,
            usuarioidmovimiento = p_usuarioid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (palletid=p_palletid);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    END IF;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_sendstorageentry(p_estadoid integer, p_tipologiaid integer, p_usuarioid integer, p_palletid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        tipologiaid = p_tipologiaid,
        palletidalmacen = p_palletid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (palletid=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_serialesbypallet(p_palletid integer)
 RETURNS TABLE(serial text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text FROM (
    SELECT i.serial FROM ingreso i
        INNER JOIN codigosap cs ON i.codigosapid = cs.id
        INNER JOIN maestro t ON cs.tipoid = t.id
        WHERE i.palletid = p_palletid AND t.codigo = 'SERIALIZADOs'
    ) AS q(c1);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_toggleforecastactivo(p_id integer, p_estado boolean, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE forecast
    SET activo = (p_estado)::boolean,
        updated_at = LOCALTIMESTAMP
    WHERE (id = p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := v_rowcount;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_togglemanoobraactivo(p_id integer, p_estado boolean, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE manodeobra
    SET activo = (p_estado)::boolean,
        updated_at = LOCALTIMESTAMP
    WHERE (id = p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := v_rowcount;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_unifyentry(p_palletiddestino integer, p_tipologiaid integer, p_usuarioid integer, p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET palletid = p_palletiddestino,
        palletidalmacen = p_palletiddestino,
        tipologiaid = p_tipologiaid,
        usuarioidmovimiento = p_usuarioid
    WHERE (palletid=p_palletid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_unifypallet(p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE pallet
    SET activo = (0)::boolean
    WHERE (id=p_palletid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateallrepair(p_estadofinalid integer, p_falla1id integer, p_falla2id integer, p_falla3id integer, p_falla4id integer, p_partescambiadas character varying, p_motivoscrapid integer, p_tecnicoreparacionid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE reparacion
    SET estadofinalid = p_estadofinalid,
        falla1id = p_falla1id,
        falla2id = p_falla2id,
        falla3id = p_falla3id,
        falla4id = p_falla4id,
        partescambiadas = p_partescambiadas,
        motivoscrapid = p_motivoscrapid,
        tecnicoreparacionid = p_tecnicoreparacionid,
        fechareparacion = LOCALTIMESTAMP
    WHERE (id=(select max(r.id) AS id from reparacion r
        inner join maestro m on r.estadofinalid=m.id
        where r.serial=p_serial and m.codigo='ASIGNADO'));
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateassemble(p_usuarioidasignado integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ensamble
    SET usuarioidasignado = p_usuarioidasignado
    WHERE (id=(SELECT max(e.id)
                    FROM ensamble e
                    INNER JOIN ingreso i ON e.serialid=i.id
                    WHERE i.serial = p_serial));
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatebatchpalletentry(p_loteid integer, p_usuarioid integer, p_palletid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    IF ((SELECT cs.codigo from codigosap cs INNER JOIN pallet p ON cs.id = p.codigosapid
                WHERE p.id= p_palletid) = 'MULTIMODELO' ) THEN
        UPDATE pallet
        SET loteid = p_loteid,
            fechamodifica = LOCALTIMESTAMP,
            usuarioidmodifica = p_usuarioid
        WHERE (id=p_palletid);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        p_filas := (v_rowcount);
    ELSE
        UPDATE pallet
        SET loteid = p_loteid,
            fechamodifica = LOCALTIMESTAMP,
            usuarioidmodifica = p_usuarioid
        WHERE (id=p_palletid);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        UPDATE ingreso
        SET loteid = p_loteid,
            fechamovimiento = LOCALTIMESTAMP,
            usuarioidmovimiento = p_usuarioid
        WHERE (palletid=p_palletid);
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        p_filas := (v_rowcount);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatebatchserialentry(p_loteid integer, p_usuarioid integer, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET loteid = p_loteid,
        fechamovimiento = LOCALTIMESTAMP,
        usuarioidmovimiento = p_usuarioid
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatebypallet(p_palletid integer, p_destinyid integer, p_positionid integer, p_userid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE wmswdtelefonicacolombiabog.pallet
    SET destinoid = p_destinyid,
        posicionid = p_positionid,
        usuarioidmodifica = p_userid,
        fechamodifica = LOCALTIMESTAMP
    WHERE (id = p_palletid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatechangedentry(p_serial1 character varying, p_serial2 character varying, p_mac character varying, p_estadoid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET serial = p_serial2,
        mac = p_mac,
        estadoid = p_estadoid
    WHERE (id=(SELECT id FROM ingreso
        WHERE serial=p_serial1));
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatechangedpackingentry(p_estadoid integer, p_palletid integer, p_cajaempaqueid integer, p_serial character varying, p_usuarioid integer, p_tipologiaid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        palletid = p_palletid,
        palletidempaque = p_palletid,
        cajaempaqueid = p_cajaempaqueid,
        usuarioidmovimiento = p_usuarioid,
        tipologiaid = p_tipologiaid
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateciudad(p_nombre character varying, p_codigociudad character varying, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ciudad
    SET nombre = p_nombre,
        codigociudad = p_codigociudad
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateclasificacion(p_estadoenviado character varying, p_nivelid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE clasificacion
    SET estadoenviado = p_estadoenviado,
        nivelid = p_nivelid,
        fecha = LOCALTIMESTAMP
    WHERE (id=
            (SELECT max(id) AS id
             FROM clasificacion
             WHERE serial=p_serial));
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateclasificationentry(p_estadoid integer, p_nivelid integer, p_usuarioid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso AS i
    SET estadoid = p_estadoid,
        nivelid = p_nivelid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (i.serial = p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatecleaning(p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE limpieza
    SET asignacion_confirmada = ('1')::boolean
    WHERE (id = (
            SELECT MAX(l.id)
            FROM limpieza l
            INNER JOIN ingreso i ON l.serialid = i.id
            WHERE i.serial = p_serial
        ));
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatedeliveryaccesory(p_id integer, p_destinoid integer, p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE accesorio
    SET estadoid = p_destinoid,
        palletid = p_palletid
    WHERE (id=p_id);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatediagnostic(p_estadofinalid integer, p_fallaid integer, p_serial character varying, p_estadocalidadid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE diagnostico
    SET estadofinalid = p_estadofinalid,
        fallaid = p_fallaid,
        fecha = LOCALTIMESTAMP,
        estadocalidadid = p_estadocalidadid
    WHERE (id=
            (SELECT max(id) AS id
             FROM diagnostico
             WHERE serial=p_serial));
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatedispatchentry(p_estadoid integer, p_palletid integer, p_cajadespachoid integer, p_usuarioid integer, p_serial character varying, p_loteid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        palletid = p_palletid,
        cajadespachoid = p_cajadespachoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP,
        loteid = p_loteid
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatefinalstatequality(p_palletid integer, p_estadofinalid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE calidad
    SET estadofinalid = p_estadofinalid
    WHERE (palletid=p_palletid and estadofinalid=0);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateforecast(p_id integer, p_linea_negocio character varying DEFAULT NULL::character varying, p_fecha date DEFAULT NULL::date, p_fecha_entrega date DEFAULT NULL::date, p_forecast_und numeric DEFAULT 0, p_jornada character varying DEFAULT 'LV'::character varying, p_dias_habiles integer DEFAULT NULL::integer, p_observaciones character varying DEFAULT NULL::character varying, p_usuario character varying DEFAULT NULL::character varying, p_familias_json text DEFAULT NULL::text, INOUT p_filas integer DEFAULT NULL::integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE forecast
    SET linea_negocio = p_linea_negocio,
        fecha = p_fecha,
        fecha_entrega = p_fecha_entrega,
        forecast_und = p_forecast_und,
        jornada = p_jornada,
        dias_habiles = p_dias_habiles,
        observaciones = p_observaciones,
        usuario = p_usuario,
        familias_json = p_familias_json,
        updated_at = LOCALTIMESTAMP
    WHERE (id = p_id AND activo = true);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := v_rowcount;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateincomebypallet(p_palletid integer, p_destinyid integer, p_option integer, p_userid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_option=0) THEN
        UPDATE wmswdtelefonicacolombiabog.ingreso
        SET estadoid = p_destinyid,
            usuarioidmovimiento = p_userid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (palletid = p_palletid);
    ELSE
        UPDATE wmswdtelefonicacolombiabog.ingreso
        SET estadoid = p_destinyid,
            palletid = 0,
            cajaempaqueid = 0,
            usuarioidmovimiento = p_userid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (palletid = p_palletid);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatelabel(p_nombre character varying, p_tipo character varying, p_impresion integer, p_codigosapid integer, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE etiqueta
    SET nombre = p_nombre,
        tipo = p_tipo,
        impresion = p_impresion,
        codigosapid = p_codigosapid
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatelabelfield(p_etiquetaid integer, p_nombre character varying, p_valor character varying, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE etiquetacampo
    SET etiquetaid = p_etiquetaid,
        nombre = p_nombre,
        valor = p_valor
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatelote(p_nombre character varying, p_descripcion character varying, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE lote
    SET nombre = p_nombre,
        descripcion = p_descripcion
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatemanodeobra(p_fecha date, p_detalle character varying DEFAULT NULL::character varying, p_observaciones character varying DEFAULT NULL::character varying, p_mano_obra_json text DEFAULT NULL::text, p_usuario character varying DEFAULT NULL::character varying, p_id integer DEFAULT NULL::integer, INOUT p_filas integer DEFAULT NULL::integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE manodeobra
    SET fecha = p_fecha,
        detalle = p_detalle,
        observaciones = p_observaciones,
        mano_obra_json = COALESCE(p_mano_obra_json, mano_obra_json),
        usuario = COALESCE(p_usuario, usuario),
        updated_at = LOCALTIMESTAMP
    WHERE (id = p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatemaster(p_codigo character varying, p_descripcion character varying, p_detalle character varying, p_adicional character varying, p_tipomaestroid integer, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE maestro
    SET codigo = p_codigo,
        descripcion = p_descripcion,
        detalle = p_detalle,
        adicional = p_adicional,
        tipomaestroid = p_tipomaestroid
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatemastertype(p_nombre character varying, p_descripcion character varying, p_tipomaestroid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE tipomaestro
    SET nombre = p_nombre,
        descripcion = p_descripcion
    WHERE (id=p_tipomaestroid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatenoveltyallentry(p_serial character varying, p_estadoid integer, p_tipologiaid integer, p_observaciones character varying, p_novedad character varying, p_usuarioid integer, p_fallacosmeticaid integer, p_fallafuncionalid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET observaciones = p_observaciones,
        novedad = p_novedad,
        estadoid = p_estadoid,
        tipologiaid = p_tipologiaid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP,
        fallacosmeticaid = p_fallacosmeticaid,
        fallafuncionalid = p_fallafuncionalid
    WHERE (serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateobservationpqrs(p_serialid integer, p_serial character varying, p_observacion text, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE pqrs
    SET x_studio_observaciones = p_observacion
    WHERE (LOWER(name) = LOWER(p_serial) and serialid = p_serialid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepacking(p_serialid integer, p_serialnuevo character varying, p_mac character varying, p_serialanterior character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE empaque
    SET serialid = p_serialid,
        serial = p_serialnuevo,
        mac = p_mac
    WHERE (id =(SELECT max(id) FROM empaque
        WHERE serial=p_serialanterior));
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepackingaccesory(p_codigosap character varying, p_tipo character varying, p_estado character varying, p_destinoid integer, p_serial character varying, p_palletid integer, p_cantidad integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (LOWER(p_estado)=LOWER('EMPAQUE')) THEN
        UPDATE accesorio
        SET estadoid = p_destinoid,
            serialempaque = p_serial,
            previouspalletid = palletid,
            palletid = p_palletid
        WHERE (id IN
                (SELECT a.id
                 FROM accesorio a
                 INNER JOIN codigosap cs ON a.codigosapid=cs.id
                 INNER JOIN general.estado e ON a.estadoid=e.id
                 WHERE LOWER(cs.codigo) = LOWER(p_codigosap)
                   AND LOWER(a.tipoaccesorio) = LOWER(p_tipo)
                   AND LOWER(e.nombre) = LOWER(p_estado) LIMIT p_cantidad));
    ELSE
        UPDATE accesorio
        SET estadoid = p_destinoid,
            serialempaque = '0',
            palletid = previouspalletid
        WHERE (id IN
                (SELECT a.id
                 FROM accesorio a
                 INNER JOIN codigosap cs ON a.codigosapid=cs.id
                 INNER JOIN general.estado e ON a.estadoid=e.id
                 WHERE LOWER(cs.codigo) = LOWER(p_codigosap)
                   AND LOWER(e.nombre) = LOWER(p_estado)
                   AND a.palletid=p_palletid
            ));
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepackingallentry(p_estadoid integer, p_usuarioid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        palletid = 0,
        palletidempaque = 0,
        cajaempaqueid = 0,
        cajadespachoid = 0,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP,
        smartcardid = NULL,
        smartcard = NULL
    WHERE (serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepackingentry(p_estadoid integer, p_palletid integer, p_cajaempaqueid integer, p_usuarioid integer, p_serial character varying, p_loteid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        palletid = p_palletid,
        palletidempaque = p_palletid,
        cajaempaqueid = p_cajaempaqueid,
        cajadespachoid = 0,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP,
        loteid = p_loteid
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepackingentrysmartcard(p_estadoid integer, p_palletid integer, p_cajaempaqueid integer, p_usuarioid integer, p_serial character varying, p_loteid integer, p_smartcardid integer, p_smartcard character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        palletid = p_palletid,
        palletidempaque = p_palletid,
        cajaempaqueid = p_cajaempaqueid,
        cajadespachoid = 0,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP,
        loteid = p_loteid,
        smartcardid = p_smartcardid,
        smartcard = p_smartcard
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepackingoffaccesory(p_serial character varying, p_destinoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE accesorio
    SET estadoid = p_destinoid,
        serialempaque = 0
    WHERE (serialempaque=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepackingsmartcard(p_smartcardid integer, p_smartcard character varying, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE empaque
    SET smartcardid = p_smartcardid,
        smartcard = p_smartcard
    WHERE (id =(SELECT max(id) FROM empaque
                        WHERE serial=p_serial));
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepackingweb(p_serialid integer, p_serialnuevo character varying, p_nivelidnuevo integer, p_mac character varying, p_serialanterior character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE empaque
    SET serialid = p_serialid,
        serial = p_serialnuevo,
        mac = p_mac,
        nivelid = p_nivelidnuevo
    WHERE (id =(SELECT max(id) FROM empaque
        WHERE serial=p_serialanterior));
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepalletandtipologyentry(p_palletid integer, p_tipologyid integer, p_usuarioid integer, p_estadoid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET palletid = p_palletid,
        tipologiaid = p_tipologyid,
        estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepalletentry(p_palletid integer, p_usuarioid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF ((SELECT cs.codigo FROM pallet p
    INNER JOIN codigosap cs ON p.codigosapid = cs.id
    WHERE p.id = p_palletid) <> 'MULTIMODELO') THEN
        UPDATE ingreso
        SET palletid = p_palletid,
            palletidalmacen = p_palletid,
            usuarioidmovimiento = p_usuarioid,
            fechamovimiento = LOCALTIMESTAMP,
            loteid = (SELECT loteid FROM pallet WHERE id = p_palletid)
        WHERE (serial=p_serial);
    ELSE
        UPDATE ingreso
        SET palletid = p_palletid,
            palletidalmacen = p_palletid,
            usuarioidmovimiento = p_usuarioid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (serial=p_serial);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepart(p_estadoid integer, p_estadocosmeticaid integer, p_usuarioid integer, p_parteid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE parte
    SET estadoid = p_estadoid,
        estadocosmeticaid = p_estadocosmeticaid,
        fechamovimiento = LOCALTIMESTAMP,
        usuarioidmovimiento = p_usuarioid
    WHERE (id=p_parteid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepositionpallet(p_palletid integer, p_posicionid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE pallet
    SET posicionid = p_posicionid
    WHERE (id=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepqrs(p_estadodiagnosticoid integer, p_falladiagnosticoid integer, p_serial character varying, p_diagnosticotencnicowoden character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_diagnosticoid integer;
    v_serialid integer;
BEGIN
    SELECT id, serialid INTO v_diagnosticoid, v_serialid
                FROM diagnostico
                WHERE serial = p_serial
                ORDER BY id DESC LIMIT 1;
    UPDATE pqrs
    SET estadodiagnosticoid = p_estadodiagnosticoid,
        falladiagnosticoid = p_falladiagnosticoid,
        fechadiagnostico = LOCALTIMESTAMP,
        x_studio_diagnstico_tcnico_woden = p_diagnosticotencnicowoden,
        id_diagnostico = v_diagnosticoid,
        x_studio_fecha_reporte = LOCALTIMESTAMP,
        id_stage = 4
    WHERE (LOWER(name) = LOWER(p_serial)
                    AND
                        serialid = v_serialid
                        AND id_diagnostico IS NULL
                        AND x_studio_diagnstico_tcnico_woden IS NULL);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateprealert(p_nombre character varying, p_tipoorigenid integer, p_origenid integer, p_guia character varying, p_prealertaid integer, p_idresponsable integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE prealerta
    SET nombre = p_nombre,
        tipoorigenid = p_tipoorigenid,
        origenid = p_origenid,
        guia = p_guia,
        idresponsable = p_idresponsable
    WHERE (id=p_prealertaid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepredispatchentry(p_estadoid integer, p_palletid integer, p_cajaempaqueid integer, p_usuarioid integer, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE wmswdtelefonicacolombiabog.ingreso
    SET estadoid = p_estadoid,
        palletid = p_palletid,
        palletidempaque = p_palletid,
        cajaempaqueid = p_cajaempaqueid,
        cajadespachoid = 0,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatepresupuesto(p_fecha date, p_segmentoid integer, p_nivelid integer, p_unidades character varying, p_presupuesto character varying, p_reclamo character varying, p_dias character varying, p_tipo character varying, p_detalle character varying, p_observaciones character varying, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE presupuesto
    SET fecha = p_fecha,
        segmentoid = p_segmentoid,
        nivelid = p_nivelid,
        unidades = p_unidades,
        presupuesto = p_presupuesto,
        reclamo = p_reclamo,
        diashabiles = p_dias,
        tipo = p_tipo,
        detalle = p_detalle,
        observaciones = p_observaciones
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatequality(p_fallafuncionalid integer, p_fallacosmeticaid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE calidad
    SET fallafuncionalid = p_fallafuncionalid,
        fallacosmeticaid = p_fallacosmeticaid
    WHERE (id=(SELECT max(id) AS id FROM calidad
        WHERE serial=p_serial));
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatequalityentry(p_estadoid integer, p_usuarioid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatequalitypalletentry(p_estadoid integer, p_usuarioid integer, p_palletid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (palletid=p_palletid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatequalitypalletentrybatch(p_estadoid integer, p_usuarioid integer, p_palletids text)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
    v_count integer;
BEGIN
    IF p_palletids IS NULL OR mssql.len((p_palletids)::text) = 0 THEN
        RAISE NOTICE '%', '⚠️ No se enviaron PalletIds válidos.';
        RETURN;
    END IF;
    DROP TABLE IF EXISTS tmp_ids;
    CREATE TEMP TABLE tmp_ids (palletid integer);
    INSERT INTO tmp_ids (palletid)
    SELECT mssql.try_int((value)::text)
    FROM (SELECT unnest(string_to_array((p_palletids)::text, ',')) AS value) AS sq475559
    WHERE rtrim((value)::text) <> '' AND mssql.try_int((value)::text) IS NOT NULL;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    UPDATE ingreso AS i
    SET estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    FROM tmp_ids x
    WHERE (i.palletid = x.palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    v_count := v_rowcount;
    RAISE NOTICE '%', CONCAT('✅ Pallets actualizados: ', v_count);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updaterepair(p_estadofinalid integer, p_falla1id integer, p_falla2id integer, p_falla3id integer, p_falla4id integer, p_partescambiadas character varying, p_motivoscrapid integer, p_tecnicoreparacionid integer, p_serial character varying, p_estadocalidadid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
    v_valfalladxid integer;
BEGIN
    v_valfalladxid := 0;
    SELECT falladxid INTO v_valfalladxid FROM reparacion
    WHERE id = (SELECT max(r.id) AS id
    FROM reparacion r
    INNER JOIN maestro m ON r.estadofinalid=m.id
    WHERE r.serial=p_serial
    AND m.codigo='ASIGNADO');
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    IF v_valfalladxid<>49 THEN
        UPDATE reparacion
        SET estadofinalid = p_estadofinalid,
            falla1id = p_falla1id,
            falla2id = p_falla2id,
            falla3id = p_falla3id,
            falla4id = p_falla4id,
            partescambiadas = p_partescambiadas,
            motivoscrapid = p_motivoscrapid,
            tecnicoreparacionid = p_tecnicoreparacionid,
            fechareparacion = LOCALTIMESTAMP,
            estadocalidadid = p_estadocalidadid
        WHERE (id=
                (SELECT max(r.id) AS id
                 FROM reparacion r
                 INNER JOIN maestro m ON r.estadofinalid=m.id
                 WHERE r.serial=p_serial
                   AND m.codigo='ASIGNADO'));
        GET DIAGNOSTICS v_rowcount = ROW_COUNT;
        p_filas := (v_rowcount);
    ELSE
        RAISE EXCEPTION USING MESSAGE = 'El Diagnostico del equipo no puede ser: SIN FALLA';
        RETURN;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updaterepairentry(p_serialreal character varying, p_macreal character varying, p_estadoid integer, p_usuarioid integer, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET serial = p_serialreal,
        mac = p_macreal,
        estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatesapcode(p_codigo character varying, p_descripcion character varying, p_familiaid integer, p_tipoid integer, p_validacion integer, p_direccion character varying, p_largos character varying, p_recorte integer, p_reingreso integer, p_clasificacionid integer, p_numserial integer, p_id integer, p_asignacion integer, p_largosmac character varying, p_largosserial3 character varying, p_largosserial4 character varying, p_largosserial5 character varying, p_tipoequipoid integer, p_areaid integer, p_modeloid integer, p_proveedorid integer, p_validacionmac integer, p_direccionmac character varying, p_recortemac integer, p_asignacionfa integer, p_multimodelo integer, p_cantidadcaja integer, p_smartcard integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE codigosap
    SET codigo = p_codigo,
        descripcion = p_descripcion,
        familiaid = p_familiaid,
        tipoid = p_tipoid,
        validacion = (p_validacion)::boolean,
        direccion = p_direccion,
        largos = p_largos,
        recorte = p_recorte,
        reingreso = p_reingreso,
        clasificacionid = p_clasificacionid,
        numserial = p_numserial,
        asignacionacc = (p_asignacion)::boolean,
        largosmac = p_largosmac,
        largosserial3 = p_largosserial3,
        largosserial4 = p_largosserial4,
        largosserial5 = p_largosserial5,
        tipoequipoid = p_tipoequipoid,
        areaid = p_areaid,
        modeloid = p_modeloid,
        proveedorid = p_proveedorid,
        validacionmac = (p_validacionmac)::boolean,
        direccionmac = p_direccionmac,
        recortemac = p_recortemac,
        asignacionfalla = (p_asignacionfa)::boolean,
        multimodelo = (p_multimodelo)::boolean,
        cantidadcaja = p_cantidadcaja,
        smartcard = (p_smartcard)::boolean
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatesapcodediagnostic(p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE diagnostico
    SET codigosapidanterior = (select codigosapid from ingreso where serial=p_serial)
    WHERE (id=(SELECT max(id) AS id FROM diagnostico
        WHERE serial=p_serial));
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatesapcodeentry(p_codigosapid integer, p_usuarioid integer, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET codigosapid = p_codigosapid,
        fechamovimiento = LOCALTIMESTAMP,
        usuarioidmovimiento = p_usuarioid
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatescrapall(p_serial character varying, p_usuarioid integer, p_novedad character varying, p_estadoid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        novedad = p_novedad,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateserialaccesory(p_serialnuevo character varying, p_serialanterior character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE accesorio
    SET serialempaque = p_serialnuevo
    WHERE (serialempaque=p_serialanterior);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateserialidandsapcodeidpqrs(p_serialid integer, p_serial character varying, p_codigosapid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
    v_fechaingreso timestamp(3);
BEGIN
    SELECT fecha INTO v_fechaingreso
            FROM ingreso
            WHERE id = p_serialid;
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    UPDATE pqrs
    SET serialid = p_serialid,
        codigosapid = p_codigosapid,
        x_studio_fecha_llegada_unidades = mssql.try_timestamp((v_fechaingreso)::text)
    WHERE (LOWER(name) = LOWER(p_serial) AND serialid IS NULL);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatesmartcard(p_estadofinalid integer, p_fallaid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE smartcard
    SET estadofinalid = p_estadofinalid,
        fallaid = p_fallaid,
        fecha = LOCALTIMESTAMP
    WHERE (id=(SELECT max(id) AS id FROM smartcard
                WHERE serial=p_serial));
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatesmartcardentry(p_smartcardid integer, p_smartcard character varying, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET smartcardid = p_smartcardid,
        smartcard = p_smartcard
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateallentry(p_estadoid integer, p_usuarioid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateboxentry(p_estadoid integer, p_usuarioid integer, p_cajaempaqueid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (cajaempaqueid=p_cajaempaqueid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateciclic(p_ciclicoid integer, p_estado character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ciclico
    SET estado = p_estado
    WHERE (id=p_ciclicoid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateentry(p_estadoid integer, p_palletid integer, p_usuarioid integer, p_fecha integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_fecha=1) THEN
        UPDATE ingreso
        SET estadoid = p_estadoid,
            palletid = p_palletid,
            usuarioidmovimiento = p_usuarioid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (serial=p_serial);
    ELSE
        UPDATE ingreso
        SET estadoid = p_estadoid,
            palletid = p_palletid,
            usuarioidmovimiento = p_usuarioid
        WHERE (serial=p_serial);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateentrynotusuario(p_estadoid integer, p_palletid integer, p_fecha integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_fecha=1) THEN
        UPDATE ingreso
        SET estadoid = p_estadoid,
            palletid = p_palletid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (serial=p_serial);
    ELSE
        UPDATE ingreso
        SET estadoid = p_estadoid,
            palletid = p_palletid
        WHERE (serial=p_serial);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateentrynotusuariodiagnosed(p_estadoid integer, p_palletid integer, p_fecha integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_fecha=1) THEN
        UPDATE ingreso
        SET estadoid = p_estadoid,
            palletid = p_palletid,
            usuarioidmovimiento = COALESCE((select d.usuarioid from diagnostico d
                    where p_serial= d.serial order by d.id DESC LIMIT 1),usuarioidmovimiento),
            fechamovimiento = LOCALTIMESTAMP
        WHERE (serial=p_serial);
    ELSE
        UPDATE ingreso
        SET estadoid = p_estadoid,
            palletid = p_palletid,
            usuarioidmovimiento = COALESCE((select d.usuarioid from diagnostico d
                    where p_serial= d.serial order by d.id DESC LIMIT 1),usuarioidmovimiento)
        WHERE (serial=p_serial);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateentrynotusuariorepaired(p_estadoid integer, p_palletid integer, p_fecha integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_fecha=1) THEN
        UPDATE ingreso
        SET estadoid = p_estadoid,
            palletid = p_palletid,
            usuarioidmovimiento = COALESCE((select r.tecnicoreparacionid from reparacion r
                    where p_serial= r.serial order by r.id DESC LIMIT 1),usuarioidmovimiento),
            fechamovimiento = LOCALTIMESTAMP
        WHERE (serial=p_serial);
    ELSE
        UPDATE ingreso
        SET estadoid = p_estadoid,
            palletid = p_palletid,
            usuarioidmovimiento = COALESCE((select r.tecnicoreparacionid from reparacion r
                    where p_serial= r.serial order by r.id DESC LIMIT 1),usuarioidmovimiento)
        WHERE (serial=p_serial);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateinventorypallet(p_palletid integer, p_estadoinventario integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE pallet
    SET estadoinventario = p_estadoinventario
    WHERE (id=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestateoneentry(p_estadoid integer, p_nivelid integer, p_usuarioid integer, p_fecha timestamp without time zone, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        nivelid = p_nivelid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestatepart(p_estadoid integer, p_palletid integer, p_usuarioid integer, p_parteid integer)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE parte
    SET estadoid = p_estadoid,
        palletid = p_palletid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (id=p_parteid);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestaterepairentry(p_estadoid integer, p_usuarioid integer, p_palletid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        usuarioidmovimiento = p_usuarioid,
        palletid = p_palletid
    WHERE (serial=p_serial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestatusallboxpacking(p_palletid integer, p_estadoid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE cajaempaque
    SET estadoid = p_estadoid
    WHERE (palletid=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatestatusboxpacking(p_cajaempaqueid integer, p_estadoid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE cajaempaque
    SET estadoid = p_estadoid
    WHERE (id=p_cajaempaqueid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatetipologyentry(p_palletid integer, p_tipologiaid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET tipologiaid = p_tipologiaid
    WHERE (palletid=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatetipologypallet(p_palletid integer, p_tipologiaid integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE pallet
    SET tipologiaid = p_tipologiaid
    WHERE (id=p_palletid);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updatetransportadora(p_nombre character varying, p_direccion character varying, p_telefono character varying, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE transportadora
    SET nombre = p_nombre,
        direccion = p_direccion,
        telefono = p_telefono
    WHERE (id=p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateunificationentry(p_estadoid integer, p_usuarioid integer, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE ingreso
    SET estadoid = p_estadoid,
        palletid = 0,
        palletidalmacen = 0,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP
    WHERE (serial=p_serial);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateunreadable(p_serialconfirmacion character varying, p_macconfirmacion character varying, p_consecutivoserial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ilegible
    SET serialconfirmacion = p_serialconfirmacion,
        macconfirmacion = p_macconfirmacion
    WHERE (consecutivoserial=p_consecutivoserial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateunreadableentry(p_serialconfirmacion character varying, p_macconfirmacion character varying, p_estadoid integer, p_palletid integer, p_usuarioid integer, p_consecutivoserial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    IF (p_palletid = 99 AND p_estadoid = 99) THEN
        UPDATE ingreso
        SET serial = p_serialconfirmacion,
            mac = p_macconfirmacion,
            usuarioidmovimiento = p_usuarioid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (serial = p_consecutivoserial);
    ELSE
        UPDATE ingreso
        SET serial = p_serialconfirmacion,
            mac = p_macconfirmacion,
            estadoid = p_estadoid,
            palletid = p_palletid,
            palletidingreso = p_palletid,
            usuarioidmovimiento = p_usuarioid,
            fechamovimiento = LOCALTIMESTAMP
        WHERE (serial = p_consecutivoserial);
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateunreadableentryadicional(p_serialconfirmacion character varying, p_macconfirmacion character varying, p_estadoid integer, p_palletid integer, p_usuarioid integer, p_consecutivoserial character varying, p_serial3confirmacion character varying, p_serial4confirmacion character varying, p_serial5confirmacion character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    UPDATE ingreso
    SET serial = p_serialconfirmacion,
        mac = p_macconfirmacion,
        estadoid = p_estadoid,
        palletid = p_palletid,
        palletidingreso = p_palletid,
        usuarioidmovimiento = p_usuarioid,
        fechamovimiento = LOCALTIMESTAMP,
        serial3 = p_serial3confirmacion,
        serial4 = p_serial4confirmacion,
        serial5 = p_serial5confirmacion
    WHERE (serial=p_consecutivoserial);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateunreadablerepair(p_serialreal character varying, p_macreal character varying, p_estadofinalid integer, p_tecnicoreparacionid integer, p_serial character varying, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE reparacion
    SET serial = p_serialreal,
        mac = p_macreal,
        estadofinalid = p_estadofinalid,
        tecnicoreparacionid = p_tecnicoreparacionid,
        fechareparacion = LOCALTIMESTAMP
    WHERE (id=(select max(r.id) AS id from reparacion r
        inner join maestro m on r.estadofinalid=m.id
        where r.serial=p_serial and m.codigo='ASIGNADO'));
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_updateworflow(p_moduloid integer, p_origenid integer, p_opcionid integer, p_tipologiaid integer, p_nivelid integer, p_id integer, INOUT p_filas integer)
 RETURNS integer
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_rowcount bigint;
BEGIN
    UPDATE workflow
    SET moduloid = p_moduloid,
        origenid = p_origenid,
        opcionid = p_opcionid,
        tipologiaid = p_tipologiaid,
        nivelid = p_nivelid
    WHERE (id = p_id);
    GET DIAGNOSTICS v_rowcount = ROW_COUNT;
    p_filas := (v_rowcount);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_informcalidadsalv()
 RETURNS TABLE(serial text, mac text, codigosap text, "Familia" text, descripcion text, pallet text, caja text, estadofinal text, fallacosmetica text, fallafuncional text, usuario text, "FECHA" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text FROM (
    select c.serial,c.mac,cs.codigo AS codigosap,fa.codigo AS familia,cs.descripcion,p.numero AS pallet,ce.numero AS caja,ef.codigo AS estadofinal,fc.codigo AS fallacosmetica,ff.codigo AS fallafuncional,u.nombreusuario AS usuario,(c.fecha)::date AS fecha
 from calidad c inner join codigosap cs on c.codigosapid=cs.id
inner join pallet p on c.palletid=p.id
inner join cajaempaque ce on c.cajaempaqueid=ce.id
inner join maestro ef on c.estadofinalid=ef.id
left join maestro fc on c.fallacosmeticaid=fc.id
left join maestro ff on c.fallafuncionalid=ff.id
inner join maestro fa on cs.familiaid = fa.id
inner join general.usuario u on c.usuarioid=u.id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_informdespachosalv()
 RETURNS TABLE(serial text, mac text, orden text, "Familia" text, pallet text, caja text, codigosap text, descripcion text, "Fecha" text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_status varchar(50);
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text FROM (
    select distinct d.serial,d.mac,d.pedidosap AS orden,fa.codigo AS familia,p.numero AS pallet,cd.numero AS caja,cs.codigo AS codigosap,cs.descripcion,(d.fecha)::date AS fecha,t.codigo AS tipologia
from despacho d
inner join pallet p on d.palletid=p.id
left join cajadespacho cd on d.cajadespachoid=cd.id
inner join codigosap cs on d.codigosapid=cs.id
inner join maestro fa on cs.familiaid = fa.id
inner join maestro t on p.tipologiaid=t.id
inner join general.estado e on d.estadoid=e.id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, NULL::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text FROM (
    select distinct i.serial,i.mac,'' AS orden,p.numero AS pallet,cd.numero AS caja,cs.codigo AS codigosap,cs.descripcion,i.fechamovimiento AS fecha,t.codigo AS tipologia from ingreso i
inner join pallet p on i.palletid=p.id
left join cajadespacho cd on i.cajadespachoid=cd.id
inner join codigosap cs on i.codigosapid=cs.id
inner join maestro fa on cs.familiaid = fa.id
inner join maestro t on p.tipologiaid=t.id
inner join general.estado e on i.estadoid=e.id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_informdiagnossalv()
 RETURNS TABLE(serial text, mac text, codigosap text, descripcion text, falla text, "estadoFinal" text, fecha text, usuario text, "Familia" text, "Origen" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text FROM (
    select d.serial,d.mac,cs.codigo AS codigosap,cs.descripcion,f.codigo || '|'|| f.descripcion AS falla,e.codigo AS estadofinal,d.fecha,u.nombreusuario AS usuario, fa.codigo AS familia,ori.codigo AS origen
from diagnostico d
inner join codigosap cs on d.codigosapid=cs.id
inner join maestro f on d.fallaid=f.id
inner join maestro e on d.estadofinalid=e.id
inner join maestro fa on cs.familiaid = fa.id
inner join ingreso i on i.serial = d.serial
inner join maestro ori on i.origenid = ori.id
inner join general.usuario u on d.usuarioid=u.id
union
select d.serial,d.mac,cs.codigo AS codigosap,cs.descripcion,f.codigo || '|'|| f.descripcion AS falla,e.codigo AS estadofinal,d.fecha,u.nombreusuario AS usuario, fa.codigo AS familia,ori.codigo AS origen
from diagnostico d
inner join codigosap cs on d.codigosapid=cs.id
inner join maestro f on d.fallaid=f.id
inner join maestro e on d.estadofinalid=e.id
inner join maestro fa on cs.familiaid = fa.id
inner join despacho i on i.serial = d.serial
inner join maestro ori on i.origenid = ori.id
inner join general.usuario u on d.usuarioid=u.id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_informempaquesalv()
 RETURNS TABLE(serial text, mac text, codigosap text, descripcion text, pallet text, caja text, usuario text, "Familia" text, fecha text, nivel text, tipologia text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text FROM (
    select e.serial,e.mac,cs.codigo AS codigosap,cs.descripcion,p.numero AS pallet,ce.numero AS caja,u.nombreusuario AS usuario,fa.codigo AS familia,e.fecha,
n.codigo AS nivel,t.codigo AS tipologia from empaque e
inner join pallet p on e.palletid=p.id
inner join cajaempaque ce on e.cajaempaqueid=ce.id
inner join codigosap cs on e.codigosapid=cs.id
inner join maestro fa on cs.familiaid = fa.id
inner join general.usuario u on e.usuarioid=u.id
inner join maestro n on e.nivelid=n.id
inner join maestro t on p.tipologiaid=t.id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_informingresosalv()
 RETURNS TABLE("Fecha" text, "Serial" text, "Mac" text, "Tipologia" text, "CodigoSap" text, "Descripcion" text, "Pallet" text, "NombreUsuario" text, "Documento" text, "Guia" text, "TipoOrigen" text, "Origen" text, "Familia" text, estado text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text FROM (
    select * from(select distinct (i.fecha)::date AS fecha,i.serial,i.mac,t.codigo AS tipologia,cs.codigo AS codigosap,cs.descripcion,
p.numero AS pallet,u.nombreusuario,i.documento,i.guia,ti.codigo AS tipoorigen,o.codigo AS origen,fa.codigo AS familia,e.nombre AS estado
from wmswdtelefonicacolombiabog.ingreso i
INNER JOIN wmswdtelefonicacolombiabog.codigosap cs on i.codigosapid = cs.id
INNER JOIN wmswdtelefonicacolombiabog.pallet p on i.palletidingreso = p.id
INNER JOIN wmswdtelefonicacolombiabog.usuario u on i.usuarioid = u.id
INNER JOIN wmswdtelefonicacolombiabog.maestro t on i.tipologiaid = t.id
INNER JOIN wmswdtelefonicacolombiabog.maestro ti on i.tipoorigenid = ti.id
inner join maestro fa on cs.familiaid = fa.id
INNER JOIN wmswdtelefonicacolombiabog.maestro o on i.origenid = o.id
INNER JOIN wmswdtelefonicacolombiabog.estado e on i.estadoid = e.id
 union
select distinct (i.fechaingreso)::date AS fecha,i.serial,i.mac,t.codigo AS tipologia,cs.codigo AS codigosap,cs.descripcion,
p.numero AS pallet,u.nombreusuario,i.documento,i.guia,ti.codigo AS tipoorigen,o.codigo AS origen,fa.codigo AS familia,e.nombre AS estado
from wmswdtelefonicacolombiabog.despacho i
INNER JOIN wmswdtelefonicacolombiabog.codigosap cs on i.codigosapid = cs.id
INNER JOIN wmswdtelefonicacolombiabog.pallet p on i.palletidingreso = p.id
INNER JOIN wmswdtelefonicacolombiabog.usuario u on i.usuarioid = u.id
INNER JOIN wmswdtelefonicacolombiabog.maestro t on i.tipologiaid = t.id
INNER JOIN wmswdtelefonicacolombiabog.maestro ti on i.tipoorigenid = ti.id
inner join maestro fa on cs.familiaid = fa.id
INNER JOIN wmswdtelefonicacolombiabog.maestro o on i.origenid = o.id
INNER JOIN wmswdtelefonicacolombiabog.estado e on i.estadoid = e.id
)as ingreso
order by fecha
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_informlabreversesal()
 RETURNS TABLE("mes produccion" text, "fecha produccion" text, "codigo modelo" text, modelo text, origen text, serialid text, "serie principal" text, categoria text, nivelid text, "nivel reparacion" text, diagnostico text, tarifa text, proyecto text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    DROP TABLE IF EXISTS tmp_empaquesal;
    CREATE TEMP TABLE tmp_empaquesal AS select mssql.datepart('month', (e.fecha)::timestamp) AS "mes produccion",(e.fecha)::date AS "fecha produccion",cs.codigo AS "codigo modelo",cs.descripcion AS modelo,('')::varchar(50) AS origen,e.serialid,e.serial AS "serie principal",
    f.codigo AS categoria,e.nivelid,(concat('NIVEL ',(n.codigo)::integer-1))::varchar(20) AS "nivel reparacion",('')::varchar(50) AS diagnostico,
    COALESCE(t.precio,0) AS tarifa,'El SALVADOR' AS proyecto
    from wmswdtigosal.empaque e
    left join wmswdtigosal.maestro n on e.nivelid = n.id
    inner join wmswdtigosal.codigosap cs on e.codigosapid=cs.id
    inner join wmswdtigosal.maestro f on cs.familiaid=f.id
    left join wmswdtigosal.tarifa t on e.nivelid=t.nivelid;
    DROP TABLE IF EXISTS tmp_serializadoscrapsal;
    CREATE TEMP TABLE tmp_serializadoscrapsal AS select mssql.datepart('month', (r.fechareparacion)::timestamp) AS "mes produccion",(r.fechareparacion)::date AS "fecha produccion",cs.codigo AS "codigo modelo",cs.descripcion AS modelo,case when COALESCE(oi.codigo,'')='' then od.codigo else oi.codigo end AS origen,r.serialid,r.serial AS "serie principal",
    f.codigo AS categoria,case when COALESCE(i.nivelid, 0) = 0 then d.nivelid else i.nivelid end AS nivelid,'SCRAP' AS "nivel reparacion",ms.codigo || '|'|| ms.descripcion AS diagnostico,
    case when COALESCE(ti.precio,'0')='0' then td.precio else ti.precio end AS tarifa,'EL SALVADOR' AS proyecto
    from wmswdtigosal.reparacion r
    inner join wmswdtigosal.codigosap cs on r.codigosapid=cs.id
    inner join wmswdtigosal.maestro ms on r.motivoscrapid=ms.id
    inner join wmswdtigosal.maestro f on cs.familiaid=f.id
    left join wmswdtigosal.ingreso i on r.serialid=i.id
    left join wmswdtigosal.maestro oi on i.origenid=oi.id
    left join wmswdtigosal.tarifa ti on i.nivelid=ti.nivelid
    left join wmswdtigosal.despacho d on r.serialid=d.serialid
    left join wmswdtigosal.maestro od on d.origenid=od.id
    left join wmswdtigosal.tarifa td on d.nivelid=td.nivelid;
    UPDATE tmp_empaquesal AS e
    SET origen = COALESCE(
        (SELECT m.codigo FROM wmswdtigosal.maestro m WHERE m.id = i.origenid),
        (SELECT md.codigo FROM wmswdtigosal.despacho d JOIN wmswdtigosal.maestro md ON d.origenid = md.id
          WHERE e.serialid = d.serialid LIMIT 1))
    FROM wmswdtigosal.ingreso i
    WHERE (e.serialid = i.id);
    DROP TABLE IF EXISTS tmp_diagnosticosal;
    CREATE TEMP TABLE tmp_diagnosticosal AS select e."serie principal",MAX(fecha) AS fecha,fd.codigo,fd.codigo || '|'|| fd.descripcion AS falla from tmp_empaquesal e
    inner join wmswdtigosal.diagnostico d on e.serialid=d.serialid
    inner join wmswdtigosal.maestro fd on d.fallaid=fd.id
    where e."nivel reparacion" in('NIVEL 0','NIVEL 1') and d.fecha=(select max(fecha) from wmswdtigosal.diagnostico where serialid=e.serialid)
    group by e."serie principal",fd.codigo,fd.descripcion
    order by e."serie principal";
    UPDATE tmp_empaquesal AS e
    SET diagnostico = d.falla
    FROM tmp_diagnosticosal d
    WHERE (e."serie principal"=d."serie principal");
    DROP TABLE IF EXISTS tmp_reparacionsal;
    CREATE TEMP TABLE tmp_reparacionsal AS select e."serie principal",MAX(r.fechareparacion) AS fecha,fr.codigo || '|'|| fr.descripcion AS falla from tmp_empaquesal e
    inner join wmswdtigosal.reparacion r on e.serialid=r.serialid
    inner join wmswdtigosal.maestro fr on r.falla1id=fr.id
    where e."nivel reparacion" ='NIVEL 2' and r.fechareparacion=(select max(fechareparacion) from wmswdtigosal.reparacion where serialid=e.serialid)
    group by e."serie principal",fr.codigo,fr.descripcion
    order by e."serie principal";
    UPDATE tmp_empaquesal AS e
    SET diagnostico = r.falla
    FROM tmp_reparacionsal r
    WHERE (e."serie principal"=r."serie principal");
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text FROM (
    select * from tmp_empaquesal union all select * from tmp_serializadoscrapsal
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13);
    DROP TABLE IF EXISTS tmp_empaquesal;
    DROP TABLE IF EXISTS tmp_serializadoscrapsal;
    DROP TABLE IF EXISTS tmp_diagnosticosal;
    DROP TABLE IF EXISTS tmp_reparacionsal;
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_informreparacionsalv()
 RETURNS TABLE(serial text, mac text, "Origen" text, codigosap text, "Familia" text, descripcion text, "fallaDiagnostico" text, falla1 text, falla2 text, falla3 text, falla4 text, "estadoFinal" text, "motivoScrap" text, "tecnicoReparacion" text, "fechaReparacion" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text FROM (
    select r.serial,r.mac,ori.codigo AS origen,cs.codigo AS codigosap,fa.codigo AS familia,cs.descripcion,fd.codigo || '|'|| fd.descripcion AS falladiagnostico,f1.codigo || '|'|| f1.descripcion AS falla1,f2.codigo || '|'|| f2.descripcion AS falla2,f3.codigo || '|'|| f3.descripcion AS falla3,f4.codigo || '|'|| f4.descripcion AS falla4,e.codigo AS estadofinal,m.codigo AS motivoscrap,tr.nombreusuario AS tecnicoreparacion,r.fechareparacion from reparacion r
inner join codigosap cs on r.codigosapid=cs.id
inner join ingreso i on i.serial = r.serial
inner join maestro ori on i.origenid = ori.id
left join maestro fd on r.falladxid=fd.id
inner join maestro f1 on r.falla1id=f1.id
left join maestro f2 on r.falla2id=f2.id
left join maestro f3 on r.falla3id=f3.id
left join maestro f4 on r.falla4id=f4.id
inner join maestro e on r.estadofinalid=e.id
left join maestro m on r.motivoscrapid=m.id
inner join maestro fa on cs.familiaid = fa.id
inner join general.usuario tr on r.tecnicoreparacionid=tr.id
union
select r.serial,r.mac,ori.codigo AS origen,cs.codigo AS codigosap,fa.codigo AS familia,cs.descripcion,fd.codigo || '|'|| fd.descripcion AS falladiagnostico,f1.codigo || '|'|| f1.descripcion AS falla1,f2.codigo || '|'|| f2.descripcion AS falla2,f3.codigo || '|'|| f3.descripcion AS falla3,f4.codigo || '|'|| f4.descripcion AS falla4,e.codigo AS estadofinal,m.codigo AS motivoscrap,tr.nombreusuario AS tecnicoreparacion,r.fechareparacion from reparacion r
inner join codigosap cs on r.codigosapid=cs.id
inner join despacho i on i.serial = r.serial
inner join maestro ori on i.origenid = ori.id
left join maestro fd on r.falladxid=fd.id
inner join maestro f1 on r.falla1id=f1.id
left join maestro f2 on r.falla2id=f2.id
left join maestro f3 on r.falla3id=f3.id
left join maestro f4 on r.falla4id=f4.id
inner join maestro e on r.estadofinalid=e.id
left join maestro m on r.motivoscrapid=m.id
inner join maestro fa on cs.familiaid = fa.id
inner join general.usuario tr on r.tecnicoreparacionid=tr.id
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15);
END;
$function$
;

CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_informwipsalv()
 RETURNS TABLE("Serial" text, mac text, codigosap text, "Descripcion" text, familia text, tipologia text, localizacion text, pallet text, posicion text, nivel text, "fechaIngreso" text, "FechaMovimiento" text, "tipoOrigen" text, origen text, "origenPallet" text, "GarantiaFabricante" text, estado text, "TotalIngresos" text)
 LANGUAGE plpgsql
 SET search_path TO 'wmswdtelefonicacolombiabog', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    RETURN QUERY SELECT q.c1::text, q.c2::text, q.c3::text, q.c4::text, q.c5::text, q.c6::text, q.c7::text, q.c8::text, q.c9::text, q.c10::text, q.c11::text, q.c12::text, q.c13::text, q.c14::text, q.c15::text, q.c16::text, q.c17::text, q.c18::text FROM (
    select i.serial,i.mac,cs.codigo AS codigosap,cs.descripcion,
f.codigo AS familia,c.codigo AS tipologia,COALESCE(e.nombre,'') AS localizacion,
COALESCE(p.numero,'') AS pallet,COALESCE(po.numero,'') AS posicion,n.codigo AS nivel,
i.fecha AS fechaingreso,i.fechamovimiento,tor.codigo AS tipoorigen,
o.codigo AS origen,COALESCE(op.nombre,'') AS origenpallet,i.garantiafabricante,
t.codigo AS estado,COALESCE(totalingresos,0) AS totalingresos
from wmswdtelefonicacolombiabog.ingreso i
left join wmswdtelefonicacolombiabog.codigosap cs on i.codigosapid=cs.id
left join wmswdtelefonicacolombiabog.maestro f on cs.familiaid=f.id
left join wmswdtelefonicacolombiabog.maestro c on cs.clasificacionid=c.id
left join wmswdtelefonicacolombiabog.estado e on i.estadoid=e.id
left join wmswdtelefonicacolombiabog.pallet p on i.palletid=p.id
left join wmswdtelefonicacolombiabog.posicion po on p.posicionid=po.id
left join wmswdtelefonicacolombiabog.maestro n on i.nivelid=n.id
left join wmswdtelefonicacolombiabog.maestro tor on i.tipoorigenid=tor.id
left join wmswdtelefonicacolombiabog.maestro o on i.origenid=o.id
left join wmswdtelefonicacolombiabog.estado op on p.origenid=op.id
left join wmswdtelefonicacolombiabog.maestro t on i.tipologiaid=t.id
left join
(select serial,sum(cantidad) AS totalingresos from
(select serial,count(serial) AS cantidad from wmswdtelefonicacolombiabog.ingreso group by serial union all
select serial,count(serial) AS cantidad from wmswdtelefonicacolombiabog.despacho group by serial)as tabla group by serial)as tabla2 on i.serial=tabla2.serial
    ) AS q(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18);
END;
$function$
;
