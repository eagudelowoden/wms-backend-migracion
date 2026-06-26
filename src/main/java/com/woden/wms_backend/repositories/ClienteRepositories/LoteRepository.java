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

  @Query(value = "EXEC pa_GetBatchList", nativeQuery = true)
  List<Object[]> getLotes();

  @Query(value = "EXEC pa_GetIdBatch :nombre", nativeQuery = true)
  Integer getIdByLote(@Param("nombre") String nombre);

  @Query(value = "EXEC pa_GetBatchName :id", nativeQuery = true)
  String getBatchName(@Param("id") Integer id);

  @Query(value = "EXEC pa_SearchBatch :lote", nativeQuery = true)
  List<Object[]> searchSP(@Param("lote") String lote);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertLote :nombre, :descripcion", nativeQuery = true)
  void insertSP(@Param("nombre") String nombre, @Param("descripcion") String descripcion);

  @Transactional
  @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateLote :nombre, :descripcion, :id, @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
  Integer updateSP(@Param("nombre") String nombre, @Param("descripcion") String descripcion, @Param("id") Integer id);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_DeleteLote :id", nativeQuery = true)
  void deleteSP(@Param("id") Integer id);

  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InnactivateLote :id, :estado", nativeQuery = true)
  void innactivateSP(@Param("id") Integer id, @Param("estado") Integer estado);

  @Query(value = """
      SELECT ISNULL(SUM(cnt), 0) FROM (
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
