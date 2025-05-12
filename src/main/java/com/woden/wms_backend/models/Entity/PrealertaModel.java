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
    private Integer id;
    private String nombre;
    private Integer tipoOrigenId;
    private String tipoOrigen;
    private Integer origenId;
    private String origen;
    private String guia;
    private Integer usuarioId;
    private Integer activo;
    private String fecha;
    private Integer idResponsable;
    private String estado;
    private Integer idResponsableRecogida;
    private Integer tipologiaId;
}
