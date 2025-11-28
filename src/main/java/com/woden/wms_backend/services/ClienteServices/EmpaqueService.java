package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.controllers.ClientesControllers.JasperReportController;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
// ASEGÚRATE DE IMPORTAR TU DTO AQUÍ:
import com.woden.wms_backend.dto.PackingTransactionDTO;
import com.woden.wms_backend.repositories.ClienteRepositories.EmpaqueRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository; // <--- 1. NUEVO IMPORT
import com.woden.wms_backend.services.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <--- 2. NUEVO IMPORT VITAL

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpaqueService extends BaseService<EmpaqueModel, Integer> {

    @Autowired
    private EmpaqueRepository empaqueRepository;

    @Autowired
    private IngresoRepository ingresoRepository; // <--- 3. INYECCIÓN DEL REPOSITORIO DE INGRESO

    public EmpaqueService(EmpaqueRepository repository) {
        // Puedes dejar esto o quitarlo si usas @Autowired en los campos
    }

    private static final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

    // =========================================================================
    // 🚀 NUEVO MÉTODO UNIFICADO (Transaccional)
    // =========================================================================
    @Transactional(rollbackFor = Exception.class) // Si algo falla, hace rollback de todo
    public void createPackingCompleto(PackingTransactionDTO dto) {

        // A. Extraemos el modelo del empaque del DTO
        EmpaqueModel empaque = dto.getEmpaque();

        // Validamos nulos como hacías en tu método original
        // Primero verificamos que NO sea nulo, y luego que sea diferente de 0
        Integer loteIdParam = (empaque.getLoteId() != null && empaque.getLoteId() != 0)
                ? empaque.getLoteId()
                : null;
        Integer smartCardIdParam = (empaque.getSmartCardId() != null && empaque.getSmartCardId() != 0) ? empaque.getSmartCardId() : null;
        String smartCardStr = (empaque.getSmartCard() != null) ? empaque.getSmartCard() : "0";

        // 1. INSERTAR EN EMPAQUE (Usando tu repositorio existente)
        empaqueRepository.createInsert(
                empaque.getSerialId(),
                empaque.getSerial(),
                empaque.getMac(),
                empaque.getCodigoSapId(),
                empaque.getPalletId(),
                empaque.getCajaEmpaqueId(),
                empaque.getNivelId(),
                empaque.getUsuarioId(),
                LocalDateTime.now(), // O dto.getFecha() si viene del front
                loteIdParam,
                smartCardIdParam,
                smartCardStr
        );

        // 2. ACTUALIZAR EL SMARTCARD (Destino 1: El registro del SmartCard en sí)
        // Usamos los datos sueltos que vienen en el DTO para el update
        ingresoRepository.UpdatePackingEntrySmartCard(
                dto.getEstadoId(),       // Estado (ej. EMPACADO)
                dto.getPalletId(),
                dto.getCajaEmpaqueId(),
                dto.getUsuarioId(),
                dto.getSmartCardCode(),  // Serial del SmartCard
                loteIdParam,
                smartCardIdParam,
                smartCardStr
        );

        // 3. ACTUALIZAR EL PRODUCTO PRINCIPAL (Destino 2: Asignar SmartCard al producto)
        ingresoRepository.UpdatePackingEntrySmartCard(
                dto.getEstadoId(),
                dto.getPalletId(),
                dto.getCajaEmpaqueId(),
                dto.getUsuarioId(),
                dto.getSerial(),         // Serial del producto (decodificador, modem, etc)
                loteIdParam,
                smartCardIdParam,
                smartCardStr
        );

        // No necesitamos catch aquí. Si falla, queremos que explote para que
        // @Transactional deshaga el insert del paso 1.
    }
    // =========================================================================

    public void createEmpaque(Integer serialId, String serial, String mac, Integer codigoSapId, Integer palletId,
                              Integer cajaEmpaqueId, Integer nivelId,
                              Integer usuarioId, LocalDateTime fecha, Integer loteId, Integer smartCardId, String smartCard) {
        try {
            Integer loteIdParam = (loteId != 0) ? loteId : null;
            empaqueRepository.createInsert(serialId, serial, mac, codigoSapId, palletId, cajaEmpaqueId, nivelId,
                    usuarioId, fecha, loteIdParam, smartCardId, smartCard);
        } catch (Exception e) {
            logger.error("Error al insertar empaque: {}", e.getMessage(), e);
        }
    }

    public int eliminarSeriesEmpaque(List<String> seriales) {
        int count = 0;
        for (String serial : seriales) {
            empaqueRepository.eliminarSeriesEmpaque(serial);
            count++;
        }
        return count > 0 ? 1 : 0;
    }

    public void updateSmartCard(Integer smartCardId, String smartCardNuevo,
                                String serial) {
        Integer filas = 4;
        empaqueRepository.updateSmartCard(smartCardId, smartCardNuevo, serial, filas);
    }

    public void UpdatePacking(Integer serialId, String serialNuevo, String mac,
                              String serialAnterior) {
        Integer filas = 4;
        empaqueRepository.UpdatePacking(serialId, serialNuevo, mac, serialAnterior, filas);
    }

    public void UpdatePackingWeb(Integer serialId, String serialNuevo, String mac, Integer nivelNuevo,
                                 String serialAnterior) {
        Integer filas = 0;
        empaqueRepository.UpdatePackingWeb(serialId, serialNuevo, nivelNuevo, mac, serialAnterior, filas);
    }

}