package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.TipoMaestroModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface TipoMaestroRepository extends BaseRepository<TipoMaestroModel, Integer> {
}
