package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.ParteModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface ParteRepositoy extends BaseRepository<ParteModel, Integer> {
  @Modifying
  @Transactional
  @Query(value = "EXEC pa_SendPart :opcion, :estadoId, :usuarioId, :palletId, :filas OUT", nativeQuery = true)
  Integer sendPart(@Param("opcion") Integer opcion, @Param("estadoId") Integer estadoId,
      @Param("usuarioId") Integer usuarioId, @Param("palletId") Integer palletId,
      @Param("filas") Integer filas);
}
