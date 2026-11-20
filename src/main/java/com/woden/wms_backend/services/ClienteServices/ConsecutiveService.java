package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConsecutiveService {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public String getConsecutive(String cliente) {
    String sql = "SELECT \"consecutivoSerial\" FROM pa_getconsecutiveunreadable(CAST(? AS character varying))";
    return jdbcTemplate.queryForObject(sql, String.class, cliente);
  }
}
