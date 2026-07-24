package com.woden.wms_backend.services.ClienteServices;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woden.wms_backend.dto.clientDTO.clasificacion.ClasificacionRequest;
import com.woden.wms_backend.models.Entity.ClasificacionModel;
import com.woden.wms_backend.repositories.ClienteRepositories.ClasificacionRepository;
import com.woden.wms_backend.services.BaseService;

import jakarta.transaction.Transactional;

@Service
public class ClasificacionService extends BaseService<ClasificacionModel, Integer> {

  private static final Logger logger     = LoggerFactory.getLogger(ClasificacionService.class);
  private static final int    CHUNK_SIZE = 50;

  @Autowired
  private ClasificacionRepository repository;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Guardado en lote de la clasificación (Ingreso + Clasificacion) vía pa_SaveClasificacion.
   * Chunks secuenciales de 50 en una transacción — reemplaza el guardado paralelo por serial
   * que causaba bloqueos y actualizaciones perdidas (mismo patrón que DiagnosticoService.save).
   */
  @Transactional
  public void save(List<ClasificacionRequest> items) {
    int total = items.size();
    int procesados = 0;
    try {
      for (int i = 0; i < total; i += CHUNK_SIZE) {
        List<ClasificacionRequest> chunk = items.subList(i, Math.min(i + CHUNK_SIZE, total));
        String json = objectMapper.writeValueAsString(chunk);
        repository.save(json);
        procesados += chunk.size();
        logger.info("[CLASIFICACION] chunk guardado: {}/{}", procesados, total);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error al guardar clasificación: " + e.getMessage(), e);
    }
  }

  @Transactional
  public void create(List<List<String>> serialEstadoUsuario, Integer usuarioId) {
    String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    for (List<String> entry : serialEstadoUsuario) {
      String serial = entry.get(0);
      String estado = entry.get(1);
      Integer usuarioAsignado = Integer.parseInt(entry.get(2));
      repository.insertClasificacion(serial, estado, usuarioId, fecha, usuarioAsignado);
    }
  }

  public void insertClasificacionWeb(List<List<String>> seriales, Integer usuarioId) {
    for (List<String> entry : seriales) {
      String serial = entry.get(0);
      Integer usuarioAsignadoId = Integer.parseInt(entry.get(2));
      repository.insertClasificacionWeb(serial, usuarioId, usuarioAsignadoId);
    }
  }

  public void updateClasificacion(String serial, String estadoEnviado, Integer nivelId) {
    repository.updateClasificacion(serial, estadoEnviado, nivelId);
  }

  public void deleteClasificacion(String serial) {
    repository.deleteClasificacion(serial);
  }

  public List<Map<String, Object>> getClassifiedUser(Integer usuarioId) {
    List<Object[]> results = repository.getClassifiedUser(usuarioId);

    List<Map<String, Object>> formattedResults = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0].toString());
      map.put("serialId", row[1].toString());
      map.put("serial", row[2].toString());
      map.put("mac", row[3].toString());
      map.put("codigoSap", row[4].toString());
      map.put("descripcion", row[5].toString());
      map.put("usuarioAsignado", row[6] != null ? row[6].toString() : "");
      map.put("nivelId", Integer.parseInt(row[7].toString()));
      map.put("loteId", row[8] != null ? Integer.parseInt(row[8].toString()) : 0);
      map.put("lote", row[9] != null ? row[9].toString() : "");
      map.put("modelo", row[10] != null ? row[10].toString() : "");
      map.put("fecha", row[11] != null ? row[11].toString() : "");
      formattedResults.add(map);
    }
    return formattedResults;
  }

}
