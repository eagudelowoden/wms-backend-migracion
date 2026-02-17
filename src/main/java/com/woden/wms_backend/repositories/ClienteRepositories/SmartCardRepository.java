package com.woden.wms_backend.repositories.ClienteRepositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.repositories.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmartCardRepository extends BaseRepository <SmartCardModel, Integer> {


    @Procedure(name = "pa_validateSmartCardInfo")
    Object validateSmartCardInfoo(
            @Param("SmartCard") String smartCard,
            @Param("Filas") Integer filas
    );

    @Query(value = "EXEC pa_validateSmartCardInfo :smartCard, :filas OUT", nativeQuery = true)
    List<Object[]> validateSmartCardInfo(
            @Param("smartCard") String smartCard,
            @Param("filas") int filas // <-- aquí también String
    );

    @Modifying // Indica que es una operación de escritura (INSERT/UPDATE)
    @Transactional // Asegura la integridad de la transacción
    @Query(value = "EXEC pa_InsertSmartCard :SerialId, :Serial, :CodigoSapId, :UsuarioId", nativeQuery = true)
    void createInsert(
            @Param("SerialId") Integer serialId,
            @Param("Serial") String serial,
            @Param("CodigoSapId") Integer codigoSapId,
            @Param("UsuarioId") Integer usuarioId
    );
}
