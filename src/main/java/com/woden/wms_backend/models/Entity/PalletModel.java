package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Pallet", schema = "dbo")
public class PalletModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String numero;
    private int posicionId;
    private String posicion;
    private int codigoSapId;
    private String codigoSap;
    private String descripcion;
    private int tipologiaId;
    private String tipologia;
    private int origenId;
    private String origen;
    private int destinoId;
    private String destino;
    private int usuarioId;
    private String usuario;
    private String fecha;
    private int usuarioIdModifica;
    private String fechaModifica;
    private int estadoInventario;
    private int activo;
    private Integer loteId;
    private int multimodelo;
    private int cantidadCaja;
}
