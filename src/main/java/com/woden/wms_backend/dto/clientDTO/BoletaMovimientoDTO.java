package com.woden.wms_backend.dto.clientDTO;

import java.util.List;

import lombok.Data;

@Data
public class BoletaMovimientoDTO {
  private String pedidoSap;
  private String origen;
  private String destino;
  private String fechaEntrega;
  private String observacionesGenerales;
  private String usuarioNombre;
  private String empresaNombre;
  private String ruc;
  private String telefono;
  private String email;
  private String web;
  private String logoUrl;
  private List<ItemDTO> equipos;

  @Data
  public static class ItemDTO {
    private Integer id;
    private String noPallet;
    private String codigoSap;
    private String descripcion;
    private Integer cantidad;
    private String tipologia;
  }
}
