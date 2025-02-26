package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface SmartCardRepository extends BaseRepository <SmartCardModel, Integer> {
    
}
