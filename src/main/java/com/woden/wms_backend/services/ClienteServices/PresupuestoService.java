package com.woden.wms_backend.services.ClienteServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.woden.wms_backend.models.Entity.PresupuestoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.PresupuestoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PresupuestoService extends BaseService<PresupuestoModel, Integer> {

    private final PresupuestoRepository presupuestoRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository) {
        this.presupuestoRepository = presupuestoRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> search(String presupuesto) {
        List<Object[]> rows = presupuestoRepository.search(presupuesto);
        return rows.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("presupuesto", row[1]);
            map.put("fecha", row[2]);
            map.put("segmento", row[3]);
            map.put("nivel", row[4]);
            map.put("unidades", row[5]);
            map.put("reclamo", row[6]);
            map.put("diasHabiles", row[7]);
            map.put("tipo", row[8]);
            map.put("detalle", row[9]);
            map.put("observaciones", row[10]);
            return map;
        }).toList();
    }

    @Transactional
    public void create(PresupuestoModel model) {
        presupuestoRepository.insert(
            model.getFecha() != null ? model.getFecha().toString() : null,
            model.getSegmentoId(), model.getNivelId(), model.getUnidades(),
            model.getPresupuesto(), model.getReclamo(), model.getDiasHabiles(),
            model.getTipo(), model.getDetalle(), model.getObservaciones());
    }

    @Transactional
    public Integer update(PresupuestoModel model) {
        return presupuestoRepository.update(
            model.getFecha() != null ? model.getFecha().toString() : null,
            model.getSegmentoId(), model.getNivelId(), model.getUnidades(),
            model.getPresupuesto(), model.getReclamo(), model.getDiasHabiles(),
            model.getTipo(), model.getDetalle(), model.getObservaciones(),
            model.getId());
    }

    @Transactional
    public void delete(Integer id) {
        presupuestoRepository.deleteById(id);
    }
}
