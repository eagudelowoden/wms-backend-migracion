package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
  int getBoxCount(@Param("palletId") Integer palletId, @Param("tabla") String tabla);

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

    @Query(value = "EXEC pa_SearchPalletBoxPallet :numero, :destino", nativeQuery = true)
    List<Object[]> SearchPalletBoxPallet(
            @Param("numero") String numero,
            @Param("destino") String destino);

    
    @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteAccesory :palletId, :cantidad, :codigoSapId", nativeQuery = true)
  Integer eliminarAccesorio(@Param("palletId") Integer palletId, @Param("cantidad") Integer cantidad,
      @Param("codigoSapId") Integer codigoSapId);

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

    //Inhabilitar pallet de empaque
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
  Integer updateSapCodePallet(@Param("palletId") Integer palletId, @Param("codigoSapId") Integer codigoSapId, @Param("filas") Integer filas);
}