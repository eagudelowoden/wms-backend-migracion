package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.dto.SmartCardDTO;
import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.repositories.ClienteRepositories.SmartCardRepository;
import com.woden.wms_backend.services.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Integer updateToPreasignado(String serial, Integer usuarioId) {
        Integer filas = 0;
        smartCardRepository.updateEstadoPreasignado(serial, usuarioId, filas);
        return filas;
    }
    public Integer updateEstadosSmartcard(Integer estadoFinalId, Integer fallaId, String serial) {
        Integer filas = 0;
        smartCardRepository.updateEstadosSmartcard(estadoFinalId, fallaId, serial);
        return filas;
    }

    public List<Map<String, Object>> getPreasignadas(Integer usuarioId) {
        List<Object[]> results = smartCardRepository.getSmartcardsPreasignadas(usuarioId);
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("serial", row[1]);
            map.put("mac", row[2]);
            map.put("codigoSap", row[3]);
            map.put("descripcion", row[4]);
            map.put("usuarioAsignado", row[5]);
            map.put("nivelId", row[6]);
            map.put("loteId", row[7]);
            map.put("lote", row[8]);
            map.put("modelo", row[9]);
            map.put("fecha", row[10]);
            lista.add(map);
        }
        return lista;
    }
    public List<Map<String, Object>> searchSmartCardEntry(String estadoFinal) {
        // 1. Llamada al repositorio (asumiendo que el método devuelve List<Object[]>)
        List<Object[]> results = smartCardRepository.searchSmartCardEntry(estadoFinal);
        List<Map<String, Object>> lista = new ArrayList<>();

        // 2. Mapeo manual de la fila (Object[]) a un Mapa
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();

            // El orden depende de cómo el SP lanza el SELECT
            map.put("serial", row[0]);
            map.put("codigo", row[1]);
            map.put("descripcion", row[2]);
            map.put("falla", row[3]);
            map.put("estado", row[4]);

            lista.add(map);
        }

        return lista;
    }




}
