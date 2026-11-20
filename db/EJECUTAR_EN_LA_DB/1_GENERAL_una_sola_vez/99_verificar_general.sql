-- Verificacion del esquema GENERAL (despues de correr 01 a 04). Cada fila debe decir OK.
SELECT item, CASE WHEN ok THEN 'OK' ELSE 'FALTA' END AS estado FROM (
    SELECT 'general.col_length (funcion)' AS item, to_regproc('general.col_length') IS NOT NULL AS ok
    UNION ALL SELECT 'general.modulo 44,46,47,48', (SELECT COUNT(*) FROM general.modulo WHERE id IN (44,46,47,48)) = 4
    UNION ALL SELECT 'general.boletamovimiento y boletamovimientopallet', to_regclass('general.boletamovimiento') IS NOT NULL AND to_regclass('general.boletamovimientopallet') IS NOT NULL
    UNION ALL SELECT 'general.boletaconsecutivo', to_regclass('general.boletaconsecutivo') IS NOT NULL
    UNION ALL SELECT 'general.despachoreversa y despachoreversaacc', to_regclass('general.despachoreversa') IS NOT NULL AND to_regclass('general.despachoreversaacc') IS NOT NULL
    UNION ALL SELECT 'general.notificacioncorreo / envio / sonido', to_regclass('general.notificacioncorreo') IS NOT NULL AND to_regclass('general.notificacioncorreoenvio') IS NOT NULL AND to_regclass('general.notificacionsonido') IS NOT NULL
    UNION ALL SELECT 'general.documentoplantilla / prealertacierre / smartcardautomatica', to_regclass('general.documentoplantilla') IS NOT NULL AND to_regclass('general.prealertacierre') IS NOT NULL AND to_regclass('general.smartcardautomatica') IS NOT NULL
    UNION ALL SELECT 'general.codigosapregla / clienteconfig / basealmacen / variableetiqueta', to_regclass('general.codigosapregla') IS NOT NULL AND to_regclass('general.clienteconfig') IS NOT NULL AND to_regclass('general.basealmacen') IS NOT NULL AND to_regclass('general.variableetiqueta') IS NOT NULL
    UNION ALL SELECT 'general.autogestionaprobador', to_regclass('general.autogestionaprobador') IS NOT NULL
    UNION ALL SELECT 'interruptores nuevos en clientevalidaciontipo (14)', (SELECT COUNT(*) FROM general.clientevalidaciontipo WHERE codigo IN ('BOLETA_CONSECUTIVO','PREALERTA_CIERRE_MANUAL','PREALERTA_SOBRANTES','INSERT_AUTOMATICO_SMARTCARD','EMPAQUE_CODIGOSAP_PALLET','LIMPIEZA_CARACTERES_INGRESO','APP_PQRS_INGRESO','DIAGNOSTICO_REPORTE34','ETIQUETADO_FORECAST','SERIAL_DISTINTO_MAC','MODELOID_DESDE_CODIGOSAP','SCRAP_MULTIMODELO_ENVIO','MODO_DEPRECIACION_CUALQUIERA','MODO_DEPRECIACION_AMBAS')) = 14
) t
ORDER BY ok, item;
