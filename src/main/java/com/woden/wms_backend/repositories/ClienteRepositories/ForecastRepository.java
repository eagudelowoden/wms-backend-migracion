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
  @Query(value = "EXEC pa_InsertForecast :fecha, :diasHabiles, :familiaId, :forecast, :forecastIngreso, :detalle, :observaciones", nativeQuery = true)
  void insertSP(@Param("fecha") String fecha, @Param("diasHabiles") String diasHabiles,
      @Param("familiaId") Integer familiaId, @Param("forecast") String forecast,
      @Param("forecastIngreso") String forecastIngreso, @Param("detalle") String detalle,
      @Param("observaciones") String observaciones);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateForecast :fecha, :diasHabiles, :familiaId, :forecast, :forecastIngreso, :detalle, :observaciones, :id, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
  Integer updateSP(@Param("fecha") String fecha, @Param("diasHabiles") String diasHabiles,
      @Param("familiaId") Integer familiaId, @Param("forecast") String forecast,
      @Param("forecastIngreso") String forecastIngreso, @Param("detalle") String detalle,
      @Param("observaciones") String observaciones, @Param("id") Integer id);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteForecast :id", nativeQuery = true)
  void deleteSP(@Param("id") Integer id);
}
