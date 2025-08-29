package com.woden.wms_backend.repositories.ClienteRepositories;

import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EmpaqueRepository extends BaseRepository<EmpaqueModel, Integer> {


    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InsertPacking :serialId, :serial, :mac, :codigoSapId, :palletId, :cajaEmpaqueId, :nivelId, :usuarioId, :fecha, :loteId, :smartCardId, :smartCard, :filas OUT", nativeQuery = true)
    void createInsert(
            @Param("serialId") Integer serialId,
            @Param("serial") String serial,
            @Param("mac") String mac,
            @Param("codigoSapId") Integer codigoSapId,
            @Param("palletId") Integer palletId,
            @Param("cajaEmpaqueId") Integer cajaEmpaqueId,
            @Param("nivelId") Integer nivelId,
            @Param("usuarioId") Integer usuarioId,
            @Param("fecha") LocalDateTime fecha,
            @Param("loteId") Integer loteId,   // 👈 agregado
            @Param("smartCardId") Integer smartCardId,
            @Param("smartCard") String smartCard,
            @Param("filas") Integer filas
    );




}
