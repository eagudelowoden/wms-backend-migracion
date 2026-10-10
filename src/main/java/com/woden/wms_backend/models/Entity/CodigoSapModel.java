package com.woden.wms_backend.models.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.woden.wms_backend.util.BooleanAsIntSerializer;
import com.woden.wms_backend.models.Activable;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "CodigoSap")
public class CodigoSapModel implements Activable {
    @Id
    @Column(name = "Id")
    private int id;
    @Column(name = "Codigo")
    private String codigo;
    @Column(name = "Descripcion")
    private String descripcion;
    @Column(name = "FamiliaId")
    private Integer familiaId;
    @Transient
    private String familia;
    @Column(name = "TipoId")
    private Integer tipoId;
    @Transient
    private String tipo;
    @Column(name = "Validacion")
    private Boolean validacion;
    @Column(name = "Direccion")
    private String direccion;
    @Column(name = "Largos")
    private String largos;
    @Column(name = "Recorte")
    private Integer recorte;
    @Column(name = "Reingreso")
    private Integer reingreso;
    @Column(name = "ClasificacionId")
    private Integer clasificacionId;
    @Column(name = "NumSerial")
    private Integer numSerial;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "AsignacionAcc")
    private Boolean asignar;
    @Column(name = "LargosMac")
    private String largosMac;
    @Column(name = "LargosSerial3")
    private String largosSerial3;
    @Column(name = "LargosSerial4")
    private String largosSerial4; 
    @Column(name = "LargosSerial5")
    private String largosSerial5;
    @Transient
    private String clasificacion;
    @Column(name = "validacionMac")
    private Integer validacionMac;
    @Column(name = "direccionMac")
    private String direccionMac;
    @Column(name = "recorteMac")
    private Integer recorteMac;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "AsignacionFalla")
    private Boolean asignarFa;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "multimodelo")
    private Boolean multimodelo;
    @Column(name = "cantidadCaja")
    private Integer cantidadCaja;
    @Column(name = "Valor")
    private Integer valor;
    @Column(name = "tipoEquipoId")
    private Integer tipoEquipoId;
    @Column(name = "areaId")
    private Integer areaId;
    @Column(name = "modeloId")
    private Integer modeloId;
    @Column(name = "proveedorId")
    private Integer proveedorId;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "smartcard")
    private Boolean smartCard;
    @Column(name = "Activo")
    private Boolean activo;

}
