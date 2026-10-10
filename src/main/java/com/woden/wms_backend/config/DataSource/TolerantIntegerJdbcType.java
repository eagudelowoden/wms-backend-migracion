package com.woden.wms_backend.config.DataSource;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;

/**
 * En SQL Server los flags eran bit/tinyint y las entidades los mapean como Integer. En PostgreSQL
 * varios de esos flags son boolean (el driver los devuelve como 't'/'f' y getInt falla con
 * "Bad value for type int : t"). Esta lectura acepta boolean e integer en la misma columna:
 * true -> 1, false -> 0. Solo cambia la lectura; la escritura sigue siendo la de IntegerJdbcType.
 */
public class TolerantIntegerJdbcType extends IntegerJdbcType {

  private static final long serialVersionUID = 1L;

  @Override
  public <X> ValueExtractor<X> getExtractor(JavaType<X> javaType) {
    return new BasicExtractor<X>(javaType, this) {
      @Override
      protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
        return toJava(rs.getObject(paramIndex), javaType, options);
      }

      @Override
      protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
        return toJava(statement.getObject(index), javaType, options);
      }

      @Override
      protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
        return toJava(statement.getObject(name), javaType, options);
      }
    };
  }

  private static <X> X toJava(Object value, JavaType<X> javaType, WrapperOptions options) {
    if (value == null) {
      return null;
    }
    if (value instanceof Boolean b) {
      return javaType.wrap(b ? 1 : 0, options);
    }
    if (value instanceof Number n) {
      return javaType.wrap(n.intValue(), options);
    }
    return javaType.wrap(value, options);
  }
}
