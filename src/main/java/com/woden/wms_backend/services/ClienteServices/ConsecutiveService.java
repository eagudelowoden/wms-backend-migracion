package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConsecutiveService {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public String getConsecutive(String cliente) {
    String sql = "EXEC pa_GetConsecutiveUnreadable ?";
    return jdbcTemplate.queryForObject(sql, String.class, cliente);
  }
}
