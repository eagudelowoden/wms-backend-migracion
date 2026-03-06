package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.EnsambleModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface EnsambleRepository extends BaseRepository<EnsambleModel, Integer> {
  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertAssemble :serialId, :serial, :mac, :serial3, :serial4, :serial5, :codigoSapId, :palletId, :estadoId, :tipologiaId, :nivelId, :usuarioId, :usuarioIdAsignado, :loteId, :smartCard", nativeQuery = true)
  void insertAssemble(
      @Param("serialId") Integer serialId,
      @Param("serial") String serial,
      @Param("mac") String mac,
      @Param("serial3") String serial3,
      @Param("serial4") String serial4,
      @Param("serial5") String serial5,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("palletId") Integer palletId,
      @Param("estadoId") Integer estadoId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("nivelId") Integer nivelId,
      @Param("usuarioId") Integer usuarioId,
      @Param("usuarioIdAsignado") Integer usuarioIdAsignado,
      @Param("loteId") Integer loteId,
      @Param("smartCard") String smartCard);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteAssemble :ensamble", nativeQuery = true)
  void deleteAssemble(@Param("ensamble") String ensamble);

  @Query(value = "EXEC pa_GetAssembledUser :usuarioId", nativeQuery = true)
  List<Object[]> getAssembleUser(@Param("usuarioId") Integer usuarioId);
}
