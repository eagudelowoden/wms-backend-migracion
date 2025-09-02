package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.controllers.ClientesControllers.JasperReportController;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EmpaqueRepository;
import com.woden.wms_backend.services.BaseService;
// import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;

import java.time.LocalDateTime;

@Service
public class EmpaqueService extends BaseService<EmpaqueModel, Integer> {

    @Autowired
    private EmpaqueRepository empaqueRepository;

    @Autowired
    private IngresoRepository ingresoRepository;


    public EmpaqueService(EmpaqueRepository repository) {}
    private static final Logger logger = LoggerFactory.getLogger(JasperReportController.class);
    public void createEmpaque(Integer serialId, String serial, String mac, Integer codigoSapId, Integer palletId, Integer cajaEmpaqueId, Integer nivelId,
                              Integer usuarioId, LocalDateTime fecha, Integer loteId, Integer smartCardId, String smartCard
    ) {
        try {

            Integer loteIdParam = (loteId != 0) ? loteId : null;
            Integer filas = 0;
            empaqueRepository.createInsert(serialId, serial, mac, codigoSapId, palletId, cajaEmpaqueId, nivelId, usuarioId, fecha, loteIdParam, smartCardId, smartCard, filas
            );
            logger.info("Guardado correctamente");
        } catch (Exception e) {
            logger.error("Error al insertar empaque: {}", e.getMessage(), e);
        }
    }


}
