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
public class ClienteModel {
    @Id
    @Column(name = "Id")
    private Integer id;
    @Column(name = "Nombre")
    private String nombre;
    @Column(name = "dbase")
    private String dbase;
    @Column(name = "Activo")
    private Integer activo;
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
    @Column(name = "RecogidaON")
    private Integer recogidaON;
    @Column(name = "BaseIngresoON")
    private Integer baseIngresoON;
    @Column(name = "BaseNoDisponibleON")
    private Integer baseNoDisponibleON;
    @Column(name = "LoteEmpaqueON")
    private Integer loteEmpaqueON;
    @Column(name = "LargoGuia")
    private String largoGuia;
    @Column(name = "KitIngresoON")
    private Integer kitIngresoON;
    @Column(name = "componenteON")
    private Integer componenteON;
    @Column(name = "TipoOrigenUsuarioON")
    private Integer tipoOrigenUsuarioON;
    @Column(name = "nivelClasificacionON")
    private Integer nivelClasificacionON;
    @Column(name = "CalidadON")
    private Integer calidadON;
    @Column(name = "smartCardInfoON")
    private Integer smartCardInfoON;
    @Column(name = "BloqueoReimpresionON")
    private Integer bloqueoReimpresionON;
    @Column(name = "etiquetaUnitariaON")
    private Integer etiquetaUnitariaON;
    @Column(name = "adicionPrealertaON")
    private Integer adicionPrealertaON;
    @Column(name = "prealerta")
    private String prealerta;
    @Column(name = "odooPqrsON")
    private Boolean odooPqrsON;
    @Column(name = "serialMasterCalidadON")
    private Integer serialMasterCalidadON;
    @Column(name = "Archivos")
    private String archivos;
    @Column(name = "LblEtiquetado")
    private String lblEtiquetado;
}
