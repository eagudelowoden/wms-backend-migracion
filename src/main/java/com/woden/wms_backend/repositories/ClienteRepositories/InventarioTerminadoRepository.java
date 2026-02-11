package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.InventarioTerminadoModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface InventarioTerminadoRepository extends BaseRepository<InventarioTerminadoModel, Integer> {
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO InventarioTerminado (serial, codigoSap, codigoSapReal, pallet, palletReal, estadoId, estadoSap, estadoRR, ajuste, fecha, usuarioId, fechaTermina, usuarioIdTermina, documento, serialId, mac, sobrante) VALUES (:serial, :codigoSap, :codigoSapReal, :pallet, :palletReal, :estadoId, :estadoSap, :estadoRR, :ajuste, :fecha, :usuarioId, :fechaTermina, :usuarioIdTermina, :documento, :serialId, :mac, :sobrante)", nativeQuery = true)
  Integer create(@Param("serial") String serial, @Param("codigoSap") String codigoSap,
      @Param("codigoSapReal") String codigoSapReal, @Param("pallet") String pallet,
      @Param("palletReal") String palletReal, @Param("estadoId") Integer estadoId, @Param("estadoSap") String estadoSap,
      @Param("estadoRR") String estadoRR, @Param("ajuste") String ajuste, @Param("fecha") String fecha,
      @Param("usuarioId") Integer usuarioId, @Param("fechaTermina") String fechaTerminado,
      @Param("usuarioIdTermina") Integer usuarioIdTerminado, @Param("documento") String documento,
      @Param("serialId") Integer serialId, @Param("mac") String mac, @Param("sobrante") Boolean sobrante);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertInventarioFaltante :usuarioIdTerminado, :documento, :filas OUT", nativeQuery = true)
  Integer insertInventarioFaltante(
      @Param("usuarioIdTerminado") Integer usuarioIdTerminado, @Param("documento") String documento,
      @Param("filas") Integer filas);
}
