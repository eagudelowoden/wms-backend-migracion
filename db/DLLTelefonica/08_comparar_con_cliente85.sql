-- Diferencias de columnas/tipos entre Telefonica y el cliente 85 (el esquema que ya funciona con el backend).
-- Ejecutar con ambos esquemas creados. Cada fila es algo que puede romper una funcion o una entidad.
WITH t AS (SELECT table_name, column_name, data_type FROM information_schema.columns WHERE table_schema = 'wmswdtelefonicacolombiabog'),
     c AS (SELECT table_name, column_name, data_type FROM information_schema.columns WHERE table_schema = 'wmswdlegacycostarica')
SELECT COALESCE(t.table_name, c.table_name) AS tabla, COALESCE(t.column_name, c.column_name) AS columna,
       t.data_type AS tipo_telefonica, c.data_type AS tipo_cliente85,
       CASE WHEN c.column_name IS NULL THEN 'solo en Telefonica'
            WHEN t.column_name IS NULL THEN 'solo en cliente 85'
            ELSE 'tipo distinto' END AS diferencia
FROM t FULL OUTER JOIN c ON c.table_name = t.table_name AND c.column_name = t.column_name
WHERE t.column_name IS NULL OR c.column_name IS NULL OR t.data_type <> c.data_type
ORDER BY 1, 2;
