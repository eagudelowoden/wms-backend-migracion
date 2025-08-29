package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.DiagnosticoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface DiagnosticoRepository extends BaseRepository<DiagnosticoModel, Integer> {
    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InsertDiagnostic :serialId, :serial, :mac, :codigoSapId, :variable1, :variable2, :variable3, :variable4, :usuarioId", nativeQuery = true)
    void create(@Param("serialId") Integer serialId,
            @Param("serial") String serial,
            @Param("mac") String mac,
            @Param("codigoSapId") Integer codigoSapId,
            @Param("usuarioId") Integer usuarioId,
            @Param("variable1") String variable1,
            @Param("variable2") String variable2,
            @Param("variable3") String variable3,
            @Param("variable4") String variable4);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdateDiagnostic :estadoFinalId, :fallaId, :serial, :estadoCalidadId", nativeQuery = true)
    void updateDiagnostico(
            @Param("estadoFinalId") Integer estadoFinalId,
            @Param("fallaId") Integer fallaId,
            @Param("serial") String serial,
            @Param("estadoCalidadId") Integer estadoCalidadId);

    @Query(value = "EXEC pa_GetFailureIdDiagnostic :serial", nativeQuery = true)
    Integer getFailureIdDiagnostic(@Param("serial") String serial);

    @Query(value = "EXEC pa_GetFailureDiagnostic :serial", nativeQuery = true)
    String getFailureDiagnostic(@Param("serial") String serial);
}
