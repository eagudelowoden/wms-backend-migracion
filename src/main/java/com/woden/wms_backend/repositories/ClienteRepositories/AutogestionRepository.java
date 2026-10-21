package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.AutogestionLogModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface AutogestionRepository extends BaseRepository<AutogestionLogModel, Integer> {

  @Query(value = "EXEC pa_AutogestionEstados", nativeQuery = true)
  List<Object[]> estados();

  @Query(value = "EXEC pa_AutogestionBuscarSerial :serial", nativeQuery = true)
  List<Object[]> buscarSerial(@Param("serial") String serial);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_AutogestionSerial :serial, :accion, :estadoDestinoId, :palletDestino, :usuarioId, :justificacion", nativeQuery = true)
  void accionSerial(
      @Param("serial") String serial,
      @Param("accion") String accion,
      @Param("estadoDestinoId") Integer estadoDestinoId,
      @Param("palletDestino") String palletDestino,
      @Param("usuarioId") Integer usuarioId,
      @Param("justificacion") String justificacion);

  @Query(value = "EXEC pa_AutogestionBuscarPallet :pallet", nativeQuery = true)
  List<Object[]> buscarPallet(@Param("pallet") String pallet);

  @Query(value = "EXEC pa_AutogestionPalletCajas :palletId", nativeQuery = true)
  List<Object[]> palletCajas(@Param("palletId") Integer palletId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_AutogestionPallet :pallet, :accion, :estadoDestinoId, :cajaNumero, :palletDestino, :usuarioId, :justificacion", nativeQuery = true)
  void accionPallet(
      @Param("pallet") String pallet,
      @Param("accion") String accion,
      @Param("estadoDestinoId") Integer estadoDestinoId,
      @Param("cajaNumero") String cajaNumero,
      @Param("palletDestino") String palletDestino,
      @Param("usuarioId") Integer usuarioId,
      @Param("justificacion") String justificacion);

  @Query(value = "EXEC pa_AutogestionLogs :serial, :limite", nativeQuery = true)
  List<Object[]> logs(@Param("serial") String serial, @Param("limite") Integer limite);
}
