package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(BoletaMovimientoService.class);

  private final PebbleEngine pebbleEngine = new PebbleEngine.Builder().build();

  // Cacheado en memoria: se lee y codifica una sola vez, no en cada boleta.
  private String logoWodenBase64;

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

    // El logo del cliente (ClienteModel.bandera) es una ruta relativa que solo
    // existe en el servidor del frontend Angular — OpenHTMLtoPDF corre en el
    // backend y no tiene forma de resolverla (no hay URL base configurada).
    // Se usa el logo fijo de Woden ya empaquetado en el backend (mismo archivo
    // que usa JasperReportService para el PDF de un solo pallet), embebido
    // como data URI en base64 para no depender de resolución de URLs.
    contexto.put("logo_url", cargarLogoWodenBase64());
    contexto.put("empresa_nombre", request.getEmpresaNombre() != null ? request.getEmpresaNombre() : "");
    contexto.put("empresa_ruc", request.getRuc() != null ? request.getRuc() : "");
    contexto.put("empresa_telefono", request.getTelefono() != null ? request.getTelefono() : "");
    contexto.put("empresa_email", request.getEmail() != null ? request.getEmail() : "");
    contexto.put("empresa_web", request.getWeb() != null ? request.getWeb() : "");

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

  private String cargarLogoWodenBase64() {
    if (logoWodenBase64 != null) {
      return logoWodenBase64;
    }

    // OJO: NO usar /reports/Logo.png (el que usa JasperReportService) — es un
    // logo blanco (confirmado por inspección de píxeles: 0% de contenido
    // opaco no-blanco), pensado para fondos oscuros/de color. En esta boleta
    // el fondo es blanco, así que ahí es invisible. Se usa una copia de
    // WODEN-WMS.png (mismo logo del login, visible sobre blanco).
    try (InputStream logoStream = getClass().getResourceAsStream("/reports/WodenLogoBoleta.png")) {
      if (logoStream == null) {
        logger.warn("[BoletaMovimiento] No se encontró /reports/WodenLogoBoleta.png en el classpath");
        return "";
      }
      String base64 = Base64.getEncoder().encodeToString(logoStream.readAllBytes());
      logoWodenBase64 = "data:image/png;base64," + base64;
      return logoWodenBase64;
    } catch (Exception e) {
      logger.error("[BoletaMovimiento] Error cargando el logo: {}", e.getMessage(), e);
      return "";
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
