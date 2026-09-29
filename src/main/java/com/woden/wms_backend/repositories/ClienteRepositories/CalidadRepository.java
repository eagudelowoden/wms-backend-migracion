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


    @Procedure(procedureName = "pa_UpdateFinalStateQuality")
    Integer updateFinalStateQuality(
            @Param("palletId") Integer palletId,
            @Param("estadoFinalId") Integer estadoFinalId
    );

    @Modifying
    @Transactional
    @Query(value = "UPDATE dbo.Calidad SET EstadoFinalId = :estadoFinalId WHERE CajaEmpaqueId = :cajaEmpaqueId", nativeQuery = true)
    void updateFinalStateQualityByCaja(
            @Param("cajaEmpaqueId") Integer cajaEmpaqueId,
            @Param("estadoFinalId") Integer estadoFinalId
    );

    @Procedure(name = "pa_InsertQuality", procedureName = "pa_InsertQuality")
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

    @Procedure(procedureName = "pa_DeleteQuality")
    void eliminarSerialCalidad(@Param("serial") String serial);



    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdateQualityEntry :estadoId, :usuarioIdMovimiento, :serial", nativeQuery = true)
    void updateQualityEntry(
            @Param("estadoId") Integer estadoId,
            @Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
            @Param("serial") String serial);


    @Procedure(procedureName = "pa_UpdateQualityPalletEntryBatch")
    /*SP optimizado pa_UpdateQualityPalletEntryBatch*/
    /*SP optimizado pa_UpdateQualityPalletEntry*/
    void updateQualityPalletEntry(
            @Param("estadoId") Integer estadoId,
            @Param("usuarioIdMovimiento") Integer usuarioIdMovimiento,
            @Param("palletId") Integer palletId
    );

    @Procedure(procedureName = "pa_UpdateQuality")
    void updateQuality(
            @Param("fallaFuncionalId") Integer fallaFuncionalId,
            @Param("fallaComesticaId") Integer fallaComesticaId,
            @Param("serial") String serial
    );


    @Query(value = "EXEC pa_ValidateQualityByPallet :palletId", nativeQuery = true)
    Integer validateQualityByPallet(@Param("palletId") Integer palletId);


}




