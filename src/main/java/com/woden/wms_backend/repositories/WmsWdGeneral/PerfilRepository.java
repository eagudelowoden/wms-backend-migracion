package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.PerfilModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PerfilRepository extends BaseRepository<PerfilModel, Integer> {

  @Query(value = "EXEC pa_GetNameProfile :usuarioClienteId", nativeQuery = true)
  String getNameProfile(@Param("usuarioClienteId") Integer usuarioClienteId);

  @Query(value = "EXEC pa_SearchProfile :nombre, :cliente", nativeQuery = true)
  List<Object[]> searchProfile(@Param("nombre") String nombre, @Param("cliente") String cliente);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertProfile :nombre, :clienteId", nativeQuery = true)
  void insertProfile(@Param("nombre") String nombre, @Param("clienteId") int clienteId);

  @Transactional
  @Query(value = "DECLARE @f int; EXEC pa_UpdateProfile :nombre, :id, :clienteId, @f OUTPUT; SELECT @f", nativeQuery = true)
  Integer updateProfile(@Param("nombre") String nombre, @Param("id") int id, @Param("clienteId") int clienteId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteProfile :id", nativeQuery = true)
  void deleteProfile(@Param("id") int id);

  @Query(value = "EXEC pa_SearchAvailableSection :perfilId", nativeQuery = true)
  List<Object[]> searchAvailableSection(@Param("perfilId") int perfilId);

  @Query(value = "EXEC pa_SearchAggregatesSection :perfilId", nativeQuery = true)
  List<Object[]> searchAggregatesSection(@Param("perfilId") int perfilId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertProfileSection :perfilId, :seccionId", nativeQuery = true)
  void insertProfileSection(@Param("perfilId") int perfilId, @Param("seccionId") int seccionId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteProfileSection :perfilId, :seccionId", nativeQuery = true)
  void deleteProfileSection(@Param("perfilId") int perfilId, @Param("seccionId") int seccionId);

  @Query(value = "EXEC pa_SearchAvailableModule :perfilId, :seccionId", nativeQuery = true)
  List<Object[]> searchAvailableModule(@Param("perfilId") int perfilId, @Param("seccionId") int seccionId);

  @Query(value = "EXEC pa_SearchAggregateModule :perfilId, :seccionId", nativeQuery = true)
  List<Object[]> searchAggregatesModule(@Param("perfilId") int perfilId, @Param("seccionId") int seccionId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertProfileModule :perfilId, :moduloId", nativeQuery = true)
  void insertProfileModule(@Param("perfilId") int perfilId, @Param("moduloId") int moduloId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteProfileModule :perfilId, :moduloId", nativeQuery = true)
  void deleteProfileModule(@Param("perfilId") int perfilId, @Param("moduloId") int moduloId);

  @Query(value = "EXEC pa_SearchAvailablePermit :moduloId, :perfilId", nativeQuery = true)
  List<Object[]> searchAvailablePermit(@Param("moduloId") int moduloId, @Param("perfilId") int perfilId);

  @Query(value = "EXEC pa_SearchAggregatesPermit :perfilId, :moduloId", nativeQuery = true)
  List<Object[]> searchAggregatesPermit(@Param("perfilId") int perfilId, @Param("moduloId") int moduloId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertProfilePermit :perfilId, :permisoId", nativeQuery = true)
  void insertProfilePermit(@Param("perfilId") int perfilId, @Param("permisoId") int permisoId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteProfilePermit :perfilId, :permisoId", nativeQuery = true)
  void deleteProfilePermit(@Param("perfilId") int perfilId, @Param("permisoId") int permisoId);

  @Query(value = "EXEC pa_SearchAvailableProfileType :perfilId", nativeQuery = true)
  List<Object[]> searchAvailableProfileType(@Param("perfilId") int perfilId);

  @Query(value = "EXEC pa_SearchAggregatesProfileType :perfilId", nativeQuery = true)
  List<Object[]> searchAggregatesProfileType(@Param("perfilId") int perfilId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertPerfilTipoPerfil :perfilId, :tipoPerfilId", nativeQuery = true)
  void insertPerfilTipoPerfil(@Param("perfilId") int perfilId, @Param("tipoPerfilId") int tipoPerfilId);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeletePerfilTipoPerfil :perfilId, :tipoPerfilId", nativeQuery = true)
  void deletePerfilTipoPerfil(@Param("perfilId") int perfilId, @Param("tipoPerfilId") int tipoPerfilId);

  @Query(value = "EXEC pa_GetListProfileType", nativeQuery = true)
  List<Object[]> listTipoPerfil();
}
