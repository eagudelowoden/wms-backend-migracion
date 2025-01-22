package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "PrealertaSerial", schema = "dbo")
public class PrealertaSerialModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int prealertaId;
    private String serial;
    private String mac;
    private String codigoSap;
    private String descripcion;
    private int cantidad;
    private int caja;
    private String falla;
    private String tecnicoCliente;
    private String pedido;
    private String tramite;
    private String novedad;
    private int garantia;
    private int recogida;
    private String tipo;
    private int cantidad_recogida;
    private int en_recogida;
    private int loteId;
}
