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
@Table(name = "Accesorio", schema = "dbo")
public class AccesorioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "CodigoSapId")
    private Integer codigoSapId;
    @Column(name = "TipoAccesorio")
    private String tipoAccesorio;
    @Column(name = "TipoOrigenId")
    private Integer tipoOrigenId;
    @Column(name = "OrigenId")
    private Integer origenId;
    @Column(name = "PalletId")
    private Integer palletId;
    @Column(name = "EstadoId")
    private Integer estadoId;
    @Column(name = "EstadoLimpiezaId")
    private Integer estadoLimpiezaId;
    @Column(name = "Documento")
    private String documento;
    @Transient // ❌ No está en la BD
    private Integer empresaTransportadoraId;
    @Transient // ❌ No está en la BD
    private Integer gaveta;
    @Column(name = "Observacion")
    private String observacion;
    @Column(name = "Guia")
    private String guia;
    @Column(name = "UsuarioId")
    private Integer usuarioId;
    @Column(name = "Fecha")
    private String fecha;
    @Column(name = "SerialEmpaque")
    private String serialEmpaque;
    @Column(name = "Caja")
    private Integer caja;
    @Column(name = "fecha_movimiento")
    private String fechaMovimiento;
    @Column(name = "fechaLimpieza")
    private String fechaLimpieza;
    @Column(name = "fechaEmpaque")
    private String fechaEmpaque;
    @Column(name = "prealertaId")
    private Integer prealertaId;
    @Column(name = "cruce")
    private Boolean cruce;
    @Column(name = "usuarioLimpiezaId")
    private Integer usuarioLimpiezaId;
    @Column(name = "previousPalletId")
    private Integer previousPalletId;
}
