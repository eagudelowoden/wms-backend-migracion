package com.woden.wms_backend.repositories.ClienteRepositories;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.woden.wms_backend.models.Entity.PresupuestoModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
public interface PresupuestoRepository extends BaseRepository<PresupuestoModel, Integer> {

    @Query(value = "EXEC pa_SearchPresupuesto :presupuesto", nativeQuery = true)
    List<Object[]> search(@Param("presupuesto") String presupuesto);

    @Modifying @Transactional
    @Query(value = "EXEC pa_InsertPresupuesto :fecha, :segmentoId, :nivelId, :unidades, :presupuesto, :reclamo, :diasHabiles, :tipo, :detalle, :observaciones", nativeQuery = true)
    void insert(@Param("fecha") String fecha, @Param("segmentoId") Integer segmentoId,
                @Param("nivelId") Integer nivelId, @Param("unidades") String unidades,
                @Param("presupuesto") String presupuesto, @Param("reclamo") String reclamo,
                @Param("diasHabiles") String diasHabiles, @Param("tipo") String tipo,
                @Param("detalle") String detalle, @Param("observaciones") String observaciones);

    @Query(value = "DECLARE @Filas INT; EXEC pa_UpdatePresupuesto :fecha, :segmentoId, :nivelId, :unidades, :presupuesto, :reclamo, :diasHabiles, :tipo, :detalle, :observaciones, :id, @Filas OUTPUT; SELECT @Filas;", nativeQuery = true)
    Integer update(@Param("fecha") String fecha, @Param("segmentoId") Integer segmentoId,
                   @Param("nivelId") Integer nivelId, @Param("unidades") String unidades,
                   @Param("presupuesto") String presupuesto, @Param("reclamo") String reclamo,
                   @Param("diasHabiles") String diasHabiles, @Param("tipo") String tipo,
                   @Param("detalle") String detalle, @Param("observaciones") String observaciones,
                   @Param("id") Integer id);

    @Modifying @Transactional
    @Query(value = "EXEC pa_DeletePresupuesto :id", nativeQuery = true)
    void deleteById(@Param("id") Integer id);
}
