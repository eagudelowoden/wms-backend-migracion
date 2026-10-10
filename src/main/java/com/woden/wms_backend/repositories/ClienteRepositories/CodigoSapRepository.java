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
  @Query(value = "SELECT f.codigo, f.descripcion FROM pa_getlistdescriptionsapcode() f", nativeQuery = true)
  List<Object[]> getListDescriptionSapCode();

  @Query(value = "SELECT f.codigo, f.descripcion FROM pa_getlistdescriptionsapcodeserial(CAST(:id AS integer)) f", nativeQuery = true)
  List<Object[]> getListDescriptionSapCodeSerial(@Param("id") Integer id);

  @Query(value = "SELECT f.codigo, f.descripcion FROM pa_getlistdescriptionsapcodenoserial(CAST(:id AS integer)) f", nativeQuery = true)
  List<Object[]> getListDescriptionSapCodeNoSerial(@Param("id") Integer id);

  @Query(value = "SELECT CAST(f.id AS integer) FROM pa_getidsapcode(CAST(:codigo AS character varying)) f", nativeQuery = true)
  Integer getIdByCodigo(@Param("codigo") String codigo);

  @Query(value = "SELECT CAST(f.id AS integer) FROM pa_getidsapcodebydescription(CAST(:codigo AS character varying), CAST(:descripcion AS character varying)) f", nativeQuery = true)
  List<Integer> getIdComboPallet(@Param("codigo") String codigo, @Param("descripcion") String descripcion);

  @Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.descripcion, CAST(f.\"familiaId\" AS integer), f.familia, CAST(f.\"tipoId\" AS integer), f.tipo, CAST(f.validacion AS boolean), f.direccion, f.largos, CAST(f.recorte AS boolean), CAST(f.reingreso AS integer), CAST(f.\"numSerial\" AS integer), CAST(f.\"AsignacionAcc\" AS boolean), f.\"LargosMac\", f.\"LargosSerial3\", f.\"LargosSerial4\", f.\"LargosSerial5\", f.clasificacion, CAST(f.\"validacionMac\" AS boolean), f.\"direccionMac\", CAST(f.\"recorteMac\" AS boolean), CAST(f.\"AsignacionFalla\" AS boolean), CAST(f.multimodelo AS boolean), CAST(f.\"cantidadCaja\" AS integer) FROM pa_getmodelsapcode(CAST(:codigo AS character varying)) f", nativeQuery = true)
  List<Object[]> getModelByCodigo(@Param("codigo") String codigo);

  @Query(value = "SELECT CAST(f.\"FamiliaId\" AS integer) FROM pa_getfamilyid(CAST(:codigoSap AS character varying)) f", nativeQuery = true)
  List<Integer> getFamilyId(@Param("codigoSap") String codigoSap);

  @Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.descripcion, f.familia, f.validacion, f.direccion, f.largos, f.recorte, f.reingreso, f.clasificacion, f.\"numSerial\", CAST(f.activo AS boolean), f.tipo, f.\"Asignacion\", f.\"AsignacionFa\", f.\"LargosMac\", f.\"LargosSerial3\", f.\"LargosSerial4\", f.\"LargosSerial5\", f.\"tipoEquipo\", f.area, f.modelo, f.proveedor, f.\"validacionMac\", f.\"direccionMac\", f.\"recorteMac\", f.multimodelo, CAST(f.\"cantidadCaja\" AS integer), f.smartcard FROM pa_searchsapcode(CAST(:codigo AS character varying)) f", nativeQuery = true)
  List<Object[]> search(@Param("codigo") String codigo);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertsapcode(CAST(:codigo AS character varying), CAST(:descripcion AS character varying), CAST(:familiaId AS integer), CAST(:tipoId AS integer), CAST(:validacion AS integer), CAST(:direccion AS character varying), CAST(:largos AS character varying), CAST(:recorte AS integer), CAST(:reingreso AS integer), CAST(:clasificacionId AS integer), CAST(:numSerial AS integer), CAST(:asignar AS integer), CAST(:largosMac AS character varying), CAST(:largosSerial3 AS character varying), CAST(:largosSerial4 AS character varying), CAST(:largosSerial5 AS character varying), CAST(:tipoEquipoId AS integer), CAST(:areaId AS integer), CAST(:modeloId AS integer), CAST(:proveedorId AS integer), CAST(:validacionMac AS integer), CAST(:direccionMac AS character varying), CAST(:recorteMac AS integer), CAST(:asignarFa AS integer), CAST(:multimodelo AS integer), CAST(:cantidadCaja AS integer), CAST(:smartCard AS integer))", nativeQuery = true)
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
  @Query(value = "SELECT pa_updatesapcode(CAST(:codigo AS character varying), CAST(:descripcion AS character varying), CAST(:familiaId AS integer), CAST(:tipoId AS integer), CAST(:validacion AS integer), CAST(:direccion AS character varying), CAST(:largos AS character varying), CAST(:recorte AS integer), CAST(:reingreso AS integer), CAST(:clasificacionId AS integer), CAST(:numSerial AS integer), CAST(:id AS integer), CAST(:asignar AS integer), CAST(:largosMac AS character varying), CAST(:largosSerial3 AS character varying), CAST(:largosSerial4 AS character varying), CAST(:largosSerial5 AS character varying), CAST(:tipoEquipoId AS integer), CAST(:areaId AS integer), CAST(:modeloId AS integer), CAST(:proveedorId AS integer), CAST(:validacionMac AS integer), CAST(:direccionMac AS character varying), CAST(:recorteMac AS integer), CAST(:asignarFa AS integer), CAST(:multimodelo AS integer), CAST(:cantidadCaja AS integer), CAST(:smartCard AS integer), CAST(NULL AS integer))", nativeQuery = true)
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

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deletesapcode(CAST(:id AS integer))", nativeQuery = true)
  void delete(@Param("id") Integer id);

  @Transactional
  @Query(value = "SELECT pa_innactivatesapcode(CAST(:id AS integer), CAST(:estado AS integer), CAST(NULL AS integer))", nativeQuery = true)
  Integer innactivate(@Param("id") Integer id, @Param("estado") Integer estado);

  @Query(value = "SELECT COALESCE(SUM(cnt), 0) FROM (SELECT COUNT(*) cnt FROM Pallet WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Accesorio WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Calidad WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Despacho WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Diagnostico WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Empaque WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Ensamble WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Etiquetado WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Etiqueta WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Ingreso WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Limpieza WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Posicion WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Parte WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Reparacion WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM SmartCard WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Cosmetica WHERE CodigoSapId = :id UNION ALL SELECT COUNT(*) FROM Clasificacion WHERE CodigoSapId = :id) refs", nativeQuery = true)
  Integer getCount(@Param("id") Integer id);

  @Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.descripcion FROM pa_searchsimplicodesap() f", nativeQuery = true)
  List<Object[]> searchSimpliCodeSap();

  @Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.descripcion FROM pa_searchcodesap_acc(CAST(:id AS integer)) f", nativeQuery = true)
  List<Object[]> searchCodeSapAccesorio(@Param("id") Integer codigoSapId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertacccodigosap(CAST(:codigoSapId AS integer), CAST(:accesorioId AS integer))", nativeQuery = true)
  void insertAccCodigoSap(@Param("codigoSapId") Integer codigoSapId, @Param("accesorioId") Integer accesorioId);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deleteacccodigosap(CAST(:codigoSapId AS integer), CAST(:accesorioId AS integer))", nativeQuery = true)
  void deleteAccCodigoSap(@Param("codigoSapId") Integer codigoSapId, @Param("accesorioId") Integer accesorioId);
}
