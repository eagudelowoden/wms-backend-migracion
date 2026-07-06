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
            @Param("numero") String numero);

    @Query(value = "EXEC pa_SearchProcessBoxPacking :palletId, :cajaEmpaqueId, :estado", nativeQuery = true)
    List<Object[]> SearchProcessBoxPacking(
            @Param("palletId") Integer palletId,
            @Param("cajaEmpaqueId") Integer cajaEmpaqueId,
            @Param("estado") String estado // <-- aquí también String
    );

    @Query(value = "EXEC pa_SearchPacking :palletId", nativeQuery = true)
    List<Object[]> SearchPacking(
            @Param("palletId") Integer palletId);

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

    @Query(value = "EXEC pa_GetCountBoxPacking :cajaEmpaqueId", nativeQuery = true)
    Integer getCountBoxPacking(@Param("cajaEmpaqueId") Integer cajaEmpaqueId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_UpdateStatusAllBoxPacking :palletId, :estadoId, :filas OUT", nativeQuery = true)
    Integer updateStatusAllBoxPacking(@Param("palletId") Integer palletId, @Param("estadoId") Integer estadoId, @Param("filas") Integer filas);

    @Query(value = "EXEC pa_GetLastBoxPacking :palletId", nativeQuery = true)
    Integer getLastBoxPacking(@Param("palletId") Integer palletId);

    /** Pallets que tienen al menos una caja rechazada (CajaEmpaque.EstadoId = :estadoId). */
    @Query(value = "SELECT p.Id, p.Numero, COUNT(c.Id) AS cajas " +
            "FROM dbo.CajaEmpaque c " +
            "INNER JOIN dbo.Pallet p ON p.Id = c.PalletId " +
            "WHERE c.EstadoId = :estadoId AND c.Activo = 1 " +
            "GROUP BY p.Id, p.Numero " +
            "ORDER BY p.Numero", nativeQuery = true)
    List<Object[]> searchRejectedPallets(@Param("estadoId") Integer estadoId);

    /** Cajas rechazadas (CajaEmpaque.EstadoId = :estadoId) de un pallet, con el conteo de seriales en Calidad. */
    @Query(value = "SELECT c.Id, c.Numero, " +
            "(SELECT COUNT(*) FROM dbo.Calidad q WHERE q.CajaEmpaqueId = c.Id) AS seriales, " +
            "p.Numero AS pallet " +
            "FROM dbo.CajaEmpaque c " +
            "INNER JOIN dbo.Pallet p ON p.Id = c.PalletId " +
            "WHERE c.PalletId = :palletId AND c.EstadoId = :estadoId AND c.Activo = 1 " +
            "ORDER BY c.Numero", nativeQuery = true)
    List<Object[]> searchRejectedBoxes(@Param("palletId") Integer palletId, @Param("estadoId") Integer estadoId);

    /** Seriales de una caja (SP legado pa_SearchBoxEntry): serial, mac, codigo, descripcion, tipologia, SmartCard. */
    @Query(value = "EXEC pa_SearchBoxEntry :cajaEmpaqueId", nativeQuery = true)
    List<Object[]> searchBoxEntry(@Param("cajaEmpaqueId") Integer cajaEmpaqueId);

    /**
     * Cajas de un pallet excluyendo las que están en :estadoId (rechazadas).
     * Replica el conteo de seriales serializados de pa_SearchPacking.
     */
    @Query(value = "SELECT ce.Id, ce.Numero, COUNT(ing.Id) AS seriales " +
            "FROM dbo.CajaEmpaque ce " +
            "LEFT JOIN (SELECT i.Id, i.CajaEmpaqueId FROM dbo.Ingreso i " +
            "           INNER JOIN dbo.CodigoSap cs ON i.CodigoSapId = cs.Id " +
            "           INNER JOIN dbo.Maestro m ON cs.TipoId = m.Id " +
            "           WHERE m.Codigo = 'SERIALIZADOS') ing ON ce.Id = ing.CajaEmpaqueId " +
            "WHERE ce.PalletId = :palletId AND ce.EstadoId <> :estadoId " +
            "GROUP BY ce.Id, ce.Numero " +
            "ORDER BY ce.Id", nativeQuery = true)
    List<Object[]> searchPackingNoRejected(@Param("palletId") Integer palletId, @Param("estadoId") Integer estadoId);

    /** Todas las cajas rechazadas (CajaEmpaque.EstadoId = :estadoId) con pallet y conteo de seriales. */
    @Query(value = "SELECT c.Id, c.Numero, " +
            "(SELECT COUNT(*) FROM dbo.Calidad q WHERE q.CajaEmpaqueId = c.Id) AS seriales, " +
            "p.Numero AS pallet " +
            "FROM dbo.CajaEmpaque c " +
            "INNER JOIN dbo.Pallet p ON p.Id = c.PalletId " +
            "WHERE c.EstadoId = :estadoId AND c.Activo = 1 " +
            "ORDER BY p.Numero, c.Numero", nativeQuery = true)
    List<Object[]> searchAllRejectedBoxes(@Param("estadoId") Integer estadoId);

}
