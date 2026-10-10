package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.BaseDespachoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface BaseDespachoRepository extends BaseRepository<BaseDespachoModel, Integer> {
  @Query(value = "SELECT f.serial, f.\"codigoSap\", f.\"estadoSap\", f.\"estadoRR\", f.\"Lote\" FROM pa_getmodelbase(CAST(:base AS character varying), CAST(:serial AS character varying)) f", nativeQuery = true)
  List<Object[]> getModelBase(@Param("base") String base, @Param("serial") String serial);
}
