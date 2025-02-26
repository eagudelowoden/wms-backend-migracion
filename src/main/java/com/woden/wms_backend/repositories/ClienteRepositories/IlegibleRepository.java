package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.IlegibleModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface IlegibleRepository extends BaseRepository<IlegibleModel, Integer> {
    
}
