package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.dto.BaseEmpaqueDTO;
import  com.woden.wms_backend.models.Entity.BaseEmpaqueModel;
import com.woden.wms_backend.services.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.woden.wms_backend.repositories.ClienteRepositories.BaseEmpaqueRepository;

import java.util.List;


@Service
public class BaseEmpaqueService extends BaseService<BaseEmpaqueModel, Integer> {

    @Autowired
    private BaseEmpaqueRepository empaqueRepository;


    public BaseEmpaqueDTO getModel(String base, String serial) {
        List<Object[]> results = empaqueRepository.getModel(base, serial);
        if (results.isEmpty()) {
            return null; // O manejar el caso de no encontrar resultados
        }
        Object[] row = results.get(0);
        BaseEmpaqueDTO model = new BaseEmpaqueDTO();
        model.setSerial(row[0].toString());
        model.setCodigoSap(row[1].toString());
        model.setEstadoSap(row[2].toString());
        model.setEstadoRR(row[3].toString());
        model.setLote(row[4] != null ? row[4].toString() : null);
        return model;
    }


}
