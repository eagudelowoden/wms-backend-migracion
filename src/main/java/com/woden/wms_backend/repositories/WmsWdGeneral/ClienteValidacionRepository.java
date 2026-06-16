package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteValidacionModel;

@Repository
public interface ClienteValidacionRepository extends JpaRepository<ClienteValidacionModel, Integer> {

    @Query(value = "EXEC dbo.pa_GetValidacionesCliente :clienteId", nativeQuery = true)
    List<Object[]> getValidacionesByClienteId(@Param("clienteId") Integer clienteId);
}
