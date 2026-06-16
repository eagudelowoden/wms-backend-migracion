package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.models.projections.PalletRowProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PosicionModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PosicionRepository extends BaseRepository<PosicionModel, Integer> {
  @Query(value = "select numero from Posicion where codigoSapId !=0 AND Activo = 1 order by numero", nativeQuery = true)
  List<String> getListNoReservado();

  @Query(value = "select numero from Posicion where codigoSapId = 0 AND Activo = 1 order by numero", nativeQuery = true)
  List<String> getListReservado();

  @Query(value = "select id from Posicion where numero= :numero", nativeQuery = true)
  Integer getId(@Param("numero") String numero);

  @Query(value = "SELECT id FROM Posicion WHERE numero = :numero", nativeQuery = true)
  Integer getIdByNumero(@Param("numero") String numero);

  @Query(value = "SELECT p.id, " +
          "       p.numero, " +
          "       COUNT(i.serial) as cantidad, " +
          "       cs.codigo as codigoSap, " +
          "       cs.descripcion " +
          "FROM Pallet p " +
          "INNER JOIN Ingreso i ON p.id = i.palletId " +
          "INNER JOIN Posicion po ON p.numero = po.numero " +
          "INNER JOIN CodigoSap cs ON po.codigoSapid = cs.id " +
          "WHERE po.codigoSapId != 0 " +
          "AND (:numero = '' OR p.numero = :numero) " +
          "GROUP BY p.id, p.numero, cs.codigo, cs.descripcion",
          nativeQuery = true)
  List<PalletRowProjection> searchPalletDetails(@Param("numero") String numero);
}
