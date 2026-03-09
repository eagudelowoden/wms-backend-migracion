package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.dto.ProcesarSmartCardDTO;
import com.woden.wms_backend.dto.SmartCardDTO;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.SmartCardRepository;
import com.woden.wms_backend.services.BaseService;
import jakarta.transaction.Transactional;
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

    @Autowired
    private IngresoService  ingresoService;


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
    public Integer updateEstadosSmartcard(Integer estadoFinalId, Integer fallaId, String serial) {
        Integer filas = 0;
        smartCardRepository.updateEstadosSmartcard(estadoFinalId, fallaId, serial);
        return filas;
    }
    @Transactional
    public void procesarGuardadoCompleto(ProcesarSmartCardDTO dto) {
        try {
            // 1. Insertar en SmartCardWeb (pa_InsertSmartCardWeb)
            smartCardRepository.createInsert(
                    dto.getSmartCard().getSerialId(),
                    dto.getSmartCard().getSerial(),
                    dto.getSmartCard().getCodigoSapId(),
                    dto.getSmartCard().getUsuarioId()
            );

            // 2. Actualizar diagnósticos (pa_UpdateSmartCard)
            smartCardRepository.updateEstadosSmartcard(
                    dto.getEstadoFinalId(),
                    dto.getFallaId(),
                    dto.getSerial()
            );

            // 3. ACTUALIZAR INVENTARIO (pa_UpdateStateAllEntry)
            if (dto.getIngresos() != null && !dto.getIngresos().isEmpty()) {
                for (IngresoModel ingreso : dto.getIngresos()) {
                    // CORRECCIÓN: Pasamos los 3 argumentos en el orden correcto
                    ingresoService.updateStateAllEntry(
                            ingreso.getEstadoId(),           // 1. Integer estadoId
                            ingreso.getUsuarioIdMovimiento(), // 2. Integer usuarioId
                            ingreso.getSerial()              // 3. String serial
                    );
                }
            }

        } catch (Exception e) {
            // Al lanzar RuntimeException, Spring hace Rollback de los pasos 1, 2 y 3 si alguno falla
            e.printStackTrace();
            throw new RuntimeException("Fallo en la transacción única: " + e.getMessage());
        }
    }

    public Integer updateToPreasignado(String serial, Integer usuarioId) {
        Integer filas = 0;
        smartCardRepository.updateEstadoPreasignado(serial, usuarioId, filas);
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
