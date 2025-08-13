package com.woden.wms_backend.repositories.ClienteRepositories;
import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

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

    @Query(value = "EXEC pa_SearchProcessBoxPacking :estado, :numero", nativeQuery = true)
    List<Object[]> SearchProcessBoxPacking(
            @Param("estado") String estado,
            @Param("numero") String numero
    );




}
