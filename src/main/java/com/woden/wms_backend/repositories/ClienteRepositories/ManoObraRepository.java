package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.ManoObraModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface ManoObraRepository extends BaseRepository<ManoObraModel, Integer> {

    @Query(value = "SELECT CAST(f.id AS integer), f.fecha, f.detalle, f.observaciones, f.mano_obra_json, CAST(f.activo AS boolean), f.usuario, f.created_at, f.updated_at FROM pa_searchmanodeobra() f", nativeQuery = true)
    List<Object[]> searchSP();

    @Transactional
    @Query(value = "SELECT 1 FROM pa_insertmanodeobra(p_fecha => CAST(:fecha AS date), p_detalle => CAST(:detalle AS character varying), p_observaciones => CAST(:observaciones AS character varying), p_mano_obra_json => CAST(:manoObraJson AS text), p_usuario => CAST(:usuario AS character varying))", nativeQuery = true)
    void insertSP(@Param("fecha") String fecha,
                  @Param("detalle") String detalle,
                  @Param("observaciones") String observaciones,
                  @Param("manoObraJson") String manoObraJson,
                  @Param("usuario") String usuario);

    @Transactional
    @Query(value = "SELECT pa_updatemanodeobra(p_fecha => CAST(:fecha AS date), p_detalle => CAST(:detalle AS character varying), p_observaciones => CAST(:observaciones AS character varying), p_mano_obra_json => CAST(:manoObraJson AS text), p_usuario => CAST(:usuario AS character varying), p_id => CAST(:id AS integer), p_filas => CAST(NULL AS integer))", nativeQuery = true)
    Integer updateSP(@Param("fecha") String fecha,
                     @Param("detalle") String detalle,
                     @Param("observaciones") String observaciones,
                     @Param("manoObraJson") String manoObraJson,
                     @Param("usuario") String usuario,
                     @Param("id") Integer id);

    @Transactional
    @Query(value = "SELECT 1 FROM pa_deletemanodeobra(p_id => CAST(:id AS integer))", nativeQuery = true)
    void deleteSP(@Param("id") Integer id);

    @Transactional
    @Query(value = "SELECT pa_togglemanoobraactivo(p_id => CAST(:id AS integer), p_estado => CAST(:estado AS boolean), p_filas => CAST(NULL AS integer))", nativeQuery = true)
    Integer toggleSP(@Param("id") Integer id, @Param("estado") Integer estado);

}
