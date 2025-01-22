package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Etiquetado", schema = "dbo")
public class EtiquetadoModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int serialId;
    private String serial;
    private String mac;
    private int codigoSapId;
    private int nivelId;
    private int loteId;
    private String variable1;
    private String variable2;
    private String variable3;
    private String variable4;
    private String estadoId;
    private int reimpresion;
    private int usuarioId;
    private String fecha;
}
