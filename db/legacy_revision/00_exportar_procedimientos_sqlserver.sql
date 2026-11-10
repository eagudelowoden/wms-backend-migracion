-- EJECUTAR EN SQL SERVER (base WmsWdLegacyCostaRica), no en Postgres.
-- El archivo "procedures.sql" que se paso tenia las TABLAS, no los procedimientos. Con estas dos consultas se sacan.

-- 1) Lista de procedimientos / funciones / vistas con su fecha de ultima modificacion (para ver que cambio recientemente):
SELECT o.name, o.type_desc, o.create_date, o.modify_date
FROM sys.objects o
WHERE o.type IN ('P', 'FN', 'IF', 'TF', 'V') AND o.is_ms_shipped = 0
ORDER BY o.modify_date DESC, o.name;

-- 2) Definicion completa de cada uno (exportar el resultado a un archivo: en DBeaver, clic derecho en la columna
--    "definition" > Exportar, o copiar la columna). Para limitar a lo modificado desde el 1-oct-2026 agrega:
--      AND o.modify_date >= '2026-10-01'
SELECT o.name, o.type_desc, o.modify_date, m.definition
FROM sys.sql_modules m
JOIN sys.objects o ON o.object_id = m.object_id
WHERE o.type IN ('P', 'FN', 'IF', 'TF', 'V') AND o.is_ms_shipped = 0
ORDER BY o.name;
