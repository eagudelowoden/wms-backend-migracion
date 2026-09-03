package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;
import lombok.Data;

@Data
public class MassUploadConfirmResponseDTO {
    private int insertados;
    private List<MassUploadConfirmErrorDTO> errores;

    @Data
    public static class MassUploadConfirmErrorDTO {
        private String serial;
        private String error;
    }
}
