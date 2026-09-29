package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.PedidoDTO;
import com.woden.wms_backend.models.Entity.PedidoModel;
import com.woden.wms_backend.services.BaseService;

@Service
public class PedidoService extends BaseService<PedidoModel, Integer> {

  // @Autowired
  // private PedidoRepository pedidoRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<PedidoDTO> buscarPedidos(String cliente, int id) {
    String baseDatos = ClientDatabaseContext.getCurrentClientDb();
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT p.id, cs.codigo AS producto, cs.descripcion, p.cantidad, r.id AS remitenteId ")
        .append("FROM WmsWdAplicaciones.dbo.Pedido p ")
        .append("INNER JOIN ").append(baseDatos).append(".dbo.CodigoSap cs ON p.productoId = cs.id ")
        .append("INNER JOIN WmsWdgeneral.dbo.usuario r ON p.remitenteId = r.id ")
        .append("INNER JOIN WmsWdgeneral.dbo.cliente c ON p.clienteId = c.id ")
        .append("WHERE p.estado = 'APROBADO' AND c.nombre = ? ");

    List<Object> params = new ArrayList<>();
    params.add(cliente);

    if (id != 0) {
      sql.append("AND p.id = ? ");
      params.add(id);
    }

    return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> new PedidoDTO(
        rs.getInt("id"),
        rs.getString("producto"),
        rs.getString("descripcion"),
        rs.getInt("cantidad"),
        rs.getInt("remitenteId")));
  }

  public void sendPedido(PedidoModel pedido) {
    
  }
}
