package com.woden.wms_backend.models.Entity;

import com.woden.wms_backend.models.Activable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CodigoSap", schema = "dbo")
public class CodigoSapModel implements Activable{
    @Id
    @Column(name = "Id")
    private int id;
    @Column(name = "Codigo")
    private String codigo;
    @Column(name = "Descripcion")
    private String descripcion;
    @Column(name = "FamiliaId")
    private String familia;
    @Column(name = "TipoId")
    private Integer tipoId;
    @Column(name = "Validacion")
    private Integer validacion;
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
    @Column(name = "Valor")
    private Integer valor;
    @Column(name = "Activo")
    private Boolean activo;
    @Column(name = "idTipoLectura")
    private String tipo;
    @Column(name = "tipoEquipoId")
    private Integer tipoEquipoId;
    @Column(name = "areaId")
    private Integer areaId;
    @Column(name = "modeloId")
    private Integer modeloId;
    @Column(name = "proveedorId")
    private Integer proveedorId;
    @Column(name = "validacionMac")
    private Integer validacionMac;
    @Column(name = "direccionMac")
    private String direccionMac;
    @Column(name = "recorteMac")
    private Integer recorteMac;
    @Column(name = "AsignacionFalla")
    private Integer asignarFa;
    @Column(name = "multimodelo")
    private Integer multimodelo;
    @Column(name = "cantidadCaja")
    private Integer cantidadCaja;
    @Column(name = "AsignacionAcc")
    private Integer asignar;
    private String largosMac,largosSerial3,largosSerial4,largosSerial5;
}
