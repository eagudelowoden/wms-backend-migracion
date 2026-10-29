-- OPCIONAL pero RECOMENDADO. En Telefonica estas columnas son INT en SQL Server (no bit), pero el backend
-- las mapea como Boolean (asi estan en el cliente 85). Con INT, guardar/leer CodigoSap o Accesorio falla
-- ("column is of type integer but expression is of type boolean").
-- Se puede ejecutar antes o despues de cargar datos: el USING convierte 0/NULL -> false y cualquier otro -> true.
SET search_path TO wmswdtelefonicacolombiabog, public;

ALTER TABLE codigosap ALTER COLUMN validacion      DROP DEFAULT, ALTER COLUMN validacion      TYPE boolean USING (COALESCE(validacion, 0)      <> 0);
ALTER TABLE codigosap ALTER COLUMN asignacionacc   DROP DEFAULT, ALTER COLUMN asignacionacc   TYPE boolean USING (COALESCE(asignacionacc, 0)   <> 0);
ALTER TABLE codigosap ALTER COLUMN asignacionfalla DROP DEFAULT, ALTER COLUMN asignacionfalla TYPE boolean USING (COALESCE(asignacionfalla, 0) <> 0);
ALTER TABLE codigosap ALTER COLUMN multimodelo     DROP DEFAULT, ALTER COLUMN multimodelo     TYPE boolean USING (COALESCE(multimodelo, 0)     <> 0);
ALTER TABLE accesorio ALTER COLUMN cruce           DROP DEFAULT, ALTER COLUMN cruce           TYPE boolean USING (COALESCE(cruce, 0)           <> 0);
