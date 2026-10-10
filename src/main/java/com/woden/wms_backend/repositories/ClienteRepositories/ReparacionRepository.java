package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.ReparacionModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface ReparacionRepository extends BaseRepository<ReparacionModel, Integer> {

    @Transactional
    @Query(value = "SELECT pa_updaterepair(CAST(:estadoFinalId AS integer), CAST(:falla1Id AS integer), CAST(:falla2Id AS integer), CAST(:falla3Id AS integer), CAST(:falla4Id AS integer), CAST(:partesCambiadas AS character varying), CAST(:motivoScrapId AS integer), CAST(:tecnicoReparacionId AS integer), CAST(:serial AS character varying), CAST(:estadoCalidadId AS integer), CAST(NULL AS integer))", nativeQuery = true)
    void updateRepair(
            @Param("estadoFinalId") Integer estadoFinalId,
            @Param("falla1Id") Integer falla1Id,
            @Param("falla2Id") Integer falla2Id,
            @Param("falla3Id") Integer falla3Id,
            @Param("falla4Id") Integer falla4Id,
            @Param("partesCambiadas") String partesCambiadas,
            @Param("motivoScrapId") Integer motivoScrapId,
            @Param("tecnicoReparacionId") Integer tecnicoReparacionId,
            @Param("serial") String serial,
            @Param("estadoCalidadId") Integer estadoCalidadId,
            @Param("filas") Integer filas);

    @Query(value = "SELECT f.\"Serial\", f.\"Mac\", f.\"Codigo\", f.\"Descripcion\", f.\"estadoFinal\", f.falla, f.\"tecnicoReparacion\", CAST(f.\"FechaReparacion\" AS timestamp) FROM pa_gethistoricrepair(CAST(:serial AS character varying)) f", nativeQuery = true)
    List<Object[]> getHistoricRepair(
            @Param("serial") String serial);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_insertrepair(CAST(:serialId AS integer), CAST(:serial AS character varying), CAST(:mac AS character varying), CAST(:codigoSapId AS integer), CAST(:estadoFinalId AS integer), CAST(:fallaDxId AS integer), CAST(:tecnicoAsignacionId AS integer), CAST(:fechaAsignacion AS timestamp without time zone), CAST(:usuarioId AS integer))", nativeQuery = true)
    void create(@Param("serialId") Integer serialId,
            @Param("serial") String serial,
            @Param("mac") String mac,
            @Param("codigoSapId") Integer codigoSapId,
            @Param("estadoFinalId") Integer estadoFinalId,
            @Param("fallaDxId") Integer fallaDxId,
            @Param("tecnicoAsignacionId") Integer tecnicoAsignacionId,
            @Param("fechaAsignacion") String fechaAsignacion,
            @Param("usuarioId") Integer usuarioId);

    @Query(value = "SELECT f.\"estadoFinal\", f.tecnicoreparacion, CAST(f.\"fechaReparacion\" AS timestamp) FROM pa_getlastrepair(CAST(:serial AS character varying)) f", nativeQuery = true)
    List<Object[]> getLastRepair(@Param("serial") String serial);

    @Query(value = "SELECT f.serial, f.mac, f.\"Codigo\", f.descripcion, f.nombre_usuario, CAST(f.truckrollid AS integer) FROM pa_getrepairuser(CAST(:perfil AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
    List<Object[]> getRepairUser(@Param("perfil") String perfil, @Param("usuarioId") Integer usuarioId);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_deleterepair(CAST(:serial AS character varying))", nativeQuery = true)
    void deleteRepair(@Param("serial") String serial);

    @Query(value = "SELECT f.serial, f.\"fechaAsignacion\" FROM pa_searchassignedrepair(CAST(:perfil AS character varying), CAST(:tecnicoAsignacionId AS integer), CAST(:tipo AS character varying)) f", nativeQuery = true)
    List<Object[]> searchAssignedRepair(@Param("perfil") String perfil,
            @Param("tecnicoAsignacionId") Integer tecnicoAsignacionId,
            @Param("tipo") String tipo);

    @Query(value = "SELECT f.serial, f.\"fechaReparacion\" FROM pa_searchrepairedrepair(CAST(:tecnicoReparacionId AS integer), CAST(:estado AS character varying)) f", nativeQuery = true)
    List<Object[]> searchRepairedRepair(@Param("tecnicoReparacionId") Integer tecnicoReparacionId,
            @Param("estado") String estado);

    @Query(value = "SELECT f.\"tecnicoAsignacion\" FROM pa_getassignedtechnicianrepair(CAST(:serial AS character varying)) f", nativeQuery = true)
    List<Object[]> getAssignedTechnicianRepair(@Param("serial") String serial);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Ingreso SET TruckRollId = :truckRollId WHERE Serial = :serial", nativeQuery = true)
    void updateTruckRollId(@Param("serial") String serial, @Param("truckRollId") Integer truckRollId);
}
