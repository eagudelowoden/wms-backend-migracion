package com.woden.wms_backend.models.Entity;

import lombok.Data;

@Data
public class ConfigurationModel {
    public static String baseDatos;
    public static String ip;
    public static String usuario;
    public static String password; 
    public static String imagenesEtiquetado;
    public static String imagenesEmpaque;
    public static String prnEtiquetado;
    public static String prnEmpaque;
    public static String lblEtiquetado;
    public static String lblEmpaque;
    public static String archivos;
    public static String pallets;
    public static String codigoSap;
    public static String prealertas;
}
