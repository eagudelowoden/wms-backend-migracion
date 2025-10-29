package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.controllers.ClientesControllers.JasperReportController;
import com.woden.wms_backend.models.Entity.CalidadModel;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.ClienteRepositories.CalidadRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.EmpaqueRepository;
import com.woden.wms_backend.services.BaseService;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalidadService extends BaseService<CalidadModel, Integer> {

  @Autowired
  private EmpaqueRepository empaqueRepository;

  @Autowired
  private CalidadRepository calidadRepository;

  public CalidadService(CalidadRepository repository) {
  }

  private static final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

    public void updateFinalStateQuality(Integer palletId, Integer estadoFinalId) {
        Integer filas = 0;
        calidadRepository.updateFinalStateQuality(palletId, estadoFinalId, filas);
        // opcional: log o manejo de filas
        System.out.println("✅ Filas actualizadas: " + filas);
    }

    public void createCalidad(Integer serialId, String serial, String mac,
                              Integer codigoSapId, Integer palletId,
                              Integer cajaEmpaqueId, Integer usuarioId,
                              LocalDateTime fecha) {
        try {
            calidadRepository.createInsertCalidad(
                    serialId, serial, mac, codigoSapId, palletId,
                    cajaEmpaqueId, usuarioId, fecha
            );
        } catch (Exception e) {
            logger.error("❌ Error al insertar en calidad: {}", e.getMessage(), e);
            throw e; // re-lanzamos para que el controller capture el error si es necesario
        }
    }


    public int eliminarSeriesCalidad(List<String> seriales) {
        int count = 0;
        for (String serial : seriales) {
            calidadRepository.eliminarSerialCalidad(serial);
            count++;
        }
        return count > 0 ? 1 : 0;
    }


    public int updateQualityEntry(Integer estadoId, Integer usuarioIdMovimiento, List<String> seriales) {
        int count = 0;

        for (String serial : seriales) {
            // Llamada al repositorio pasando los parámetros requeridos
            calidadRepository.updateQualityEntry(estadoId, usuarioIdMovimiento, serial);
            count++;
        }
        return count > 0 ? 1 : 0;
    }




}
