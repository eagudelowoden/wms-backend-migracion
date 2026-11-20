-- Triggers de SQL Server que no tenian funcion equivalente en Postgres (convertidos).
SET search_path TO wmswdlegacycostarica, public;

-- AFTER UPDATE ON ingreso: bloquea el paso 66 -> 38 si el serial sigue empacado.
-- pa_updatepackingallentry marca la sesion (wms.sourcesp) para quedar exento, igual que SESSION_CONTEXT('SourceSP').
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.tr_ingreso_preventinconsistent_fn()
RETURNS trigger LANGUAGE plpgsql
SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
BEGIN
    IF COALESCE(current_setting('wms.sourcesp', true), '') = 'pa_UpdatePackingAllEntry' THEN
        RETURN NULL;
    END IF;
    IF OLD.estadoid = 66 AND NEW.estadoid = 38 THEN
        IF NEW.palletid > 0 OR NEW.cajaempaqueid > 0 OR NEW.palletidempaque > 0
           OR EXISTS (SELECT 1 FROM empaque e WHERE e.serialid = NEW.id) THEN
            RAISE EXCEPTION 'Error de red. Por favor verifique la cantidad empacada.';
        END IF;
    END IF;
    RETURN NULL;
END;
$function$;

-- AFTER INSERT ON ingreso: el serial nuevo no puede existir como MAC de otra fila, ni la MAC como serial.
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.tr_ingreso_validarserialvsmac_fn()
RETURNS trigger LANGUAGE plpgsql
SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
DECLARE v_otro varchar;
BEGIN
    SELECT i.mac INTO v_otro FROM ingreso i WHERE i.mac = NEW.serial AND i.id <> NEW.id LIMIT 1;
    IF FOUND THEN
        RAISE EXCEPTION 'El Serial ya existe como MAC en: %', v_otro;
    END IF;
    SELECT i.serial INTO v_otro FROM ingreso i WHERE i.serial = NEW.mac AND i.id <> NEW.id LIMIT 1;
    IF FOUND THEN
        RAISE EXCEPTION 'La MAC ya existe con el serial: %', v_otro;
    END IF;
    RETURN NULL;
END;
$function$;

-- AFTER UPDATE ON ingreso: al pasar de estado 112 a 14 registra un Diagnostico "Envio a reparacion".
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.trg_clasificacion_reparacion_fn()
RETURNS trigger LANGUAGE plpgsql
SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
DECLARE v_estadofinal integer; v_falla integer;
BEGIN
    IF NOT (OLD.estadoid = 112 AND NEW.estadoid = 14) THEN
        RETURN NULL;
    END IF;
    SELECT m.id INTO v_estadofinal FROM maestro m JOIN tipomaestro td ON td.id = m.tipomaestroid
     WHERE UPPER(m.descripcion) = 'MAL ESTADO' AND UPPER(td.nombre) = 'ESTADOS DIAGNOSTICO' ORDER BY m.id LIMIT 1;
    SELECT m.id INTO v_falla FROM maestro m JOIN tipomaestro td ON td.id = m.tipomaestroid
     WHERE UPPER(m.descripcion) = 'OXIDO' AND UPPER(td.nombre) = 'FALLAS FUNCIONALES' ORDER BY m.id LIMIT 1;  -- Paraguay: CORROSION
    INSERT INTO diagnostico (serialid, serial, mac, serial3, codigosapid, codigosapidanterior, estadofinalid, fallaid,
                             usuarioid, observaciones, fecha, estadocalidadid)
    VALUES (NEW.id, NEW.serial, NEW.mac, NULL, NEW.codigosapid, OLD.codigosapid, v_estadofinal, v_falla,
            NEW.usuarioidmovimiento, 'Envio a reparación', LOCALTIMESTAMP, NULL);
    RETURN NULL;
END;
$function$;

-- AFTER INSERT ON ingreso: borra la fila si llega con novedad 'NO EXISTE PREALERTA'.
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.trg_deletenoexisteprealerta_fn()
RETURNS trigger LANGUAGE plpgsql
SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
BEGIN
    IF UPPER(COALESCE(NEW.novedad, '')) = 'NO EXISTE PREALERTA' THEN
        DELETE FROM ingreso WHERE id = NEW.id;
    END IF;
    RETURN NULL;
END;
$function$;

-- pa_updatepackingallentry: marca la sesion para que tr_ingreso_preventinconsistent no la bloquee.
CREATE OR REPLACE FUNCTION wmswdlegacycostarica.pa_updatepackingallentry(p_estadoid integer, p_usuarioid integer, p_serial character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'wmswdlegacycostarica', 'mssql', 'public'
AS $function$
#variable_conflict use_column
BEGIN
    PERFORM set_config('wms.sourcesp', 'pa_UpdatePackingAllEntry', true);  -- solo dentro de esta transaccion
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
    PERFORM set_config('wms.sourcesp', '', true);
END;
$function$;

-- Asociar los triggers que falten (solo crea los que no existan; no toca los que ya estan).
-- Antes corre 02_triggers_verificar.sql para ver el estado.
DO $$
BEGIN
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'clasificacion' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_insert_clasificacion BEFORE INSERT ON wmswdlegacycostarica.clasificacion  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''REMANUFACTURA'', ''fecha'')';
        RAISE NOTICE 'Trigger creado: tgr_insert_clasificacion en clasificacion';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'cosmetica' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_insert_cosmetica BEFORE INSERT ON wmswdlegacycostarica.cosmetica  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''REMANUFACTURA'', ''fecha'')';
        RAISE NOTICE 'Trigger creado: tgr_insert_cosmetica en cosmetica';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'diagnostico' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_insert_diagnostico BEFORE INSERT ON wmswdlegacycostarica.diagnostico  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''REMANUFACTURA'', ''fecha'')';
        RAISE NOTICE 'Trigger creado: tgr_insert_diagnostico en diagnostico';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ensamble' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_insert_ensamble BEFORE INSERT ON wmswdlegacycostarica.ensamble  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''REMANUFACTURA'', ''fecha'')';
        RAISE NOTICE 'Trigger creado: tgr_insert_ensamble en ensamble';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'limpieza' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_insert_limpieza BEFORE INSERT ON wmswdlegacycostarica.limpieza  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''REMANUFACTURA'', ''fecha'')';
        RAISE NOTICE 'Trigger creado: tgr_insert_limpieza en limpieza';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'calidad' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_insert_calidad BEFORE INSERT ON wmswdlegacycostarica.calidad  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''REMANUFACTURA'', ''fecha'')';
        RAISE NOTICE 'Trigger creado: tgr_insert_calidad en calidad';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'empaque' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_insert_empaque BEFORE INSERT OR UPDATE ON wmswdlegacycostarica.empaque  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''REMANUFACTURA'', ''fecha'')';
        RAISE NOTICE 'Trigger creado: tgr_insert_empaque en empaque';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'despacho' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_insert_despacho BEFORE INSERT ON wmswdlegacycostarica.despacho  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''LOGISTICA'', ''fecha'')';
        RAISE NOTICE 'Trigger creado: tgr_insert_despacho en despacho';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tgr_fechacierre_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'reparacion' AND p.proname = 'tgr_fechacierre_fn') THEN
        EXECUTE 'CREATE TRIGGER tgr_update_reparacion BEFORE UPDATE ON wmswdlegacycostarica.reparacion  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tgr_fechacierre_fn(''REMANUFACTURA'', ''fechareparacion'')';
        RAISE NOTICE 'Trigger creado: tgr_update_reparacion en reparacion';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_acc_deletemovement_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'accesorio' AND p.proname = 'trg_acc_deletemovement_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_acc_deletemovement AFTER DELETE ON wmswdlegacycostarica.accesorio REFERENCING OLD TABLE AS deleted FOR EACH STATEMENT EXECUTE FUNCTION wmswdlegacycostarica.trg_acc_deletemovement_fn()';
        RAISE NOTICE 'Trigger creado: trg_acc_deletemovement en accesorio';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_acc_insertmovement_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'accesorio' AND p.proname = 'trg_acc_insertmovement_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_acc_insertmovement AFTER INSERT ON wmswdlegacycostarica.accesorio REFERENCING NEW TABLE AS inserted FOR EACH STATEMENT EXECUTE FUNCTION wmswdlegacycostarica.trg_acc_insertmovement_fn()';
        RAISE NOTICE 'Trigger creado: trg_acc_insertmovement en accesorio';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_acc_updatemovement_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'accesorio' AND p.proname = 'trg_acc_updatemovement_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_acc_updatemovement AFTER UPDATE ON wmswdlegacycostarica.accesorio REFERENCING OLD TABLE AS deleted NEW TABLE AS inserted FOR EACH STATEMENT EXECUTE FUNCTION wmswdlegacycostarica.trg_acc_updatemovement_fn()';
        RAISE NOTICE 'Trigger creado: trg_acc_updatemovement en accesorio';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tr_update_fechaempaque_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'accesorio' AND p.proname = 'tr_update_fechaempaque_fn') THEN
        EXECUTE 'CREATE TRIGGER tr_update_fechaempaque AFTER UPDATE ON wmswdlegacycostarica.accesorio  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tr_update_fechaempaque_fn()';
        RAISE NOTICE 'Trigger creado: tr_update_fechaempaque en accesorio';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tr_update_fechalimpieza_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'accesorio' AND p.proname = 'tr_update_fechalimpieza_fn') THEN
        EXECUTE 'CREATE TRIGGER tr_update_fechalimpieza AFTER UPDATE ON wmswdlegacycostarica.accesorio  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tr_update_fechalimpieza_fn()';
        RAISE NOTICE 'Trigger creado: tr_update_fechalimpieza en accesorio';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_deletepallet_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'pallet' AND p.proname = 'trg_deletepallet_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_deletepallet AFTER DELETE ON wmswdlegacycostarica.pallet REFERENCING OLD TABLE AS deleted FOR EACH STATEMENT EXECUTE FUNCTION wmswdlegacycostarica.trg_deletepallet_fn()';
        RAISE NOTICE 'Trigger creado: trg_deletepallet en pallet';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_palletupdatebatch_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'pallet' AND p.proname = 'trg_palletupdatebatch_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_palletupdatebatch AFTER UPDATE ON wmswdlegacycostarica.pallet  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_palletupdatebatch_fn()';
        RAISE NOTICE 'Trigger creado: trg_palletupdatebatch en pallet';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_palletupdatetypology_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'pallet' AND p.proname = 'trg_palletupdatetypology_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_palletupdatetypology AFTER UPDATE ON wmswdlegacycostarica.pallet  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_palletupdatetypology_fn()';
        RAISE NOTICE 'Trigger creado: trg_palletupdatetypology en pallet';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_updateposition_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'pallet' AND p.proname = 'trg_updateposition_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_updateposition AFTER UPDATE ON wmswdlegacycostarica.pallet  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_updateposition_fn()';
        RAISE NOTICE 'Trigger creado: trg_updateposition en pallet';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_updatestatepacking_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_updatestatepacking_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_updatestatepacking AFTER UPDATE ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_updatestatepacking_fn()';
        RAISE NOTICE 'Trigger creado: trg_updatestatepacking en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_deletemovement_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_deletemovement_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_deletemovement AFTER DELETE ON wmswdlegacycostarica.ingreso REFERENCING OLD TABLE AS deleted FOR EACH STATEMENT EXECUTE FUNCTION wmswdlegacycostarica.trg_deletemovement_fn()';
        RAISE NOTICE 'Trigger creado: trg_deletemovement en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_entryupdatebatch_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_entryupdatebatch_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_entryupdatebatch AFTER UPDATE ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_entryupdatebatch_fn()';
        RAISE NOTICE 'Trigger creado: trg_entryupdatebatch en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_entryupdatetypology_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_entryupdatetypology_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_entryupdatetypology AFTER UPDATE ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_entryupdatetypology_fn()';
        RAISE NOTICE 'Trigger creado: trg_entryupdatetypology en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_insertmovement_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_insertmovement_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_insertmovement AFTER INSERT ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_insertmovement_fn()';
        RAISE NOTICE 'Trigger creado: trg_insertmovement en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_updatemovement_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_updatemovement_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_updatemovement AFTER UPDATE ON wmswdlegacycostarica.ingreso REFERENCING OLD TABLE AS deleted NEW TABLE AS inserted FOR EACH STATEMENT EXECUTE FUNCTION wmswdlegacycostarica.trg_updatemovement_fn()';
        RAISE NOTICE 'Trigger creado: trg_updatemovement en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_update_palletidaccesory_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_update_palletidaccesory_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_update_palletidaccesory AFTER UPDATE ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_update_palletidaccesory_fn()';
        RAISE NOTICE 'Trigger creado: trg_update_palletidaccesory en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tr_ingreso_preventinconsistent_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'tr_ingreso_preventinconsistent_fn') THEN
        EXECUTE 'CREATE TRIGGER tr_ingreso_preventinconsistent AFTER UPDATE ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tr_ingreso_preventinconsistent_fn()';
        RAISE NOTICE 'Trigger creado: tr_ingreso_preventinconsistent en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.tr_ingreso_validarserialvsmac_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'tr_ingreso_validarserialvsmac_fn') THEN
        EXECUTE 'CREATE TRIGGER tr_ingreso_validarserialvsmac AFTER INSERT ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.tr_ingreso_validarserialvsmac_fn()';
        RAISE NOTICE 'Trigger creado: tr_ingreso_validarserialvsmac en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_clasificacion_reparacion_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_clasificacion_reparacion_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_clasificacion_reparacion AFTER UPDATE ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_clasificacion_reparacion_fn()';
        RAISE NOTICE 'Trigger creado: trg_clasificacion_reparacion en ingreso';
    END IF;
    IF to_regprocedure('wmswdlegacycostarica.trg_deletenoexisteprealerta_fn()') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_trigger t JOIN pg_class c ON c.oid = t.tgrelid JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal AND n.nspname = 'wmswdlegacycostarica' AND c.relname = 'ingreso' AND p.proname = 'trg_deletenoexisteprealerta_fn') THEN
        EXECUTE 'CREATE TRIGGER trg_deletenoexisteprealerta AFTER INSERT ON wmswdlegacycostarica.ingreso  FOR EACH ROW EXECUTE FUNCTION wmswdlegacycostarica.trg_deletenoexisteprealerta_fn()';
        RAISE NOTICE 'Trigger creado: trg_deletenoexisteprealerta en ingreso';
    END IF;
END $$;
