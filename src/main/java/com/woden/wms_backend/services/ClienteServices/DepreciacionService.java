package com.woden.wms_backend.services.ClienteServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.repositories.ClienteRepositories.DiagnosticoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.EmpaqueRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.repositories.WmsWdGeneral.ClienteValidacionRepository;

@Service
public class DepreciacionService {

    public enum Modo {
        CUALQUIERA,
        AMBAS
    }

    private static final Logger logger = LoggerFactory.getLogger(DepreciacionService.class);
    private static final String CODIGO_CUALQUIERA = "MODO_DEPRECIACION_CUALQUIERA";
    private static final String CODIGO_AMBAS = "MODO_DEPRECIACION_AMBAS";
    private static final int REINGRESOS_MINIMOS = 3;

    private final ClienteValidacionRepository clienteValidacionRepository;
    private final IngresoRepository ingresoRepository;
    private final EmpaqueRepository empaqueRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final MaestroRepository maestroRepository;

    public DepreciacionService(
            ClienteValidacionRepository clienteValidacionRepository,
            IngresoRepository ingresoRepository,
            EmpaqueRepository empaqueRepository,
            DiagnosticoRepository diagnosticoRepository,
            MaestroRepository maestroRepository) {
        this.clienteValidacionRepository = clienteValidacionRepository;
        this.ingresoRepository = ingresoRepository;
        this.empaqueRepository = empaqueRepository;
        this.diagnosticoRepository = diagnosticoRepository;
        this.maestroRepository = maestroRepository;
    }

    public Modo obtenerModoActivo(Integer clienteId) {
        if (clienteId == null) {
            return null;
        }

        List<String> codigos = clienteValidacionRepository.findModosDepreciacionActivos(clienteId);
        if (codigos == null || codigos.isEmpty()) {
            return null;
        }

        boolean cualquiera = codigos.contains(CODIGO_CUALQUIERA);
        boolean ambas = codigos.contains(CODIGO_AMBAS);

        if (cualquiera && ambas) {
            logger.warn("[DEPRECIACION] Cliente {} tiene ambos modos activos; se aplica AMBAS", clienteId);
        }

        if (ambas) {
            return Modo.AMBAS;
        }

        if (cualquiera) {
            return Modo.CUALQUIERA;
        }

        return null;
    }

    public void evaluarTransicion(Integer ingresoId, String serial, Modo modo) {
        if (ingresoId == null || serial == null || modo == null) {
            return;
        }

        Integer tabla = ingresoRepository.existeTablaDepreciados();
        if (tabla == null || tabla == 0) {
            logger.error("[DEPRECIACION] No existe la tabla Depreciados; no se evalua serial={} ingresoId={}", serial, ingresoId);
            return;
        }

        Integer marcado = ingresoRepository.existeDepreciado(serial);
        boolean enDepreciados = marcado != null && marcado > 0;

        Integer reingresos = empaqueRepository.countReingresosNivel62(serial);
        int totalReingresos = reingresos != null ? reingresos : 0;

        logger.warn("[DEPRECIACION-EVAL] clienteId={} serial='{}' modo={} enDepreciados={} reingresos={}",
                ClientDatabaseContext.getCurrentClientId(), serial, modo, enDepreciados, totalReingresos);

        boolean cumple = modo == Modo.AMBAS
                ? enDepreciados && totalReingresos >= REINGRESOS_MINIMOS
                : enDepreciados || totalReingresos >= REINGRESOS_MINIMOS;

        if (!cumple) {
            logger.debug("[DEPRECIACION] No cumple serial={} modo={} enDepreciados={} reingresos={}",
                    serial, modo, enDepreciados, totalReingresos);
            return;
        }

        Integer estadoDepreciadoId = maestroRepository.getIdEstadoDepreciado();
        if (estadoDepreciadoId == null) {
            logger.error("[DEPRECIACION] No se encontro Maestro DEPRECIADO (TipoMaestro 'Estados Diagnostico') serial={} ingresoId={}",
                    serial, ingresoId);
            return;
        }

        int filas = diagnosticoRepository.marcarDepreciado(ingresoId, estadoDepreciadoId);
        if (filas == 0) {
            logger.warn("[DEPRECIACION] Serial {} cumple la regla pero no se actualizo Diagnostico. ingresoId={} estadoDepreciadoId={} modo={} enDepreciados={} reingresos={}",
                    serial, ingresoId, estadoDepreciadoId, modo, enDepreciados, totalReingresos);
            return;
        }
        logger.info("[DEPRECIACION] Serial {} marcado como depreciado. ingresoId={} estadoDepreciadoId={} modo={} enDepreciados={} reingresos={} filas={}",
                serial, ingresoId, estadoDepreciadoId, modo, enDepreciados, totalReingresos, filas);
    }

    /**
     * Consulta de solo lectura: determina si un serial cumpliría las condiciones de depreciación
     * según el modo activo del cliente, sin modificar nada en BD.
     * Sirve como indicador visual al cargar el serial en Diagnóstico.
     *
     * @param serial número de serie a evaluar
     * @return mapa con seraDepreciado, modo, enDepreciados, totalReingresos
     */
    public Map<String, Object> consultarEstadoDepreciacion(String serial) {
        Map<String, Object> result = new HashMap<>();
        result.put("seraDepreciado", false);
        result.put("modo", null);
        result.put("enDepreciados", false);
        result.put("totalReingresos", 0);

        Integer clienteId = ClientDatabaseContext.getCurrentClientId();
        Modo modo = obtenerModoActivo(clienteId);
        result.put("modo", modo != null ? modo.name() : null);

        if (modo == null) {
            return result;
        }

        Integer tabla = ingresoRepository.existeTablaDepreciados();
        if (tabla == null || tabla == 0) {
            logger.debug("[DEPRECIACION-CONSULTA] Tabla Depreciados no existe; serial={}", serial);
            return result;
        }

        Integer marcado = ingresoRepository.existeDepreciado(serial);
        boolean enDepreciados = marcado != null && marcado > 0;
        result.put("enDepreciados", enDepreciados);

        Integer reingresos = empaqueRepository.countReingresosNivel62(serial);
        int totalReingresos = reingresos != null ? reingresos : 0;
        result.put("totalReingresos", totalReingresos);

        boolean seraDepreciado = modo == Modo.AMBAS
                ? enDepreciados && totalReingresos >= REINGRESOS_MINIMOS
                : enDepreciados || totalReingresos >= REINGRESOS_MINIMOS;

        result.put("seraDepreciado", seraDepreciado);
        logger.warn("[DEPRECIACION-CONSULTA] clienteId={} serial='{}' modo={} seraDepreciado={} enDepreciados={} reingresos={}",
                clienteId, serial, modo, seraDepreciado, enDepreciados, totalReingresos);
        return result;
    }
}
