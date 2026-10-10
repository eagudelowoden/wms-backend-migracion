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

  @Query(value = "SELECT CAST(f.id AS integer) FROM pa_getidworflowdestiny(CAST(:opcion AS character varying), CAST(:origen AS character varying), CAST(:descripcion AS character varying), CAST(:tipologiaId AS integer)) f", nativeQuery = true)
  List<Integer> getIdDestino(
      @Param("opcion") String opcion,
      @Param("origen") String origen,
      @Param("descripcion") String descripcion,
      @Param("tipologiaId") int tipologiaId);

  @Query(value = "SELECT f.\"Nombre\" FROM pa_getnameworflowdestiny(CAST(:opcion AS character varying), CAST(:origen AS character varying), CAST(:descripcion AS character varying), CAST(:tipologiaId AS integer)) f", nativeQuery = true)
  List<String> listarDestinosWorkflow(
      @Param("opcion") String opcion,
      @Param("origen") String origen,
      @Param("descripcion") String descripcion,
      @Param("tipologiaId") int tipologiaId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM pa_searchworflowdestiny(CAST(:workFlowId AS integer)) f", nativeQuery = true)
  List<Object[]> search(@Param("workFlowId") int workFlowId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertworflowdestiny(CAST(:workFlowId AS integer), CAST(:destinoId AS integer))", nativeQuery = true)
  void createDestino(@Param("workFlowId") int workFlowId, @Param("destinoId") int destinoId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deleteworflowdestiny(CAST(:workFlowId AS integer), CAST(:destinoId AS integer))", nativeQuery = true)
  void deleteDestino(@Param("workFlowId") int workFlowId, @Param("destinoId") int destinoId);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO general.WorkflowLogs (UsuarioModifica, Cliente, Descripcion, Fecha) VALUES (:usuario, :cliente, :descripcion, NOW())", nativeQuery = true)
  void insertLog(@Param("usuario") String usuario, @Param("cliente") String cliente, @Param("descripcion") String descripcion);
}
