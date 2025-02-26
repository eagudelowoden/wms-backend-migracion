package com.woden.wms_backend.services.ClienteServices;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.repositories.ClienteRepositories.AccesorioRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class AccesorioService extends BaseService<AccesorioModel, Integer> {
    public AccesorioService(AccesorioRepository repository) {
        
    }
}
