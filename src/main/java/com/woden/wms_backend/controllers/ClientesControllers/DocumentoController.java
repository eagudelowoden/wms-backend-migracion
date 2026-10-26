package com.woden.wms_backend.controllers.ClientesControllers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.BoletaMovimientoDTO;
import com.woden.wms_backend.dto.clientDTO.PalletReportDTO;
import com.woden.wms_backend.services.ClienteServices.BoletaMovimientoService;
import com.woden.wms_backend.services.ClienteServices.BoletaReservaService;
import com.woden.wms_backend.services.ClienteServices.DocumentoPlantillaService;
import com.woden.wms_backend.services.ClienteServices.PalletDocumentoService;
import com.woden.wms_backend.services.ClienteServices.PrealertaCierreService;

/** Administración > Documentos: plantillas editables por cliente, vista previa y consecutivo de la boleta. */
@RestController
@RequestMapping("/client/documentos")
public class DocumentoController {

  @Autowired
  private DocumentoPlantillaService plantillas;

  @Autowired
  private BoletaMovimientoService boletaMovimientoService;

  @Autowired
  private PalletDocumentoService palletDocumentoService;

  @Autowired
  private PrealertaCierreService prealertaCierreService;

  @Autowired
  private BoletaReservaService boletaReservaService;

  @GetMapping("/tipos")
  public ResponseEntity<List<Map<String, String>>> tipos() {
    return ResponseEntity.ok(plantillas.tipos());
  }

  @GetMapping("/consecutivo")
  public ResponseEntity<Map<String, Object>> consecutivo(@RequestParam(defaultValue = "") String zonaHoraria) {
    return ResponseEntity.ok(boletaReservaService.consecutivo(zonaHoraria));
  }

  @PutMapping("/consecutivo")
  public ResponseEntity<Map<String, Object>> fijarConsecutivo(@RequestParam int ultimo,
      @RequestParam(defaultValue = "") String zonaHoraria) {
    return ResponseEntity.ok(boletaReservaService.fijarConsecutivo(ultimo, zonaHoraria));
  }

  @GetMapping("/{tipo}")
  public ResponseEntity<Map<String, Object>> estado(@PathVariable String tipo) {
    return ResponseEntity.ok(plantillas.estado(tipo));
  }

  @PutMapping("/{tipo}")
  public ResponseEntity<Map<String, Object>> guardar(@PathVariable String tipo, @RequestBody Map<String, Object> config,
      @RequestParam(defaultValue = "") String usuario) {
    return ResponseEntity.ok(plantillas.guardar(tipo, config, usuario));
  }

  @DeleteMapping("/{tipo}")
  public ResponseEntity<Map<String, Object>> restablecer(@PathVariable String tipo) {
    return ResponseEntity.ok(plantillas.restablecer(tipo));
  }

  /**
   * Vista previa con datos de ejemplo y la plantilla del editor sin guardar.
   * body = { config: {...}, datos: { empresaNombre, ruc, telefono, email, web, usuario }, manual: true|false }
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/{tipo}/vista-previa")
  public ResponseEntity<byte[]> vistaPrevia(@PathVariable String tipo, @RequestBody Map<String, Object> body)
      throws Exception {
    Map<String, Object> config = plantillas.conCambios(tipo,
        body.get("config") instanceof Map<?, ?> m ? (Map<String, Object>) m : null);
    Map<String, Object> datos = body.get("datos") instanceof Map<?, ?> d ? (Map<String, Object>) d : Map.of();
    String usuario = texto(datos.get("usuario"));
    switch (tipo) {
      case DocumentoPlantillaService.BOLETA -> {
        return pdf(boletaMovimientoService.generarBoleta(boletaEjemplo(datos, usuario), config));
      }
      case DocumentoPlantillaService.PALLET -> {
        return pdf(palletDocumentoService.generar(palletEjemplo(usuario), config));
      }
      default -> {
        String html = prealertaCierreService.vistaPrevia(config, Boolean.TRUE.equals(body.get("manual")), usuario);
        return ResponseEntity.ok().contentType(new MediaType("text", "html", StandardCharsets.UTF_8))
            .body(html.getBytes(StandardCharsets.UTF_8));
      }
    }
  }

  private static ResponseEntity<byte[]> pdf(byte[] contenido) {
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
        .header("Content-Disposition", "inline; filename=vista-previa.pdf").body(contenido);
  }

  private static BoletaMovimientoDTO boletaEjemplo(Map<String, Object> datos, String usuario) {
    BoletaMovimientoDTO r = new BoletaMovimientoDTO();
    r.setPedidoSap("2026-045");
    r.setOrigen("ALMACENAMIENTO");
    r.setDestino("P-CLASIFICACION");
    r.setFechaEntrega(java.time.LocalDate.now().toString());
    r.setObservacionesGenerales("");
    r.setUsuarioNombre(usuario.isBlank() ? "USUARIO DE PRUEBA" : usuario);
    r.setEmpresaNombre(texto(datos.get("empresaNombre")));
    r.setRuc(texto(datos.get("ruc")));
    r.setTelefono(texto(datos.get("telefono")));
    r.setEmail(texto(datos.get("email")));
    r.setWeb(texto(datos.get("web")));
    List<BoletaMovimientoDTO.ItemDTO> items = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      BoletaMovimientoDTO.ItemDTO it = new BoletaMovimientoDTO.ItemDTO();
      it.setId(0);
      it.setNoPallet(String.valueOf(261001124096L + i * 10));
      it.setCodigoSap(i % 2 == 0 ? "HME0004010" : "TMS0003135");
      it.setDescripcion(i % 2 == 0 ? "EMTA DOCSIS3.0 TG2482 ARRIS" : "DECODIFICADOR HD DCX525");
      it.setCantidad(48 + (i * 37) % 300);
      it.setTipologia(i % 3 == 0 ? "LIBRE UTILIZACION" : "EMPAQUE APROBADO");
      items.add(it);
    }
    r.setEquipos(items);
    return r;
  }

  private static PalletReportDTO palletEjemplo(String usuario) {
    PalletReportDTO p = new PalletReportDTO();
    p.setCliente(texto(ClientDatabaseContext.getCurrentClientName()));
    p.setPallet("261025134502");
    p.setPosicion("A-01-02");
    p.setFecha(java.time.LocalDateTime.now().withNano(0).toString().replace('T', ' '));
    p.setOrigen("INGRESO");
    p.setDestino("ALMACENAMIENTO");
    p.setTipologia("LIBRE UTILIZACION");
    p.setCodigoSap("HME0004010");
    p.setModelo("EMTA DOCSIS3.0 TG2482");
    p.setUsuario(usuario.isBlank() ? "usuario" : usuario);
    p.setCantidad(48);
    return p;
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o).trim();
  }
}
