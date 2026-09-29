package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Lote", schema = "dbo")
public class LoteModel {
    @Id
    @Column(name = "Id")
    private Integer Id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}
