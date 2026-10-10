package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.woden.wms_backend.models.Entity.BaseEtiquetadoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface BaseEtiquetadoRepository extends BaseRepository<BaseEtiquetadoModel, Integer> {

  @Query(value = "SELECT f.serial, f.\"codigoSap\", f.\"estadoSap\", f.\"estadoRR\", f.\"Lote\" FROM pa_getmodelbase(CAST(:base AS character varying), CAST(:serial AS character varying)) f", nativeQuery = true)
  List<Object[]> getModelBase(@Param("base") String base, @Param("serial") String serial);

  @Query(value = "SELECT CAST(f.cantidad AS integer) FROM pa_getcountbase(CAST(:baseEtiquetado AS character varying)) f", nativeQuery = true)
  Integer getCountBase(@Param("baseEtiquetado") String baseEtiquetado);

}