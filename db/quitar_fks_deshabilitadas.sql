-- Quita en PostgreSQL las llaves foráneas que en SQL Server estaban DESHABILITADAS (is_disabled = 1).
-- En SQL Server no validaban nada (el sistema guarda 0 como "sin valor"); la migración las creó activas
-- y por eso fallan los INSERT/UPDATE con 0 (fk_pallet_posicion, fk_ingreso_cajadespacho, ...).
--
-- Las llaves que en SQL Server estaban ACTIVAS (con o sin confianza) NO se tocan.
-- Para otro cliente, agregar su esquema a la lista 'esquemas'.
-- Es idempotente: se puede ejecutar varias veces.

DO $$
DECLARE
    esquemas text[] := ARRAY['wmswdlegacycostarica'];
    llaves   text[] := ARRAY[
        'fk_accesorio_pallet',
        'fk_calidad_maestrocosmetica', 'fk_calidad_maestroestado', 'fk_calidad_maestrofuncional',
        'fk_despacho_cajadespacho', 'fk_despacho_cajaingreso', 'fk_despacho_prealerta',
        'fk_diagnostico_maestroestadofinal', 'fk_diagnostico_maestrofalla',
        'fk_empaque_maestronivel',
        'fk_ilegible_ingreso',
        'fk_ingreso_cajadespacho', 'fk_ingreso_cajaempaque', 'fk_ingreso_cajaingreso', 'fk_ingreso_codigosap',
        'fk_ingreso_maestroobservacionesingreso', 'fk_ingreso_maestroorigen', 'fk_ingreso_maestrotipologia',
        'fk_ingreso_maestrotipoorigen', 'fk_ingreso_pallet', 'fk_ingreso_palletalmacen',
        'fk_ingreso_palletempaque', 'fk_ingreso_palletingreso', 'fk_ingreso_prealerta',
        'fk_pallet_posicion',
        'fk_posicion_codigosap',
        'fk_presupuesto_nivel', 'fk_presupuesto_segmento',
        'fk_reparacion_maestroestado', 'fk_reparacion_maestrofallafuncional1', 'fk_reparacion_maestrofallafuncional2',
        'fk_reparacion_maestrofallafuncional3', 'fk_reparacion_maestrofallafuncional4',
        'fk_reparacion_maestrofallafuncionaldx', 'fk_reparacion_maestromotivoscrap',
        'fk_smartcard_maestroestadofinal', 'fk_smartcard_maestrofalla'
    ];
    r record;
    n int := 0;
BEGIN
    FOR r IN
        SELECT ns.nspname, c.relname, con.conname
        FROM pg_constraint con
        JOIN pg_class c      ON c.oid = con.conrelid
        JOIN pg_namespace ns ON ns.oid = c.relnamespace
        WHERE con.contype = 'f'
          AND ns.nspname = ANY (esquemas)
          AND lower(con.conname) = ANY (llaves)
    LOOP
        EXECUTE format('ALTER TABLE %I.%I DROP CONSTRAINT %I', r.nspname, r.relname, r.conname);
        RAISE NOTICE 'Quitada: %.% -> %', r.nspname, r.relname, r.conname;
        n := n + 1;
    END LOOP;
    RAISE NOTICE 'Llaves quitadas: % de % esperadas', n, array_length(llaves, 1);
END $$;
