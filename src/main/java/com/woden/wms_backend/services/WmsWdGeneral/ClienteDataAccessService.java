package com.woden.wms_backend.services.WmsWdGeneral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.WmsWdGeneral.ClienteDataAccessRepository;

@Service
public class ClienteDataAccessService {

  @Autowired
  private ClienteDataAccessRepository clienteRepository;

  public Integer getTipoOrigenValue(int id) {
    Boolean result = clienteRepository.getTipoOrigenValue(id);
    return result != null ? (result ? 1 : 0) : null;
  }

  public Integer getBaseIngresoON(int id) {
    Boolean result = clienteRepository.getBaseIngresoON(id);
    return result != null ? (result ? 1 : 0) : null;
  }

  public Integer getBaseNoDisponibleON(int id) {
    Boolean result = clienteRepository.getBaseNoDisponibleON(id);
    return result != null ? (result ? 1 : 0) : null;
  }

  public Integer getCalidadON(int id) {
    Integer result = clienteRepository.getCalidadON(id);
    return result;
  }

  public Integer getNivelClasificacionValue(int id) {
    Boolean result = clienteRepository.getNivelClasificacionValue(id);
    return result ? 1 : 0;
  }

  public Integer getOdooPqrsON(int id) {
    Boolean result = clienteRepository.getOdooPqrsON(id);
    return result != null ? (result ? 1 : 0) : null;
  }

  public Integer getsmartCardInfoON(int id) {
       Boolean result = clienteRepository.getsmartCardInfoON(id);
       return result != null ? (result ? 1 : 0) : null;
  }


}
