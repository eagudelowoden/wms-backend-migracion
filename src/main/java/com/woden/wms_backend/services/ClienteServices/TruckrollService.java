package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.models.WmsWdGeneral.ParametroTruckrollModel;
import com.woden.wms_backend.repositories.ClienteRepositories.TruckrollRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckrollService {

    private static final Logger logger = LoggerFactory.getLogger(TruckrollService.class);

    @Autowired
    private TruckrollRepository truckrollRepository;

    private static final String CODIGO_VALIDACION = "TRUCKROLL";

    /**
     * Clasifica un serial recién ingresado según las reglas de negocio TruckRoll.
     * Se ejecuta en hilo separado para no bloquear la respuesta del ingreso.
     *
     * Clasificación por rango de días:
     *   <= DiasGarantia  → sin PQRS: 0 (Posible Garantía) | con PQRS: 1 (Garantía, Amarillo)
     *   <= DiasTruckRoll → sin PQRS: 6 (Posible TruckRoll) | con PQRS: 7 (TruckRoll, Amarillo)
     *   > DiasTruckRoll  → NULL (fuera de ventana)
     *   TipoOrigen.Adicional=1 excluye solo el rango TruckRoll (baja normal del cliente).
     *
     * Se requiere pasar clientDbName/clientName/clientId porque @Async corre en un
     * hilo nuevo donde ClientDatabaseContext (ThreadLocal) ya no tiene valores.
     */
    @Async("truckrollExecutor")
    public void clasificarAsync(String serial, String mac, Integer tipoOrigenId,
                                 String clientDbName, String clientName, Integer clientId) {
        if (serial == null || serial.isBlank()) return;

        logger.debug("[TruckrollService] Iniciando clasificación — serial={}, mac={}, tipoOrigenId={}, clientDb={}", serial, mac, tipoOrigenId, clientDbName);
        ClientDatabaseContext.setCurrentClient(clientName, clientDbName, clientId);
        try {
            if (!truckrollRepository.tieneValidacionActiva(clientId, CODIGO_VALIDACION)) {
                logger.debug("[TruckrollService] Validación TRUCKROLL no activa para el cliente {} — no se clasifica", clientName);
                return;
            }

            List<ParametroTruckrollModel> reglas = truckrollRepository.findReglasActivasPorCliente(clientDbName);
            logger.debug("[TruckrollService] Reglas activas encontradas para {}: {}", clientDbName, reglas.size());
            if (reglas.isEmpty()) {
                logger.debug("[TruckrollService] Sin reglas activas para este cliente — no se clasifica");
                return;
            }

            boolean adicionalUno = truckrollRepository.isTipoOrigenAdicionalUno(tipoOrigenId);

            for (ParametroTruckrollModel regla : reglas) {
                String valorCruce = resolverValorCruce(regla.getCampoCruceDestino(), serial, mac);
                logger.debug("[TruckrollService] Regla '{}' — campoCruceDestino={}, valorCruce={}",
                        regla.getDescripcion(), regla.getCampoCruceDestino(), valorCruce);
                if (valorCruce == null || valorCruce.isBlank()) {
                    logger.debug("[TruckrollService] Valor de cruce vacío, saltando regla");
                    continue;
                }

                long[] resultado = truckrollRepository.findDespachoOrigen(regla, valorCruce);
                if (resultado == null) {
                    logger.debug("[TruckrollService] Sin Despacho en '{}' para cruce '{}'", regla.getBaseOrigen(), valorCruce);
                    continue;
                }

                long despachoId = resultado[0];
                long dias = resultado[1];
                Integer truckRollId;

                if (dias <= regla.getDiasGarantia()) {
                    boolean tienePqrs = truckrollRepository.hasPqrsTicket(serial);
                    truckRollId = tienePqrs ? 1 : 0;
                    logger.debug("[TruckrollService] Garantía ({} días) — PQRS={} → TruckRollId={}", dias, tienePqrs, truckRollId);
                } else if (dias <= regla.getDiasTruckRoll()) {
                    if (adicionalUno) {
                        logger.debug("[TruckrollService] TipoOrigen.Adicional=1 en rango TruckRoll — serial {} excluido", serial);
                        return;
                    }
                    boolean tienePqrs = truckrollRepository.hasPqrsTicket(serial);
                    truckRollId = tienePqrs ? 7 : 6;
                    logger.debug("[TruckrollService] TruckRoll ({} días) — PQRS={} → TruckRollId={}", dias, tienePqrs, truckRollId);
                } else {
                    truckRollId = null;
                    logger.debug("[TruckrollService] Fuera de ventana ({} días) — TruckRollId=NULL", dias);
                }

                truckrollRepository.updateIngresoTruckroll(regla, serial, despachoId, truckRollId);
                truckrollRepository.updateDespachoConsumido(regla, despachoId, serial, truckRollId);

                logger.info("[TruckrollService] Serial {} clasificado: TruckRollId={} ({} días) — regla '{}'",
                        serial, truckRollId, dias, regla.getDescripcion());
                return;
            }

            logger.debug("[TruckrollService] Serial {} sin Despacho matching en {} reglas activas", serial, reglas.size());

            // Sin cruce en Despacho pero con ticket PQRS → TruckRollId = 14 (solo PQRS)
            if (truckrollRepository.hasPqrsTicket(serial)) {
                truckrollRepository.updateIngresoSoloPqrs(serial);
                logger.info("[TruckrollService] Serial {} sin cruce Despacho pero con PQRS → TruckRollId=14", serial);
            }

        } catch (Exception e) {
            logger.error("[TruckrollService] Error clasificando serial {}: {}", serial, e.getMessage(), e);
        } finally {
            ClientDatabaseContext.clear();
        }
    }

    /**
     * Limpieza post-delete: al eliminar un Ingreso clasificado, libera el Despacho
     * de la BD origen (ClienteDestinoId y TruckRollId a NULL) para que un reingreso
     * del mismo serial vuelva a clasificarse correctamente.
     * Se ejecuta en el hilo del request (el contexto de cliente ya está establecido),
     * ANTES de borrar el Ingreso — después ya no existe el vínculo ClienteOrigenId.
     */
    public void limpiarPorEliminacion(String serial) {
        try {
            Integer clienteId = ClientDatabaseContext.getCurrentClientId();
            if (!truckrollRepository.tieneValidacionActiva(clienteId, CODIGO_VALIDACION)) {
                return;
            }

            long[] vinculo = truckrollRepository.getDespachoVinculado(serial);
            if (vinculo == null) return; // serial sin clasificación TruckRoll — nada que limpiar

            long despachoId = vinculo[0];
            long parametroId = vinculo[1];

            String clientDb = ClientDatabaseContext.getCurrentClientDb();
            List<ParametroTruckrollModel> reglas = truckrollRepository.findReglasActivasPorCliente(clientDb);
            for (ParametroTruckrollModel regla : reglas) {
                if (regla.getId() == parametroId) {
                    truckrollRepository.limpiarDespachoOrigen(regla, despachoId);
                    logger.info("[TruckrollService] Serial {} eliminado — Despacho {} liberado en {}",
                            serial, despachoId, regla.getBaseOrigen());
                    return;
                }
            }
            logger.warn("[TruckrollService] Serial {} tiene ClienteParametroId={} pero la regla no está activa — Despacho {} no liberado",
                    serial, parametroId, despachoId);
        } catch (Exception e) {
            logger.error("[TruckrollService] Error en limpieza post-delete del serial {}: {}", serial, e.getMessage(), e);
        }
    }

    /**
     * Determina qué valor del Ingreso usar para el cruce según CampoCruceDestino de la regla.
     */
    private String resolverValorCruce(String campoCruceDestino, String serial, String mac) {
        if (campoCruceDestino == null) return serial;
        return switch (campoCruceDestino.toLowerCase()) {
            case "mac" -> mac;
            default -> serial;
        };
    }
}
