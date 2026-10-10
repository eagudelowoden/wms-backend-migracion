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

  @Query(value = "SELECT f.nombre FROM general.pa_getnameprofile(:usuarioClienteId) f", nativeQuery = true)
  String getNameProfile(@Param("usuarioClienteId") Integer usuarioClienteId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchprofile(:nombre, :cliente) f", nativeQuery = true)
  List<Object[]> searchProfile(@Param("nombre") String nombre, @Param("cliente") String cliente);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_insertprofile(:nombre, :clienteId)", nativeQuery = true)
  void insertProfile(@Param("nombre") String nombre, @Param("clienteId") int clienteId);

  @Transactional
  @Query(value = "SELECT general.pa_updateprofile(:nombre, :id, :clienteId, CAST(NULL AS integer))", nativeQuery = true)
  Integer updateProfile(@Param("nombre") String nombre, @Param("id") int id, @Param("clienteId") int clienteId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_deleteprofile(:id)", nativeQuery = true)
  void deleteProfile(@Param("id") int id);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchavailablesection(:perfilId) f", nativeQuery = true)
  List<Object[]> searchAvailableSection(@Param("perfilId") int perfilId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchaggregatessection(:perfilId) f", nativeQuery = true)
  List<Object[]> searchAggregatesSection(@Param("perfilId") int perfilId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_insertprofilesection(:perfilId, :seccionId)", nativeQuery = true)
  void insertProfileSection(@Param("perfilId") int perfilId, @Param("seccionId") int seccionId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_deleteprofilesection(:perfilId, :seccionId)", nativeQuery = true)
  void deleteProfileSection(@Param("perfilId") int perfilId, @Param("seccionId") int seccionId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchavailablemodule(:perfilId, :seccionId) f", nativeQuery = true)
  List<Object[]> searchAvailableModule(@Param("perfilId") int perfilId, @Param("seccionId") int seccionId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchaggregatemodule(:perfilId, :seccionId) f", nativeQuery = true)
  List<Object[]> searchAggregatesModule(@Param("perfilId") int perfilId, @Param("seccionId") int seccionId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_insertprofilemodule(:perfilId, :moduloId)", nativeQuery = true)
  void insertProfileModule(@Param("perfilId") int perfilId, @Param("moduloId") int moduloId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_deleteprofilemodule(:perfilId, :moduloId)", nativeQuery = true)
  void deleteProfileModule(@Param("perfilId") int perfilId, @Param("moduloId") int moduloId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchavailablepermit(:moduloId, :perfilId) f", nativeQuery = true)
  List<Object[]> searchAvailablePermit(@Param("moduloId") int moduloId, @Param("perfilId") int perfilId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchaggregatespermit(:perfilId, :moduloId) f", nativeQuery = true)
  List<Object[]> searchAggregatesPermit(@Param("perfilId") int perfilId, @Param("moduloId") int moduloId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_insertprofilepermit(:perfilId, :permisoId)", nativeQuery = true)
  void insertProfilePermit(@Param("perfilId") int perfilId, @Param("permisoId") int permisoId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_deleteprofilepermit(:perfilId, :permisoId)", nativeQuery = true)
  void deleteProfilePermit(@Param("perfilId") int perfilId, @Param("permisoId") int permisoId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchavailableprofiletype(:perfilId) f", nativeQuery = true)
  List<Object[]> searchAvailableProfileType(@Param("perfilId") int perfilId);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre FROM general.pa_searchaggregatesprofiletype(:perfilId) f", nativeQuery = true)
  List<Object[]> searchAggregatesProfileType(@Param("perfilId") int perfilId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_insertperfiltipoperfil(:perfilId, :tipoPerfilId)", nativeQuery = true)
  void insertPerfilTipoPerfil(@Param("perfilId") int perfilId, @Param("tipoPerfilId") int tipoPerfilId);

  @Transactional
  @Query(value = "SELECT 1 FROM general.pa_deleteperfiltipoperfil(:perfilId, :tipoPerfilId)", nativeQuery = true)
  void deletePerfilTipoPerfil(@Param("perfilId") int perfilId, @Param("tipoPerfilId") int tipoPerfilId);

  @Query(value = "SELECT CAST(t.id AS integer), t.nombre FROM general.tipoperfil t ORDER BY t.nombre", nativeQuery = true)
  List<Object[]> listTipoPerfil();
}
