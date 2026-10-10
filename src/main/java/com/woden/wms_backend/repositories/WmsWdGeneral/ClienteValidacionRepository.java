package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteValidacionModel;

@Repository
public interface ClienteValidacionRepository extends JpaRepository<ClienteValidacionModel, Integer> {

    @Query(value = "SELECT f.codigo, CAST(f.activo AS boolean) FROM general.pa_getvalidacionescliente(:clienteId) f", nativeQuery = true)
    List<Object[]> getValidacionesByClienteId(@Param("clienteId") Integer clienteId);

    @Query(value = "SELECT CAST(f.\"Id\" AS integer), f.\"Codigo\", f.\"Descripcion\" FROM general.pa_getvalidaciontiposdisponibles(:clienteId) f", nativeQuery = true)
    List<Object[]> getDisponibles(@Param("clienteId") Integer clienteId);

    @Query(value = "SELECT CAST(f.\"Id\" AS integer), CAST(f.\"ValidacionTipoId\" AS integer), f.\"Codigo\", f.\"Descripcion\", CAST(f.\"Activo\" AS boolean) FROM general.pa_getvalidacionesasignadas(:clienteId) f", nativeQuery = true)
    List<Object[]> getAsignadas(@Param("clienteId") Integer clienteId);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM ClienteValidacion cv JOIN ClienteValidacionTipo cvt ON cv.ValidacionTipoId = cvt.Id WHERE cv.ClienteId = :clienteId AND cvt.Codigo = :codigo AND cv.Activo = true", nativeQuery = true)
    Integer tieneValidacion(@Param("clienteId") Integer clienteId, @Param("codigo") String codigo);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_insertclientevalidacion(:clienteId, :validacionTipoId)", nativeQuery = true)
    void insertValidacion(@Param("clienteId") Integer clienteId, @Param("validacionTipoId") Integer validacionTipoId);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_deleteclientevalidacion(:clienteId, :validacionTipoId)", nativeQuery = true)
    void deleteValidacion(@Param("clienteId") Integer clienteId, @Param("validacionTipoId") Integer validacionTipoId);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_toggleclientevalidacion(:clienteId, :validacionTipoId, CAST(:activo AS boolean))", nativeQuery = true)
    void toggleValidacion(@Param("clienteId") Integer clienteId, @Param("validacionTipoId") Integer validacionTipoId, @Param("activo") Boolean activo);
}
