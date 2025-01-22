package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.stereotype.Repository;
import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface CodigoSapRepository extends BaseRepository<CodigoSapModel, Integer> {
    
}
