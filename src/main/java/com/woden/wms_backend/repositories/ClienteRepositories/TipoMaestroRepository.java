package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.TipoMaestroModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface TipoMaestroRepository extends BaseRepository<TipoMaestroModel, Integer> {
  @Query(value = "SELECT id FROM TipoMaestro WHERE descripcion = :descripcion", nativeQuery = true)
  Integer getTipoCodigoSap(@Param("descripcion") String descripcion);
}
