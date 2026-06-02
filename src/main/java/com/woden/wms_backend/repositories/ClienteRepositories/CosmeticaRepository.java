package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.CosmeticaModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface CosmeticaRepository extends BaseRepository<CosmeticaModel, Integer> {

  @Query(value = "EXEC pa_SearchCosmeticaEntry :usuarioId", nativeQuery = true)
  public List<Object[]> searchCosmeticaEntry(@Param("usuarioId") Integer usuarioId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertCosmetica :serial, :usuarioId, :fecha", nativeQuery = true)
  void insertCosmetica(
      @Param("serial") String serial,
      @Param("usuarioId") Integer usuarioId,
      @Param("fecha") Date fecha);
}
