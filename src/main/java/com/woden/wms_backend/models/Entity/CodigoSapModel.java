package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CodigoSap", schema = "dbo")
public class CodigoSapModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String codigo;
    private String descripcion;
    private int familiaId,modeloId,proveedorId;
    private String familia;
    private int tipoId,tipoEquipoId,areaId;
    private String tipo;
    private int validacion,validacionMac;
    private String direccion,direccionMac;
    private String largos;
    private int recorte,recorteMac;
    private int reingreso;
    private int clasificacionId;
    private String clasificacion;
    private int numSerial;
    private int valor;
    private int activo;
    private int asignar, asignarFa;
    private String largosMac,largosSerial3,largosSerial4,largosSerial5;
    private int multimodelo;
    private int cantidadCaja;
}
