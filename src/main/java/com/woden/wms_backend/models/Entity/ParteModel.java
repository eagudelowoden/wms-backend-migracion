package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Parte", schema = "dbo")
public class ParteModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int ParteId;
    private int codigoSapId;
    private int palletId;
    private int estadoId;
    private int estadoCosmeticaId;
    private String fecha;
    private int usuarioId;
    private int usuarioIdMovimiento;
    private String fechaMovimiento;
}
