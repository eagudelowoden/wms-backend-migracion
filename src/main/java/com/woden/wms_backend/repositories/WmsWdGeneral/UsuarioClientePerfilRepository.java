package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.WmsWdGeneral.UsuarioClientePerfilModel;

@Repository
public interface UsuarioClientePerfilRepository extends CrudRepository<UsuarioClientePerfilModel, Integer> {

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getiduserclientprofile(:usuarioClienteId) f", nativeQuery = true)
    Integer getIdUsuarioClientePerfil(@Param("usuarioClienteId") Integer usuarioClienteId);

    @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchavailableuserclientprofile(:cliente, :usuarioClienteId) f", nativeQuery = true)
    List<Object[]> searchAvailableUserClientProfile(@Param("cliente") String cliente,
                                                     @Param("usuarioClienteId") Integer usuarioClienteId);

    @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchaggregatesuserclientprofile(:usuarioClienteId) f", nativeQuery = true)
    List<Object[]> searchAggregatesUserClientProfile(@Param("usuarioClienteId") Integer usuarioClienteId);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_insertuserclientprofile(:usuarioClienteId, :perfilId)", nativeQuery = true)
    void insertUserClientProfile(@Param("usuarioClienteId") Integer usuarioClienteId,
                                  @Param("perfilId") Integer perfilId);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_deleteuserclientprofile(:usuarioClienteId, :perfilId)", nativeQuery = true)
    void deleteUserClientProfile(@Param("usuarioClienteId") Integer usuarioClienteId,
                                  @Param("perfilId") Integer perfilId);
}