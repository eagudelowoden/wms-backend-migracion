package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface EtiquetaCampoRepository extends BaseRepository<EtiquetaCampoModel, Integer> {
  @Query(value = "EXEC pa_GetListLabelField :etiquetaId", nativeQuery = true)
  List<Object[]> getListLabelField(@Param("etiquetaId") Integer etiquetaId);
}
