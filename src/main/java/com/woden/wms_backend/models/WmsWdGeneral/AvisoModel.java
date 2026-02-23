package com.woden.wms_backend.models.WmsWdGeneral;

public class AvisoModel {
  private String mensaje;
  private boolean activo;

  public AvisoModel() {
  }

  public AvisoModel(String mensaje, boolean activo) {
    this.mensaje = mensaje;
    this.activo = activo;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public boolean isActivo() {
    return activo;
  }

  public void setActivo(boolean activo) {
    this.activo = activo;
  }
}