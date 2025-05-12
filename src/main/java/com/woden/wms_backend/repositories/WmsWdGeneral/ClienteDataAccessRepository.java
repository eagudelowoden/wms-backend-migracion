package com.woden.wms_backend.repositories.WmsWdGeneral;

import org.springframework.data.jpa.repository.JpaRepository;
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
}