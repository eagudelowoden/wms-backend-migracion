package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.ClienteRepositories.CosmeticaRepository;

@Service
public class CosmeticaService {
  @Autowired
  private CosmeticaRepository repository;
  
  public List<Map<String, Object>> searchCosmeticaEntry() {
    List<Object[]> results = repository.searchCosmeticaEntry();

    List<Map<String, Object>> formattedResults = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("serial", row[0].toString());
      map.put("mac", row[1].toString());
      map.put("codigoSap", row[2].toString());
      map.put("descripcion", row[3].toString());
      formattedResults.add(map);
    }
    return formattedResults;
  }
}
