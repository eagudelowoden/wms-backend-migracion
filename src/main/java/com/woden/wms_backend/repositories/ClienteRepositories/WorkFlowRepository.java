package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
