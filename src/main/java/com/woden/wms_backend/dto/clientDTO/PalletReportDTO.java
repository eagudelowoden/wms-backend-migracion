package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class PalletReportDTO {
  private String cliente;
  private String fecha;
  private String pallet;
  private String origen;
  private String destino;
  private String tipologia;
  private String codigoSap;
  private String modelo;
  private String usuario;
  private Integer cantidad;
  private String posicion;
}
