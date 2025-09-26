package com.woden.wms_backend.repositories.ClienteRepositories;
import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CajaEmpaqueRepository extends BaseRepository<CajaEmpaqueModel, Integer> {


    @Modifying
    @Transactional
    @Query(value = "EXEC pa_updateStatusBoxPacking :cajaEmpaqueId, :estadoId, :filas OUT", nativeQuery = true)
    void updateStatusBoxPacking(
            @Param("cajaEmpaqueId") Integer cajaEmpaqueId,
            @Param("estadoId") Integer estadoId,
            @Param("filas") Integer filas);


    @Query(value = "EXEC pa_SearchReceivePacking :estado, :numero", nativeQuery = true)
    List<Object[]> SearchReceivePacking(
            @Param("estado") String estado,
            @Param("numero") String numero
    );

    @Query(value = "EXEC pa_SearchProcessBoxPacking :palletId, :cajaEmpaqueId, :estado", nativeQuery = true)
    List<Object[]> SearchProcessBoxPacking(
            @Param("palletId") Integer palletId,
            @Param("cajaEmpaqueId") Integer cajaEmpaqueId,
            @Param("estado") String estado // <-- aquí también String
    );

    @Query(value = "EXEC pa_SearchPacking :palletId", nativeQuery = true)
    List<Object[]> SearchPacking(
            @Param("palletId") Integer palletId
    );

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InsertBoxPacking :numero, :palletId, :estadoId, :usuarioId, :fecha", nativeQuery = true)
    void create(
            @Param("numero") String numero,
            @Param("palletId") Integer palletId,
            @Param("estadoId") Integer estadoId,
            @Param("usuarioId") Integer usuarioId, // <-- aquí también String
            @Param("fecha") LocalDateTime fecha // <-- aquí también String
    );

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_DeleteBoxPacking :cajaEmpaqueId", nativeQuery = true)
    Integer eliminarCaja(
            @Param("cajaEmpaqueId") Integer cajaEmpaqueId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_SerialesByPallet :palletId", nativeQuery = true)
    List<String> SerialesByPallet(@Param("palletId") Integer palletId);
}