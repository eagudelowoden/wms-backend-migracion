package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.ForecastModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface ForecastRepository extends BaseRepository<ForecastModel, Integer> {

  @Query(value = "SELECT CAST(f.id AS integer), f.linea_negocio, f.fecha, f.fecha_entrega, f.forecast_und, f.jornada, f.dias_habiles, f.observaciones, CAST(f.activo AS boolean), f.usuario, f.created_at, f.updated_at, f.familias_json FROM pa_searchforecast(CAST(:forecast AS character varying)) f", nativeQuery = true)
  List<Object[]> searchSP(@Param("forecast") String forecast);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertforecast(p_linea_negocio => CAST(:lineaNegocio AS character varying), p_fecha => CAST(:fecha AS date), p_fecha_entrega => CAST(:fechaEntrega AS date), p_forecast_und => CAST(:forecastUnd AS numeric), p_jornada => CAST(:jornada AS character varying), p_dias_habiles => CAST(:diasHabiles AS integer), p_observaciones => CAST(:observaciones AS character varying), p_usuario => CAST(:usuario AS character varying), p_familias_json => CAST(:familiasJson AS text))", nativeQuery = true)
  void insertSP(@Param("lineaNegocio") String lineaNegocio, @Param("fecha") String fecha,
      @Param("fechaEntrega") String fechaEntrega,
      @Param("forecastUnd") String forecastUnd, @Param("jornada") String jornada,
      @Param("diasHabiles") String diasHabiles,
      @Param("observaciones") String observaciones, @Param("usuario") String usuario,
      @Param("familiasJson") String familiasJson);

  @Transactional
  @Query(value = "SELECT pa_updateforecast(p_id => CAST(:id AS integer), p_linea_negocio => CAST(:lineaNegocio AS character varying), p_fecha => CAST(:fecha AS date), p_fecha_entrega => CAST(:fechaEntrega AS date), p_forecast_und => CAST(:forecastUnd AS numeric), p_jornada => CAST(:jornada AS character varying), p_dias_habiles => CAST(:diasHabiles AS integer), p_observaciones => CAST(:observaciones AS character varying), p_usuario => CAST(:usuario AS character varying), p_familias_json => CAST(:familiasJson AS text), p_filas => CAST(NULL AS integer))", nativeQuery = true)
  Integer updateSP(@Param("id") Integer id, @Param("lineaNegocio") String lineaNegocio,
      @Param("fecha") String fecha, @Param("fechaEntrega") String fechaEntrega,
      @Param("forecastUnd") String forecastUnd,
      @Param("jornada") String jornada, @Param("diasHabiles") String diasHabiles,
      @Param("observaciones") String observaciones,
      @Param("usuario") String usuario, @Param("familiasJson") String familiasJson);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deleteforecast(CAST(:id AS integer))", nativeQuery = true)
  void deleteSP(@Param("id") Integer id);

  @Transactional
  @Query(value = "SELECT pa_toggleforecastactivo(CAST(:id AS integer), CAST(:estado AS boolean), CAST(NULL AS integer))", nativeQuery = true)
  Integer toggleSP(@Param("id") Integer id, @Param("estado") Integer estado);
}
