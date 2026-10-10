package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.LoteModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface LoteRepository extends BaseRepository<LoteModel, Integer> {

  @Query(value = "SELECT f.nombre FROM pa_getbatchlist() f", nativeQuery = true)
  List<Object[]> getLotes();

  @Query(value = "SELECT CAST(f.id AS integer) FROM pa_getidbatch(CAST(:nombre AS character varying)) f", nativeQuery = true)
  Integer getIdByLote(@Param("nombre") String nombre);

  @Query(value = "SELECT f.nombre FROM pa_getbatchname(CAST(:id AS integer)) f", nativeQuery = true)
  String getBatchName(@Param("id") Integer id);

  @Query(value = "SELECT CAST(f.id AS integer), f.nombre, f.descripcion, CAST(f.activo AS boolean) FROM pa_searchbatch(CAST(:lote AS character varying)) f", nativeQuery = true)
  List<Object[]> searchSP(@Param("lote") String lote);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_insertlote(CAST(:nombre AS character varying), CAST(:descripcion AS character varying))", nativeQuery = true)
  void insertSP(@Param("nombre") String nombre, @Param("descripcion") String descripcion);

  @Transactional
  @Query(value = "SELECT pa_updatelote(CAST(:nombre AS character varying), CAST(:descripcion AS character varying), CAST(:id AS integer), CAST(NULL AS integer))", nativeQuery = true)
  Integer updateSP(@Param("nombre") String nombre, @Param("descripcion") String descripcion, @Param("id") Integer id);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_deletelote(CAST(:id AS integer))", nativeQuery = true)
  void deleteSP(@Param("id") Integer id);

  @Transactional
  @Query(value = "SELECT 1 FROM pa_innactivatelote(CAST(:id AS integer), CAST(:estado AS integer))", nativeQuery = true)
  void innactivateSP(@Param("id") Integer id, @Param("estado") Integer estado);

  @Query(value = """
      SELECT COALESCE(SUM(cnt), 0) FROM (
          SELECT COUNT(*) cnt FROM Pallet WHERE loteId = :id
          UNION ALL SELECT COUNT(*) FROM Ingreso WHERE loteId = :id
          UNION ALL SELECT COUNT(*) FROM Empaque WHERE loteId = :id
          UNION ALL SELECT COUNT(*) FROM Despacho WHERE loteId = :id
          UNION ALL SELECT COUNT(*) FROM Etiquetado WHERE loteId = :id
          UNION ALL SELECT COUNT(*) FROM Ensamble WHERE loteId = :id
          UNION ALL SELECT COUNT(*) FROM PrealertaSerial WHERE loteId = :id
          UNION ALL SELECT COUNT(*) FROM Cosmetica WHERE loteId = :id
          UNION ALL SELECT COUNT(*) FROM Clasificacion WHERE loteId = :id
      ) refs
      """, nativeQuery = true)
  Integer getCount(@Param("id") Integer id);
}
