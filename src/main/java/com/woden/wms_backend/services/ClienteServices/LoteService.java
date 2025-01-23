package com.woden.wms_backend.services.ClienteServices;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.LoteModel;
import com.woden.wms_backend.repositories.ClienteRepositories.LoteRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class LoteService extends BaseService<LoteModel, Integer> {
    LoteService(LoteRepository loteRepository) {
        
    }
}
