package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PedidoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PedidoRepository extends BaseRepository<PedidoModel, Integer> {
  // @Query(value = "select p.id,cs.codigo,cs.descripcion,p.cantidad,r.id as remitenteId from Pedido p inner join +this.co.getBaseDatos().dbo.CodigoSap cs on p.productoId=cs.id inner join WmsWdgeneral.dbo.usuario r on p.remitenteId=r.id inner join WmsWdgeneral.dbo.cliente c on p.clienteId=c.id where p.estado='APROBADO' and c.nombre='cliente+", nativeQuery = true)
  // List<Object[]> getPedidos();
}
