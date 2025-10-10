package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.CajaDespachoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.CajaDespachoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class CajaDespachoService extends BaseService<CajaDespachoModel, Integer> {
  @Autowired
  private CajaDespachoRepository cajaDespachoRepository;

  public void insertBoxDispatch(String numero, Integer palletId, Integer usuarioId, String fecha) {
    cajaDespachoRepository.insertBoxDispatch(numero, palletId, usuarioId, fecha);
  }

  public List<Map<String, Object>> searchDispatch(Integer palletId) {
    List<Object[]> results = cajaDespachoRepository.searchDispatch(palletId);
    List<Map<String, Object>> dispatchList = new ArrayList<>();
    for (Object[] obj : results) {
      Map<String, Object> dispatch = new HashMap<>();
      dispatch.put("id", (Integer) obj[0]);
      dispatch.put("numero", (String) obj[1]);
      dispatch.put("seriales", (Integer) obj[2]);
      dispatchList.add(dispatch);
    }
    return dispatchList;
  }

  public void deleteBoxDispatch(Integer id) {
    cajaDespachoRepository.deleteBoxDispatch(id);
  }

  public Integer getLastBoxDispatch(Integer palletId) {
    List<Object[]> results = cajaDespachoRepository.getLastBoxDispatch(palletId);
    if (results.isEmpty() || results.get(0) == null) {
      return 0;      
    }
    return (Integer) results.get(results.size() - 1)[0];
  }

  public Integer getCountBoxDispatch(Integer cajaId) {
    return cajaDespachoRepository.getCountBoxDispatch(cajaId);
  }
}
