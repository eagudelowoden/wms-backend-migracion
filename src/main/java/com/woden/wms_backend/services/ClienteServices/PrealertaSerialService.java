package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.PrealertaSerialModel;
import com.woden.wms_backend.repositories.ClienteRepositories.PrealertaSerialRepository;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.util.TypeMapper;

@Service
public class PrealertaSerialService extends BaseService<PrealertaSerialModel, Integer> {
  @Autowired
  private PrealertaSerialRepository prealertaSerialRepository;

  public PrealertaSerialModel getModelPreAlerta(Integer id, String serial) {
    List<Object[]> results = prealertaSerialRepository.getPreSerialAlertaById(id, serial);

    if (results.isEmpty()) {
      return null;
    }

    Object[] obj = results.get(0);
    PrealertaSerialModel preAlerta = new PrealertaSerialModel();
    preAlerta.setId((Integer) obj[0]);
    preAlerta.setSerial((String) obj[1]);
    preAlerta.setCodigoSap((String) obj[2]);
    preAlerta.setTramite((String) obj[3]);
    preAlerta.setPedido((String) obj[4]);
    preAlerta.setCaja((Integer) obj[5]);
    preAlerta.setFalla((String) obj[6]);
    preAlerta.setTecnicoCliente((String) obj[7]);
    preAlerta.setNovedad((String) obj[8]);
    preAlerta.setGarantia(TypeMapper.toBoolean(obj[9]));
    preAlerta.setRecogida((Byte) obj[10]);
    preAlerta.setMac((String) obj[11]);
    preAlerta.setLoteId((Integer) obj[12]);
    return preAlerta;
  }

  public Integer countRegister(int prealertaId, int recogidaOn, String tipo) {
    if ("Serializable".equalsIgnoreCase(tipo)) {
      return recogidaOn == 1
          ? prealertaSerialRepository.getTotalByPrealertSerializable(prealertaId)
          : prealertaSerialRepository.getTotalByPrealertSerializableNoRecogida(prealertaId);
    } else {
      return recogidaOn == 1
          ? prealertaSerialRepository.getTotalByPrealertNoSerializable(prealertaId)
          : prealertaSerialRepository.getTotalByPrealertNoSerializableNoRecogida(prealertaId);
    }
  }

  public List<PrealertaSerialModel> searchPrealertSerial(Integer palletId) {
    List<Object[]> results = prealertaSerialRepository.searchPrealertSerial(palletId);

    return results.stream().map(obj -> {
      PrealertaSerialModel preAlerta = new PrealertaSerialModel();
      preAlerta.setId((Integer) obj[0]);
      preAlerta.setSerial((String) obj[1]);
      preAlerta.setCodigoSap((String) obj[2]);
      preAlerta.setTramite((String) obj[3]);
      preAlerta.setPedido((String) obj[4]);
      preAlerta.setCaja((Integer) obj[5]);
      preAlerta.setFalla((String) obj[6]);
      preAlerta.setTecnicoCliente((String) obj[7]);
      preAlerta.setNovedad((String) obj[8]);
      preAlerta.setGarantia(TypeMapper.toBoolean(obj[9]));
      preAlerta.setRecogida((Byte) obj[10]);
      preAlerta.setMac((String) obj[11]);
      preAlerta.setLoteId((Integer) obj[12]);
      return preAlerta;
    }).collect(Collectors.toList());
  }
}
