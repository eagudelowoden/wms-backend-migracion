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
  // al reservar: quien la guarda y su zona horaria (año del consecutivo)
  private Integer usuarioId;
  private String zonaHoraria;

  @Data
  public static class ItemDTO {
    private Integer id;
    private String noPallet;
    private String codigoSap;
    private String descripcion;
    private Integer cantidad;
    private String tipologia;
    // opcionales: si no llegan se buscan por el Id del pallet (la vista previa los manda de ejemplo)
    private String lote;
    private Integer cajas;
  }
}
