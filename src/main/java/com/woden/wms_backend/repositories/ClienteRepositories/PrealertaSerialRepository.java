package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PrealertaSerialModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PrealertaSerialRepository extends BaseRepository<PrealertaSerialModel, Integer> {
  @Query(value = "EXEC pa_GetModelPrealertSerial :id, :serial", nativeQuery = true)
  List<Object[]> getPreSerialAlertaById(@Param("id") Integer id, @Param("serial") String serial);

  @Query(value = "SELECT COUNT(tipo) FROM PrealertaSerial WHERE prealertaId = :prealertaId AND tipo = 'Serializable' AND recogida = 1", nativeQuery = true)
  Integer getTotalByPrealertSerializable(@Param("prealertaId") Integer prealertaId);

  @Query(value = "SELECT COUNT(tipo) FROM PrealertaSerial WHERE prealertaId = :prealertaId AND tipo = 'Serializable'", nativeQuery = true)
  Integer getTotalByPrealertSerializableNoRecogida(@Param("prealertaId") Integer prealertaId);

  @Query(value = "SELECT SUM(cantidad) FROM PrealertaSerial WHERE prealertaId = :prealertaId AND tipo = 'No_Serializable'", nativeQuery = true)
  Integer getTotalByPrealertNoSerializableNoRecogida(@Param("prealertaId") Integer prealertaId);

  @Query(value = "SELECT SUM(cantidad_recogida) FROM PrealertaSerial WHERE prealertaId = :prealertaId AND tipo = 'No_Serializable'", nativeQuery = true)
  Integer getTotalByPrealertNoSerializable(@Param("prealertaId") Integer prealertaId);

  // @Query(value = "EXEC pa_SearchPrealertSerial :prealertId", nativeQuery =
  // true)
  @Query(value = "select id,serial,codigosap,tramite,pedido,caja,falla,tecnicoCliente,novedad,garantia,isnull(recogida,0) recogida,mac,loteId from PrealertaSerial \r\n"
      + //
      "where prealertaId = :prealertId", nativeQuery = true)
  List<Object[]> searchPrealertSerial(@Param("prealertId") Integer prealertId);
}
