package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Ensamble", schema = "dbo")
public class EnsambleModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int serialId;
    private String serial;
    private String mac;
    private String serial3;
    private String serial4;
    private String serial5;
    private int codigoSapId;
    private int palletId;
    private int estadoId;
    private int tipologiaId;
    private int nivelId;
    private int usuarioId;
    private int usuarioIdAsignado;
    private String fecha;
    private int loteId;
    private String smartCard;
}
