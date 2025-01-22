package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface MaestroRepository extends BaseRepository<MaestroModel, Integer> {
    
}
