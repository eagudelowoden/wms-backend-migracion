package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface EtiquetaRepository extends BaseRepository<EtiquetaModel, Integer> {
  @Query(value = "EXEC pa_GetModelLabel :nombre", nativeQuery = true)
  List<Object[]> getModelLabel(@Param("nombre") String nombre);

  @Query(value = "EXEC pa_GetListLabel :tipo", nativeQuery = true)
  List<Object[]> getListLabel(@Param("tipo") String tipo);

  @Query(value = "EXEC pa_SearchLabeled :nombre, :tipo", nativeQuery = true)
  List<Object[]> searchLabeled(@Param("nombre") String nombre, @Param("tipo") String tipo);

  @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END "
      + "FROM [WmsWdGeneral].dbo.ClienteValidacion cv "
      + "JOIN [WmsWdGeneral].dbo.ClienteValidacionTipo cvt ON cv.ValidacionTipoId = cvt.Id "
      + "WHERE cv.ClienteId = :clienteId AND cvt.Codigo = :codigo AND cv.Activo = 1", nativeQuery = true)
  Integer tieneValidacionDirecta(@Param("clienteId") Integer clienteId, @Param("codigo") String codigo);
}
