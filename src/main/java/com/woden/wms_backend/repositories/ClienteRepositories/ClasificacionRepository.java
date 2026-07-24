package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.woden.wms_backend.models.Entity.ClasificacionModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

public interface ClasificacionRepository extends BaseRepository<ClasificacionModel, Integer> {

    @Transactional
    @Modifying
    @Query(value = "EXEC pa_InsertClasification :serial, :estadoEnviado, :usuarioId, :fecha, :usuarioAsignadoId", nativeQuery = true)
    public void insertClasificacion(@Param("serial") String serial, @Param("estadoEnviado") String estadoEnviado,
            @Param("usuarioId") Integer usuarioId, @Param("fecha") String fecha,
            @Param("usuarioAsignadoId") Integer usuarioAsignadoId);

    @Transactional
    @Modifying
    @Query(value = "EXEC pa_InsertClasificationWeb :serial, :usuarioId, :usuarioAsignadoId", nativeQuery = true)
    public void insertClasificacionWeb(@Param("serial") String serial, @Param("usuarioId") Integer usuarioId,
            @Param("usuarioAsignadoId") Integer usuarioAsignadoId);

    @Transactional
    @Modifying
    @Query(value = "EXEC pa_UpdateClasificacion :estadoEnviado, :nivelId, :serial", nativeQuery = true)
    public void updateClasificacion(@Param("serial") String serial, @Param("estadoEnviado") String estadoEnviado,
            @Param("nivelId") Integer nivelId);

    @Transactional
    @Modifying
    @Query(value = "EXEC pa_DeleteClasificacion :serial", nativeQuery = true)
    public void deleteClasificacion(@Param("serial") String serial);

    @Query(value = "EXEC pa_GetClassifiedUser :usuarioId", nativeQuery = true)
    public List<Object[]> getClassifiedUser(@Param("usuarioId") Integer usuarioId);

    @Transactional
    @Modifying
    @Query(value = "EXEC pa_SaveClasificacion :datos", nativeQuery = true)
    public void save(@Param("datos") String datos);
}
