package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
@Primary
public interface ClienteRepository extends BaseRepository<ClienteModel, Integer> {
    // Llamar al procedimiento almacenado pa_GetListClient
    @Query(value = "EXEC pa_GetListClient :Id", nativeQuery = true)
    List<String> getListClient(@Param("Id") int usuarioId);

    // Llamar al procedimiento almacenado pa_GetIdClient
    @Query(value = "EXEC pa_GetIdClient :nombre", nativeQuery = true)
    Integer getIdClient(@Param("nombre") String nombre);

    @Query(value = "SELECT kitIngresoON FROM Cliente WHERE id = :id", nativeQuery = true)
    Boolean getKitIngresoON(@Param("id") int id);

    @Query(value = "SELECT id, nombre from Cliente where id in (92,151)", nativeQuery = true)
    List<Object[]> getClientes();
}
