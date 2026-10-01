package com.woden.wms_backend.dto.clientDTO.codigosap;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MultimodeloRestringidoDTO {
  private Integer codigoSapId;
  private boolean habilitado;
  private boolean restringido;
  private List<SapPermitido> permitidos = new ArrayList<>();

  @Data
  public static class SapPermitido {
    private Integer id;
    private String codigo;
    private String descripcion;
  }
}
