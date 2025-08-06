package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface MaestroRepository extends BaseRepository<MaestroModel, Integer> {
  List<MaestroModel> findByTipoMaestroId(int tipoMaestroId);

  @Query(value = "EXEC pa_GetTipologyMaster :desc1, :desc2, :desc3, :desc4", nativeQuery = true)
  List<Object[]> getTipologias(
      @Param("desc1") String desc1,
      @Param("desc2") String desc2,
      @Param("desc3") String desc3,
      @Param("desc4") String desc4);

  @Query(value = "EXEC pa_GetIdMaster :codigo, :tipo", nativeQuery = true)
  List<Integer> getIdMaster(@Param("codigo") String codigo, @Param("tipo") String tipo);

  @Query(value = "EXEC pa_GetListMaster :tipo", nativeQuery = true)
  List<String> getListByTipo(@Param("tipo") String tipo);

  @Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
  Integer getIdByCodigo(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

  @Query(value = "EXEC pa_GetOriginsMaster :tipo", nativeQuery = true)
  List<String> getOrigenes(@Param("tipo") String tipo);

  @Query(value = "EXEC pa_GetFamilyNumberPallet :codigoSap", nativeQuery = true)
  int getFamilyNumberPallet(@Param("codigoSap") String codigoSap);

  @Query(value = "EXEC pa_AddCountPalletFamily :value, :familyId :filas OUT", nativeQuery = true)
  int addCountPalletFamily(@Param("value") String value, @Param("familyId") int familyId,
      @Param("filas") Integer filas);

  @Query(value = "EXEC pa_GetModelMaster :codigoSap", nativeQuery = true)
  List<Object[]> getModelMaster(@Param("codigoSap") String codigoSap);

  @Query(value = "EXEC pa_GetLevelsClasification", nativeQuery = true)
  List<Object[]> getLevelsClasification();

  @Query(value = "EXEC pa_GetListFailuresMaster :nombre", nativeQuery = true)
  List<Object[]> getFallas(@Param("nombre") String nombre);

  @Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
  Integer getIdSerial(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

  @Query(value = "SELECT id FROM Maestro WHERE codigo = :codigo AND TipoMaestroId = :tipoMaestroId", nativeQuery = true)
  Integer getIdNoSerial(@Param("codigo") String codigo, @Param("tipoMaestroId") Integer tipoMaestroId);

}
