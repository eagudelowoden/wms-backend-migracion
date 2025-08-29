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
@Table(name = "Empaque", schema = "dbo")
public class EmpaqueModel {
    @Id
    @Column(name = "Id")
    private Integer id;
    private Integer serialId;
    private String serial;
    private String mac;
    private Integer codigoSapId;
    private Integer palletId;
    private Integer cajaEmpaqueId;
    private Integer nivelId;
    private Integer usuarioId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 👈 importante para que JSON la parsee bien
    private LocalDateTime fecha;
    private Integer loteId;
    private Integer smartCardId;
    private String smartCard;
}
