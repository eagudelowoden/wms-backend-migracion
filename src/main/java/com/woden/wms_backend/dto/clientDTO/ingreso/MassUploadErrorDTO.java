package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;
import lombok.Data;

@Data
public class MassUploadErrorDTO {
    private int fila;
    private String serial;
    private List<String> errores;
}
