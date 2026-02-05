package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.LoteModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface LoteRepository extends BaseRepository<LoteModel, Integer> {
  @Query(value = "EXEC pa_GetBatchList", nativeQuery = true)
  List<Object[]> getLotes();

  @Query(value = "EXEC pa_GetIdBatch :nombre", nativeQuery = true)
  Integer getIdByLote(@Param("nombre") String nombre);

  @Query(value = "EXEC pa_GetBatchName :id", nativeQuery = true)
  String getBatchName(@Param("id") Integer id);
}
