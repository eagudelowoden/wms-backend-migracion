package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.MenuModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface MenuRepository extends BaseRepository<MenuModel, String> {
  @Query(value = "SELECT id,descripcion,tipo,orden,accion,estado,icono,id_padre,view_name,controller_name FROM Menu WHERE id_padre = :id_padre ORDER BY orden ASC", nativeQuery = true)
  public List<MenuModel> GetByIdPadre(@Param("id_padre") String id_padre);

  @Query(value = "SELECT m.id,m.descripcion,m.tipo,m.orden,m.accion,m.estado,m.icono,m.id_padre,m.view_name,m.controller_name FROM Menu m INNER JOIN menu_perfil mp ON m.Id = mp.IdMenu AND mp.IdPerfil = :idPerfil WHERE m.id_padre = :id ORDER BY m.orden ASC", nativeQuery = true)
  public List<MenuModel> GetByIdPadrePerfil(@Param("idPerfil") Integer idPerfil, @Param("id") String id);

  @Query(value = "SELECT id,descripcion,tipo,orden,accion,estado,icono,id_padre,view_name,controller_name FROM Menu", nativeQuery = true)
  public List<MenuModel> GetAllMenus();

  @Query(value = "SELECT moduloId FROM [WmsWdGeneral].dbo.PerfilModulo WHERE perfilId = :perfilId", nativeQuery = true)
  List<Integer> getAssignedModuleIds(@Param("perfilId") int perfilId);

  @Query(value = "SELECT seccionId FROM [WmsWdGeneral].dbo.PerfilSeccion WHERE perfilId = :perfilId", nativeQuery = true)
  List<Integer> getAssignedSeccionIds(@Param("perfilId") int perfilId);

  @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM [WmsWdGeneral].dbo.ClienteValidacion cv JOIN [WmsWdGeneral].dbo.ClienteValidacionTipo cvt ON cv.ValidacionTipoId = cvt.Id JOIN [WmsWdGeneral].dbo.Perfil p ON p.clienteId = cv.ClienteId WHERE p.id = :perfilId AND cvt.Codigo = :codigo AND cv.Activo = 1", nativeQuery = true)
  Integer tieneValidacion(@Param("perfilId") int perfilId, @Param("codigo") String codigo);
}
