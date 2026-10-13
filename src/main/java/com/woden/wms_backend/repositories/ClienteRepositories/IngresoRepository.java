package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface IngresoRepository extends BaseRepository<IngresoModel, Integer> {
	@Transactional
	@Query(value = "SELECT 1 FROM pa_insertentry(CAST(:serial AS character varying), CAST(:mac AS character varying), CAST(:serial3 AS character varying), CAST(:serial4 AS character varying), CAST(:serial5 AS character varying), CAST(:codigoSapId AS integer), CAST(:palletId AS integer), CAST(:estadoId AS integer), CAST(:tipoOrigenId AS integer), CAST(:origenId AS integer), CAST(:tipologiaId AS integer), CAST(:nivelId AS integer), CAST(:tramite AS character varying), CAST(:documento AS character varying), CAST(:guia AS character varying), CAST(:caja AS integer), CAST(:falla AS character varying), CAST(:tecnicoCliente AS character varying), CAST(:prealertaId AS integer), CAST(:cruce AS integer), CAST(:novedad AS character varying), CAST(:garantiaFabricante AS integer), CAST(:usuarioId AS integer), CAST(:observaciones AS character varying), CAST(:estadoCliente AS character varying), CAST(:loteId AS integer), CAST(:cajaIngresoId AS integer), CAST(:modeloId AS integer), CAST(NULL AS integer), CAST(NULL AS character varying))", nativeQuery = true)
	Integer insertIngreso(
			@Param("serial") String serial,
			@Param("mac") String mac,
			@Param("serial3") String serial3,
			@Param("serial4") String serial4,
			@Param("serial5") String serial5,
			@Param("codigoSapId") Integer codigoSapId,
			@Param("palletId") Integer palletId,
			@Param("estadoId") Integer estadoId,
			@Param("tipoOrigenId") Integer tipoOrigenId,
			@Param("origenId") Integer origenId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("nivelId") Integer nivelId,
			@Param("tramite") String tramite,
			@Param("documento") String documento,
			@Param("guia") String guia,
			@Param("caja") Integer caja,
			@Param("falla") String falla,
			@Param("tecnicoCliente") String tecnicoCliente,
			@Param("prealertaId") Integer prealertaId,
			@Param("cruce") Integer cruce,
			@Param("novedad") String novedad,
			@Param("garantiaFabricante") Boolean garantiaFabricante,
			@Param("usuarioId") Integer usuarioId,
			@Param("observaciones") String observaciones,
			@Param("estadoCliente") String estadoCliente,
			@Param("loteId") Integer loteId,
			@Param("cajaIngresoId") Integer cajaIngresoId,
			@Param("modeloId") Integer modeloId);

	@Query(value = "SELECT 1 FROM pa_deleteentry(CAST(:serial AS character varying))", nativeQuery = true)
	void eliminarIngresos(@Param("serial") String serial);

	/** Borra todos los seriales indicados en un solo DELETE set-based (ver pa_DeleteEntries). */
	@Query(value = "SELECT 1 FROM pa_deleteentries(CAST(:serialesJson AS text))", nativeQuery = true)
	void eliminarIngresosBatch(@Param("serialesJson") String serialesJson);

	@Query(value = "SELECT CAST(f.id AS integer), f.serial, f.mac, f.\"Serial3\", f.\"Serial4\", f.\"Serial5\", f.codigo, CAST(f.\"codigoSapId\" AS integer), f.descripcion, f.estado, CAST(f.\"estadoId\" AS integer), f.nombreusuario, CAST(f.\"cajaEmpaqueId\" AS integer), f.caja, f.\"cajaDespacho\", f.pallet, CAST(f.\"palletId\" AS integer), f.posicion, f.nivel, CAST(f.\"nivelId\" AS integer), CAST(f.\"tipoOrigenId\" AS integer), CAST(f.\"origenId\" AS integer), CAST(f.\"tipologiaId\" AS integer), f.tipologia, CAST(f.\"palletIdIngreso\" AS integer), CAST(f.fecha AS timestamp), CAST(f.\"garantiaFabricante\" AS boolean), f.falla, CAST(f.\"loteId\" AS integer), CAST(f.\"PalletIdEmpaque\" AS integer), f.lote, CAST(f.\"smartCardId\" AS integer), f.\"SmartCard\", CAST(f.\"cajaIngresoId\" AS integer), f.\"cajaIngreso\", CAST(f.\"modeloId\" AS integer), f.modelo, CAST(f.\"truckRollId\" AS integer) FROM pa_getmodelentry(CAST(:serial AS character varying)) f", nativeQuery = true)
	List<Object[]> searchIngreso(@Param("serial") String serial);

	// @Query(value = "EXEC pa_GetModelEntryV2 :serial", nativeQuery = true)
	// String searchIngresoV2(@Param("serial") String serial);

	@Query(value = "SELECT id FROM Ingreso where serial = :serial LIMIT 1", nativeQuery = true)
	List<Object[]> searchIngresoPrueba(@Param("serial") String serial);

	@Query(value = "SELECT f.serial, f.mac, f.\"Serial3\", f.\"Serial4\", f.\"Serial5\", f.codigosap, f.descripcion, f.tipologia, f.\"tipoOrigen\", CAST(f.fecha AS timestamp), f.\"estadoCliente\", f.lote, f.modelo, CAST(f.\"Reingreso\" AS integer), CAST(f.\"TruckRollId\" AS integer) FROM pa_searchentryreingreso(CAST(:palletId AS integer)) f", nativeQuery = true)
	List<Object[]> searchEntryReingreso(@Param("palletId") Integer palletId);

	@Transactional
	@Query(value = "SELECT pa_sendentry(CAST(:estadoId AS integer), CAST(:tipologiaId AS integer), CAST(:usuarioId AS integer), CAST(:palletId AS integer), CAST(:opcion AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void sendIngreso(
			@Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId,
			@Param("opcion") Integer opcion,
			@Param("filas") Integer filas);

	@Query(value = "SELECT f.serial, f.mac, f.codigosap, f.descripcion, f.tipologia, f.lote, f.guia, f.documento, f.fecha FROM pa_searchtransitentry(CAST(:palletId AS integer)) f", nativeQuery = true)
	List<Object[]> searchIngresoTransito(@Param("palletId") Integer palletId);

	// repetido
	@Transactional
	@Query(value = "SELECT pa_updatesapcodeentry(CAST(:codigoSapId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:serial AS character varying), CAST(NULL AS integer))", nativeQuery = true)
	Integer updateSapCode(
			@Param("codigoSapId") int codigoSapId,
			@Param("usuarioIdMovimiento") int usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("filas") int filas);

	@Transactional
	@Query(value = "SELECT pa_updatebatchserialentry(CAST(:loteId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:serial AS character varying), CAST(NULL AS integer))", nativeQuery = true)
	void updateBatchSerial(
			@Param("loteId") int loteId,
			@Param("usuarioIdMovimiento") int usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("filas") int filas);

	@Query(value = "SELECT f.serial FROM pa_getserialbymac(CAST(:mac AS character varying)) f", nativeQuery = true)
	List<Object[]> getSerialByMac(@Param("mac") String mac);

	@Query(value = "SELECT pa_reingresos(CAST(:serial AS character varying), CAST(NULL AS integer))", nativeQuery = true)
	Integer getReingresos(@Param("serial") String serial);

	@Query(value = "SELECT f.serial, f.mac, f.\"smartCard\", f.\"numeroSmartcard\", f.\"Serial3\", f.\"Serial4\", f.codigo, f.descripcion, f.nivel, f.\"Lote\", f.modelo FROM pa_searchpalletboxentry(CAST(:estado AS character varying), CAST(:palletId AS integer), CAST(:cajaId AS integer)) f", nativeQuery = true)
	List<Object[]> searchPalletBoxEntry(
			@Param("estado") String estado,
			@Param("palletId") Integer palletId,
			@Param("cajaId") Integer cajaId);

	@Query(value = "SELECT f.serial, f.mac, f.\"smartCard\", f.\"numeroSmartcard\", f.\"Serial3\", f.\"Serial4\", f.\"codigoSap\", f.\"descripcionSap\", f.nivel, f.lote, f.modelo, f.\"existeEnEmpaque\", f.\"numPalletIngreso\", f.\"numCajaIngreso\", f.\"numPalletEmpaque\", f.\"numCajaEmpaque\" FROM pa_searchpalletboxvalidate(CAST(:estado AS character varying), CAST(:palletId AS integer), CAST(:cajaId AS integer)) f", nativeQuery = true)
	List<Object[]> searchPalletBoxValidate(
			@Param("estado") String estado,
			@Param("palletId") Integer palletId,
			@Param("cajaId") Integer cajaId);

	/// repetido
	@Transactional
	@Query(value = "SELECT pa_updatesapcodeentry(CAST(:codigoSapId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:serial AS character varying), CAST(NULL AS integer))", nativeQuery = true)
	Integer updateBatchPallet(
			@Param("codigoSapId") int codigoSapId,
			@Param("usuarioIdMovimiento") int usuarioIdMovimiento,
			@Param("loteId") String serial,
			@Param("filas") int filas);

	@Transactional
	@Query(value = "SELECT pa_sendcosmeticentry(CAST(:estadoId AS integer), CAST(:tipologiaId AS integer), CAST(:usuarioId AS integer), CAST(:palletId AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void sendCosmeticEntry(
			@Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId,
			@Param("filas") Integer filas);

	@Transactional
	@Query(value = "SELECT pa_sendstorageentry(CAST(:estadoId AS integer), CAST(:tipologiaId AS integer), CAST(:usuarioId AS integer), CAST(:palletId AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void SendStorageEntry(
			@Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId,
			@Param("filas") Integer filas);

	@Transactional
	@Query(value = "SELECT pa_updatetipologyentry(CAST(:palletId AS integer), CAST(:tipologiaId AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void updateTipologyEntry(
			@Param("palletId") Integer palletId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("filas") Integer filas);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_unifyentry(CAST(:palletIdDestino AS integer), CAST(:tipologiaId AS integer), CAST(:usuarioId AS integer), CAST(:palletId AS integer))", nativeQuery = true)
	void unifyEntry(
			@Param("palletIdDestino") Integer palletIdDestino,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatepalletentry(CAST(:palletId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:serial AS character varying))", nativeQuery = true)
	void updatePalletEntry(@Param("palletId") Integer palletId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatepalletandtipologyentry(CAST(:palletId AS integer), CAST(:tipologiaId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:estadoId AS integer), CAST(:serial AS character varying))", nativeQuery = true)
	void updatePalletAndTipologyEntry(@Param("palletId") Integer palletId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("estadoId") Integer estadoId,
			@Param("serial") String serial);

	@Query(value = "SELECT f.codigo FROM pa_getlevelentry(CAST(:serial AS character varying)) f", nativeQuery = true)
	List<Object[]> getLevelEntry(@Param("serial") String serial);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatestateallentry(CAST(:estadoId AS integer), CAST(:usuarioId AS integer), CAST(:serial AS character varying))", nativeQuery = true)
	void updateStateAllEntry(@Param("estadoId") Integer estadoId,
			@Param("usuarioId") Integer usuarioId,
			@Param("serial") String serial);

	@Query(value = "SELECT f.serial, f.mac, f.codigo, f.descripcion, f.falla, f.estado, CAST(f.truckrollid AS integer) FROM pa_searchdiagnosticentry(CAST(:estadoFinal AS character varying), CAST(:perfil AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
	List<Object[]> searchDiagnosticEntry(@Param("estadoFinal") String estadoFinal,
			@Param("perfil") String perfil,
			@Param("usuarioId") Integer usuarioId);

	@Query(value = "SELECT f.serial FROM pa_searchdeliveryentry(CAST(:serial AS character varying)) f", nativeQuery = true)
	List<String> searchDeliveryEntry(@Param("serial") String serial);

	@Transactional
	@Query(value = "SELECT pa_updatelevel(CAST(:levelId AS integer), CAST(:palletId AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void updateLevel(@Param("levelId") Integer levelId, @Param("palletId") Integer palletId,
			@Param("filas") Integer filas);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatestateentrynotusuario(CAST(:estadoId AS integer), CAST(:palletId AS integer), CAST(:fecha AS integer), CAST(:serial AS character varying))", nativeQuery = true)
	void updateStateEntryNotUsuario(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("fecha") Integer fecha,
			@Param("serial") String serial);

	@Transactional
	@Query(value = "SELECT pa_updatechangedentry(CAST(:serial1 AS character varying), CAST(:serial2 AS character varying), CAST(:mac AS character varying), CAST(:estadoId AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void updateChangedEntry(@Param("serial1") String serial1,
			@Param("serial2") String serial2,
			@Param("mac") String mac,
			@Param("estadoId") Integer estadoId);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updateclasificationentry(CAST(:estadoId AS integer), CAST(:nivelId AS integer), CAST(:usuarioId AS integer), CAST(:serial AS character varying))", nativeQuery = true)
	void updateClasificationEntry(@Param("serial") String serial, @Param("estadoId") Integer estadoId,
			@Param("nivelId") Integer nivelId, @Param("usuarioId") Integer usuarioId);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatestateentry(CAST(:estadoId AS integer), CAST(:palletId AS integer), CAST(:usuarioId AS integer), CAST(:fecha AS integer), CAST(:serial AS character varying))", nativeQuery = true)
	void updateStateEntry(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("usuarioId") Integer usuarioId,
			@Param("fecha") Integer fecha,
			@Param("serial") String serial);

	@Query(value = "SELECT f.serial, f.mac, f.codigo, f.descripcion FROM pa_searchclasificationentry() f", nativeQuery = true)
	List<Object[]> searchClasificationEntry();

	@Query(value = "SELECT pa_updatestateoneentry(p_estadoid => CAST(:estadoId AS integer), p_nivelid => CAST(:nivelId AS integer), p_usuarioid => CAST(:usuarioIdMovimiento AS integer), p_fecha => CAST(:fecha AS timestamp without time zone), p_serial => CAST(:serial AS character varying), p_filas => CAST(NULL AS integer))", nativeQuery = true) // 👈 usa procedureName, no name
	Integer updateStateOneEntry(
			@Param("estadoId") Integer estadoId,
			@Param("nivelId") Integer nivelId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("fecha") Date fecha,
			@Param("serial") String serial);

	/**
	 * Novedad final de Reparación (Garantía/TruckRoll) — Id de Maestro (TipoMaestro
	 * "Estados de Novedad"). Se llama solo al finalizar Reparación, cuando el
	 * TruckRollId del serial terminó en la familia de Garantía (4,5) o TruckRoll
	 * (10-13). No usa SP propio: es un UPDATE de una sola columna.
	 */
	@Modifying
	@Transactional
	@Query(value = "UPDATE Ingreso SET NovedadId = :novedadId WHERE Serial = :serial", nativeQuery = true)
	void updateNovedadIngreso(@Param("novedadId") Integer novedadId, @Param("serial") String serial);

	@Transactional
	@Query(value = "SELECT pa_updatepackingentry(CAST(:estadoId AS integer), CAST(:palletId AS integer), CAST(:cajaEmpaqueId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:serial AS character varying), CAST(:loteId AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void UpdatePackingEntry(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("cajaEmpaqueId") Integer cajaEmpaqueId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("loteId") Integer loteId,
			@Param("filas") Integer filas);

	@Query(value = "SELECT pa_updatepackingentrysmartcard(p_estadoid => CAST(:EstadoId AS integer), p_palletid => CAST(:PalletId AS integer), p_cajaempaqueid => CAST(:CajaEmpaqueId AS integer), p_usuarioid => CAST(:UsuarioId AS integer), p_serial => CAST(:Serial AS character varying), p_loteid => CAST(:LoteId AS integer), p_smartcardid => CAST(:smartCardId AS integer), p_smartcard => CAST(:SmartCard AS character varying), p_filas => CAST(NULL AS integer))", nativeQuery = true)
	Integer UpdatePackingEntrySmartCard(
			@Param("EstadoId") Integer estadoId,
			@Param("PalletId") Integer palletId,
			@Param("CajaEmpaqueId") Integer cajaEmpaqueId,
			@Param("UsuarioId") Integer usuarioIdMovimiento,
			@Param("Serial") String serial,
			@Param("LoteId") Integer loteId,
			@Param("smartCardId") Integer smartCardId,
			@Param("SmartCard") String SmartCard);

	@Query(value = "SELECT 1 FROM pa_updatepackingallentry(p_estadoid => CAST(:estadoId AS integer), p_usuarioid => CAST(:usuarioIdMovimiento AS integer), p_serial => CAST(:serial AS character varying))", nativeQuery = true)
	void UpdatePackingAllEntry(
			@Param("estadoId") Integer estadoId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial);

	@Query(value = "SELECT f.\"Serial\" FROM pa_getproactiverepairall() f", nativeQuery = true)
	List<String> getProactiveRepairAll();

	@Transactional
	@Query(value = "SELECT pa_updatechangedpackingentry(CAST(:estadoId AS integer), CAST(:palletId AS integer), CAST(:cajaEmpaqueId AS integer), CAST(:serial AS character varying), CAST(:usuarioIdMovimiento AS integer), CAST(:tipologiaId AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void UpdateChangedPackingEntry(
			@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("cajaEmpaqueId") Integer cajaEmpaqueId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("filas") Integer filas);

	@Transactional
	@Query(value = "SELECT pa_updatesmartcardentry(CAST(:smartCardId AS integer), CAST(:smartCardNuevo AS character varying), CAST(:serial AS character varying), CAST(NULL AS integer))", nativeQuery = true)
	void updateSmartCardEntry(
			@Param("smartCardId") Integer smartCardId,
			@Param("smartCardNuevo") String smartCardNuevo,
			@Param("serial") String serial,
			@Param("filas") Integer filas);

	@Transactional
	@Query(value = "SELECT pa_backrepairedentry(CAST(:serial AS character varying), CAST(NULL AS integer))", nativeQuery = true)
	void backRepairedEntry(
			@Param("serial") String serial,
			@Param("filas") Integer filas);

	@Query(value = "SELECT f.\"Serial\", f.\"Mac\", f.\"Codigo\", f.\"Descripcion\", f.\"Estado\", CAST(f.\"TruckRollId\" AS integer) FROM pa_searchrepairentry(CAST(:estadoFinal AS character varying), CAST(:perfil AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
	List<Object[]> searchRepairEntry(@Param("estadoFinal") String estadoFinal,
			@Param("perfil") String perfil,
			@Param("usuarioId") Integer usuarioId);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatestateentrynotusuariorepaired(CAST(:estadoId AS integer), CAST(:palletId AS integer), CAST(:fecha AS integer), CAST(:serial AS character varying))", nativeQuery = true)
	void updateStateEntryNotUsuarioRepaired(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("fecha") Integer fecha,
			@Param("serial") String serial);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatestateentrynotusuariodiagnosed(CAST(:estadoId AS integer), CAST(:palletId AS integer), CAST(:fecha AS integer), CAST(:serial AS character varying))", nativeQuery = true)
	void updateStateEntryNotUsuarioDiagnosed(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("fecha") Integer fecha,
			@Param("serial") String serial);

	@Transactional
	@Query(value = "SELECT pa_updatedispatchentry(CAST(:estadoId AS integer), CAST(:palletId AS integer), CAST(:cajaDespachoId AS integer), CAST(:usuarioMovimientoId AS integer), CAST(:serial AS character varying), CAST(:loteId AS integer), CAST(NULL AS integer))", nativeQuery = true)
	void updateDispatchEntry(@Param("serial") String serial, @Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("cajaDespachoId") Integer cajaDespachoId, @Param("usuarioMovimientoId") Integer usuarioMovimientoId,
			@Param("loteId") Integer loteId, @Param("filas") Integer filas);

	@Query(value = "SELECT CAST(f.\"Id\" AS integer), f.\"Serial\", f.\"Mac\", CAST(f.\"CodigoSapId\" AS integer), CAST(f.\"PalletId\" AS integer), CAST(f.\"PalletIdIngreso\" AS integer), CAST(f.\"CajaDespachoId\" AS integer), CAST(f.\"TipoOrigenId\" AS integer), CAST(f.\"OrigenId\" AS integer), CAST(f.\"TipologiaId\" AS integer), CAST(f.\"NivelId\" AS integer), f.\"Tramite\", f.\"Documento\", f.\"Guia\", f.\"Falla\", CAST(f.\"PrealertaId\" AS integer), f.\"Cruce\", f.\"Novedad\", f.\"Fecha\", f.\"SmartCard\", CAST(f.\"smartCardId\" AS integer), CAST(f.\"loteId\" AS integer), f.serial3, CAST(f.\"cajaIngresoId\" AS integer), f.\"numeroSmartcard\", CAST(f.\"FallaCosmeticaId\" AS integer), CAST(f.\"FallaFuncionalId\" AS integer), f.\"Causa\" FROM pa_getmodeldispatch(CAST(:palletId AS integer), CAST(:cajaId AS integer)) f", nativeQuery = true)
	List<Object[]> getModelDispatch(@Param("palletId") Integer palletId, @Param("cajaId") Integer cajaId);

	@Query(value = "SELECT f.serial, f.mac, f.pallet, f.caja, f.\"SmartCard\" FROM pa_searchqualityentry() f", nativeQuery = true)
	List<Object[]> searchQualityEntry();

	@Transactional
	@Query(value = "SELECT 1 FROM pa_packoffpalletentry(CAST(:palletId AS integer), CAST(:usuarioId AS integer))", nativeQuery = true)
	void packOffPalletEntry(@Param("palletId") Integer palletId, @Param("usuarioId") Integer usuarioId);

	@Query(value = "SELECT pa_updatelevel(p_nivelid => CAST(:LevelId AS integer), p_palletid => CAST(:PalletId AS integer), p_filas => CAST(NULL AS integer))", nativeQuery = true)
	Integer updateLevelWeb(
			@Param("LevelId") Integer levelId,
			@Param("PalletId") Integer palletId);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_packoffboxentry(CAST(:palletId AS integer), CAST(:cajaId AS integer), CAST(:usuarioId AS integer))", nativeQuery = true)
	void packOffBoxEntry(@Param("palletId") Integer palletId, @Param("cajaId") Integer cajaId,
			@Param("usuarioId") Integer usuarioId);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatenoveltyallentry(CAST(:serial AS character varying), CAST(:estadoId AS integer), CAST(:tipologiaId AS integer), CAST(:observaciones AS character varying), CAST(:novedad AS character varying), CAST(:usuarioId AS integer), CAST(:fallaCosmeticaId AS integer), CAST(:fallaFuncionalId AS integer))", nativeQuery = true)
	void updateNoveltyAllEntry(@Param("serial") String serial, @Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId, @Param("observaciones") String observaciones,
			@Param("novedad") String novedad, @Param("usuarioId") Integer usuarioId,
			@Param("fallaCosmeticaId") Integer fallaCosmeticaId, @Param("fallaFuncionalId") Integer fallaFuncionalId);

	@Query(value = "SELECT CAST(f.id AS integer), f.serial, f.mac, f.codigo, f.descripcion, f.novedad, f.observaciones FROM pa_searchnoveltyentry(CAST(:tipoNovedad AS character varying)) f", nativeQuery = true)
	List<Object[]> searchNoveltyEntry(@Param("tipoNovedad") String tipoNovedad);

	@Transactional
	@Query(value = "SELECT pa_updatenoveltyentry(CAST(:id AS integer), CAST(:serial AS character varying), CAST(:mac AS character varying), CAST(:codigoSapId AS integer), CAST(:guia AS character varying), CAST(:documento AS character varying), CAST(:tipoOrigenId AS integer), CAST(:origenId AS integer), CAST(:tipologiaId AS integer), CAST(:estadoId AS integer), CAST(:tipoNovedad AS character varying), CAST(:usuarioId AS integer), 4)", nativeQuery = true)
	void updateNoveltyEntry(@Param("id") Integer id, @Param("serial") String serial, @Param("mac") String mac,
			@Param("codigoSapId") Integer codigoSapId, @Param("guia") String guia, @Param("documento") String documento,
			@Param("tipoOrigenId") Integer tipoOrigenId, @Param("origenId") Integer origenId,
			@Param("tipologiaId") Integer tipologiaId, @Param("estadoId") Integer estadoId,
			@Param("tipoNovedad") String tipoNovedad, @Param("usuarioId") Integer usuarioId);

	@Query(value = "SELECT f.serial, f.mac, f.codigo, f.descripcion FROM pa_searchnoveltyentryweb(CAST(:perfil AS character varying), CAST(:usuarioId AS integer), CAST(:tipoNovedad AS character varying)) f", nativeQuery = true)
	List<Object[]> searchNoveltyEntryDelivery(@Param("perfil") String perfil, @Param("usuarioId") Integer usuarioId,
			@Param("tipoNovedad") String tipoNovedad);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_SendNoveltyEntry :estadoId, :tipologiaId, :usuarioId, :palletId, :opcion, :filas OUT", nativeQuery = true)
	void sendNoveltyEntry(
			@Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId,
			@Param("opcion") Integer opcion,
			@Param("filas") Integer filas);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatestateentrybatch(CAST(:estadoId AS integer), CAST(:palletId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:fecha AS integer), CAST(:serial AS character varying), CAST(:loteId AS integer))", nativeQuery = true)
	void updateStatusBatch(@Param("estadoId") Integer estadoId, @Param("palletId") Integer palletId,
			@Param("usuarioIdMovimiento") Integer usuarioId, @Param("fecha") Integer fecha, @Param("serial") String serial,
			@Param("loteId") Integer loteId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateEntryDispatchWeb :estadoId, :palletId, :usuarioIdMovimiento, :serial, :loteId, :filas OUT", nativeQuery = true)
	void UpdateEntryDispatch(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("loteId") Integer loteId,
			@Param("filas") Integer filas);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO Inventario (Serial, CodigoSap, CodigoSapReal, Pallet, PalletReal, EstadoId, EstadoSap, EstadoRR, Ajuste, Fecha, UsuarioId, SerialId, Mac, Sobrante) " +
			"SELECT " +
			":serial, " +
			":codigoSap, " +
			"i.CodigoSapId, " +
			":palletNumero, " +
			"i.PalletId, " +
			"COALESCE(i.EstadoId, 0), " +
			"0, " +
			"0, " +
			"COALESCE(i.Ajuste, ''), " +
			"NOW(), " +
			":usuarioId, " +
			"i.SerialId, " +
			"i.Mac, " +
			"1 " +
			"FROM (" +
			"SELECT " +
			"cs.codigo AS CodigoSapId, " +
			"COALESCE(p.numero,0) AS PalletId, " +
			"i.estadoid AS EstadoId, " +
			"i.Id AS SerialId, " +
			"i.Mac AS Mac, " +
			"CASE " +
			"    WHEN i.estadoid = 11 THEN '' " +
			"    ELSE CONCAT('Verificar estado en WMS, estado actual: ', e.Nombre) " +
			"END AS Ajuste " +
			"FROM Ingreso i " +
			"INNER JOIN CodigoSap cs ON cs.Id = i.CodigoSapId " +
			"LEFT JOIN Pallet p ON p.Id = i.PalletId " +
			"INNER JOIN Estado e ON e.Id = i.EstadoId " +
			"WHERE i.Serial = :serial " +
			"LIMIT 1) i", nativeQuery = true)
	void insertSerialInventory(
			@Param("serial") String serial,
			@Param("codigoSap") String codigoSap,
			@Param("palletNumero") String palletNumero,
			@Param("usuarioId") Integer usuarioId);

	@Query(value = "SELECT v.id, v.serial, v.codigoSapReal as codigoSap, v.codigoSapReal, v.pallet, v.palletReal," +
			"e.Nombre as estadoId," +
			"v.palletReal as estadoSap,  " +
			"cs.Descripcion as estadoRR, " +
			"v.ajuste, " +
			"v.fecha, " +
			"v.usuarioId, " +
			"v.serialId, " +
			"v.mac, " +
			"v.sobrante " +
			"FROM Inventario v " +
			"INNER JOIN Estado e ON e.id = v.estadoid " +
			"INNER JOIN Codigosap cs ON cs.codigo = v.codigoSapReal " +
			"WHERE v.pallet = :pallet " +
			"ORDER BY v.id DESC", nativeQuery = true)
	List<Object[]> getSerialsByPalletInventory(String pallet);

	@Query(value = "SELECT f.\"Nombre\" FROM pa_getvalidastateinventory(CAST(:estado AS character varying), CAST(:estadoInventario AS character varying)) f", nativeQuery = true)
	String getValidaStateInventory(@Param("estado") String estado, @Param("estadoInventario") String estadoInventario);

	@Query(value = "SELECT CAST(f.\"totalCount\" AS integer), CAST(f.\"totalProcessed\" AS integer), CAST(f.\"totalRemaining\" AS integer) FROM pa_getentryprogress(CAST(:palletId AS integer), CAST(:estadoId AS integer)) f", nativeQuery = true)
	List<Object[]> getEntryProgress(
			@Param("palletId") Integer palletId,
			@Param("estadoId") Integer estadoId);

	@Query(value = "SELECT f.serial, f.mac, f.codigosap, f.descripcion FROM pa_searchscrapentry(CAST(:estado AS character varying)) f", nativeQuery = true)
	List<Object[]> searchScrapEntry(@Param("estado") String estado);

	@Transactional
	@Query(value = "SELECT 1 FROM pa_updatescrapall(CAST(:serial AS character varying), CAST(:usuarioIdMovimiento AS integer), CAST(:novedad AS character varying), CAST(:estadoId AS integer))", nativeQuery = true)
	void updateScrapAll(@Param("estadoId") Integer estadoId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("novedad") String novedad);

	@Query(value = "SELECT CAST(f.id AS integer), f.serial, f.mac, f.serial3, f.serial4, f.serial5, f.codigosap, f.descripcion, CAST(f.nivelid AS integer), CAST(f.loteid AS integer), f.lote, f.modelo, f.fecha, f.rn FROM pa_getscrapuser(CAST(:perfil AS character varying), CAST(:usuarioIdMovimiento AS integer)) f", nativeQuery = true)
	List<Object[]> getScrapUser(@Param("perfil") String perfil, @Param("usuarioIdMovimiento") Integer usuarioIdMovimiento);

	@Query(value = "EXEC pa_GetEtiquetadoUser :usuarioIdMovimiento", nativeQuery = true)
	List<Object[]> getEtiquetadoUser(@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento);

	@Query(value = "SELECT UPPER(d.Serial) AS serial, COUNT(*) AS reingresos " +
			"FROM Despacho d " +
			"WHERE UPPER(d.Serial) IN (:seriales) " +
			"GROUP BY UPPER(d.Serial)", nativeQuery = true)
	List<Object[]> countReingresosBySeriales(@Param("seriales") List<String> seriales);

	@Query(value = "SELECT Serial FROM Ingreso WHERE Serial IN (:seriales)", nativeQuery = true)
	List<String> findExistingSerials(@Param("seriales") List<String> seriales);

	@Query(value = "SELECT Mac FROM Ingreso WHERE Mac IN (:macs) AND Mac NOT IN ('', '0')", nativeQuery = true)
	List<String> findExistingMacs(@Param("macs") List<String> macs);

	@Query(value = "SELECT Serial3 FROM Ingreso WHERE Serial3 IN (:serial3s) AND Serial3 NOT IN ('', '0')", nativeQuery = true)
	List<String> findExistingSerial3s(@Param("serial3s") List<String> serial3s);

	@Query(value = "EXEC pa_GetDiasUltimoIngreso :serial", nativeQuery = true)
	List<Object[]> getDiasUltimoIngresoRaw(@Param("serial") String serial);

	@Query(value = "EXEC dbo.pa_ValidarReingresoScrap :serial", nativeQuery = true)
	String getUltimaTipologiaSerial(@Param("serial") String serial);

	@Query(value = "SELECT UPPER(m.Serial) AS serial, m.Descripcion AS descripcion " +
			"FROM Movimiento m " +
			"INNER JOIN ( " +
			"SELECT Serial, MAX(Id) AS MaxId " +
			"FROM Movimiento " +
			"WHERE UPPER(Serial) IN (:seriales) AND Descripcion LIKE '%Tipologia%' " +
			"GROUP BY Serial " +
			") latest ON m.Id = latest.MaxId " +
			"UNION ALL " +
			"SELECT UPPER(d.Serial) AS serial, 'Despacho con Tipologia: SCRAP' AS descripcion " +
			"FROM Despacho d " +
			"INNER JOIN ( " +
			"SELECT Serial, MAX(Id) AS MaxId " +
			"FROM Despacho " +
			"WHERE UPPER(Serial) IN (:seriales) " +
			"GROUP BY Serial " +
			") latestD ON d.Id = latestD.MaxId " +
			"INNER JOIN Maestro mo ON mo.Id = d.TipologiaId " +
			"WHERE UPPER(mo.Codigo) = 'SCRAP'", nativeQuery = true)
	List<Object[]> findUltimasTipologias(@Param("seriales") List<String> seriales);

	@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END " +
			"FROM general.ClienteValidacion cv " +
			"JOIN general.ClienteValidacionTipo cvt ON cv.ValidacionTipoId = cvt.Id " +
			"WHERE cv.ClienteId = :clienteId AND cvt.Codigo = :codigo AND cv.Activo = true", nativeQuery = true)
	Integer tieneValidacionDirecta(@Param("clienteId") Integer clienteId, @Param("codigo") String codigo);

	@Query(value = "SELECT UPPER(m.Codigo) " +
			"FROM Pallet p WITH(NOLOCK) " +
			"INNER JOIN Maestro m WITH(NOLOCK) ON m.Id = p.TipologiaId " +
			"WHERE p.Id = :palletId", nativeQuery = true)
	String findPalletTipologiaCodigo(@Param("palletId") Integer palletId);

	@Query(value = "SELECT UPPER(p.Numero), UPPER(m.Codigo) " +
			"FROM Pallet p WITH(NOLOCK) " +
			"INNER JOIN Maestro m WITH(NOLOCK) ON m.Id = p.TipologiaId " +
			"WHERE UPPER(p.Numero) IN (:numeros)", nativeQuery = true)
	List<Object[]> findPalletTipologiasPorNumero(@Param("numeros") List<String> numeros);
}
