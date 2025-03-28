package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.UsuarioSysModel;

@Repository
public interface UsuarioSysRepository extends JpaRepository<UsuarioSysModel, Integer> {

  @Query(value = "SELECT id, nombres, nombre_usuario, perfil_id FROM UsuarioSys WHERE id = :id", nativeQuery = true)
  UsuarioSysModel findByIdCustom(@Param("id") int id);
}
