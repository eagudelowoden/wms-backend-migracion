package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteValidacionModel;

@Repository
public interface ClienteValidacionRepository extends JpaRepository<ClienteValidacionModel, Integer> {

    @Query(value = "EXEC dbo.pa_GetValidacionesCliente :clienteId", nativeQuery = true)
    List<Object[]> getValidacionesByClienteId(@Param("clienteId") Integer clienteId);

    @Query(value = "EXEC dbo.pa_GetValidacionTiposDisponibles :clienteId", nativeQuery = true)
    List<Object[]> getDisponibles(@Param("clienteId") Integer clienteId);

    @Query(value = "EXEC dbo.pa_GetValidacionesAsignadas :clienteId", nativeQuery = true)
    List<Object[]> getAsignadas(@Param("clienteId") Integer clienteId);

    @Modifying
    @Transactional
    @Query(value = "EXEC dbo.pa_InsertClienteValidacion :clienteId, :validacionTipoId", nativeQuery = true)
    void insertValidacion(@Param("clienteId") Integer clienteId, @Param("validacionTipoId") Integer validacionTipoId);

    @Modifying
    @Transactional
    @Query(value = "EXEC dbo.pa_DeleteClienteValidacion :clienteId, :validacionTipoId", nativeQuery = true)
    void deleteValidacion(@Param("clienteId") Integer clienteId, @Param("validacionTipoId") Integer validacionTipoId);

    @Modifying
    @Transactional
    @Query(value = "EXEC dbo.pa_ToggleClienteValidacion :clienteId, :validacionTipoId, :activo", nativeQuery = true)
    void toggleValidacion(@Param("clienteId") Integer clienteId, @Param("validacionTipoId") Integer validacionTipoId, @Param("activo") Boolean activo);
}
