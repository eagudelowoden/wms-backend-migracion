package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.InventarioModel;
import com.woden.wms_backend.repositories.ClienteRepositories.InventarioRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class InventarioService extends BaseService<InventarioModel, Integer> {
  @Autowired
  private InventarioRepository inventarioRepository;

  public Integer getCountEntrySap(Integer codigoSapId) {
    Integer count = inventarioRepository.getCountEntrySap(codigoSapId);
    return count != null ? count : 0;
  }

  public Integer getCountInventorySap(Integer codigoSapId) {
    Integer count = inventarioRepository.getCountInventorySap(codigoSapId);
    return count != null ? count : 0;
  }

  public Integer getCountDiffSap(Integer codigoSapId) {
    Integer count = inventarioRepository.getCountDiffSap(codigoSapId);
    return count != null ? count : 0;
  }

  public Double getPercentDiffSap(Integer codigoSapId) {
    Double cumplimiento = inventarioRepository.getPercentDiffSap(codigoSapId);

    double percent = cumplimiento != null
        ? Math.round(cumplimiento * 10000d) / 100d
        : 0d;

    return percent;

  }

  public Integer getCountEntry() {
    return inventarioRepository.getCountEntry();
  }

  public Integer getCountInventory() {
    Integer count = inventarioRepository.getCountInventory();
    return count != null ? count : 0;
  }

  public Integer getCountDiff() {
    Integer count = inventarioRepository.getCountDiff();
    return count != null ? count : 0;
  }

  public Double getPercentDiff() {
    Double cumplimiento = inventarioRepository.getPercentDiff();

    double percent = cumplimiento != null
        ? Math.round(cumplimiento * 10000d) / 100d
        : 0d;

    return percent;
  }

  public Double getCountSurplus() {
    Double count = inventarioRepository.getCountSurplus();
    return count != null ? count : 0d;
  }

  public Integer deleteSerialInventory(String serial) {
    Integer count = inventarioRepository.deleteSerialInventory(serial);
    return count != null ? count : 0;
  }

  public InventarioModel getModel(String serial) {
    String usuario = inventarioRepository.getModel(serial);
    if (usuario == null) {
      return null;
    }
    InventarioModel model = new InventarioModel();
    model.setUsuario(usuario);
    return model;
  }

  public List<String> getListStateInventory() {
    return inventarioRepository.getListStateInventory();
  }

  public List<InventarioModel> getModelFinishInventory() {
    List<Object[]> results = inventarioRepository.getModelFinishInventory();
    List<InventarioModel> inventarios = new ArrayList<>();
    for (Object[] result : results) {
      InventarioModel inventario = new InventarioModel();
      inventario.setId((Integer) result[0]);
      inventario.setSerial((String) result[1]);
      inventario.setCodigoSap((String) result[2]);
      inventario.setCodigoSapReal((String) result[3]);
      inventario.setPallet((String) result[4]);
      inventario.setPalletReal((String) result[5]);
      inventario.setEstadoId((Integer) result[6]);
      inventario.setEstadoSap((String) result[7]);
      inventario.setEstadoRR((String) result[8]);
      inventario.setAjuste((String) result[9]);
      inventario.setFecha((String) result[10].toString());
      inventario.setUsuarioId((Integer) result[11]);
      inventario.setSerialId((Integer) result[12]);
      inventario.setMac((String) result[13]);
      inventario.setSobrante((Boolean) result[14]);
      inventarios.add(inventario);
    }
    return inventarios;
  }

  public Integer cleanTable() {
    try {
      return inventarioRepository.cleanTable();
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  public List<InventarioModel> searchInventory(String pallet) {
    List<Object[]> results = inventarioRepository.searchInventory(pallet);
    List<InventarioModel> inventarios = new ArrayList<>();
    for (Object[] result : results) {
      InventarioModel inventario = new InventarioModel();
      inventario.setSerial((String) result[0]);
      inventario.setEstado((String) result[1]);
      inventario.setCodigoSap((String) result[2]);
      inventario.setPallet((String) result[3]);
      inventario.setEstadoSap((String) result[4]);
      inventario.setEstadoRR((String) result[5]);
      inventario.setAjuste((String) result[6]);
      inventarios.add(inventario);
    }
    return inventarios;
  }

  public Integer create(InventarioModel inventario) {
    if (inventario == null) {
      return 0;
    }

    try {
      inventarioRepository.create(inventario.getSerial(), inventario.getCodigoSap(),
          inventario.getCodigoSapReal(), inventario.getPallet(),
          inventario.getPalletReal(), inventario.getEstadoId(),
          inventario.getEstadoSap(), inventario.getEstadoRR(),
          inventario.getAjuste(), inventario.getFecha(),
          inventario.getUsuarioId(),
          inventario.getSerialId(), inventario.getMac(),
          inventario.getSobrante());
      return 1;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }
}
