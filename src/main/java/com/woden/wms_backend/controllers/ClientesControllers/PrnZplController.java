package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.dto.clientDTO.EtiquetaDatosGeneralesDTO;
import com.woden.wms_backend.dto.clientDTO.EtiquetaListDTO;
import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;
import com.woden.wms_backend.services.ClienteServices.EtiquetaService;
import com.woden.wms_backend.services.ClienteServices.ZplPrinterService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/client/prn")
public class PrnZplController {


    @Autowired
    private ZplPrinterService zplPrinterService;

    @Autowired
    private EtiquetaService etiquetaService;

    @PostMapping("/generar")
    public ResponseEntity<String> generarZplMultiple(@RequestBody GenerarZplDTOS dto) {
        String plantillaBasePath = dto.getPlantillaBasePath();
        EtiquetaModel etiqueta = dto.getEtiqueta();
        List<EtiquetaCampoModel> campos = dto.getCampos();
        List<IngresoModel> seriales = dto.getSeriales();
        EtiquetaDatosGeneralesDTO datosGenerales = dto.getDatosGenerales();
        String zpl = zplPrinterService.generarZpl(plantillaBasePath, etiqueta, campos, seriales, datosGenerales);
        return ResponseEntity.ok(zpl);
    }

    @PostMapping("/imprimir-zpl")
    public ResponseEntity<byte[]> imprimirZpl(@RequestBody String zpl) {
        try {
            URL url = new URL("http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/pdf");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(zpl.getBytes(StandardCharsets.UTF_8));
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream in = conn.getInputStream()) {
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) != -1)
                    baos.write(buffer, 0, n);
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"etiqueta.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/vista-previa")
    public ResponseEntity<byte[]> vistaPreviaZpl(@RequestBody String zpl) {
        try {
            URL url = new URL("http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "image/png");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(zpl.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStream in = conn.getInputStream()) {
                byte[] imageBytes = in.readAllBytes();
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(imageBytes);
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

@Data
class GenerarZplDTOS {
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
class EtiquetaDTOS {
    private String nombre;
    private int impresion;
    // cualquier otro campo necesario
}
