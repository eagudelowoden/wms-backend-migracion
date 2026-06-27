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

    /**
     * Clasifica un serial recién ingresado como Garantía, TruckRoll o Baja Normal.
     * Se ejecuta en hilo separado para no bloquear la respuesta del ingreso.
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
            List<ParametroTruckrollModel> reglas = truckrollRepository.findReglasActivasPorCliente(clientDbName);
            logger.debug("[TruckrollService] Reglas activas encontradas para {}: {}", clientDbName, reglas.size());
            if (reglas.isEmpty()) {
                logger.debug("[TruckrollService] Sin reglas activas para este cliente — no se clasifica");
                return;
            }

            boolean adicionalUno = truckrollRepository.isTipoOrigenAdicionalUno(tipoOrigenId);
            if (adicionalUno) {
                logger.debug("[TruckrollService] TipoOrigen.Adicional=1 — serial {} excluido de toda clasificación TruckRoll", serial);
                return;
            }

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
                Integer truckRollId = calcularTruckRollId(dias, regla);
                logger.debug("[TruckrollService] Despacho encontrado: id={}, dias={} -> TruckRollId={}", despachoId, dias, truckRollId);

                truckrollRepository.updateIngresoTruckroll(regla, serial, despachoId, truckRollId);
                truckrollRepository.updateDespachoConsumido(regla, despachoId, serial, truckRollId);

                logger.info("[TruckrollService] Serial {} clasificado: TruckRollId={} ({} días) — regla '{}'",
                        serial, truckRollId, dias, regla.getDescripcion());
                return;
            }

            logger.debug("[TruckrollService] Serial {} sin Despacho matching en {} reglas activas", serial, reglas.size());

        } catch (Exception e) {
            logger.error("[TruckrollService] Error clasificando serial {}: {}", serial, e.getMessage(), e);
        } finally {
            ClientDatabaseContext.clear();
        }
    }

    /**
     * Determina qué valor del Ingreso usar para el cruce según CampoCruceDestino de la regla.
     * Ejemplo: si CampoCruceDestino = "Mac", usa el MAC del ingreso para buscar en Despacho.Serial
     */
    private String resolverValorCruce(String campoCruceDestino, String serial, String mac) {
        if (campoCruceDestino == null) return serial;
        return switch (campoCruceDestino.toLowerCase()) {
            case "mac" -> mac;
            default -> serial;
        };
    }

    /**
     * 0 = Garantía (0–DiasGarantia días)
     * 1 = Posible TruckRoll (DiasGarantia+1 – DiasTruckRoll días)
     * null = Vacío (> DiasTruckRoll o sin cruce)
     * TipoOrigen.Adicional=1 se evalúa antes de llegar aquí — si aplica, se retorna sin clasificar.
     */
    private Integer calcularTruckRollId(long dias, ParametroTruckrollModel regla) {
        if (dias <= regla.getDiasGarantia()) return 0;
        if (dias <= regla.getDiasTruckRoll()) return 1;
        return null;
    }
}
