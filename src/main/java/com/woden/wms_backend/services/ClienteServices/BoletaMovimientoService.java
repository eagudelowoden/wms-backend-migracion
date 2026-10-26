package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.BoletaMovimientoDTO;
import com.woden.wms_backend.util.PlantillaTexto;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Genera la "boleta de control de movimiento" (varios pallets en un solo PDF).
 * Motor aparte del Jasper existente: Pebble renderiza la plantilla HTML
 * (/reports/boleta-movimiento.html, sintaxis Jinja2/Twig) y OpenHTMLtoPDF
 * convierte el HTML final a PDF. No toca ni depende de JasperReportService.
 *
 * El contenido sale de la plantilla del cliente (Administración > Documentos > Boleta de entrega): colores, logo,
 * líneas de la empresa, control del documento, títulos, campos, columnas, total, observaciones, firmas y pie. Sin
 * plantilla guardada es el formato LS-ADM-FO-16 de siempre (resources/documentos/BOLETA_ENTREGA.json).
 */
@Service
public class BoletaMovimientoService {

  private static final Logger logger = LoggerFactory.getLogger(BoletaMovimientoService.class);

  /** alto del encabezado que se repite (logo, título y datos de la boleta) y del pie, en mm */
  private static final double ENCABEZADO_MM = 65;
  private static final double PIE_MM = 18;
  private static final double HOJA_MM = 297;
  private static final double ANCHO_UTIL_MM = 210 - 26;
  /** centrado de una boleta de una sola hoja: nunca más de esto hacia abajo */
  private static final double MAX_CENTRADO_MM = 45;

  private final PebbleEngine pebbleEngine = new PebbleEngine.Builder().build();

  // Cacheado en memoria: se lee y codifica una sola vez, no en cada boleta.
  private String logoWodenBase64;

  // Firma en cursiva: Brush Script MT si el servidor la tiene (la misma de las hojas de vida); si no, Great
  // Vibes (licencia OFL) empaquetada en el backend. OpenHTMLtoPDF no usa las fuentes del sistema por si solo.
  private static final String[] FIRMA_SISTEMA = {
      "C:/Windows/Fonts/BRUSHSCI.TTF", "C:/Windows/Fonts/brushsci.ttf" };
  private static final String FIRMA_RECURSO = "/reports/fonts/GreatVibes-Regular.ttf";

  @Autowired(required = false)
  private DocumentoPlantillaService plantillas;

  @PersistenceContext
  private EntityManager em;

  /** Boleta con la plantilla del cliente de la sesión. */
  public byte[] generarBoleta(BoletaMovimientoDTO request) throws Exception {
    Map<String, Object> config = plantillas != null ? plantillas.config(DocumentoPlantillaService.BOLETA) : null;
    return generarBoleta(request, config);
  }

  /** Boleta con una plantilla dada (vista previa del editor de Documentos). */
  public byte[] generarBoleta(BoletaMovimientoDTO request, Map<String, Object> config) throws Exception {
    if (config == null) {
      config = new DocumentoPlantillaService().porDefecto(DocumentoPlantillaService.BOLETA);
    }
    PebbleTemplate template = pebbleEngine.getTemplate("reports/boleta-movimiento.html");
    StringWriter writer = new StringWriter();
    template.evaluate(writer, contexto(request, config));
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
      registrarFuenteFirma(builder);
      builder.withW3cDocument(w3cDoc, "");
      builder.toStream(baos);
      builder.run();
      return baos.toByteArray();
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> contexto(BoletaMovimientoDTO request, Map<String, Object> cfg) {
    List<BoletaMovimientoDTO.ItemDTO> equipos = request.getEquipos() == null ? List.of() : request.getEquipos();
    int totalUnidades = totalUnidades(equipos);

    Map<String, String> vars = new HashMap<>();
    vars.put("documento", nulo(request.getPedidoSap()));
    vars.put("fecha_entrega", nulo(request.getFechaEntrega()));
    vars.put("origen", nulo(request.getOrigen()));
    vars.put("destino", nulo(request.getDestino()));
    vars.put("usuario", nulo(request.getUsuarioNombre()));
    vars.put("observaciones", nulo(request.getObservacionesGenerales()));
    vars.put("cliente", nulo(ClientDatabaseContext.getCurrentClientName()));
    vars.put("empresa_nombre", nulo(request.getEmpresaNombre()));
    vars.put("empresa_ruc", nulo(request.getRuc()));
    vars.put("empresa_telefono", nulo(request.getTelefono()));
    vars.put("empresa_email", nulo(request.getEmail()));
    vars.put("empresa_web", nulo(request.getWeb()));
    vars.put("total_unidades", miles(totalUnidades));
    vars.put("total_pallets", String.valueOf(equipos.size()));
    vars.put("fecha_impresion", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

    Map<String, Object> ctx = new HashMap<>();
    ctx.put("documento", PlantillaTexto.escapar(nulo(request.getPedidoSap())));

    // colores (solo #hex: nada de CSS libre)
    Map<String, Object> col = mapa(cfg.get("colores"));
    Map<String, String> c = new HashMap<>();
    c.put("texto", PlantillaTexto.color(col.get("texto"), "#1b232d"));
    c.put("acento", PlantillaTexto.color(col.get("acento"), "#E47D06"));
    c.put("cabecera", PlantillaTexto.color(col.get("cabecera"), "#1b232d"));
    c.put("cabeceraTexto", PlantillaTexto.color(col.get("cabeceraTexto"), "#ffffff"));
    c.put("etiquetas", PlantillaTexto.color(col.get("etiquetas"), "#5b6573"));
    c.put("fondoEtiquetas", PlantillaTexto.color(col.get("fondoEtiquetas"), "#f5f6f8"));
    c.put("bordes", PlantillaTexto.color(col.get("bordes"), "#c4cad3"));
    c.put("documento", PlantillaTexto.color(col.get("documento"), "#b35f00"));
    c.put("cebra", PlantillaTexto.color(col.get("cebra"), "#f7f8fa"));
    ctx.put("c", c);

    // El logo del cliente (ClienteModel.bandera) es una ruta relativa que solo existe en el servidor del frontend
    // Angular; por eso va embebido como data URI: el que se cargue en Documentos o el de Woden empaquetado.
    String logo = texto(cfg.get("logo"));
    ctx.put("logo_url", logo.startsWith("data:image/") ? logo : cargarLogoWodenBase64());
    ctx.put("logo_alto", (int) PlantillaTexto.numero(cfg.get("logoAlto"), 50, 20, 90));

    Map<String, Object> empresa = mapa(cfg.get("empresa"));
    ctx.put("mostrar_empresa", !Boolean.FALSE.equals(empresa.get("mostrar")));
    List<String> lineas = new ArrayList<>();
    for (Object l : lista(empresa.get("lineas"))) {
      if (!PlantillaTexto.vacia(texto(l), vars)) {
        lineas.add(PlantillaTexto.html(texto(l), vars));
      }
    }
    ctx.put("empresa_lineas", lineas);

    Map<String, Object> control = mapa(cfg.get("control"));
    List<Map<String, String>> controlFilas = new ArrayList<>();
    for (Object o : lista(control.get("filas"))) {
      Map<String, Object> f = mapa(o);
      controlFilas.add(Map.of("etiqueta", PlantillaTexto.html(texto(f.get("etiqueta")), vars),
          "valor", PlantillaTexto.html(texto(f.get("valor")), vars)));
    }
    ctx.put("control_filas", controlFilas);
    ctx.put("mostrar_control", !Boolean.FALSE.equals(control.get("mostrar")) && !controlFilas.isEmpty());

    ctx.put("titulo", PlantillaTexto.html(texto(cfg.get("titulo")), vars));
    ctx.put("subtitulo", PlantillaTexto.html(texto(cfg.get("subtitulo")), vars));

    // campos de dos en dos (etiqueta | valor | etiqueta | valor); el último impar ocupa la fila
    List<Map<String, Object>> campos = new ArrayList<>();
    for (Object o : lista(cfg.get("campos"))) {
      Map<String, Object> f = mapa(o);
      Map<String, Object> campo = new HashMap<>();
      campo.put("etiqueta", PlantillaTexto.html(texto(f.get("etiqueta")), vars));
      campo.put("valor", PlantillaTexto.html(texto(f.get("valor")), vars));
      campo.put("resaltar", Boolean.TRUE.equals(f.get("resaltar")));
      campo.put("ancho", false);
      campos.add(campo);
    }
    List<List<Map<String, Object>>> camposFilas = new ArrayList<>();
    for (int i = 0; i < campos.size(); i += 2) {
      if (i + 1 < campos.size()) {
        camposFilas.add(List.of(campos.get(i), campos.get(i + 1)));
      } else {
        campos.get(i).put("ancho", true);
        camposFilas.add(List.of(campos.get(i)));
      }
    }
    ctx.put("campos_filas", camposFilas);

    // columnas del detalle (anchos normalizados a 100 %)
    List<Map<String, Object>> columnas = new ArrayList<>();
    double suma = 0;
    for (Object o : lista(cfg.get("columnas"))) {
      Map<String, Object> f = mapa(o);
      Map<String, Object> columna = new HashMap<>();
      columna.put("titulo", PlantillaTexto.html(texto(f.get("titulo")), vars));
      columna.put("valor", texto(f.get("valor")));
      double ancho = PlantillaTexto.numero(f.get("ancho"), 10, 2, 90);
      columna.put("anchoBruto", ancho);
      columna.put("alinear", alinear(f.get("alinear")));
      columna.put("estilo", estilo(f.get("estilo")));
      columna.put("total", Boolean.TRUE.equals(f.get("total")));
      suma += ancho;
      columnas.add(columna);
    }
    for (Map<String, Object> columna : columnas) {
      columna.put("ancho", redondo((double) columna.get("anchoBruto") * 100 / (suma <= 0 ? 1 : suma)));
    }
    ctx.put("columnas", columnas);

    Map<Integer, String[]> extra = datosPallets(equipos, columnas);
    List<List<Map<String, String>>> filas = new ArrayList<>();
    int n = 0;
    for (BoletaMovimientoDTO.ItemDTO item : equipos) {
      n++;
      Map<String, String> fv = new HashMap<>(vars);
      fv.put("n", String.valueOf(n));
      fv.put("no_pallet", nulo(item.getNoPallet()));
      fv.put("codigo_sap", nulo(item.getCodigoSap()));
      fv.put("descripcion", nulo(item.getDescripcion()));
      fv.put("cantidad", item.getCantidad() == null ? "" : String.valueOf(item.getCantidad()));
      fv.put("tipologia", nulo(item.getTipologia()));
      String[] ex = item.getId() == null ? null : extra.get(item.getId());
      fv.put("posicion", ex == null ? "" : ex[0]);
      fv.put("lote", ex == null ? "" : ex[1]);
      fv.put("fecha_pallet", ex == null ? "" : ex[2]);
      List<Map<String, String>> fila = new ArrayList<>();
      for (Map<String, Object> columna : columnas) {
        fila.add(Map.of("valor", PlantillaTexto.html((String) columna.get("valor"), fv),
            "estilo", (String) columna.get("estilo"), "alinear", (String) columna.get("alinear")));
      }
      filas.add(fila);
    }
    ctx.put("filas", filas);

    // total bajo la columna marcada como total
    Map<String, Object> totalCfg = mapa(cfg.get("total"));
    Map<String, Object> total = new HashMap<>();
    int iTotal = -1;
    for (int i = 0; i < columnas.size(); i++) {
      if (Boolean.TRUE.equals(columnas.get(i).get("total"))) {
        iTotal = i;
        break;
      }
    }
    boolean mostrarTotal = !Boolean.FALSE.equals(totalCfg.get("mostrar")) && iTotal >= 0;
    total.put("mostrar", mostrarTotal);
    if (mostrarTotal) {
      double antes = 0;
      for (int i = 0; i < iTotal; i++) {
        antes += (double) columnas.get(i).get("ancho");
      }
      double ancho = (double) columnas.get(iTotal).get("ancho");
      total.put("antes", redondo(Math.max(antes, 1)));
      total.put("ancho", redondo(ancho));
      total.put("despues", redondo(Math.max(0, 100 - antes - ancho)));
      total.put("alinear", columnas.get(iTotal).get("alinear"));
      total.put("etiqueta", PlantillaTexto.html(texto(totalCfg.get("etiqueta")), vars));
      total.put("valor", sumaColumna(equipos, (String) columnas.get(iTotal).get("valor"), totalUnidades));
    }
    ctx.put("total", total);

    Map<String, Object> obsCfg = mapa(cfg.get("observaciones"));
    Map<String, Object> observaciones = new HashMap<>();
    observaciones.put("mostrar", !Boolean.FALSE.equals(obsCfg.get("mostrar")));
    observaciones.put("etiqueta", PlantillaTexto.html(texto(obsCfg.get("etiqueta")), vars));
    observaciones.put("texto", nulo(request.getObservacionesGenerales()));
    ctx.put("observaciones", observaciones);

    List<Map<String, String>> firmas = new ArrayList<>();
    for (Object o : lista(cfg.get("firmas"))) {
      Map<String, Object> f = mapa(o);
      String firma = PlantillaTexto.texto(texto(f.get("firma")), vars).trim();
      firmas.add(Map.of("titulo", PlantillaTexto.html(texto(f.get("titulo")), vars),
          "firma", PlantillaTexto.escapar(PlantillaTexto.nombrePropio(firma)),
          "nombre", PlantillaTexto.html(texto(f.get("nombre")), vars)));
    }
    ctx.put("firmas", firmas);
    ctx.put("ancho_firma", firmas.isEmpty() ? 100 : redondo(100.0 / firmas.size()));

    Map<String, Object> pie = mapa(cfg.get("pie"));
    ctx.put("pie_izquierda", PlantillaTexto.html(texto(pie.get("izquierda")), vars));
    ctx.put("pie_derecha", PlantillaTexto.html(texto(pie.get("derecha")), vars));

    // margen superior: el de la plantilla y, si la boleta cabe en una hoja, centrada en la hoja segun sus filas
    Map<String, Object> pagina = mapa(cfg.get("pagina"));
    double margen = PlantillaTexto.numero(pagina.get("margenSuperior"), 10, 0, 60);
    double disponible = HOJA_MM - ENCABEZADO_MM - margen - PIE_MM;
    double estimado = altoEstimado(equipos, columnas, filas, observaciones, firmas, mostrarTotal);
    double centrado = 0;
    if (!Boolean.FALSE.equals(pagina.get("centrar")) && estimado < disponible) {
      centrado = Math.min(MAX_CENTRADO_MM, (disponible - estimado) / 2);
    }
    ctx.put("margen_superior", redondo(ENCABEZADO_MM + margen + centrado));
    return ctx;
  }

  /** Alto aproximado del cuerpo (detalle, total, observaciones, firmas) en mm, para centrar una boleta corta. */
  private double altoEstimado(List<BoletaMovimientoDTO.ItemDTO> equipos, List<Map<String, Object>> columnas,
      List<List<Map<String, String>>> filas, Map<String, Object> observaciones, List<Map<String, String>> firmas,
      boolean total) {
    double alto = 7.5; // títulos del detalle
    for (List<Map<String, String>> fila : filas) {
      int lineas = 1;
      for (int i = 0; i < fila.size() && i < columnas.size(); i++) {
        double anchoMm = (double) columnas.get(i).get("ancho") * ANCHO_UTIL_MM / 100 - 3.2;
        int porLinea = Math.max(4, (int) (anchoMm / 1.45));
        int largo = fila.get(i).get("valor").replaceAll("<[^>]*>", "").length();
        lineas = Math.max(lineas, (int) Math.ceil(largo / (double) porLinea));
      }
      alto += 2.4 + lineas * 3.6;
    }
    if (total) {
      alto += 9;
    }
    if (Boolean.TRUE.equals(observaciones.get("mostrar"))) {
      alto += 22;
    }
    if (!firmas.isEmpty()) {
      alto += 28;
    }
    return alto;
  }

  /** Posición, lote y fecha de cada pallet: solo si alguna columna los usa. */
  private Map<Integer, String[]> datosPallets(List<BoletaMovimientoDTO.ItemDTO> equipos,
      List<Map<String, Object>> columnas) {
    Map<Integer, String[]> out = new HashMap<>();
    boolean usa = columnas.stream().map(col -> (String) col.get("valor"))
        .anyMatch(v -> v.contains("{posicion}") || v.contains("{lote}") || v.contains("{fecha_pallet}"));
    if (!usa || em == null || equipos.isEmpty()) {
      return out;
    }
    try {
      String ids = equipos.stream().map(BoletaMovimientoDTO.ItemDTO::getId).filter(i -> i != null && i > 0)
          .distinct().limit(2000).map(String::valueOf).collect(Collectors.joining(","));
      if (ids.isEmpty()) {
        return out;
      }
      List<?> rs = em.createNativeQuery("SELECT p.Id, ISNULL(pos.Numero, ''), ISNULL(l.nombre, ''), "
          + "CONVERT(varchar(16), p.Fecha, 120) FROM Pallet p LEFT JOIN Posicion pos ON pos.Id = p.PosicionId "
          + "LEFT JOIN Lote l ON l.id = p.loteId WHERE p.Id IN (" + ids + ")").getResultList();
      for (Object f : rs) {
        Object[] r = (Object[]) f;
        out.put(((Number) r[0]).intValue(), new String[] { texto(r[1]), texto(r[2]), texto(r[3]) });
      }
    } catch (Exception e) {
      logger.warn("[BoletaMovimiento] sin datos extra de los pallets: {}", e.getMessage());
    }
    return out;
  }

  /** Total bajo la columna marcada: el número de filas si es la columna #, si no el total de unidades. */
  private static String sumaColumna(List<BoletaMovimientoDTO.ItemDTO> equipos, String valor, int totalUnidades) {
    return "{n}".equals(valor.trim()) ? miles(equipos.size()) : miles(totalUnidades);
  }

  private static String miles(int n) {
    return new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US)).format(n);
  }

  private static String alinear(Object v) {
    String a = texto(v);
    return a.equals("center") || a.equals("right") ? a : "left";
  }

  private static String estilo(Object v) {
    String e = texto(v);
    return switch (e) {
      case "tenue", "mono", "negrita" -> e;
      default -> "";
    };
  }

  private static double redondo(double v) {
    return Math.round(v * 100) / 100.0;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> mapa(Object o) {
    return o instanceof Map<?, ?> m ? (Map<String, Object>) m : new LinkedHashMap<>();
  }

  private static List<?> lista(Object o) {
    return o instanceof List<?> l ? l : List.of();
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o);
  }

  private static String nulo(String s) {
    return s == null ? "" : s;
  }

  private void registrarFuenteFirma(PdfRendererBuilder builder) {
    for (String ruta : FIRMA_SISTEMA) {
      File archivo = new File(ruta);
      if (archivo.isFile()) {
        builder.useFont(archivo, "Firma");
        return;
      }
    }
    if (getClass().getResource(FIRMA_RECURSO) != null) {
      builder.useFont(() -> getClass().getResourceAsStream(FIRMA_RECURSO), "Firma");
    } else {
      logger.warn("[BoletaMovimiento] Sin fuente de firma: {} no esta en el classpath", FIRMA_RECURSO);
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

  private int totalUnidades(List<BoletaMovimientoDTO.ItemDTO> equipos) {
    return equipos.stream()
        .mapToInt(item -> item.getCantidad() != null ? item.getCantidad() : 0)
        .sum();
  }
}
