package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface CodigoSapPermitidoRepository extends BaseRepository<CodigoSapModel, Integer> {

  @Query(value = "EXEC pa_GetListCodigoSapPermitido :codigoSapId, 0", nativeQuery = true)
  List<Object[]> getPool(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "EXEC pa_GetListCodigoSapPermitido :codigoSapId, 1", nativeQuery = true)
  List<Object[]> getPermitidos(@Param("codigoSapId") Integer codigoSapId);
}
