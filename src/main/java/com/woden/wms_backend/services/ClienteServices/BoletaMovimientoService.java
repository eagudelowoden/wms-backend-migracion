package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.woden.wms_backend.dto.clientDTO.BoletaMovimientoDTO;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;

/**
 * Genera la "boleta de control de movimiento" (varios pallets en un solo PDF).
 * Motor aparte del Jasper existente: Pebble renderiza la plantilla HTML
 * (/reports/boleta-movimiento.html, sintaxis Jinja2/Twig) y OpenHTMLtoPDF
 * convierte el HTML final a PDF. No toca ni depende de JasperReportService.
 *
 * Por ahora solo se completan los datos del pallet — datos de empresa y los
 * campos de metadata (nota de remisión, áreas, observaciones) quedan en blanco,
 * pendiente de definir su origen.
 */
@Service
public class BoletaMovimientoService {

  private final PebbleEngine pebbleEngine = new PebbleEngine.Builder().build();

  public byte[] generarBoleta(BoletaMovimientoDTO request) throws Exception {
    PebbleTemplate template = pebbleEngine.getTemplate("reports/boleta-movimiento.html");

    Map<String, Object> contexto = new HashMap<>();
    contexto.put("pedido_sap", request.getPedidoSap());
    contexto.put("origen", request.getOrigen());
    contexto.put("destino", request.getDestino());
    contexto.put("fecha_entrega", request.getFechaEntrega());
    contexto.put("observaciones_generales", request.getObservacionesGenerales());
    contexto.put("usuario_nombre", request.getUsuarioNombre());
    contexto.put("equipos", mapearEquipos(request.getEquipos()));
    contexto.put("total_unidades", totalUnidades(request.getEquipos()));
    contexto.put("doc_pagina", "1");

    // Datos de empresa/logo — pendientes de definir origen, van vacíos por ahora.
    contexto.put("logo_url", "");
    contexto.put("empresa_nombre", "");
    contexto.put("empresa_ruc", "");
    contexto.put("empresa_telefono", "");
    contexto.put("empresa_email", "");
    contexto.put("empresa_web", "");

    StringWriter writer = new StringWriter();
    template.evaluate(writer, contexto);
    String htmlFinal = writer.toString();

    // OpenHTMLtoPDF exige XML estricto (tags de cierre en todo, incluidos los
    // "void" como <meta>/<img>). JSoup parsea el HTML tolerante y lo normaliza
    // a XHTML válido antes de entregárselo, para no depender de que la
    // plantilla esté escrita a mano en XML perfecto.
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

  private List<Map<String, Object>> mapearEquipos(List<BoletaMovimientoDTO.ItemDTO> equipos) {
    return equipos.stream().map(item -> {
      Map<String, Object> m = new HashMap<>();
      m.put("id", item.getId());
      m.put("no_pallet", item.getNoPallet());
      m.put("codigo_sap", item.getCodigoSap());
      m.put("descripcion", item.getDescripcion());
      m.put("cantidad", item.getCantidad());
      m.put("tipologia", item.getTipologia());
      return m;
    }).toList();
  }

  private int totalUnidades(List<BoletaMovimientoDTO.ItemDTO> equipos) {
    return equipos.stream()
        .mapToInt(item -> item.getCantidad() != null ? item.getCantidad() : 0)
        .sum();
  }
}
