package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EmpaqueRepository extends BaseRepository<EmpaqueModel, Integer> {

  @Query(value = "SELECT pa_insertpacking(p_serialid => CAST(:SerialId AS integer), p_serial => CAST(:Serial AS character varying), p_mac => CAST(:Mac AS character varying), p_codigosapid => CAST(:CodigoSapId AS integer), p_palletid => CAST(:PalletId AS integer), p_cajaempaqueid => CAST(:CajaEmpaqueId AS integer), p_nivelid => CAST(:NivelId AS integer), p_usuarioid => CAST(:UsuarioId AS integer), p_fecha => CAST(:Fecha AS timestamp without time zone), p_loteid => CAST(:LoteId AS integer), p_smartcardid => CAST(:SmartCardId AS integer), p_smartcard => CAST(:SmartCard AS character varying), p_filas => CAST(NULL AS integer))", nativeQuery = true)
  Integer createInsert(
      @Param("SerialId") Integer serialId,
      @Param("Serial") String serial,
      @Param("Mac") String mac,
      @Param("CodigoSapId") Integer codigoSapId,
      @Param("PalletId") Integer palletId,
      @Param("CajaEmpaqueId") Integer cajaEmpaqueId,
      @Param("NivelId") Integer nivelId,
      @Param("UsuarioId") Integer usuarioId,
      @Param("Fecha") LocalDateTime fecha,
      @Param("LoteId") Integer loteId,
      @Param("SmartCardId") Integer smartCardId,
      @Param("SmartCard") String smartCard);

  // Llama a la funcion de empaque (devuelve las filas afectadas).
  @Query(value = "SELECT pa_insertpackingwebd2(CAST(:SerialId AS integer), CAST(:Serial AS character varying), CAST(:Mac AS character varying), CAST(:CodigoSapId AS integer), CAST(:PalletId AS integer), CAST(:CajaEmpaqueId AS integer), CAST(:EstadoId AS integer), CAST(:NivelId AS integer), CAST(:UsuarioId AS integer), CAST(:Fecha AS timestamp without time zone), CAST(:LoteId AS integer), CAST(:SmartCardId AS integer), CAST(:SmartCard AS character varying), CAST(:EstadoSmartCard AS integer), CAST(:SmartCardCode AS character varying), CAST(NULL AS integer))", nativeQuery = true)
  Integer executePackingFunction(
          @Param("SerialId") Integer serialId,
          @Param("Serial") String serial,
          @Param("Mac") String mac,
          @Param("CodigoSapId") Integer codigoSapId,
          @Param("PalletId") Integer palletId,
          @Param("CajaEmpaqueId") Integer cajaEmpaqueId,
          @Param("EstadoId") Integer estadoId,
          @Param("NivelId") Integer nivelId,
          @Param("UsuarioId") Integer usuarioId,
          @Param("Fecha") LocalDateTime fecha,
          @Param("LoteId") Integer loteId,
          @Param("SmartCardId") Integer smartCardId,
          @Param("SmartCard") String smartCard,
          @Param("EstadoSmartCard") Integer estadoSmartCard,
          @Param("SmartCardCode") String smartCardCode
  );

  @Modifying
  @Query(value = "UPDATE Ingreso SET CodigoSapId = :CodigoSapIngreso WHERE Id = :SerialId AND COALESCE(CodigoSapId, 0) <> :CodigoSapIngreso", nativeQuery = true)
  int actualizarCodigoSapIngreso(@Param("SerialId") Integer serialId, @Param("CodigoSapIngreso") Integer codigoSapIngreso);

  // con CodigoSapIngreso > 0 (EMPAQUE_CODIGOSAP_PALLET) Ingreso queda con el mismo codigo SAP que el Empaque
  @Transactional
  default Integer executeInsertPacking(
          Integer serialId, String serial, String mac, Integer codigoSapId, Integer palletId, Integer cajaEmpaqueId,
          Integer estadoId, Integer nivelId, Integer usuarioId, LocalDateTime fecha, Integer loteId,
          Integer smartCardId, String smartCard, Integer estadoSmartCard, String smartCardCode,
          Integer codigoSapIngreso) {
    Integer filas = executePackingFunction(serialId, serial, mac, codigoSapId, palletId, cajaEmpaqueId, estadoId,
        nivelId, usuarioId, fecha, loteId, smartCardId, smartCard, estadoSmartCard, smartCardCode);
    if (codigoSapIngreso != null && codigoSapIngreso > 0 && filas != null && filas > 0) {
      actualizarCodigoSapIngreso(serialId, codigoSapIngreso);
    }
    return filas;
  }

  @Query(value = "SELECT 1 FROM pa_deletepacking(p_serial => CAST(:serial AS character varying))", nativeQuery = true)
  void eliminarSeriesEmpaque(@Param("serial") String serial);

  @Transactional
  @Query(value = "SELECT pa_updatepackingsmartcard(CAST(:smartCardId AS integer), CAST(:smartCardNuevo AS character varying), CAST(:serial AS character varying), CAST(NULL AS integer))", nativeQuery = true)
  void updateSmartCard(
      @Param("smartCardId") Integer smartCardId,
      @Param("smartCardNuevo") String smartCardNuevo,
      @Param("serial") String serial,
      @Param("filas") Integer filas);

//sp nuevo

  @Transactional
  @Query(value = "SELECT pa_updatepacking(CAST(:serialId AS integer), CAST(:serialNuevo AS character varying), CAST(:mac AS character varying), CAST(:serialAnterior AS character varying), CAST(NULL AS integer))", nativeQuery = true)
  void UpdatePacking(
      @Param("serialId") Integer serialId,
      @Param("serialNuevo") String serialNuevo,
      @Param("mac") String mac,
      @Param("serialAnterior") String serialAnterior,
      @Param("filas") Integer filas);

  @Transactional
  @Query(value = "SELECT pa_updatepackingweb(CAST(:serialId AS integer), CAST(:serialNuevo AS character varying), CAST(:nivelNuevo AS integer), CAST(:mac AS character varying), CAST(:serialAnterior AS character varying), CAST(NULL AS integer))", nativeQuery = true)
  void UpdatePackingWeb(
      @Param("serialId") Integer serialId,
      @Param("serialNuevo") String serialNuevo,
      @Param("nivelNuevo") Integer nivelNuevo,
      @Param("mac") String mac,
      @Param("serialAnterior") String serialAnterior,
      @Param("filas") Integer filas);

  @Query(value = "SELECT CAST(COUNT(*) AS integer) FROM Empaque WHERE TRIM(Serial) = TRIM(:serial) AND NivelId >= 62", nativeQuery = true)
  Integer countReingresosNivel62(@Param("serial") String serial);

}
