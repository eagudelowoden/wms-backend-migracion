package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CiclicoSerial", schema = "dbo")
public class CiclicoSerialModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int ciclicoId;
    private int serialId;
    private String estado;
    private String novedad;
    private int usuarioId;
    private String fecha;
}
