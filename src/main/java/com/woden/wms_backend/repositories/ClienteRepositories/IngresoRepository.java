package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

  @Query(value = "EXEC pa_SearchPalletBoxEntry :estado, :palletId, :cajaEmpaqueId", nativeQuery = true)
  List<Object[]> SearchPalletBoxEntry(
            @Param("estado") String estado,
            @Param("palletId") Integer palletId,
            @Param("cajaEmpaqueId") Integer cajaEmpaqueId // <-- aquí también String
  );

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
	@Query(value = "EXEC pa_SendStorageEntry :estadoId, :tipologiaId, :usuarioId, :palletId, :opcion, :filas OUT", nativeQuery = true)
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

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_UpdateStateOneEntry :estadoId, :nivelId, :usuarioIdMovimiento, :fecha, :serial, :filas OUT", nativeQuery = true)
	void updateStateOneEntry(@Param("estadoId") Integer estadoId,
			@Param("nivelId") Integer nivelId,
			@Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
			@Param("fecha") Date fecha,
			@Param("serial") String serial,
			@Param("filas") Integer filas);


    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdatePackingEntry :estadoId, :palletId, :cajaEmpaqueId, :usuarioIdMovimiento, :serial, :loteId :filas OUT", nativeQuery = true)
    void UpdatePackingEntry(@Param("estadoId") Integer estadoId,
                            @Param("palletId") Integer palletId,
                            @Param("cajaEmpaqueId") Integer cajaEmpaqueId,
                            @Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
                            @Param("serial") String serial,
                            @Param("loteId") Integer loteId,
                            @Param("filas") Integer filas);



    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdatePackingEntrySmartCard :estadoId, :palletId, :cajaEmpaqueId, :usuarioIdMovimiento, :serial, :loteId, :SmardCardId, :SmartCard, :filas OUT", nativeQuery = true)
    void UpdatePackingEntrySmartCard(@Param("estadoId") Integer estadoId,
                            @Param("palletId") Integer palletId,
                            @Param("cajaEmpaqueId") Integer cajaEmpaqueId,
                            @Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
                            @Param("serial") String serial,
                            @Param("loteId") Integer loteId,
                            @Param("SmardCardId") Integer SmardCardId,
                            @Param("SmartCard") String SmartCard,
                            @Param("filas") Integer filas);






}
