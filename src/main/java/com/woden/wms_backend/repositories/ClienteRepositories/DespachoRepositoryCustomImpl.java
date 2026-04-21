package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.Entity.AccesorioModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class DespachoRepositoryCustomImpl implements DespachoRepositoryCustom {

    @Autowired
    private DataSource dataSource;

    @Override
    public Integer insertDispatchAccesoryBatch(
            List<AccesorioModel> accesorios,
            Integer estadoId,
            Integer usuarioId,
            String pedidoSap) {

        String sql = "{call pa_InsertDispatchAccesory(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        Connection conn = null;
//METODO PARA DESPACHAR DE MANERA MASIVA
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            CallableStatement cs = conn.prepareCall(sql);

            for (AccesorioModel a : accesorios) {
                cs.setInt(1,  a.getId());
                cs.setInt(2,  a.getCodigoSapId());
                cs.setString(3,  a.getTipoAccesorio());
                cs.setInt(4,  a.getTipoOrigenId());
                cs.setInt(5,  a.getOrigenId());
                cs.setInt(6,  a.getPalletId());
                cs.setInt(7,  estadoId);
                cs.setInt(8,  a.getEstadoLimpiezaId());
                cs.setString(9,  a.getDocumento());
                cs.setString(10, a.getObservacion());
                cs.setString(11, a.getGuia());
                cs.setInt(12, usuarioId);
                cs.setTimestamp(13, toTimestamp(a.getFecha()));
                cs.setString(14, a.getSerialEmpaque());
                cs.setInt(15, a.getCaja());
                cs.setString(16, pedidoSap);
                cs.setTimestamp(17, toTimestamp(a.getFechaLimpieza()));
                cs.setTimestamp(18, toTimestamp(a.getFechaEmpaque()));
                cs.setInt(19, a.getUsuarioLimpiezaId());
                cs.addBatch();
            }

            int[] filas = cs.executeBatch();
            conn.commit();
            return filas.length > 0 ? 1 : 0;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return 0;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private Timestamp toTimestamp(String fecha) {
        if (fecha == null || fecha.isEmpty()) return null;
        try {
            return Timestamp.valueOf(fecha.replace("T", " "));
        } catch (Exception e) {
            try {
                return Timestamp.valueOf(fecha + " 00:00:00");
            } catch (Exception ex) {
                return null;
            }
        }
    }
}