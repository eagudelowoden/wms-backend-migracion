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
@Table(name = "ClienteValidacion", schema = "dbo")
public class ClienteValidacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "ClienteId", nullable = false)
    private Integer clienteId;

    @Column(name = "ValidacionTipoId", nullable = false)
    private Integer validacionTipoId;

    @Column(name = "Activo", nullable = false)
    private Boolean activo;
}
