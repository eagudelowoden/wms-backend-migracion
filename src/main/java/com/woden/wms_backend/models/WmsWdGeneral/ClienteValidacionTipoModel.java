package com.woden.wms_backend.models.WmsWdGeneral;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ClienteValidacionTipo", schema = "dbo")
public class ClienteValidacionTipoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Codigo", nullable = false)
    private String codigo;

    @Column(name = "Descripcion", nullable = false)
    private String descripcion;

    @Column(name = "Activo", nullable = false)
    private Boolean activo;
}
