package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import com.woden.wms_backend.models.Entity.LoteModel;
import com.woden.wms_backend.repositories.BaseRepository;

public interface LoteRepository extends BaseRepository<LoteModel, Integer> {
  @Query(value = "EXEC pa_GetBatchList", nativeQuery = true)
  List<Object[]> getLotes();
}
