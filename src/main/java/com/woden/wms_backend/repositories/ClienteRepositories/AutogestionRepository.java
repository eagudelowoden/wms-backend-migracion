package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.AutogestionLogModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface AutogestionRepository extends BaseRepository<AutogestionLogModel, Integer> {

  @Query(value = "SELECT f.* FROM pa_autogestionestados(CAST(:ambito AS varchar)) f", nativeQuery = true)
  List<Object[]> estados(@Param("ambito") String ambito);

  @Query(value = "SELECT f.* FROM pa_autogestionbuscarserial(CAST(:serial AS varchar)) f", nativeQuery = true)
  List<Object[]> buscarSerial(@Param("serial") String serial);

  @Query(value = "SELECT f.* FROM pa_autogestionbuscarpallet(CAST(:pallet AS varchar)) f", nativeQuery = true)
  List<Object[]> buscarPallet(@Param("pallet") String pallet);

  @Query(value = "SELECT f.* FROM pa_autogestionpalletcajas(CAST(:palletId AS integer)) f", nativeQuery = true)
  List<Object[]> palletCajas(@Param("palletId") Integer palletId);

  @Query(value = "SELECT f.* FROM pa_autogestionlogs(CAST(:serial AS varchar), CAST(:limite AS integer)) f", nativeQuery = true)
  List<Object[]> logs(@Param("serial") String serial, @Param("limite") Integer limite);

  // ── Solicitudes de aprobación ─────────────────────────────────────────

  /** Valida en seco y crea la solicitud. Devuelve el id. */
  @Transactional
  @Query(value = "SELECT pa_autogestionsolicitudcrear(p_ambito => CAST(:ambito AS varchar), p_accion => CAST(:accion AS varchar), p_objetivo => CAST(:objetivo AS varchar), p_solicitanteid => CAST(:solicitanteId AS integer), p_justificacion => CAST(:justificacion AS varchar), p_estadodestinoid => CAST(:estadoDestinoId AS integer), p_cajanumero => CAST(:cajaNumero AS varchar), p_palletdestino => CAST(:palletDestino AS varchar), p_horas => CAST(:horas AS integer))", nativeQuery = true)
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

  // Las funciones void se llaman con SELECT 1 FROM fn(...) (devuelven una fila), sin @Modifying.
  @Transactional
  @Query(value = "SELECT 1 FROM pa_autogestionsolicitudtoken(CAST(:solicitudId AS integer), CAST(:aprobadorId AS integer), CAST(:nombre AS varchar), CAST(:correo AS varchar), CAST(:tokenHash AS varchar))", nativeQuery = true)
  void guardarToken(
      @Param("solicitudId") Integer solicitudId,
      @Param("aprobadorId") Integer aprobadorId,
      @Param("nombre") String nombre,
      @Param("correo") String correo,
      @Param("tokenHash") String tokenHash);

  /** Marca la solicitud como ERROR si no se pudo completar el envío (no queda "pendiente" huérfana). */
  @Transactional
  @Query(value = "SELECT 1 FROM pa_autogestionsolicitudcancelar(CAST(:solicitudId AS integer), CAST(:motivo AS varchar))", nativeQuery = true)
  void cancelarSolicitud(@Param("solicitudId") Integer solicitudId, @Param("motivo") String motivo);

  @Query(value = "SELECT f.* FROM pa_autogestionsolicitudportoken(CAST(:tokenHash AS varchar)) f", nativeQuery = true)
  List<Object[]> solicitudPorToken(@Param("tokenHash") String tokenHash);

  /** Autoriza/rechaza y, si autoriza, ejecuta el movimiento (todo en una transacción). */
  @Transactional
  @Query(value = "SELECT f.* FROM pa_autogestionsolicitudresolver(CAST(:tokenHash AS varchar), CAST(:decision AS varchar)) f", nativeQuery = true)
  List<Object[]> resolverSolicitud(@Param("tokenHash") String tokenHash, @Param("decision") String decision);

  @Query(value = "SELECT f.* FROM pa_autogestionsolicitudes(CAST(:limite AS integer)) f", nativeQuery = true)
  List<Object[]> solicitudes(@Param("limite") Integer limite);
}
