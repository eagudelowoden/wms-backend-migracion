package com.woden.wms_backend.models;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private static int id;
     @Column(nullable = false, unique = true)
    private long identificacion;

    @Column(nullable = false, length = 50)
    private String nombres;

    @Column(nullable = false, length = 50)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(nullable = false, columnDefinition = "NVARCHAR(1000)")
    private String clave;

    @Column(name = "fechaNacimiento")
    private Date fechaNacimiento;

    @Column(length = 50)
    private String correo;

    @Column(nullable = false)
    private int cargoId;

    private int areaId;

    private int temaId;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaUltimoAcceso;

    @Column(length = 15)
    private String ip;

    private int sedeId;

    @Column(nullable = false)
    private boolean activo;

    @Column(nullable = false, columnDefinition = "VARCHAR(2000)")
    private String claveHash;
    // private static long identificacion;
    // private static String nombres;
    // private static String apellidos;
    // private static String nombreUsuario;
    // private static String clave;
    // private static String fechaNacimiento;
    // private static String correo;
    // private static int cargoId;
    // private static String cargo;
    // private static int areaId;
    // private static String area;
    // private static String fechaCreacion;
    // private static String fechaUltimoAcceso;
    // private static String ip;
    // private static int sedeId;
    // private static int activo;
}
