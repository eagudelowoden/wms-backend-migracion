package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PrealertaModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PrealertaRepository extends BaseRepository<PrealertaModel, Integer> {
    @Query(value = "SELECT CAST(f.id AS integer), f.nombre, f.\"tipoOrigen\", f.origen, f.guia, CAST(f.\"tipologiaId\" AS integer) FROM pa_getmodelprealert(CAST(:nombre AS character varying)) f", nativeQuery = true)
    List<Object[]> getPreAlertaById(@Param("nombre") String nombre);

    @Query(value = "SELECT Id, Nombre FROM Prealerta WHERE Estado = :estado", nativeQuery = true)
    List<Object[]> findByEstado(@Param("estado") String estado);

    @Query(value = "SELECT CAST(f.total AS integer) FROM pa_getdifferenceprealert(CAST(:prealertaId AS integer)) f", nativeQuery = true)
    Integer getDifferencePrealerta(@Param("prealertaId") Integer prealertaId);

    @Modifying
    @Query(value = "UPDATE Prealerta SET estado ='COMPLETADA' where id = :prealertaId", nativeQuery = true)
    void updatePrealerta(@Param("prealertaId") Integer prealertaId);
}
