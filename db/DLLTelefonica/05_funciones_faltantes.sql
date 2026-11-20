-- Los 5 SP de Telefonica que NO existian como funcion en Postgres (convertidos a mano).
-- Los otros 440 SP ya estan en FuncionesPostgress_boolean.sql (ver LEEME.md, paso 5).
-- Convenciones del proyecto: columnas devueltas como text; parametros de fecha como varchar.
SET search_path TO wmswdtelefonicacolombiabog, public;

DROP FUNCTION IF EXISTS wmswdtelefonicacolombiabog.pa_getlistprealertoms();
CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_getlistprealertoms()
RETURNS TABLE (id text, nombre text, fecha text, estado text, usuarioid text, tipoorigenid text, origenid text, usuarionombre text)
LANGUAGE plpgsql AS $$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT p.id::text, p.nombre::text, p.fecha::text, p.estado::text, p.usuarioid::text,
           p.tipoorigenid::text, p.origenid::text, u.nombres::text
    FROM prealerta p
    LEFT JOIN usuariosys u ON u.id = p.usuarioid
    WHERE p.activo = true
    ORDER BY p.id DESC;
END;
$$;

DROP FUNCTION IF EXISTS wmswdtelefonicacolombiabog.pa_informedespachotlco2(varchar, varchar, varchar, varchar);
CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_informedespachotlco2(
    p_fechainicial varchar, p_fechafinal varchar, p_cliente varchar, p_pais varchar)
RETURNS TABLE (serial text, mac text, tipologia text, codigosap text, descripcion text, pallet text,
               nombreusuario text, orden text, familia text, tecnologia text,
               g0 text, g1 text, g2 text, g3 text, g4 text, g5 text)
LANGUAGE plpgsql AS $$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    WITH cte_tempg2 AS (
        SELECT m.serial, m.serialid, 'G2' AS nivel FROM movimiento m WHERE m.origenid = 22),
    cte_tempg3 AS (
        SELECT r.serial, r.serialid, 'G3' AS nivel FROM reparacion r),
    cte_tempg4 AS (
        SELECT m.serial, m.serialid, 'G4' AS nivel FROM movimiento m WHERE m.destinoid = 20),
    cte_tecnologia AS (
        SELECT cs.id, cs.codigo, cs.descripcion,
               CASE WHEN m.descripcion ILIKE '%BASEPORT%' THEN 'BP'
                    WHEN m.descripcion ILIKE '%DECO HD%' THEN 'DECO HD'
                    WHEN m.descripcion ILIKE '%IPTV%' OR m.descripcion ILIKE '%SKYWORTH%' THEN 'IPTV'
                    WHEN m.descripcion ILIKE '%HGU%' OR m.descripcion ILIKE '%WIFI 6%' THEN 'HGU'
                    WHEN m.descripcion ILIKE '%VDSL%' THEN 'VDSL'
                    ELSE 'OTROS' END AS tipoequipo,
               m.codigo AS familia
        FROM codigosap cs
        INNER JOIN maestro m ON m.id = cs.familiaid)
    SELECT DISTINCT e.serial::text, e.mac::text, tpg.codigo::text, t.codigo::text, t.descripcion::text, p.numero::text,
           (u.nombres || ' ' || u.apellidos)::text, e.pedidosap::text, t.familia::text, t.tipoequipo::text,
           CONCAT(t.tipoequipo, 'G0'), CONCAT(t.tipoequipo, 'G1'),
           CASE WHEN tpg2.nivel IS NOT NULL THEN CONCAT(t.tipoequipo, tpg2.nivel) END,
           CASE WHEN tpg3.nivel IS NOT NULL THEN CONCAT(t.tipoequipo, tpg3.nivel) END,
           CASE WHEN tpg4.nivel IS NOT NULL THEN CONCAT(t.tipoequipo, tpg4.nivel) END,
           CONCAT(t.tipoequipo, 'G5')
    FROM despacho e
    LEFT JOIN cte_tempg2 tpg2 ON tpg2.serialid = e.serialid
    LEFT JOIN cte_tempg3 tpg3 ON tpg3.serialid = e.serialid
    LEFT JOIN cte_tempg4 tpg4 ON tpg4.serialid = e.serialid
    INNER JOIN cte_tecnologia t ON t.id = e.codigosapid
    LEFT JOIN pallet p ON e.palletid = p.id
    LEFT JOIN usuario u ON e.usuarioid = u.id
    LEFT JOIN maestro tpg ON e.tipologiaid = tpg.id
    WHERE e.fecha::date BETWEEN CAST(p_fechainicial AS date) AND CAST(p_fechafinal AS date);
END;
$$;

DROP FUNCTION IF EXISTS wmswdtelefonicacolombiabog.pa_informewiptelefonica();
CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.pa_informewiptelefonica()
RETURNS TABLE (serial text, codigosap text, descripcion text, familia text, tipologia text, localizacion text,
               pallet text, posicion text, nivel text, fechaingreso text, fechamovimiento text, tipoorigen text,
               origen text, origenpallet text, garantiafabricante text, estado text, totalingresos text,
               area text, tipodeequipo text)
LANGUAGE plpgsql AS $$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT i.serial::text, cs.codigo::text, cs.descripcion::text, fa.codigo::text, c.codigo::text,
           COALESCE(e.nombre, '')::text, COALESCE(p.numero, '')::text, COALESCE(po.numero, '')::text, n.codigo::text,
           ((i.fecha - interval '6 hours')::date)::text,
           ((i.fechamovimiento - interval '6 hours')::date)::text,
           tor.codigo::text, o.codigo::text, COALESCE(op.nombre, '')::text,
           i.garantiafabricante::text, t.codigo::text,
           COALESCE(tabla2.totalingresos, 0)::text, ar.codigo::text, te.codigo::text
    FROM ingreso i
    LEFT JOIN codigosap cs ON i.codigosapid = cs.id
    LEFT JOIN maestro fa ON cs.familiaid = fa.id
    LEFT JOIN maestro te ON cs.tipoequipoid = te.id
    LEFT JOIN maestro ar ON cs.areaid = ar.id
    LEFT JOIN maestro c ON cs.clasificacionid = c.id
    LEFT JOIN estado e ON i.estadoid = e.id
    LEFT JOIN pallet p ON i.palletid = p.id
    LEFT JOIN posicion po ON p.posicionid = po.id
    LEFT JOIN maestro n ON i.nivelid = n.id
    LEFT JOIN maestro tor ON i.tipoorigenid = tor.id
    LEFT JOIN maestro o ON i.origenid = o.id
    LEFT JOIN estado op ON p.origenid = op.id
    LEFT JOIN maestro t ON i.tipologiaid = t.id
    LEFT JOIN (
        SELECT tabla.serial, SUM(tabla.cantidad) AS totalingresos
        FROM (SELECT ing.serial, COUNT(ing.serial) AS cantidad FROM ingreso ing GROUP BY ing.serial
              UNION ALL
              SELECT d.serial, COUNT(d.serial) FROM despacho d GROUP BY d.serial) AS tabla
        GROUP BY tabla.serial) AS tabla2 ON i.serial = tabla2.serial;
END;
$$;

-- Head count: usuarios distintos por proceso y dia. La variante "Tableros" cuenta registros en vez de usuarios distintos.
DROP FUNCTION IF EXISTS wmswdtelefonicacolombiabog.sp_reporteheadcounttelefonica();
CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_reporteheadcounttelefonica()
RETURNS TABLE (fecha text, cliente text, detalle text, ingreso text, despacho text, reparacion text,
               clasificacion text, calidad text, diagnostico text, cosmetica text, empaque text,
               inventario text, ensamble text, etiquetado text)
LANGUAGE plpgsql AS $$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    WITH datosusuarios AS (
        SELECT CAST(x.fecha AS date) AS fecha, x.usuarioid, x.proceso, 'Telefonica' AS cliente FROM (
            SELECT fecha, usuarioid, 'Ingreso' AS proceso FROM ingreso WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Despacho' FROM despacho WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fechareparacion, usuarioid, 'Reparacion' FROM reparacion WHERE fechareparacion >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Clasificacion' FROM clasificacion WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Calidad' FROM calidad WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Diagnostico' FROM diagnostico WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Cosmetica' FROM cosmetica WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Empaque' FROM empaque WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Inventario' FROM inventario WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Ensamble' FROM ensamble WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Etiquetado' FROM etiquetado WHERE fecha >= '2024-01-01'
        ) x)
    SELECT d.fecha::text, d.cliente::text, 'HeadCount'::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Ingreso' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Despacho' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Reparacion' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Clasificacion' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Calidad' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Diagnostico' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Cosmetica' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Empaque' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Inventario' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Ensamble' THEN d.usuarioid END)::text,
           COUNT(DISTINCT CASE WHEN d.proceso = 'Etiquetado' THEN d.usuarioid END)::text
    FROM datosusuarios d
    GROUP BY d.fecha, d.cliente
    ORDER BY d.fecha, d.cliente;
END;
$$;

DROP FUNCTION IF EXISTS wmswdtelefonicacolombiabog.sp_reporteheadcounttelefonicatableros();
CREATE OR REPLACE FUNCTION wmswdtelefonicacolombiabog.sp_reporteheadcounttelefonicatableros()
RETURNS TABLE (fecha text, cliente text, detalle text, ingreso text, despacho text, reparacion text,
               clasificacion text, calidad text, diagnostico text, cosmetica text, empaque text,
               inventario text, ensamble text, etiquetado text)
LANGUAGE plpgsql AS $$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    WITH datosusuarios AS (
        SELECT CAST(x.fecha AS date) AS fecha, x.usuarioid, x.proceso, 'Telefonica' AS cliente FROM (
            SELECT fecha, usuarioid, 'Ingreso' AS proceso FROM ingreso WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Despacho' FROM despacho WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fechareparacion, usuarioid, 'Reparacion' FROM reparacion WHERE fechareparacion >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Clasificacion' FROM clasificacion WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Calidad' FROM calidad WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Diagnostico' FROM diagnostico WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Cosmetica' FROM cosmetica WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Empaque' FROM empaque WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Inventario' FROM inventario WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Ensamble' FROM ensamble WHERE fecha >= '2024-01-01'
            UNION ALL SELECT fecha, usuarioid, 'Etiquetado' FROM etiquetado WHERE fecha >= '2024-01-01'
        ) x)
    SELECT d.fecha::text, d.cliente::text, 'HeadCount'::text,
           COUNT(CASE WHEN d.proceso = 'Ingreso' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Despacho' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Reparacion' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Clasificacion' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Calidad' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Diagnostico' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Cosmetica' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Empaque' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Inventario' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Ensamble' THEN d.usuarioid END)::text,
           COUNT(CASE WHEN d.proceso = 'Etiquetado' THEN d.usuarioid END)::text
    FROM datosusuarios d
    GROUP BY d.fecha, d.cliente
    ORDER BY d.fecha, d.cliente;
END;
$$;
