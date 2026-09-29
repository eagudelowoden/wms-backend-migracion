package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class EntryProgressDTO {
  private Integer totalCount;
  private Integer totalProcessed;
  private Integer totalRemaining;

  public EntryProgressDTO() {
  }

  public EntryProgressDTO(Integer totalCount, Integer totalProcessed, Integer totalRemaining) {
    this.totalCount = totalCount;
    this.totalProcessed = totalProcessed;
    this.totalRemaining = totalRemaining;
  }
}
