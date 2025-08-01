package com.woden.wms_backend.repositories.WmsWdGeneral;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PerfilModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PerfilRepository extends BaseRepository<PerfilModel, Integer> {
  @Query(value = "EXEC pa_GetNameProfile :usuarioClienteId", nativeQuery = true)
  String getNameProfile(@Param("usuarioClienteId") Integer usuarioClienteId);
}
