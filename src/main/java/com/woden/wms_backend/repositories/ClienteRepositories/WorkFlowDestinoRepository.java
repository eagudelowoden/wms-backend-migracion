package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
