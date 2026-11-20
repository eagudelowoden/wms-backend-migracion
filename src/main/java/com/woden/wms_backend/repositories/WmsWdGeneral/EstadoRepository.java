package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.EstadoModel;

@Repository
public interface EstadoRepository extends JpaRepository<EstadoModel, Integer> {

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getidstate(:nombre) f", nativeQuery = true)
    Integer getIdByNombre(@Param("nombre") String nombre);

    @Query(value = "SELECT f.nombre FROM general.pa_getnamestate(:id) f", nativeQuery = true)
    String getNameById(@Param("id") int id);

    @Query(value = "SELECT f.nombre FROM general.pa_getlistmodule() f", nativeQuery = true)
    List<String> searchModulos();

    @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchstate() f", nativeQuery = true)
    List<Object[]> searchEstados();

    @Query(value = "SELECT f.nombre FROM general.pa_getliststate(:moduloNombre) f", nativeQuery = true)
    List<String> searchEstadosByModulo(@Param("moduloNombre") String moduloNombre);

    @Query(value = "SELECT f.descripcion FROM general.pa_getlistdescriptionpermit(:moduloNombre) f", nativeQuery = true)
    List<String> getDescripciones(@Param("moduloNombre") String moduloNombre);

    @Query(value = "SELECT f.nombre FROM general.pa_getlistpermit(:moduloNombre, :descripcion) f", nativeQuery = true)
    List<String> getOpciones(@Param("moduloNombre") String moduloNombre, @Param("descripcion") String descripcion);

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getidpermit(:modulo, :nombre) f", nativeQuery = true)
    Integer getPermisoId(@Param("modulo") String modulo, @Param("nombre") String nombre);

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getidmodule(:nombre) f", nativeQuery = true)
    Integer getModuloId(@Param("nombre") String nombre);
}