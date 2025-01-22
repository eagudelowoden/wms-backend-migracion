package com.woden.wms_backend.services.ClienteServices;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.repositories.ClienteRepositories.CodigoSapRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class CodigoSapService extends BaseService<CodigoSapModel, Integer> {
    public CodigoSapService(CodigoSapRepository codigoSapRepository) {
    }
}
