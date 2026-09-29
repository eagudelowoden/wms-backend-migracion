package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name = "BaseInventario", schema = "dbo")
public class BaseInventarioModel {
    @Id 
    @Column(name = "Id")
    private int id;
    private String serial;
    private String codigoSap;
    private String estadoSap;
    private String estadoRR;
}
