package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Cliente", schema = "dbo")
public class ClienteModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String colorCorporativo;
    private int activo;
}
