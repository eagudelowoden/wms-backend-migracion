package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.UsuarioSysModel;

@Repository
public interface UsuarioSysRepository extends JpaRepository<UsuarioSysModel, Integer> {

  @Query(value = "SELECT id, nombres, nombre_usuario, perfil_id FROM UsuarioSys WHERE id = :id", nativeQuery = true)
  UsuarioSysModel findByIdUsuarioSys(@Param("id") int id);

  @Modifying
  @Transactional
  @Query(value = "IF EXISTS (SELECT 1 FROM UsuarioSys WHERE id = :id) " +
         "UPDATE UsuarioSys SET nombres = :nombres, nombre_usuario = :nombreUsuario, perfil_id = :perfilId WHERE id = :id " +
         "ELSE INSERT INTO UsuarioSys (id, nombres, nombre_usuario, perfil_id) VALUES (:id, :nombres, :nombreUsuario, :perfilId)",
         nativeQuery = true)
  void syncUsuarioSys(@Param("id") int id, @Param("nombres") String nombres,
                      @Param("nombreUsuario") String nombreUsuario, @Param("perfilId") int perfilId);
}
