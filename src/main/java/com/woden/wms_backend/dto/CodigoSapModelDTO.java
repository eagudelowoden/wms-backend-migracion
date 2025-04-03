package com.woden.wms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Constructor con todos los parámetros
@NoArgsConstructor // Constructor vacío
public class CodigoSapModelDTO {
  private Integer id;
  private String codigo;
  private String descripcion;
  private Integer familiaId;
  private String familia;
  private Integer tipoId;
  private String tipo;
  private Boolean validacion; // BIT -> Boolean
  private String direccion;
  private String largos;
  private Byte recorte; // TINYINT -> Byte
  private Integer reingreso; // BIT -> Boolean
  private Integer numSerial;
  private Boolean asignacionAcc; // BIT -> Boolean
  private String largosMac;
  private String largosSerial3;
  private String largosSerial4;
  private String largosSerial5;
  private String clasificacion;
  private Boolean validacionMac; // BIT -> Boolean
  private String direccionMac;
  private Byte recorteMac; // TINYINT -> Byte
  private Boolean asignacionFalla; // BIT -> Boolean
  private Boolean multimodelo; // BIT -> Boolean
  private Integer cantidadCaja;
}