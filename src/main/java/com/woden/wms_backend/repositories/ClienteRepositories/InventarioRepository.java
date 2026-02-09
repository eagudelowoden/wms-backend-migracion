package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.InventarioModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface InventarioRepository extends BaseRepository<InventarioModel, Integer> {
  @Query(value = "EXEC pa_GetCountEntrySap :codigoSapId", nativeQuery = true)
  Integer getCountEntrySap(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "EXEC pa_GetCountInventorySap :codigoSapId", nativeQuery = true)
  Integer getCountInventorySap(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "EXEC pa_GetCountDiffSap :codigoSapId", nativeQuery = true)
  Integer getCountDiffSap(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "EXEC pa_GetPercentDiffSap :codigoSapId", nativeQuery = true)
  Double getPercentDiffSap(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "EXEC pa_GetCountEntry", nativeQuery = true)
  Integer getCountEntry();

  @Query(value = "EXEC pa_GetCountInventory", nativeQuery = true)
  Integer getCountInventory();

  @Query(value = "EXEC pa_GetCountDiff", nativeQuery = true)
  Integer getCountDiff();

  @Query(value = "EXEC pa_GetPercentDiff", nativeQuery = true)
  Double getPercentDiff();
  
  @Query(value = "EXEC pa_GetCountSurplus", nativeQuery = true)
  Double getCountSurplus();

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM Inventario WHERE Serial = :serial", nativeQuery = true)
  Integer deleteSerialInventory(@Param("serial") String serial);

  @Query(value = "select u.nombreUsuario from Inventario i inner join WmsWdGeneral.dbo.usuario u on i.usuarioId=u.id where i.serial=:serial", nativeQuery = true)
  String getModel(@Param("serial") String serial);

  @Query(value = "EXEC pa_GetListStateInventory", nativeQuery = true)
  List<String> getListStateInventory();
}
