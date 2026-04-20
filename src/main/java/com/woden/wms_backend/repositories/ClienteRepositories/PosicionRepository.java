package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PosicionModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PosicionRepository extends BaseRepository<PosicionModel, Integer> {
  @Query(value = "select numero from Posicion where codigoSapId !=0 AND Activo = 1 order by numero", nativeQuery = true)
  List<String> getListNoReservado();

  @Query(value = "select numero from Posicion where codigoSapId = 0 AND Activo = 1 order by numero", nativeQuery = true)
  List<String> getListReservado();

  @Query(value = "select id from Posicion where numero= :numero", nativeQuery = true)
  Integer getId(@Param("numero") String numero);

  @Query(value = "SELECT id FROM Posicion WHERE numero = :numero", nativeQuery = true)
  Integer getIdByNumero(@Param("numero") String numero);
}
