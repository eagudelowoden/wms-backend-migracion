package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PalletRepository extends BaseRepository<PalletModel, Integer> {

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.\"posicionId\" AS integer), f.posicion, CAST(f.\"tipologiaId\" AS integer), f.tipologia, CAST(f.\"codigoSapId\" AS integer), f.\"codigoSap\", f.descripcion, CAST(f.\"origenId\" AS integer), f.origen, CAST(f.\"destinoId\" AS integer), CAST(f.activo AS boolean), f.\"nombreUsuario\", CAST(f.\"loteId\" AS integer), f.fecha, CAST(f.multimodelo AS boolean), CAST(f.\"cantidadCaja\" AS integer), CAST(f.smartcard AS boolean) FROM pa_getmodelpallet(CAST(:id AS integer)) f", nativeQuery = true)
  List<Object[]> getPalletById(@Param("id") int id);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertpallet(CAST(:numero AS character varying), CAST(:posicionId AS integer), CAST(:codigoSapId AS integer), CAST(:tipologiaId AS integer), CAST(:origenId AS integer), CAST(:destinoId AS integer), CAST(:usuarioId AS integer), CAST(:loteId AS integer))", nativeQuery = true)
  void insertPallet(
      @Param("numero") String numero,
      @Param("posicionId") Integer posicionId,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("origenId") Integer origenId,
      @Param("destinoId") Integer destinoId,
      @Param("usuarioId") Integer usuarioId,
      @Param("loteId") Integer loteId);

  @Query(value = "SELECT numero FROM Pallet ORDER BY id DESC LIMIT 1", nativeQuery = true)
  String getLastInsertedPalletNumber();

  @Query(value = "SELECT CAST(f.\"Id\" AS integer), f.\"Numero\", f.\"Codigo\", f.\"Descripcion\", CAST(f.\"Cantidad\" AS integer), f.\"Tipologia\", f.\"Lote\" FROM pa_searchentrypallet(CAST(:numero AS character varying), CAST(:destino AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
  List<Object[]> searchEntry(
      @Param("numero") String numero,
      @Param("destino") String destino,
      @Param("usuarioId") int usuarioId);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, f.codigo, f.descripcion, CAST(f.cantidad AS integer), f.tipologia, f.lote FROM pa_searchaccesorypallet(CAST(:numero AS character varying), CAST(:destino AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
  List<Object[]> searchAccesory(
      @Param("numero") String numero,
      @Param("destino") String destino,
      @Param("usuarioId") int usuarioId);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, f.codigosap, f.descripcion, CAST(f.cantidad AS integer), f.tipologia, f.estado FROM pa_searchaccesorynovedadpallet(CAST(:numero AS character varying)) f", nativeQuery = true)
  List<Object[]> searchAccesoryNovedad(@Param("numero") String numero);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.origen, f.tipologia, f.lote FROM pa_searchtransitpallet(CAST(:numero AS character varying)) f", nativeQuery = true)
  List<Object[]> searchTransitPallet(@Param("numero") String numero);

  @Transactional
  @Query(value = "SELECT pa_sendpallet(CAST(:destinoId AS integer), CAST(:tipologiaId AS integer), CAST(:posicionId AS integer), CAST(:estado AS integer), CAST(:palletId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void sendPallet(
      @Param("destinoId") Integer destinoId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("posicionId") Integer posicionId,
      @Param("estado") Integer estado,
      @Param("palletId") Integer palletId,
      @Param("filas") Integer filas);

  @Query(value = "SELECT CAST(f.cantidad AS integer) FROM pa_getcountpallet(CAST(:palletId AS integer), CAST(:tabla AS character varying)) f", nativeQuery = true)
  Integer getCountPallet(
      @Param("palletId") Integer palletId,
      @Param("tabla") String tabla);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deletepallet(CAST(:palletId AS integer))", nativeQuery = true)
  void deletePallet(@Param("palletId") Integer palletId);

  @Query(value = "SELECT CAST(f.cajas AS integer) FROM pa_getboxpallet(CAST(:palletId AS integer), CAST(:tabla AS character varying)) f", nativeQuery = true)
  Integer getBoxPallet(@Param("palletId") Integer palletId, @Param("tabla") String tabla);

  @Transactional
  @Query(value = "SELECT pa_updatebatchpalletentry(CAST(:loteId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:palletId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void updateBatchPallet(
      @Param("palletId") int palletId,
      @Param("loteId") int loteId,
      @Param("usuarioIdMovimiento") int usuarioIdMovimiento,
      @Param("filas") int filas);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.origen, f.tipologia, f.\"CodigoSap\", f.\"Descripcion\" FROM pa_searchreceivepallet(CAST(:numero AS character varying), CAST(:destino AS character varying), CAST(:tipo AS character varying)) f", nativeQuery = true)
  List<Object[]> searchReceivePallet(
      @Param("numero") String numero,
      @Param("destino") String destino,
      @Param("tipo") String tipo);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.origen, f.tipologia, f.\"CodigoSap\", f.\"Descripcion\" FROM pa_searchpackingdeliverypallet(CAST(:numero AS character varying), CAST(:tipologia AS character varying), CAST(:tipo AS character varying)) f", nativeQuery = true)
  List<Object[]> SearchPackingDeliveryPallet(
      @Param("numero") String numero,
      @Param("tipologia") String tipologia,
      @Param("tipo") String tipo);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, f.cajas, f.tipologia, f.\"Descripcion\" FROM pa_searchpalletboxpallet(CAST(:numero AS character varying), CAST(:destino AS character varying)) f", nativeQuery = true)
  List<Object[]> SearchPalletBoxPallet(
      @Param("numero") String numero,
      @Param("destino") String destino);

  @Query(value = "SELECT CAST(f.id AS integer), f.\"Numero\", f.cajas, f.\"Descripcion\", f.tipologia FROM pa_searchpalletboxpalletweb(CAST(:numero AS character varying), CAST(:destino AS character varying)) f", nativeQuery = true)
  List<Object[]> SearchPalletBoxPalletWeb(
      @Param("numero") String numero,
      @Param("destino") String destino);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deleteaccesory(CAST(:palletId AS integer), CAST(:cantidad AS integer), CAST(:codigoSapId AS integer))", nativeQuery = true)
  Integer eliminarAccesorio(@Param("palletId") Integer palletId, @Param("cantidad") Integer cantidad,
      @Param("codigoSapId") Integer codigoSapId, @Param("usuarioId") Integer usuarioId);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.origen, f.tipologia, f.\"codigoSap\", f.descripcion, f.lote, f.posicion FROM pa_searchstoragepallet(CAST(:numero AS character varying), CAST(:tipo AS character varying), CAST(:tipoAccesorio AS character varying)) f", nativeQuery = true)
  List<Object[]> searchStoragePallet(@Param("numero") String numero, @Param("tipo") String tipo,
      @Param("tipoAccesorio") String tipoAccesorio);

  @Transactional
  @Query(value = "SELECT pa_updatepositionpallet(CAST(:palletId AS integer), CAST(:posicionId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void updatePosition(@Param("palletId") Integer palletId,
      @Param("posicionId") Integer posicionId, @Param("filas") Integer filas);

  @Transactional
  @Query(value = "SELECT pa_updatetipologypallet(CAST(:palletId AS integer), CAST(:tipologiaId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void updateTipologia(@Param("palletId") Integer palletId,
      @Param("tipologiaId") Integer tipologiaId, @Param("filas") Integer filas);

  // Inhabilitar pallet de empaque
  @Transactional
  @Query(value = "SELECT pa_innactivatepallet(CAST(:palletId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void innactivatePallet(@Param("palletId") int palletId, @Param("filas") int filas);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.\"codigoSap\", f.\"Descripcion\", f.tipologia, f.posicion, f.origen FROM pa_searchstoragegrouppalletaccesory(CAST(:numero AS character varying), CAST(:tipoAccesorio AS character varying)) f", nativeQuery = true)
  List<Object[]> searchStorageGroupPalletAccesory(@Param("numero") String numero,
      @Param("tipoAccesorio") String tipoAccesorio);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.\"codigoSap\", f.tipologia, f.posicion, f.origen, f.lote FROM pa_searchstoragegrouppalletaccesorycreated(CAST(:numero AS character varying), CAST(:tipoAccesorio AS character varying)) f", nativeQuery = true)
  List<Object[]> searchStorageGroupPalletAccesoryCreated(@Param("numero") String numero,
      @Param("tipoAccesorio") String tipoAccesorio);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.\"codigoSap\", f.\"Descripcion\", f.tipologia, f.posicion, f.origen, f.lote FROM pa_searchstoragegrouppallet(CAST(:numero AS character varying), CAST(:tipoEquipo AS character varying)) f", nativeQuery = true)
  List<Object[]> searchStorageGroupPallet(@Param("numero") String numero,
      @Param("tipoEquipo") String tipoEquipo);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_unifypallet(CAST(:palletId AS integer))", nativeQuery = true)
  Integer unifyPallet(@Param("palletId") Integer palletId);

  @Query(value = "SELECT f.numero FROM pa_getlistpallet(CAST(:destino AS character varying)) f", nativeQuery = true)
  List<Object[]> getListPallets(@Param("destino") String destino);

  @Query(value = "SELECT CAST(f.id AS integer) FROM pa_getidpallet(CAST(:numero AS character varying)) f", nativeQuery = true)
  Integer getIdPallet(@Param("numero") String numero);

  @Transactional
  @Query(value = "SELECT pa_updatesapcodepallet(CAST(:palletId AS integer), CAST(:codigoSapId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  Integer updateSapCodePallet(@Param("palletId") Integer palletId, @Param("codigoSapId") Integer codigoSapId,
      @Param("filas") Integer filas);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, f.cajas FROM pa_searchqualitydeliverypallet() f", nativeQuery = true)
  List<Object[]> searchQualityDeliveryPallet();

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, f.\"Codigo\", f.\"Descripcion\", f.cajas, CAST(f.\"Cantidad Equip.\" AS integer), CAST(f.\"Cantidad Acc.\" AS integer), CAST(f.\"Cantidad SmartC.\" AS integer), f.tipologia FROM pa_searchpalletboxdispatchpallet(CAST(:numero AS character varying)) f", nativeQuery = true)
  List<Object[]> searchPalletBoxDispatchPallet(@Param("numero") String numero);

  @Query(value = "SELECT 1 FROM pa_sendallpallet(p_destinoid => CAST(:destinoId AS integer), p_palletid => CAST(:palletId AS integer))", nativeQuery = true)
  void sendAllPallet(
      @Param("destinoId") Integer destinoId,
      @Param("palletId") Integer palletId);

  @Query(value = "SELECT " +
      "p.Id, " +
      "p.Numero, " +
      "(SELECT COUNT(*) FROM Inventario i WHERE i.pallet = p.Numero) AS Cantidad, " +
      "po.Numero AS Posicion, " +
      "p.CodigoSapId, " +
      "cs.Codigo AS CodigoSap, " +
      "cs.Descripcion AS DescSap " +
      "FROM Pallet p " +
      "LEFT JOIN Posicion po ON p.PosicionId = po.Id " +
      "LEFT JOIN CodigoSap cs ON cs.Id = p.CodigoSapId " +
      "WHERE p.OrigenId = 104 AND EstadoInventario=1 AND p.UsuarioId = ? " +
      "ORDER BY p.Id DESC", nativeQuery = true)
  List<Object[]> getPalletsInventory(@Param("usuarioId") Integer usuarioId);

  @Modifying
  @Transactional
  @Query(value = "UPDATE Pallet SET EstadoInventario=0 WHERE Id=:palletId", nativeQuery = true)
  Integer innactivatePalletInventory(@Param("palletId") Integer palletId);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.origen, f.tipologia FROM pa_searchreceivepartspallet(CAST(:destino AS character varying), CAST(:numero AS character varying)) f", nativeQuery = true)
  List<Object[]> searchReceivePartsPallet(@Param("destino") String destino, @Param("numero") String numero);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.posicion FROM pa_searchgeneralinventorypallet(CAST(:numero AS character varying), CAST(:estado AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
  List<Object[]> searchGeneralInventoryPallet(@Param("numero") String numero, @Param("estado") String estado,
      @Param("usuarioId") Integer usuarioId);

  @Transactional
  @Query(value = "SELECT pa_updatestateinventorypallet(CAST(:palletId AS integer), CAST(:estadoInventario AS integer), CAST(NULL AS integer))", nativeQuery = true)
  Integer updateStateInventoryPallet(@Param("palletId") Integer palletId,
      @Param("estadoInventario") Integer estadoInventario, @Param("filas") Integer filas);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.cantidad AS integer), f.posicion FROM pa_searchgeneralsettingspallet(CAST(:numero AS character varying), CAST(:estado AS character varying)) f", nativeQuery = true)
  List<Object[]> searchGeneralSettingsPallet(@Param("numero") String numero, @Param("estado") String estado);

  @Query(value = "SELECT CAST(f.\"Existe\" AS boolean) FROM pa_getpalletnumero(CAST(:palletNumero AS character varying)) f", nativeQuery = true)
  Boolean getPalletNumero(@Param("palletNumero") String palletNumero);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_enviarpallet(CAST(:palletId AS integer), CAST(:destinoId AS integer), CAST(:estadoId AS integer), CAST(:tipologiaId AS integer), CAST(:posicionId AS integer), CAST(:estado AS integer), CAST(:usuarioId AS integer), CAST(:opcion AS integer), CAST(NULL AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void enviarPallet(
      @Param("palletId") Integer palletId,
      @Param("destinoId") Integer destinoId,
      @Param("estadoId") Integer estadoId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("posicionId") Integer posicionId,
      @Param("estado") Integer estado,
      @Param("usuarioId") Integer usuarioId,
      @Param("opcion") Integer opcion,
      @Param("filasIngreso") Integer filasIngreso,
      @Param("filasPallet") Integer filasPallet);

  @Query(value = "SELECT f.codigo, CAST(f.cantidad AS integer) FROM pa_searchmodelspallet(CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> searchModels(@Param("palletId") Integer palletId);



  @Transactional
  @Query(value = "SELECT pa_sendmodelsentry(CAST(:estadoId AS integer), CAST(:usuarioId AS integer), CAST(:palletDestinoId AS integer), CAST(:palletId AS integer), CAST(:codigoSapId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void sendModels(
          @Param("estadoId")        Integer estadoId,
          @Param("usuarioId")       Integer usuarioId,
          @Param("palletDestinoId") Integer palletDestinoId,
          @Param("palletId")        Integer palletId,
          @Param("codigoSapId")     Integer codigoSapId
  );

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.\"posicionId\" AS integer), f.posicion, CAST(f.\"tipologiaId\" AS integer), f.tipologia, CAST(f.\"codigoSapId\" AS integer), f.\"codigoSap\", f.descripcion, CAST(f.\"origenId\" AS integer), f.origen, CAST(f.\"destinoId\" AS integer), CAST(f.activo AS boolean), f.\"nombreUsuario\" FROM pa_getmodelpalletbynumber(CAST(:numero AS character varying)) f", nativeQuery = true)
  List<Object[]> getModelByNumber(@Param("numero") String numero);

  @Query(value = "SELECT p.Id, p.CodigoSapId, cs.Codigo, p.LoteId, p.TipologiaId " +
      "FROM Pallet p LEFT JOIN CodigoSap cs ON cs.Id = p.CodigoSapId " +
      "WHERE UPPER(p.Numero) = UPPER(:numero)", nativeQuery = true)
  List<Object[]> getMassUploadConfigByNumber(@Param("numero") String numero);

  /**
   * Incrementa el consecutivo y retorna el número generado en una sola llamada.
   * Delega en pa_IncrementarConsecutivoPallet para evitar el bug de MERGE
   * que referenciaba la tabla target dentro del INSERT, causando updates
   * incorrectos en filas de otros minutos al superar el valor xx99.
   */
  @Query(value = "SELECT f.* FROM general.pa_incrementarconsecutivopallet(CAST(:minutoActual AS timestamp)) f", nativeQuery = true)
  String incrementarYObtenerConsecutivo(@Param("minutoActual") String minutoActual);

  boolean existsByNumero(String numero);
}