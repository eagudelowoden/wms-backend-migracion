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


}
