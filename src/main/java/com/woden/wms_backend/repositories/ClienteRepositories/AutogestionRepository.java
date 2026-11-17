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

  @Query(value = "EXEC pa_AutogestionEstados :ambito", nativeQuery = true)
  List<Object[]> estados(@Param("ambito") String ambito);

  @Query(value = "EXEC pa_AutogestionBuscarSerial :serial", nativeQuery = true)
  List<Object[]> buscarSerial(@Param("serial") String serial);

  @Query(value = "EXEC pa_AutogestionBuscarPallet :pallet", nativeQuery = true)
  List<Object[]> buscarPallet(@Param("pallet") String pallet);

  @Query(value = "EXEC pa_AutogestionPalletCajas :palletId", nativeQuery = true)
  List<Object[]> palletCajas(@Param("palletId") Integer palletId);

  @Query(value = "EXEC pa_AutogestionLogs :serial, :limite", nativeQuery = true)
  List<Object[]> logs(@Param("serial") String serial, @Param("limite") Integer limite);

  // ── Solicitudes de aprobación ─────────────────────────────────────────

  /** Valida en seco y crea la solicitud. Devuelve el id. */
  @Transactional
  @Query(value = "EXEC pa_AutogestionSolicitudCrear :ambito, :accion, :objetivo, :estadoDestinoId, :cajaNumero, :palletDestino, :solicitanteId, :justificacion, :horas", nativeQuery = true)
  Integer crearSolicitud(
      @Param("ambito") String ambito,
      @Param("accion") String accion,
      @Param("objetivo") String objetivo,
      @Param("estadoDestinoId") Integer estadoDestinoId,
      @Param("cajaNumero") String cajaNumero,
      @Param("palletDestino") String palletDestino,
      @Param("solicitanteId") Integer solicitanteId,
      @Param("justificacion") String justificacion,
      @Param("horas") Integer horas);

  // Los SP que no devuelven filas deben ir como @Modifying (si no, JDBC falla con
  // "La instrucción no devolvió un conjunto de resultados").
  @Modifying
  @Transactional
  @Query(value = "EXEC pa_AutogestionSolicitudToken :solicitudId, :aprobadorId, :nombre, :correo, :tokenHash", nativeQuery = true)
  void guardarToken(
      @Param("solicitudId") Integer solicitudId,
      @Param("aprobadorId") Integer aprobadorId,
      @Param("nombre") String nombre,
      @Param("correo") String correo,
      @Param("tokenHash") String tokenHash);

  /** Marca la solicitud como ERROR si no se pudo completar el envío (no queda "pendiente" huérfana). */
  @Modifying
  @Transactional
  @Query(value = "EXEC pa_AutogestionSolicitudCancelar :solicitudId, :motivo", nativeQuery = true)
  void cancelarSolicitud(@Param("solicitudId") Integer solicitudId, @Param("motivo") String motivo);

  @Query(value = "EXEC pa_AutogestionSolicitudPorToken :tokenHash", nativeQuery = true)
  List<Object[]> solicitudPorToken(@Param("tokenHash") String tokenHash);

  /** Autoriza/rechaza y, si autoriza, ejecuta el movimiento (todo en una transacción). */
  @Transactional
  @Query(value = "EXEC pa_AutogestionSolicitudResolver :tokenHash, :decision", nativeQuery = true)
  List<Object[]> resolverSolicitud(@Param("tokenHash") String tokenHash, @Param("decision") String decision);

  @Query(value = "EXEC pa_AutogestionSolicitudes :limite", nativeQuery = true)
  List<Object[]> solicitudes(@Param("limite") Integer limite);
}
