package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.woden.wms_backend.models.Entity.ClasificacionModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

public interface ClasificacionRepository extends BaseRepository<ClasificacionModel, Integer> {

  @Transactional
  @Modifying
  @Query(value = "EXEC pa_InsertClasification :serial, :estadoEnviado, :usuarioId, :fecha, :usuarioAsignadoId", nativeQuery = true)
  public void insertClasificacion(@Param("serial") String serial, @Param("estadoEnviado") String estadoEnviado,
      @Param("usuarioId") Integer usuarioId, @Param("fecha") String fecha,
      @Param("usuarioAsignadoId") Integer usuarioAsignadoId);
}
