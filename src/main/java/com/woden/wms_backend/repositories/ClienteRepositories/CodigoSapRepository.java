package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
  List<Integer> getFamilyId(@Param("codigoSap") String codigoSap);

  @Query(value = "EXEC pa_SearchSapCode :codigo", nativeQuery = true)
  List<Object[]> search(@Param("codigo") String codigo);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertSapCode :codigo, :descripcion, :familiaId, :tipoId, :validacion, :direccion, :largos, :recorte, :reingreso, :clasificacionId, :numSerial, :asignar, :largosMac, :largosSerial3, :largosSerial4, :largosSerial5, :tipoEquipoId, :areaId, :modeloId, :proveedorId, :validacionMac, :direccionMac, :recorteMac, :asignarFa, :multimodelo, :cantidadCaja, :smartCard", nativeQuery = true)
  void insert(@Param("codigo") String codigo, @Param("descripcion") String descripcion,
      @Param("familiaId") Integer familiaId, @Param("tipoId") Integer tipoId,
      @Param("validacion") Integer validacion, @Param("direccion") String direccion,
      @Param("largos") String largos, @Param("recorte") Integer recorte,
      @Param("reingreso") Integer reingreso, @Param("clasificacionId") Integer clasificacionId,
      @Param("numSerial") Integer numSerial, @Param("asignar") Integer asignar,
      @Param("largosMac") String largosMac, @Param("largosSerial3") String largosSerial3,
      @Param("largosSerial4") String largosSerial4, @Param("largosSerial5") String largosSerial5,
      @Param("tipoEquipoId") Integer tipoEquipoId, @Param("areaId") Integer areaId,
      @Param("modeloId") Integer modeloId, @Param("proveedorId") Integer proveedorId,
      @Param("validacionMac") Integer validacionMac, @Param("direccionMac") String direccionMac,
      @Param("recorteMac") Integer recorteMac, @Param("asignarFa") Integer asignarFa,
      @Param("multimodelo") Integer multimodelo, @Param("cantidadCaja") Integer cantidadCaja,
      @Param("smartCard") Integer smartCard);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateSapCode :codigo, :descripcion, :familiaId, :tipoId, :validacion, :direccion, :largos, :recorte, :reingreso, :clasificacionId, :numSerial, :id, :asignar, :largosMac, :largosSerial3, :largosSerial4, :largosSerial5, :tipoEquipoId, :areaId, :modeloId, :proveedorId, :validacionMac, :direccionMac, :recorteMac, :asignarFa, :multimodelo, :cantidadCaja, :smartCard, @Filas OUTPUT; SELECT @Filas;", nativeQuery = true)
  Integer update(@Param("codigo") String codigo, @Param("descripcion") String descripcion,
      @Param("familiaId") Integer familiaId, @Param("tipoId") Integer tipoId,
      @Param("validacion") Integer validacion, @Param("direccion") String direccion,
      @Param("largos") String largos, @Param("recorte") Integer recorte,
      @Param("reingreso") Integer reingreso, @Param("clasificacionId") Integer clasificacionId,
      @Param("numSerial") Integer numSerial, @Param("id") Integer id,
      @Param("asignar") Integer asignar,
      @Param("largosMac") String largosMac, @Param("largosSerial3") String largosSerial3,
      @Param("largosSerial4") String largosSerial4, @Param("largosSerial5") String largosSerial5,
      @Param("tipoEquipoId") Integer tipoEquipoId, @Param("areaId") Integer areaId,
      @Param("modeloId") Integer modeloId, @Param("proveedorId") Integer proveedorId,
      @Param("validacionMac") Integer validacionMac, @Param("direccionMac") String direccionMac,
      @Param("recorteMac") Integer recorteMac, @Param("asignarFa") Integer asignarFa,
      @Param("multimodelo") Integer multimodelo, @Param("cantidadCaja") Integer cantidadCaja,
      @Param("smartCard") Integer smartCard);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteSapCode :id", nativeQuery = true)
  void delete(@Param("id") Integer id);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_InnactivateSapCode :estado, :id, @Filas OUTPUT; SELECT @Filas;", nativeQuery = true)
  Integer innactivate(@Param("estado") Integer estado, @Param("id") Integer id);

  @Query(value = "EXEC pa_GetCountSapCode :id", nativeQuery = true)
  Integer getCount(@Param("id") Integer id);

  @Query(value = "EXEC pa_SearchSimpliCodeSap", nativeQuery = true)
  List<Object[]> searchSimpliCodeSap();

  @Query(value = "EXEC pa_SearchCodeSap_Acc :id", nativeQuery = true)
  List<Object[]> searchCodeSapAccesorio(@Param("id") Integer codigoSapId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertAccCodigoSap :codigoSapId, :accesorioId", nativeQuery = true)
  void insertAccCodigoSap(@Param("codigoSapId") Integer codigoSapId, @Param("accesorioId") Integer accesorioId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteAccCodigoSap :codigoSapId, :accesorioId", nativeQuery = true)
  void deleteAccCodigoSap(@Param("codigoSapId") Integer codigoSapId, @Param("accesorioId") Integer accesorioId);
}
