package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Inventario", schema = "dbo")
public class InventarioModel {
    @Id
    @Column(name = "Id")
    private Integer id;
    private Integer serialId;
    private String serial;
    private String codigoSap;
    private String codigoSapReal;
    private String pallet;
    private String palletReal;
    private Integer estadoId;
    private String estado;
    private String estadoSap;
    private String estadoRR;
    private String ajuste;
    private String fecha;
    private Integer usuarioId;
    private String usuario;
    private String mac;
    private Boolean sobrante;
}
