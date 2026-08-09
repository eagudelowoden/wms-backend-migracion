package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.EstadoModel;

@Repository
public interface EstadoRepository extends JpaRepository<EstadoModel, Integer> {

    @Query(value = "EXEC pa_GetIdState :nombre", nativeQuery = true)
    Integer getIdByNombre(@Param("nombre") String nombre);

    @Query(value = "EXEC pa_GetNameState :id", nativeQuery = true)
    String getNameById(@Param("id") int id);

    @Query(value = "EXEC pa_GetListModule", nativeQuery = true)
    List<String> searchModulos();

    @Query(value = "EXEC pa_SearchState", nativeQuery = true)
    List<Object[]> searchEstados();

    @Query(value = "EXEC pa_GetListState :moduloNombre", nativeQuery = true)
    List<String> searchEstadosByModulo(@Param("moduloNombre") String moduloNombre);

    @Query(value = "EXEC pa_GetListDescriptionPermit :moduloNombre", nativeQuery = true)
    List<String> getDescripciones(@Param("moduloNombre") String moduloNombre);

    @Query(value = "EXEC pa_GetListPermit :moduloNombre, :descripcion", nativeQuery = true)
    List<String> getOpciones(@Param("moduloNombre") String moduloNombre, @Param("descripcion") String descripcion);

    @Query(value = "EXEC pa_GetIdPermit :modulo, :nombre", nativeQuery = true)
    Integer getPermisoId(@Param("modulo") String modulo, @Param("nombre") String nombre);

    @Query(value = "EXEC pa_GetIdModule :nombre", nativeQuery = true)
    Integer getModuloId(@Param("nombre") String nombre);
}