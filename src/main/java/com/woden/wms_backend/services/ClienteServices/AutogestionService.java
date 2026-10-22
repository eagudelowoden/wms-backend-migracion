package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.clientDTO.AutogestionPalletDTO;
import com.woden.wms_backend.dto.clientDTO.AutogestionSerialDTO;
import com.woden.wms_backend.repositories.ClienteRepositories.AutogestionRepository;

@Service
public class AutogestionService {
  private static final Set<String> ACCIONES = Set.of("ESTADO", "CAJA", "PALLET");
  private static final Set<String> ACCIONES_PALLET = Set.of("MOVER", "CAJA_VACIAR", "CAJA_ELIMINAR", "CAJA_PALLET");

  @Autowired
  private AutogestionRepository repository;

  public List<Map<String, Object>> estados() {
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object[] row : repository.estados()) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("id", row[0]);
      map.put("nombre", row[1]);
      result.add(map);
    }
    return result;
  }

  public Map<String, Object> buscarSerial(String serial) {
    List<Object[]> rows = repository.buscarSerial(serial.trim());
    if (rows.isEmpty()) {
      return null;
    }
    Object[] r = rows.get(0);
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", r[0]);
    map.put("serial", r[1]);
    map.put("mac", r[2]);
    map.put("codigoSap", r[3]);
    map.put("descripcion", r[4]);
    map.put("estadoId", r[5]);
    map.put("estado", r[6]);
    map.put("palletId", r[7]);
    map.put("pallet", r[8]);
    map.put("cajaEmpaqueId", r[9]);
    map.put("cajaEmpaque", r[10]);
    map.put("modulo", r[11]);
    map.put("seccion", r[12]);
    map.put("esProduccion", r[13] != null && ((Number) r[13]).intValue() == 1);
    return map;
  }

  public Map<String, Object> buscarPallet(String pallet) {
    List<Object[]> rows = repository.buscarPallet(pallet.trim());
    if (rows.isEmpty()) {
      return null;
    }
    Object[] r = rows.get(0);
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", r[0]);
    map.put("numero", r[1]);
    map.put("codigoSap", r[2]);
    map.put("descripcion", r[3]);
    map.put("estadoId", r[4]);
    map.put("estado", r[5]);
    map.put("modulo", r[6]);
    map.put("seccion", r[7]);
    map.put("esProduccion", r[8] != null && ((Number) r[8]).intValue() == 1);
    map.put("totalSeriales", r[9]);
    map.put("totalCajas", r[10]);
    List<Map<String, Object>> cajas = new ArrayList<>();
    for (Object[] c : repository.palletCajas(((Number) r[0]).intValue())) {
      Map<String, Object> caja = new LinkedHashMap<>();
      caja.put("id", c[0]);
      caja.put("numero", c[1]);
      caja.put("cantidad", c[2]);
      cajas.add(caja);
    }
    map.put("cajas", cajas);
    return map;
  }

  public void accionPallet(AutogestionPalletDTO dto) {
    if (dto.getPallet() == null || dto.getPallet().isBlank()) {
      throw new IllegalArgumentException("El pallet es obligatorio.");
    }
    if (dto.getUsuarioId() == null || dto.getUsuarioId() <= 0) {
      throw new IllegalArgumentException("No se pudo identificar el usuario.");
    }
    if (dto.getAccion() == null || !ACCIONES_PALLET.contains(dto.getAccion())) {
      throw new IllegalArgumentException("Acción no válida.");
    }
    if (dto.getJustificacion() == null || dto.getJustificacion().trim().length() < 10) {
      throw new IllegalArgumentException("La justificación es obligatoria (mínimo 10 caracteres).");
    }
    repository.accionPallet(dto.getPallet().trim(), dto.getAccion(), dto.getEstadoDestinoId(),
        dto.getCajaNumero() == null ? null : dto.getCajaNumero().trim(),
        dto.getPalletDestino() == null ? null : dto.getPalletDestino().trim(),
        dto.getUsuarioId(), dto.getJustificacion().trim());
  }

  public void accionSerial(AutogestionSerialDTO dto) {
    if (dto.getSerial() == null || dto.getSerial().isBlank()) {
      throw new IllegalArgumentException("El serial es obligatorio.");
    }
    if (dto.getUsuarioId() == null || dto.getUsuarioId() <= 0) {
      throw new IllegalArgumentException("No se pudo identificar el usuario.");
    }
    if (dto.getAccion() == null || !ACCIONES.contains(dto.getAccion())) {
      throw new IllegalArgumentException("Acción no válida.");
    }
    if (dto.getJustificacion() == null || dto.getJustificacion().trim().length() < 10) {
      throw new IllegalArgumentException("La justificación es obligatoria (mínimo 10 caracteres).");
    }
    repository.accionSerial(dto.getSerial().trim(), dto.getAccion(), dto.getEstadoDestinoId(),
        dto.getPalletDestino() == null ? null : dto.getPalletDestino().trim(),
        dto.getUsuarioId(), dto.getJustificacion().trim());
  }

  public List<Map<String, Object>> logs(String serial, Integer limite) {
    String filtro = (serial == null || serial.isBlank()) ? null : serial.trim();
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object[] r : repository.logs(filtro, limite == null ? 200 : limite)) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("id", r[0]);
      map.put("fecha", r[1] == null ? null : r[1].toString());
      map.put("usuario", r[2]);
      map.put("accion", r[3]);
      map.put("serial", r[4]);
      map.put("estadoOrigen", r[5]);
      map.put("estadoDestino", r[6]);
      map.put("palletOrigen", r[7]);
      map.put("palletDestino", r[8]);
      map.put("cajaOrigen", r[9]);
      map.put("justificacion", r[10]);
      map.put("cantidad", r[11]);
      result.add(map);
    }
    return result;
  }
}
