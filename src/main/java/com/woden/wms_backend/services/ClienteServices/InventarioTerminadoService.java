package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.InventarioModel;
import com.woden.wms_backend.models.Entity.InventarioTerminadoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.InventarioTerminadoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class InventarioTerminadoService extends BaseService<InventarioTerminadoModel, Integer> {
  @Autowired
  private InventarioTerminadoRepository inventarioTerminadoRepository;

  public Integer create(List<InventarioModel> inventario, Integer usuarioIdTerminado, String fechaTerminado,
      String documento) {
    if (inventario == null || inventario.isEmpty()) {
      return 0;
    }

    try {
      for (InventarioModel inventarioModel : inventario) {
        inventarioTerminadoRepository.create(inventarioModel.getSerial(), inventarioModel.getCodigoSap(),

            inventarioModel.getCodigoSapReal(), inventarioModel.getPallet(),
            inventarioModel.getPalletReal(), inventarioModel.getEstadoId(),
            inventarioModel.getEstadoSap(), inventarioModel.getEstadoRR(),
            inventarioModel.getAjuste(), inventarioModel.getFecha(),
            inventarioModel.getUsuarioId(), fechaTerminado,
            usuarioIdTerminado, documento,
            inventarioModel.getSerialId(), inventarioModel.getMac(),
            inventarioModel.getSobrante());
      }
      return 1;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  public Integer insertInventarioFaltante(Integer usuarioIdTerminado, String documento) {
    Integer filas = 4;
    try {
      inventarioTerminadoRepository.insertInventarioFaltante(usuarioIdTerminado, documento, filas);
      return 1;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }
}
