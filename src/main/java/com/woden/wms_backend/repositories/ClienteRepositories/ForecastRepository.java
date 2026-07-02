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
  @Query(value = "EXEC pa_InsertForecast :idLinea, :fecha, :fechaEntrega, :forecastUnd, :jornada, :diasHabiles, :estado, :observaciones, :usuario, :familiasJson", nativeQuery = true)
  void insertSP(@Param("idLinea") Integer idLinea, @Param("fecha") String fecha,
      @Param("fechaEntrega") String fechaEntrega,
      @Param("forecastUnd") String forecastUnd, @Param("jornada") String jornada,
      @Param("diasHabiles") Integer diasHabiles, @Param("estado") String estado,
      @Param("observaciones") String observaciones, @Param("usuario") String usuario,
      @Param("familiasJson") String familiasJson);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateForecast :id, :idLinea, :fecha, :fechaEntrega, :forecastUnd, :jornada, :diasHabiles, :estado, :observaciones, :usuario, :familiasJson, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
  Integer updateSP(@Param("id") Integer id, @Param("idLinea") Integer idLinea,
      @Param("fecha") String fecha, @Param("fechaEntrega") String fechaEntrega,
      @Param("forecastUnd") String forecastUnd,
      @Param("jornada") String jornada, @Param("diasHabiles") Integer diasHabiles,
      @Param("estado") String estado, @Param("observaciones") String observaciones,
      @Param("usuario") String usuario, @Param("familiasJson") String familiasJson);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteForecast :id", nativeQuery = true)
  void deleteSP(@Param("id") Integer id);
}
