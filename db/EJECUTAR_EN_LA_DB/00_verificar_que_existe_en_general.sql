-- EJECUTAR PRIMERO (solo lectura). Comprueba que en el esquema `general` de esta base existen las tablas y columnas
-- que usan los scripts y el backend. Cada fila debe decir OK; si sale FALTA, no corras los demas scripts y pasame el resultado.
-- (En esta base el esquema se llama `general`: no hay WmsWdGeneral ni dbo para estas tablas.)
SELECT e.tabla, e.columna,
       CASE WHEN NOT EXISTS (SELECT 1 FROM information_schema.tables t WHERE t.table_schema = 'general' AND lower(t.table_name) = e.tabla) THEN 'FALTA LA TABLA'
            WHEN e.columna = '' THEN 'OK'
            WHEN NOT EXISTS (SELECT 1 FROM information_schema.columns c WHERE c.table_schema = 'general' AND lower(c.table_name) = e.tabla AND lower(c.column_name) = e.columna) THEN 'FALTA LA COLUMNA'
            ELSE 'OK' END AS estado
FROM (VALUES
    ('estado', 'id'), ('estado', 'nombre'), ('estado', 'moduloid'),
    ('modulo', 'id'), ('modulo', 'seccionid'), ('modulo', 'nombre'), ('modulo', 'icono'), ('modulo', 'controlador'),
    ('seccion', 'id'), ('seccion', 'nombre'),
    ('usuario', 'id'), ('usuario', 'nombreusuario'), ('usuario', 'nombres'), ('usuario', 'apellidos'),
    ('cliente', 'id'), ('cliente', 'nombre'), ('cliente', 'recogidaon'),
    ('perfil', 'id'), ('perfil', 'clienteid'), ('perfil', 'nombre'),
    ('perfilmodulo', 'perfilid'), ('perfilmodulo', 'moduloid'), ('perfilmodulo', 'orden'),
    ('perfilpermiso', 'perfilid'), ('perfilpermiso', 'permisoid'),
    ('permiso', 'id'), ('permiso', 'moduloid'), ('permiso', 'nombre'), ('permiso', 'descripcion'),
    ('clientevalidacion', 'clienteid'), ('clientevalidacion', 'validaciontipoid'), ('clientevalidacion', 'activo'),
    ('clientevalidaciontipo', 'id'), ('clientevalidaciontipo', 'codigo'), ('clientevalidaciontipo', 'descripcion'), ('clientevalidaciontipo', 'activo'),
    ('controlcierresctes', 'bdcliente'), ('controlcierresctes', 'fechacierre'), ('controlcierresctes', 'proyecto'), ('controlcierresctes', 'difutc'), ('controlcierresctes', 'activo'),
    ('usuariocliente', 'id'), ('usuarioclienteperfil', 'perfilid')
) AS e(tabla, columna)
ORDER BY CASE WHEN estado = 'OK' THEN 1 ELSE 0 END, e.tabla, e.columna;

-- Tipo de las columnas id de modulo y de cliente (para saber si son identity o serial):
SELECT table_name, column_name, data_type, is_identity, column_default
FROM information_schema.columns
WHERE table_schema = 'general' AND ((table_name = 'modulo' AND column_name = 'id') OR (table_name = 'clientevalidaciontipo' AND column_name = 'id'));
