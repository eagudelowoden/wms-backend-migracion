package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.EnsambleModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EnsambleRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class EnsambleService extends BaseService<EnsambleModel, Integer> {
  @Autowired
  private EnsambleRepository ensambleRepository;

  public Integer insertAssemble(Integer serialId, String serial, String serial3, String serial4, String serial5,
      String mac,
      Integer codigoSapId, Integer palletId, Integer estadoId, Integer tipologiaId, Integer nivelId, Integer usuarioId,
      Integer usuarioIdAsignado, Integer loteId, String smartCard) {
    try {
      ensambleRepository.insertAssemble(serialId, serial, mac, serial3, serial4, serial5, codigoSapId, palletId,
          estadoId, tipologiaId, nivelId, usuarioId, usuarioIdAsignado, loteId, smartCard);
      return 1;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  public Integer deleteAssemble(List<String> seriales) {
    try {
      for (String serial : seriales) {
        ensambleRepository.deleteAssemble(serial);
      }
      return 1;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  public List<Map<String, Object>> getAssembleUser(String perfil, Integer usuarioId) {
    List<Object[]> results = ensambleRepository.getAssembleUser(perfil, usuarioId);

    System.out.println(results);
    List<Map<String, Object>> formattedResults = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0].toString());
      map.put("serial", row[1].toString());
      map.put("mac", row[2].toString());
      map.put("serial3", row[3] != null ? row[3].toString() : "");
      map.put("serial4", row[4] != null ? row[4].toString() : "");
      map.put("serial5", row[5] != null ? row[5].toString() : "");
      map.put("codigoSap", row[6].toString());
      map.put("descripcion", row[7].toString());
      map.put("usuarioAsignado", row[8] != null ? row[8].toString() : "");
      map.put("nivelId", Integer.parseInt(row[9].toString()));
      map.put("loteId", row[10] != null ? Integer.parseInt(row[10].toString()) : 0);
      map.put("lote", row[11] != null ? row[11].toString() : "");
      map.put("modelo", row[12] != null ? row[12].toString() : "");
      map.put("fecha", row[13] != null ? row[13].toString() : "");
      formattedResults.add(map);
    }
    return formattedResults;
  }
}