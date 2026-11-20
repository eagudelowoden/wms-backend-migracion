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

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getiduserclient(:usuarioId, :cliente) f", nativeQuery = true)
    Integer getIdUserClient(
            @Param("usuarioId") int usuarioId,
            @Param("cliente") String cliente
    );

    @Query(value = "SELECT CAST(f.id AS integer), f.nombre, f.dbase FROM general.pa_searchavailableuserclient(:usuarioId) f", nativeQuery = true)
    List<Object[]> searchAvailableUserClient(@Param("usuarioId") int usuarioId);

    @Query(value = "SELECT CAST(f.id AS integer), f.nombre, f.dbase FROM general.pa_searchaggregatesuserclient(:usuarioId) f", nativeQuery = true)
    List<Object[]> searchAggregatesUserClient(@Param("usuarioId") int usuarioId);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_insertuserclient(:usuarioId, :clienteId)", nativeQuery = true)
    void insertUserClient(@Param("usuarioId") int usuarioId, @Param("clienteId") int clienteId);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_deleteuserclient(:usuarioId, :clienteId)", nativeQuery = true)
    void deleteUserClient(@Param("usuarioId") int usuarioId, @Param("clienteId") int clienteId);
}