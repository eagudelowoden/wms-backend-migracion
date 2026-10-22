package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.controllers.ClientesControllers.JasperReportController;
import com.woden.wms_backend.dto.AuditoriaInSituRequest;
import com.woden.wms_backend.dto.CalidadExtrasRequest;
import com.woden.wms_backend.dto.ConfirmarPalletDTO;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.models.Entity.CalidadModel;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.CalidadRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.EmpaqueRepository;
import com.woden.wms_backend.services.BaseService;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.woden.wms_backend.repositories.ClienteRepositories.PalletRepository;
import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

@Service
public class CalidadService extends BaseService<CalidadModel, Integer> {

  @Autowired
  private EmpaqueRepository empaqueRepository;

  @Autowired
  private CalidadRepository calidadRepository;

  @Autowired
  private PalletRepository palletRepository;

  @Autowired
  private IngresoService ingresoService;

  @Autowired
  private DataSource dataSource;

  public CalidadService(CalidadRepository repository) {
  }

  private static final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

  private static final String CODIGO_CAMPOS_EXTRAS = "CALIDAD_CAMPOS_EXTRAS";

    public void updateFinalStateQuality(Integer palletId, Integer estadoFinalId) {
        Integer filas = 0;
        calidadRepository.updateFinalStateQuality(palletId, estadoFinalId);
        // opcional: log o manejo de filas
        System.out.println("✅ Filas actualizadas: " + filas);
    }

    public void updateFinalStateQualityByCaja(Integer cajaEmpaqueId, Integer estadoFinalId) {
        calidadRepository.updateFinalStateQualityByCaja(cajaEmpaqueId, estadoFinalId);
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
            logger.error("Error al insertar en calidad: {}", e.getMessage(), e);
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

    public int updateQualityPalletEntry(Integer estadoId, Integer usuarioIdMovimiento, List<Integer> palletIds) {
        if (palletIds == null || palletIds.isEmpty()) {
            return 0; // nada que actualizar
        }

        for (Integer palletId : palletIds) {
            calidadRepository.updateQualityPalletEntry(estadoId, usuarioIdMovimiento, palletId);
        }

        return 1; // ✅ éxito (actualizó al menos uno)
    }

    public int updateQuality(Integer fallaFuncionalId, Integer fallaComesticaId, String serial) {
        try {
            calidadRepository.updateQuality(fallaFuncionalId, fallaComesticaId, serial);
            return 1; // si llega aquí, el SP se ejecutó correctamente
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public int updateQualityExtras(CalidadExtrasRequest req) {
        if (req == null || req.getSerial() == null || req.getSerial().trim().isEmpty()) {
            throw new BusinessRuleException("Serial requerido para guardar los campos extras.");
        }
        if (!camposExtrasActivos()) {
            throw new BusinessRuleException(
                    "Los campos extras no estan habilitados para este cliente o falta el procedimiento pa_UpdateQualityExtrasBySerial.");
        }
        Integer filas = calidadRepository.updateQualityExtrasBySerial(
                req.getSerial().trim(),
                req.getPersonal() == null ? "" : req.getPersonal(),
                req.getDni() == null ? "" : req.getDni(),
                req.getVoltaje() == null ? "" : req.getVoltaje(),
                req.getObservaciones() == null ? "" : req.getObservaciones());
        return filas != null ? filas : 0;
    }

    private boolean camposExtrasActivos() {
        Integer clienteId = ClientDatabaseContext.getCurrentClientId();
        if (clienteId == null) {
            return false;
        }
        String sql = "SELECT cv.Activo "
                + "FROM [WmsWdGeneral].[dbo].[ClienteValidacion] cv "
                + "INNER JOIN [WmsWdGeneral].[dbo].[ClienteValidacionTipo] cvt ON cvt.Id = cv.ValidacionTipoId "
                + "WHERE cv.ClienteId = ? AND cvt.Codigo = ? AND cv.Activo = 1 AND cvt.Activo = 1 "
                + "AND OBJECT_ID('dbo.pa_UpdateQualityExtrasBySerial') IS NOT NULL";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setString(2, CODIGO_CAMPOS_EXTRAS);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("[CalidadService] Error consultando validacion '{}' para clienteId={}: {}",
                    CODIGO_CAMPOS_EXTRAS, clienteId, e.getMessage());
            return false;
        }
    }

    public boolean sendCalidad(ConfirmarPalletDTO dto) {
        palletRepository.enviarPallet(
                dto.getPalletId(), dto.getDestinoId(), dto.getDestinoId(),
                dto.getTipologiaId(), dto.getPosicionId(), 1,
                dto.getUsuarioId(), 0, 0, 0);
        return true;
    }

    public int validateQualityByPallet(Integer palletId) {
        try {
            Integer result = calidadRepository.validateQualityByPallet(palletId);
            return result != null ? result : 0;
        } catch (Exception e) {
            // SP no existe o error → dejar pasar
            return 0;
        }
    }

    public Map<String, Object> getAuditoriaBySerial(String serial) {
        List<Object[]> rows = calidadRepository.findLatestBySerial(serial);
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        Object[] r = rows.get(0);
        Map<String, Object> out = new HashMap<>();
        out.put("id", r[0]);
        out.put("serialId", r[1]);
        out.put("serial", r[2]);
        out.put("mac", r[3]);
        out.put("codigoSapId", r[4]);
        out.put("palletId", r[5]);
        out.put("cajaEmpaqueId", r[6]);
        out.put("fallaFuncionalId", r[7]);
        out.put("fallaCosmeticaId", r[8]);
        out.put("usuarioId", r[9]);
        out.put("fecha", r[10] != null ? r[10].toString() : null);
        out.put("fallaFuncionalCodigo", r[11] != null ? r[11].toString() : null);
        out.put("fallaCosmeticaCodigo", r[12] != null ? r[12].toString() : null);
        out.put("estadoFinalCodigo", r[13] != null ? r[13].toString() : null);
        return out;
    }

    @Transactional
    public Map<String, Object> auditarInSitu(AuditoriaInSituRequest req) {
        if (req == null || req.getSerial() == null || req.getSerial().trim().isEmpty()) {
            throw new BusinessRuleException("Serial requerido para auditoria in-situ.");
        }
        if (req.getFallaFuncionalId() == null || req.getFallaCosmeticaId() == null) {
            throw new BusinessRuleException("Falla funcional y cosmetica requeridas.");
        }
        if (req.getUsuarioId() == null) {
            throw new BusinessRuleException("Usuario requerido para auditoria in-situ.");
        }
        String serial = req.getSerial().trim();
        IngresoModel ingreso = ingresoService.getModelIngreso(serial);
        if (ingreso.getCodigoSapId() == null) {
            throw new BusinessRuleException("Serial sin codigo SAP asignado.");
        }
        calidadRepository.createInsertCalidad(
                ingreso.getId(),
                ingreso.getSerial(),
                ingreso.getMac() == null ? "" : ingreso.getMac(),
                ingreso.getCodigoSapId(),
                ingreso.getPalletId(),
                ingreso.getCajaEmpaqueId(),
                req.getUsuarioId(),
                LocalDateTime.now());
        calidadRepository.updateQuality(req.getFallaFuncionalId(), req.getFallaCosmeticaId(), serial);
        if (req.getEstadoCalidadId() != null) {
            calidadRepository.updateEstadoCalidadBySerial(serial, req.getEstadoCalidadId());
        }
        Map<String, Object> out = new HashMap<>();
        out.put("success", true);
        out.put("serial", serial);
        out.put("fallaFuncionalId", req.getFallaFuncionalId());
        out.put("fallaCosmeticaId", req.getFallaCosmeticaId());
        out.put("estadoCalidadId", req.getEstadoCalidadId());
        return out;
    }









}
