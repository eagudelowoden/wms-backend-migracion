package com.woden.wms_backend.models.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Forecast", schema = "dbo")
public class ForecastModel {
    @Id
    @Column(name = "id")
    private Integer id;
    private String forecast;
    private LocalDate fecha;
    private String diasHabiles;
    private Integer familiaId;
    private String forecastIngreso;
    private String detalle;
    private String observaciones;
}
