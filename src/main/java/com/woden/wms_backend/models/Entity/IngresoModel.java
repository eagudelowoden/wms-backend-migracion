package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Ingreso", schema = "dbo")
public class IngresoModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String serial;
    private String mac;
    private String serial3;
    private String serial4;
    private String serial5;
    private String smartCard;
    private int codigoSapId;
    private String codigoSap;
    private String descripcion;
    private int palletId;
    private String pallet;
    private String posicion;
    private int palletIdIngreso;
    private int palletIdAlmacen;
    private int palletIdEmpaque;
    private int cajaEmpaqueId;
    private String cajaEmpaque;
    private int cajaDespachoId;
    private int cajaDespacho;
    private int estadoId;
    private String estado;
    private int tipoOrigenId;
    private int origenId;
    private int tipologiaId;
    private String tipologia;
    private int nivelId;
    private int nivel;
    private String tramite;
    private String documento;
    private String guia;
    private int caja;
    private String falla;
    private String tecnicoCliente;
    private int prealertaId;
    private int cruce;
    private String novedad;
    private int garantiaFabricante;
    private int garantiaWoden;
    private int observacionesId;
    private String observaciones;
    private int usuarioId;
    private String usuario;
    private String fecha;
    private String bodega;
    private String estadoCliente;
    private int usuarioIdMovimiento;
    private String fechaMovimiento;
    private int reingreso;
    private int loteId;
    private int smartCardId;
    private String lote;
    private int cajaIngresoId;
    private String cajaIngreso;
    private String numeroSmartcard;
    private String modelo;
    private int modeloId;
    private int fallaCosmeticaId;
    private int fallaFuncionalId;
}
