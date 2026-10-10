package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface MaestroRepository extends BaseRepository<MaestroModel, Integer> {
	List<MaestroModel> findByTipoMaestroId(int tipoMaestroId);
	List<MaestroModel> findByTipoMaestroIdAndActivo(int tipoMaestroId, int activo);

	@Query(value = "SELECT f.codigo FROM pa_gettipologymaster(CAST(:desc1 AS character varying), CAST(:desc2 AS character varying), CAST(:desc3 AS character varying), CAST(:desc4 AS character varying)) f", nativeQuery = true)
	List<Object[]> getTipologias(
			@Param("desc1") String desc1,
			@Param("desc2") String desc2,
			@Param("desc3") String desc3,
			@Param("desc4") String desc4);

	@Query(value = "SELECT CAST(f.id AS integer) FROM pa_getidmaster(CAST(:codigo AS character varying), CAST(:tipo AS character varying)) f", nativeQuery = true)
	List<Integer> getIdMaster(@Param("codigo") String codigo, @Param("tipo") String tipo);

	@Query(value = "SELECT f.codigo FROM pa_getlistmaster(CAST(:tipo AS character varying)) f", nativeQuery = true)
	List<String> getListByTipo(@Param("tipo") String tipo);

	@Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
	Integer getIdByCodigo(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

	@Query(value = "SELECT f.codigo FROM pa_getoriginsmaster(CAST(:tipo AS character varying)) f", nativeQuery = true)
	List<String> getOrigenes(@Param("tipo") String tipo);

	@Query(value = "SELECT CAST(f.spallet AS integer) FROM pa_getfamilynumberpallet(CAST(:codigoSap AS character varying), CAST(NULL AS character varying)) f", nativeQuery = true)
	int getFamilyNumberPallet(@Param("codigoSap") String codigoSap);

	@Query(value = "SELECT pa_addcountpalletfamily(CAST(:value AS character varying), CAST(NULL AS integer), CAST(NULL AS integer))", nativeQuery = true)
	int addCountPalletFamily(@Param("value") String value, @Param("familyId") int familyId,
			@Param("filas") Integer filas);

	@Query(value = "SELECT f.codigo FROM pa_getmodelmaster(CAST(:codigoSap AS character varying)) f", nativeQuery = true)
	List<Object[]> getModelMaster(@Param("codigoSap") String codigoSap);

	@Query(value = "SELECT f.codigo FROM pa_getlevelsclasification() f", nativeQuery = true)
	List<Object[]> getLevelsClasification();

	@Query(value = "SELECT f.codigo, f.descripcion FROM pa_getlistfailuresmaster(CAST(:nombre AS character varying)) f", nativeQuery = true)
	List<Object[]> getFallas(@Param("nombre") String nombre);

	@Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
	Integer getIdSerial(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

	@Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
	Integer getIdNoSerial(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

	@Query(value = "SELECT f.descripcion FROM pa_getdescriptionmaster(CAST(:codigo AS character varying)) f", nativeQuery = true)
	List<Object[]> getDesctiption(@Param("codigo") String codigo);

	@Query(value = "SELECT CAST(f.codigo AS integer) FROM pa_getwarrantymaster() f", nativeQuery = true)
	Integer getWarranty();

	@Query(value = "SELECT CAST(f.nivel AS integer) FROM pa_searchlevelcomponentsasig(CAST(:serial AS character varying)) f", nativeQuery = true)
	Integer searchLevelComponentsAsig(@Param("serial") String serial);

	@Query(value = "SELECT CAST(f.\"Codigo\" AS integer) FROM pa_searchlevel(CAST(:levelId AS integer)) f", nativeQuery = true)
	Integer getLevelById(@Param("levelId") Integer levelId);

	@Query(value = "SELECT CAST(f.\"Id\" AS integer) FROM pa_searchlevelid(CAST(:level AS character varying)) f", nativeQuery = true)
	Integer getLevelId(@Param("level") String level);

	@Query(value = "SELECT CAST(f.id AS integer) FROM pa_getidlevelrepairmaster() f", nativeQuery = true)
	Integer getIdLevelRepairMaster();

	@Transactional
	@Query(value = "SELECT 1 FROM pa_insertcomponentsasig(CAST(:serial AS character varying), CAST(:componenteId AS integer))", nativeQuery = true)
	void insertComponentsAsig(@Param("serial") String serial, @Param("componenteId") Integer componenteId);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_deletecomponentsasig(CAST(:serial AS character varying), CAST(:componenteId AS integer))", nativeQuery = true)
	void deleteComponentsAsig(@Param("serial") String serial, @Param("componenteId") Integer componenteId);

	@Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.nivel FROM pa_searchcomponents(CAST(:serial AS character varying), CAST(:falla AS character varying)) f", nativeQuery = true)
	List<Object[]> searchComponents(@Param("serial") String serial, @Param("falla") String falla);

	@Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.nivel FROM pa_searchcomponentsasig(CAST(:serial AS character varying), CAST(:falla AS character varying), CAST(NULL AS integer)) f", nativeQuery = true)
	List<Object[]> searchComponentsAsig(@Param("serial") String serial, @Param("falla") String falla,
			@Param("filas") Integer filas);

	@Query(value = "SELECT CAST(f.scaja AS integer) FROM pa_getfamilynumberbox(CAST(:codigoSap AS character varying), CAST(:destino AS character varying)) f", nativeQuery = true)
	Integer getFamilyNumberBox(@Param("codigoSap") String codigoSap, @Param("destino") String destino);

	@Query(value = "SELECT f.siglas FROM pa_getfamilyacronyms(CAST(:codigoSap AS character varying), CAST(:destino AS character varying)) f", nativeQuery = true)
	List<String> getFamilyAcronyms(@Param("codigoSap") String codigoSap, @Param("destino") String destino);

	@Transactional
	@Query(value = "SELECT pa_addcountboxfamily(CAST(:familyId AS character varying), CAST(:value AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void addCountBoxFamily(@Param("familyId") int familyId, @Param("value") String value, @Param("filas") Integer filas);
	
	@Query(value = "SELECT f.codigo FROM pa_getfamilymaster(CAST(:codigoSap AS character varying)) f", nativeQuery = true)
	List<Object[]> getFamilyMaster(@Param("codigoSap") String codigoSap);

	@Query(value = "SELECT f.codigo FROM pa_getprovidermaster(CAST(:codigoSap AS character varying)) f", nativeQuery = true)
	List<Object[]> getProviderMaster(@Param("codigoSap") String codigoSap);

	@Query(value = "SELECT f.descripcion FROM pa_getproviderdescriptionmaster(CAST(:descripcion AS character varying)) f", nativeQuery = true)
	List<Object[]> getProviderDescriptionMaster(@Param("descripcion") String codigoSap);

	@Query(value = "SELECT f.\"Codigo\" FROM pa_getmasternamebyid(CAST(:id AS integer)) f", nativeQuery = true)
	List<String> getMasterNameById(@Param("id") Integer id);

	@Query(value = "SELECT f.\"Descripcion\" FROM pa_getmasterdescriptionbyid(CAST(:id AS integer)) f", nativeQuery = true)
	List<String> getMasterDescriptionById(@Param("id") Integer id);

	@Query(value = "SELECT f.\"Detalle\" FROM pa_getmasterdetailbyid(CAST(:id AS integer)) f", nativeQuery = true)
	List<String> getMasterDetailById(@Param("id") Integer id);

	@Query(value = "SELECT id FROM Maestro WHERE descripcion = :descripcion AND tipo = :tipo", nativeQuery = true)
	Integer getIdByDescripcionAndTipo(@Param("descripcion") String descripcion, @Param("tipo") String tipo);

	@Query(value = "SELECT CAST(f.id AS integer), f.codigo, f.descripcion, f.detalle, f.adicional, f.\"tipoMaestro\", CAST(f.activo AS boolean) FROM pa_searchmaster(CAST(:tipoMaestro AS character varying)) f", nativeQuery = true)
	List<Object[]> searchSP(@Param("tipoMaestro") String tipoMaestro);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_insertmaster(CAST(:codigo AS character varying), CAST(:descripcion AS character varying), CAST(:detalle AS character varying), CAST(:adicional AS character varying), CAST(:tipoMaestroId AS integer))", nativeQuery = true)
	void insertSP(@Param("codigo") String codigo, @Param("descripcion") String descripcion,
			@Param("detalle") String detalle, @Param("adicional") String adicional,
			@Param("tipoMaestroId") int tipoMaestroId);

	@Transactional
	@Query(value = "SELECT pa_updatemaster(CAST(:codigo AS character varying), CAST(:descripcion AS character varying), CAST(:detalle AS character varying), CAST(:adicional AS character varying), CAST(:tipoMaestroId AS integer), CAST(:id AS integer), CAST(NULL AS integer))", nativeQuery = true)
	Integer updateSP(@Param("codigo") String codigo, @Param("descripcion") String descripcion,
			@Param("detalle") String detalle, @Param("adicional") String adicional,
			@Param("tipoMaestroId") int tipoMaestroId, @Param("id") Integer id);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_deletemaster(CAST(:id AS integer))", nativeQuery = true)
	void deleteSP(@Param("id") Integer id);

	@Transactional
	@Query(value = "SELECT pa_innactivatemaster(CAST(:estado AS integer), CAST(:id AS integer), CAST(NULL AS integer))", nativeQuery = true)
	Integer innactivateSP(@Param("estado") Integer estado, @Param("id") Integer id);

	@Query(value = """
			SELECT COALESCE(SUM(cnt), 0) FROM (
				SELECT COUNT(*) cnt FROM CodigoSap WHERE familiaId = :id
				UNION ALL SELECT COUNT(*) FROM CodigoSap WHERE tipoEquipoId = :id
				UNION ALL SELECT COUNT(*) FROM CodigoSap WHERE areaId = :id
				UNION ALL SELECT COUNT(*) FROM CodigoSap WHERE proveedorId = :id
				UNION ALL SELECT COUNT(*) FROM CodigoSap WHERE modeloId = :id
				UNION ALL SELECT COUNT(*) FROM Ingreso WHERE tipologiaId = :id
				UNION ALL SELECT COUNT(*) FROM Ingreso WHERE tipoOrigenId = :id
				UNION ALL SELECT COUNT(*) FROM Ingreso WHERE origenId = :id
				UNION ALL SELECT COUNT(*) FROM Ingreso WHERE nivelId = :id
				UNION ALL SELECT COUNT(*) FROM Ingreso WHERE modeloId = :id
				UNION ALL SELECT COUNT(*) FROM Calidad WHERE estadoFinalId = :id
				UNION ALL SELECT COUNT(*) FROM Calidad WHERE fallaCosmeticaId = :id
				UNION ALL SELECT COUNT(*) FROM Diagnostico WHERE estadoFinalId = :id
				UNION ALL SELECT COUNT(*) FROM Diagnostico WHERE fallaId = :id
				UNION ALL SELECT COUNT(*) FROM Reparacion WHERE estadoFinalId = :id
				UNION ALL SELECT COUNT(*) FROM Reparacion WHERE motivoScrapId = :id
				UNION ALL SELECT COUNT(*) FROM SerialComponente WHERE componenteId = :id
			) refs
			""", nativeQuery = true)
	Integer getCount(@Param("id") Integer id);
}
