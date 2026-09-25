package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.woden.wms_backend.dto.clientDTO.InformeTecnicoDTO;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;

/**
 * Genera el "Informe técnico PQRS" (dictamen de Garantías/TruckRolls) y lo
 * guarda como Hoja de Vida del serial. Mismo motor que BoletaMovimientoService
 * (Pebble -> JSoup -> OpenHTMLtoPDF, plantilla en reports/), plantilla propia:
 * reports/informe-tecnico-pqrs.html.
 *
 * SOLO VISUAL el modal del frontend por ahora (rama lberrio/formularioTR) —
 * este servicio ya genera y guarda el PDF real; falta que el modal lo llame
 * en vez de solo emitir el evento con los datos capturados.
 */
@Service
public class InformeTecnicoService {

  private static final Logger logger = LoggerFactory.getLogger(InformeTecnicoService.class);

  /** Metadatos de control del documento — fijos hasta que se confirme con el cliente quién los define. */
  private static final String CODIGO_DEFECTO = "PQRS-WMS-RI-001";
  private static final String VERSION_DEFECTO = "01";

  private final PebbleEngine pebbleEngine = new PebbleEngine.Builder().build();

  @Autowired
  private DiagnosticoService diagnosticoService;

  // Cacheado en memoria: se lee y codifica una sola vez, no en cada informe.
  private String logoBase64;

  public byte[] generarPdf(InformeTecnicoDTO request) throws Exception {
    PebbleTemplate template = pebbleEngine.getTemplate("reports/informe-tecnico-pqrs.html");

    Map<String, Object> contexto = new HashMap<>();
    contexto.put("logo_url", cargarLogoBase64());
    contexto.put("codigo", valorODefecto(request.getCodigo(), CODIGO_DEFECTO));
    contexto.put("version", valorODefecto(request.getVersion(), VERSION_DEFECTO));
    contexto.put("fecha_emision", valorODefecto(request.getFechaEmision(), ""));
    contexto.put("cliente", valorODefecto(request.getCliente(), ""));
    contexto.put("proceso", valorODefecto(request.getProceso(), ""));
    contexto.put("modelo", valorODefecto(request.getModelo(), ""));
    contexto.put("cantidad", request.getCantidad() != null ? request.getCantidad() : 1);
    contexto.put("serial", valorODefecto(request.getSerial(), ""));
    contexto.put("tecnico", valorODefecto(request.getTecnico(), ""));
    contexto.put("fecha_revision", valorODefecto(request.getFechaRevision(), ""));
    contexto.put("causa_encontrada", valorODefecto(request.getCausaEncontrada(), ""));
    contexto.put("veredicto_texto", valorODefecto(request.getVeredictoTexto(), ""));
    contexto.put("estado_aprobacion", valorODefecto(request.getEstadoAprobacion(), ""));
    contexto.put("fecha_aprobacion", valorODefecto(request.getFechaAprobacion(), ""));
    contexto.put("responsable_tecnico", valorODefecto(request.getResponsableTecnico(), ""));
    contexto.put("responsable_operacion", valorODefecto(request.getResponsableOperacion(), ""));
    contexto.put("evidencias", mapearEvidencias(request.getEvidencias()));

    StringWriter writer = new StringWriter();
    template.evaluate(writer, contexto);
    String htmlFinal = writer.toString();

    // OpenHTMLtoPDF exige XML estricto (tags de cierre en todo, incluidos los
    // "void" como <meta>/<img>). JSoup parsea el HTML tolerante y lo normaliza
    // a XHTML válido antes de entregárselo, mismo patrón que la Boleta.
    Document jsoupDoc = Jsoup.parse(htmlFinal);
    jsoupDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
    org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.useFastMode();
      builder.withW3cDocument(w3cDoc, "");
      builder.toStream(baos);
      builder.run();
      return baos.toByteArray();
    }
  }

  /**
   * Genera el PDF y lo guarda como Hoja de Vida del serial (mismo almacenamiento
   * y registro en la tabla HojaVida que la carga manual de un PDF/DOCX firmado).
   */
  public void generarYGuardar(InformeTecnicoDTO request) throws Exception {
    if (request.getSerial() == null || request.getSerial().isBlank()) {
      throw new IllegalArgumentException("Serial requerido");
    }
    byte[] pdf = generarPdf(request);
    diagnosticoService.guardarHojaVidaGenerada(
        request.getSerial(), pdf, request.getUsuarioId(), request.getModulo());
  }

  private String valorODefecto(String valor, String defecto) {
    return valor != null ? valor : defecto;
  }

  private List<Map<String, Object>> mapearEvidencias(List<InformeTecnicoDTO.EvidenciaDTO> evidencias) {
    List<Map<String, Object>> resultado = new ArrayList<>();
    if (evidencias == null) return resultado;
    for (InformeTecnicoDTO.EvidenciaDTO ev : evidencias) {
      if (ev.getSrc() == null || ev.getSrc().isBlank()) continue;
      Map<String, Object> m = new HashMap<>();
      m.put("src", ev.getSrc());
      m.put("leyenda", ev.getLeyenda());
      resultado.add(m);
    }
    return resultado;
  }

  private String cargarLogoBase64() {
    if (logoBase64 != null) {
      return logoBase64;
    }
    try (InputStream logoStream = getClass().getResourceAsStream("/reports/logo-informe-tecnico.png")) {
      if (logoStream == null) {
        logger.warn("[InformeTecnico] No se encontró /reports/logo-informe-tecnico.png en el classpath");
        return "";
      }
      String base64 = Base64.getEncoder().encodeToString(logoStream.readAllBytes());
      logoBase64 = "data:image/png;base64," + base64;
      return logoBase64;
    } catch (IOException e) {
      logger.error("[InformeTecnico] Error cargando el logo: {}", e.getMessage(), e);
      return "";
    }
  }
}
