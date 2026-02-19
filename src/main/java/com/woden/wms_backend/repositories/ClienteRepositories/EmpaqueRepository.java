package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EmpaqueRepository extends BaseRepository<EmpaqueModel, Integer> {

  @Procedure(procedureName = "pa_InsertPacking")
  Integer createInsert(
      @Param("SerialId") Integer serialId,
      @Param("Serial") String serial,
      @Param("Mac") String mac,
      @Param("CodigoSapId") Integer codigoSapId,
      @Param("PalletId") Integer palletId,
      @Param("CajaEmpaqueId") Integer cajaEmpaqueId,
      @Param("NivelId") Integer nivelId,
      @Param("UsuarioId") Integer usuarioId,
      @Param("Fecha") LocalDateTime fecha,
      @Param("LoteId") Integer loteId,
      @Param("SmartCardId") Integer smartCardId,
      @Param("SmartCard") String smartCard);

  @Transactional
  @Query(value = "DECLARE @FilasOut INT; " +
          "EXEC [dbo].[pa_InsertPackingWebD] " +
          ":SerialId, :Serial, :Mac, :CodigoSapId, :PalletId, :CajaEmpaqueId, " +
          ":NivelId, :UsuarioId, :Fecha, :LoteId, :SmartCardId, :SmartCard, " +
          "@FilasOut OUTPUT; " +
          "SELECT @FilasOut;", nativeQuery = true)
  Integer executeInsertPacking(
          @Param("SerialId") Integer serialId,
          @Param("Serial") String serial,
          @Param("Mac") String mac,
          @Param("CodigoSapId") Integer codigoSapId,
          @Param("PalletId") Integer palletId,
          @Param("CajaEmpaqueId") Integer cajaEmpaqueId,
          @Param("NivelId") Integer nivelId,
          @Param("UsuarioId") Integer usuarioId,
          @Param("Fecha") LocalDateTime fecha,
          @Param("LoteId") Integer loteId,
          @Param("SmartCardId") Integer smartCardId,
          @Param("SmartCard") String smartCard
  );

  @Procedure(procedureName = "pa_DeletePacking")
  void eliminarSeriesEmpaque(@Param("serial") String serial);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdatePackingSmartCard :smartCardId, :smartCardNuevo, :serial, :filas OUT", nativeQuery = true)
  void updateSmartCard(
      @Param("smartCardId") Integer smartCardId,
      @Param("smartCardNuevo") String smartCardNuevo,
      @Param("serial") String serial,
      @Param("filas") Integer filas);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdatePacking :serialId, :serialNuevo, :mac, :serialAnterior ,:filas OUT", nativeQuery = true)
  void UpdatePacking(
      @Param("serialId") Integer serialId,
      @Param("serialNuevo") String serialNuevo,
      @Param("mac") String mac,
      @Param("serialAnterior") String serialAnterior,
      @Param("filas") Integer filas);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdatePackingWeb :serialId, :serialNuevo, :nivelNuevo, :mac, :serialAnterior, :filas OUT", nativeQuery = true)
  void UpdatePackingWeb(
      @Param("serialId") Integer serialId,
      @Param("serialNuevo") String serialNuevo,
      @Param("nivelNuevo") Integer nivelNuevo,
      @Param("mac") String mac,
      @Param("serialAnterior") String serialAnterior,
      @Param("filas") Integer filas);

}
