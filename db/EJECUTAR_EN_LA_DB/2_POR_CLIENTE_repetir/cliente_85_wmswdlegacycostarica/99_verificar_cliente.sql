-- Verificacion del esquema del CLIENTE wmswdlegacycostarica (despues de correr 01 a 04). Cada fila debe decir OK.
SELECT item, CASE WHEN ok THEN 'OK' ELSE 'FALTA' END AS estado FROM (
    SELECT 'cosmetica.usuarioasignadoid' AS item, EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'wmswdlegacycostarica' AND table_name = 'cosmetica' AND column_name = 'usuarioasignadoid') AS ok
    UNION ALL SELECT 'calidad: personal, dni, voltaje, observaciones', (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'wmswdlegacycostarica' AND table_name = 'calidad' AND column_name IN ('personal','dni','voltaje','observaciones')) = 4
    UNION ALL SELECT 'despacho.modeloid', EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'wmswdlegacycostarica' AND table_name = 'despacho' AND column_name = 'modeloid')
    UNION ALL SELECT 'accesorio.novedad y usuarioidmovimiento', (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'wmswdlegacycostarica' AND table_name = 'accesorio' AND column_name IN ('novedad','usuarioidmovimiento')) = 2
    UNION ALL SELECT 'tabla depreciados', to_regclass('wmswdlegacycostarica.depreciados') IS NOT NULL
    UNION ALL SELECT 'autogestion: 4 tablas', (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'wmswdlegacycostarica' AND table_name IN ('autogestionlog','autogestionestadodestino','autogestionsolicitud','autogestionsolicitudtoken')) = 4
    UNION ALL SELECT 'autogestion: funciones (>= 15)', (SELECT COUNT(*) FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace WHERE n.nspname = 'wmswdlegacycostarica' AND p.proname LIKE '%autogestion%') >= 15
    UNION ALL SELECT 'pa_insertdispatch con modeloid (33 parametros)', EXISTS (SELECT 1 FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace WHERE n.nspname = 'wmswdlegacycostarica' AND p.proname = 'pa_insertdispatch' AND p.pronargs = 33)
    UNION ALL SELECT 'pa_insertcosmetica de 4 parametros', EXISTS (SELECT 1 FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace WHERE n.nspname = 'wmswdlegacycostarica' AND p.proname = 'pa_insertcosmetica' AND p.pronargs = 4)
    UNION ALL SELECT 'permisos de Despachado en general.permiso', (SELECT COUNT(*) FROM general.permiso WHERE nombre IN ('Opc_Despachado','Opc_Equipos','Opc_Accesorios')) >= 3
    UNION ALL SELECT 'calidad: 4 funciones (extras, auditoria in-situ, estado)', (SELECT COUNT(*) FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace WHERE n.nspname = 'wmswdlegacycostarica' AND p.proname IN ('pa_searchqualityentryextras','pa_searchauditoriainsituentry','pa_updatequalityextrasbyserial','pa_updateestadocalidadbyserial')) = 4
) t
ORDER BY ok, item;
