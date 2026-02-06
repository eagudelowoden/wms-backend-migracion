package com.woden.wms_backend.services.ClienteServices;

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

  public Integer deleteSerialInventory(String serial) {
    Integer count = inventarioRepository.deleteSerialInventory(serial);
    return count != null ? count : 0;
  }
}
