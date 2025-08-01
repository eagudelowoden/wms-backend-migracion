package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class AbrirPalletDTO {
  private int palletId;
    private int origenId;
    private int tipologiaId;
    private int usuarioId;
    private int posicionId;
}
