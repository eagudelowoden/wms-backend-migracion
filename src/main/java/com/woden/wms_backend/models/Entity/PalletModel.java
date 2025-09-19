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
@Table(name = "Pallet", schema = "dbo")
public class PalletModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Numero")
    private String numero;

    @Column(name = "PosicionId")
    private Integer posicionId;

    @Transient // ❌ No está en la BD
    private String posicion;

    @Column(name = "CodigoSapId")
    private Integer codigoSapId;

    @Transient // ❌ No está en la BD
    private String codigoSap;

    @Transient // ❌ No está en la BD
    private String descripcion;

    @Column(name = "TipologiaId")
    private Integer tipologiaId;

    @Transient // ❌ No está en la BD
    private String tipologia;

    @Column(name = "OrigenId")
    private Integer origenId;

    @Transient // ❌ No está en la BD
    private String origen;

    @Column(name = "DestinoId")
    private Integer destinoId;

    @Column(name = "UsuarioId")
    private Integer usuarioId;

    @Transient // ❌ No está en la BD
    private String usuario;

    @Column(name = "Fecha")
    private String fecha;

    @Column(name = "UsuarioIdModifica")
    private Integer usuarioIdModifica;

    @Column(name = "FechaModifica")
    private String fechaModifica;

    @Column(name = "EstadoInventario")
    private Integer estadoInventario;

    @Column(name = "Activo")
    private Boolean activo;

    @Column(name = "loteId")
    private Integer loteId;

    @Transient // ❌ No está en la BD
    private Boolean multimodelo;

    @Transient // ❌ No está en la BD
    private Integer cantidadCaja;

    @Transient // ❌ No está en la BD
    private Boolean smartCard;
}
