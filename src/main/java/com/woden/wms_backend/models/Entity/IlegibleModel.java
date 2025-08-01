package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Ilegible", schema = "dbo")
public class IlegibleModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int serialId;
    private String consecutivoSerial;
    private String consecutivoMac;
    private String serialConfirmacion;
    private String macConfirmacion;
    private int confirmado;
    private String fecha;
    private String serial3Confirmacion;
    private String serial4Confirmacion;
    private String serial5Confirmacion;
    private String consecutivoSerial3;
    private String consecutivoSerial4;
    private String consecutivoSerial5;
}
