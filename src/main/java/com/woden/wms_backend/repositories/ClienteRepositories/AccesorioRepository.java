package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface AccesorioRepository extends BaseRepository<AccesorioModel, Integer> {
  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertaccesory(CAST(:codigoSapId AS integer), CAST(:tipoAccesorio AS character varying), CAST(:tipoOrigenId AS integer), CAST(:origenId AS integer), CAST(:palletId AS integer), CAST(:estadoId AS integer), CAST(:documento AS character varying), CAST(:observacion AS character varying), CAST(:guia AS character varying), CAST(:usuarioId AS integer), CAST(:caja AS integer), CAST(:prealertaId AS integer), CAST(:cruce AS boolean))", nativeQuery = true)
  void insertarAccesorio(@Param("codigoSapId") Integer codigoSapId, @Param("tipoAccesorio") String tipoAccesorio,
      @Param("tipoOrigenId") Integer tipoOrigenId, @Param("origenId") Integer origenId,
      @Param("palletId") Integer palletId, @Param("estadoId") Integer estadoId, @Param("documento") String documento,
      @Param("observacion") String observacion, @Param("guia") String guia, @Param("usuarioId") Integer usuarioId,
      @Param("caja") Integer caja, @Param("prealertaId") Integer prealertaId, @Param("cruce") Boolean cruce);

  @Query(value = "SELECT f.\"Codigo\", f.\"Descripcion\", f.\"TipoAccesorio\", f.origen, f.\"Documento\", f.\"Guia\", f.\"Caja\" FROM pa_searchaccesory(CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> buscarPorPalletId(@Param("palletId") int palletId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deleteaccesory(CAST(:palletId AS integer), CAST(:cantidad AS integer), CAST(:codigoSapId AS integer))", nativeQuery = true)
  Integer deleteAccesorio(@Param("palletId") Integer palletId, @Param("cantidad") Integer cantidad,
      @Param("codigoSapId") Integer codigoSapId);

  @Transactional
  @Query(value = "SELECT pa_sendaccesory(CAST(:palletId AS integer), CAST(:estadoId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void sendAccesory(@Param("palletId") Integer palletId, @Param("estadoId") Integer estadoId,
      @Param("filas") Integer filas);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_unifyaccesory(CAST(:palletIdDestino AS integer), CAST(:palletId AS integer))", nativeQuery = true)
  void unifyAccesory(@Param("palletIdDestino") Integer palletIdDestino, @Param("palletId") Integer palletId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_updatepalletaccesory(CAST(:accesorioId AS integer), CAST(:palletId AS integer))", nativeQuery = true)
  void updatePalletAccesory(@Param("accesorioId") Integer accesorioId, @Param("palletId") Integer palletId);

  @Query(value = "SELECT CAST(f.id AS integer), f.\"Codigo\", f.\"Descripcion\", f.\"Tipologia\" FROM pa_searchseparatepalletaccesory(CAST(:cantidad AS integer), CAST(:palletId AS integer), CAST(:PalletDestinoId AS integer)) f", nativeQuery = true)
  List<Object[]> searchSeparatePalletAccesory(@Param("cantidad") Integer cantidad, @Param("palletId") Integer palletId, @Param("PalletDestinoId") Integer PalletDestinoId);

  @Transactional
  @Query(value = "SELECT pa_updateserialaccesory(CAST(:serialNuevo AS character varying), CAST(:serialAnterior AS character varying), CAST(NULL AS integer))", nativeQuery = true)
  void UpdateSerialAccesory(@Param("serialNuevo") String serialNuevo, @Param("serialAnterior") String serialAnterior,
      @Param("filas") Integer filas);

    @Query(value = "SELECT f.codigo, f.descripcion, f.familia, f.\"Nuevos\", f.\"Reacondicionados\" FROM pa_searchpackingcodigosapaccesory(CAST(:estado AS character varying), CAST(:palletId AS integer), CAST(:codigoSapId AS integer)) f", nativeQuery = true)
  List<Object[]> searchPackingCodigoSapAccesory(@Param("estado") String estado, @Param("palletId") Integer palletId,
      @Param("codigoSapId") Integer codigoSapId);

  @Query(value = "SELECT f.\"SerialEmpaque\" FROM pa_getpackedaccesoriesserials(CAST(:codigoSap AS character varying), CAST(:tipoAccesorio AS character varying), CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> GetPackedAccesoriesSerials(@Param("codigoSap") String codigoSap,
      @Param("tipoAccesorio") String tipoAccesorio, @Param("palletId") Integer palletId);

  @Query(value = "SELECT f.codigo, f.descripcion, f.\"Nuevos\", f.\"Reacondicionados\" FROM pa_searchpackedaccesory(CAST(:estado AS character varying), CAST(:serial AS character varying)) f", nativeQuery = true)
  List<Object[]> SearchPackedAccesory(@Param("estado") String estado, @Param("serial") String serial);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_updatepackingaccesory(CAST(:codigoSap AS character varying), CAST(:tipo AS character varying), CAST(:estado AS character varying), CAST(:destinoId AS integer), CAST(:serial AS character varying), CAST(:palletId AS integer), CAST(:cantidad AS integer))", nativeQuery = true)
  int updatePackingAccesory(@Param("codigoSap") String codigoSap, @Param("tipo") String tipo,
      @Param("estado") String estado, @Param("destinoId") Integer destinoId, @Param("serial") String serial,
      @Param("palletId") Integer palletId, @Param("cantidad") Integer cantidad);

  @Query(value = "SELECT f.codigo, f.descripcion, f.familia, f.\"Nuevos\", f.\"Reacondicionados\" FROM pa_searchallpackedaccesory(CAST(:estado AS character varying)) f", nativeQuery = true)
  List<Object[]> SearchAllPackedAccesory(@Param("estado") String estado);

  @Query(value = "SELECT CAST(f.\"Id\" AS integer), CAST(f.\"CodigoSapid\" AS integer), f.\"TipoAccesorio\", CAST(f.\"TipoOrigenId\" AS integer), CAST(f.\"OrigenId\" AS integer), CAST(f.\"PalletId\" AS integer), CAST(f.\"EstadoLimpiezaId\" AS integer), f.\"Documento\", f.\"Observacion\", f.\"Guia\", f.\"Fecha\", f.\"SerialEmpaque\", CAST(f.\"Caja\" AS integer), f.\"fechaLimpieza\", f.\"fechaEmpaque\", CAST(f.\"UsuarioLimpiezaId\" AS integer) FROM pa_getmodeldispatchaccesory(CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> getModelDispatchAccesory(@Param("palletId") Integer palletId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_packoffpalletaccesory(p_palletid => CAST(:palletId AS integer))", nativeQuery = true)
  void packOffPalletAccesory(@Param("palletId") Integer palletId);

  @Query(value = "SELECT CAST(f.\"Id\" AS integer), CAST(f.\"CodigoSapid\" AS integer), f.\"TipoAccesorio\", CAST(f.\"TipoOrigenId\" AS integer), CAST(f.\"OrigenId\" AS integer), CAST(f.\"PalletId\" AS integer), CAST(f.\"EstadoLimpiezaId\" AS integer), f.\"Documento\", f.\"Observacion\", f.\"Guia\", CAST(f.\"Fecha\" AS timestamp), f.\"SerialEmpaque\", CAST(f.\"Caja\" AS integer), CAST(f.\"fechaLimpieza\" AS timestamp), CAST(f.\"fechaEmpaque\" AS timestamp) FROM pa_getmodeldispatchboxaccesory(CAST(:palletId AS integer), CAST(:cajaDespachoId AS integer)) f", nativeQuery = true)
  List<Object[]> getModelDispatchBoxAccesory(@Param("palletId") Integer palletId,
      @Param("cajaDespachoId") Integer cajaDespachoId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_packoffpalletaccesorybox(CAST(:palletId AS integer), CAST(:cajaDespachoId AS integer))", nativeQuery = true)
  void packOffPalletAccesoryBox(@Param("palletId") Integer palletId, @Param("cajaDespachoId") Integer cajaDespachoId);

  @Query(value = "SELECT f.codigo, f.descripcion, CAST(f.cantidad AS integer) FROM pa_searchgroupaccesory(CAST(:codigoSap AS character varying), CAST(:estado AS character varying), CAST(:tipo AS character varying)) f", nativeQuery = true)
  List<Object[]> searchGroupAccesory(
      @Param("codigoSap") String codigoSap,
      @Param("estado") String estado,
      @Param("tipo") String tipo);

  @Query(value = "SELECT f.\"Codigo\", f.\"Descripcion\", f.\"EstadoLimpieza\", f.\"UsuarioLimpieza\", CAST(f.\"CodigoSapId\" AS integer), CAST(f.\"EstadoLimpiezaId\" AS integer), CAST(f.\"UsuarioLimpiezaId\" AS integer) FROM pa_searchcleaningaccesory(CAST(:tipoAccesorio AS character varying), CAST(:codigoSap AS character varying)) f", nativeQuery = true)
  List<Object[]> searchCleaningAccesory(@Param("tipoAccesorio") String tipoAccesorio,
      @Param("codigoSap") String codigoSap);

  @Transactional
  @Query(value = "SELECT pa_updateaccesory(CAST(:cantidad AS integer), CAST(:destinoId AS integer), CAST(:estadoLimpiezaId AS integer), CAST(:codigo AS character varying), CAST(:estado AS character varying), CAST(:tipoAccesorio AS character varying), CAST(:UsuarioLimpiezaId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void updateAccesory(@Param("cantidad") Integer cantidad,
      @Param("destinoId") Integer destinoId,
      @Param("estadoLimpiezaId") Integer estadoLimpiezaId,
      @Param("codigo") String codigo,
      @Param("estado") String estado,
      @Param("tipoAccesorio") String tipoAccesorio,
      @Param("UsuarioLimpiezaId") Integer UsuarioLimpiezaId,
      @Param("filas") Integer filas);

  @Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.descripcion, f.\"estadoLimpieza\" FROM pa_searchprocessaccesory(CAST(:estadoLimpieza AS character varying)) f", nativeQuery = true)
  List<Object[]> searchProcessAccesory(
      @Param("estadoLimpieza") String estadoLimpieza);

  @Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.descripcion FROM pa_searchdeliveryaccesory(CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> searchDeliveryAccesory(@Param("palletId") Integer palletId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_updatedeliveryaccesory(CAST(:id AS integer), CAST(:destinoId AS integer), CAST(:palletId AS integer))", nativeQuery = true)
  void updateDeliveryAccesory(
      @Param("id") Integer id,
      @Param("destinoId") Integer destinoId,
      @Param("palletId") Integer palletId);

  @Transactional
  @Query(value = "SELECT pa_backcleanentry(CAST(:id AS character varying), CAST(NULL AS integer))", nativeQuery = true)
  void BackCleanEntry(@Param("id") Integer id, @Param("filas") Integer filas);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_updatenoveltyallaccesory(CAST(:palletId AS integer), CAST(:estadoId AS integer), CAST(:observaciones AS character varying), CAST(:novedad AS character varying), CAST(:usuarioId AS integer))", nativeQuery = true)
  void updateNoveltyAllAccesory(
      @Param("palletId") Integer palletId,
      @Param("estadoId") Integer estadoId,
      @Param("observaciones") String observaciones,
      @Param("novedad") String novedad,
      @Param("usuarioId") Integer usuarioId);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, f.codigosap, f.descripcion, CAST(f.cantidad AS integer), f.tipologia, f.novedad FROM pa_searchnoveltyaccesorypallet(CAST(:tipoNovedad AS character varying)) f", nativeQuery = true)
  List<Object[]> searchNoveltyAccesoryPallet(@Param("tipoNovedad") String tipoNovedad);

  @Query(value = "SELECT CAST(f.palletid AS integer), CAST(f.codigosapid AS integer), f.codigo, f.descripcion, f.tipoaccesorio, f.origen, f.documento, f.guia, f.caja FROM pa_searchnoveltyaccesory(CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> searchNoveltyAccesory(@Param("palletId") Integer palletId);

  @Transactional
  @Query(value = "SELECT pa_updateaccesorynovedad(CAST(:palletId AS integer), CAST(:codigoSapId AS integer), CAST(:usuarioId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void updateAccesoryNovedad(
      @Param("palletId") Integer palletId,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("usuarioId") Integer usuarioId,
      @Param("filas") Integer filas);

  @Query(value = "SELECT CAST(f.cantidad AS integer) FROM pa_countavailableaccesory(CAST(:codigoSapId AS integer)) f", nativeQuery = true)
  Integer countAvailableAccesory(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "SELECT CAST(f.resultado AS integer) FROM pa_updateaccesorynovedadcantidad(CAST(:palletId AS integer), CAST(:codigoSapId AS integer), CAST(:cantidad AS integer), CAST(:accion AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
  Integer updateAccesoryNovedadCantidad(
      @Param("palletId") Integer palletId,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("cantidad") Integer cantidad,
      @Param("accion") String accion,
      @Param("usuarioId") Integer usuarioId);

}
