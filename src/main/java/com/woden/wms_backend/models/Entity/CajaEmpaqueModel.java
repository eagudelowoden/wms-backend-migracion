package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CajaEmpaque", schema = "dbo")
public class CajaEmpaqueModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String numero;
    private int palletId;
    private int estadoId;
    private int usuarioId;
    private String fecha;
    private int activo;
}
