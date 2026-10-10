-- dbo.fn_GetDescripcionMovimiento de SQL Server convertida a PostgreSQL.
-- La usa el trigger trg_updatemovement_fn para armar la descripcion del historial en 'movimiento'.
-- Cada cliente (esquema) necesita su propia copia: cambiar 'wmswdlegacycostarica' por el esquema del cliente.
-- Los parametros son text para aceptar varchar y citext (usuario.nombreusuario es citext).
-- Semantica igual a SQL Server: CONCAT ignora NULL y una comparacion con NULL cae en el ELSE.

CREATE OR REPLACE FUNCTION wmswdlegacycostarica.fn_getdescripcionmovimiento(
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
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
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
