package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.UsuarioTipoOrigenModel;

@Repository
public interface UsuarioTipoOrigenRepository extends JpaRepository<UsuarioTipoOrigenModel, Integer> {
    
    @Query(value = "EXEC pa_GetListUsuarioTipoOrigen :usuarioId, :clienteId", nativeQuery = true)
    List<Object[]> getListAssigned(@Param("usuarioId") int usuarioId, @Param("clienteId") int clienteId);
}
