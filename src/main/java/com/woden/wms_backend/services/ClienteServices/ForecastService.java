package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.ForecastModel;
import com.woden.wms_backend.repositories.ClienteRepositories.ForecastRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class ForecastService extends BaseService<ForecastModel, Integer> {
    private final ForecastRepository forecastRepository;

    public ForecastService(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    public List<Map<String, Object>> search(String forecast) {
        List<Object[]> results = forecastRepository.searchSP(forecast);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("lineaNegocio", row[1]);
            map.put("fecha", row[2] != null ? row[2].toString() : null);
            map.put("fechaEntrega", row[3] != null ? row[3].toString() : null);
            map.put("forecastUnd", row[4]);
            map.put("jornada", row[5]);
            map.put("diasHabiles", row[6]);
            map.put("observaciones", row[7]);
            map.put("activo", row[8]);
            map.put("usuario", row[9]);
            map.put("createdAt", row[10] != null ? row[10].toString() : null);
            map.put("updatedAt", row[11] != null ? row[11].toString() : null);
            map.put("familiasJson", row[12]);
            list.add(map);
        }
        return list;
    }

    @Transactional
    public void create(ForecastModel model) {
        forecastRepository.insertSP(
                model.getLineaNegocio(),
                model.getFecha() != null ? model.getFecha().toString() : null,
                model.getFechaEntrega() != null ? model.getFechaEntrega().toString() : null,
                model.getForecastUnd() != null ? model.getForecastUnd().toPlainString() : "0",
                model.getJornada(),
                model.getDiasHabiles(),
                model.getObservaciones(),
                model.getUsuario(),
                model.getFamiliasJson());
    }

    @Transactional
    public Integer update(ForecastModel model) {
        return forecastRepository.updateSP(
                model.getId(),
                model.getLineaNegocio(),
                model.getFecha() != null ? model.getFecha().toString() : null,
                model.getFechaEntrega() != null ? model.getFechaEntrega().toString() : null,
                model.getForecastUnd() != null ? model.getForecastUnd().toPlainString() : "0",
                model.getJornada(),
                model.getDiasHabiles(),
                model.getObservaciones(),
                model.getUsuario(),
                model.getFamiliasJson());
    }

    @Transactional
    public void delete(Integer id) {
        forecastRepository.deleteSP(id);
    }

    @Transactional
    public Integer toggle(Integer id, Integer estado) {
        return forecastRepository.toggleSP(id, estado);
    }
}
