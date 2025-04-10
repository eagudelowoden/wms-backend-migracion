package com.woden.wms_backend.config.DataSource;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class ClientDatabaseContext {
  // Atributos para almacenar en RequestAttributes
  private static final String CLIENT_NAME_ATTRIBUTE = "CLIENT_NAME";
  private static final String DB_NAME_ATTRIBUTE = "DB_NAME";
  private static final String CLIENT_ID_ATTRIBUTE = "CLIENT_ID";
  private static final String USE_GENERAL_DB_ATTRIBUTE = "USE_GENERAL_DB";

  // ThreadLocal para almacenamiento temporal
  private static final ThreadLocal<String> clientNameHolder = new ThreadLocal<>();
  private static final ThreadLocal<String> dbNameHolder = new ThreadLocal<>();
  private static final ThreadLocal<Integer> clientIdHolder = new ThreadLocal<>();
  private static final ThreadLocal<Boolean> useGeneralDbHolder = new ThreadLocal<>();

  public static void setCurrentClient(String clientName, String dbName, Integer clientId) {
    // Almacenar en RequestAttributes
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      requestAttributes.setAttribute(CLIENT_NAME_ATTRIBUTE, clientName, RequestAttributes.SCOPE_REQUEST);
      requestAttributes.setAttribute(DB_NAME_ATTRIBUTE, dbName, RequestAttributes.SCOPE_REQUEST);
      requestAttributes.setAttribute(CLIENT_ID_ATTRIBUTE, clientId, RequestAttributes.SCOPE_REQUEST);
      requestAttributes.setAttribute(USE_GENERAL_DB_ATTRIBUTE, false, RequestAttributes.SCOPE_REQUEST);
    }

    // Almacenar en ThreadLocal
    clientNameHolder.set(clientName);
    dbNameHolder.set(dbName);
    clientIdHolder.set(clientId);
    useGeneralDbHolder.set(false);
  }

  public static String getCurrentClientName() {
    // Primero intentar obtener de ThreadLocal
    String clientName = clientNameHolder.get();
    if (clientName != null)
      return clientName;

    // Si no está en ThreadLocal, buscar en RequestAttributes
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      return (String) requestAttributes.getAttribute(CLIENT_NAME_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }
    return null;
  }

  public static String getCurrentClientDb() {
    // Si se está forzando el uso de la BD general
    if (isUsingGeneralDb() && !isExcludedEndpoint()) {
      return "WmsWdGeneral";
    }

    // Primero intentar obtener de ThreadLocal
    String dbName = dbNameHolder.get();
    if (dbName != null)
      return dbName;

    // Si no está en ThreadLocal, buscar en RequestAttributes
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      Object dbNameAttr = requestAttributes.getAttribute(DB_NAME_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
      if (dbNameAttr != null && !dbNameAttr.toString().isEmpty()) {
        return dbNameAttr.toString();
      }
    }
    return "WmsWdGeneral"; // BD por defecto
  }

  public static Integer getCurrentClientId() {
    // Primero intentar obtener de ThreadLocal
    Integer clientId = clientIdHolder.get();
    if (clientId != null)
      return clientId;

    // Si no está en ThreadLocal, buscar en RequestAttributes
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      return (Integer) requestAttributes.getAttribute(CLIENT_ID_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }
    return null;
  }

  // Métodos para forzar el uso de la BD general
  public static void useGeneralDatabase() {
    useGeneralDbHolder.set(true);
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      requestAttributes.setAttribute(USE_GENERAL_DB_ATTRIBUTE, true, RequestAttributes.SCOPE_REQUEST);
    }
  }

  public static void resetGeneralDatabaseOverride() {
    useGeneralDbHolder.set(false);
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      requestAttributes.setAttribute(USE_GENERAL_DB_ATTRIBUTE, false, RequestAttributes.SCOPE_REQUEST);
    }
  }

  public static boolean isUsingGeneralDb() {
    Boolean useGeneral = useGeneralDbHolder.get();
    if (useGeneral != null) {
      return useGeneral;
    }

    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      Object attr = requestAttributes.getAttribute(USE_GENERAL_DB_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
      return Boolean.TRUE.equals(attr);
    }
    return false;
  }

  public static void clear() {
    // Limpiar ThreadLocal
    clientNameHolder.remove();
    dbNameHolder.remove();
    clientIdHolder.remove();
    useGeneralDbHolder.remove();

    // Limpiar RequestAttributes si está disponible
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      requestAttributes.removeAttribute(CLIENT_NAME_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
      requestAttributes.removeAttribute(DB_NAME_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
      requestAttributes.removeAttribute(CLIENT_ID_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
      requestAttributes.removeAttribute(USE_GENERAL_DB_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }
  }

  // Método para verificar si estamos en un endpoint excluido
  // Puedes implementar esto según tus necesidades
  private static boolean isExcludedEndpoint() {
    // Obtener el request actual
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
      String uri = request.getRequestURI();
      // Excluir endpoints específicos
      return uri.contains("/current-connections") || uri.contains("/switch-client");
    }
    return false;
  }
}
