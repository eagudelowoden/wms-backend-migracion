package com.woden.wms_backend.models.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ManoDeObra", schema = "dbo")
public class ManoObraModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "detalle", length = 50)
    private String detalle;

    @Column(name = "observaciones", length = 50)
    private String observaciones;

    @Column(name = "mano_obra_json", columnDefinition = "nvarchar(max)")
    private String manoObraJson;

    @Column(name = "activo", columnDefinition = "bit")
    private Boolean activo;

    @Column(name = "usuario", length = 150)
    private String usuario;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
