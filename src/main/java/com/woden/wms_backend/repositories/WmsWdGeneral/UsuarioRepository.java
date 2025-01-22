package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {
    Optional<UsuarioModel> findByNombreUsuario(String nombreUsuario);
    List<UsuarioModel> findByActivoTrue();
}
