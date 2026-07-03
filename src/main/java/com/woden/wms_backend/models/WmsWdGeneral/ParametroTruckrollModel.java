package com.woden.wms_backend.models.WmsWdGeneral;

import lombok.Data;

@Data
public class ParametroTruckrollModel {

    private Integer id;
    private String descripcion;
    private String baseOrigen;
    private String tablaOrigen;
    private String campoCruceOrigen;
    private String campoFechaOrigen;
    private String campoIdOrigen;
    private String campoClienteDestinoOrigen;
    private String campoTruckRollOrigen;
    private String baseDestino;
    private String tablaDestino;
    private String campoCruceDestino;
    private String campoClienteOrigenDestino;
    private String campoTruckRollDestino;
    private String campoParametroId;
    private String campoCodigoSapOrigen;
    private Integer codigoSapIdOrigen;
    private Integer diasGarantia;
    private Integer diasTruckRoll;
    private Integer ventanaDias;

}
