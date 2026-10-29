package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.woden.wms_backend.dto.clientDTO.PalletReportDTO;
import com.woden.wms_backend.util.CodigoImagen;
import com.woden.wms_backend.util.PlantillaTexto;

import io.pebbletemplates.pebble.PebbleEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Boleta del pallet que viaja entre módulos con la plantilla del cliente (Administración > Documentos > Boleta de
 * pallet). Solo se usa si el cliente activó el editor (usarEditor); si no, JasperReportService genera el Pallet.jasper
 * de siempre. Mismo diseño de fábrica: hoja carta, logo | título | icono y una fila por dato con el código de barras.
 */
@Service
public class PalletDocumentoService {

  private static final Logger logger = LoggerFactory.getLogger(PalletDocumentoService.class);
  /** alto útil para las filas (Jasper: de y=90 a y=740) */
  private static final double ALTO_FILAS = 650;
  /** ancho útil de la hoja en pt (612 - 2 x 5) */
  private static final double ANCHO = 602;

  private final PebbleEngine pebble = new PebbleEngine.Builder().build();
  private String logoBase64;
  private String iconoBase64;

  @Autowired(required = false)
  private DocumentoPlantillaService plantillas;

  @PersistenceContext
  private EntityManager em;

  /** El cliente imprime la boleta de pallet con su plantilla (si falla la lectura, sigue el Jasper). */
  public boolean usaEditor() {
    try {
      return plantillas != null
          && Boolean.TRUE.equals(plantillas.config(DocumentoPlantillaService.PALLET).get("usarEditor"));
    } catch (Exception e) {
      logger.warn("[Pallet] no se pudo leer la plantilla, se usa el Jasper: {}", e.getMessage());
      return false;
    }
  }

  public byte[] generar(PalletReportDTO dto) throws Exception {
    return generar(dto, plantillas.config(DocumentoPlantillaService.PALLET));
  }

  public byte[] generar(PalletReportDTO dto, Map<String, Object> cfg) throws Exception {
    Map<String, String> vars = new HashMap<>();
    vars.put("cliente", nulo(dto.getCliente()));
    vars.put("pallet", nulo(dto.getPallet()));
    vars.put("posicion", nulo(dto.getPosicion()));
    vars.put("fecha", nulo(dto.getFecha()));
    vars.put("origen", nulo(dto.getOrigen()));
    vars.put("destino", nulo(dto.getDestino()));
    vars.put("tipologia", nulo(dto.getTipologia()));
    vars.put("codigo_sap", nulo(dto.getCodigoSap()));
    vars.put("modelo", nulo(dto.getModelo()));
    vars.put("usuario", nulo(dto.getUsuario()));
    vars.put("cantidad", dto.getCantidad() == null ? "" : String.valueOf(dto.getCantidad()));
    String[] extra = datosPallet(dto, cfg);
    vars.put("nombre_usuario", extra[0]);
    vars.put("lote", extra[1]);

    Map<String, Object> ctx = new HashMap<>();
    ctx.put("pallet", PlantillaTexto.escapar(nulo(dto.getPallet())));
    Map<String, Object> col = mapa(cfg.get("colores"));
    Map<String, String> c = new HashMap<>();
    c.put("logoFondo", PlantillaTexto.color(col.get("logoFondo"), "#354A5F"));
    c.put("bordes", PlantillaTexto.color(col.get("bordes"), "#000000"));
    c.put("titulo", PlantillaTexto.color(col.get("titulo"), "#000000"));
    c.put("etiquetas", PlantillaTexto.color(col.get("etiquetas"), "#000000"));
    c.put("fondoEtiquetas", PlantillaTexto.color(col.get("fondoEtiquetas"), "#ffffff"));
    c.put("texto", PlantillaTexto.color(col.get("texto"), "#000000"));
    ctx.put("c", c);

    String logo = texto(cfg.get("logo"));
    String icono = texto(cfg.get("icono"));
    ctx.put("logo", logo.startsWith("data:image/") ? logo : recurso("/reports/Logo.png", true));
    ctx.put("icono", icono.startsWith("data:image/") ? icono : recurso("/reports/Icono.png", false));
    ctx.put("titulo", PlantillaTexto.html(texto(cfg.get("titulo")), vars));
    ctx.put("tamano_titulo", PlantillaTexto.numero(cfg.get("tamanoTitulo"), 18, 8, 40));
    double anchoEtq = PlantillaTexto.numero(cfg.get("anchoEtiqueta"), 41.5, 15, 70);
    ctx.put("ancho_etiqueta", anchoEtq);
    ctx.put("pie", PlantillaTexto.html(texto(cfg.get("pie")), vars));

    // filas: si suman más alto que la hoja se reducen en proporción (nunca pasa a una segunda hoja)
    List<?> filasCfg = cfg.get("filas") instanceof List<?> l ? l : List.of();
    double suma = 0;
    for (Object o : filasCfg) {
      suma += PlantillaTexto.numero(mapa(o).get("alto"), 40, 15, 300);
    }
    double escala = suma > ALTO_FILAS ? ALTO_FILAS / suma : 1;
    double anchoEtqPt = ANCHO * anchoEtq / 100 - 6;
    double anchoValPt = ANCHO - ANCHO * anchoEtq / 100 - 6;
    List<Map<String, Object>> filas = new ArrayList<>();
    for (Object o : filasCfg) {
      Map<String, Object> f = mapa(o);
      double alto = Math.floor(PlantillaTexto.numero(f.get("alto"), 40, 15, 300) * escala);
      String etiqueta = PlantillaTexto.texto(texto(f.get("etiqueta")), vars);
      String valor = PlantillaTexto.texto(texto(f.get("valor")), vars);
      Map<String, Object> fila = new HashMap<>();
      fila.put("alto", alto);
      fila.put("etiqueta", PlantillaTexto.escapar(etiqueta));
      fila.put("valor", PlantillaTexto.escapar(valor));
      fila.put("tamanoEtiqueta", ajustar(etiqueta, PlantillaTexto.numero(f.get("tamanoEtiqueta"), 30, 6, 60), anchoEtqPt, alto));
      fila.put("tamano", ajustar(valor, PlantillaTexto.numero(f.get("tamano"), 30, 6, 60), anchoValPt, alto));
      String tipo = texto(f.get("tipo"));
      if ("barras".equals(tipo) && !valor.isBlank()) {
        fila.put("barras", CodigoImagen.barras(valor));
        // proporción del código del Jasper (281 x 129 en una fila de 140)
        double altoBarras = Math.max(10, alto - 11);
        fila.put("altoBarras", altoBarras);
        fila.put("anchoBarras", Math.min(anchoValPt, altoBarras * 281 / 129));
      } else if ("qr".equals(tipo) && !valor.isBlank()) {
        double lado = Math.max(10, Math.min(alto - 6, anchoValPt));
        fila.put("barras", CodigoImagen.qr(valor));
        fila.put("altoBarras", lado);
        fila.put("anchoBarras", lado);
      }
      filas.add(fila);
    }
    ctx.put("filas", filas);

    StringWriter w = new StringWriter();
    pebble.getTemplate("reports/pallet.html").evaluate(w, ctx);
    Document doc = Jsoup.parse(w.toString());
    doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      PdfRendererBuilder b = new PdfRendererBuilder();
      b.useFastMode();
      b.withW3cDocument(new W3CDom().fromJsoup(doc), "");
      b.toStream(out);
      b.run();
      return out.toByteArray();
    }
  }

  /** Tamaño de letra que cabe en el ancho de la celda (en una línea, como el Jasper) y en su alto. */
  private static double ajustar(String texto, double tamano, double anchoPt, double altoPt) {
    double t = Math.min(tamano, altoPt * 0.8);
    int largo = Math.max(1, texto.length());
    double cabe = anchoPt / (largo * 0.56);
    return Math.max(6, Math.floor(Math.min(t, cabe) * 10) / 10);
  }

  /**
   * El pallet real más reciente del cliente (primero uno con lote) para la vista previa de Documentos; null si no hay.
   * NombreUsuario y lote se completan por su número como en la impresión de verdad.
   */
  public PalletReportDTO palletEjemplo(String cliente) {
    if (em == null) {
      return null;
    }
    try {
      List<?> rs = em.createNativeQuery("SELECT TOP 1 p.Numero, ISNULL(pos.Numero, ''), CONVERT(varchar(19), p.Fecha, 120), "
          + "ISNULL(eo.Nombre, ''), ISNULL(ed.Nombre, ''), ISNULL(t.Codigo, ''), ISNULL(cs.Codigo, ''), "
          + "ISNULL(cs.Descripcion, ''), ISNULL(LTRIM(RTRIM(ISNULL(u.Nombres, '') + ' ' + ISNULL(u.Apellidos, ''))), ''), "
          + "(SELECT COUNT(*) FROM Ingreso i WHERE i.PalletId = p.Id) FROM Pallet p "
          + "LEFT JOIN Posicion pos ON pos.Id = p.PosicionId LEFT JOIN WmsWdGeneral.dbo.Estado eo ON eo.Id = p.OrigenId "
          + "LEFT JOIN WmsWdGeneral.dbo.Estado ed ON ed.Id = p.DestinoId LEFT JOIN Maestro t ON t.Id = p.TipologiaId "
          + "LEFT JOIN CodigoSap cs ON cs.Id = p.CodigoSapId LEFT JOIN WmsWdGeneral.dbo.Usuario u ON u.Id = p.UsuarioId "
          + "WHERE p.Activo = 1 ORDER BY CASE WHEN p.loteId IS NOT NULL THEN 0 ELSE 1 END, p.Id DESC").getResultList();
      if (rs.isEmpty()) {
        return null;
      }
      Object[] r = (Object[]) rs.get(0);
      PalletReportDTO p = new PalletReportDTO();
      p.setCliente(cliente);
      p.setPallet(texto(r[0]));
      p.setPosicion(texto(r[1]));
      p.setFecha(texto(r[2]));
      p.setOrigen(texto(r[3]));
      p.setDestino(texto(r[4]));
      p.setTipologia(texto(r[5]));
      p.setCodigoSap(texto(r[6]));
      p.setModelo(texto(r[7]));
      p.setUsuario(texto(r[8]));
      p.setCantidad(((Number) r[9]).intValue());
      return p;
    } catch (Exception e) {
      logger.warn("[Pallet] sin pallet de ejemplo: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Usuario (NombreUsuario) y lote del pallet: los manda el front o se buscan por el número del pallet, solo si alguna
   * fila los usa.
   */
  private String[] datosPallet(PalletReportDTO dto, Map<String, Object> cfg) {
    String usuario = nulo(dto.getNombreUsuario());
    String lote = nulo(dto.getLote());
    String filas = String.valueOf(cfg.get("filas")) + texto(cfg.get("titulo")) + texto(cfg.get("pie"));
    boolean faltaUsuario = usuario.isBlank() && filas.contains("{nombre_usuario}");
    boolean faltaLote = lote.isBlank() && filas.contains("{lote}");
    if ((faltaUsuario || faltaLote) && em != null && !nulo(dto.getPallet()).isBlank()) {
      try {
        List<?> rs = em.createNativeQuery("SELECT TOP 1 ISNULL(u.NombreUsuario, ''), ISNULL(l.nombre, '') FROM Pallet p "
            + "LEFT JOIN WmsWdGeneral.dbo.Usuario u ON u.Id = p.UsuarioId LEFT JOIN Lote l ON l.id = p.loteId "
            + "WHERE p.Numero = ?1 ORDER BY p.Id DESC").setParameter(1, dto.getPallet().trim()).getResultList();
        if (!rs.isEmpty()) {
          Object[] r = (Object[]) rs.get(0);
          usuario = faltaUsuario ? texto(r[0]) : usuario;
          lote = faltaLote ? texto(r[1]) : lote;
        }
      } catch (Exception e) {
        logger.warn("[Pallet] sin usuario/lote del pallet {}: {}", dto.getPallet(), e.getMessage());
      }
    }
    return new String[] { usuario, lote };
  }

  private String recurso(String ruta, boolean esLogo) {
    String cache = esLogo ? logoBase64 : iconoBase64;
    if (cache != null) {
      return cache;
    }
    try (InputStream in = getClass().getResourceAsStream(ruta)) {
      String v = in == null ? "" : "data:image/png;base64," + Base64.getEncoder().encodeToString(in.readAllBytes());
      if (esLogo) {
        logoBase64 = v;
      } else {
        iconoBase64 = v;
      }
      return v;
    } catch (Exception e) {
      return "";
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> mapa(Object o) {
    return o instanceof Map<?, ?> m ? (Map<String, Object>) m : new LinkedHashMap<>();
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o);
  }

  private static String nulo(String s) {
    return s == null ? "" : s;
  }
}
