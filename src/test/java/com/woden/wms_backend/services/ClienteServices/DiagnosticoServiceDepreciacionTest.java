package com.woden.wms_backend.services.ClienteServices;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.diagnostico.DiagnosticoRequest;
import com.woden.wms_backend.repositories.ClienteRepositories.DiagnosticoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.services.ClienteServices.DepreciacionService.Modo;

@ExtendWith(MockitoExtension.class)
class DiagnosticoServiceDepreciacionTest {

    private static final String ESTADO_TERMINADO = "DIAGNOSTICO-TERMINADO";

    @Mock
    private DiagnosticoRepository repository;

    @Mock
    private IngresoRepository ingresoRepository;

    @Mock
    private DepreciacionService depreciacionService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DiagnosticoService service;

    @Test
    void sinItemsEnEstadoTerminadoNoConsultaTransiciones() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(52);

        service.save(List.of(request("SN1", 40)));

        verify(repository).save("[]");
        verify(ingresoRepository, never()).findUltimoEstadoBySerial(anyString());
        verifyNoInteractions(depreciacionService);
    }

    @Test
    void transicionA52EvaluaConModoActivo() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(52);
        when(ingresoRepository.findUltimoEstadoBySerial("SN1"))
                .thenReturn(List.<Object[]>of(new Object[] { 10, 51 }));
        when(depreciacionService.obtenerModoActivo(7)).thenReturn(Modo.AMBAS);

        try (MockedStatic<ClientDatabaseContext> context = mockStatic(ClientDatabaseContext.class)) {
            context.when(ClientDatabaseContext::getCurrentClientId).thenReturn(7);
            service.save(List.of(request("SN1", 52)));
        }

        InOrder order = inOrder(repository, depreciacionService);
        order.verify(repository).save("[]");
        order.verify(depreciacionService).evaluarTransicion(10, "SN1", Modo.AMBAS);
    }

    @Test
    void transicionAEstadoConfiguradoDistintoDe52Evalua() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(99);
        when(ingresoRepository.findUltimoEstadoBySerial("SN1"))
                .thenReturn(List.<Object[]>of(new Object[] { 10, 98 }));
        when(depreciacionService.obtenerModoActivo(7)).thenReturn(Modo.AMBAS);

        try (MockedStatic<ClientDatabaseContext> context = mockStatic(ClientDatabaseContext.class)) {
            context.when(ClientDatabaseContext::getCurrentClientId).thenReturn(7);
            service.save(List.of(request("SN1", 99)));
        }

        verify(depreciacionService).evaluarTransicion(10, "SN1", Modo.AMBAS);
    }

    @Test
    void request52ConEstadoConfigurado99NoEvalua() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(99);

        service.save(List.of(request("SN1", 52)));

        verify(ingresoRepository, never()).findUltimoEstadoBySerial(anyString());
        verifyNoInteractions(depreciacionService);
    }

    @Test
    void sinEstadoTerminadoConfiguradoNoEvalua() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(null);

        service.save(List.of(request("SN1", 52)));

        verify(repository).save("[]");
        verify(ingresoRepository, never()).findUltimoEstadoBySerial(anyString());
        verifyNoInteractions(depreciacionService);
    }

    @Test
    void siYaEstabaEn52NoLeeModoNiEvalua() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(52);
        when(ingresoRepository.findUltimoEstadoBySerial("SN1"))
                .thenReturn(List.<Object[]>of(new Object[] { 10, 52 }));

        service.save(List.of(request("SN1", 52)));

        verifyNoInteractions(depreciacionService);
    }

    @Test
    void sinIngresoPrevioNoLeeModoNiEvalua() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(52);
        when(ingresoRepository.findUltimoEstadoBySerial("SN1")).thenReturn(List.of());

        service.save(List.of(request("SN1", 52)));

        verifyNoInteractions(depreciacionService);
    }

    @Test
    void sinModoActivoNoEvalua() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(52);
        when(ingresoRepository.findUltimoEstadoBySerial("SN1"))
                .thenReturn(List.<Object[]>of(new Object[] { 10, 51 }));
        when(depreciacionService.obtenerModoActivo(7)).thenReturn(null);

        try (MockedStatic<ClientDatabaseContext> context = mockStatic(ClientDatabaseContext.class)) {
            context.when(ClientDatabaseContext::getCurrentClientId).thenReturn(7);
            service.save(List.of(request("SN1", 52)));
        }

        verify(depreciacionService, never()).evaluarTransicion(anyInt(), anyString(), any());
    }

    @Test
    void soloEvaluaLosItemsQueEntranA52() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(ingresoRepository.getIdEstadoByNombre(ESTADO_TERMINADO)).thenReturn(52);
        when(ingresoRepository.findUltimoEstadoBySerial("SN1"))
                .thenReturn(List.<Object[]>of(new Object[] { 10, 51 }));
        when(depreciacionService.obtenerModoActivo(7)).thenReturn(Modo.CUALQUIERA);

        try (MockedStatic<ClientDatabaseContext> context = mockStatic(ClientDatabaseContext.class)) {
            context.when(ClientDatabaseContext::getCurrentClientId).thenReturn(7);
            service.save(List.of(request("SN1", 52), request("SN2", 40)));
        }

        verify(depreciacionService).evaluarTransicion(10, "SN1", Modo.CUALQUIERA);
        verify(depreciacionService, never()).evaluarTransicion(20, "SN2", Modo.CUALQUIERA);
    }

    private DiagnosticoRequest request(String serial, Integer estadoId) {
        DiagnosticoRequest request = new DiagnosticoRequest();
        request.setSerial(serial);
        request.setEstadoId(estadoId);
        request.setUsuarioId(1);
        return request;
    }
}
