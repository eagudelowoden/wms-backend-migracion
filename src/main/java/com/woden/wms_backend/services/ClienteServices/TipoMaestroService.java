package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.models.Entity.TipoMaestroModel;
import com.woden.wms_backend.repositories.ClienteRepositories.TipoMaestroRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class TipoMaestroService extends BaseService<TipoMaestroModel, Integer> {

    @Autowired
    private TipoMaestroRepository tipoMaestroRepository;

    public List<Map<String, Object>> search() {
        List<Object[]> results = tipoMaestroRepository.search();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("nombre", row[1]);
            map.put("descripcion", row[2]);
            Object activoObj = row[3];
            map.put("activo", activoObj instanceof Boolean ? (((Boolean) activoObj) ? 1 : 0) : activoObj);
            list.add(map);
        }
        return list;
    }

    @Transactional
    public void create(TipoMaestroModel model) {
        tipoMaestroRepository.insertSP(model.getNombre(), model.getDescripcion());
    }

    @Transactional
    public Integer update(TipoMaestroModel model) {
        Integer result = tipoMaestroRepository.updateSP(model.getNombre(), model.getDescripcion(), model.getId());
        TipoMaestroModel current = getById(model.getId());
        if (current != null && current.getActivo() != model.getActivo()) {
            tipoMaestroRepository.innactivateSP(model.getId(), model.getActivo());
        }
        return result;
    }

    @Transactional
    public void delete(Integer id) {
        Integer count = tipoMaestroRepository.getCount(id);
        if (count != null && count > 0) {
            throw new BusinessRuleException("El Tipo Maestro tiene registros asociados.");
        }
        tipoMaestroRepository.deleteSP(id);
    }

    @Transactional
    public Integer toggle(Integer id, Integer estado) {
        return tipoMaestroRepository.innactivateSP(id, estado);
    }

    public Integer getCount(Integer id) {
        return tipoMaestroRepository.getCount(id);
    }

    public boolean hasMovements(Integer id) {
        Integer count = tipoMaestroRepository.getCount(id);
        return count != null && count > 0;
    }
}
