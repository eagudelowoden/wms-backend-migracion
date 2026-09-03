package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;
import lombok.Data;

@Data
public class MassUploadConfirmRequestDTO {
    private List<MassUploadRowResolvedDTO> seriales;
}
