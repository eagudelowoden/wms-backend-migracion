package com.woden.wms_backend.services.ClienteServices;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class MaestroService extends BaseService<MaestroModel, Integer> {
    public MaestroService(MaestroRepository maestroRepository) {
    }
}
