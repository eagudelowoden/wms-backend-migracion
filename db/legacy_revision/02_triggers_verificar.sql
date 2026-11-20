-- Triggers de SQL Server (WmsWdLegacyCostaRica) contra los de PostgreSQL (esquema wmswdlegacycostarica).
-- Para cada uno: ¿existe la funcion?  ¿hay un trigger que la use sobre esa tabla?
WITH esperado(trigger_sqlserver, tabla, evento, funcion) AS (
    VALUES
        ('TGR_INSERT_CLASIFICACION', 'clasificacion', 'INSERT', 'tgr_fechacierre_fn'),
        ('TGR_INSERT_COSMETICA', 'cosmetica', 'INSERT', 'tgr_fechacierre_fn'),
        ('TGR_INSERT_DIAGNOSTICO', 'diagnostico', 'INSERT', 'tgr_fechacierre_fn'),
        ('TGR_INSERT_ENSAMBLE', 'ensamble', 'INSERT', 'tgr_fechacierre_fn'),
        ('TGR_INSERT_LIMPIEZA', 'limpieza', 'INSERT', 'tgr_fechacierre_fn'),
        ('TGR_INSERT_CALIDAD', 'calidad', 'INSERT', 'tgr_fechacierre_fn'),
        ('TGR_INSERT_EMPAQUE', 'empaque', 'INSERT OR UPDATE', 'tgr_fechacierre_fn'),
        ('TGR_INSERT_DESPACHO', 'despacho', 'INSERT', 'tgr_fechacierre_fn'),
        ('TGR_UPDATE_REPARACION', 'reparacion', 'UPDATE', 'tgr_fechacierre_fn'),
        ('Trg_Acc_DeleteMovement', 'accesorio', 'DELETE', 'trg_acc_deletemovement_fn'),
        ('Trg_Acc_InsertMovement', 'accesorio', 'INSERT', 'trg_acc_insertmovement_fn'),
        ('Trg_Acc_UpdateMovement', 'accesorio', 'UPDATE', 'trg_acc_updatemovement_fn'),
        ('tr_update_fechaEmpaque', 'accesorio', 'UPDATE', 'tr_update_fechaempaque_fn'),
        ('tr_update_fechaLimpieza', 'accesorio', 'UPDATE', 'tr_update_fechalimpieza_fn'),
        ('Trg_DeletePallet', 'pallet', 'DELETE', 'trg_deletepallet_fn'),
        ('Trg_PalletUpdateBatch', 'pallet', 'UPDATE', 'trg_palletupdatebatch_fn'),
        ('Trg_PalletUpdateTypology', 'pallet', 'UPDATE', 'trg_palletupdatetypology_fn'),
        ('Trg_UpdatePosition', 'pallet', 'UPDATE', 'trg_updateposition_fn'),
        ('TRG_UpdateStatePacking', 'ingreso', 'UPDATE', 'trg_updatestatepacking_fn'),
        ('Trg_DeleteMovement', 'ingreso', 'DELETE', 'trg_deletemovement_fn'),
        ('Trg_EntryUpdateBatch', 'ingreso', 'UPDATE', 'trg_entryupdatebatch_fn'),
        ('Trg_EntryUpdateTypology', 'ingreso', 'UPDATE', 'trg_entryupdatetypology_fn'),
        ('Trg_InsertMovement', 'ingreso', 'INSERT', 'trg_insertmovement_fn'),
        ('Trg_UpdateMovement', 'ingreso', 'UPDATE', 'trg_updatemovement_fn'),
        ('trg_update_palletIdAccesory', 'ingreso', 'UPDATE', 'trg_update_palletidaccesory_fn'),
        ('TR_Ingreso_PreventInconsistent', 'ingreso', 'UPDATE', 'tr_ingreso_preventinconsistent_fn'),
        ('TR_Ingreso_ValidarSerialVsMac', 'ingreso', 'INSERT', 'tr_ingreso_validarserialvsmac_fn'),
        ('trg_Clasificacion_Reparacion', 'ingreso', 'UPDATE', 'trg_clasificacion_reparacion_fn'),
        ('trg_DeleteNoExistePrealerta', 'ingreso', 'INSERT', 'trg_deletenoexisteprealerta_fn')
), existentes AS (
    SELECT lower(c.relname) AS tabla, lower(p.proname) AS funcion, t.tgname, t.tgenabled
    FROM pg_trigger t
    JOIN pg_class c ON c.oid = t.tgrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace AND n.nspname = 'wmswdlegacycostarica'
    JOIN pg_proc p ON p.oid = t.tgfoid
    WHERE NOT t.tgisinternal
)
SELECT e.trigger_sqlserver, e.tabla, e.evento, e.funcion,
       CASE WHEN NOT EXISTS (SELECT 1 FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace
                             WHERE n.nspname = 'wmswdlegacycostarica' AND p.proname = e.funcion) THEN 'FALTA LA FUNCION'
            WHEN NOT EXISTS (SELECT 1 FROM existentes x WHERE x.tabla = e.tabla AND x.funcion = e.funcion) THEN 'FALTA EL TRIGGER'
            WHEN EXISTS (SELECT 1 FROM existentes x WHERE x.tabla = e.tabla AND x.funcion = e.funcion AND x.tgenabled = 'D') THEN 'DESHABILITADO'
            ELSE 'OK' END AS estado
FROM esperado e
ORDER BY CASE WHEN e.funcion IS NULL THEN 0 ELSE 1 END, estado DESC, e.tabla, e.trigger_sqlserver;

-- Triggers que hay en Postgres y no estan en la lista anterior (informativo)
SELECT c.relname AS tabla, t.tgname AS trigger, p.proname AS funcion
FROM pg_trigger t
JOIN pg_class c ON c.oid = t.tgrelid
JOIN pg_namespace n ON n.oid = c.relnamespace AND n.nspname = 'wmswdlegacycostarica'
JOIN pg_proc p ON p.oid = t.tgfoid
WHERE NOT t.tgisinternal
  AND lower(p.proname) NOT IN (SELECT DISTINCT lower(x) FROM (VALUES ('tgr_fechacierre_fn'), ('tr_ingreso_preventinconsistent_fn'), ('tr_ingreso_validarserialvsmac_fn'), ('tr_update_fechaempaque_fn'), ('tr_update_fechalimpieza_fn'), ('trg_acc_deletemovement_fn'), ('trg_acc_insertmovement_fn'), ('trg_acc_updatemovement_fn'), ('trg_clasificacion_reparacion_fn'), ('trg_deletemovement_fn'), ('trg_deletenoexisteprealerta_fn'), ('trg_deletepallet_fn'), ('trg_entryupdatebatch_fn'), ('trg_entryupdatetypology_fn'), ('trg_insertmovement_fn'), ('trg_palletupdatebatch_fn'), ('trg_palletupdatetypology_fn'), ('trg_update_palletidaccesory_fn'), ('trg_updatemovement_fn'), ('trg_updateposition_fn'), ('trg_updatestatepacking_fn')) v(x))
ORDER BY 1, 2;
