package com.woden.wms_backend.models.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table (name = "Accesorio", schema = "dbo")
public class AccesorioModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int codigoSapId;
    private String tipoAccesorio;
    private int tipoOrigenId;
    private int origenId;
    private int palletId;
    private int estadoId;
    private int estadoLimpiezaId;
    private String documento;
    private int empresaTransportadoraId;
    private int gaveta;
    private String observacion;
    private String guia;
    private int usuarioId;
    private String fecha;
    private String serialEmpaque;
    private int caja;
    private String fechaLimpieza;
    private String fechaEmpaque;
    private int prealertaId;
    private int cruce;
    private int usuarioLimpiezaId;
}
