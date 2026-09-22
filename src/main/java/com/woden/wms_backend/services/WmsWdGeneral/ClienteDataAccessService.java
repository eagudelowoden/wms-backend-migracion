package com.woden.wms_backend.services.WmsWdGeneral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

  @Cacheable("baseEmpaqueOn")
  public Integer getBaseEmpaqueON(int id) {
    return clienteRepository.getBaseEmpaqueON(id);
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

  public Integer updateBaseEmpaqueON(int id, int baseEmpaqueON) {
    return clienteRepository.updateBaseEmpaqueON(baseEmpaqueON, id);
  }

  public Boolean getEtiquetaUnitariaON(int id) {
    return clienteRepository.getEtiquetaUnitariaON(id);
  }

  public Integer getSerialMasterON(int id) {
    Integer v = clienteRepository.getSerialMasterON(id);
    return v != null ? v : 0;
  }

}
