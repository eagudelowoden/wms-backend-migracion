package com.woden.wms_backend.models.Entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Presupuesto", schema = "dbo")
public class PresupuestoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "presupuesto", length = 255)
    private String presupuesto;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "segmentoId")
    private Integer segmentoId;

    @Column(name = "nivelId")
    private Integer nivelId;

    @Column(name = "unidades", length = 50)
    private String unidades;

    @Column(name = "reclamo", length = 50)
    private String reclamo;

    @Column(name = "diasHabiles", length = 50)
    private String diasHabiles;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "detalle", length = 50)
    private String detalle;

    @Column(name = "observaciones", length = 500)
    private String observaciones;
}
