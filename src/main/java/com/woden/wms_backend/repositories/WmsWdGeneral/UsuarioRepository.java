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

    @Query(value = "SELECT CAST(f.id AS integer), CAST(f.identificacion AS bigint), f.nombres, f.apellidos, f.nombreusuario, f.clave, f.\"fechaCreacion\", f.ip, CAST(f.activo AS boolean), f.\"fechaUltimoAcceso\", f.sede, f.correo, f.\"fechaNacimiento\", CAST(f.\"TemaId\" AS integer), f.cargo, f.area FROM general.pa_getmodeluser(:nombreUsuario, :clave) f", nativeQuery = true)
    List<Object[]> getModelUser(@Param("nombreUsuario") String nombreUsuario, @Param("clave") String clave);

    @Query(value = "SELECT f.\"nombreUsuario\" FROM general.pa_getlistuser(:idCliente, :tipoPerfil) f", nativeQuery = true)
    List<String> getListUser(@Param("idCliente") Integer idCliente, @Param("tipoPerfil") String tipoPerfil);

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getiduser(:nombreUsuario) f", nativeQuery = true)
    Integer getIdUser(@Param("nombreUsuario") String nombreUsuario);

    @Query(value = "SELECT NombreUsuario FROM Usuario WHERE Id = :id", nativeQuery = true)
    List<String> getNombreUsuarioById(@Param("id") Integer id);

    @Query(value = "SELECT Clave FROM Usuario WHERE Id = :id", nativeQuery = true)
    List<String> getClaveById(@Param("id") Integer id);

    @Query(value = "SELECT CAST(f.id AS integer), f.nombres, f.apellidos, f.nombreusuario FROM general.pa_searchuser(:usuario) f", nativeQuery = true)
    List<Object[]> searchUser(@Param("usuario") String usuario);

    @Query(value = "SELECT CAST(f.id AS integer), CAST(f.identificacion AS bigint), f.nombreusuario FROM general.pa_searchfilteruser() f", nativeQuery = true)
    List<Object[]> searchFilterUser();

    @Query(value = "SELECT CAST(f.id AS integer), CAST(f.identificacion AS bigint), f.nombres, f.apellidos, f.nombreusuario, CAST(f.activo AS boolean), f.\"fechaNacimiento\", f.correo, f.cargo, f.area, CAST(f.\"MaestroCargoId\" AS integer), CAST(f.\"MaestroAreaId\" AS integer), f.\"activo web\" FROM general.pa_searchedituser(:usuario) f", nativeQuery = true)
    List<Object[]> searchEditUser(@Param("usuario") String usuario);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_updateuser(CAST(:identificacion AS integer), :nombres, :apellidos, :usuario, :clave, CAST(CAST(:fechaNacimiento AS timestamp) AS date), :correo, CAST(:cargoId AS integer), CAST(:areaId AS integer), CAST(:maestroCargoId AS integer), CAST(:maestroAreaId AS integer), :id, CAST(NULL AS integer))", nativeQuery = true)
    void updateUser(@Param("identificacion") Long identificacion, @Param("nombres") String nombres,
                    @Param("apellidos") String apellidos, @Param("usuario") String usuario,
                    @Param("clave") String clave, @Param("fechaNacimiento") String fechaNacimiento,
                    @Param("correo") String correo, @Param("cargoId") Integer cargoId,
                    @Param("areaId") Integer areaId, @Param("maestroCargoId") Integer maestroCargoId,
                    @Param("maestroAreaId") Integer maestroAreaId, @Param("id") Integer id);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_insertuser(CAST(:identificacion AS bigint), :nombres, :apellidos, :usuario, :clave, :fechaNacimiento, :correo, CAST(:cargoId AS integer), CAST(:areaId AS integer), CAST(:maestroCargoId AS integer), CAST(:maestroAreaId AS integer))", nativeQuery = true)
    void insertUser(@Param("identificacion") Long identificacion, @Param("nombres") String nombres,
                    @Param("apellidos") String apellidos, @Param("usuario") String usuario,
                    @Param("clave") String clave, @Param("fechaNacimiento") String fechaNacimiento,
                    @Param("correo") String correo, @Param("cargoId") Integer cargoId,
                    @Param("areaId") Integer areaId, @Param("maestroCargoId") Integer maestroCargoId,
                    @Param("maestroAreaId") Integer maestroAreaId);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_innactivateuser(:id, :estado)", nativeQuery = true)
    void innactivateUser(@Param("id") int id, @Param("estado") int estado);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_deleteuser(:id)", nativeQuery = true)
    void deleteUser(@Param("id") int id);

    @Query(value = "SELECT f.nombre FROM general.pa_getlistposition() f", nativeQuery = true)
    List<String> getListPosition();

    @Query(value = "SELECT f.nombre FROM general.pa_getlistarea() f", nativeQuery = true)
    List<String> getListArea();

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getidposition(:nombre) f", nativeQuery = true)
    List<Integer> getIdPosition(@Param("nombre") String nombre);

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getidarea(:nombre) f", nativeQuery = true)
    List<Integer> getIdArea(@Param("nombre") String nombre);

    @Query(value = "SELECT c.dbase, c.id, c.nombre FROM UsuarioCliente uc INNER JOIN Cliente c ON uc.clienteId = c.id WHERE uc.usuarioId = :usuarioId", nativeQuery = true)
    List<Object[]> getDbsByUsuarioId(@Param("usuarioId") int usuarioId);

    @Query(value = "SELECT id, CASE WHEN clave_hash IS NOT NULL THEN 1 ELSE 0 END FROM Usuario", nativeQuery = true)
    List<Object[]> getAllWebAccess();

    @Modifying
    @Transactional
    @Query(value = "UPDATE Usuario SET clave_hash = NULL WHERE id = :id", nativeQuery = true)
    void deactivateWebAccess(@Param("id") int id);

    @Query(value = "SELECT clave_hash FROM Usuario WHERE id = :id", nativeQuery = true)
    List<String> getClaveHashById(@Param("id") int id);

    @Query(value = "SELECT u.id, u.nombres || ' ' || u.apellidos AS nombres, u.nombreUsuario, COALESCE(ucp.perfilId, -1) AS perfilId FROM Usuario u LEFT JOIN UsuarioCliente uc ON uc.usuarioId = u.id AND uc.clienteId = :clienteId LEFT JOIN UsuarioClientePerfil ucp ON ucp.usuarioClienteId = uc.id WHERE u.id = :usuarioId", nativeQuery = true)
    List<Object[]> getProfileForClient(@Param("usuarioId") int usuarioId, @Param("clienteId") int clienteId);
}
