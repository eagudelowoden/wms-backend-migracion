package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.WmsWdGeneral.ParametroTruckrollModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TruckrollRepository {

    private static final Logger logger = LoggerFactory.getLogger(TruckrollRepository.class);

    @Autowired
    private DataSource dataSource;

    /**
     * Carga las reglas activas del cliente actual desde WmsWdGeneral usando query cross-DB.
     * Se filtra por BaseDestino para obtener solo las reglas que aplican al cliente.
     */
    public List<ParametroTruckrollModel> findReglasActivasPorCliente(String clientDbName) {
        String sql = "SELECT Id, Descripcion, BaseOrigen, TablaOrigen, CampoCruceOrigen, " +
                "CampoFechaOrigen, CampoIdOrigen, CampoClienteDestinoOrigen, CampoTruckRollOrigen, " +
                "BaseDestino, TablaDestino, CampoCruceDestino, CampoClienteOrigenDestino, " +
                "CampoTruckRollDestino, CampoParametroId, CampoCodigoSapOrigen, CodigoSapIdOrigen, " +
                "DiasGarantia, DiasTruckRoll, VentanaDias " +
                "FROM [WmsWdGeneral].[dbo].[Parametro_TruckrollsLiberty] " +
                "WHERE Activo = 1 AND BaseDestino = ?";

        List<ParametroTruckrollModel> reglas = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clientDbName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ParametroTruckrollModel r = new ParametroTruckrollModel();
                    r.setId(rs.getInt("Id"));
                    r.setDescripcion(rs.getString("Descripcion"));
                    r.setBaseOrigen(rs.getString("BaseOrigen"));
                    r.setTablaOrigen(rs.getString("TablaOrigen"));
                    r.setCampoCruceOrigen(rs.getString("CampoCruceOrigen"));
                    r.setCampoFechaOrigen(rs.getString("CampoFechaOrigen"));
                    r.setCampoIdOrigen(rs.getString("CampoIdOrigen"));
                    r.setCampoClienteDestinoOrigen(rs.getString("CampoClienteDestinoOrigen"));
                    r.setCampoTruckRollOrigen(rs.getString("CampoTruckRollOrigen"));
                    r.setBaseDestino(rs.getString("BaseDestino"));
                    r.setTablaDestino(rs.getString("TablaDestino"));
                    r.setCampoCruceDestino(rs.getString("CampoCruceDestino"));
                    r.setCampoClienteOrigenDestino(rs.getString("CampoClienteOrigenDestino"));
                    r.setCampoTruckRollDestino(rs.getString("CampoTruckRollDestino"));
                    r.setCampoParametroId(rs.getString("CampoParametroId"));
                    r.setCampoCodigoSapOrigen(rs.getString("CampoCodigoSapOrigen"));
                    r.setCodigoSapIdOrigen(rs.getInt("CodigoSapIdOrigen"));
                    r.setDiasGarantia(rs.getInt("DiasGarantia"));
                    r.setDiasTruckRoll(rs.getInt("DiasTruckRoll"));
                    r.setVentanaDias(rs.getInt("VentanaDias"));
                    reglas.add(r);
                }
            }
        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error cargando reglas para {}: {}", clientDbName, e.getMessage());
        }
        return reglas;
    }

    /**
     * Verifica si el cliente tiene activa la validación con el código dado (ej. "TRUCKROLL").
     * Usa JDBC directo con nombre calificado a WmsWdGeneral (igual que el resto de este
     * repositorio) para evitar el "connection pinning": esta llamada ocurre con
     * ClientDatabaseContext ya apuntando a la BD del cliente, así que una consulta vía
     * Hibernate/JPA a ClienteValidacionRepository terminaría enrutada a la BD del cliente
     * en vez de WmsWdGeneral (donde realmente viven ClienteValidacion/ClienteValidacionTipo).
     */
    public boolean tieneValidacionActiva(Integer clienteId, String codigo) {
        if (clienteId == null || codigo == null) return false;
        String sql = "SELECT cv.Activo " +
                "FROM [WmsWdGeneral].[dbo].[ClienteValidacion] cv " +
                "INNER JOIN [WmsWdGeneral].[dbo].[ClienteValidacionTipo] cvt ON cvt.Id = cv.ValidacionTipoId " +
                "WHERE cv.ClienteId = ? AND cvt.Codigo = ? AND cv.Activo = 1 AND cvt.Activo = 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setString(2, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error consultando validación '{}' para clienteId={}: {}", codigo, clienteId, e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si existe un ticket PQRS previo para el serial en App_PQRS_Tickets del cliente.
     * La columna "mac" de esta tabla almacena en realidad el serial del equipo.
     * Determina si la clasificación sube de nivel: 0→1 (Garantía) o 6→7 (Posible TruckRoll).
     */
    public boolean hasPqrsTicket(String serial) {
        if (serial == null || serial.isBlank()) return false;
        String sql = "SELECT COUNT(1) FROM App_PQRS_Tickets WHERE mac = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error consultando App_PQRS_Tickets para serial={}: {}", serial, e.getMessage());
        }
        return false;
    }

    /**
     * Marca el ingreso como "solo PQRS" (TruckRollId = 14): el serial no cruzó en
     * ninguna tabla Despacho origen pero sí tiene ticket en App_PQRS_Tickets.
     * WHERE TruckRollId IS NULL asegura idempotencia.
     */
    public void updateIngresoSoloPqrs(String serial) {
        String sql = "UPDATE Ingreso SET TruckRollId = 14 WHERE Serial = ? AND TruckRollId IS NULL";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serial);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error marcando solo-PQRS para serial '{}': {}", serial, e.getMessage());
        }
    }

    /**
     * Obtiene el Despacho origen vinculado a un Ingreso antes de eliminarlo.
     * Devuelve { ClienteOrigenId, ClienteParametroId } o null si el serial no fue clasificado.
     */
    public long[] getDespachoVinculado(String serial) {
        String sql = "SELECT ClienteOrigenId, ClienteParametroId FROM Ingreso " +
                "WHERE Serial = ? AND ClienteOrigenId IS NOT NULL AND ClienteParametroId IS NOT NULL";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new long[]{ rs.getLong(1), rs.getLong(2) };
                }
            }
        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error consultando despacho vinculado del serial '{}': {}", serial, e.getMessage());
        }
        return null;
    }

    /**
     * Libera el Despacho en la BD origen cuando se elimina el Ingreso vinculado:
     * limpia ClienteDestinoId y TruckRollId para que el serial pueda reclasificarse
     * correctamente en un reingreso.
     */
    public void limpiarDespachoOrigen(ParametroTruckrollModel regla, long despachoId) {
        String sql = String.format(
                "UPDATE [%s].[dbo].[%s] SET [%s] = NULL, [%s] = NULL, [%s] = NULL WHERE [%s] = ?",
                regla.getBaseOrigen(),
                regla.getTablaOrigen(),
                regla.getCampoClienteDestinoOrigen(),
                regla.getCampoTruckRollOrigen(),
                regla.getCampoParametroId(),
                regla.getCampoIdOrigen()
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, despachoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error liberando Despacho {} en {}: {}",
                    despachoId, regla.getBaseOrigen(), e.getMessage());
        }
    }

    /**
     * Verifica si el TipoOrigen del ingreso tiene Adicional = 1 en el maestro de WmsWdGeneral.
     * Cuando es true, el serial es "baja normal del cliente" y se omite la clasificación Posible TruckRoll.
     */
    public boolean isTipoOrigenAdicionalUno(Integer tipoOrigenId) {
        if (tipoOrigenId == null) return false;
        String sql = "SELECT ISNULL(Adicional, 0) FROM Maestro WHERE Id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tipoOrigenId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error consultando Maestro.Adicional para tipoOrigenId={}: {}", tipoOrigenId, e.getMessage());
        }
        return false;
    }

    /**
     * Busca el Despacho en la BD origen que corresponde al serial/mac del equipo.
     * Usa query cross-DB con nombres de tabla y columna dinámicos desde la regla.
     * Devuelve { id, diasDesdeDespacho } o null si no hay match.
     */
    public long[] findDespachoOrigen(ParametroTruckrollModel regla, String valorCruce) {
        // Nombres de campos son seguros — vienen de Parametro_TruckrollsLiberty (BD interna, no input del usuario)
        String sql = String.format(
                "SELECT TOP 1 [%s], DATEDIFF(day, [%s], GETDATE()) AS Dias " +
                "FROM [%s].[dbo].[%s] " +
                "WHERE [%s] = ? " +
                "AND [%s] IS NULL " +
                "AND [%s] = ? " +
                "AND [%s] >= DATEADD(day, -%d, GETDATE()) " +
                "ORDER BY [%s] DESC",
                regla.getCampoIdOrigen(),
                regla.getCampoFechaOrigen(),
                regla.getBaseOrigen(),
                regla.getTablaOrigen(),
                regla.getCampoCruceOrigen(),
                regla.getCampoClienteDestinoOrigen(),
                regla.getCampoCodigoSapOrigen(),
                regla.getCampoFechaOrigen(),
                regla.getVentanaDias(),
                regla.getCampoFechaOrigen()
        );

        logger.debug("[TruckrollRepository] Buscando Despacho — SQL: {}", sql);
        logger.debug("[TruckrollRepository] Params: cruce='{}', codigoSapId={}", valorCruce, regla.getCodigoSapIdOrigen());
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, valorCruce);
            ps.setInt(2, regla.getCodigoSapIdOrigen());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long despachoId = rs.getLong(regla.getCampoIdOrigen());
                    long dias = rs.getLong("Dias");
                    logger.debug("[TruckrollRepository] Despacho encontrado: id={}, dias={}", despachoId, dias);
                    return new long[]{ despachoId, dias };
                } else {
                    logger.debug("[TruckrollRepository] No se encontró Despacho matching");
                }
            }
        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error buscando Despacho en {} para cruce '{}': {}",
                    regla.getBaseOrigen(), valorCruce, e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza las columnas TruckRoll del Ingreso (cliente actual).
     * WHERE TruckRollId IS NULL asegura idempotencia.
     */
    public void updateIngresoTruckroll(ParametroTruckrollModel regla, String serial,
                                        long despachoId, Integer truckRollId) {
        String sql = String.format(
                "UPDATE Ingreso SET [%s] = ?, [%s] = ?, [%s] = ? " +
                "WHERE Serial = ? AND [%s] IS NULL",
                regla.getCampoTruckRollDestino(),
                regla.getCampoClienteOrigenDestino(),
                regla.getCampoParametroId(),
                regla.getCampoTruckRollDestino()
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (truckRollId != null) ps.setInt(1, truckRollId);
            else ps.setNull(1, Types.INTEGER);

            ps.setLong(2, despachoId);
            ps.setInt(3, regla.getId());
            ps.setString(4, serial);
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error actualizando Ingreso para serial '{}': {}", serial, e.getMessage());
        }
    }

    /**
     * Marca el Despacho en la BD origen como consumido (asignado a este Ingreso).
     * Usa query cross-DB con nombres dinámicos.
     */
    public void updateDespachoConsumido(ParametroTruckrollModel regla, long despachoId,
                                         String serial, Integer truckRollId) {
        // El subselect busca el Ingreso por serial + vínculo con este despacho (ClienteOrigenId),
        // que updateIngresoTruckroll ya dejó asignado — así también funciona cuando TruckRollId es NULL.
        String sql = String.format(
                "UPDATE [%s].[dbo].[%s] " +
                "SET [%s] = (SELECT TOP 1 Id FROM Ingreso WHERE Serial = ? AND [%s] = ?), " +
                "    [%s] = ?, " +
                "    [%s] = ? " +
                "WHERE [%s] = ?",
                regla.getBaseOrigen(),
                regla.getTablaOrigen(),
                regla.getCampoClienteDestinoOrigen(),
                regla.getCampoClienteOrigenDestino(),
                regla.getCampoTruckRollOrigen(),
                regla.getCampoParametroId(),
                regla.getCampoIdOrigen()
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, serial);
            ps.setLong(2, despachoId);
            if (truckRollId != null) ps.setInt(3, truckRollId);
            else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, regla.getId());
            ps.setLong(5, despachoId);
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("[TruckrollRepository] Error actualizando Despacho {} en {}: {}",
                    despachoId, regla.getBaseOrigen(), e.getMessage());
        }
    }
}
