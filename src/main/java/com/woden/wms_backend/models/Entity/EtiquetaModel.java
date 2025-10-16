package com.woden.wms_backend.models.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Etiqueta", schema = "dbo")
public class EtiquetaModel {
    @Id
    @Column(name = "Id")
    private Integer id;
    private String nombre;
    private String tipo;
    private Integer impresion;
    private Integer codigoSapId;
    private String codigoSapCombo;
}
