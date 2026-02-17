package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.dto.SmartCardDTO;
import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.repositories.ClienteRepositories.SmartCardRepository;
import com.woden.wms_backend.services.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    // En tu Servicio Java
    public void createSmartCard(SmartCardDTO dto) {
        try {
            smartCardRepository.createInsert(
                    dto.getSerialId(), // Asegúrate que este no sea null
                    dto.getSerial(),
                    dto.getCodigoSapId(),
                    dto.getUsuarioId()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error en la base de datos: " + e.getMessage());
        }
    }


}
