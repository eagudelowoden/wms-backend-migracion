package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.BaseIngresoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface BaseIngresoRepository extends BaseRepository<BaseIngresoModel, Integer> {

  @Query(value = "EXEC pa_GetModelBaseWeb :base, :serial", nativeQuery = true)
  List<Object[]> getModel(@Param("base") String base, @Param("serial") String serial);
}
