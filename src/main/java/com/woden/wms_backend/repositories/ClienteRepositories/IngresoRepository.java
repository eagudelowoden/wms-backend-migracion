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
	@Modifying
	@Transactional
	@Query(value = "EXEC pa_InsertEntry :serial, :mac, :serial3, :serial4, :serial5, :codigoSapId, :palletId, :estadoId, "
			+
			":tipoOrigenId, :origenId, :tipologiaId, :nivelId, :tramite, :documento, :guia, :caja, :falla, :tecnicoCliente, "
			+
			":prealertaId, :cruce, :novedad, :garantiaFabricante, :usuarioId, :observaciones, :estadoCliente, :loteId, "
			+
			":cajaIngresoId, :modeloId", nativeQuery = true)
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

	@Modifying
	@Query(value = "EXEC pa_DeleteEntry :serial", nativeQuery = true)
	void eliminarIngresos(@Param("serial") String serial);

	/** Borra todos los seriales indicados en un solo DELETE set-based (ver pa_DeleteEntries). */
	@Modifying
	@Query(value = "EXEC pa_DeleteEntries :serialesJson", nativeQuery = true)
	void eliminarIngresosBatch(@Param("serialesJson") String serialesJson);

	@Query(value = "EXEC pa_GetModelEntry :serial", nativeQuery = true)
	List<Object[]> searchIngreso(@Param("serial") String serial);

	// @Query(value = "EXEC pa_GetModelEntryV2 :serial", nativeQuery = true)
	// String searchIngresoV2(@Param("serial") String serial);

	@Query(value = "SELECT top(1) id FROM Ingreso where serial = :serial", nativeQuery = true)
	List<Object[]> searchIngresoPrueba(@Param("serial") String serial);

	@Query(value = "EXEC pa_SearchEntryReingreso :palletId", nativeQuery = true)
	List<Object[]> searchEntryReingreso(@Param("palletId") Integer palletId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_SendEntry :estadoId, :tipologiaId, :usuarioId, :palletId, :opcion, :filas OUT", nativeQuery = true)
	void sendIngreso(
			@Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId,
			@Param("opcion") Integer opcion,
			@Param("filas") Integer filas);

	@Query(value = "EXEC pa_SearchTransitEntry :palletId", nativeQuery = true)
	List<Object[]> searchIngresoTransito(@Param("palletId") Integer palletId);

	// repetido
	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateSapCodeEntry :codigoSapId, :usuarioIdMovimiento, :serial, :filas OUT", nativeQuery = true)
	Integer updateSapCode(
			@Param("codigoSapId") int codigoSapId,
			@Param("usuarioIdMovimiento") int usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("filas") int filas);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateBatchSerialEntry :loteId, :usuarioIdMovimiento, :serial, :filas OUT", nativeQuery = true)
	void updateBatchSerial(
			@Param("loteId") int loteId,
			@Param("usuarioIdMovimiento") int usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("filas") int filas);

	@Query(value = "EXEC pa_GetSerialByMac :mac", nativeQuery = true)
	List<Object[]> getSerialByMac(@Param("mac") String mac);

	@Query(value = "DECLARE @result INT; EXEC pa_Reingresos :serial, @result OUTPUT; SELECT @result", nativeQuery = true)
	Integer getReingresos(@Param("serial") String serial);

	@Query(value = "EXEC pa_SearchPalletBoxEntry :estado, :palletId, :cajaId", nativeQuery = true)
	List<Object[]> searchPalletBoxEntry(
			@Param("estado") String estado,
			@Param("palletId") Integer palletId,
			@Param("cajaId") Integer cajaId);

	@Query(value = "EXEC pa_SearchPalletBoxValidate :estado, :palletId, :cajaId", nativeQuery = true)
	List<Object[]> searchPalletBoxValidate(
			@Param("estado") String estado,
			@Param("palletId") Integer palletId,
			@Param("cajaId") Integer cajaId);

	/// repetido
	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateSapCodeEntry :codigoSapId, :usuarioIdMovimiento, :serial, :filas OUT", nativeQuery = true)
	Integer updateBatchPallet(
			@Param("codigoSapId") int codigoSapId,
			@Param("usuarioIdMovimiento") int usuarioIdMovimiento,
			@Param("loteId") String serial,
			@Param("filas") int filas);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_SendCosmeticEntry :estadoId, :tipologiaId, :usuarioId, :palletId, :filas OUT", nativeQuery = true)
	void sendCosmeticEntry(
			@Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId,
			@Param("filas") Integer filas);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_SendStorageEntry :estadoId, :tipologiaId, :usuarioId, :palletId, :filas OUT", nativeQuery = true)
	void SendStorageEntry(
			@Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId,
			@Param("filas") Integer filas);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateTipologyEntry :palletId, :tipologiaId, :filas OUT", nativeQuery = true)
	void updateTipologyEntry(
			@Param("palletId") Integer palletId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("filas") Integer filas);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UnifyEntry :palletIdDestino, :tipologiaId, :usuarioId, :palletId", nativeQuery = true)
	void unifyEntry(
			@Param("palletIdDestino") Integer palletIdDestino,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioId") Integer usuarioId,
			@Param("palletId") Integer palletId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdatePalletEntry :palletId, :usuarioIdMovimiento, :serial", nativeQuery = true)
	void updatePalletEntry(@Param("palletId") Integer palletId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdatePalletAndTipologyEntry :palletId, :tipologiaId, :usuarioIdMovimiento, :estadoId, :serial", nativeQuery = true)
	void updatePalletAndTipologyEntry(@Param("palletId") Integer palletId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("estadoId") Integer estadoId,
			@Param("serial") String serial);

	@Query(value = "EXEC pa_GetLevelEntry :serial", nativeQuery = true)
	List<Object[]> getLevelEntry(@Param("serial") String serial);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateStateAllEntry :estadoId, :usuarioId, :serial", nativeQuery = true)
	void updateStateAllEntry(@Param("estadoId") Integer estadoId,
			@Param("usuarioId") Integer usuarioId,
			@Param("serial") String serial);

	@Query(value = "EXEC pa_SearchDiagnosticEntry :estadoFinal, :perfil, :usuarioId", nativeQuery = true)
	List<Object[]> searchDiagnosticEntry(@Param("estadoFinal") String estadoFinal,
			@Param("perfil") String perfil,
			@Param("usuarioId") Integer usuarioId);

	@Query(value = "EXEC pa_SearchDeliveryEntry :serial", nativeQuery = true)
	List<String> searchDeliveryEntry(@Param("serial") String serial);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateLevel :levelId, :palletId, :filas OUT", nativeQuery = true)
	void updateLevel(@Param("levelId") Integer levelId, @Param("palletId") Integer palletId,
			@Param("filas") Integer filas);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateStateEntryNotUsuario :estadoId, :palletId, :fecha, :serial", nativeQuery = true)
	void updateStateEntryNotUsuario(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("fecha") Integer fecha,
			@Param("serial") String serial);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateChangedEntry :serial1, :serial2, :mac, :estadoId", nativeQuery = true)
	void updateChangedEntry(@Param("serial1") String serial1,
			@Param("serial2") String serial2,
			@Param("mac") String mac,
			@Param("estadoId") Integer estadoId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateClasificationEntry :estadoId, :nivelId, :usuarioId, :serial ", nativeQuery = true)
	void updateClasificationEntry(@Param("serial") String serial, @Param("estadoId") Integer estadoId,
			@Param("nivelId") Integer nivelId, @Param("usuarioId") Integer usuarioId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateStateEntry :estadoId, :palletId, :usuarioId, :fecha, :serial", nativeQuery = true)
	void updateStateEntry(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("usuarioId") Integer usuarioId,
			@Param("fecha") Integer fecha,
			@Param("serial") String serial);

	@Query(value = "EXEC pa_SearchClasificationEntry", nativeQuery = true)
	List<Object[]> searchClasificationEntry();

	@Procedure(procedureName = "pa_UpdateStateOneEntry") // 👈 usa procedureName, no name
	Integer updateStateOneEntry(
			@Param("estadoId") Integer estadoId,
			@Param("nivelId") Integer nivelId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("fecha") Date fecha,
			@Param("serial") String serial);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdatePackingEntry :estadoId, :palletId, :cajaEmpaqueId, :usuarioIdMovimiento, :serial, :loteId, :filas OUT", nativeQuery = true)
	void UpdatePackingEntry(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("cajaEmpaqueId") Integer cajaEmpaqueId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("loteId") Integer loteId,
			@Param("filas") Integer filas);

	@Procedure(procedureName = "pa_UpdatePackingEntrySmartCard")
	Integer UpdatePackingEntrySmartCard(
			@Param("EstadoId") Integer estadoId,
			@Param("PalletId") Integer palletId,
			@Param("CajaEmpaqueId") Integer cajaEmpaqueId,
			@Param("UsuarioId") Integer usuarioIdMovimiento,
			@Param("Serial") String serial,
			@Param("LoteId") Integer loteId,
			@Param("smartCardId") Integer smartCardId,
			@Param("SmartCard") String SmartCard);

	@Procedure(procedureName = "pa_UpdatePackingAllEntry")
	void UpdatePackingAllEntry(
			@Param("estadoId") Integer estadoId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial);

	@Query(value = "EXEC pa_GetProactiveRepairAll", nativeQuery = true)
	List<String> getProactiveRepairAll();

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateChangedPackingEntry :estadoId, :palletId, :cajaEmpaqueId, :serial, :usuarioIdMovimiento, :tipologiaId, :filas OUT", nativeQuery = true)
	void UpdateChangedPackingEntry(
			@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("cajaEmpaqueId") Integer cajaEmpaqueId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("filas") Integer filas);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateSmartCardEntry :smartCardId, :smartCardNuevo, :serial,  :filas OUT", nativeQuery = true)
	void updateSmartCardEntry(
			@Param("smartCardId") Integer smartCardId,
			@Param("smartCardNuevo") String smartCardNuevo,
			@Param("serial") String serial,
			@Param("filas") Integer filas);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_BackRepairedEntry :serial, :filas OUT", nativeQuery = true)
	void backRepairedEntry(
			@Param("serial") String serial,
			@Param("filas") Integer filas);

	@Query(value = "EXEC pa_SearchRepairEntry :estadoFinal, :perfil, :usuarioId", nativeQuery = true)
	List<Object[]> searchRepairEntry(@Param("estadoFinal") String estadoFinal,
			@Param("perfil") String perfil,
			@Param("usuarioId") Integer usuarioId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateStateEntryNotUsuarioRepaired :estadoId, :palletId, :fecha, :serial", nativeQuery = true)
	void updateStateEntryNotUsuarioRepaired(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("fecha") Integer fecha,
			@Param("serial") String serial);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateStateEntryNotUsuarioDiagnosed :estadoId, :palletId, :fecha, :serial", nativeQuery = true)
	void updateStateEntryNotUsuarioDiagnosed(@Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("fecha") Integer fecha,
			@Param("serial") String serial);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateDispatchEntry :estadoId, :palletId, :cajaDespachoId, :usuarioMovimientoId, :serial, :loteId, :filas OUT", nativeQuery = true)
	void updateDispatchEntry(@Param("serial") String serial, @Param("estadoId") Integer estadoId,
			@Param("palletId") Integer palletId,
			@Param("cajaDespachoId") Integer cajaDespachoId, @Param("usuarioMovimientoId") Integer usuarioMovimientoId,
			@Param("loteId") Integer loteId, @Param("filas") Integer filas);

	@Query(value = "EXEC pa_GetModelDispatch :palletId, :cajaId", nativeQuery = true)
	List<Object[]> getModelDispatch(@Param("palletId") Integer palletId, @Param("cajaId") Integer cajaId);

	@Query(value = "EXEC pa_SearchQualityEntry", nativeQuery = true)
	List<Object[]> searchQualityEntry();

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_PackOffPalletEntry :palletId, :usuarioId", nativeQuery = true)
	void packOffPalletEntry(@Param("palletId") Integer palletId, @Param("usuarioId") Integer usuarioId);

	@Procedure(procedureName = "dbo.pa_UpdateLevel")
	Integer updateLevelWeb(
			@Param("LevelId") Integer levelId,
			@Param("PalletId") Integer palletId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_PackOffBoxEntry :palletId, :cajaId, :usuarioId", nativeQuery = true)
	void packOffBoxEntry(@Param("palletId") Integer palletId, @Param("cajaId") Integer cajaId,
			@Param("usuarioId") Integer usuarioId);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateNoveltyAllEntry :serial, :estadoId, :tipologiaId, :observaciones, :novedad, :usuarioId, :fallaCosmeticaId, :fallaFuncionalId", nativeQuery = true)
	void updateNoveltyAllEntry(@Param("serial") String serial, @Param("estadoId") Integer estadoId,
			@Param("tipologiaId") Integer tipologiaId, @Param("observaciones") String observaciones,
			@Param("novedad") String novedad, @Param("usuarioId") Integer usuarioId,
			@Param("fallaCosmeticaId") Integer fallaCosmeticaId, @Param("fallaFuncionalId") Integer fallaFuncionalId);

	@Query(value = "EXEC pa_SearchNoveltyEntry :tipoNovedad", nativeQuery = true)
	List<Object[]> searchNoveltyEntry(@Param("tipoNovedad") String tipoNovedad);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateNoveltyEntry :id, :serial, :mac, :codigoSapId, :guia, :documento, :tipoOrigenId, :origenId, :tipologiaId, :estadoId, :tipoNovedad, :usuarioId, 4", nativeQuery = true)
	void updateNoveltyEntry(@Param("id") Integer id, @Param("serial") String serial, @Param("mac") String mac,
			@Param("codigoSapId") Integer codigoSapId, @Param("guia") String guia, @Param("documento") String documento,
			@Param("tipoOrigenId") Integer tipoOrigenId, @Param("origenId") Integer origenId,
			@Param("tipologiaId") Integer tipologiaId, @Param("estadoId") Integer estadoId,
			@Param("tipoNovedad") String tipoNovedad, @Param("usuarioId") Integer usuarioId);

	@Query(value = "EXEC pa_SearchNoveltyEntryWeb :perfil, :usuarioId, :tipoNovedad", nativeQuery = true)
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

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateStateEntryBatch :estadoId, :palletId, :usuarioIdMovimiento, :fecha, :serial, :loteId", nativeQuery = true)
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
	@Query(value = "DECLARE @ingreso TABLE (CodigoSapId VARCHAR(50), PalletId INT, EstadoId INT, SerialId INT, Mac VARCHAR(50), Ajuste VARCHAR(200)); "
			+
			"INSERT INTO @ingreso " +
			"SELECT TOP 1 " +
			"cs.codigo AS CodigoSapId, " +
			"ISNULL(p.numero,0) AS PalletId, " +
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
			"WHERE i.Serial = :serial; " +
			"INSERT INTO Inventario (Serial, CodigoSap, CodigoSapReal, Pallet, PalletReal, EstadoId, EstadoSap, EstadoRR, Ajuste, Fecha, UsuarioId, SerialId, Mac, Sobrante) "
			+
			"SELECT " +
			":serial, " +
			":codigoSap, " +
			"ISNULL(i.CodigoSapId, NULL), " +
			":palletNumero, " +
			"ISNULL(i.PalletId, NULL), " +
			"ISNULL(i.EstadoId, 0), " +
			"0, " +
			"0, " +
			"ISNULL(i.Ajuste, ''), " +
			"GETDATE(), " +
			":usuarioId, " +
			"ISNULL(i.SerialId, NULL), " +
			"ISNULL(i.Mac, NULL), " +
			"1 " +
			"FROM @ingreso i;", nativeQuery = true)
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

	@Query(value = "EXEC pa_GetValidaStateInventory :estado, :estadoInventario", nativeQuery = true)
	String getValidaStateInventory(@Param("estado") String estado, @Param("estadoInventario") String estadoInventario);

	@Query(value = "EXEC pa_GetEntryProgress :palletId, :estadoId", nativeQuery = true)
	List<Object[]> getEntryProgress(
			@Param("palletId") Integer palletId,
			@Param("estadoId") Integer estadoId);

	@Query(value = "EXEC pa_SearchScrapEntry :estado", nativeQuery = true)
	List<Object[]> searchScrapEntry(@Param("estado") String estado);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateScrapAll :serial, :usuarioIdMovimiento, :novedad, :estadoId", nativeQuery = true)
	void updateScrapAll(@Param("estadoId") Integer estadoId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("serial") String serial,
			@Param("novedad") String novedad);

	@Query(value = "EXEC pa_GetScrapUser :usuarioIdMovimiento", nativeQuery = true)
	List<Object[]> getScrapUser(@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento);

	@Query(value = "EXEC pa_GetEtiquetadoUser :usuarioIdMovimiento", nativeQuery = true)
	List<Object[]> getEtiquetadoUser(@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento);
}
