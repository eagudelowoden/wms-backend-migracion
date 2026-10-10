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
    @Query(value = "SELECT 1 FROM pa_insertclasification(CAST(:serial AS character varying), CAST(:estadoEnviado AS character varying), CAST(:usuarioId AS integer), CAST(:fecha AS timestamp without time zone), CAST(:usuarioAsignadoId AS integer))", nativeQuery = true)
    public void insertClasificacion(@Param("serial") String serial, @Param("estadoEnviado") String estadoEnviado,
            @Param("usuarioId") Integer usuarioId, @Param("fecha") String fecha,
            @Param("usuarioAsignadoId") Integer usuarioAsignadoId);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_insertclasificationweb(CAST(:serial AS character varying), CAST(:usuarioId AS integer), CAST(:usuarioAsignadoId AS integer))", nativeQuery = true)
    public void insertClasificacionWeb(@Param("serial") String serial, @Param("usuarioId") Integer usuarioId,
            @Param("usuarioAsignadoId") Integer usuarioAsignadoId);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_updateclasificacion(CAST(:estadoEnviado AS character varying), CAST(:nivelId AS integer), CAST(:serial AS character varying))", nativeQuery = true)
    public void updateClasificacion(@Param("serial") String serial, @Param("estadoEnviado") String estadoEnviado,
            @Param("nivelId") Integer nivelId);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_deleteclasificacion(CAST(:serial AS character varying))", nativeQuery = true)
    public void deleteClasificacion(@Param("serial") String serial);

    @Query(value = "SELECT CAST(f.id AS integer), CAST(f.serialid AS integer), f.serial, f.mac, f.codigosap, f.descripcion, f.usuarioasignado, CAST(f.nivelid AS integer), CAST(f.loteid AS integer), f.lote, f.modelo, f.fecha, f.rn FROM pa_getclassifieduser(CAST(:perfil AS character varying), CAST(:usuarioId AS integer)) f", nativeQuery = true)
    public List<Object[]> getClassifiedUser(@Param("perfil") String perfil, @Param("usuarioId") Integer usuarioId);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_saveclasificacion(CAST(:datos AS text))", nativeQuery = true)
    public void save(@Param("datos") String datos);
}
