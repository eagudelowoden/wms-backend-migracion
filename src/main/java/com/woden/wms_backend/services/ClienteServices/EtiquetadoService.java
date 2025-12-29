package com.woden.wms_backend.services.ClienteServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.clientDTO.EtiquetadoRequestDTO;
import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetadoModel;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetadoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class EtiquetadoService extends BaseService<EtiquetadoModel, Integer> {
  @Autowired
  private EtiquetadoRepository etiquetadoRepository;

  public Integer insertEtiquetado(String serial, String mac, String variable1, String variable2, String variable3,
      String variable4, Integer reImpresion, Integer usuarioId, String fecha) {
    try {
      etiquetadoRepository.insertEtiquetado(serial, mac, variable1, variable2, variable3, variable4, reImpresion,
          usuarioId, fecha);
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  public String generarCodigoZpl(EtiquetadoRequestDTO request) {
    StringBuilder zplFinal = new StringBuilder();

    List<IngresoModel> seriales = request.getListaSeriales();
    int porImpresion = request.getEtiqueta().getImpresion(); // Ej: 2, 4, o 20 etiquetas por hoja
    int total = seriales.size();

    // Cálculos de paginación
    int hojasCompletas = total / porImpresion;
    int residuo = total % porImpresion;
    int indexGeneral = 0;

    // 1. Procesar hojas completas
    if (hojasCompletas > 0) {
      String plantilla = leerPlantilla(request.getRutaPlantillas(), request.getEtiqueta().getNombre(), porImpresion);
      for (int h = 0; h < hojasCompletas; h++) {
        // Extraemos el subgrupo de seriales para esta hoja
        List<IngresoModel> lote = seriales.subList(indexGeneral, indexGeneral + porImpresion);
        zplFinal.append(procesarLote(plantilla, lote, request));
        indexGeneral += porImpresion;
      }
    }

    // 2. Procesar residuo (última hoja incompleta)
    if (residuo > 0) {
      String plantilla = leerPlantilla(request.getRutaPlantillas(), request.getEtiqueta().getNombre(), residuo);
      List<IngresoModel> lote = seriales.subList(indexGeneral, total);
      zplFinal.append(procesarLote(plantilla, lote, request));
    }

    return zplFinal.toString();
  }

  // Lógica central de reemplazo (Reemplaza los Switch Case gigantes del Swing)
  private String procesarLote(String plantillaBase, List<IngresoModel> lote, EtiquetadoRequestDTO request) {
    String zpl = plantillaBase;

    // Reemplazo de Datos Generales (Fecha, Usuario, etc - una vez por hoja)
    // Nota: En tu Swing esto se hacía por serial, pero si son "Generales" deberían
    // ser constantes.
    // Si cambian por posición, el método 'replace' global lo manejará bien.

    for (int i = 0; i < lote.size(); i++) {
      IngresoModel item = lote.get(i);
      String sufijo = obtenerSufijo(i); // La magia: 1-9, 0, A-J

      // A. Campos Dinámicos (Configurados en BD)
      for (EtiquetaCampoModel campo : request.getCamposConfigurados()) {
        // OJO: Aquí debes mapear 'campo.getValor()' (que en swing era indice de
        // columna)
        // a la propiedad real del objeto IngresoModel.
        String valor = obtenerValorPorReflexion(item, campo.getValor());
        zpl = zpl.replace(campo.getNombre() + sufijo, valor);
      }

      // B. Variables Estáticas (Hardcoded en Swing)
      zpl = zpl.replace("fecha" + sufijo, Objects.toString(request.getDatosGenerales().getFecha(), ""));
      zpl = zpl.replace("unitSerial3" + sufijo, Objects.toString(item.getSerial3(), ""));
      zpl = zpl.replace("unitSerial4" + sufijo, Objects.toString(item.getSerial4(), ""));

      // ... Agrega aquí el resto de tus variables (familia, descripcion, etc)
      // zpl = zpl.replace("familia" + sufijo, cortarString(item.getFamilia(), 35));
      zpl = zpl.replace("descripcion" + sufijo, cortarString(item.getDescripcion(), 35));
    }

    // Agregar delimitador para que el front sepa donde corta si necesita preview
    return zpl + "\n^XZ\n^XA\n";
  }

  // Réplica exacta de tu lógica Swing para los sufijos
  private String obtenerSufijo(int index) {
    if (index < 9)
      return String.valueOf(index + 1); // 0->1, 8->9
    if (index == 9)
      return "0"; // 9->0
    return String.valueOf((char) ('A' + (index - 10))); // 10->A, 11->B...
  }

  private String leerPlantilla(String base, String nombre, int cantidad) {
    String path = base + File.separator + nombre + File.separator + "codigo" + cantidad + ".prn";
    try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
      StringBuilder sb = new StringBuilder();
      String linea;
      while ((linea = br.readLine()) != null)
        sb.append(linea).append("\n");
      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException("No se encontró la plantilla: " + path);
    }
  }

  private String cortarString(String str, int len) {
    if (str == null)
      return "";
    return str.length() > len ? str.substring(0, len) : str;
  }

  // Método simple para simular el getValueAt de la JTable
  private String obtenerValorPorReflexion(IngresoModel model, String columnaIndex) {
    // En Swing usabas índices de columna. Aquí mapealos a getters.
    switch (columnaIndex) {
      case "0":
        return model.getSerial(); // Ejemplo
      case "1":
        return model.getSerial();
      // ... COMPLETA ESTO SEGÚN TU TABLA DE SWING
      default:
        return "";
    }
  }
}
