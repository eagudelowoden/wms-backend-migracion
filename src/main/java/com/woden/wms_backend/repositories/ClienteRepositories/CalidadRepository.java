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


    @Query(value = "EXEC pa_ValidateQualityByPallet :palletId", nativeQuery = true)
    Integer validateQualityByPallet(@Param("palletId") Integer palletId);


}




