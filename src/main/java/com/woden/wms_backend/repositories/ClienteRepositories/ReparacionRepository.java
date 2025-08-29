package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.ReparacionModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface ReparacionRepository extends BaseRepository<ReparacionModel, Integer> {
  @Query(value = "EXEC pa_GetHistoricRepair :serial", nativeQuery = true)
  List<Object[]> getHistoricRepair(
      @Param("serial") String serial);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertRepair :serialId, :serial, :mac, :codigoSapId, :estadoFinalId, :fallaDxId, :tecnicoAsignacionId, :fechaAsignacion, :usuarioId", nativeQuery = true)
  void create
          (@Param("serialId") Integer serialId,
      @Param("serial") String serial,
      @Param("mac") String mac,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("estadoFinalId") Integer estadoFinalId,
      @Param("fallaDxId") Integer fallaDxId,
      @Param("tecnicoAsignacionId") Integer tecnicoAsignacionId,
      @Param("fechaAsignacion") String fechaAsignacion,
      @Param("usuarioId") Integer usuarioId);

  @Query(value = "EXEC pa_GetLastRepair :serial", nativeQuery = true)
  List<Object[]> getLastRepair(@Param("serial") String serial);

  @Query(value = "EXEC pa_GetRepairUser :usuarioId", nativeQuery = true)
  List<Object[]> getRepairUser(@Param("usuarioId") Integer usuarioId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteRepair :serial", nativeQuery = true)
  void deleteRepair(@Param("serial") String serial);

  @Query(value = "EXEC pa_SearchAssignedRepair :tecnicoAsignacionId, :tipo", nativeQuery = true)
  List<Object[]> searchAssignedRepair(@Param("tecnicoAsignacionId") Integer tecnicoAsignacionId,
      @Param("tipo") String tipo);

  @Query(value = "EXEC pa_SearchRepairedRepair :tecnicoReparacionId, :estado", nativeQuery = true)
  List<Object[]> searchRepairedRepair(@Param("tecnicoReparacionId") Integer tecnicoReparacionId,
      @Param("estado") String estado);

  @Query(value = "EXEC pa_GetAssignedTechnicianRepair :serial", nativeQuery = true)
  List<Object[]> getAssignedTechnicianRepair(@Param("serial") String serial);
}
