package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Pedido", schema = "dbo")
public class PedidoModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int productoId;
    private String producto;
    private int clienteId;
    private String cliente;
    private int cantidad;
    private String fechaEnvio;
    private String fechaNecesaria;
    private String fechaAprobacion;
    private String fechaEntrega;
    private int remitenteId;
    private String remitente;
    private int destinatarioId;
    private String destinatario;
    private String observaciones;
    private String estado;
}
