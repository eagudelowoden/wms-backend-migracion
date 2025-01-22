package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.stereotype.Repository;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface IngresoRepository extends BaseRepository<IngresoModel, Integer> {
    
}
