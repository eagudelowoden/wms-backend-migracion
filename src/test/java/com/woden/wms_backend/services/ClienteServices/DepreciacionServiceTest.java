package com.woden.wms_backend.services.ClienteServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.woden.wms_backend.repositories.ClienteRepositories.DiagnosticoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.EmpaqueRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.repositories.WmsWdGeneral.ClienteValidacionRepository;
import com.woden.wms_backend.services.ClienteServices.DepreciacionService.Modo;

@ExtendWith(MockitoExtension.class)
class DepreciacionServiceTest {

    @Mock
    private ClienteValidacionRepository clienteValidacionRepository;

    @Mock
    private IngresoRepository ingresoRepository;

    @Mock
    private EmpaqueRepository empaqueRepository;

    @Mock
    private DiagnosticoRepository diagnosticoRepository;

    @Mock
    private MaestroRepository maestroRepository;

    private DepreciacionService service;

    @BeforeEach
    void setUp() {
        service = new DepreciacionService(
                clienteValidacionRepository,
                ingresoRepository,
                empaqueRepository,
                diagnosticoRepository,
                maestroRepository);
    }

    @Test
    void sinClienteDevuelveNullSinConsultar() {
        assertNull(service.obtenerModoActivo(null));
        verifyNoInteractions(clienteValidacionRepository);
    }

    @Test
    void sinCodigosActivosDevuelveNull() {
        when(clienteValidacionRepository.findModosDepreciacionActivos(7)).thenReturn(List.of());

        assertNull(service.obtenerModoActivo(7));
    }

    @Test
    void soloCualquieraDevuelveCualquiera() {
        when(clienteValidacionRepository.findModosDepreciacionActivos(7))
                .thenReturn(List.of("MODO_DEPRECIACION_CUALQUIERA"));

        assertEquals(Modo.CUALQUIERA, service.obtenerModoActivo(7));
    }

    @Test
    void soloAmbasDevuelveAmbas() {
        when(clienteValidacionRepository.findModosDepreciacionActivos(7))
                .thenReturn(List.of("MODO_DEPRECIACION_AMBAS"));

        assertEquals(Modo.AMBAS, service.obtenerModoActivo(7));
    }

    @Test
    void ambosActivosDevuelveAmbas() {
        when(clienteValidacionRepository.findModosDepreciacionActivos(7))
                .thenReturn(List.of("MODO_DEPRECIACION_CUALQUIERA", "MODO_DEPRECIACION_AMBAS"));

        assertEquals(Modo.AMBAS, service.obtenerModoActivo(7));
    }

    @Test
    void ambasMarcaCuandoCumpleLasDosCondiciones() {
        when(ingresoRepository.existeTablaDepreciados()).thenReturn(1);
        when(ingresoRepository.existeDepreciado("SN1")).thenReturn(1);
        when(empaqueRepository.countReingresosNivel62("SN1")).thenReturn(3);
        when(maestroRepository.getIdEstadoDepreciado()).thenReturn(248);
        when(diagnosticoRepository.marcarDepreciado(10, 248)).thenReturn(1);

        service.evaluarTransicion(10, "SN1", Modo.AMBAS);

        verify(diagnosticoRepository).marcarDepreciado(10, 248);
    }

    @Test
    void ambasNoMarcaSinDepreciado() {
        when(ingresoRepository.existeTablaDepreciados()).thenReturn(1);
        when(ingresoRepository.existeDepreciado("SN1")).thenReturn(0);
        when(empaqueRepository.countReingresosNivel62("SN1")).thenReturn(3);

        service.evaluarTransicion(10, "SN1", Modo.AMBAS);

        verify(diagnosticoRepository, never()).marcarDepreciado(anyInt(), anyInt());
        verifyNoInteractions(maestroRepository);
    }

    @Test
    void ambasNoMarcaConMenosDeTresReingresos() {
        when(ingresoRepository.existeTablaDepreciados()).thenReturn(1);
        when(ingresoRepository.existeDepreciado("SN1")).thenReturn(1);
        when(empaqueRepository.countReingresosNivel62("SN1")).thenReturn(2);

        service.evaluarTransicion(10, "SN1", Modo.AMBAS);

        verify(diagnosticoRepository, never()).marcarDepreciado(anyInt(), anyInt());
        verifyNoInteractions(maestroRepository);
    }

    @Test
    void cualquieraMarcaConSoloDepreciado() {
        when(ingresoRepository.existeTablaDepreciados()).thenReturn(1);
        when(ingresoRepository.existeDepreciado("SN1")).thenReturn(1);
        when(empaqueRepository.countReingresosNivel62("SN1")).thenReturn(0);
        when(maestroRepository.getIdEstadoDepreciado()).thenReturn(248);
        when(diagnosticoRepository.marcarDepreciado(10, 248)).thenReturn(1);

        service.evaluarTransicion(10, "SN1", Modo.CUALQUIERA);

        verify(diagnosticoRepository).marcarDepreciado(10, 248);
    }

    @Test
    void cualquieraMarcaConSoloReingresos() {
        when(ingresoRepository.existeTablaDepreciados()).thenReturn(1);
        when(ingresoRepository.existeDepreciado("SN1")).thenReturn(0);
        when(empaqueRepository.countReingresosNivel62("SN1")).thenReturn(3);
        when(maestroRepository.getIdEstadoDepreciado()).thenReturn(248);
        when(diagnosticoRepository.marcarDepreciado(10, 248)).thenReturn(1);

        service.evaluarTransicion(10, "SN1", Modo.CUALQUIERA);

        verify(diagnosticoRepository).marcarDepreciado(10, 248);
    }

    @Test
    void cualquieraNoMarcaSinNingunaCondicion() {
        when(ingresoRepository.existeTablaDepreciados()).thenReturn(1);
        when(ingresoRepository.existeDepreciado("SN1")).thenReturn(0);
        when(empaqueRepository.countReingresosNivel62("SN1")).thenReturn(2);

        service.evaluarTransicion(10, "SN1", Modo.CUALQUIERA);

        verify(diagnosticoRepository, never()).marcarDepreciado(anyInt(), anyInt());
        verifyNoInteractions(maestroRepository);
    }

    @Test
    void sinEstadoDepreciadoNoMarca() {
        when(ingresoRepository.existeTablaDepreciados()).thenReturn(1);
        when(ingresoRepository.existeDepreciado("SN1")).thenReturn(1);
        when(empaqueRepository.countReingresosNivel62("SN1")).thenReturn(3);
        when(maestroRepository.getIdEstadoDepreciado()).thenReturn(null);

        service.evaluarTransicion(10, "SN1", Modo.AMBAS);

        verify(diagnosticoRepository, never()).marcarDepreciado(anyInt(), anyInt());
    }

    @Test
    void tablaDepreciadosInexistenteNoMarcaNiConsultaDepreciado() {
        when(ingresoRepository.existeTablaDepreciados()).thenReturn(0);

        service.evaluarTransicion(10, "SN1", Modo.AMBAS);

        verify(ingresoRepository, never()).existeDepreciado(anyString());
        verifyNoInteractions(empaqueRepository);
        verifyNoInteractions(diagnosticoRepository);
        verifyNoInteractions(maestroRepository);
    }

    @Test
    void modoNullNoConsultaRepositorios() {
        service.evaluarTransicion(10, "SN1", null);

        verifyNoInteractions(ingresoRepository);
        verifyNoInteractions(empaqueRepository);
        verifyNoInteractions(diagnosticoRepository);
        verifyNoInteractions(maestroRepository);
    }
}
