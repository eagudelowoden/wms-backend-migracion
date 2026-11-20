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

    @Query(value = "SELECT CAST(f.id AS integer), f.presupuesto, f.fecha, f.segmento, f.nivel, f.unidades, f.reclamo, f.\"diasHabiles\", f.tipo, f.detalle, f.observaciones FROM pa_searchpresupuesto(CAST(:presupuesto AS character varying)) f", nativeQuery = true)
    List<Object[]> search(@Param("presupuesto") String presupuesto);

    @Modifying @Transactional
    @Query(value = "SELECT 1 FROM pa_insertpresupuesto(CAST(:fecha AS date), CAST(:segmentoId AS integer), CAST(:nivelId AS integer), CAST(:unidades AS character varying), CAST(:presupuesto AS character varying), CAST(:reclamo AS character varying), CAST(:diasHabiles AS character varying), CAST(:tipo AS character varying), CAST(:detalle AS character varying), CAST(:observaciones AS character varying))", nativeQuery = true)
    void insert(@Param("fecha") String fecha, @Param("segmentoId") Integer segmentoId,
                @Param("nivelId") Integer nivelId, @Param("unidades") String unidades,
                @Param("presupuesto") String presupuesto, @Param("reclamo") String reclamo,
                @Param("diasHabiles") String diasHabiles, @Param("tipo") String tipo,
                @Param("detalle") String detalle, @Param("observaciones") String observaciones);

    @Query(value = "SELECT pa_updatepresupuesto(CAST(:fecha AS date), CAST(:segmentoId AS integer), CAST(:nivelId AS integer), CAST(:unidades AS character varying), CAST(:presupuesto AS character varying), CAST(:reclamo AS character varying), CAST(:diasHabiles AS character varying), CAST(:tipo AS character varying), CAST(:detalle AS character varying), CAST(:observaciones AS character varying), CAST(:id AS integer), CAST(NULL AS integer))", nativeQuery = true)
    Integer update(@Param("fecha") String fecha, @Param("segmentoId") Integer segmentoId,
                   @Param("nivelId") Integer nivelId, @Param("unidades") String unidades,
                   @Param("presupuesto") String presupuesto, @Param("reclamo") String reclamo,
                   @Param("diasHabiles") String diasHabiles, @Param("tipo") String tipo,
                   @Param("detalle") String detalle, @Param("observaciones") String observaciones,
                   @Param("id") Integer id);

    @Modifying @Transactional
    @Query(value = "SELECT 1 FROM pa_deletepresupuesto(CAST(:id AS integer))", nativeQuery = true)
    void deleteById(@Param("id") Integer id);
}
