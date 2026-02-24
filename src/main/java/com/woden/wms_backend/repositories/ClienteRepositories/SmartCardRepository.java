package com.woden.wms_backend.repositories.ClienteRepositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.repositories.BaseRepository;

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

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdateEstadoSmartCardAsign :serial, :usuarioId, :filas OUT", nativeQuery = true)
    void updateEstadoPreasignado(
            @Param("serial") String serial,
            @Param("usuarioId") Integer usuarioId,
            @Param("filas") Integer filas);

    @Query(value = "EXEC pa_GetSmartcardAsingUserWeb :usuarioId", nativeQuery = true)
    List<Object[]> getSmartcardsPreasignadas(@Param("usuarioId") Integer usuarioId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdateSmartCard :estadoFinalId, :fallaId, :serial", nativeQuery = true)
    void updateEstadosSmartcard(
            @Param("estadoFinalId") Integer estadoFinalId,
            @Param("fallaId") Integer fallaId,
            @Param("serial") String serial);
}
