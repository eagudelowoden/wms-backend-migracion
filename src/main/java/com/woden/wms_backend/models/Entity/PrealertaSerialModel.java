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
    private Integer id;
    private Integer prealertaId;
    private String serial;
    private String mac;
    private String codigoSap;
    private String descripcion;
    private Integer cantidad;
    private Integer caja;
    private String falla;
    private String tecnicoCliente;
    private String pedido;
    private String tramite;
    private String novedad;
    private Boolean garantia;
    private Byte recogida;
    private String tipo;
    private Integer cantidad_recogida;
    private Integer en_recogida;
    private Integer loteId;
}
