package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.ConexionOdooModel;

@Repository
public interface ConexionOdooRepository extends JpaRepository<ConexionOdooModel, Integer> {
  @Query(value = "EXEC pa_GetConexionOdoo :id", nativeQuery = true)
  List<Object[]> getConexionOdoo(@Param("id") int id);
}
