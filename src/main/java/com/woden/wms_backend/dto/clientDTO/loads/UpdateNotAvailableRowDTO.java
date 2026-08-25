package com.woden.wms_backend.dto.clientDTO.loads;

import lombok.Data;

@Data
public class UpdateNotAvailableRowDTO {
  private String serial;
  private String mac;
  private int modeloId;
  private int codigoSapId;
  private int tipologiaId;
  private int loteId;
  private int tipoOrigenId;
  private int origenId;
  private String fecha;
  private int usuarioIdMovimiento;
}
