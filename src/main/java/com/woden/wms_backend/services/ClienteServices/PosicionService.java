package com.woden.wms_backend.services.ClienteServices;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.PosicionModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PosicionService extends BaseService<PosicionModel, Integer> {
    public PosicionService(MaestroRepository maestroRepository) {}
}
