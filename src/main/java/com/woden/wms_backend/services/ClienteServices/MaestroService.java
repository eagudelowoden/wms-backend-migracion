package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class MaestroService extends BaseService<MaestroModel, Integer> {

    private final MaestroRepository maestroRepository;

    public MaestroService(MaestroRepository maestroRepository) {
        this.maestroRepository = maestroRepository;
    }

    public List<MaestroModel> getByTipoMaestroId(int tipoMaestroId) {
        return maestroRepository.findByTipoMaestroId(tipoMaestroId);
    }
}
