package com.woden.wms_backend.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlantillaTexto {

  private static final Pattern VARIABLE = Pattern.compile("\\{([a-z_]+)\\}");
  private static final Pattern COLOR = Pattern.compile("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");
  private static final Pattern OPCIONAL = Pattern.compile("\\[([^\\[\\]]*)\\]");

  private PlantillaTexto() {
  }

  public static String texto(String plantilla, Map<String, String> vars) {
    if (plantilla == null || plantilla.isEmpty()) {
      return "";
    }
    Matcher m = VARIABLE.matcher(plantilla);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      String v = vars.containsKey(m.group(1)) ? nulo(vars.get(m.group(1))) : m.group();
      m.appendReplacement(sb, Matcher.quoteReplacement(v));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static String html(String plantilla, Map<String, String> vars) {
    if (plantilla == null || plantilla.isEmpty()) {
      return "";
    }
    Matcher m = VARIABLE.matcher(plantilla);
    StringBuilder sb = new StringBuilder();
    int desde = 0;
    while (m.find()) {
      sb.append(escapar(plantilla.substring(desde, m.start())));
      String clave = m.group(1);
      if ("pagina".equals(clave)) {
        sb.append("<span class=\"num-pagina\"></span>");
      } else if ("paginas".equals(clave)) {
        sb.append("<span class=\"num-paginas\"></span>");
      } else if (vars.containsKey(clave)) {
        sb.append(escapar(nulo(vars.get(clave))));
      } else {
        sb.append(escapar(m.group()));
      }
      desde = m.end();
    }
    sb.append(escapar(plantilla.substring(desde)));
    return sb.toString();
  }

  // texto de correo: además **negrita** y [partes opcionales] que desaparecen si sus variables salen vacías
  public static String htmlRico(String plantilla, Map<String, String> vars) {
    return html(opcionales(plantilla, vars), vars).replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
  }

  public static String opcionales(String plantilla, Map<String, String> vars) {
    if (plantilla == null || plantilla.isEmpty()) {
      return "";
    }
    Matcher m = OPCIONAL.matcher(plantilla);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      String dentro = m.group(1);
      m.appendReplacement(sb, Matcher.quoteReplacement(vacia(dentro, vars) ? "" : dentro));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static boolean vacia(String plantilla, Map<String, String> vars) {
    if (plantilla == null || plantilla.isBlank()) {
      return true;
    }
    Matcher m = VARIABLE.matcher(plantilla);
    boolean tieneVariables = false;
    while (m.find()) {
      String clave = m.group(1);
      if ("pagina".equals(clave) || "paginas".equals(clave)) {
        return false;
      }
      tieneVariables = true;
      if (!nulo(vars.get(clave)).isBlank()) {
        return false;
      }
    }
    return tieneVariables;
  }

  public static String escapar(String s) {
    if (s == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder(s.length() + 16);
    for (char c : s.toCharArray()) {
      switch (c) {
        case '&' -> sb.append("&amp;");
        case '<' -> sb.append("&lt;");
        case '>' -> sb.append("&gt;");
        case '"' -> sb.append("&quot;");
        case '\'' -> sb.append("&#39;");
        default -> sb.append(c);
      }
    }
    return sb.toString();
  }

  public static String color(Object valor, String porDefecto) {
    String v = valor == null ? "" : String.valueOf(valor).trim();
    return COLOR.matcher(v).matches() ? v : porDefecto;
  }

  public static double numero(Object valor, double porDefecto, double min, double max) {
    double v;
    if (valor instanceof Number n) {
      v = n.doubleValue();
    } else {
      try {
        v = Double.parseDouble(String.valueOf(valor).trim().replace(',', '.'));
      } catch (Exception e) {
        return porDefecto;
      }
    }
    if (Double.isNaN(v)) {
      return porDefecto;
    }
    return Math.max(min, Math.min(max, v));
  }

  public static String nombrePropio(String s) {
    String v = nulo(s).toLowerCase();
    StringBuilder sb = new StringBuilder(v.length());
    boolean inicio = true;
    for (char c : v.toCharArray()) {
      sb.append(inicio ? Character.toUpperCase(c) : c);
      inicio = Character.isWhitespace(c) || c == '-' || c == '(';
    }
    return sb.toString();
  }

  private static String nulo(String s) {
    return s == null ? "" : s;
  }
}
