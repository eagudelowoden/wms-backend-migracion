package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.woden.wms_backend.models.Entity.EtiquetadoModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

public interface EtiquetadoRepository extends BaseRepository<EtiquetadoModel, Integer> {
  @Transactional
  @Modifying
  @Query(value = "EXEC pa_InsertEtiquetado :serial, :mac, :variable1, :variable2, :variable3, :variable4, :reImpresion, :usuarioId, :fecha", nativeQuery = true)
  void insertEtiquetado(@Param("serial") String serial, @Param("mac") String mac, @Param("variable1") String variable1,
      @Param("variable2") String variable2, @Param("variable3") String variable3, @Param("variable4") String variable4,
      @Param("reImpresion") Integer reImpresion, @Param("usuarioId") Integer usuarioId, @Param("fecha") String fecha);

  @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END "
      + "FROM [WmsWdGeneral].dbo.ClienteValidacion cv "
      + "JOIN [WmsWdGeneral].dbo.ClienteValidacionTipo cvt ON cv.ValidacionTipoId = cvt.Id "
      + "WHERE cv.ClienteId = :clienteId AND cvt.Codigo = :codigo AND cv.Activo = 1", nativeQuery = true)
  Integer tieneValidacionDirecta(@Param("clienteId") Integer clienteId, @Param("codigo") String codigo);
}
