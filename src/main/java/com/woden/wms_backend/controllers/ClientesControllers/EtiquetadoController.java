package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.clientDTO.EtiquetadoRequestDTO;
import com.woden.wms_backend.models.Entity.EtiquetadoModel;
import com.woden.wms_backend.services.ClienteServices.EtiquetadoService;

@RestController
@RequestMapping("/client/etiquetado")
public class EtiquetadoController extends BaseController<EtiquetadoModel, Integer> {

  public EtiquetadoController(EtiquetadoService service) {
    super(service);
  }

  @Autowired
  private EtiquetadoService etiquetadoService;

  @PostMapping("/insertEtiquetado")
  public ResponseEntity<Integer> insertEtiquetado(@RequestBody List<EtiquetadoModel> etiquetados) {
    try {
      for (EtiquetadoModel etiquetado : etiquetados) {
        etiquetadoService.insertEtiquetado(etiquetado.getSerial(), etiquetado.getMac(), etiquetado.getVariable1(),
            etiquetado.getVariable2(), etiquetado.getVariable3(), etiquetado.getVariable4(),
            etiquetado.getReimpresion(), etiquetado.getUsuarioId(), etiquetado.getFecha());
      }
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace(); // 👈 se mantiene simple sin log
      return ResponseEntity.badRequest().body(0);
    }
  }

  /**
   * 🏗️ GENERA CÓDIGO ZPL PARA IMPRIMIR
   * 
   * @param request DTO con todos los datos necesarios (plantillas, seriales,
   *                campos, etc.)
   * @return String con el código ZPL generado
   */
  @PostMapping("/generar")
  public ResponseEntity<String> generarZpl(@RequestBody EtiquetadoRequestDTO request) {
    try {
      // Validaciones básicas
      if (request == null) {
        return ResponseEntity.badRequest().body("❌ Request vacío");
      }

      if (request.getEtiqueta() == null) {
        return ResponseEntity.badRequest().body("❌ Etiqueta no especificada");
      }

      if (request.getListaSeriales() == null || request.getListaSeriales().isEmpty()) {
        return ResponseEntity.badRequest().body("❌ Lista de seriales vacía");
      }

      if (request.getRutaPlantillas() == null || request.getRutaPlantillas().isEmpty()) {
        return ResponseEntity.badRequest().body("❌ Ruta de plantillas no especificada");
      }

      // Generar ZPL
      System.out.println("🔄 Generando ZPL para " + request.getListaSeriales().size() + " seriales");
      System.out.println("📂 Ruta plantillas: " + request.getRutaPlantillas());
      System.out.println("🏷️ Etiqueta: " + request.getEtiqueta().getNombre());
      System.out.println("📊 Lectura Variable: "
          + (request.getSearchLabelVariables() != null && request.getSearchLabelVariables() ? "ACTIVADA"
              : "DESACTIVADA"));

      String zpl = etiquetadoService.generarCodigoZpl(request);

      if (zpl == null || zpl.trim().isEmpty()) {
        System.err.println("❌ ZPL generado está vacío");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("❌ Error: ZPL generado está vacío");
      }

      System.out.println("✅ ZPL generado correctamente (" + zpl.length() + " caracteres)");
      return ResponseEntity.ok(zpl);

    } catch (IllegalArgumentException e) {
      System.err.println("❌ Error de validación: " + e.getMessage());
      return ResponseEntity.badRequest().body("❌ " + e.getMessage());

    } catch (RuntimeException e) {
      System.err.println("❌ Error al leer plantilla: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("❌ " + e.getMessage());

    } catch (Exception e) {
      System.err.println("❌ Error generando ZPL: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ Error interno generando ZPL: " + e.getMessage());
    }
  }
}
