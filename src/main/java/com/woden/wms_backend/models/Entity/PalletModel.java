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
    private int id;
    @Column(name = "Numero")
    private String numero;
    @Column(name = "PosicionId")
    private int posicionId;
    @Column(name = "CodigoSapId")
    private int codigoSapId;
    @Column(name = "TipologiaId")
    private int tipologiaId;
    @Column(name = "OrigenId")
    private int origenId;
    @Column(name = "DestinoId")
    private int destinoId;
    @Column(name = "UsuarioId")
    private int usuarioId;
    @Column(name = "Fecha")
    private String fecha;
    @Column(name = "UsuarioIdModifica")
    private int usuarioIdModifica;
    @Column(name = "FechaModifica")
    private String fechaModifica;
    @Column(name = "EstadoInventario")
    private int estadoInventario;
    @Column(name = "Activo")
    private int activo;
    @Column(name = "loteId")
    private Integer loteId;
}
