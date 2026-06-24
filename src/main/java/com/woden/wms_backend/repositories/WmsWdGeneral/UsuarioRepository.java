package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {
    Optional<UsuarioModel> findByNombreUsuario(String nombreUsuario);

    List<UsuarioModel> findByActivoTrue();

    @Query(value = "EXEC pa_GetModelUser :nombreUsuario, :clave", nativeQuery = true)
    List<Object[]> getModelUser(@Param("nombreUsuario") String nombreUsuario, @Param("clave") String clave);

    @Query(value = "EXEC pa_GetListUser :idCliente, :tipoPerfil", nativeQuery = true)
    List<Object[]> getListUser(@Param("idCliente") Integer idCliente, @Param("tipoPerfil") String tipoPerfil);

    @Query(value = "EXEC pa_GetIdUser :nombreUsuario", nativeQuery = true)
    Integer getIdUser(@Param("nombreUsuario") String nombreUsuario);

    @Query(value = "SELECT NombreUsuario FROM Usuario WHERE Id = :id", nativeQuery = true)
    List<String> getNombreUsuarioById(@Param("id") Integer id);

    @Query(value = "SELECT Clave FROM Usuario WHERE Id = :id", nativeQuery = true)
    List<String> getClaveById(@Param("id") Integer id);

    @Query(value = "EXEC pa_SearchUser :usuario", nativeQuery = true)
    List<Object[]> searchUser(@Param("usuario") String usuario);

    @Query(value = "EXEC pa_SearchFilterUser", nativeQuery = true)
    List<Object[]> searchFilterUser();

    @Query(value = "EXEC pa_SearchEditUser :usuario", nativeQuery = true)
    List<Object[]> searchEditUser(@Param("usuario") String usuario);

    @Modifying
    @Transactional
    @Query(value = "DECLARE @f int; EXEC pa_UpdateUser :identificacion, :nombres, :apellidos, :usuario, :clave, :fechaNacimiento, :correo, :cargoId, :areaId, :id, @f OUTPUT", nativeQuery = true)
    void updateUser(@Param("identificacion") Long identificacion, @Param("nombres") String nombres,
                    @Param("apellidos") String apellidos, @Param("usuario") String usuario,
                    @Param("clave") String clave, @Param("fechaNacimiento") String fechaNacimiento,
                    @Param("correo") String correo, @Param("cargoId") Integer cargoId,
                    @Param("areaId") Integer areaId, @Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InsertUser :identificacion, :nombres, :apellidos, :usuario, :clave, :fechaNacimiento, :correo, :cargoId, :areaId", nativeQuery = true)
    void insertUser(@Param("identificacion") Long identificacion, @Param("nombres") String nombres,
                    @Param("apellidos") String apellidos, @Param("usuario") String usuario,
                    @Param("clave") String clave, @Param("fechaNacimiento") String fechaNacimiento,
                    @Param("correo") String correo, @Param("cargoId") Integer cargoId,
                    @Param("areaId") Integer areaId);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_InnactivateUser :id, :estado", nativeQuery = true)
    void innactivateUser(@Param("id") int id, @Param("estado") int estado);

    @Query(value = "EXEC pa_GetListPosition", nativeQuery = true)
    List<Object[]> getListPosition();

    @Query(value = "EXEC pa_GetListArea", nativeQuery = true)
    List<Object[]> getListArea();

    @Query(value = "EXEC pa_GetIdPosition :nombre", nativeQuery = true)
    List<Object[]> getIdPosition(@Param("nombre") String nombre);

    @Query(value = "EXEC pa_GetIdArea :nombre", nativeQuery = true)
    List<Object[]> getIdArea(@Param("nombre") String nombre);
}
