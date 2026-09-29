package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Correo", schema = "dbo")
public class CorreoModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String tipo;
    private int clienteId;
    private int pedidoId;
    private String asunto;
    private String mensaje;
    private int remitenteId;
    private String remitente;
    private int destinatarioId;
    private String fecha;
    private String estado;
}
