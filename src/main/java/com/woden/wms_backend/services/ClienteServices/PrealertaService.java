package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.PreAlertaDTO;
import com.woden.wms_backend.models.Entity.PrealertaModel;
import com.woden.wms_backend.repositories.ClienteRepositories.PrealertaRepository;
import com.woden.wms_backend.services.BaseService;

import jakarta.transaction.Transactional;

@Service
public class PrealertaService extends BaseService<PrealertaModel, Integer> {
  @Autowired
  private PrealertaRepository prealertaRepository;

  public PrealertaModel getModelPreAlerta(String nombre) {
    List<Object[]> results = prealertaRepository.getPreAlertaById(nombre);

    if (results.isEmpty()) {
      return null;
    }

    Object[] obj = results.get(0);
    PrealertaModel prealerta = new PrealertaModel();
    prealerta.setId((Integer) obj[0]);
    prealerta.setNombre((String) obj[1]);
    prealerta.setTipoOrigen((String) obj[2]);
    prealerta.setOrigen((String) obj[3]);
    prealerta.setGuia((String) obj[4]);
    prealerta.setTipologiaId((Integer) obj[5]);
    return prealerta;
  }

  public List<PreAlertaDTO> getPrealertasByEstado(String estado) {
    List<Object[]> results = prealertaRepository.findByEstado(estado);

    if (results.isEmpty()) {
      return null;
    }

    return results.stream().map(obj -> {
      PreAlertaDTO prealerta = new PreAlertaDTO();
      prealerta.setId((Integer) obj[0]);
      prealerta.setNombre((String) obj[1]);
      return prealerta;
    }).collect(Collectors.toList());
  }

  public Integer getDifferencePrealerta(Integer prealertaId) {
    return prealertaRepository.getDifferencePrealerta(prealertaId);
  }

  @Transactional
  public void updatePrealerta(Integer prealertaId) {
    try {
      prealertaRepository.updatePrealerta(prealertaId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
