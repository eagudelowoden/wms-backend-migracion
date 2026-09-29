package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ListaFijaValor", schema = "dbo")
public class ListaFijaValorModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int listaFijaId;
    private String valor;
    private int activo;
}
