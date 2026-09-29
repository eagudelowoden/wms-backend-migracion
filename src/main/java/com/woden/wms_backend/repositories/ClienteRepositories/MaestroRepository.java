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

	@Query(value = "EXEC pa_GetTipologyMaster :desc1, :desc2, :desc3, :desc4", nativeQuery = true)
	List<Object[]> getTipologias(
			@Param("desc1") String desc1,
			@Param("desc2") String desc2,
			@Param("desc3") String desc3,
			@Param("desc4") String desc4);

	@Query(value = "EXEC pa_GetIdMaster :codigo, :tipo", nativeQuery = true)
	List<Integer> getIdMaster(@Param("codigo") String codigo, @Param("tipo") String tipo);

	@Query(value = "EXEC pa_GetListMaster :tipo", nativeQuery = true)
	List<String> getListByTipo(@Param("tipo") String tipo);

	@Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
	Integer getIdByCodigo(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

	@Query(value = "EXEC pa_GetOriginsMaster :tipo", nativeQuery = true)
	List<String> getOrigenes(@Param("tipo") String tipo);

	@Query(value = "EXEC pa_GetFamilyNumberPallet :codigoSap", nativeQuery = true)
	int getFamilyNumberPallet(@Param("codigoSap") String codigoSap);

	@Query(value = "EXEC pa_AddCountPalletFamily :value, :familyId :filas OUT", nativeQuery = true)
	int addCountPalletFamily(@Param("value") String value, @Param("familyId") int familyId,
			@Param("filas") Integer filas);

	@Query(value = "EXEC pa_GetModelMaster :codigoSap", nativeQuery = true)
	List<Object[]> getModelMaster(@Param("codigoSap") String codigoSap);

	@Query(value = "EXEC pa_GetLevelsClasification", nativeQuery = true)
	List<Object[]> getLevelsClasification();

	@Query(value = "EXEC pa_GetListFailuresMaster :nombre", nativeQuery = true)
	List<Object[]> getFallas(@Param("nombre") String nombre);

	@Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
	Integer getIdSerial(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

	@Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
	Integer getIdNoSerial(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

	@Query(value = "EXEC pa_GetDescriptionMaster :codigo", nativeQuery = true)
	List<Object[]> getDesctiption(@Param("codigo") String codigo);

	@Query(value = "EXEC pa_GetWarrantyMaster", nativeQuery = true)
	Integer getWarranty();

	@Query(value = "EXEC pa_SearchLevelComponentsAsig :serial", nativeQuery = true)
	Integer searchLevelComponentsAsig(@Param("serial") String serial);

	@Query(value = "EXEC pa_SearchLevel :levelId", nativeQuery = true)
	Integer getLevelById(@Param("levelId") Integer levelId);

	@Query(value = "EXEC pa_SearchLevelId :level", nativeQuery = true)
	Integer getLevelId(@Param("level") String level);

	@Query(value = "EXEC pa_GetIdLevelRepairMaster", nativeQuery = true)
	Integer getIdLevelRepairMaster();

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_InsertComponentsAsig :serial, :componenteId", nativeQuery = true)
	void insertComponentsAsig(@Param("serial") String serial, @Param("componenteId") Integer componenteId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_DeleteComponentsAsig :serial, :componenteId", nativeQuery = true)
	void deleteComponentsAsig(@Param("serial") String serial, @Param("componenteId") Integer componenteId);

	@Query(value = "EXEC pa_SearchComponents :serial, :falla", nativeQuery = true)
	List<Object[]> searchComponents(@Param("serial") String serial, @Param("falla") String falla);

	@Query(value = "EXEC pa_SearchComponentsAsig :serial, :falla, :filas OUT", nativeQuery = true)
	List<Object[]> searchComponentsAsig(@Param("serial") String serial, @Param("falla") String falla,
			@Param("filas") Integer filas);

	@Query(value = "EXEC pa_GetFamilyNumberBox :codigoSap, :destino", nativeQuery = true)
	Integer getFamilyNumberBox(@Param("codigoSap") String codigoSap, @Param("destino") String destino);

	@Query(value = "EXEC pa_GetFamilyAcronyms :codigoSap, :destino", nativeQuery = true)
	List<String> getFamilyAcronyms(@Param("codigoSap") String codigoSap, @Param("destino") String destino);

	@Transactional
	@Modifying
	@Query(value = "EXEC pa_AddCountBoxFamily :familyId, :value, :filas OUT", nativeQuery = true)
	void addCountBoxFamily(@Param("familyId") int familyId, @Param("value") String value, @Param("filas") Integer filas);
	
	@Query(value = "EXEC pa_GetFamilyMaster :codigoSap", nativeQuery = true)
	List<Object[]> getFamilyMaster(@Param("codigoSap") String codigoSap);

	@Query(value = "EXEC pa_GetProviderMaster :codigoSap", nativeQuery = true)
	List<Object[]> getProviderMaster(@Param("codigoSap") String codigoSap);

	@Query(value = "EXEC pa_GetProviderDescriptionMaster :descripcion", nativeQuery = true)
	List<Object[]> getProviderDescriptionMaster(@Param("descripcion") String codigoSap);

	@Query(value = "EXEC pa_GetMasterNameById :id", nativeQuery = true)
	List<String> getMasterNameById(@Param("id") Integer id);

	@Query(value = "EXEC pa_GetMasterDescriptionById :id", nativeQuery = true)
	List<String> getMasterDescriptionById(@Param("id") Integer id);

	@Query(value = "EXEC pa_GetMasterDetailById :id", nativeQuery = true)
	List<String> getMasterDetailById(@Param("id") Integer id);

	@Query(value = "SELECT id FROM Maestro WHERE descripcion = :descripcion AND tipo = :tipo", nativeQuery = true)
	Integer getIdByDescripcionAndTipo(@Param("descripcion") String descripcion, @Param("tipo") String tipo);

	@Query(value = "EXEC pa_SearchMaster :tipoMaestro", nativeQuery = true)
	List<Object[]> searchSP(@Param("tipoMaestro") String tipoMaestro);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_InsertMaster :codigo, :descripcion, :detalle, :adicional, :tipoMaestroId", nativeQuery = true)
	void insertSP(@Param("codigo") String codigo, @Param("descripcion") String descripcion,
			@Param("detalle") String detalle, @Param("adicional") String adicional,
			@Param("tipoMaestroId") int tipoMaestroId);

	@Transactional
	@Query(value = "DECLARE @Filas INT; EXEC pa_UpdateMaster :codigo, :descripcion, :detalle, :adicional, :tipoMaestroId, :id, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
	Integer updateSP(@Param("codigo") String codigo, @Param("descripcion") String descripcion,
			@Param("detalle") String detalle, @Param("adicional") String adicional,
			@Param("tipoMaestroId") int tipoMaestroId, @Param("id") Integer id);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_DeleteMaster :id", nativeQuery = true)
	void deleteSP(@Param("id") Integer id);

	@Transactional
	@Query(value = "DECLARE @Filas INT; EXEC pa_InnactivateMaster :estado, :id, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
	Integer innactivateSP(@Param("estado") Integer estado, @Param("id") Integer id);

	@Query(value = """
			SELECT ISNULL(SUM(cnt), 0) FROM (
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
