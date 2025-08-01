package com.woden.wms_backend.repositories.WmsWdGeneral;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.UsuarioClienteModel;

@Repository
public interface UsuarioClienteRepository extends JpaRepository<UsuarioClienteModel, Integer> {
    
    @Query(value = "EXEC pa_GetIdUserClient :usuarioId, :cliente", nativeQuery = true)
    Integer getIdUserClient(
            @Param("usuarioId") int usuarioId,
            @Param("cliente") String cliente
    );
}
