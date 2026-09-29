package com.woden.wms_backend.models.WmsWdGeneral;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

@NonNull
@Data
@Entity
@Table(name = "Ingreso")
public class IngresoAppModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sn;
    private String cmMac;
    // private String ssid;
    private String wifiPassword;
    // private String settingsPassword;
    private Integer clienteId;
}
