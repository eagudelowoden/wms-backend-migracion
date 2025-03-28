package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class IngresoIlegibleDTO {
  private String serial;
  private String mac;
  private int codigoSapId;
  private int origenId;
  private int tipoOrigenId;
  private int estadoId;
  private int palletId;
  private int usuarioId;
  private int tipologiaId;

  private String fecha;
  private String documento;
  private String guia;
}
