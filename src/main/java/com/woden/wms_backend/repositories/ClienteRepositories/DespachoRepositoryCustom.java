package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.Entity.AccesorioModel;

import java.util.List;

public interface DespachoRepositoryCustom {
    Integer insertDispatchAccesoryBatch(
            List<AccesorioModel> accesorios,
            Integer estadoId,
            Integer usuarioId,
            String pedidoSap
    );
}