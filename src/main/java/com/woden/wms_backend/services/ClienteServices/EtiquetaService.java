package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.EtiquetaListDTO;
import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetaCampoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetaRepository;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteService;

@Service
public class EtiquetaService extends BaseService<EtiquetaModel, Integer> {
  @Autowired
  private EtiquetaRepository etiquetaRepository;

  @Autowired
  private EtiquetaCampoRepository etiquetaCampoRepository;

  @Autowired
  private ClienteService clienteService;

  @Value("${PRN_LOCAL_PATH:}")
  private String localPrnPath;

  public EtiquetaModel getModelLabel(String nombre) {
    List<Object[]> results = etiquetaRepository.getModelLabel(nombre);

    if (results == null || results.isEmpty()) {
      throw new RuntimeException("No se encontró etiqueta con nombre: " + nombre);
    }

    Object[] obj = results.get(0);
    EtiquetaModel etiqueta = new EtiquetaModel();

    etiqueta.setId((Integer) obj[0]);
    etiqueta.setNombre((String) obj[1]);
    etiqueta.setTipo((String) obj[2]);
    etiqueta.setImpresion((Integer) obj[3]);
    etiqueta.setCodigoSapId((Integer) obj[4]);
    etiqueta.setCodigoSapCombo((String) obj[5] + "|" + (String) obj[6]);


    if (obj.length > 7) {
      etiqueta.setActivo((Boolean) obj[7]);
    }

    return etiqueta;
  }
  public List<EtiquetaListDTO> getListLabel(String tipo) {
    List<Object[]> results = etiquetaRepository.getListLabel(tipo);
    if (results.isEmpty()) {
      return Collections.emptyList();
    }
    List<EtiquetaListDTO> etiquetas = results.stream().map(obj -> {
      EtiquetaListDTO etiqueta = new EtiquetaListDTO();
      etiqueta.setNombre((String) obj[0]);
      return etiqueta;
    }).toList();
    return etiquetas;
  }

  public List<Map<String, Object>> searchLabeled(String nombre, String tipo) {
    List<Object[]> results = etiquetaRepository.searchLabeled(nombre, tipo);
    if (results.isEmpty()) {
      return Collections.emptyList();
    }
    List<Map<String, Object>> etiquetas = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0]);
      map.put("nombre", row[1]);
      etiquetas.add(map);
    }
    return etiquetas;
  }

  public boolean crearDirectorioPrn(String nombre, String tipo) {
    String prnRoute = resolvePrnRoute(tipo);
    if (prnRoute == null) return false;
    File dir = new File(prnRoute, nombre);
    boolean created = dir.mkdirs();
    System.out.println(created ? "Directorio PRN creado: " + dir.getAbsolutePath()
        : "No se pudo crear directorio PRN: " + dir.getAbsolutePath());
    return created;
  }

  public boolean uploadPrn(String nombre, String tipo, int impresion, MultipartFile file) {
    String prnRoute = resolvePrnRoute(tipo);
    if (prnRoute == null) return false;
    File dir = new File(prnRoute, nombre);
    if (!dir.exists()) dir.mkdirs();
    File dest = new File(dir, "codigo" + impresion + ".prn");
    try {
      Files.copy(file.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
      System.out.println("PRN subido: " + dest.getAbsolutePath());
      return true;
    } catch (IOException e) {
      System.err.println("Error al subir PRN: " + e.getMessage());
      return false;
    }
  }

  public List<String> listPrnFiles(String nombre, String tipo) {
    String prnRoute = resolvePrnRoute(tipo);
    if (prnRoute == null) return Collections.emptyList();
    File dir = new File(prnRoute, nombre);
    if (!dir.exists() || !dir.isDirectory()) return Collections.emptyList();
    String[] files = dir.list((d, name) -> name.endsWith(".prn"));
    return files != null ? Arrays.asList(files) : Collections.emptyList();
  }

  public boolean deletePrnFile(String nombre, String tipo, String archivo) {
    String prnRoute = resolvePrnRoute(tipo);
    if (prnRoute == null) return false;
    File file = new File(new File(prnRoute, nombre), archivo);
    if (!file.exists()) return false;
    boolean deleted = file.delete();
    if (deleted) System.out.println("PRN eliminado: " + file.getAbsolutePath());
    return deleted;
  }

  private String resolvePrnRoute(String tipo) {
    if (localPrnPath != null && !localPrnPath.isBlank()) {
      String tipoLower = tipo != null ? tipo.toLowerCase() : "empaque";
      return localPrnPath + File.separator + tipoLower;
    }
    Integer clientId = ClientDatabaseContext.getCurrentClientId();
    ClienteModel cliente = clienteService.getById(clientId);
    if (cliente == null) return null;
    return "ETIQUETADO".equalsIgnoreCase(tipo)
        ? cliente.getPrnEtiquetado()
        : cliente.getPrnEmpaque();
  }

  public String previewPrn(String nombre, String tipo, String archivo) {
    String prnRoute = resolvePrnRoute(tipo);
    if (prnRoute == null) throw new RuntimeException("Ruta de PRN no encontrada");
    File file = new File(new File(prnRoute, nombre), archivo);
    if (!file.exists()) throw new RuntimeException("Archivo no encontrado: " + file.getAbsolutePath());

    try {
      String zpl = Files.readString(file.toPath());
      EtiquetaModel etiqueta = getModelLabel(nombre);
      List<EtiquetaCampoModel> campos = etiquetaCampoRepository.getListLabelField(etiqueta.getId())
          .stream().map(obj -> {
            EtiquetaCampoModel c = new EtiquetaCampoModel();
            c.setNombre((String) obj[0]);
            c.setValor((String) obj[1]);
            return c;
          }).toList();

      int previewCount = Math.min(etiqueta.getImpresion() != null ? etiqueta.getImpresion() : 3, 3);
      String[] sufijosNormales = { "1", "2", "3" };
      String[] valoresMuestra = { "SERIAL-001", "SERIAL-002", "SERIAL-003" };
      String macMuestra = "F8:1A:2B:3C";
      String[] varMuestra = { "COLOR:Rojo", "MODELO:X1", "LOTE:A1", "SN:001" };

      for (int i = 0; i < previewCount; i++) {
        String sufijo = sufijosNormales[i];

        for (EtiquetaCampoModel campo : campos) {
          String muestra = switch (campo.getValor()) {
            case "0" -> valoresMuestra[i];
            case "1" -> macMuestra;
            case "2" -> varMuestra[0] + "-" + (i + 1);
            case "3" -> varMuestra[1] + "-" + (i + 1);
            case "4" -> varMuestra[2] + "-" + (i + 1);
            case "5" -> varMuestra[3] + "-" + (i + 1);
            default -> campo.getValor() + "-" + (i + 1);
          };
          zpl = zpl.replace(campo.getNombre() + sufijo, muestra);
        }

        zpl = zpl.replace("codeinModel" + sufijo, macMuestra.length() >= 4 ? macMuestra.substring(macMuestra.length() - 4) : macMuestra);
        zpl = zpl.replace("fecha" + sufijo, "2026-01-01");
        zpl = zpl.replace("descripcion" + sufijo, "Desc. muestra");
        zpl = zpl.replace("codigosap" + sufijo, "80001234");
        zpl = zpl.replace("usuario" + sufijo, "PREVIEW");
        zpl = zpl.replace("tipologia" + sufijo, "TIP-01");
        zpl = zpl.replace("modelo" + sufijo, "MOD-X1");
        zpl = zpl.replace("codProveedor" + sufijo, "PROV-01");
        zpl = zpl.replace("proveedor" + sufijo, "Proveedor S.A.");
        zpl = zpl.replace("lote" + sufijo, "LOTE-001");
        zpl = zpl.replace("passModel" + sufijo, "PASS-123456");
        zpl = zpl.replace("modelCodigo" + sufijo, "MC-001");
        zpl = zpl.replace("modelDescripcion" + sufijo, "Modelo desc.");
        zpl = zpl.replace("modelDetalle" + sufijo, "Detalle");
        zpl = zpl.replace("unitSerial3" + sufijo, "S3-001");
        zpl = zpl.replace("unitSerial4" + sufijo, "S4-001");
        zpl = zpl.replace("unitSerial5" + sufijo, "S5-001");
        zpl = zpl.replace("variable1_" + sufijo, varMuestra[0]);
        zpl = zpl.replace("variable2_" + sufijo, varMuestra[1]);
        zpl = zpl.replace("variable3_" + sufijo, varMuestra[2]);
        zpl = zpl.replace("variable4_" + sufijo, varMuestra[3]);
      }

      return convertirZplAPngBase64(zpl);
    } catch (IOException e) {
      throw new RuntimeException("Error al leer archivo: " + e.getMessage());
    }
  }

  private String convertirZplAPngBase64(String zpl) {
    try {
      URL url = new URL("http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Accept", "image/png");
      conn.setConnectTimeout(10000);
      conn.setReadTimeout(10000);

      try (OutputStream os = conn.getOutputStream()) {
        os.write(zpl.getBytes(StandardCharsets.UTF_8));
      }

      int responseCode = conn.getResponseCode();
      if (responseCode != 200) {
        throw new RuntimeException("Labelary API error: " + responseCode);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (InputStream in = conn.getInputStream()) {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
          baos.write(buffer, 0, bytesRead);
        }
      }

      String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
      return "data:image/png;base64," + base64Image;
    } catch (Exception e) {
      throw new RuntimeException("Error generando preview: " + e.getMessage());
    }
  }
}
