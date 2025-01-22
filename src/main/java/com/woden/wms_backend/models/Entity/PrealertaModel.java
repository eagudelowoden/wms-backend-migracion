package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Prealerta", schema = "dbo")
public class PrealertaModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String nombre;
    private int tipoOrigenId;
    private String tipoOrigen;
    private int origenId;
    private String origen;
    private String guia;
    private int usuarioId;
    private int activo;
    private String fecha;
    private int idResponsable;
    private String estado;
    private int idResponsableRecogida;
    private int tipologiaId;
}
