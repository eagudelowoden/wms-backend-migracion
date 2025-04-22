package com.woden.wms_backend.models.WmsWdGeneral;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.woden.wms_backend.models.Activable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "Usuario", schema = "dbo")
public class UsuarioModel implements Activable {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Identificacion")
    private Long identificacion;

    @Column(name = "Nombres")
    private String nombres;

    @Column(name = "Apellidos")
    private String apellidos;

    @Column(name = "NombreUsuario")
    private String nombreUsuario;

    @Column(name = "Clave")
    private String clave;

    @Column(name = "FechaNacimiento")
    private String fechaNacimiento;

    @Column(name = "Correo")
    private String correo;

    @Column(name = "CargoId")
    private Integer cargoId;

    @Column(name = "AreaId")
    private Integer AreaId;

    @Column(name = "TemaId")
    private Integer temaId;

    @Column(name = "FechaCreacion")
    private String fechaCreacion;

    @Column(name = "FechaUltimoAcceso")
    private String fechaUltimoAcceso;

    @Column(name = "Ip")
    private String ip;

    @Column(name = "SedeId")
    private Integer sedeId;

    @Column(name = "Activo")
    private Boolean activo;

    @Transient
    private String Cargo;
    @Transient
    private String Area;
    
    @Column(name = "clave_hash")
    private String claveHash;
}