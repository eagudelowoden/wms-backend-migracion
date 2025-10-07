package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.repositories.ClienteRepositories.SmartCardRepository;
import com.woden.wms_backend.services.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartCardService extends BaseService<SmartCardModel, Integer> {


    @Autowired
        private  SmartCardRepository smartCardRepository;

    public boolean validateSmartCardInfo(String smartCard) {
        int filas = 0;
        List<Object[]> resultado = smartCardRepository.validateSmartCardInfo(smartCard, filas);

        // ⚙️ Si no hay resultados → no existe → se puede ingresar
        if (resultado == null || resultado.isEmpty()) {
            return false; // no empacado
        }

        // ⚙️ Si hay filas → ya existe / empacado
        return true;
    }


    public SmartCardService(SmartCardRepository repository) {
    }


}
