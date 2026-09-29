package com.woden.wms_backend.models.projections;

public interface PalletRowProjection {
    Integer getId();
    String getNumero();
    Integer getCantidad();
    String getCodigoSap();
    String getDescripcion();
}