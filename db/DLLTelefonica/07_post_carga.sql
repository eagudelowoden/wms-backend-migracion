-- Ejecutar DESPUES de cargar los datos: con identity BY DEFAULT, los INSERT con Id explicito no avanzan la secuencia.
SET search_path TO wmswdtelefonicacolombiabog, public;

DO $$
DECLARE r record;
BEGIN
    FOR r IN
        SELECT c.table_name, c.column_name
        FROM information_schema.columns c
        WHERE c.table_schema = 'wmswdtelefonicacolombiabog' AND c.is_identity = 'YES'
    LOOP
        EXECUTE format(
            'SELECT setval(pg_get_serial_sequence(%L, %L), COALESCE((SELECT MAX(%I) FROM wmswdtelefonicacolombiabog.%I), 0) + 1, false)',
            'wmswdtelefonicacolombiabog.' || r.table_name, r.column_name, r.column_name, r.table_name);
    END LOOP;
END $$;
