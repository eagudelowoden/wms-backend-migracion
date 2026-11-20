package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.TipoMaestroModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface TipoMaestroRepository extends BaseRepository<TipoMaestroModel, Integer> {

  @Query(value = "SELECT id FROM TipoMaestro WHERE descripcion = :descripcion", nativeQuery = true)
  Integer getTipoCodigoSap(@Param("descripcion") String descripcion);

  @Query(value = "SELECT CAST(f.\"Id\" AS integer), f.\"Nombre\", f.\"Descripcion\", CAST(f.\"Activo\" AS boolean) FROM pa_searchmastertype() f", nativeQuery = true)
  List<Object[]> search();

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertmastertype(CAST(:nombre AS character varying), CAST(:descripcion AS character varying))", nativeQuery = true)
  void insertSP(@Param("nombre") String nombre, @Param("descripcion") String descripcion);

  @Transactional
  @Query(value = "SELECT pa_updatemastertype(CAST(:nombre AS character varying), CAST(:descripcion AS character varying), CAST(:id AS integer), CAST(NULL AS integer))", nativeQuery = true)
  Integer updateSP(@Param("nombre") String nombre, @Param("descripcion") String descripcion, @Param("id") Integer id);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deletemastertype(CAST(:id AS integer))", nativeQuery = true)
  void deleteSP(@Param("id") Integer id);

  @Transactional
  @Query(value = "SELECT pa_innactivatemastertype(CAST(:id AS integer), CAST(:estado AS integer), CAST(NULL AS integer))", nativeQuery = true)
  Integer innactivateSP(@Param("id") Integer id, @Param("estado") Integer estado);

  @Query(value = "SELECT CAST(f.cantidad AS integer) FROM pa_getcountmastertype(CAST(:id AS integer)) f", nativeQuery = true)
  Integer getCount(@Param("id") Integer id);

  @Query(value = "SELECT f.nombre FROM pa_getlistmastertype() f", nativeQuery = true)
  List<Object[]> getList();

  @Query(value = "SELECT CAST(f.id AS integer) FROM pa_getidmastertype(CAST(:nombre AS character varying)) f", nativeQuery = true)
  Integer getIdByNombre(@Param("nombre") String nombre);
}
