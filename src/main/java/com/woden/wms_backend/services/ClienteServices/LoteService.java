package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.LoteDTO;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.models.Entity.LoteModel;
import com.woden.wms_backend.repositories.ClienteRepositories.LoteRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class LoteService extends BaseService<LoteModel, Integer> {
    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    public List<LoteDTO> getLotes() {
        List<Object[]> results = loteRepository.searchSP("");
        return results.stream().map(obj -> {
            LoteDTO lote = new LoteDTO();
            lote.setId(obj[0] != null ? ((Number) obj[0]).intValue() : null);
            lote.setNombre((String) obj[1]);
            return lote;
        }).collect(Collectors.toList());
    }

    public Integer getIdByLote(String lote) {
        return loteRepository.getIdByLote(lote);
    }

    public String getBatchName(Integer id) {
        return loteRepository.getBatchName(id);
    }

    public List<Map<String, Object>> search(String lote) {
        List<Object[]> results = loteRepository.searchSP(lote);
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
    public void create(LoteModel model) {
        loteRepository.insertSP(model.getNombre(), model.getDescripcion());
    }

    @Transactional
    public Integer update(LoteModel model) {
        Integer result = loteRepository.updateSP(model.getNombre(), model.getDescripcion(), model.getId());
        LoteModel current = getById(model.getId());
        if (current != null && current.getActivo() != null && model.getActivo() != null
                && !current.getActivo().equals(model.getActivo())) {
            loteRepository.innactivateSP(model.getId(), Boolean.TRUE.equals(model.getActivo()) ? 1 : 0);
        }
        return result;
    }

    @Transactional
    public void delete(Integer id) {
        Integer count = loteRepository.getCount(id);
        if (count != null && count > 0) {
            throw new BusinessRuleException("El Lote tiene registros asociados.");
        }
        loteRepository.deleteSP(id);
    }

    @Transactional
    public void toggle(Integer id, Integer estado) {
        loteRepository.innactivateSP(id, estado);
    }

    public Integer getCount(Integer id) {
        return loteRepository.getCount(id);
    }

    public boolean hasMovements(Integer id) {
        Integer count = loteRepository.getCount(id);
        return count != null && count > 0;
    }

}
