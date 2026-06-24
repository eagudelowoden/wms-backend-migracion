package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.MenuPerfilModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface MenuPerfilRepository extends BaseRepository<MenuPerfilModel, Integer> {

    @Query(value = "EXEC [WmsWdGeneral].dbo.pa_GetAvailableGruposForProfile @perfilId = :perfilId, @cliente = :cliente", nativeQuery = true)
    List<Object[]> getAvailableGrupos(@Param("perfilId") int perfilId, @Param("cliente") String cliente);

    @Query(value = "EXEC [WmsWdGeneral].dbo.pa_GetAssignedGruposForProfile @perfilId = :perfilId, @cliente = :cliente", nativeQuery = true)
    List<Object[]> getAssignedGrupos(@Param("perfilId") int perfilId, @Param("cliente") String cliente);

    @Query(value = "EXEC [WmsWdGeneral].dbo.pa_GetAvailableItemsForProfile @perfilId = :perfilId, @idPadre = :idPadre, @cliente = :cliente", nativeQuery = true)
    List<Object[]> getAvailableItems(@Param("perfilId") int perfilId, @Param("idPadre") String idPadre, @Param("cliente") String cliente);

    @Query(value = "EXEC [WmsWdGeneral].dbo.pa_GetAssignedItemsForProfile @perfilId = :perfilId, @idPadre = :idPadre, @cliente = :cliente", nativeQuery = true)
    List<Object[]> getAssignedItems(@Param("perfilId") int perfilId, @Param("idPadre") String idPadre, @Param("cliente") String cliente);

    @Modifying
    @Transactional
    @Query(value = "EXEC [WmsWdGeneral].dbo.pa_InsertMenuPerfil @idMenu = :idMenu, @idPerfil = :idPerfil, @cliente = :cliente", nativeQuery = true)
    void insertMenuPerfil(@Param("idMenu") String idMenu, @Param("idPerfil") int idPerfil, @Param("cliente") String cliente);

    @Modifying
    @Transactional
    @Query(value = "EXEC [WmsWdGeneral].dbo.pa_DeleteMenuPerfil @idMenu = :idMenu, @idPerfil = :idPerfil, @cliente = :cliente", nativeQuery = true)
    void deleteMenuPerfil(@Param("idMenu") String idMenu, @Param("idPerfil") int idPerfil, @Param("cliente") String cliente);

    @Modifying
    @Transactional
    @Query(value = "EXEC [WmsWdGeneral].dbo.pa_DeleteMenuPerfilByPerfil @perfilId = :perfilId, @cliente = :cliente", nativeQuery = true)
    void deleteByPerfilId(@Param("perfilId") int perfilId, @Param("cliente") String cliente);
}
