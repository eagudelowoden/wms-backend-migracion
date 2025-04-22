package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.LoteDTO;
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
        List<Object[]> results = loteRepository.getLotes();
        return results.stream().map(obj -> {
            LoteDTO lote = new LoteDTO();
            lote.setNombreLote((String) obj[0]);
            return lote;
        }).collect(Collectors.toList());        
    }

}
