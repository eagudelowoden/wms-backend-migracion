package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PosicionModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PosicionRepository extends BaseRepository<PosicionModel, Integer> {
    
}
