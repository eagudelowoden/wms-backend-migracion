package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.ManoObraModel;
import com.woden.wms_backend.repositories.ClienteRepositories.ManoObraRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class ManoObraService extends BaseService<ManoObraModel, Integer> {
    private final ManoObraRepository manoObraRepository;

    public ManoObraService(ManoObraRepository manoObraRepository) {
        this.manoObraRepository = manoObraRepository;
    }

    public List<Map<String, Object>> search() {
        List<Object[]> results = manoObraRepository.searchSP();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("fecha", row[1] != null ? row[1].toString() : null);
            map.put("detalle", row[2]);
            map.put("observaciones", row[3]);
            map.put("manoObraJson", row[4]);
            map.put("activo", row[5]);
            map.put("usuario", row[6]);
            map.put("createdAt", row[7] != null ? row[7].toString() : null);
            map.put("updatedAt", row[8] != null ? row[8].toString() : null);
            list.add(map);
        }
        return list;
    }

    @Transactional
    public void create(ManoObraModel model) {
        manoObraRepository.insertSP(
                model.getFecha() != null ? model.getFecha().toString() : null,
                model.getDetalle(),
                model.getObservaciones(),
                model.getManoObraJson(),
                model.getUsuario());
    }

    @Transactional
    public Integer update(ManoObraModel model) {
        return manoObraRepository.updateSP(
                model.getFecha() != null ? model.getFecha().toString() : null,
                model.getDetalle(),
                model.getObservaciones(),
                model.getManoObraJson(),
                model.getUsuario(),
                model.getId());
    }

    @Transactional
    public void delete(Integer id) {
        manoObraRepository.deleteSP(id);
    }

    @Transactional
    public Integer toggle(Integer id, Integer estado) {
        return manoObraRepository.toggleSP(id, estado);
    }
}
