package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class AccesorioSearchDTO {
  private String codigo;
  private String descripcion;
  private String tipoAccesorio;
  private String origen;
  private String documento;
  private String guia;
  private String caja;

  // Getters y setters
}