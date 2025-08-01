package com.woden.wms_backend.util;

public class TypeMapper {
  public static Integer toInt(Object value) {
    if (value instanceof Number)
      return ((Number) value).intValue();
    return null;
  }

  public static Boolean toBoolean(Object value) {
    if (value instanceof Boolean)
      return (Boolean) value;
    if (value instanceof Number)
      return ((Number) value).intValue() != 0;
    return null;
  }

  public static Byte toByte(Object value) {
    if (value instanceof Number)
      return ((Number) value).byteValue();
    return null;
  }

  public static String toString(Object value) {
    return value != null ? value.toString() : null;
  }
}
