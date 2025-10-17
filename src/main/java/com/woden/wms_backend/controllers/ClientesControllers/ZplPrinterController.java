package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.woden.wms_backend.dto.clientDTO.EtiquetaDatosGeneralesDTO;
import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;
import com.woden.wms_backend.services.ClienteServices.ZplPrinterService;

import lombok.Data;

@RestController
@RequestMapping("/client/zplPrinter")
public class ZplPrinterController {

  @Autowired
  private ZplPrinterService zplPrinterService;

  @PostMapping("/generar")
  public ResponseEntity<String> generarZplMultiple(@RequestBody GenerarZplDTO dto) {
    String plantillaBasePath = dto.getPlantillaBasePath();
    EtiquetaModel etiqueta = dto.getEtiqueta();
    List<EtiquetaCampoModel> campos = dto.getCampos();
    List<IngresoModel> seriales = dto.getSeriales();
    EtiquetaDatosGeneralesDTO datosGenerales = dto.getDatosGenerales();
    String zpl = zplPrinterService.generarZpl(plantillaBasePath, etiqueta, campos, seriales, datosGenerales);
    return ResponseEntity.ok(zpl);
  }

  @PostMapping("/imprimir-zpl")
  public ResponseEntity<String> imprimirZpl(@RequestBody String zpl) {
    return ResponseEntity.ok(zpl);
  }

}

@Data
class GenerarZplDTO {
  private String plantillaBasePath;
  private EtiquetaModel etiqueta;
  private List<EtiquetaCampoModel> campos;
  private List<IngresoModel> seriales;
  private PalletModel palletModel;
  private UsuarioModel usuario;
  private SmartCardModel smartCardModel;
  private EtiquetaDatosGeneralesDTO datosGenerales;
}

@Data
class EtiquetaDTO {
  private String nombre;
  private int impresion;
  // cualquier otro campo necesario
}