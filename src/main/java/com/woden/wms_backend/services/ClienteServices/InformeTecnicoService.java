package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayInputStream;
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

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
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

  /**
   * TTF de las firmas: Lobster Two (SIL Open Font License 1.1, licencia en
   * reports/OFL-LobsterTwo.txt), script con cuerpo que se lee bien impreso.
   * OpenHTMLtoPDF no lee las fuentes del sistema, asi que los archivos viajan
   * empaquetados en el JAR para que tambien funcione en el servidor.
   *
   * Se registran DOS cortes bajo un mismo nombre de familia: la bold italic
   * (lo que pide el template) y la regular vertical como respaldo. Es
   * importante porque OpenHTMLtoPDF no sintetiza estilos: si "italic" no
   * llegara a resolver, cae en la regular vertical de esta misma fuente y no
   * en Helvetica, que romperia el contraste de la firma. Tampoco engrosa una
   * fuente que no tenga la variante pedida, asi que el bold tambien tiene que
   * existir de verdad.
   */
  private static final String RUTA_FUENTE_FIRMA_CURSIVA = "/reports/LobsterTwo-BoldItalic.ttf";
  private static final String RUTA_FUENTE_FIRMA_REGULAR = "/reports/LobsterTwo-Regular.ttf";

  /**
   * Nombre CSS con el que se registra la fuente. El template la referencia asi
   * y no por el nombre real del TTF, para poder cambiar de fuente sin tocarlo.
   */
  private static final String NOMBRE_FUENTE_FIRMA = "Firma Script";

  private final PebbleEngine pebbleEngine = new PebbleEngine.Builder().build();

  @Autowired
  private DiagnosticoService diagnosticoService;

  // Cacheado en memoria: se lee y codifica una sola vez, no en cada informe.
  private String logoBase64;

  // Cacheado en memoria: los TTF pesan ~240 KB cada uno y se leen una sola vez.
  private byte[] fuenteFirmaCursiva;
  private byte[] fuenteFirmaRegular;
  private boolean fuenteFirmaCargada;

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
    // La firma lleva el mismo nombre en Camel Case: viene de
    // Usuario.Nombres/Apellidos y puede llegar en cualquier caja, y en la
    // cursive se ve raro si viene todo en mayusculas. Se expone aparte del
    // original para que la columna de valor lo muestre tal cual lo guardo el
    // cliente y solo la firma se normalice.
    contexto.put("firma_tecnico", nombreParaFirma(request.getResponsableTecnico()));
    contexto.put("firma_operacion", nombreParaFirma(request.getResponsableOperacion()));
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
      byte[][] fuenteFirma = cargarFuentesFirma();
      // El cast a FSSupplier es obligatorio: useFont no acepta el InputStream
      // directo. Se pasa un lambda para que OpenHTMLtoPDF pueda abrir un stream
      // nuevo por cada uso en vez de reusar el ya consumido.
      if (fuenteFirma[0].length > 0) {
        builder.useFont((FSSupplier<InputStream>) () -> new ByteArrayInputStream(fuenteFirma[0]),
            NOMBRE_FUENTE_FIRMA, 700, FontStyle.ITALIC, true);
      }
      if (fuenteFirma[1].length > 0) {
        builder.useFont((FSSupplier<InputStream>) () -> new ByteArrayInputStream(fuenteFirma[1]),
            NOMBRE_FUENTE_FIRMA, 400, FontStyle.NORMAL, true);
      }
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

  /**
   * Deja un nombre en Camel Case para imprimirlo manuscrito en la firma:
   * solo la inicial de cada palabra en mayuscula y el resto en minuscula, con
   * los espacios colapsados.
   *
   * El nombre se arma en el backend desde Usuario.Nombres/Apellidos, asi que
   * puede llegar como "MARIA FERNANDA", "maria fernanda" o ya bien escrito. Se
   * baja el resto a minuscula porque en la cursor del PDF las mayusculas
   * sostenidas se ven como un shouting. El guion cuenta como separador de
   * palabra para los nombres compuestos ("MARIA-JOSE" -> "Maria-Jose"), y null
   * o vacio devien "" para que la celda de firma quede en blanco.
   */
  private String nombreParaFirma(String nombre) {
    if (nombre == null) {
      return "";
    }
    StringBuilder salida = new StringBuilder(nombre.length());
    boolean inicioDePalabra = true;
    for (int i = 0; i < nombre.length(); i++) {
      char c = nombre.charAt(i);
      if (Character.isWhitespace(c) || c == '-') {
        if (salida.length() > 0 && salida.charAt(salida.length() - 1) != ' ') {
          salida.append(' ');
        }
        inicioDePalabra = true;
        continue;
      }
      salida.append(inicioDePalabra ? Character.toUpperCase(c) : Character.toLowerCase(c));
      inicioDePalabra = false;
    }
    int fin = salida.length();
    while (fin > 0 && salida.charAt(fin - 1) == ' ') {
      fin--;
    }
    salida.setLength(fin);
    return salida.toString();
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

  /**
   * Bytes de los TTF de firma, cacheados, en el orden {cursiva, regular}. Si un
   * recurso no esta en el classpath su entrada queda vacia y el PDF se sigue
   * generando con la fuente por defecto en vez de fallar, para no dejar de
   * emitir el informe.
   */
  private byte[][] cargarFuentesFirma() {
    if (fuenteFirmaCargada) {
      return new byte[][] { fuenteFirmaCursiva, fuenteFirmaRegular };
    }
    fuenteFirmaCursiva = leerTtf(RUTA_FUENTE_FIRMA_CURSIVA);
    fuenteFirmaRegular = leerTtf(RUTA_FUENTE_FIRMA_REGULAR);
    fuenteFirmaCargada = true;
    return new byte[][] { fuenteFirmaCursiva, fuenteFirmaRegular };
  }

  private byte[] leerTtf(String ruta) {
    try (InputStream stream = InformeTecnicoService.class.getResourceAsStream(ruta)) {
      if (stream == null) {
        logger.warn("[InformeTecnico] No se encontró {} en el classpath; la firma saldrá en la fuente por defecto", ruta);
        return new byte[0];
      }
      return stream.readAllBytes();
    } catch (IOException e) {
      logger.error("[InformeTecnico] Error cargando {}: {}", ruta, e.getMessage(), e);
      return new byte[0];
    }
  }
}
