package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;
import lombok.Data;

@Data
public class MassUploadPreviewResponseDTO {
    private List<MassUploadRowResolvedDTO> validos;
    private List<MassUploadErrorDTO> errores;
}
