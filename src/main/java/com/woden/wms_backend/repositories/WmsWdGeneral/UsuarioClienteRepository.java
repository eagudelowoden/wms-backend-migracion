package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.WmsWdGeneral.UsuarioClienteModel;

@Repository
public interface UsuarioClienteRepository extends JpaRepository<UsuarioClienteModel, Integer> {

    @Query(value = "EXEC pa_GetIdUserClient :usuarioId, :cliente", nativeQuery = true)
    Integer getIdUserClient(
            @Param("usuarioId") int usuarioId,
            @Param("cliente") String cliente
    );

    @Query(value = "EXEC pa_SearchAvailableUserClient :usuarioId", nativeQuery = true)
    List<Object[]> searchAvailableUserClient(@Param("usuarioId") int usuarioId);

    @Query(value = "EXEC pa_SearchAggregatesUserClient :usuarioId", nativeQuery = true)
    List<Object[]> searchAggregatesUserClient(@Param("usuarioId") int usuarioId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InsertUserClient :usuarioId, :clienteId", nativeQuery = true)
    void insertUserClient(@Param("usuarioId") int usuarioId, @Param("clienteId") int clienteId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_DeleteUserClient :usuarioId, :clienteId", nativeQuery = true)
    void deleteUserClient(@Param("usuarioId") int usuarioId, @Param("clienteId") int clienteId);
}