package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.models.Entity.AccesorioModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.DespachoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.DespachoRepository;
import com.woden.wms_backend.services.BaseService;

import java.util.List;

@Service
public class DespachoService extends BaseService<DespachoModel, Integer> {
  @Autowired
  private DespachoRepository repository;

  public Integer insertDispatch(Integer id, String serial, String mac, Integer codigoSapId, Integer palletId,
      Integer palletIdIngreso, Integer cajaDespachoId, Integer estadoId, Integer tipoOrigenId, Integer origenId,
      Integer tipologiaId, Integer nivelId, String tramite, String documento, String guia, String falla,
      Integer prealertaId, Integer cruce, String novedad, Integer usuarioId, String fecha,
      String pedidoSap, Integer smartCardId, String smartCard, Integer loteId, String serial3, Integer cajaIngresoId,
      String numeroSmartcard, Integer fallaCosmeticaId, Integer fallaFuncionalId, String causa,
      String observaciones) {
    try {
      repository.insertDispatch(id, serial, mac, codigoSapId, palletId, palletIdIngreso, cajaDespachoId, estadoId,
          tipoOrigenId, origenId, tipologiaId, nivelId, tramite, documento, guia, falla, prealertaId, cruce, novedad,
          usuarioId, fecha, pedidoSap, smartCardId, smartCard, loteId, serial3, cajaIngresoId, numeroSmartcard,
          fallaCosmeticaId,fallaFuncionalId ,causa, observaciones);
      return 1;
    } catch (Exception e) {
      System.out.println(e);
      return 0;
    }
  }

  public Integer insertDispatchAccesory(
          List<AccesorioModel> accesorios,
          Integer estadoId,
          Integer usuarioId,
          String pedidoSap) {

    return repository.insertDispatchAccesoryBatch(
            accesorios,
            estadoId,
            usuarioId,
            pedidoSap
    );
  }

//  public Integer insertDispatchAccesory(Integer id, Integer codigoSapId, String tipoAccesorio,
//      Integer tipoOrigenId, Integer origenId, Integer palletId, Integer estadoId,
//      Integer estadoLimpiezaId, String documento, String observacion, String guia,
//      Integer usuarioId, String fechaIngreso, String serialEmpaque, Integer caja,
//      String pedidoSap, String fechaLimpieza, String fechaEmpaque, Integer usuarioLimpiezaId) {
//    try {
//      repository.insertDispatchAccesory(
//          id,
//          codigoSapId,
//          tipoAccesorio,
//          tipoOrigenId,
//          origenId,
//          palletId,
//          estadoId, // AGREGADO
//          estadoLimpiezaId,
//          documento,
//          observacion,
//          guia,
//          usuarioId,
//          fechaIngreso, // AGREGADO
//          serialEmpaque, // AGREGADO
//          caja,
//          pedidoSap, // AGREGADO
//          fechaLimpieza,
//          fechaEmpaque,
//          usuarioLimpiezaId);
//      return 1;
//    } catch (Exception e) {
//      e.printStackTrace();
//      return 0;
//    }
//  }
}
