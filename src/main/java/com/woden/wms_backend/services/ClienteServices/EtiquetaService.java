package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.clientDTO.EtiquetaListDTO;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetaRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class EtiquetaService extends BaseService<EtiquetaModel, Integer> {
  @Autowired
  private EtiquetaRepository etiquetaRepository;

  public EtiquetaModel getModelLabel(String nombre) {
    List<Object[]> results = etiquetaRepository.getModelLabel(nombre);
    EtiquetaModel etiqueta = new EtiquetaModel();

    Object[] obj = results.get(0);
    etiqueta.setId((Integer) obj[0]);
    etiqueta.setNombre((String) obj[1]);
    etiqueta.setTipo((String) obj[2]);
    etiqueta.setImpresion((Integer) obj[3]);
    etiqueta.setCodigoSapId((Integer) obj[4]);
    etiqueta.setCodigoSapCombo((String) obj[5] + "|" + (String) obj[6]);
    etiqueta.setActivo((Boolean) obj[7]);
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
}
