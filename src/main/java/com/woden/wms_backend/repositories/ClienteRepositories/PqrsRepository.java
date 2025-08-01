package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PqrsModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface PqrsRepository extends BaseRepository<PqrsModel, Integer> {
  @Transactional
  @Modifying
  @Query(value = "EXEC pa_UpdatePqrs :estadoDiagnosticoId, :fallaDiagnosticoId, :name, :XStudioDiagnosticoTecnicoWoden", nativeQuery = true)
  void updatePqrs(
      @Param("estadoDiagnosticoId") Integer estadoDiagnosticoId,
      @Param("fallaDiagnosticoId") Integer fallaDiagnosticoId,
      @Param("name") String name,
      @Param("XStudioDiagnosticoTecnicoWoden") String XStudioDiagnosticoTecnicoWoden);
}
