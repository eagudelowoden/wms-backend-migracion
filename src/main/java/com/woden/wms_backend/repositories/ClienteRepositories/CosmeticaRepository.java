package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.CosmeticaModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface CosmeticaRepository extends BaseRepository<CosmeticaModel, Integer> {

  @Query(value = "EXEC pa_SearchCosmeticaEntry", nativeQuery = true)
  public List<Object[]> searchCosmeticaEntry();
}
