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
            map.put("segmento", row[2]);
            map.put("usuarioActivo", row[3]);
            map.put("altas", row[4]);
            map.put("bajas", row[5]);
            map.put("costo", row[6]);
            map.put("reclamo", row[7]);
            map.put("tipo", row[8]);
            map.put("detalle", row[9]);
            map.put("observaciones", row[10]);
            map.put("manoObraJson", row[11]);
            map.put("activo", row[12]);
            map.put("usuario", row[13]);
            map.put("createdAt", row[14] != null ? row[14].toString() : null);
            map.put("updatedAt", row[15] != null ? row[15].toString() : null);
            list.add(map);
        }
        return list;
    }

    @Transactional
    public void create(ManoObraModel model) {
        manoObraRepository.insertSP(
                model.getFecha() != null ? model.getFecha().toString() : null,
                model.getSegmentoId(),
                model.getUsuarioActivo(),
                model.getAltas(),
                model.getBajas(),
                model.getCosto(),
                model.getReclamo(),
                model.getTipo(),
                model.getDetalle(),
                model.getObservaciones(),
                model.getManoObraJson(),
                model.getActivo(),
                model.getUsuario());
    }

    @Transactional
    public Integer update(ManoObraModel model) {
        return manoObraRepository.updateSP(
                model.getFecha() != null ? model.getFecha().toString() : null,
                model.getSegmentoId(),
                model.getUsuarioActivo(),
                model.getAltas(),
                model.getBajas(),
                model.getCosto(),
                model.getReclamo(),
                model.getTipo(),
                model.getDetalle(),
                model.getObservaciones(),
                model.getId(),
                model.getManoObraJson(),
                model.getUsuario());
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
