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
}
