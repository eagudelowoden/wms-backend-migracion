package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface CodigoSapRepository extends BaseRepository<CodigoSapModel, Integer> {
  @Query(value = "EXEC pa_GetListDescriptionSapCode", nativeQuery = true)
  List<Object[]> getListDescriptionSapCode();

  @Query(value = "EXEC pa_GetListDescriptionSapCodeSerial :id", nativeQuery = true)
  List<Object[]> getListDescriptionSapCodeSerial(@Param("id") Integer id);

  @Query(value = "EXEC pa_GetListDescriptionSapCodeNoSerial :id", nativeQuery = true)
  List<Object[]> getListDescriptionSapCodeNoSerial(@Param("id") Integer id);

  @Query(value = "EXEC pa_GetIdSapCode :codigo", nativeQuery = true)
  Integer getIdByCodigo(@Param("codigo") String codigo);

  @Query(value = "EXEC pa_GetIdSapCodeByDescription :codigo, :descripcion", nativeQuery = true)
  List<Integer> getIdComboPallet(@Param("codigo") String codigo, @Param("descripcion") String descripcion);

  @Query(value = "EXEC pa_GetModelSapCode :codigo", nativeQuery = true)
  List<Object[]> getModelByCodigo(@Param("codigo") String codigo);

  @Query(value = "EXEC pa_GetFamilyId :codigoSap", nativeQuery = true)
  Integer getFamilyId(@Param("codigoSap") String codigoSap);
}
