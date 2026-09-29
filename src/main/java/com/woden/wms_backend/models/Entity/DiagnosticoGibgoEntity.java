package com.woden.wms_backend.models.Entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Diagnostico", schema = "dbo")
public class DiagnosticoGibgoEntity {
    @Id
    @Column(name = "Id")
    private int id;
    private int test_id;
    private Date start_date;
    private String mac;
    private String serial;
    private String status;
    private String rxpower;
    private String snr;
    private String vendor;
    private String model;
    private String swv;
    private String eth1;
    private int router;
    private int wifi;
    private int mta;
    private int diagnostic_id;
    private String diagnostic;
    private int custom_diagnostic_id;
    private String custom_diagnostic;
    private String username;
    private String workstation;
    private String client;
}
