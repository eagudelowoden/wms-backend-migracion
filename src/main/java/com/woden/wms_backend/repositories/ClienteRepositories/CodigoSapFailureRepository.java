package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.CodigoSapFailureModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface CodigoSapFailureRepository extends BaseRepository<CodigoSapFailureModel, Integer> {
  @Query(value = "SELECT f.codigo, f.descripcion FROM pa_getlistfailurescodigosapnoid(CAST(:nombre AS character varying)) f", nativeQuery = true)
  List<Object[]> getFallas(@Param("nombre") String nombre);

  @Query(value = "SELECT CAST(f.\"ComponenteObligatorio\" AS boolean) FROM pa_searchmandatorycomponent(CAST(:codigoSapId AS integer), CAST(:fallaId AS integer)) f", nativeQuery = true)
  Boolean searchMandatoryComponent(@Param("codigoSapId") Integer codigoSapId, @Param("fallaId") Integer fallaId);

  @Query (value = "EXEC pa_SearchSapCodeFailureComponent :codigoSapId, :fallaId", nativeQuery = true)
  List<Object[]> searchSapCodeFailureComponent(@Param("codigoSapId") Integer codigoSapId, @Param("fallaId") Integer fallaId);

  @Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.descripcion, f.\"ComponenteObligatorio\" FROM pa_getlistfailurescodigosap(CAST(:codigoSapId AS integer)) f", nativeQuery = true)
  List<Object[]> getFailuresBySap(@Param("codigoSapId") Integer codigoSapId);
}