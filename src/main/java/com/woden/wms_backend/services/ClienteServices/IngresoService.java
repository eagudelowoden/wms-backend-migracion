package com.woden.wms_backend.services.ClienteServices;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.IngresoDTO;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.services.BaseService;

import jakarta.transaction.Transactional;

@Service
public class IngresoService extends BaseService<IngresoModel, Integer> {
  @Autowired
  private IngresoRepository ingresoRepository;

  @Transactional
  public void createIngreso(IngresoModel ingreso) {
    ingresoRepository.insertIngreso(
        ingreso.getSerial(),
        ingreso.getMac(),
        ingreso.getSerial3(),
        ingreso.getSerial4(),
        ingreso.getSerial5(),
        ingreso.getCodigoSapId(),
        ingreso.getPalletId(),
        ingreso.getEstadoId(),
        ingreso.getTipoOrigenId(),
        ingreso.getOrigenId(),
        ingreso.getTipologiaId(),
        ingreso.getNivelId(),
        ingreso.getTramite(),
        ingreso.getDocumento(),
        ingreso.getGuia(),
        ingreso.getCaja(),
        ingreso.getFalla(),
        ingreso.getTecnicoCliente(),
        ingreso.getPrealertaId(),
        ingreso.getCruce(),
        ingreso.getNovedad(),
        ingreso.getGarantiaFabricante(),
        ingreso.getUsuarioId(),
        ingreso.getObservaciones(),
        ingreso.getEstadoCliente(),
        ingreso.getLoteId(),
        ingreso.getCajaIngresoId(),
        ingreso.getModeloId());
  }

  public List<IngresoDTO> searchEntryReingreso(int palletId) {
    List<Object[]> results = ingresoRepository.searchEntryReingreso(palletId);

    return results.stream().map(obj -> {
      IngresoDTO ingreso = new IngresoDTO();
      ingreso.setSerial((String) obj[0]);
      ingreso.setMac((String) obj[1]);
      ingreso.setSerial3((String) obj[2]);
      ingreso.setSerial4((String) obj[3]);
      ingreso.setSerial5((String) obj[4]);
      ingreso.setCodigoSap((String) obj[5]);
      ingreso.setDescripcion((String) obj[6]);
      ingreso.setTipologia((String) obj[7]);
      ingreso.setTipoOrigen((String) obj[8]);
      ingreso.setFecha(((Timestamp) obj[9]).toString());
      ingreso.setEstadoCliente((String) obj[10]);
      ingreso.setLote((String) obj[11]);
      ingreso.setModelo((String) obj[12]);
      ingreso.setReingreso((Integer) obj[13]);
      return ingreso;
    }).collect(Collectors.toList());
  }

  public void cerrarIngreso(Integer palletId, Integer estadoId, Integer tipologiaId, Integer usuarioId,
      Integer opcion) {
    Integer filas = 0; // aquí el OUT lo usamos de forma simbólica
    ingresoRepository.sendIngreso(estadoId, tipologiaId, usuarioId, palletId, opcion, filas);
  }
}
