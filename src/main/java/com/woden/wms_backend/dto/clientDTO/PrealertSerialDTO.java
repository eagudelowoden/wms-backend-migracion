package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class PrealertSerialDTO {
  private String serial;
  private String mac;
  private String codigoSap;
  private String descripcion;
  private Integer cantidad;
  private Integer caja;
  private String falla;
  private String tecnicoCliente;
  private String pedido;
  private String tramite;
  private String novedad;
  private Boolean garantia;
}
