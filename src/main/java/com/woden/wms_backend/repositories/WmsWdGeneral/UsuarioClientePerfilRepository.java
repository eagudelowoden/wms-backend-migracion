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

    @Query(value = "EXEC pa_GetIdUserClientProfile :usuarioClienteId", nativeQuery = true)
    Integer getIdUsuarioClientePerfil(@Param("usuarioClienteId") Integer usuarioClienteId);

    @Query(value = "EXEC pa_SearchAvailableUserClientProfile :cliente, :usuarioClienteId", nativeQuery = true)
    List<Object[]> searchAvailableUserClientProfile(@Param("cliente") String cliente,
                                                     @Param("usuarioClienteId") Integer usuarioClienteId);

    @Query(value = "EXEC pa_SearchAggregatesUserClientProfile :usuarioClienteId", nativeQuery = true)
    List<Object[]> searchAggregatesUserClientProfile(@Param("usuarioClienteId") Integer usuarioClienteId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InsertUserClientProfile :usuarioClienteId, :perfilId", nativeQuery = true)
    void insertUserClientProfile(@Param("usuarioClienteId") Integer usuarioClienteId,
                                  @Param("perfilId") Integer perfilId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_DeleteUserClientProfile :usuarioClienteId, :perfilId", nativeQuery = true)
    void deleteUserClientProfile(@Param("usuarioClienteId") Integer usuarioClienteId,
                                  @Param("perfilId") Integer perfilId);
}