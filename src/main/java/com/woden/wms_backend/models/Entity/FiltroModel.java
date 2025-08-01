package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Filtro", schema = "dbo")
public class FiltroModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String nombre;
    private String tipo;
    private String consulta;
    private String bd;
}
