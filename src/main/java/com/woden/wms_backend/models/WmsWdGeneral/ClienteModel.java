package com.woden.wms_backend.models.WmsWdGeneral;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.woden.wms_backend.util.BooleanAsIntSerializer;
import org.hibernate.annotations.JdbcTypeRegistration;

import com.woden.wms_backend.config.DataSource.TolerantIntegerJdbcType;
import com.woden.wms_backend.dto.ClienteDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;
import lombok.Data;

// Lectura de Integer tolerante a columnas boolean de PostgreSQL (aplica a toda la SessionFactory)
@JdbcTypeRegistration(TolerantIntegerJdbcType.class)
@Data
@Entity
@Table(name = "Cliente")
@NamedStoredProcedureQuery(name = "pa_GetListClient", procedureName = "pa_GetListClient", resultClasses = ClienteDTO.class, parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class, name = "usuarioId")
})
public class ClienteModel {
    @Id
    @Column(name = "Id")
    private Integer id;
    @Column(name = "Nombre")
    private String nombre;
    @Column(name = "dbase")
    private String dbase;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "Activo")
    private Boolean activo;
    @Column(name = "ColorCorporativo")
    private String ColorCorporativo;
    @Column(name = "conn")
    private String conn;
    @Column(name = "man_app")
    private Integer man_app;
    @Column(name = "ImagenesEtiquetado")
    private String ImagenesEtiquetado;
    @Column(name = "ImagenesEmpaque")
    private String ImagenesEmpaque;
    @Column(name = "PrnEtiquetado")
    private String PrnEtiquetado;
    @Column(name = "PrnEmpaque")
    private String PrnEmpaque;
    @Column(name = "LblEmpaque")
    private String LblEmpaque;
    @Column(name = "Pallet")
    private String Pallet;
    @Column(name = "CodigoSap")
    private String CodigoSap;
    @Column(name = "ip_server")
    private String ip_server;
    @Column(name = "db_user")
    private String db_user;
    @Column(name = "db_pass")
    private String db_pass;
    @Column(name = "bandera")
    private String bandera;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "RecogidaON")
    private Boolean recogidaON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "BaseIngresoON")
    private Boolean baseIngresoON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "BaseNoDisponibleON")
    private Boolean baseNoDisponibleON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "LoteEmpaqueON")
    private Boolean loteEmpaqueON;
    @Column(name = "LargoGuia")
    private String largoGuia;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "KitIngresoON")
    private Boolean kitIngresoON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "componenteON")
    private Boolean componenteON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "TipoOrigenUsuarioON")
    private Boolean tipoOrigenUsuarioON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "nivelClasificacionON")
    private Boolean nivelClasificacionON;
    @Column(name = "CalidadON")
    private Integer calidadON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "smartCardInfoON")
    private Boolean smartCardInfoON;
    @Column(name = "BloqueoReimpresionON")
    private Integer bloqueoReimpresionON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "etiquetaUnitariaON")
    private Boolean etiquetaUnitariaON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "adicionPrealertaON")
    private Boolean adicionPrealertaON;
    @Column(name = "prealerta")
    private String prealerta;
    @Column(name = "odooPqrsON")
    private Boolean odooPqrsON;
    @JsonSerialize(using = BooleanAsIntSerializer.class)
    @Column(name = "serialMasterCalidadON")
    private Boolean serialMasterCalidadON;
    @Column(name = "Archivos")
    private String archivos;
    @Column(name = "LblEtiquetado")
    private String lblEtiquetado;
    @Column(name = "Empresa_Nombre")
    private String empresaNombre;
    @Column(name = "Ruc")
    private String ruc;
    @Column(name = "Telefono")
    private String telefono;
    @Column(name = "Email")
    private String email;
    @Column(name = "Web")
    private String web;
}
