package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class PalletStorageDTO {
  private Integer id;
  private String numero;
  private Integer cantidad;
  private String origen;
  private String tipologia;
  private String lote;
  private String posicion;
}
