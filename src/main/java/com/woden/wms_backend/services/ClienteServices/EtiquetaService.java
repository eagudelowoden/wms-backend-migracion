package com.woden.wms_backend.services.ClienteServices;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.EtiquetaListDTO;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetaRepository;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteService;

@Service
public class EtiquetaService extends BaseService<EtiquetaModel, Integer> {
  @Autowired
  private EtiquetaRepository etiquetaRepository;

  @Autowired
  private ClienteService clienteService;

  @Value("${PRN_LOCAL_PATH:}")
  private String localPrnPath;

  public EtiquetaModel getModelLabel(String nombre) {
    List<Object[]> results = etiquetaRepository.getModelLabel(nombre);

    if (results == null || results.isEmpty()) {
      throw new RuntimeException("No se encontró etiqueta con nombre: " + nombre);
    }

    Object[] obj = results.get(0);
    EtiquetaModel etiqueta = new EtiquetaModel();

    etiqueta.setId((Integer) obj[0]);
    etiqueta.setNombre((String) obj[1]);
    etiqueta.setTipo((String) obj[2]);
    etiqueta.setImpresion((Integer) obj[3]);
    etiqueta.setCodigoSapId((Integer) obj[4]);
    etiqueta.setCodigoSapCombo((String) obj[5] + "|" + (String) obj[6]);


    if (obj.length > 7) {
      etiqueta.setActivo((Boolean) obj[7]);
    }

    return etiqueta;
  }
  public List<EtiquetaListDTO> getListLabel(String tipo) {
    List<Object[]> results = etiquetaRepository.getListLabel(tipo);
    if (results.isEmpty()) {
      return Collections.emptyList();
    }
    List<EtiquetaListDTO> etiquetas = results.stream().map(obj -> {
      EtiquetaListDTO etiqueta = new EtiquetaListDTO();
      etiqueta.setNombre((String) obj[0]);
      return etiqueta;
    }).toList();
    return etiquetas;
  }

  public List<Map<String, Object>> searchLabeled(String nombre, String tipo) {
    List<Object[]> results = etiquetaRepository.searchLabeled(nombre, tipo);
    if (results.isEmpty()) {
      return Collections.emptyList();
    }
    List<Map<String, Object>> etiquetas = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0]);
      map.put("nombre", row[1]);
      etiquetas.add(map);
    }
    return etiquetas;
  }

  public boolean crearDirectorioPrn(String nombre, String tipo) {
    String prnRoute;
    if (localPrnPath != null && !localPrnPath.isBlank()) {
      String tipoLower = tipo != null ? tipo.toLowerCase() : "empaque";
      prnRoute = localPrnPath + File.separator + tipoLower;
    } else {
      Integer clientId = ClientDatabaseContext.getCurrentClientId();
      ClienteModel cliente = clienteService.getById(clientId);
      if (cliente == null) return false;
      prnRoute = "ETIQUETADO".equalsIgnoreCase(tipo)
          ? cliente.getPrnEtiquetado()
          : cliente.getPrnEmpaque();
    }
    if (prnRoute == null || prnRoute.isBlank()) return false;
    File dir = new File(prnRoute, nombre);
    boolean created = dir.mkdirs();
    System.out.println(created ? "Directorio PRN creado: " + dir.getAbsolutePath()
        : "No se pudo crear directorio PRN: " + dir.getAbsolutePath());
    return created;
  }
}
