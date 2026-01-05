package com.woden.wms_backend.dto.clientDTO;

import java.util.List;

import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.models.Entity.IngresoModel;

import lombok.Data;

@Data
public class EtiquetadoRequestDTO {
  private String rutaPlantillas; // Ruta base en el servidor
  private EtiquetaModel etiqueta; // Info de la etiqueta (nombre, cant. impresión)
  private List<EtiquetaCampoModel> camposConfigurados; // Mapeo de campos
  private List<IngresoModel> listaSeriales; // Los datos de la tabla (10, 100, 500 registros)
  private EtiquetaDatosGeneralesDTO datosGenerales; // Datos fijos (Usuario, Fecha, etc)
  private Boolean lecturaVariables;
}