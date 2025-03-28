package com.woden.wms_backend.repositories.WmsWdGeneral;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.EstadoModel;

@Repository
public interface EstadoRepository extends JpaRepository<EstadoModel, Integer> {

    @Query(value = "EXEC pa_GetIdState :nombre", nativeQuery = true)
    Integer getIdByNombre(@Param("nombre") String nombre);
}
