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

  @Query(value = "EXEC pa_GetModelPallet :id", nativeQuery = true)
  List<Object[]> getPalletById(@Param("id") int id);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertPallet :numero, :posicionId, :codigoSapId, :tipologiaId, :origenId, :destinoId, :usuarioId, :loteId", nativeQuery = true)
  void insertPallet(
      @Param("numero") String numero,
      @Param("posicionId") Integer posicionId,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("origenId") Integer origenId,
      @Param("destinoId") Integer destinoId,
      @Param("usuarioId") Integer usuarioId,
      @Param("loteId") Integer loteId);

  @Query(value = "SELECT TOP 1 numero FROM Pallet ORDER BY id DESC", nativeQuery = true)
  String getLastInsertedPalletNumber();

  @Query(value = "EXEC pa_SearchEntryPallet :numero, :destino, :usuarioId", nativeQuery = true)
  List<Object[]> searchEntry(
      @Param("numero") String numero,
      @Param("destino") String destino,
      @Param("usuarioId") int usuarioId);

  @Query(value = "EXEC pa_SearchAccesoryPallet :numero, :destino, :usuarioId", nativeQuery = true)
  List<Object[]> searchAccesory(
      @Param("numero") String numero,
      @Param("destino") String destino,
      @Param("usuarioId") int usuarioId);

  @Query(value = "EXEC pa_SearchTransitPallet :numero", nativeQuery = true)
  List<Object[]> searchTransitPallet(@Param("numero") String numero);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_SendPallet :destinoId, :tipologiaId, :posicionId, :estado, :palletId, :filas OUT", nativeQuery = true)
  void sendPallet(
      @Param("destinoId") Integer destinoId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("posicionId") Integer posicionId,
      @Param("estado") Integer estado,
      @Param("palletId") Integer palletId,
      @Param("filas") Integer filas);

  @Query(value = "EXEC pa_GetCountPallet :palletId, :tabla", nativeQuery = true)
  Integer getCountPallet(
      @Param("palletId") Integer palletId,
      @Param("tabla") String tabla);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeletePallet :palletId", nativeQuery = true)
  void deletePallet(@Param("palletId") Integer palletId);

  @Query(value = "EXEC pa_GetBoxPallet :palletId, :tabla", nativeQuery = true)
  Integer getBoxPallet(@Param("palletId") Integer palletId, @Param("tabla") String tabla);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdateBatchPalletEntry :loteId, :usuarioIdMovimiento, :palletId, :filas OUT", nativeQuery = true)
  void updateBatchPallet(
      @Param("palletId") int palletId,
      @Param("loteId") int loteId,
      @Param("usuarioIdMovimiento") int usuarioIdMovimiento,
      @Param("filas") int filas);

  @Query(value = "EXEC pa_SearchReceivePallet :numero, :destino, :tipo", nativeQuery = true)
  List<Object[]> searchReceivePallet(
      @Param("numero") String numero,
      @Param("destino") String destino,
      @Param("tipo") String tipo);

  @Query(value = "EXEC pa_SearchPackingDeliveryPallet :numero, :tipologia, :tipo", nativeQuery = true)
  List<Object[]> SearchPackingDeliveryPallet(
      @Param("numero") String numero,
      @Param("tipologia") String tipologia,
      @Param("tipo") String tipo);

  @Query(value = "EXEC pa_SearchPalletBoxPallet :numero, :destino", nativeQuery = true)
  List<Object[]> SearchPalletBoxPallet(
      @Param("numero") String numero,
      @Param("destino") String destino);

  @Query(value = "EXEC pa_SearchPalletBoxPalletWeb :numero, :destino", nativeQuery = true)
  List<Object[]> SearchPalletBoxPalletWeb(
      @Param("numero") String numero,
      @Param("destino") String destino);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteAccesory :palletId, :cantidad, :codigoSapId, :usuarioId", nativeQuery = true)
  Integer eliminarAccesorio(@Param("palletId") Integer palletId, @Param("cantidad") Integer cantidad,
      @Param("codigoSapId") Integer codigoSapId, @Param("usuarioId") Integer usuarioId);

  @Query(value = "EXEC pa_SearchStoragePallet :numero, :tipo, :tipoAccesorio", nativeQuery = true)
  List<Object[]> searchStoragePallet(@Param("numero") String numero, @Param("tipo") String tipo,
      @Param("tipoAccesorio") String tipoAccesorio);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdatePositionPallet :palletId, :posicionId, :filas OUT", nativeQuery = true)
  void updatePosition(@Param("palletId") Integer palletId,
      @Param("posicionId") Integer posicionId, @Param("filas") Integer filas);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdateTipologyPallet :palletId, :tipologiaId, :filas OUT", nativeQuery = true)
  void updateTipologia(@Param("palletId") Integer palletId,
      @Param("tipologiaId") Integer tipologiaId, @Param("filas") Integer filas);

  // Inhabilitar pallet de empaque
  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InnactivatePallet :palletId, :filas OUT", nativeQuery = true)
  void innactivatePallet(@Param("palletId") int palletId, @Param("filas") int filas);

  @Query(value = "EXEC pa_SearchStorageGroupPalletAccesory :numero, :tipoAccesorio", nativeQuery = true)
  List<Object[]> searchStorageGroupPalletAccesory(@Param("numero") String numero,
      @Param("tipoAccesorio") String tipoAccesorio);

  @Query(value = "EXEC pa_SearchStorageGroupPalletAccesoryCreated :numero, :tipoAccesorio", nativeQuery = true)
  List<Object[]> searchStorageGroupPalletAccesoryCreated(@Param("numero") String numero,
      @Param("tipoAccesorio") String tipoAccesorio);

  @Query(value = "EXEC pa_SearchStorageGroupPallet :numero, :tipoEquipo", nativeQuery = true)
  List<Object[]> searchStorageGroupPallet(@Param("numero") String numero,
      @Param("tipoEquipo") String tipoEquipo);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UnifyPallet :palletId", nativeQuery = true)
  Integer unifyPallet(@Param("palletId") Integer palletId);

  @Query(value = "EXEC pa_GetListPallet :destino", nativeQuery = true)
  List<Object[]> getListPallets(@Param("destino") String destino);

  @Query(value = "pa_GetIdPallet :numero", nativeQuery = true)
  Integer getIdPallet(@Param("numero") String numero);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_updateSapCodePallet :palletId, :codigoSapId, :filas OUT", nativeQuery = true)
  Integer updateSapCodePallet(@Param("palletId") Integer palletId, @Param("codigoSapId") Integer codigoSapId,
      @Param("filas") Integer filas);

  @Query(value = "EXEC pa_SearchQualityDeliveryPallet", nativeQuery = true)
  List<Object[]> searchQualityDeliveryPallet();

  @Query(value = "EXEC pa_SearchPalletBoxDispatchPallet :numero", nativeQuery = true)
  List<Object[]> searchPalletBoxDispatchPallet(@Param("numero") String numero);

  @Procedure(procedureName = "pa_SendAllPallet")
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

  @Query(value = "EXEC pa_SearchReceivePartsPallet :destino, :numero", nativeQuery = true)
  List<Object[]> searchReceivePartsPallet(@Param("destino") String destino, @Param("numero") String numero);

  @Query(value = "EXEC pa_SearchGeneralInventoryPallet :numero, :estado, :usuarioId", nativeQuery = true)
  List<Object[]> searchGeneralInventoryPallet(@Param("numero") String numero, @Param("estado") String estado,
      @Param("usuarioId") Integer usuarioId);

  @Transactional
  @Modifying
  @Query(value = "EXEC pa_UpdateStateInventoryPallet :palletId, :estadoInventario, :filas OUT", nativeQuery = true)
  Integer updateStateInventoryPallet(@Param("palletId") Integer palletId,
      @Param("estadoInventario") Integer estadoInventario, @Param("filas") Integer filas);

  @Query(value = "EXEC pa_SearchGeneralSettingsPallet :numero, :estado", nativeQuery = true)
  List<Object[]> searchGeneralSettingsPallet(@Param("numero") String numero, @Param("estado") String estado);

  @Query(value = "EXEC pa_GetPalletNumero :palletNumero", nativeQuery = true)
  Boolean getPalletNumero(@Param("palletNumero") String palletNumero);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_EnviarPallet :palletId, :destinoId, :estadoId, :tipologiaId, :posicionId, :estado, :usuarioId, :opcion, :filasIngreso OUT, :filasPallet OUT", nativeQuery = true)
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

  /**
   * Incrementa el consecutivo y retorna el número generado en una sola llamada.
   * Delega en pa_IncrementarConsecutivoPallet para evitar el bug de MERGE
   * que referenciaba la tabla target dentro del INSERT, causando updates
   * incorrectos en filas de otros minutos al superar el valor xx99.
   */
  @Query(value = "EXEC WmsWdGeneral.dbo.pa_IncrementarConsecutivoPallet :minutoActual",
      nativeQuery = true)
  String incrementarYObtenerConsecutivo(@Param("minutoActual") String minutoActual);
}