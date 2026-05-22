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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional; // <--- 2. NUEVO IMPORT VITAL

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @Transactional(rollbackFor = Exception.class)
    public void createPackingCompleto(PackingTransactionDTO dto) {

        // Verificación de seguridad
        if (dto.getEstadoId() == null) {
            throw new IllegalArgumentException("El estadoId (SmartCard) es obligatorio para la transacción.");
        }
        if (dto.getEstadoSerialId() == null) {
            throw new IllegalArgumentException("El estadoSerialId (Serial) es obligatorio para la transacción.");
        }

        Integer loteIdParam    = (dto.getLoteId()    != null && dto.getLoteId()    != 0) ? dto.getLoteId()    : null;
        Integer smartCardIdParam = (dto.getSmartCardId() != null && dto.getSmartCardId() != 0) ? dto.getSmartCardId() : null;
        String  smartCardStr   = (dto.getSmartCard() != null && !dto.getSmartCard().trim().isEmpty()
                                  && !dto.getSmartCard().equals("0")) ? dto.getSmartCard() : null;

        // PASO 1: pa_InsertPackingWebD — usa estadoSerialId (ej: 66 - EMPACADO)
        //   - DELETE previo del serialId (evita duplicados)
        //   - INSERT en Empaque
        //   - UPDATE en Ingreso del serial con el estado correcto del serial
        Integer filasAfectadas = empaqueRepository.executeInsertPacking(
                dto.getSerialId(), dto.getSerial(), dto.getMac(), dto.getCodigoSapId(),
                dto.getPalletId(), dto.getCajaEmpaqueId(),
                dto.getEstadoSerialId(), // ← Serial usa su propio estado (66)
                dto.getNivelId(), dto.getUsuarioId(), java.time.LocalDateTime.now(),
                loteIdParam, smartCardIdParam, smartCardStr
        );

        if (filasAfectadas == null || filasAfectadas == 0) {
            throw new RuntimeException("La serie ya fue procesada o no existe.");
        }

        // PASO 1.5: UPDATE smartCardId en el Ingreso del SERIAL (asigna la smartcard al serial)
        if (smartCardIdParam != null && smartCardStr != null) {
            ingresoRepository.updateSmartCardEntry(smartCardIdParam, smartCardStr, dto.getSerial(), 4);
        }

        // PASO 2: UPDATE en Ingreso del SmartCard — usa estadoId (ej: 99 - EMPAQUE APROBADO)
        if (dto.getSmartCardCode() != null
                && !dto.getSmartCardCode().equals("0")
                && !dto.getSmartCardCode().isEmpty()) {

            ingresoRepository.UpdatePackingEntrySmartCard(
                    dto.getEstadoId(), // ← SmartCard usa su propio estado (99)
                    dto.getPalletId(), dto.getCajaEmpaqueId(),
                    dto.getUsuarioId(), dto.getSmartCardCode(),
                    loteIdParam, smartCardIdParam,
                    smartCardStr != null ? smartCardStr : "0"
            );
        }

        logger.info("✅ createPackingCompleto OK — serial: {}", dto.getSerial());
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



    @Transactional
    public void createEmpaqueWEB(Integer serialId, String serial, String mac, Integer codigoSapId,
                                 Integer palletId, Integer cajaEmpaqueId,
                                 Integer estadoId,
                                 Integer nivelId,
                                 Integer usuarioId, LocalDateTime fecha, Integer loteId,
                                 Integer smartCardId, String smartCard) {

        // Limpieza de nulos
        Integer loteParam = (loteId != null && loteId != 0) ? loteId : null;
        Integer scIdParam = (smartCardId != null && smartCardId != 0) ? smartCardId : null;
        String scParam = (smartCard != null && !smartCard.trim().isEmpty() && !smartCard.equals("0")) ? smartCard : null;

        try {
            // La Native Query devolverá el valor de @FilasOut a través del SELECT final
            Integer filasAfectadas = empaqueRepository.executeInsertPacking(
                    serialId, serial, mac, codigoSapId, palletId, cajaEmpaqueId,
                    estadoId,
                    nivelId, usuarioId, fecha, loteParam, scIdParam, scParam
            );

            if (filasAfectadas == null || filasAfectadas == 0) {
                throw new RuntimeException("La serie ya fue procesada o no existe.");
            }

            logger.info("✅ Empaque registrado. Filas: {}", filasAfectadas);

        } catch (Exception e) {
            // Extraemos "Serie No empacada, Por favor Reintentar."
            String mensajeParaAngular = extraerMensajeLimpio(e);
            logger.error("❌ Error en DB: {}", mensajeParaAngular);

            // Re-lanzamos el mensaje exacto
            throw new RuntimeException(mensajeParaAngular);
        }
    }

    private String extraerMensajeLimpio(Exception e) {
        Throwable cause = e;
        while (cause.getCause() != null) cause = cause.getCause();

        String msg = cause.getMessage();

        // SQL Server suele enviar los mensajes de RAISERROR con prefijos.
        // Intentamos limpiar si contiene el texto del error 50000
        if (msg != null && msg.contains("Error -")) {
            // Esto cortará después del guion si viene como "Runtime Error: Error - Equipo..."
            return msg.substring(msg.indexOf("Error -")).trim();
        }

        return msg != null ? msg : "Error desconocido en base de datos";
    }

    public int eliminarSeriesEmpaque(List<String> seriales) {
        int count = 0;
        for (String serial : seriales) {
            empaqueRepository.eliminarSeriesEmpaque(serial);
            count++;
        }
        return count > 0 ? 1 : 0;
    }
    @Transactional(rollbackFor = Exception.class) // Vital para que si falla el último, desaga los anteriores
    public int procesarEliminacionCompleta(Integer estadoId, Integer usuarioId, List<String> seriales) {
        int count = 0;

        for (String s : seriales) {
            // 1. Actualizamos el estado en Ingreso (Llamada al SP)
            // Como el SP recibe de a uno, el bucle debe estar aquí dentro de la transacción
            ingresoRepository.UpdatePackingAllEntry(estadoId, usuarioId, s);

            // 2. Eliminamos de la tabla de Empaque
            empaqueRepository.eliminarSeriesEmpaque(s);

            count++;
        }

        // Si el bucle termina sin errores, Spring hace el COMMIT de todo
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