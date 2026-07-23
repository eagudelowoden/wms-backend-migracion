package com.woden.wms_backend.models.Entity;


import com.woden.wms_backend.models.Activable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Etiqueta", schema = "dbo")
public class EtiquetaModel implements Activable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    private String nombre;
    private String tipo;
    private Integer impresion;
    private Integer codigoSapId;
    @Transient
    private String codigoSapCombo;
    private Boolean activo;
}
