package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.woden.wms_backend.models.Entity.CajaDespachoModel;
import com.woden.wms_backend.repositories.BaseRepository;

public interface CajaDespachoRepository extends BaseRepository<CajaDespachoModel, Integer> {
  @Query(value = "EXEC pa_SearchDispatch :palletId", nativeQuery = true)
  List<Object[]> searchDispatch(
      Integer palletId);
}
