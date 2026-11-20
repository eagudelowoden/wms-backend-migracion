package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.MovimientoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface MovimientoRepository extends BaseRepository<MovimientoModel, Integer> {

  @Query(value = "SELECT CAST(f.\"origenId\" AS integer) FROM pa_getlastmovimient(CAST(:palletId AS integer)) f", nativeQuery = true)
  Integer getLast(@Param("palletId") Integer palletId);

  @Query(value = "select count(id) cantidad from Movimiento where usuarioId= :usuarioId", nativeQuery = true)
  Integer userCount(@Param("usuarioId") Integer usuarioId);

  @Query(value = "SELECT CAST(f.\"origenId\" AS integer) FROM pa_getlastmovimientaux(CAST(:palletId AS integer)) f", nativeQuery = true)
  Integer getLastMovimient(@Param("palletId") Integer palletId);

  @Query(value = "SELECT CAST(f.\"origenId\" AS integer) FROM pa_getlastmovimientaccesory(CAST(:palletId AS integer)) f", nativeQuery = true)
  Integer getLastMovimientAccesory(@Param("palletId") Integer palletId);
}
