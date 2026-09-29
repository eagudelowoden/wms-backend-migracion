package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table (name = "ServicioWebConfiguracion", schema = "dbo")
public class ServicioWebConfiguracionModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int servicioWebId;
    private int tipoRegistroId;
    private String tagPadre;
    private String tagHijo;
    private String nombreCampo;
    private String campo;
    private String descripcion;
    private int categoriaId;
    private int dependenciaId;
    private int tipoDatoId;
    private int longitudMax;
    private int valorXDefecto;
    private String valor;
}
