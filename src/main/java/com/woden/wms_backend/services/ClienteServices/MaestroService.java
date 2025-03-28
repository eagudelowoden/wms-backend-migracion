package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> obtenerTipologias(String desc1, String desc2, String desc3, String desc4) {
        List<Object[]> result = maestroRepository.getTipologias(desc1, desc2, desc3, desc4);
        return result.stream()
                .map(r -> (String) r[0]) // extrae el código del resultado
                .collect(Collectors.toList());
    }

    public int getIdMaster(String codigo, String tipo) {
        Integer id = maestroRepository.getIdMaster(codigo, tipo);
        return id != null ? id : 0;
    }

    public List<String> getListByTipo(String tipo) {
        return maestroRepository.getListByTipo(tipo);
    }

}
