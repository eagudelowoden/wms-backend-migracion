package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface CodigoSapPermitidoRepository extends BaseRepository<CodigoSapModel, Integer> {

  /**
   * Pool de codigos SAP que pueden asignarse como permitidos del padre, con la marca de los ya asignados.
   * (Reemplaza pa_GetListCodigoSapPermitido @Asignado = 0: consulta directa, sin procedimiento.)
   */
  @Query(value = "SELECT CAST(cs.id AS integer), cs.codigo, cs.descripcion, "
      + "CAST(EXISTS (SELECT 1 FROM CodigoSapPermitido p WHERE p.CodigoSapId = :codigoSapId AND p.PermitidoId = cs.id) AS boolean) "
      + "FROM CodigoSap cs WHERE cs.id <> :codigoSapId AND cs.activo = true ORDER BY cs.codigo", nativeQuery = true)
  List<Object[]> getPool(@Param("codigoSapId") Integer codigoSapId);

  /** Codigos SAP ya permitidos para el padre (reemplaza pa_GetListCodigoSapPermitido @Asignado = 1). */
  @Query(value = "SELECT CAST(cs.id AS integer), cs.codigo, cs.descripcion "
      + "FROM CodigoSapPermitido p INNER JOIN CodigoSap cs ON cs.id = p.PermitidoId "
      + "WHERE p.CodigoSapId = :codigoSapId ORDER BY cs.codigo", nativeQuery = true)
  List<Object[]> getPermitidos(@Param("codigoSapId") Integer codigoSapId);
}
