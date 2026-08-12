package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;
import lombok.Data;

@Data
public class MassUploadPreviewRequestDTO {
    private List<MassUploadRowDTO> rows;
    private PalletConfigDTO palletConfig;
}
