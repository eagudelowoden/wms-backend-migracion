package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.CodigoSapFailureModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface CodigoSapFailureRepository extends BaseRepository<CodigoSapFailureModel, Integer> {
  @Query(value = "EXEC pa_GetListFailuresCodigoSapNoId :nombre", nativeQuery = true)
  List<Object[]> getFallas(@Param("nombre") String nombre);

  @Query(value = "EXEC pa_SearchMandatoryComponent :codigoSapId, :fallaId", nativeQuery = true)
  Boolean searchMandatoryComponent(@Param("codigoSapId") Integer codigoSapId, @Param("fallaId") Integer fallaId);

  @Query (value = "EXEC pa_SearchSapCodeFailureComponent :codigoSapId, :fallaId", nativeQuery = true)
  List<Object[]> searchSapCodeFailureComponent(@Param("codigoSapId") Integer codigoSapId, @Param("fallaId") Integer fallaId);

  @Query(value = "EXEC pa_GetListFailuresCodigoSap :codigoSapId", nativeQuery = true)
  List<Object[]> getFailuresBySap(@Param("codigoSapId") Integer codigoSapId);
}