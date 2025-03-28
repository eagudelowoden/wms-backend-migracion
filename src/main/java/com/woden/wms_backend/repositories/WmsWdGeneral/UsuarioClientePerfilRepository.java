package com.woden.wms_backend.repositories.WmsWdGeneral;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.UsuarioClientePerfilModel;

@Repository
public interface UsuarioClientePerfilRepository extends CrudRepository<UsuarioClientePerfilModel, Integer> {

  @Query(value = "EXEC pa_GetIdUserClientProfile :usuarioClienteId", nativeQuery = true)
  Integer getIdUsuarioClientePerfil(@Param("usuarioClienteId") int usuarioClienteId);
}