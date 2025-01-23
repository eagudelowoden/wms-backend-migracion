package com.woden.wms_backend.services.ClienteServices;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.TipoMaestroModel;
import com.woden.wms_backend.repositories.ClienteRepositories.TipoMaestroRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class TipoMaestroService extends BaseService<TipoMaestroModel, Integer> {
    public TipoMaestroService(TipoMaestroRepository repository) {
    }
}
