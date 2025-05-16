package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface IngresoRepository extends BaseRepository<IngresoModel, Integer> {
  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertEntry :serial, :mac, :serial3, :serial4, :serial5, :codigoSapId, :palletId, :estadoId, "
      +
      ":tipoOrigenId, :origenId, :tipologiaId, :nivelId, :tramite, :documento, :guia, :caja, :falla, :tecnicoCliente, "
      +
      ":prealertaId, :cruce, :novedad, :garantiaFabricante, :usuarioId, :observaciones, :estadoCliente, :loteId, "
      +
      ":cajaIngresoId, :modeloId", nativeQuery = true)
  void insertIngreso(
      @Param("serial") String serial,
      @Param("mac") String mac,
      @Param("serial3") String serial3,
      @Param("serial4") String serial4,
      @Param("serial5") String serial5,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("palletId") Integer palletId,
      @Param("estadoId") Integer estadoId,
      @Param("tipoOrigenId") Integer tipoOrigenId,
      @Param("origenId") Integer origenId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("nivelId") Integer nivelId,
      @Param("tramite") String tramite,
      @Param("documento") String documento,
      @Param("guia") String guia,
      @Param("caja") Integer caja,
      @Param("falla") String falla,
      @Param("tecnicoCliente") String tecnicoCliente,
      @Param("prealertaId") Integer prealertaId,
      @Param("cruce") Integer cruce,
      @Param("novedad") String novedad,
      @Param("garantiaFabricante") Integer garantiaFabricante,
      @Param("usuarioId") Integer usuarioId,
      @Param("observaciones") String observaciones,
      @Param("estadoCliente") String estadoCliente,
      @Param("loteId") Integer loteId,
      @Param("cajaIngresoId") Integer cajaIngresoId,
      @Param("modeloId") Integer modeloId);

  @Modifying
  @Query(value = "EXEC pa_DeleteEntry :serial", nativeQuery = true)
  void eliminarIngresos(@Param("serial") String serial);

  @Query(value = "EXEC pa_GetModelEntry :serial", nativeQuery = true)
  List<Object[]> searchIngreso(@Param("serial") String serial);

  @Query(value = "EXEC pa_SearchEntryReingreso :palletId", nativeQuery = true)
  List<Object[]> searchEntryReingreso(@Param("palletId") Integer palletId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_SendEntry :estadoId, :tipologiaId, :usuarioId, :palletId, :opcion, :filas OUT", nativeQuery = true)
  void sendIngreso(
      @Param("estadoId") Integer estadoId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("usuarioId") Integer usuarioId,
      @Param("palletId") Integer palletId,
      @Param("opcion") Integer opcion,
      @Param("filas") Integer filas);

  @Query(value = "EXEC pa_SearchTransitEntry :palletId", nativeQuery = true)
  List<Object[]> searchIngresoTransito(@Param("palletId") Integer palletId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdateSapCodeEntry :codigoSapId, :usuarioIdMovimiento, :serial, :filas OUT", nativeQuery = true)
  Integer updateSapCode(
      @Param("codigoSapId") int codigoSapId,
      @Param("usuarioIdMovimiento") int usuarioIdMovimiento,
      @Param("serial") String serial,
      @Param("filas") int filas);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdateBatchSerialEntry :loteId, :usuarioIdMovimiento, :serial, :filas OUT", nativeQuery = true)
  void updateBatchSerial(
      @Param("loteId") int loteId,
      @Param("usuarioIdMovimiento") int usuarioIdMovimiento,
      @Param("serial") String serial,
      @Param("filas") int filas);

  @Query(value = "EXEC pa_GetSerialByMac :mac", nativeQuery = true)
  List<Object[]> getSerialByMac(@Param("mac") String mac);

  @Query(value = "DECLARE @result INT; EXEC pa_Reingresos :serial, @result OUTPUT; SELECT @result", nativeQuery = true)
  Integer getReingresos(@Param("serial") String serial);

  @Query(value = "EXEC pa_GetProactiveRepair :serial, :filas OUT", nativeQuery = true)
  Integer getProactiveRepair(@Param("serial") String serial, @Param("filas") Integer filas);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_UpdateSapCodeEntry :codigoSapId, :usuarioIdMovimiento, :serial, :filas OUT", nativeQuery = true)
  Integer updateBatchPallet(
      @Param("codigoSapId") int codigoSapId,
      @Param("usuarioIdMovimiento") int usuarioIdMovimiento,
      @Param("loteId") String serial,
      @Param("filas") int filas);
}
