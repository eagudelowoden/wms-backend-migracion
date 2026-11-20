/* ============================================================================
   Autogestión como módulo asignable desde Gestión de Perfiles.
   Se ejecuta UNA sola vez, en la base WmsWdGeneral (compartida por los clientes).
   Id fijo = 46 (el mayor actual es 45); debe coincidir con MenuModuloMapping.java.
   ============================================================================ */
USE WmsWdGeneral;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Modulo WHERE Id = 46 OR (SeccionId = 3 AND Nombre = N'Autogestión'))
BEGIN
    IF COLUMNPROPERTY(OBJECT_ID('dbo.Modulo'), 'Id', 'IsIdentity') = 1
    BEGIN
        SET IDENTITY_INSERT dbo.Modulo ON;
        INSERT INTO dbo.Modulo (Id, SeccionId, Nombre, Icono, Controlador)
        VALUES (46, 3, N'Autogestión', '', 'autogestion');
        SET IDENTITY_INSERT dbo.Modulo OFF;
    END
    ELSE
    BEGIN
        INSERT INTO dbo.Modulo (Id, SeccionId, Nombre, Icono, Controlador)
        VALUES (46, 3, N'Autogestión', '', 'autogestion');
    END
END
GO

SELECT * FROM dbo.Modulo WHERE Id = 46;
GO
