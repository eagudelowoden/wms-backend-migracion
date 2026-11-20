package com.woden.wms_backend.dto.clientDTO.codigosap;

import java.util.List;

import lombok.Data;

@Data
public class CodigoSapPermitidoRequestDTO {
  private Integer codigoSapId;
  private List<Integer> permitidosIds;
}
