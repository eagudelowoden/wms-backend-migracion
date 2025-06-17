package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.PosicionModel;
import com.woden.wms_backend.repositories.ClienteRepositories.PosicionRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PosicionService extends BaseService<PosicionModel, Integer> {

    @Autowired
    private PosicionRepository posicionRepository;

    public List<String> getList(Integer reservado) {
        if (reservado == 0) {
            return posicionRepository.getListReservado();
        } else {
            return posicionRepository.getListNoReservado();
        }
    }

    public Integer getId(String numero) {
        return posicionRepository.getId(numero);
    }
}
