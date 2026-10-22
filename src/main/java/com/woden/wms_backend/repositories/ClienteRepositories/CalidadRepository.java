package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.Entity.CalidadModel;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalidadRepository extends BaseRepository<CalidadModel, Integer> {


    @Query(value = "SELECT pa_updatefinalstatequality(p_palletid => CAST(:palletId AS integer), p_estadofinalid => CAST(:estadoFinalId AS integer), p_filas => CAST(NULL AS integer))", nativeQuery = true)
    Integer updateFinalStateQuality(
            @Param("palletId") Integer palletId,
            @Param("estadoFinalId") Integer estadoFinalId
    );

    @Modifying
    @Transactional
    @Query(value = "UPDATE Calidad SET EstadoFinalId = :estadoFinalId WHERE CajaEmpaqueId = :cajaEmpaqueId", nativeQuery = true)
    void updateFinalStateQualityByCaja(
            @Param("cajaEmpaqueId") Integer cajaEmpaqueId,
            @Param("estadoFinalId") Integer estadoFinalId
    );

    @Query(value = "SELECT 1 FROM pa_insertquality(p_serialid => CAST(:SerialId AS integer), p_serial => CAST(:Serial AS character varying), p_mac => CAST(:Mac AS character varying), p_codigosapid => CAST(:CodigoSapId AS integer), p_palletid => CAST(:PalletId AS integer), p_cajaempaqueid => CAST(:CajaEmpaqueId AS integer), p_usuarioid => CAST(:UsuarioId AS integer), p_fecha => CAST(:Fecha AS timestamp without time zone))", nativeQuery = true)
    void createInsertCalidad(
            @Param("SerialId") Integer serialId,
            @Param("Serial") String serial,
            @Param("Mac") String mac,
            @Param("CodigoSapId") Integer codigoSapId,
            @Param("PalletId") Integer palletId,
            @Param("CajaEmpaqueId") Integer cajaEmpaqueId,
            @Param("UsuarioId") Integer usuarioId,
            @Param("Fecha") LocalDateTime fecha
    );

    @Query(value = "SELECT 1 FROM pa_deletequality(p_serial => CAST(:serial AS character varying))", nativeQuery = true)
    void eliminarSerialCalidad(@Param("serial") String serial);



    @Transactional
    @Query(value = "SELECT 1 FROM pa_updatequalityentry(CAST(:estadoId AS integer), CAST(:usuarioIdMovimiento AS integer), CAST(:serial AS character varying))", nativeQuery = true)
    void updateQualityEntry(
            @Param("estadoId") Integer estadoId,
            @Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
            @Param("serial") String serial);


    @Query(value = "SELECT 1 FROM pa_updatequalitypalletentrybatch(p_estadoid => CAST(:estadoId AS integer), p_usuarioid => CAST(:usuarioIdMovimiento AS integer), p_palletids => CAST(:palletId AS text))", nativeQuery = true)
    /*SP optimizado pa_UpdateQualityPalletEntryBatch*/
    /*SP optimizado pa_UpdateQualityPalletEntry*/
    void updateQualityPalletEntry(
            @Param("estadoId") Integer estadoId,
            @Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
            @Param("palletId") Integer palletId
    );

    @Query(value = "SELECT 1 FROM pa_updatequality(p_fallafuncionalid => CAST(:fallaFuncionalId AS integer), p_fallacosmeticaid => CAST(:fallaComesticaId AS integer), p_serial => CAST(:serial AS character varying))", nativeQuery = true)
    void updateQuality(
            @Param("fallaFuncionalId") Integer fallaFuncionalId,
            @Param("fallaComesticaId") Integer fallaComesticaId,
            @Param("serial") String serial
    );

    @Query(value = "DECLARE @filas INT; EXEC pa_UpdateQualityExtrasBySerial :serial, :personal, :dni, :voltaje, :observaciones, @filas OUTPUT; SELECT @filas", nativeQuery = true)
    Integer updateQualityExtrasBySerial(
            @Param("serial") String serial,
            @Param("personal") String personal,
            @Param("dni") String dni,
            @Param("voltaje") String voltaje,
            @Param("observaciones") String observaciones
    );

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdateEstadoCalidadBySerial :serial, :estadoCalidadId", nativeQuery = true)
    void updateEstadoCalidadBySerial(
            @Param("serial") String serial,
            @Param("estadoCalidadId") Integer estadoCalidadId
    );


    @Query(value = "SELECT CAST(f.resultado AS integer) FROM pa_validatequalitybypallet(CAST(:palletId AS integer)) f", nativeQuery = true)
    Integer validateQualityByPallet(@Param("palletId") Integer palletId);

    @Query(value = "SELECT TOP 1 c.Id, c.SerialId, c.Serial, c.Mac, c.CodigoSapId, c.PalletId, c.CajaEmpaqueId, " +
            "c.FallaFuncionalId, c.FallaCosmeticaId, c.UsuarioId, c.Fecha, " +
            "mf.Codigo AS FallaFuncionalCodigo, mc.Codigo AS FallaCosmeticaCodigo, " +
            "CASE WHEN ISNULL(c.EstadoFinalId, 0) = 0 THEN NULL ELSE me.Codigo END AS EstadoFinalCodigo " +
            "FROM dbo.Calidad c " +
            "LEFT JOIN dbo.Maestro mf ON mf.Id = c.FallaFuncionalId " +
            "LEFT JOIN dbo.Maestro mc ON mc.Id = c.FallaCosmeticaId " +
            "LEFT JOIN dbo.Maestro me ON me.Id = c.EstadoFinalId " +
            "WHERE c.Serial = :serial ORDER BY c.Id DESC", nativeQuery = true)
    List<Object[]> findLatestBySerial(@Param("serial") String serial);


}




