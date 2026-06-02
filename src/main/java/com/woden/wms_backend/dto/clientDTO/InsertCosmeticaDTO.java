package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;
import java.util.List;

@Data
public class InsertCosmeticaDTO {
    private List<String> seriales;
    private Integer usuarioId;
}
