package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface EtiquetaCampoRepository extends BaseRepository<EtiquetaCampoModel, Integer> {
  @Query(value = "SELECT f.nombre, f.valor FROM pa_getlistlabelfield(CAST(:etiquetaId AS integer)) f", nativeQuery = true)
  List<Object[]> getListLabelField(@Param("etiquetaId") Integer etiquetaId);
}
