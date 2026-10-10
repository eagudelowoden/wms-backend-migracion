package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.EnsambleModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface EnsambleRepository extends BaseRepository<EnsambleModel, Integer> {
  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertassemble(CAST(:serialId AS integer), CAST(:serial AS character varying), CAST(:mac AS character varying), CAST(:serial3 AS character varying), CAST(:serial4 AS character varying), CAST(:serial5 AS character varying), CAST(:codigoSapId AS integer), CAST(:palletId AS integer), CAST(:estadoId AS integer), CAST(:tipologiaId AS integer), CAST(:nivelId AS integer), CAST(:usuarioId AS integer), CAST(:usuarioIdAsignado AS integer), CAST(:loteId AS character varying), CAST(:smartCard AS integer))", nativeQuery = true)
  void insertAssemble(
      @Param("serialId") Integer serialId,
      @Param("serial") String serial,
      @Param("mac") String mac,
      @Param("serial3") String serial3,
      @Param("serial4") String serial4,
      @Param("serial5") String serial5,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("palletId") Integer palletId,
      @Param("estadoId") Integer estadoId,
      @Param("tipologiaId") Integer tipologiaId,
      @Param("nivelId") Integer nivelId,
      @Param("usuarioId") Integer usuarioId,
      @Param("usuarioIdAsignado") Integer usuarioIdAsignado,
      @Param("loteId") Integer loteId,
      @Param("smartCard") String smartCard);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deleteassemble(CAST(:ensamble AS character varying))", nativeQuery = true)
  void deleteAssemble(@Param("ensamble") String ensamble);

  @Query(value = "SELECT CAST(f.id AS integer), f.serial, f.mac, f.serial3, f.serial4, f.serial5, f.codigosap, f.descripcion, f.usuarioasignado, CAST(f.nivelid AS integer), CAST(f.loteid AS integer), f.lote, f.modelo, f.fecha, f.rn FROM pa_getassembleduser(CAST(:perfil AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
  List<Object[]> getAssembleUser(@Param("perfil") String perfil, @Param("usuarioId") Integer usuarioId);
}
