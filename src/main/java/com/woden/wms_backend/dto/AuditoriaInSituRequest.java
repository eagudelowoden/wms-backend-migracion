package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class AuditoriaInSituRequest {
  private String serial;
  private Integer fallaFuncionalId;
  private Integer fallaCosmeticaId;
  private Integer usuarioId;
}
