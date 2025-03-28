package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "CodigoSapId")
    private Integer codigoSapId;
    @Column(name = "TipologiaId")
    private Integer tipologiaId;
    @Column(name = "OrigenId")
    private Integer origenId;
    @Column(name = "DestinoId")
    private Integer destinoId;
    @Column(name = "UsuarioId")
    private Integer usuarioId;
    @Column(name = "Fecha")
    private String fecha;
    @Column(name = "UsuarioIdModifica")
    private Integer usuarioIdModifica;
    @Column(name = "FechaModifica")
    private String fechaModifica;
    @Column(name = "EstadoInventario")
    private Integer estadoInventario;
    @Column(name = "Activo")
    private Integer activo;
    @Column(name = "loteId")
    private Integer loteId;
}
