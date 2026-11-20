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
import java.util.LinkedHashSet;
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
import com.woden.wms_backend.util.CodigoImagen;
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
 */
@Service
public class BoletaMovimientoService {

  private static final Logger logger = LoggerFactory.getLogger(BoletaMovimientoService.class);

  private static final double ENCABEZADO_MM = 65;
  private static final double PIE_MM = 18;
  private static final double HOJA_MM = 297;
  private static final double ANCHO_UTIL_MM = 210 - 26;
  private static final double MAX_CENTRADO_MM = 45;
  private static final double SOBRE_TITULO_MM = 10.5;
  private static final double BAJO_TITULO_MM = 2;
  private static final double FIRMA_NOMBRE_MM = 0.8;
  private static final double QR_MM = 14;
  private static final double BARRAS_MM = 9;
  private static final double FILA_CAMPOS_MM = 6.4;

  private final PebbleEngine pebbleEngine = new PebbleEngine.Builder().build();

  // Cacheado en memoria: se lee y codifica una sola vez, no en cada boleta.
  private String logoWodenBase64;

  // firma en cursiva: uso Brush Script MT si el servidor la tiene; si no, Great Vibes (OFL), que va en el
  // backend. OpenHTMLtoPDF no toma solo las fuentes del sistema
  private static final String[] FIRMA_SISTEMA = {
      "C:/Windows/Fonts/BRUSHSCI.TTF", "C:/Windows/Fonts/brushsci.ttf" };
  private static final String FIRMA_RECURSO = "/reports/fonts/GreatVibes-Regular.ttf";

  @Autowired(required = false)
  private DocumentoPlantillaService plantillas;

  @PersistenceContext
  private EntityManager em;

  public byte[] generarBoleta(BoletaMovimientoDTO request) throws Exception {
    Map<String, Object> config = plantillas != null ? plantillas.config(DocumentoPlantillaService.BOLETA) : null;
    return generarBoleta(request, config);
  }

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
    String todo = String.valueOf(cfg);

    Map<Integer, String[]> extra = datosPallets(equipos, todo);
    List<String[]> porItem = new ArrayList<>();
    LinkedHashSet<String> lotes = new LinkedHashSet<>();
    int totalCajas = 0;
    for (BoletaMovimientoDTO.ItemDTO item : equipos) {
      String[] ex = item.getId() == null ? null : extra.get(item.getId());
      String lote = ex != null ? ex[1] : nulo(item.getLote());
      String cajas = ex != null ? ex[3]
          : item.getCajas() == null ? "" : item.getCajas() == 0 ? "-" : String.valueOf(item.getCajas());
      porItem.add(new String[] { ex == null ? "" : ex[0], lote, ex == null ? "" : ex[2], cajas });
      if (!lote.isBlank()) {
        lotes.add(lote);
      }
      totalCajas += cajas.matches("\\d+") ? Integer.parseInt(cajas) : 0;
    }

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
    vars.put("total_equipos", miles(suma(equipos, BoletaMovimientoDTO.ItemDTO::getCantidadEquipos)));
    vars.put("total_accesorios", miles(suma(equipos, BoletaMovimientoDTO.ItemDTO::getCantidadAccesorios)));
    vars.put("total_smartcards", miles(suma(equipos, BoletaMovimientoDTO.ItemDTO::getCantidadSmartcard)));
    vars.put("total_pallets", String.valueOf(equipos.size()));
    vars.put("fecha_impresion", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    vars.put("lotes", String.join(", ", lotes));
    vars.put("total_cajas", miles(totalCajas));
    vars.put("responsable_op", todo.contains("{responsable_op}") ? responsableOp() : "");

    Map<String, Object> ctx = new HashMap<>();
    ctx.put("documento", PlantillaTexto.escapar(nulo(request.getPedidoSap())));

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

    // el logo del cliente es una ruta del servidor del front: lo embebo como data URI
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

    List<Map<String, Object>> campos = new ArrayList<>();
    for (Object o : lista(cfg.get("campos"))) {
      Map<String, Object> f = mapa(o);
      Map<String, Object> campo = new HashMap<>();
      campo.put("etiqueta", PlantillaTexto.html(texto(f.get("etiqueta")), vars));
      campo.put("valor", texto(f.get("valor")));
      campo.put("tipo", tipoCodigo(f.get("tipo")));
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
    double extraCampos = 0;
    for (List<Map<String, Object>> fila : camposFilas) {
      double altoImagen = 0;
      for (Map<String, Object> campo : fila) {
        double anchoMm = ANCHO_UTIL_MM * (Boolean.TRUE.equals(campo.get("ancho")) ? 0.83 : 0.33) - 5;
        String valor = (String) campo.get("valor");
        String tipo = (String) campo.get("tipo");
        String plano = PlantillaTexto.texto(valor, vars).trim();
        String img = tipo.isEmpty() || plano.isEmpty() ? null : imagen(tipo, plano, anchoMm);
        campo.put("valor", img != null ? img : PlantillaTexto.html(valor, vars));
        if (img != null) {
          altoImagen = Math.max(altoImagen, altoImagen(tipo, anchoMm));
        }
      }
      extraCampos += altoImagen > 0 ? Math.max(0, altoImagen + 2.2 - FILA_CAMPOS_MM) : 0;
    }
    ctx.put("campos_filas", camposFilas);

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
    double altoImagenFila = 0;
    for (Map<String, Object> columna : columnas) {
      columna.put("ancho", redondo((double) columna.get("anchoBruto") * 100 / (suma <= 0 ? 1 : suma)));
      double anchoMm = (double) columna.get("ancho") * ANCHO_UTIL_MM / 100 - 3.2;
      columna.put("anchoMm", anchoMm);
      String estilo = (String) columna.get("estilo");
      if (estilo.equals("qr") || estilo.equals("barras")) {
        altoImagenFila = Math.max(altoImagenFila, altoImagen(estilo, anchoMm));
      }
    }
    ctx.put("columnas", columnas);

    List<List<Map<String, String>>> filas = new ArrayList<>();
    long[] sumas = new long[columnas.size()];
    int n = 0;
    for (BoletaMovimientoDTO.ItemDTO item : equipos) {
      n++;
      Map<String, String> fv = new HashMap<>(vars);
      fv.put("n", String.valueOf(n));
      fv.put("no_pallet", nulo(item.getNoPallet()));
      fv.put("codigo_sap", nulo(item.getCodigoSap()));
      fv.put("descripcion", nulo(item.getDescripcion()));
      fv.put("cantidad", item.getCantidad() == null ? "" : String.valueOf(item.getCantidad()));
      fv.put("equipos", item.getCantidadEquipos() == null ? "" : String.valueOf(item.getCantidadEquipos()));
      fv.put("accesorios", item.getCantidadAccesorios() == null ? "" : String.valueOf(item.getCantidadAccesorios()));
      fv.put("smartcards", item.getCantidadSmartcard() == null ? "" : String.valueOf(item.getCantidadSmartcard()));
      fv.put("tipologia", nulo(item.getTipologia()));
      String[] ex = porItem.get(n - 1);
      fv.put("posicion", ex[0]);
      fv.put("lote", ex[1]);
      fv.put("fecha_pallet", ex[2]);
      fv.put("cajas", ex[3]);
      List<Map<String, String>> fila = new ArrayList<>();
      for (int k = 0; k < columnas.size(); k++) {
        Map<String, Object> columna = columnas.get(k);
        String estilo = (String) columna.get("estilo");
        String valor = (String) columna.get("valor");
        if (Boolean.TRUE.equals(columna.get("total"))) {
          sumas[k] += numero(PlantillaTexto.texto(valor, fv));
        }
        String img = null;
        if (estilo.equals("qr") || estilo.equals("barras")) {
          String plano = PlantillaTexto.texto(valor, fv).trim();
          img = plano.isEmpty() ? null : imagen(estilo, plano, (double) columna.get("anchoMm"));
        }
        fila.add(Map.of("valor", img != null ? img : PlantillaTexto.html(valor, fv),
            "estilo", estilo, "alinear", (String) columna.get("alinear")));
      }
      filas.add(fila);
    }
    ctx.put("filas", filas);

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
      List<Map<String, Object>> celdas = new ArrayList<>();
      for (int i = iTotal; i < columnas.size(); i++) {
        Map<String, Object> columna = columnas.get(i);
        boolean conTotal = Boolean.TRUE.equals(columna.get("total"));
        String v = !conTotal ? "" : "{n}".equals(((String) columna.get("valor")).trim()) ? miles(equipos.size())
            : miles(sumas[i]);
        celdas.add(Map.of("ancho", redondo((double) columna.get("ancho")), "alinear", columna.get("alinear"), "valor", v));
      }
      total.put("antes", redondo(Math.max(antes, 1)));
      total.put("celdas", celdas);
      total.put("etiqueta", PlantillaTexto.html(texto(totalCfg.get("etiqueta")), vars));
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

    Map<String, Object> espacios = mapa(cfg.get("espacios"));
    double sobreTitulo = PlantillaTexto.numero(espacios.get("sobreTitulo"), SOBRE_TITULO_MM, 0, 40);
    double bajoTitulo = PlantillaTexto.numero(espacios.get("bajoTitulo"), BAJO_TITULO_MM, 0, 40);
    double firmaNombre = PlantillaTexto.numero(espacios.get("firmaNombre"), FIRMA_NOMBRE_MM, 0, 40);
    ctx.put("titulo_margen", redondo(sobreTitulo * 0.6));
    ctx.put("titulo_relleno", redondo(sobreTitulo * 0.4));
    ctx.put("bajo_titulo", redondo(bajoTitulo));
    ctx.put("firma_nombre", redondo(firmaNombre));
    double encabezado = ENCABEZADO_MM + (sobreTitulo - SOBRE_TITULO_MM) + (bajoTitulo - BAJO_TITULO_MM) + extraCampos;

    Map<String, Object> pie = mapa(cfg.get("pie"));
    ctx.put("pie_izquierda", PlantillaTexto.html(texto(pie.get("izquierda")), vars));
    ctx.put("pie_derecha", PlantillaTexto.html(texto(pie.get("derecha")), vars));

    Map<String, Object> pagina = mapa(cfg.get("pagina"));
    double margen = PlantillaTexto.numero(pagina.get("margenSuperior"), 10, 0, 60);
    double disponible = HOJA_MM - encabezado - margen - PIE_MM;
    double estimado = altoEstimado(equipos, columnas, filas, observaciones, firmas, mostrarTotal, altoImagenFila)
        + (firmas.isEmpty() ? 0 : firmaNombre - FIRMA_NOMBRE_MM);
    double centrado = 0;
    if (!Boolean.FALSE.equals(pagina.get("centrar")) && estimado < disponible) {
      centrado = Math.min(MAX_CENTRADO_MM, (disponible - estimado) / 2);
    }
    ctx.put("margen_superior", redondo(encabezado + margen + centrado));
    return ctx;
  }

  private double altoEstimado(List<BoletaMovimientoDTO.ItemDTO> equipos, List<Map<String, Object>> columnas,
      List<List<Map<String, String>>> filas, Map<String, Object> observaciones, List<Map<String, String>> firmas,
      boolean total, double altoImagenFila) {
    double alto = 7.5;
    for (List<Map<String, String>> fila : filas) {
      int lineas = 1;
      for (int i = 0; i < fila.size() && i < columnas.size(); i++) {
        double anchoMm = (double) columnas.get(i).get("ancho") * ANCHO_UTIL_MM / 100 - 3.2;
        int porLinea = Math.max(4, (int) (anchoMm / 1.45));
        String estilo = fila.get(i).get("estilo");
        int largo = estilo.equals("qr") || estilo.equals("barras") ? 0
            : fila.get(i).get("valor").replaceAll("<[^>]*>", "").length();
        lineas = Math.max(lineas, (int) Math.ceil(largo / (double) porLinea));
      }
      alto += 2.4 + Math.max(lineas * 3.6, altoImagenFila);
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

  private Map<Integer, String[]> datosPallets(List<BoletaMovimientoDTO.ItemDTO> equipos, String plantilla) {
    Map<Integer, String[]> out = new HashMap<>();
    boolean usa = List.of("{posicion}", "{lote}", "{fecha_pallet}", "{cajas}", "{lotes}", "{total_cajas}").stream()
        .anyMatch(plantilla::contains);
    if (!usa || em == null || equipos.isEmpty()) {
      return out;
    }
    String ids = equipos.stream().map(BoletaMovimientoDTO.ItemDTO::getId).filter(i -> i != null && i > 0)
        .distinct().limit(2000).map(String::valueOf).collect(Collectors.joining(","));
    if (ids.isEmpty()) {
      return out;
    }
    // cajas como en Despacho: las de CajaDespacho activas o, si no hay, las cajas de empaque de sus equipos
    String cajasEmpaque = "(SELECT COUNT(DISTINCT ie.CajaEmpaqueId) FROM Ingreso ie WHERE ie.PalletId = p.Id "
        + "AND ie.CajaEmpaqueId > 0)";
    String cajasDespacho = "COALESCE(NULLIF((SELECT COUNT(*) FROM CajaDespacho cd WHERE cd.palletId = p.Id "
        + "AND cd.Activo = true), 0), " + cajasEmpaque + ")";
    for (String cajas : List.of(cajasDespacho, cajasEmpaque, "''")) {
      try {
        List<?> rs = em.createNativeQuery("SELECT p.Id, COALESCE(pos.Numero, ''), COALESCE(l.nombre, ''), "
            + "to_char(p.Fecha, 'YYYY-MM-DD HH24:MI'), " + cajas + " FROM Pallet p "
            + "LEFT JOIN Posicion pos ON pos.Id = p.PosicionId LEFT JOIN Lote l ON l.id = p.loteId "
            + "WHERE p.Id IN (" + ids + ")").getResultList();
        for (Object f : rs) {
          Object[] r = (Object[]) f;
          String nCajas = texto(r[4]);
          out.put(((Number) r[0]).intValue(), new String[] { texto(r[1]), texto(r[2]), texto(r[3]),
              nCajas.isEmpty() || nCajas.equals("0") ? "-" : nCajas });
        }
        return out;
      } catch (Exception e) {
        logger.warn("[BoletaMovimiento] datos de los pallets: {}", e.getMessage());
      }
    }
    return out;
  }

  public List<BoletaMovimientoDTO.ItemDTO> palletsEjemplo() {
    List<BoletaMovimientoDTO.ItemDTO> out = new ArrayList<>();
    if (em == null) {
      return out;
    }
    try {
      // cuento igual que pa_SearchPalletBoxDispatchPallet; primero los pallets con accesorios o smartcards
      for (Object f : em.createNativeQuery("SELECT p.Id, p.Numero, COALESCE(cs.Codigo, ''), COALESCE(cs.Descripcion, ''), "
          + "x.Equipos, y.Accesorios, x.Smartcards, COALESCE(t.Codigo, '') "
          + "FROM (SELECT Id, Numero, CodigoSapId, TipologiaId, loteId FROM Pallet WHERE Activo = true ORDER BY Id DESC LIMIT 300) p "
          + "LEFT JOIN CodigoSap cs ON cs.Id = p.CodigoSapId LEFT JOIN Maestro t ON t.Id = p.TipologiaId "
          + "CROSS JOIN LATERAL (SELECT COUNT(CASE WHEN COALESCE(m.Codigo, '') <> 'SMARTCARD' THEN 1 END) AS Equipos, "
          + "COUNT(CASE WHEN m.Codigo = 'SMARTCARD' THEN 1 END) AS Smartcards FROM Ingreso i "
          + "LEFT JOIN CodigoSap c2 ON c2.Id = i.CodigoSapId LEFT JOIN Maestro m ON m.Id = c2.TipoId "
          + "WHERE i.PalletId = p.Id) x "
          + "CROSS JOIN LATERAL (SELECT COUNT(*) AS Accesorios FROM Accesorio a WHERE a.PalletId = p.Id) y "
          + "WHERE x.Equipos + x.Smartcards + y.Accesorios > 0 "
          + "ORDER BY CASE WHEN y.Accesorios > 0 OR x.Smartcards > 0 THEN 0 ELSE 1 END, "
          + "CASE WHEN p.loteId IS NOT NULL THEN 0 ELSE 1 END, p.Id DESC LIMIT 8").getResultList()) {
        Object[] r = (Object[]) f;
        int equipos = ((Number) r[4]).intValue(), accesorios = ((Number) r[5]).intValue();
        int smartcards = ((Number) r[6]).intValue();
        BoletaMovimientoDTO.ItemDTO it = new BoletaMovimientoDTO.ItemDTO();
        it.setId(((Number) r[0]).intValue());
        it.setNoPallet(texto(r[1]));
        it.setCodigoSap(texto(r[2]));
        it.setDescripcion(texto(r[3]));
        it.setCantidad(equipos > 0 ? equipos : accesorios > 0 ? accesorios : smartcards);
        it.setCantidadEquipos(equipos);
        it.setCantidadAccesorios(accesorios);
        it.setCantidadSmartcard(smartcards);
        it.setTipologia(texto(r[7]));
        out.add(it);
      }
    } catch (Exception e) {
      logger.warn("[BoletaMovimiento] sin pallets de ejemplo: {}", e.getMessage());
    }
    return out;
  }

  private String responsableOp() {
    if (em == null) {
      return "";
    }
    try {
      List<?> rs = em.createNativeQuery("SELECT COALESCE(NULLIF(TRIM(m.Descripcion), ''), m.Codigo) FROM Maestro m "
          + "INNER JOIN TipoMaestro tm ON tm.Id = m.TipoMaestroId WHERE tm.Nombre = 'Responsable Op' AND m.Activo = true "
          + "ORDER BY m.Id").getResultList();
      return rs.stream().map(BoletaMovimientoService::texto).map(String::trim).filter(v -> !v.isEmpty())
          .collect(Collectors.joining(" / "));
    } catch (Exception e) {
      logger.warn("[BoletaMovimiento] sin responsable de operación: {}", e.getMessage());
      return "";
    }
  }

  private static String imagen(String tipo, String valor, double anchoMm) {
    try {
      double ancho;
      double alto;
      String uri;
      if (tipo.equals("qr")) {
        ancho = Math.min(QR_MM, anchoMm);
        alto = ancho;
        uri = CodigoImagen.qr(valor);
      } else {
        CodigoImagen.Imagen img = CodigoImagen.barrasImagen(valor);
        ancho = Math.min(anchoMm, BARRAS_MM * img.proporcion());
        alto = ancho / img.proporcion();
        uri = img.uri();
      }
      return "<img src=\"" + uri + "\" alt=\"\" style=\"width:" + redondo(ancho) + "mm;height:" + redondo(alto)
          + "mm\"/>";
    } catch (Exception e) {
      logger.warn("[BoletaMovimiento] {} de '{}' no se generó: {}", tipo, valor, e.getMessage());
      return null;
    }
  }

  private static double altoImagen(String tipo, double anchoMm) {
    return tipo.equals("qr") ? Math.min(QR_MM, anchoMm) : BARRAS_MM;
  }

  private static String tipoCodigo(Object v) {
    String t = texto(v);
    return t.equals("qr") || t.equals("barras") ? t : "";
  }

  private static long numero(String valor) {
    String d = valor == null ? "" : valor.replaceAll("[^0-9]", "");
    try {
      return d.isEmpty() ? 0 : Long.parseLong(d);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static int suma(List<BoletaMovimientoDTO.ItemDTO> equipos,
      java.util.function.Function<BoletaMovimientoDTO.ItemDTO, Integer> campo) {
    return equipos.stream().map(campo).mapToInt(v -> v == null ? 0 : v).sum();
  }

  private static String miles(long n) {
    return new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US)).format(n);
  }

  private static String alinear(Object v) {
    String a = texto(v);
    return a.equals("center") || a.equals("right") ? a : "left";
  }

  private static String estilo(Object v) {
    String e = texto(v);
    return switch (e) {
      case "tenue", "mono", "negrita", "qr", "barras" -> e;
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
