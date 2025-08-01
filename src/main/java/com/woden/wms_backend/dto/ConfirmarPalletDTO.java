package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class ConfirmarPalletDTO {
  private int palletId;
  private int destinoId;
  private int tipologiaId;
  private int usuarioId;
  private int posicionId;
}
