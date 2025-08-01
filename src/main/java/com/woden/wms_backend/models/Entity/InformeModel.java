package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Informe", schema = "dbo")
public class InformeModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String nombre;
    private String tipo;
    private String consulta;
    private int seccionId;
    private int clienteId;
    private int usuarioId;
    private String fecha;
}
