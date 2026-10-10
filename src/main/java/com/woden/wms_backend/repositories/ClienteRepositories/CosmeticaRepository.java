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

  @Query(value = "SELECT f.serial, f.mac, f.codigo, f.descripcion FROM pa_searchcosmeticaentry(CAST(:perfil AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
  public List<Object[]> searchCosmeticaEntry(@Param("perfil") String perfil, @Param("usuarioId") Integer usuarioId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertcosmetica(CAST(:serial AS character varying), CAST(:usuarioId AS integer), CAST(:fecha AS timestamp without time zone))", nativeQuery = true)
  void insertCosmetica(
      @Param("serial") String serial,
      @Param("usuarioId") Integer usuarioId,
      @Param("fecha") Date fecha);
}
