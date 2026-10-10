package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.DiagnosticoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface DiagnosticoRepository extends BaseRepository<DiagnosticoModel, Integer> {
    @Transactional
    @Query(value = "SELECT 1 FROM pa_insertdiagnostic(CAST(:serialId AS integer), CAST(:serial AS character varying), CAST(:mac AS character varying), CAST(:codigoSapId AS integer), CAST(:variable1 AS character varying), CAST(:variable2 AS character varying), CAST(:variable3 AS character varying), CAST(:variable4 AS character varying), CAST(:usuarioId AS integer))", nativeQuery = true)
    void create(@Param("serialId") Integer serialId,
            @Param("serial") String serial,
            @Param("mac") String mac,
            @Param("codigoSapId") Integer codigoSapId,
            @Param("usuarioId") Integer usuarioId,
            @Param("variable1") String variable1,
            @Param("variable2") String variable2,
            @Param("variable3") String variable3,
            @Param("variable4") String variable4);

    @Query(value = "SELECT 1 FROM pa_updatediagnostic(CAST(:estadoFinalId AS integer), CAST(:fallaId AS integer), CAST(:serial AS character varying), CAST(:estadoCalidadId AS integer))", nativeQuery = true)
    void updateDiagnostico(
            @Param("estadoFinalId") Integer estadoFinalId,
            @Param("fallaId") Integer fallaId,
            @Param("serial") String serial,
            @Param("estadoCalidadId") Integer estadoCalidadId);

    @Query(value = "SELECT CAST(f.\"FallaId\" AS integer) FROM pa_getfailureiddiagnostic(CAST(:serialId AS integer)) f", nativeQuery = true)
    Integer getFailureIdDiagnostic(@Param("serialId") Integer serialId);

    @Query(value = "SELECT f.falla, f.descripcion FROM pa_getfailurediagnostic(CAST(:serial AS character varying)) f", nativeQuery = true)
    String getFailureDiagnostic(@Param("serial") String serial);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_deletediagnostic(CAST(:serial AS character varying))", nativeQuery = true)
    void delete(@Param("serial") String serial);

    
    @Query(value = "SELECT CAST(f.\"Id\" AS integer), CAST(f.\"SerialId\" AS integer), f.\"Serial\", f.\"Mac\", f.\"CodigoSap\", f.\"Descripcion\", CAST(f.\"Fallaid\" AS integer), CAST(f.\"EstadoFinalid\" AS integer), f.\"Usuario\", CAST(f.\"LoteId\" AS integer), f.\"Lote\", f.\"PQRS\", f.\"Fecha\", CAST(f.\"TruckRollId\" AS integer) FROM pa_getdiagnoseduser(CAST(:perfil AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
    public List<Object[]> getDiagnosedUser(@Param("perfil") String perfil, @Param("usuarioId") Integer usuarioId);

    @Query(value = "SELECT f.variable1, f.variable2, f.variable3, f.variable4 FROM pa_getdiagnosticvariables(CAST(:serial AS character varying)) f", nativeQuery = true)
    public List<Object[]> getDiagnosticVariables(@Param("serial") String serial);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_savediagnostic(CAST(:datos AS text))", nativeQuery = true)
    void save(@Param("datos") String datos);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Ingreso SET TruckRollId = :truckRollId WHERE Serial = :serial", nativeQuery = true)
    void updateTruckRollId(@Param("serial") String serial, @Param("truckRollId") Integer truckRollId);
}
