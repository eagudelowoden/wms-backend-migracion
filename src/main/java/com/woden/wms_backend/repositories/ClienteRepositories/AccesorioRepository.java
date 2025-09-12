package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface AccesorioRepository extends BaseRepository<AccesorioModel, Integer> {
    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InsertAccesory :codigoSapId, :tipoAccesorio, :tipoOrigenId, :origenId, :palletId, :estadoId, :documento, :observacion, :guia, :usuarioId, :caja, :prealertaId, :cruce", nativeQuery = true)
    void insertarAccesorio(
            @Param("codigoSapId") Integer codigoSapId,
            @Param("tipoAccesorio") String tipoAccesorio,
            @Param("tipoOrigenId") Integer tipoOrigenId,
            @Param("origenId") Integer origenId,
            @Param("palletId") Integer palletId,
            @Param("estadoId") Integer estadoId,
            @Param("documento") String documento,
            @Param("observacion") String observacion,
            @Param("guia") String guia,
            @Param("usuarioId") Integer usuarioId,
            @Param("caja") Integer caja,
            @Param("prealertaId") Integer prealertaId,
            @Param("cruce") Boolean cruce);

    @Query(value = "{call pa_SearchAccesory(:palletId)}", nativeQuery = true)
    List<Object[]> buscarPorPalletId(@Param("palletId") int palletId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_DeleteAccesory :palletId, :cantidad, :codigoSapId", nativeQuery = true)
    Integer deleteAccesorio(@Param("palletId") Integer palletId,
                            @Param("cantidad") Integer cantidad,
                            @Param("codigoSapId") Integer codigoSapId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_SendAccesory :palletId, :estadoId, :filas OUT", nativeQuery = true)
    void sendAccesory(
            @Param("palletId") Integer palletId,
            @Param("estadoId") Integer estadoId,
            @Param("filas") Integer filas);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UnifyAccesory :palletIdDestino, :palletId", nativeQuery = true)
    void unifyAccesory(
            @Param("palletIdDestino") Integer palletIdDestino,
            @Param("palletId") Integer palletId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdatePalletAccesory :accesorioId, :palletId", nativeQuery = true)
    void updatePalletAccesory(
            @Param("accesorioId") Integer accesorioId,
            @Param("palletId") Integer palletId);

    @Query(value = "EXEC pa_SearchSeparatePalletAccesory :cantidad, :palletId", nativeQuery = true)
    List<Object[]> searchSeparatePalletAccesory(@Param("cantidad") Integer cantidad,
                                                @Param("palletId") Integer palletId);



    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdateSerialAccesory :serialNuevo, :serialAnterior, :filas OUT", nativeQuery = true)
    void UpdateSerialAccesory(
            @Param("serialNuevo") String serialNuevo,
            @Param("serialAnterior") String serialAnterior,
            @Param("filas") Integer fiñas);




}
