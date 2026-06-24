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

  @Query(value = "EXEC pa_SearchMasterType", nativeQuery = true)
  List<Object[]> search();

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertMasterType :nombre, :descripcion", nativeQuery = true)
  void insertSP(@Param("nombre") String nombre, @Param("descripcion") String descripcion);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateMasterType :nombre, :descripcion, :id, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
  Integer updateSP(@Param("nombre") String nombre, @Param("descripcion") String descripcion, @Param("id") Integer id);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteMasterType :id", nativeQuery = true)
  void deleteSP(@Param("id") Integer id);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_InnactivateMasterType :id, :estado, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
  Integer innactivateSP(@Param("id") Integer id, @Param("estado") Integer estado);

  @Query(value = "EXEC pa_GetCountMasterType :id", nativeQuery = true)
  Integer getCount(@Param("id") Integer id);

  @Query(value = "EXEC pa_GetListMasterType", nativeQuery = true)
  List<Object[]> getList();

  @Query(value = "EXEC pa_GetIdMasterType :nombre", nativeQuery = true)
  Integer getIdByNombre(@Param("nombre") String nombre);
}
