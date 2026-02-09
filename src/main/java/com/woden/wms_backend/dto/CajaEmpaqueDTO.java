package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class CajaEmpaqueDTO {
  private Integer id;
  private String numero;
  private String pallet;
  private Integer cantidad;
  private Integer Seriales;
  private String Estado;

}
