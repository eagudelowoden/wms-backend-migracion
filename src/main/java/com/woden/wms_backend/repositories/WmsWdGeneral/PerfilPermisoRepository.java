package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.PerfilPermisoModel;

@Repository
public interface PerfilPermisoRepository extends JpaRepository<PerfilPermisoModel, Integer> {

  @Query(value = "EXEC pa_GetListProfilePermit :modulo, :descripcion, :usuarioClientePerfilId", nativeQuery = true)
  List<String> listPerfilPermiso(
      @Param("modulo") String modulo,
      @Param("descripcion") String descripcion,
      @Param("usuarioClientePerfilId") int usuarioClientePerfilId);
}
