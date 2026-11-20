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
  @Query(value = "SELECT 1 FROM pa_insertboxdispatch(CAST(:numero AS character varying), CAST(:palletId AS integer), CAST(:usuarioId AS integer), CAST(:fecha AS timestamp without time zone))", nativeQuery = true)
  void insertBoxDispatch(@Param("numero") String numero, @Param("palletId") Integer palletId,
      @Param("usuarioId") Integer usuarioId, @Param("fecha") String fecha);

  @Query(value = "SELECT CAST(f.id AS integer), f.numero, CAST(f.seriales AS integer) FROM pa_searchdispatch(CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> searchDispatch(
      Integer palletId);

  @Query(value = "SELECT f.\"Numero\" FROM pa_getlastboxdispatch(CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> getLastBoxDispatch(@Param("palletId") Integer palletId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deleteboxdispatch(CAST(:id AS integer))", nativeQuery = true)
  void deleteBoxDispatch(@Param("id") Integer id);

  @Query(value = "SELECT CAST(f.cantidad AS integer) FROM pa_getcountboxdispatch(CAST(:cajaId AS integer)) f", nativeQuery = true)
  Integer getCountBoxDispatch(@Param("cajaId") Integer cajaId);

  @Transactional
  @Query(value = "SELECT pa_inactivateallboxdispatch(CAST(:palletId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void inactivateAllBoxDispatch(@Param("palletId") Integer palletId, @Param("filas") Integer filas);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_inactivateboxdispatch(CAST(:cajaDespachoId AS integer))", nativeQuery = true)
  void inactivateBoxDispatch(@Param("cajaDespachoId") Integer cajaDespachoId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertdispatchboxfrompackedbox(CAST(:palletId AS integer))", nativeQuery = true)
  void insertDispatchBoxFromPackedBox(@Param("palletId") Integer palletId);
}
