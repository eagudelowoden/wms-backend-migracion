package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface ClienteRepository extends BaseRepository<ClienteModel, Integer> {
    // Llamar al procedimiento almacenado pa_GetListClient
    @Query(value = "EXEC pa_GetListClient :Id", nativeQuery = true)
    List<String> getListClient(@Param("Id") int usuarioId);

    // Llamar al procedimiento almacenado pa_GetIdClient
    @Query(value = "EXEC pa_GetIdClient :nombre", nativeQuery = true)
    Integer getIdClient(@Param("nombre") String nombre);
}
