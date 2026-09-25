package com.woden.wms_backend.repositories.WmsWdGeneral;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;

@Repository
public interface ClienteDataAccessRepository extends JpaRepository<ClienteModel, Integer> {

  @Query(value = "SELECT tipoOrigenUsuarioON FROM Cliente WHERE Id = :id", nativeQuery = true)
  Boolean getTipoOrigenValue(@Param("id") int id);

  @Query(value = "SELECT BaseIngresoON FROM Cliente WHERE Id = :id ", nativeQuery = true)
  Boolean getBaseIngresoON(@Param("id") int id);

  @Query(value = "SELECT BaseNoDisponibleON FROM Cliente WHERE Id = :id", nativeQuery = true)
  Boolean getBaseNoDisponibleON(@Param("id") int id);

  @Query(value = "SELECT calidadON FROM Cliente WHERE Id = :id", nativeQuery = true)
  Integer getCalidadON(@Param("id") int Id);

  @Query(value = "SELECT nivelClasificacionON FROM Cliente WHERE Id = :id", nativeQuery = true)
  Boolean getNivelClasificacionValue(@Param("id") int id);

  @Query(value = "SELECT odooPqrsON FROM Cliente WHERE Id = :id", nativeQuery = true)
  Boolean getOdooPqrsON(@Param("id") int id);

  @Query(value = "SELECT smartCardInfoON FROM Cliente WHERE Id = :id", nativeQuery = true)
  Boolean getsmartCardInfoON(@Param("id") int id);

  @Modifying
  @Transactional
  @Query(value = "UPDATE Cliente SET BaseEmpaqueON = :baseEmpaqueON WHERE Id = :id", nativeQuery = true)
  int updateBaseEmpaqueON(@Param("baseEmpaqueON") int baseEmpaqueON, @Param("id") int id);

  @Query(value = "SELECT CASE WHEN BaseEmpaqueON = 1 THEN 1 ELSE 0 END FROM Cliente WHERE Id = :id", nativeQuery = true)
  Integer getBaseEmpaqueON(@Param("id") int id);

  @Query(value = "SELECT etiquetaUnitariaON FROM Cliente WHERE Id = :id", nativeQuery = true)
  Boolean getEtiquetaUnitariaON(@Param("id") int id);

  @Query(value = "SELECT CASE WHEN serialMasterCalidadON = 1 THEN 1 ELSE 0 END FROM Cliente WHERE Id = :id", nativeQuery = true)
  Integer getSerialMasterCalidadON(@Param("id") int id);

  @Query(value = "SELECT CAST(serialMasterCalidadON AS INT) FROM Cliente WHERE Id = :id", nativeQuery = true)
  Integer getSerialMasterON(@Param("id") int id);
}