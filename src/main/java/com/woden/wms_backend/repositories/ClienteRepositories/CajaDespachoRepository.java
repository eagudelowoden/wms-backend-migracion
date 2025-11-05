package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.CajaDespachoModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface CajaDespachoRepository extends BaseRepository<CajaDespachoModel, Integer> {
  @Transactional
  @Modifying
  @Query(value = "EXEC pa_InsertBoxDispatch :numero, :palletId, :usuarioId, :fecha", nativeQuery = true)
  void insertBoxDispatch(@Param("numero") String numero, @Param("palletId") Integer palletId,
      @Param("usuarioId") Integer usuarioId, @Param("fecha") String fecha);

  @Query(value = "EXEC pa_SearchDispatch :palletId", nativeQuery = true)
  List<Object[]> searchDispatch(
      Integer palletId);

  @Query(value = "EXEC pa_GetLastBoxDispatch :palletId", nativeQuery = true)
  List<Object[]> getLastBoxDispatch(@Param("palletId") Integer palletId);

  @Transactional
  @Modifying
  @Query(value = "EXEC pa_DeleteBoxDispatch :id", nativeQuery = true)
  void deleteBoxDispatch(@Param("id") Integer id);

  @Query(value = "EXEC pa_GetCountBoxDispatch :cajaId", nativeQuery = true)
  Integer getCountBoxDispatch(@Param("cajaId") Integer cajaId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InactivateAllBoxDispatch :palletId, :filas OUT", nativeQuery = true)
  void inactivateAllBoxDispatch(@Param("palletId") Integer palletId, @Param("filas") Integer filas);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InactivateBoxDispatch :cajaDespachoId", nativeQuery = true)
  void inactivateBoxDispatch(@Param("cajaDespachoId") Integer cajaDespachoId);
}
