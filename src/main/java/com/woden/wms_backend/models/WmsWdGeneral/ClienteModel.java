package com.woden.wms_backend.models.WmsWdGeneral;

import com.woden.wms_backend.dto.ClienteDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Cliente", schema = "dbo")
@NamedStoredProcedureQuery(name = "pa_GetListClient", procedureName = "pa_GetListClient", resultClasses = ClienteDTO.class, parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class, name = "usuarioId")
})
// @NamedStoredProcedureQuery(name = "pa_GetIdClient", procedureName = "pa_GetListClient", resultClasses = ClienteDTO.class, parameters = {
//     @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class, name = "usuarioId")
// })
public class ClienteModel {
    @Id
    @Column(name = "Id")
    private int id;
    @Column(name = "Nombre")
    private String nombre;
    @Column(name = "dbase")
    private String dbase;
    @Column(name = "Activo")
    private Integer activo;
}
