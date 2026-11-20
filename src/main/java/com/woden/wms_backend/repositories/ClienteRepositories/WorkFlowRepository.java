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

  @Query(value = "SELECT CAST(f.\"nivelId\" AS integer) FROM pa_getlevelidworflow(CAST(:opcion AS character varying), CAST(:origen AS character varying), CAST(:descripcion AS character varying), CAST(:tipologiaId AS integer)) f", nativeQuery = true)
  Integer getLevelId(
      @Param("opcion") String opcion,
      @Param("origen") String origen,
      @Param("descripcion") String descripcion,
      @Param("tipologiaId") int tipologiaId);

  @Query(value = "SELECT CAST(f.id AS integer), f.modulo, f.origen, f.opcion, f.descripcion, f.tipologia, f.nivel FROM pa_searchworflow() f", nativeQuery = true)
  List<Object[]> search();

  @Query(value = "SELECT wfd.WorkFlowId, e.Id, e.Nombre FROM WorkFlowDestino wfd INNER JOIN Estado e ON e.Id = wfd.DestinoId", nativeQuery = true)
  List<Object[]> searchDestinos();

  @Query(value = "SELECT Id FROM WorkFlow WHERE ModuloId = :moduloId AND OrigenId = :origenId AND OpcionId = :opcionId AND TipologiaId = :tipologiaId LIMIT 1", nativeQuery = true)
  Integer findByConfig(@Param("moduloId") int moduloId, @Param("origenId") int origenId,
                       @Param("opcionId") int opcionId, @Param("tipologiaId") int tipologiaId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertworflow(CAST(:moduloId AS integer), CAST(:origenId AS integer), CAST(:opcionId AS integer), CAST(:tipologiaId AS integer), CAST(:nivelId AS integer))", nativeQuery = true)
  void create(@Param("moduloId") int moduloId, @Param("origenId") int origenId,
              @Param("opcionId") int opcionId, @Param("tipologiaId") int tipologiaId,
              @Param("nivelId") int nivelId);

  @Transactional
  @Query(value = "SELECT pa_updateworflow(CAST(:moduloId AS integer), CAST(:origenId AS integer), CAST(:opcionId AS integer), CAST(:tipologiaId AS integer), CAST(:nivelId AS integer), CAST(:id AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void updateWorkflow(@Param("moduloId") int moduloId, @Param("origenId") int origenId,
              @Param("opcionId") int opcionId, @Param("tipologiaId") int tipologiaId,
              @Param("nivelId") int nivelId, @Param("id") int id);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deleteworflow(CAST(:id AS integer))", nativeQuery = true)
  void deleteWorkflow(@Param("id") int id);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO general.WorkflowLogs (UsuarioModifica, Cliente, Descripcion, Fecha) VALUES (:usuario, :cliente, :descripcion, NOW())", nativeQuery = true)
  void insertLog(@Param("usuario") String usuario, @Param("cliente") String cliente, @Param("descripcion") String descripcion);
}