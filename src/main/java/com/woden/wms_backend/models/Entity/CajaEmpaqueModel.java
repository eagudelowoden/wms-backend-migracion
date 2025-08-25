package com.woden.wms_backend.models.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 👈 importante para que JSON la parsee bien
    private LocalDateTime fecha;
    private int activo;
}
