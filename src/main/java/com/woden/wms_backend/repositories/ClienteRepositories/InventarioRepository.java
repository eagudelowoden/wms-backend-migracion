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
  @Query(value = "SELECT CAST(f.\"Conteo\" AS integer) FROM pa_getcountentrysap(CAST(:codigoSapId AS integer)) f", nativeQuery = true)
  Integer getCountEntrySap(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "SELECT CAST(f.\"Conteo\" AS integer) FROM pa_getcountinventorysap(CAST(:codigoSapId AS integer)) f", nativeQuery = true)
  Integer getCountInventorySap(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "SELECT CAST(f.\"Faltante\" AS integer) FROM pa_getcountdiffsap(CAST(:codigoSapId AS integer)) f", nativeQuery = true)
  Integer getCountDiffSap(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "SELECT CAST(f.\"Resultado\" AS double precision) FROM pa_getpercentdiffsap(CAST(:codigoSapId AS integer)) f", nativeQuery = true)
  Double getPercentDiffSap(@Param("codigoSapId") Integer codigoSapId);

  @Query(value = "SELECT CAST(f.col1 AS integer) FROM pa_getcountentry() f", nativeQuery = true)
  Integer getCountEntry();

  @Query(value = "SELECT CAST(f.\"Conteo\" AS integer) FROM pa_getcountinventory() f", nativeQuery = true)
  Integer getCountInventory();

  @Query(value = "SELECT CAST(f.\"Faltante\" AS integer) FROM pa_getcountdiff() f", nativeQuery = true)
  Integer getCountDiff();

  @Query(value = "SELECT CAST(f.\"Resultado\" AS double precision) FROM pa_getpercentdiff() f", nativeQuery = true)
  Double getPercentDiff();

  @Query(value = "SELECT CAST(f.col1 AS double precision) FROM pa_getcountsurplus() f", nativeQuery = true)
  Double getCountSurplus();

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM Inventario WHERE Serial = :serial", nativeQuery = true)
  Integer deleteSerialInventory(@Param("serial") String serial);

  @Query(value = "select u.nombreUsuario from Inventario i inner join general.usuario u on i.usuarioId=u.id where i.serial=:serial", nativeQuery = true)
  String getModel(@Param("serial") String serial);

  @Query(value = "SELECT f.\"padreInventario\" FROM pa_getliststateinventory() f", nativeQuery = true)
  List<String> getListStateInventory();

  @Query(value = "SELECT CAST(f.id AS integer), f.serial, f.\"codigoSap\", f.\"codigoSapReal\", f.pallet, f.\"palletReal\", CAST(f.\"estadoId\" AS integer), f.\"estadoSap\", f.\"estadoRR\", f.ajuste, f.fecha, CAST(f.\"usuarioId\" AS integer), CAST(f.\"serialId\" AS integer), f.mac, CAST(f.sobrante AS boolean) FROM pa_getmodelfinishinventory() f", nativeQuery = true)
  List<Object[]> getModelFinishInventory();

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM Inventario ", nativeQuery = true)
  Integer cleanTable();

  @Query(value = "EXEC pa_SearchInventory :pallet", nativeQuery = true)
  List<Object[]> searchInventory(@Param("pallet") String pallet);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO Inventario(serial,codigoSap,codigoSapReal,pallet,PalletReal,estadoId,estadoSap,estadoRR,ajuste,fecha,usuarioId,serialId,mac,sobrante) VALUES (:serial, :codigoSap, :codigoSapReal, :pallet, :palletReal, :estadoId, :estadoSap, :estadoRR, :ajuste, :fecha, :usuarioId, :serialId, :mac, :sobrante)", nativeQuery = true)
  Integer create(@Param("serial") String serial, @Param("codigoSap") String codigoSap,
      @Param("codigoSapReal") String codigoSapReal, @Param("pallet") String pallet,
      @Param("palletReal") String palletReal, @Param("estadoId") Integer estadoId, @Param("estadoSap") String estadoSap,
      @Param("estadoRR") String estadoRR, @Param("ajuste") String ajuste, @Param("fecha") String fecha,
      @Param("usuarioId") Integer usuarioId,
      @Param("serialId") Integer serialId, @Param("mac") String mac, @Param("sobrante") Boolean sobrante);

  @Query(value = "EXEC pa_SearchSettings :pallet", nativeQuery = true)
  List<Object[]> searchSettings(@Param("pallet") String pallet);

}
