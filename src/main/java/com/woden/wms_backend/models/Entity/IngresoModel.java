package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "Ingreso", schema = "dbo")
public class IngresoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;
    private String serial;
    private String mac;
    private String serial3;
    private String serial4;
    private String serial5;
    private String smartCard;
    private Integer codigoSapId;    
    @Transient // ❌ No está en la BD
    private String codigoSap;
    @Transient // ❌ No está en la BD
    private String descripcion;
    private Integer palletId;
    @Transient // ❌ No está en la BD
    private String pallet;
    @Transient // ❌ No está en la BD
    private String posicion;
    private Integer palletIdIngreso;
    private Integer palletIdAlmacen;
    private Integer palletIdEmpaque;
    private Integer cajaEmpaqueId;
    @Transient // ❌ No está en la BD
    private Integer cajaDespachoId;
    private Integer estadoId;
    private Integer tipoOrigenId;
    private Integer origenId;
    private Integer tipologiaId;
    private Integer nivelId;
    private String tramite;
    private String documento;
    private String guia;
    private Integer caja;
    private String falla;
    private String tecnicoCliente;
    private Integer prealertaId;
    private Boolean cruce;
    private String novedad;
    private Boolean garantiaFabricante;
    private Integer garantiaWoden;
    private String observaciones;
    private Integer usuarioId;
    private String bodega;
    private String estadoCliente;
    private String estadoEquipo;
    private String estadoActual;
    private Integer usuarioIdMovimiento;
    private String fechaMovimiento;
    private Integer transportadoraId;
    private Integer ciudadId;
    private Integer loteId;
    private Integer smartCardId;
    private Integer cajaIngresoId;
    private String cajaIngreso;
    private String numeroSmartcard;
    private Integer modeloId;
    private Integer fallaCosmeticaId;
    private Integer fallaFuncionalId;
    private String causa;
    private String fecha;
    @Transient // ❌ No está en la BD
    private String cajaEmpaque;
    @Transient
    private String estado;
    @Transient
    private String tipologia;
    @Transient
    private String nivel;
    private String usuario;
    private String cajaDespacho;
    private String Lote;
    private String modelo;
}
