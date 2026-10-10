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

	@Transactional
	@Query(value = "SELECT 1 FROM pa_insertdispatch(CAST(:id AS integer), CAST(:serial AS character varying), CAST(:mac AS character varying), CAST(:codigoSapId AS integer), CAST(:palletId AS integer), CAST(:palletIdIngreso AS integer), CAST(:cajaDespachoId AS integer), CAST(:estadoId AS integer), CAST(:tipoOrigenId AS integer), CAST(:origenId AS integer), CAST(:tipologiaId AS integer), CAST(:nivelId AS integer), CAST(:tramite AS character varying), CAST(:documento AS character varying), CAST(:guia AS character varying), CAST(:falla AS character varying), CAST(:prealertaId AS integer), CAST(:cruce AS integer), CAST(:novedad AS character varying), CAST(:usuarioId AS integer), CAST(:fecha AS timestamp without time zone), CAST(:pedidoSap AS character varying), CAST(:smartCardId AS integer), CAST(:smartCard AS character varying), CAST(:loteId AS integer), CAST(:serial3 AS character varying), CAST(:cajaIngresoId AS integer), CAST(:numeroSmartcard AS character varying), CAST(:fallaCosmeticaId AS integer), CAST(:fallaFuncionalId AS integer), CAST(:causa AS character varying), CAST(:observaciones AS character varying))", nativeQuery = true)
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

	@Transactional
	@Query(value = "SELECT 1 FROM pa_insertdispatchaccesory(CAST(:id AS integer), CAST(:codigoSapId AS integer), CAST(:tipoAccesorio AS character varying), CAST(:tipoOrigenId AS integer), CAST(:origenId AS integer), CAST(:palletId AS integer), CAST(:estadoId AS integer), CAST(:estadoLimpiezaId AS integer), CAST(:documento AS character varying), CAST(:observacion AS character varying), CAST(:guia AS character varying), CAST(:usuarioId AS integer), CAST(:fechaIngreso AS timestamp without time zone), CAST(:serialEmpaque AS character varying), CAST(:caja AS integer), CAST(:pedidoSap AS character varying), CAST(:fechaLimpieza AS timestamp without time zone), CAST(:fechaEmpaque AS timestamp without time zone), CAST(:usuarioLimpiezaId AS integer))", nativeQuery = true)
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
