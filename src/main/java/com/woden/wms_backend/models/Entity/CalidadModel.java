package com.woden.wms_backend.models.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Calidad", schema = "dbo")
public class CalidadModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "SerialId")
    private int serialId;

    @Column(name = "Serial")
    private String serial;

    @Column(name = "Mac")
    private String mac;

    @Column(name = "CodigoSapId")
    private int codigoSapId;

    @Column(name = "PalletId")
    private int palletId;

    @Column(name = "CajaEmpaqueId")
    private int cajaEmpaqueId;

    @Column(name = "FallaCosmeticaId")
    private Integer fallaCosmeticaId;

    @Column(name = "FallaFuncionalId")
    private Integer fallaFuncionalId;

    @Column(name = "EstadoId")
    private Integer estadoId;

    @Column(name = "UsuarioId")
    private int usuarioId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "Fecha")
    private LocalDateTime fecha;
}
