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

    @Query(value = "SELECT f.id, f.descripcion FROM general.pa_getavailablegruposforprofile(:perfilId, :cliente) f", nativeQuery = true)
    List<Object[]> getAvailableGrupos(@Param("perfilId") int perfilId, @Param("cliente") String cliente);

    @Query(value = "SELECT f.id, f.descripcion FROM general.pa_getassignedgruposforprofile(:perfilId, :cliente) f", nativeQuery = true)
    List<Object[]> getAssignedGrupos(@Param("perfilId") int perfilId, @Param("cliente") String cliente);

    @Query(value = "SELECT f.id, f.descripcion FROM general.pa_getavailableitemsforprofile(:perfilId, :idPadre, :cliente) f", nativeQuery = true)
    List<Object[]> getAvailableItems(@Param("perfilId") int perfilId, @Param("idPadre") String idPadre, @Param("cliente") String cliente);

    @Query(value = "SELECT f.id, f.descripcion FROM general.pa_getassigneditemsforprofile(:perfilId, :idPadre, :cliente) f", nativeQuery = true)
    List<Object[]> getAssignedItems(@Param("perfilId") int perfilId, @Param("idPadre") String idPadre, @Param("cliente") String cliente);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_insertmenuperfil(:idMenu, :idPerfil, :cliente)", nativeQuery = true)
    void insertMenuPerfil(@Param("idMenu") String idMenu, @Param("idPerfil") int idPerfil, @Param("cliente") String cliente);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_deletemenuperfil(:idMenu, :idPerfil, :cliente)", nativeQuery = true)
    void deleteMenuPerfil(@Param("idMenu") String idMenu, @Param("idPerfil") int idPerfil, @Param("cliente") String cliente);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_deletemenuperfilbyperfil(:perfilId, :cliente)", nativeQuery = true)
    void deleteByPerfilId(@Param("perfilId") int perfilId, @Param("cliente") String cliente);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_syncmenuperfil(:perfilId, :menuIds, :cliente)", nativeQuery = true)
    void syncMenuPerfil(@Param("perfilId") int perfilId, @Param("menuIds") String menuIds, @Param("cliente") String cliente);
}
