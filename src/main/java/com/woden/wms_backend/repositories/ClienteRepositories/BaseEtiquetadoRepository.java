package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.woden.wms_backend.models.Entity.BaseEtiquetadoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface BaseEtiquetadoRepository extends BaseRepository<BaseEtiquetadoModel, Integer> {

  @Query(value = "EXEC pa_GetModelBase :base, :serial", nativeQuery = true)
  List<Object[]> getModelBase(@Param("base") String base, @Param("serial") String serial);

  @Query(value = "EXEC pa_getCountBase :baseEtiquetado", nativeQuery = true)
  Integer getCountBase(@Param("baseEtiquetado") String baseEtiquetado);

}