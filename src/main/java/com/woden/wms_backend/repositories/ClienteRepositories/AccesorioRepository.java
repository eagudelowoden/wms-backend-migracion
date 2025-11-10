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
    void insertarAccesorio(@Param("codigoSapId") Integer codigoSapId, @Param("tipoAccesorio") String tipoAccesorio, @Param("tipoOrigenId") Integer tipoOrigenId, @Param("origenId") Integer origenId, @Param("palletId") Integer palletId, @Param("estadoId") Integer estadoId, @Param("documento") String documento, @Param("observacion") String observacion, @Param("guia") String guia, @Param("usuarioId") Integer usuarioId, @Param("caja") Integer caja, @Param("prealertaId") Integer prealertaId, @Param("cruce") Boolean cruce);

    @Query(value = "{call pa_SearchAccesory(:palletId)}", nativeQuery = true)
    List<Object[]> buscarPorPalletId(@Param("palletId") int palletId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_DeleteAccesory :palletId, :cantidad, :codigoSapId", nativeQuery = true)
    Integer deleteAccesorio(@Param("palletId") Integer palletId, @Param("cantidad") Integer cantidad, @Param("codigoSapId") Integer codigoSapId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_SendAccesory :palletId, :estadoId, :filas OUT", nativeQuery = true)
    void sendAccesory(@Param("palletId") Integer palletId, @Param("estadoId") Integer estadoId, @Param("filas") Integer filas);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UnifyAccesory :palletIdDestino, :palletId", nativeQuery = true)
    void unifyAccesory(@Param("palletIdDestino") Integer palletIdDestino, @Param("palletId") Integer palletId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdatePalletAccesory :accesorioId, :palletId", nativeQuery = true)
    void updatePalletAccesory(@Param("accesorioId") Integer accesorioId, @Param("palletId") Integer palletId);

    @Query(value = "EXEC pa_SearchSeparatePalletAccesory :cantidad, :palletId", nativeQuery = true)
    List<Object[]> searchSeparatePalletAccesory(@Param("cantidad") Integer cantidad, @Param("palletId") Integer palletId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdateSerialAccesory :serialNuevo, :serialAnterior, :filas OUT", nativeQuery = true)
    void UpdateSerialAccesory(@Param("serialNuevo") String serialNuevo, @Param("serialAnterior") String serialAnterior, @Param("filas") Integer filas);

    @Query(value = "EXEC pa_SearchPackingCodigoSapAccesory :estado, :palletId, :codigoSapId", nativeQuery = true)
    List<Object[]> searchPackingCodigoSapAccesory(@Param("estado") String estado, @Param("palletId") Integer palletId, @Param("codigoSapId") Integer codigoSapId);

    @Query(value = "EXEC pa_GetPackedAccesoriesSerials :codigoSap, :tipoAccesorio, :palletId", nativeQuery = true)
    List<Object[]> GetPackedAccesoriesSerials(@Param("codigoSap") String codigoSap, @Param("tipoAccesorio") String tipoAccesorio, @Param("palletId") Integer palletId);

    @Query(value = "EXEC pa_SearchPackedAccesory :estado, :serial", nativeQuery = true)
    List<Object[]> SearchPackedAccesory(@Param("estado") String estado, @Param("serial") String serial);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdatePackingAccesory :codigoSap, :tipo, :estado, :destinoId, :serial, :palletId, :cantidad", nativeQuery = true)
    int updatePackingAccesory(@Param("codigoSap") String codigoSap, @Param("tipo") String tipo, @Param("estado") String estado, @Param("destinoId") Integer destinoId, @Param("serial") String serial, @Param("palletId") Integer palletId, @Param("cantidad") Integer cantidad);

    @Query(value = "EXEC pa_SearchAllPackedAccesory :estado", nativeQuery = true)
    List<Object[]> SearchAllPackedAccesory(@Param("estado") String estado);

    @Query(value = "EXEC pa_GetModelDispatchAccesory :palletId", nativeQuery = true)
    List<Object[]> getModelDispatchAccesory(@Param("palletId") Integer palletId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_PackOffPalletAccesory :palletId", nativeQuery = true)
    void packOffPalletAccesory(@Param("palletId") Integer palletId);

    @Query(value = "EXEC pa_GetModelDispatchBoxAccesory :palletId, :cajaDespachoId", nativeQuery = true)
    List<Object[]> getModelDispatchBoxAccesory(@Param("palletId") Integer palletId, @Param("cajaDespachoId") Integer cajaDespachoId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_PackOffPalletAccesoryBox :palletId, :cajaDespachoId", nativeQuery = true)
    void packOffPalletAccesoryBox(@Param("palletId") Integer palletId, @Param("cajaDespachoId") Integer cajaDespachoId);


    @Query(value = "EXEC pa_SearchGroupAccesory :codigoSap, :estado, :tipo", nativeQuery = true)
    List<String[]> searchGroupAccesory(@Param("codigoSap") String codigoSap, @Param("estado") String estado, @Param("tipo") String tipo);


    @Query(value = "EXEC pa_SearchCleaningAccesory :tipoAccesorio, :codigoSap", nativeQuery = true)
    List<Object[]> searchCleaningAccesory(@Param("tipoAccesorio") String tipoAccesorio, @Param("codigoSap") String codigoSap);


}
