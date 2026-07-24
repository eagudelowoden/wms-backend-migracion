package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.WorkFlowModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface WorkFlowRepository  extends BaseRepository<WorkFlowModel, Integer> {

  @Query(value = "EXEC pa_GetLevelIdWorflow :opcion, :origen, :descripcion, :tipologiaId", nativeQuery = true)
  Integer getLevelId(
      @Param("opcion") String opcion,
      @Param("origen") String origen,
      @Param("descripcion") String descripcion,
      @Param("tipologiaId") int tipologiaId);

  @Query(value = "EXEC pa_SearchWorflow", nativeQuery = true)
  List<Object[]> search();

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertWorflow :moduloId, :origenId, :opcionId, :tipologiaId, :nivelId", nativeQuery = true)
  void create(@Param("moduloId") int moduloId, @Param("origenId") int origenId,
              @Param("opcionId") int opcionId, @Param("tipologiaId") int tipologiaId,
              @Param("nivelId") int nivelId);

  @Modifying
  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateWorflow :moduloId, :origenId, :opcionId, :tipologiaId, :nivelId, :id, @Filas OUTPUT", nativeQuery = true)
  void updateWorkflow(@Param("moduloId") int moduloId, @Param("origenId") int origenId,
              @Param("opcionId") int opcionId, @Param("tipologiaId") int tipologiaId,
              @Param("nivelId") int nivelId, @Param("id") int id);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteWorflow :id", nativeQuery = true)
  void deleteWorkflow(@Param("id") int id);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO WmsWdGeneral.dbo.WorkflowLogs (UsuarioModifica, Cliente, Descripcion, Fecha) VALUES (:usuario, :cliente, :descripcion, GETDATE())", nativeQuery = true)
  void insertLog(@Param("usuario") String usuario, @Param("cliente") String cliente, @Param("descripcion") String descripcion);
}