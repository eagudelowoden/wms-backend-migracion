package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "InventarioTerminado", schema = "dbo")
public class InventarioTerminadoModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String serial;
    private String codigoSap;
    private String codigoSapReal;
    private String pallet;
    private String palletReal;
    private int estadoId;
    private String estadoSap;
    private String estadoRR;
    private String ajuste;
    private String fecha;
    private int usuarioId;
    private String usuario;
    private String fechaTermina;
    private int usuarioIdtermina;
}
