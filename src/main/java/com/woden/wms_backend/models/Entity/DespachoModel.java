package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Despacho", schema = "dbo")
public class DespachoModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int serialId;
    private String serial;
    private String mac;
    private int codigoSapId;
    private int palletId;
    private int palletIdIngreso;
    private int cajaDespachoId;
    private int estadoId;
    private int tipoOrigenId;
    private int origenId;
    private int tipologiaId;
    private int nivelId;
    private String tramite;
    private String documento;
    private String guia;
    private String falla;
    private int prealertaId;
    private Boolean cruce;
    private String novedad;
    private String pedidoSap;
    private int usuarioId;
    private String fecha;
    private String fechaIngreso;
    private Integer smartCardId;
    private String smartCard;
    private Integer loteId;
    private String serial3;
    private Integer cajaIngresoId;
    private String numeroSmartcard;
    private Integer fallaCosmeticaId;
    private String causa;
    private Integer fallaFuncionalId;
}
