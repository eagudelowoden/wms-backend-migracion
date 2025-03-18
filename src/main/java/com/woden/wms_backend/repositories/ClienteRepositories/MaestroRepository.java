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
}
