package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class CalidadExtrasRequest {
  private String serial;
  private String personal;
  private String dni;
  private String voltaje;
  private String observaciones;
}
