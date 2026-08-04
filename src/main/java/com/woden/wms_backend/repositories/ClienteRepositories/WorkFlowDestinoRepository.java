package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.WorkFlowDestinoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface WorkFlowDestinoRepository extends BaseRepository<WorkFlowDestinoModel, Integer> {

  @Query(value = "EXEC pa_GetIdWorflowDestiny :opcion, :origen, :descripcion, :tipologiaId", nativeQuery = true)
  List<Integer> getIdDestino(
      @Param("opcion") String opcion,
      @Param("origen") String origen,
      @Param("descripcion") String descripcion,
      @Param("tipologiaId") int tipologiaId);

  @Query(value = "EXEC pa_GetNameWorflowDestiny :opcion, :origen, :descripcion, :tipologiaId", nativeQuery = true)
  List<String> listarDestinosWorkflow(
      @Param("opcion") String opcion,
      @Param("origen") String origen,
      @Param("descripcion") String descripcion,
      @Param("tipologiaId") int tipologiaId);

  @Query(value = "EXEC pa_SearchWorflowDestiny :workFlowId", nativeQuery = true)
  List<Object[]> search(@Param("workFlowId") int workFlowId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertWorflowDestiny :workFlowId, :destinoId", nativeQuery = true)
  void createDestino(@Param("workFlowId") int workFlowId, @Param("destinoId") int destinoId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteWorflowDestiny :workFlowId, :destinoId", nativeQuery = true)
  void deleteDestino(@Param("workFlowId") int workFlowId, @Param("destinoId") int destinoId);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO WmsWdGeneral.dbo.WorkflowLogs (UsuarioModifica, Cliente, Descripcion, Fecha) VALUES (:usuario, :cliente, :descripcion, GETDATE())", nativeQuery = true)
  void insertLog(@Param("usuario") String usuario, @Param("cliente") String cliente, @Param("descripcion") String descripcion);
}
