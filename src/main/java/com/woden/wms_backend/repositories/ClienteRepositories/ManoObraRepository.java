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

    @Query(value = "EXEC pa_SearchManoDeObra", nativeQuery = true)
    List<Object[]> searchSP();

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InsertManoDeObra @Fecha = :fecha, @Detalle = :detalle, @Observaciones = :observaciones, @mano_obra_json = :manoObraJson, @usuario = :usuario", nativeQuery = true)
    void insertSP(@Param("fecha") String fecha,
                  @Param("detalle") String detalle,
                  @Param("observaciones") String observaciones,
                  @Param("manoObraJson") String manoObraJson,
                  @Param("usuario") String usuario);

    @Transactional
    @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateManoDeObra @Fecha = :fecha, @Detalle = :detalle, @Observaciones = :observaciones, @mano_obra_json = :manoObraJson, @usuario = :usuario, @Id = :id, @Filas = @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
    Integer updateSP(@Param("fecha") String fecha,
                     @Param("detalle") String detalle,
                     @Param("observaciones") String observaciones,
                     @Param("manoObraJson") String manoObraJson,
                     @Param("usuario") String usuario,
                     @Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_DeleteManoDeObra @Id = :id", nativeQuery = true)
    void deleteSP(@Param("id") Integer id);

    @Transactional
    @Query(value = "DECLARE @Filas INT; EXEC pa_ToggleManoObraActivo @Id = :id, @estado = :estado, @Filas = @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
    Integer toggleSP(@Param("id") Integer id, @Param("estado") Integer estado);

}
