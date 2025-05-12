package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.AccesorioSearchDTO;
import com.woden.wms_backend.dto.CerrarPalletDTO;
import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.repositories.ClienteRepositories.AccesorioRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.PalletRepository;
import com.woden.wms_backend.services.BaseService;

import jakarta.transaction.Transactional;

@Service
public class AccesorioService extends BaseService<AccesorioModel, Integer> {
  public AccesorioService(AccesorioRepository repository) {

  }

  @Autowired
  private AccesorioRepository accesorioRepository;
  @Autowired
  private PalletRepository palletRepository;

  @Transactional
  public void guardarAccesorios(List<AccesorioModel> accesorios) {
    for (AccesorioModel a : accesorios) {
      accesorioRepository.insertarAccesorio(
          a.getCodigoSapId(),
          a.getTipoAccesorio(),
          a.getTipoOrigenId(),
          a.getOrigenId(),
          a.getPalletId(),
          a.getEstadoId(),
          a.getDocumento(),
          a.getObservacion(),
          a.getGuia(),
          a.getUsuarioId(),
          a.getCaja(),
          a.getPrealertaId(),
          a.getCruce());
    }
  }

  public List<AccesorioSearchDTO> buscarAccesoriosPorPallet(int palletId) {
    List<Object[]> resultados = accesorioRepository.buscarPorPalletId(palletId);
    List<AccesorioSearchDTO> accesorios = new ArrayList<>();

    for (Object[] fila : resultados) {
      AccesorioSearchDTO dto = new AccesorioSearchDTO();
      dto.setCodigo((String) fila[0]);
      dto.setDescripcion((String) fila[1]);
      dto.setTipoAccesorio((String) fila[2]);
      dto.setOrigen((String) fila[3]);
      dto.setDocumento((String) fila[4]);
      dto.setGuia((String) fila[5]);
      dto.setCaja(fila[6] != null ? fila[6].toString() : null);
      accesorios.add(dto);
    }

    return accesorios;
  }

  public int eliminarAccesorio(Integer palletId, Integer cantidad, Integer codigoSapId) {
    return accesorioRepository.deleteAccesorio(palletId, cantidad, codigoSapId);
  }

  public boolean cerrarPalletAccesorio(CerrarPalletDTO dto) {
    int filas = 0; // OUT simbólico
    accesorioRepository.sendAccesory(dto.getPalletId(), dto.getDestinoId(), filas);
    palletRepository.sendPallet(dto.getDestinoId(), dto.getTipologiaId(), dto.getPosicionId(), 1, dto.getPalletId(), 0);
    return true;
  }
}
