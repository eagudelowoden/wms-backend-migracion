package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.Entity.PqrsDetalleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Detalle de PQRS — reemplaza la vieja tabla PQRS (App_PQRS_Tickets primero,
 * App_PQRS_Truckrolls como fallback). Ambas tablas viven en la BD del cliente
 * actual (ya seleccionada por ClientDatabaseContext al momento de la llamada).
 *
 * id_contratista, id_razon_escalamiento e id_tipo_equipo de App_PQRS_Tickets
 * se resuelven contra App_Master_Contratista / App_Master_RazonEscalamiento /
 * App_Master_TipoEquipo en [WmsWdGeneral] (columna Nombre, confirmado).
 *
 * TODO (pendiente confirmación del usuario): columna(s) de Observación en
 * App_PQRS_Tickets y App_PQRS_Truckrolls (VARCHAR ~100-200, aún sin nombre
 * definido) — el input de Observación en el frontend sigue siendo solo
 * visual hasta que se confirme el nombre exacto.
 */
@Repository
public class PqrsDetalleRepository {

    private static final Logger logger = LoggerFactory.getLogger(PqrsDetalleRepository.class);

    @Autowired
    private DataSource dataSource;

    public PqrsDetalleModel buscarDetalle(String serial) {
        if (serial == null || serial.isBlank()) return null;

        PqrsDetalleModel detalle = buscarEnTickets(serial);
        if (detalle != null) return detalle;

        return buscarEnTruckrolls(serial);
    }

    private PqrsDetalleModel buscarEnTickets(String serial) {
        // En la práctica serial_equipo viene NULL en casi todos los registros de
        // App_PQRS_Tickets — el serial real siempre está en "mac" (la misma
        // particularidad ya conocida de esta tabla: "mac" en realidad guarda el
        // serial del equipo, no una MAC). Se busca y se muestra por mac.
        // Cruce cross-DB a WmsWdGeneral para traer el nombre real de las 3 FKs.
        String sql = "SELECT TOP 1 t.mac, t.created_at, t.observaciones, t.foto_mac_path, " +
                "mc.Nombre AS contratista_nombre, " +
                "mre.Nombre AS razon_nombre, " +
                "mte.Nombre AS tipo_equipo_nombre " +
                "FROM App_PQRS_Tickets t " +
                "LEFT JOIN [WmsWdGeneral].[dbo].[App_Master_Contratista] mc ON mc.id = t.id_contratista " +
                "LEFT JOIN [WmsWdGeneral].[dbo].[App_Master_RazonEscalamiento] mre ON mre.id = t.id_razon_escalamiento " +
                "LEFT JOIN [WmsWdGeneral].[dbo].[App_Master_TipoEquipo] mte ON mte.id = t.id_tipo_equipo " +
                "WHERE t.mac = ? ORDER BY t.created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                PqrsDetalleModel detalle = new PqrsDetalleModel();
                detalle.setFuente("TICKETS");
                detalle.setSerial(rs.getString("mac"));
                detalle.setFechaCreacion(String.valueOf(rs.getTimestamp("created_at")));
                detalle.setContratista(rs.getString("contratista_nombre"));
                detalle.setInfDano(rs.getString("razon_nombre"));
                detalle.setTecnologia(rs.getString("tipo_equipo_nombre"));
                detalle.setObsPqrs(rs.getString("observaciones"));
                detalle.setImagenUrl(rs.getString("foto_mac_path"));
                return detalle;
            }
        } catch (SQLException e) {
            logger.error("[PqrsDetalleRepository] Error consultando App_PQRS_Tickets para serial '{}': {}", serial, e.getMessage());
            return null;
        }
    }

    private PqrsDetalleModel buscarEnTruckrolls(String serial) {
        String sql = "SELECT TOP 1 serial_equipo, created_at, contratista, " +
                "motivo_diagnostico, tipo_tt, razon_apertura " +
                "FROM App_PQRS_Truckrolls WHERE serial_equipo = ? ORDER BY created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                PqrsDetalleModel detalle = new PqrsDetalleModel();
                detalle.setFuente("TRUCKROLL");
                detalle.setSerial(rs.getString("serial_equipo"));
                detalle.setFechaCreacion(String.valueOf(rs.getTimestamp("created_at")));
                detalle.setContratista(rs.getString("contratista"));
                detalle.setInfDano(rs.getString("motivo_diagnostico"));
                detalle.setTecnologia(rs.getString("tipo_tt"));
                detalle.setObsPqrs(rs.getString("razon_apertura"));
                detalle.setImagenUrl(null); // App_PQRS_Truckrolls no tiene imagen
                return detalle;
            }
        } catch (SQLException e) {
            logger.error("[PqrsDetalleRepository] Error consultando App_PQRS_Truckrolls para serial '{}': {}", serial, e.getMessage());
            return null;
        }
    }
}
