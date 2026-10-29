package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.services.ClienteServices.ClienteConfigService;
import com.woden.wms_backend.services.ClienteServices.CodigoSapReglaService;
import com.woden.wms_backend.services.ClienteServices.EtiquetadoForecastService;
import com.woden.wms_backend.services.ClienteServices.LoadsService;
import com.woden.wms_backend.services.ClienteServices.VariableEtiquetaService;

@RestController
@RequestMapping("/client/parametros")
public class ParametrosClienteController {

  private static final String LECTURA_VARIABLE = "ETIQUETAS_LECTURA_VARIABLE";
  private static final String LARGOS = "ETIQUETAS_LARGOS";
  private static final String MODO_ALMACEN = "BASE_ALMACEN_MODO";

  @Autowired
  private CodigoSapReglaService codigoSapReglas;

  @Autowired
  private EtiquetadoForecastService etiquetadoForecast;

  @Autowired
  private ClienteConfigService config;

  @Autowired
  private LoadsService loads;

  @Autowired
  private VariableEtiquetaService variables;

  @Autowired
  private ObjectMapper json;


  @GetMapping("/codigosap/{codigoSapId}/reglas")
  public ResponseEntity<Map<String, Object>> reglas(@PathVariable int codigoSapId) {
    return ResponseEntity.ok(codigoSapReglas.listar(codigoSapId));
  }

  @PutMapping("/codigosap/{codigoSapId}/reglas")
  public ResponseEntity<Map<String, Object>> guardarReglas(@PathVariable int codigoSapId,
      @RequestBody List<Map<String, Object>> reglas, @RequestParam(defaultValue = "") String usuario) {
    return ResponseEntity.ok(codigoSapReglas.guardar(codigoSapId, reglas, usuario));
  }

  @SuppressWarnings("unchecked")
  @PostMapping("/codigosap/reglas/probar")
  public ResponseEntity<Map<String, String>> probarReglas(@RequestBody Map<String, Object> body) {
    List<Map<String, Object>> reglas = body.get("reglas") instanceof List<?> l ? (List<Map<String, Object>>) l : List.of();
    return ResponseEntity.ok(codigoSapReglas.probar(reglas, texto(body.get("serial")), texto(body.get("mac"))));
  }


  @GetMapping("/etiquetado/forecast")
  public ResponseEntity<Map<String, Object>> forecast() {
    return ResponseEntity.ok(etiquetadoForecast.estado());
  }

  @PutMapping("/etiquetado/forecast")
  public ResponseEntity<Map<String, Object>> guardarForecast(@RequestBody Map<String, Object> valores,
      @RequestParam(defaultValue = "") String usuario) {
    return ResponseEntity.ok(etiquetadoForecast.guardar(valores, usuario));
  }

  @GetMapping("/etiquetas/lectura-variable")
  public ResponseEntity<List<Integer>> lecturaVariable() {
    return ResponseEntity.ok(etiquetasLectura());
  }

  @PutMapping("/etiquetas/{etiquetaId}/lectura-variable")
  public ResponseEntity<List<Integer>> guardarLecturaVariable(@PathVariable int etiquetaId, @RequestParam boolean activo,
      @RequestParam(defaultValue = "") String usuario) {
    List<Integer> ids = new ArrayList<>(etiquetasLectura());
    ids.removeIf(i -> i == etiquetaId);
    if (activo) {
      ids.add(etiquetaId);
    }
    try {
      config.set(LECTURA_VARIABLE, json.writeValueAsString(ids), usuario);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      throw new BusinessRuleException("No se pudo guardar: " + e.getMessage());
    }
    return ResponseEntity.ok(ids);
  }

  @GetMapping("/etiquetas/largos")
  public ResponseEntity<Map<String, String>> largos() {
    return ResponseEntity.ok(largosCampos());
  }

  @PutMapping("/etiquetas/campos/{campoId}/largo")
  public ResponseEntity<Map<String, String>> guardarLargo(@PathVariable int campoId,
      @RequestParam(defaultValue = "") String largo, @RequestParam(defaultValue = "") String usuario) {
    String l = largo.replaceAll("\\s", "");
    if (!l.isEmpty() && !l.matches("\\d{1,3}(\\|\\d{1,3})*")) {
      throw new BusinessRuleException("El largo va como 12 o, si admite varios, 12|16.");
    }
    Map<String, String> m = new LinkedHashMap<>(largosCampos());
    if (l.isEmpty() || l.matches("0+")) {
      m.remove(String.valueOf(campoId));
    } else {
      m.put(String.valueOf(campoId), l);
    }
    try {
      config.set(LARGOS, json.writeValueAsString(m), usuario);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      throw new BusinessRuleException("No se pudo guardar: " + e.getMessage());
    }
    return ResponseEntity.ok(m);
  }


  @GetMapping("/base-almacen")
  public ResponseEntity<Map<String, Object>> baseAlmacen() {
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("modo", config.get(MODO_ALMACEN, "INCLUSION"));
    out.put("registros", loads.contarAlmacen());
    return ResponseEntity.ok(out);
  }

  @PutMapping("/base-almacen/modo")
  public ResponseEntity<Map<String, Object>> modoAlmacen(@RequestParam String modo,
      @RequestParam(defaultValue = "") String usuario) {
    String m = modo == null ? "" : modo.trim().toUpperCase();
    if (!m.equals("INCLUSION") && !m.equals("EXCLUSION")) {
      throw new BusinessRuleException("Modo no válido: " + modo);
    }
    config.set(MODO_ALMACEN, m, usuario);
    return baseAlmacen();
  }

  @GetMapping("/base-almacen/serial")
  public ResponseEntity<Map<String, Object>> serialAlmacen(@RequestParam String serial) {
    String modo = config.get(MODO_ALMACEN, "INCLUSION");
    boolean esta = loads.enAlmacen(serial);
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("modo", modo);
    out.put("enBase", esta);
    out.put("permitido", "EXCLUSION".equals(modo) ? !esta : esta);
    return ResponseEntity.ok(out);
  }


  @GetMapping("/variables")
  public ResponseEntity<Map<String, Object>> variables() {
    return ResponseEntity.ok(variables.listar());
  }

  @PutMapping("/variables")
  public ResponseEntity<Map<String, Object>> guardarVariable(@RequestBody Map<String, Object> variable,
      @RequestParam(defaultValue = "") String usuario) {
    return ResponseEntity.ok(variables.guardar(variable, usuario));
  }

  @DeleteMapping("/variables/{id}")
  public ResponseEntity<Map<String, Object>> eliminarVariable(@PathVariable int id) {
    return ResponseEntity.ok(variables.eliminar(id));
  }

  @SuppressWarnings("unchecked")
  @PostMapping("/variables/probar")
  public ResponseEntity<Map<String, Object>> probarVariable(@RequestBody Map<String, Object> body) {
    Map<String, Object> variable = body.get("variable") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
    return ResponseEntity.ok(variables.probar(variable, texto(body.get("serial"))));
  }

  private List<Integer> etiquetasLectura() {
    try {
      return json.readValue(config.get(LECTURA_VARIABLE, "[]"), new TypeReference<List<Integer>>() {
      });
    } catch (Exception e) {
      return List.of();
    }
  }

  private Map<String, String> largosCampos() {
    try {
      return json.readValue(config.get(LARGOS, "{}"), new TypeReference<LinkedHashMap<String, String>>() {
      });
    } catch (Exception e) {
      return Map.of();
    }
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o).trim();
  }
}
