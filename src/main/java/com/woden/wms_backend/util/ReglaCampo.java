package com.woden.wms_backend.util;

import java.math.BigInteger;
import java.util.List;

import com.woden.wms_backend.exception.BusinessRuleException;

/**
 * Valor calculado a partir de otro campo: Códigos SAP > Campos calculados (MAC, Serial3-5 al ingresar y Variable1-4 en
 * etiquetado) y Administración > Variables de etiquetas.
 *   COPIAR      el valor tal cual
 *   PRIMEROS    los primeros N caracteres (Parámetro 1 = N)
 *   ULTIMOS     los últimos N caracteres (Parámetro 1 = N)
 *   SUBCADENA   desde la posición P1 (1 = primera) y P2 caracteres
 *   HEX_SUMA    el valor como hexadecimal + P1, con P2 dígitos (12 por defecto): la MAC + 1 de Tigo Nicaragua o el
 *               serial + 1 de Tigo Costa Rica que estaban en pa_InsertEntry
 *   TEXTO       el texto fijo de P1
 */
public final class ReglaCampo {

  public static final List<String> OPERACIONES = List.of("COPIAR", "PRIMEROS", "ULTIMOS", "SUBCADENA", "HEX_SUMA",
      "TEXTO");

  private ReglaCampo() {
  }

  public static String aplicar(String operacion, String origen, String p1, String p2) {
    String v = origen == null ? "" : origen.trim();
    int largo = v.length();
    switch (operacion == null ? "" : operacion.toUpperCase()) {
      case "PRIMEROS": {
        int n = entero(p1, largo);
        return v.substring(0, Math.max(0, Math.min(n, largo)));
      }
      case "ULTIMOS": {
        int n = entero(p1, largo);
        return v.substring(Math.max(0, largo - Math.max(0, n)));
      }
      case "SUBCADENA": {
        int desde = Math.max(1, entero(p1, 1)) - 1;
        int cuantos = Math.max(0, entero(p2, largo));
        return desde >= largo ? "" : v.substring(desde, Math.min(largo, desde + cuantos));
      }
      case "HEX_SUMA":
        return hexSuma(v, entero(p1, 1), Math.max(1, entero(p2, 12)));
      case "TEXTO":
        return p1 == null ? "" : p1;
      case "COPIAR":
      default:
        return v;
    }
  }

  private static String hexSuma(String valor, int suma, int digitos) {
    String hex = valor.replaceAll("[:\\-.\\s]", "");
    if (hex.isEmpty()) {
      return "";
    }
    if (!hex.matches("[0-9A-Fa-f]+")) {
      throw new BusinessRuleException("El valor " + valor + " no es hexadecimal: no se puede calcular el campo.");
    }
    String r = new BigInteger(hex, 16).add(BigInteger.valueOf(suma)).toString(16).toUpperCase();
    if (r.startsWith("-")) {
      return "";
    }
    if (r.length() < digitos) {
      r = "0".repeat(digitos - r.length()) + r;
    }
    return r.substring(r.length() - digitos);
  }

  private static int entero(String v, int porDefecto) {
    try {
      return v == null || v.isBlank() ? porDefecto : Integer.parseInt(v.trim());
    } catch (NumberFormatException e) {
      return porDefecto;
    }
  }
}
