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
            map.put("forecast", row[1]);
            map.put("fecha", row[2] != null ? row[2].toString() : null);
            map.put("diasHabiles", row[3]);
            map.put("familia", row[4]);
            map.put("forecastIngreso", row[5]);
            map.put("detalle", row[6]);
            map.put("observaciones", row[7]);
            list.add(map);
        }
        return list;
    }

    @Transactional
    public void create(ForecastModel model) {
        forecastRepository.insertSP(
                model.getFecha() != null ? model.getFecha().toString() : null,
                model.getDiasHabiles(),
                model.getFamiliaId(),
                model.getForecast(),
                model.getForecastIngreso(),
                model.getDetalle(),
                model.getObservaciones());
    }

    @Transactional
    public Integer update(ForecastModel model) {
        return forecastRepository.updateSP(
                model.getFecha() != null ? model.getFecha().toString() : null,
                model.getDiasHabiles(),
                model.getFamiliaId(),
                model.getForecast(),
                model.getForecastIngreso(),
                model.getDetalle(),
                model.getObservaciones(),
                model.getId());
    }

    @Transactional
    public void delete(Integer id) {
        forecastRepository.deleteSP(id);
    }
}
