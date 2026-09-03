package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.DespachoModel;
import com.woden.wms_backend.repositories.BaseRepository;
import jakarta.transaction.Transactional;

@Repository
public interface DespachoRepository extends BaseRepository<DespachoModel, Integer>, DespachoRepositoryCustom {

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_InsertDispatch :id, :serial, :mac,:codigoSapId, :palletId,:palletIdIngreso, :cajaDespachoId, :estadoId, :tipoOrigenId, :origenId, :tipologiaId, :nivelId, :tramite, :documento, "
			+
			" :guia, :falla, :prealertaId, :cruce, :novedad, :usuarioId, :fecha, :pedidoSap, :smartCardId, :smartCard, :loteId, :serial3, :cajaIngresoId, :numeroSmartcard, :fallaCosmeticaId, :fallaFuncionalId, :causa, :observaciones", nativeQuery = true)
	void insertDispatch(
			@Param("id") Integer id,
			@Param("serial") String serial,
			@Param("mac") String mac,
			@Param("codigoSapId") Integer codigoSapId,
			@Param("palletId") Integer palletId,
			@Param("palletIdIngreso") Integer palletIdIngreso,
			@Param("cajaDespachoId") Integer cajaDespachoId,
			@Param("estadoId") Integer estadoId,
			@Param("tipoOrigenId") Integer tipoOrigenId,
			@Param("origenId") Integer origenId,
			@Param("tipologiaId") Integer tipologiaId,
			@Param("nivelId") Integer nivelId,
			@Param("tramite") String tramite,
			@Param("documento") String documento,
			@Param("guia") String guia,
			@Param("falla") String falla,
			@Param("prealertaId") Integer prealertaId,
			@Param("cruce") Integer cruce,
			@Param("novedad") String novedad,
			@Param("usuarioId") Integer usuarioId,
			@Param("fecha") String fecha,
			@Param("pedidoSap") String pedidoSap,
			@Param("smartCardId") Integer smartCardId,
			@Param("smartCard") String smartCard,
			@Param("loteId") Integer loteId,
			@Param("serial3") String serial3,
			@Param("cajaIngresoId") Integer cajaIngresoId,
			@Param("numeroSmartcard") String numeroSmartcard,
			@Param("fallaCosmeticaId") Integer fallaCosmeticaId,
			@Param("fallaFuncionalId") Integer fallaFuncionalId,
			@Param("causa") String causa,
			@Param("observaciones") String observaciones);

	/**
	 * Novedad final de Reparación (Garantía/TruckRoll) arrastrada de Ingreso a
	 * Despacho — igual que updateNovedadIngreso en IngresoRepository, sin SP
	 * propio (UPDATE de una sola columna) para no tocar pa_InsertDispatch, que
	 * se usa para TODOS los clientes (con o sin TruckRoll activo).
	 */
	@Modifying
	@Transactional
	@Query(value = "UPDATE Despacho SET NovedadId = :novedadId WHERE Serial = :serial", nativeQuery = true)
	void updateNovedadDespacho(@Param("novedadId") Integer novedadId, @Param("serial") String serial);

	@Modifying
	@Transactional
	@Query(value = "EXEC pa_InsertDispatchAccesory :id, :codigoSapId, :tipoAccesorio, :tipoOrigenId, " +
			":origenId, :palletId, :estadoId, :estadoLimpiezaId, :documento, :observacion, :guia, " +
			":usuarioId, :fechaIngreso, :serialEmpaque, :caja, :pedidoSap, :fechaLimpieza, " +
			":fechaEmpaque, :usuarioLimpiezaId", nativeQuery = true)
	void insertDispatchAccesory(
			@Param("id") Integer id,
			@Param("codigoSapId") Integer codigoSapId,
			@Param("tipoAccesorio") String tipoAccesorio,
			@Param("tipoOrigenId") Integer tipoOrigenId,
			@Param("origenId") Integer origenId,
			@Param("palletId") Integer palletId,
			@Param("estadoId") Integer estadoId, // AGREGADO
			@Param("estadoLimpiezaId") Integer estadoLimpiezaId,
			@Param("documento") String documento,
			@Param("observacion") String observacion,
			@Param("guia") String guia,
			@Param("usuarioId") Integer usuarioId,
			@Param("fechaIngreso") String fechaIngreso, // AGREGADO
			@Param("serialEmpaque") String serialEmpaque, // AGREGADO
			@Param("caja") Integer caja,
			@Param("pedidoSap") String pedidoSap, // AGREGADO
			@Param("fechaLimpieza") String fechaLimpieza,
			@Param("fechaEmpaque") String fechaEmpaque,
			@Param("usuarioLimpiezaId") Integer usuarioLimpiezaId);
}
