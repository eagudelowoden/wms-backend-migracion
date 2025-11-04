package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.DespachoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.DespachoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class DespachoService extends BaseService<DespachoModel, Integer> {
  @Autowired
  private DespachoRepository repository;

  public Integer insertDispatch(Integer id, String serial, String mac, Integer codigoSapId, Integer palletId,
      Integer palletIdIngreso, Integer cajaDespachoId, Integer estadoId, Integer tipoOrigenId, Integer origenId,
      Integer tipologiaId, Integer nivelId, String tramite, String documento, String guia, String falla,
      Integer prealertaId, Integer cruce, String novedad, Integer usuarioId, String fecha,
      String pedidoSap, Integer smartCardId, String smartCard, Integer loteId, String serial3, Integer cajaIngresoId,
      String numeroSmartcard, Integer fallaCosmeticaId, Integer fallaFuncionalId) {
    try {
      repository.insertDispatch(id, serial, mac, codigoSapId, palletId, palletIdIngreso, cajaDespachoId, estadoId,
          tipoOrigenId, origenId, tipologiaId, nivelId, tramite, documento, guia, falla, prealertaId, cruce, novedad,
          usuarioId, fecha, pedidoSap, smartCardId, smartCard, loteId, serial3, cajaIngresoId, numeroSmartcard,
          fallaCosmeticaId, fallaFuncionalId);
      return 1;
    } catch (Exception e) {
      System.out.println(e);
      return 0;
    }
  }

  public Integer insertDispatchAccesory(Integer id, Integer codigoSapId, String tipoAccesorio, Integer tipoOrigenId,
      Integer origenId, Integer palletId, Integer estadoLimpiezaId, String documento, String observacion, String guia,
      String fecha, Integer usuarioId, Integer caja, String fechaLimpieza, String fechaEmpaque,
      Integer usuarioLimpiezaId) {
    try {
      repository.insertDispatchAccesory(id, codigoSapId, tipoAccesorio, tipoOrigenId, origenId, palletId,
          estadoLimpiezaId, documento, observacion, guia, fecha, usuarioId, caja, fechaLimpieza, fechaEmpaque,
          usuarioLimpiezaId);
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }
}
