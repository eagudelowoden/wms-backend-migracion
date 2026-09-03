package com.woden.wms_backend.dto.clientDTO.loads;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class BulkUploadRequest {
  private String base;
  private List<Map<String, Object>> records;
}
