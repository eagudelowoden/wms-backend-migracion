package com.woden.wms_backend.dto.clientDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturaVariableDTO {

  // Variables básicas
  private String serial;
  private String mac;
  private String fecha;

  // Variables de maestro
  private String familia;
  private String descripcion;
  private String codigoSap;
  private String tipologia;
  private String modelo;
  private String codProveedor;
  private String proveedor;
  private String lote;

  // Variables adicionales de modelo
  private String passModel; // Serial truncado a 12 caracteres
  private String codeInModel; // Últimos 4 dígitos del MAC
  private String modelCodigo;
  private String modelDescripcion;
  private String modelDetalle;
  private String unitSerial3;
  private String unitSerial4;
}