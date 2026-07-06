package com.woden.wms_backend.models.Entity;

import java.math.BigDecimal;
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
@Table(name = "Forecast", schema = "dbo")
public class ForecastModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "linea_negocio", length = 255)
    private String lineaNegocio;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @Column(name = "forecast_und", precision = 18, scale = 2)
    private BigDecimal forecastUnd;

    @Column(name = "jornada", length = 8)
    private String jornada;

    @Column(name = "dias_habiles")
    private Integer diasHabiles;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "activo", columnDefinition = "bit")
    private Boolean activo;

    @Column(name = "usuario", length = 150)
    private String usuario;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "familias_json", columnDefinition = "nvarchar(max)")
    private String familiasJson;
}
