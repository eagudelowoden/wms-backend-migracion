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

  @Query(value = "EXEC pa_SearchForecast :forecast", nativeQuery = true)
  List<Object[]> searchSP(@Param("forecast") String forecast);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertForecast :lineaNegocio, :fecha, :fechaEntrega, :forecastUnd, :jornada, :diasHabiles, :observaciones, :usuario, :familiasJson", nativeQuery = true)
  void insertSP(@Param("lineaNegocio") String lineaNegocio, @Param("fecha") String fecha,
      @Param("fechaEntrega") String fechaEntrega,
      @Param("forecastUnd") String forecastUnd, @Param("jornada") String jornada,
      @Param("diasHabiles") Integer diasHabiles,
      @Param("observaciones") String observaciones, @Param("usuario") String usuario,
      @Param("familiasJson") String familiasJson);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateForecast :id, :lineaNegocio, :fecha, :fechaEntrega, :forecastUnd, :jornada, :diasHabiles, :observaciones, :usuario, :familiasJson, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
  Integer updateSP(@Param("id") Integer id, @Param("lineaNegocio") String lineaNegocio,
      @Param("fecha") String fecha, @Param("fechaEntrega") String fechaEntrega,
      @Param("forecastUnd") String forecastUnd,
      @Param("jornada") String jornada, @Param("diasHabiles") Integer diasHabiles,
      @Param("observaciones") String observaciones,
      @Param("usuario") String usuario, @Param("familiasJson") String familiasJson);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteForecast :id", nativeQuery = true)
  void deleteSP(@Param("id") Integer id);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_ToggleForecastActivo :id, :estado, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
  Integer toggleSP(@Param("id") Integer id, @Param("estado") Integer estado);
}
