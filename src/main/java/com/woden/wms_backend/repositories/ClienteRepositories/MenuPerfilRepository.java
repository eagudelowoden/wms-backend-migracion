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

    @Query(value = "SELECT m.id, m.descripcion FROM dbo.Menu m WHERE m.id_padre = '0' AND m.id NOT IN (SELECT mp.IdMenu FROM dbo.menu_perfil mp WHERE mp.IdPerfil = :perfilId) ORDER BY m.orden", nativeQuery = true)
    List<Object[]> getAvailableGrupos(@Param("perfilId") int perfilId);

    @Query(value = "SELECT m.id, m.descripcion FROM dbo.Menu m INNER JOIN dbo.menu_perfil mp ON m.Id = mp.IdMenu AND mp.IdPerfil = :perfilId WHERE m.id_padre = '0' ORDER BY m.orden", nativeQuery = true)
    List<Object[]> getAssignedGrupos(@Param("perfilId") int perfilId);

    @Query(value = "SELECT m.id, m.descripcion FROM dbo.Menu m WHERE m.id_padre = :idPadre AND m.id NOT IN (SELECT mp.IdMenu FROM dbo.menu_perfil mp WHERE mp.IdPerfil = :perfilId) ORDER BY m.orden", nativeQuery = true)
    List<Object[]> getAvailableItems(@Param("perfilId") int perfilId, @Param("idPadre") String idPadre);

    @Query(value = "SELECT m.id, m.descripcion FROM dbo.Menu m INNER JOIN dbo.menu_perfil mp ON m.Id = mp.IdMenu AND mp.IdPerfil = :perfilId WHERE m.id_padre = :idPadre ORDER BY m.orden", nativeQuery = true)
    List<Object[]> getAssignedItems(@Param("perfilId") int perfilId, @Param("idPadre") String idPadre);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO dbo.menu_perfil (IdMenu, IdPerfil) VALUES (:idMenu, :idPerfil)", nativeQuery = true)
    void insertMenuPerfil(@Param("idMenu") String idMenu, @Param("idPerfil") int idPerfil);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM dbo.menu_perfil WHERE IdMenu = :idMenu AND IdPerfil = :idPerfil", nativeQuery = true)
    void deleteMenuPerfil(@Param("idMenu") String idMenu, @Param("idPerfil") int idPerfil);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM dbo.menu_perfil WHERE IdPerfil = :perfilId", nativeQuery = true)
    void deleteByPerfilId(@Param("perfilId") int perfilId);
}
