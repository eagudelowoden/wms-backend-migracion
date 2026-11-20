package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface EtiquetaRepository extends BaseRepository<EtiquetaModel, Integer> {
  @Query(value = "SELECT CAST(f.id AS integer), f.nombre, f.tipo, CAST(f.impresion AS integer), CAST(f.\"codigoSapId\" AS integer), f.codigo, f.descripcion, CAST(f.activo AS boolean) FROM pa_getmodellabel(CAST(:nombre AS character varying)) f", nativeQuery = true)
  List<Object[]> getModelLabel(@Param("nombre") String nombre);

  @Query(value = "SELECT f.nombre FROM pa_getlistlabel(CAST(:tipo AS character varying)) f", nativeQuery = true)
  List<Object[]> getListLabel(@Param("tipo") String tipo);

  @Query(value = "SELECT CAST(f.\"Id\" AS integer), f.\"Nombre\", f.\"Tipo\", f.\"Impresion\", CAST(f.\"codigoSapId\" AS integer), CAST(f.\"Activo\" AS boolean) FROM pa_searchlabeled(CAST(:nombre AS character varying), CAST(:tipo AS character varying)) f", nativeQuery = true)
  List<Object[]> searchLabeled(@Param("nombre") String nombre, @Param("tipo") String tipo);

  @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END "
      + "FROM general.ClienteValidacion cv "
      + "JOIN general.ClienteValidacionTipo cvt ON cv.ValidacionTipoId = cvt.Id "
      + "WHERE cv.ClienteId = :clienteId AND cvt.Codigo = :codigo AND cv.Activo = true", nativeQuery = true)
  Integer tieneValidacionDirecta(@Param("clienteId") Integer clienteId, @Param("codigo") String codigo);
}
