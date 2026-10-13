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


    @Query(value = "SELECT * FROM pa_validatesmartcardinfo(p_smartcard => CAST(:SmartCard AS character varying), p_filas => CAST(:Filas AS integer)) f", nativeQuery = true)
    Object validateSmartCardInfoo(
            @Param("SmartCard") String smartCard,
            @Param("Filas") Integer filas
    );

    @Query(value = "SELECT f.\"Serial\" FROM pa_validatesmartcardinfo(CAST(:smartCard AS character varying), CAST(NULL AS integer)) f", nativeQuery = true)
    List<Object[]> validateSmartCardInfo(
            @Param("smartCard") String smartCard,
            @Param("filas") int filas // <-- aquí también String
    );

    @Transactional
    @Query(value = "SELECT 1 FROM pa_insertsmartcardweb(p_serialid => CAST(:serialId AS integer), p_serial => CAST(:serial AS character varying), p_codigosapid => CAST(:codigoSapId AS integer), p_usuarioid => CAST(:usuarioId AS integer))", nativeQuery = true)
    void createInsert(
            @Param("serialId") Integer serialId,
            @Param("serial") String serial,
            @Param("codigoSapId") Integer codigoSapId,
            @Param("usuarioId") Integer usuarioId
    );

    @Transactional
    @Query(value = "SELECT pa_updateestadosmartcardasign(CAST(:serial AS character varying), CAST(:usuarioId AS integer), CAST(NULL AS integer))", nativeQuery = true)
    void updateEstadoPreasignado(
            @Param("serial") String serial,
            @Param("usuarioId") Integer usuarioId,
            @Param("filas") Integer filas);

    @Query(value = "SELECT CAST(f.id AS integer), f.serial, f.mac, f.codigosap, f.descripcion, f.usuarioasignado, CAST(f.nivelid AS integer), CAST(f.loteid AS integer), f.lote, f.modelo, f.fecha, f.rn FROM pa_getsmartcardasinguserweb(CAST(:usuarioId AS integer)) f", nativeQuery = true)
    List<Object[]> getSmartcardsPreasignadas(@Param("usuarioId") Integer usuarioId);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_updatesmartcard(CAST(:estadoFinalId AS integer), CAST(:fallaId AS integer), CAST(:serial AS character varying))", nativeQuery = true)
    void updateEstadosSmartcard(
            @Param("estadoFinalId") Integer estadoFinalId,
            @Param("fallaId") Integer fallaId,
            @Param("serial") String serial);

    @Query(value = "SELECT f.serial, f.codigo, f.descripcion, f.falla, f.estado FROM pa_searchsmartcardentry(CAST(:estadoFinal AS character varying)) f", nativeQuery = true)
    List<Object[]> searchSmartCardEntry(@Param("estadoFinal") String estadoFinal);


}
