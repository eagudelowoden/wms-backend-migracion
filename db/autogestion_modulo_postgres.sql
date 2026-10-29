/* ============================================================================
   Autogestion como modulo asignable desde Gestion de Perfiles.
   Se ejecuta UNA sola vez, en el esquema general (compartido por los clientes).
   Id fijo = 46 (debe coincidir con MenuModuloMapping.java). Equivale a sql/autogestion_modulo.sql.
   ============================================================================ */
SET search_path TO general, public;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM modulo WHERE id = 46 OR (seccionid = 3 AND nombre = 'Autogestión')) THEN
        -- OVERRIDING SYSTEM VALUE permite fijar el id aunque la columna sea identity
        INSERT INTO modulo (id, seccionid, nombre, icono, controlador)
        OVERRIDING SYSTEM VALUE
        VALUES (46, 3, 'Autogestión', '', 'autogestion');
    END IF;
END $$;

-- Si modulo.id es identity/serial, deja la secuencia por encima del 46 para que los proximos insert no choquen
DO $$
DECLARE v_seq text;
BEGIN
    v_seq := pg_get_serial_sequence('general.modulo', 'id');
    IF v_seq IS NOT NULL THEN
        PERFORM setval(v_seq, GREATEST((SELECT MAX(id) FROM modulo), 46));
    END IF;
END $$;

SELECT * FROM modulo WHERE id = 46;
