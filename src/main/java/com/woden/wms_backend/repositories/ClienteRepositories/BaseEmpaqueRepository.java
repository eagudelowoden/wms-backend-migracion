package com.woden.wms_backend.repositories.ClienteRepositories;
import com.woden.wms_backend.models.Entity.BaseEmpaqueModel;
import com.woden.wms_backend.repositories.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BaseEmpaqueRepository extends BaseRepository<BaseEmpaqueModel, Integer>{

    @Query(value = "EXEC pa_GetModelBase :base, :serial", nativeQuery = true)
    List<Object[]> getModel(@Param("base") String base, @Param("serial") String serial);

    @Query(value = "EXEC pa_GetCountBase :base", nativeQuery = true)
    List<Object[]> getCountBase(@Param("base") String base);



}
