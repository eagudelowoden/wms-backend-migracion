package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.Entity.BaseEmpaqueModel;
import com.woden.wms_backend.repositories.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseEmpaqueRepository extends BaseRepository<BaseEmpaqueModel, Integer> {

  @Query(value = "SELECT f.serial, f.\"codigoSap\", f.\"estadoSap\", f.\"estadoRR\", f.\"Lote\" FROM pa_getmodelbaseweb(CAST(:base AS character varying), CAST(:serial AS character varying)) f", nativeQuery = true)
  List<Object[]> getModel(@Param("base") String base, @Param("serial") String serial);

  @Query(value = "SELECT CAST(f.cantidad AS integer) FROM pa_getcountbase(CAST(:base AS character varying)) f", nativeQuery = true)
  List<Object[]> getCountBase(@Param("base") String base);

}
