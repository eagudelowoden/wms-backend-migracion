package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.stream.Collectors;

import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.models.projections.PalletRowProjection;
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


    public List<PalletDTO> getPalletBusqueda(String numero) {
        String filtro = (numero == null || numero.equalsIgnoreCase("null")) ? "" : numero;
        List<PalletRowProjection> resultados = posicionRepository.searchPalletDetails(filtro);
        return resultados.stream().map(res -> {
            PalletDTO dto = new PalletDTO();
            dto.setId(res.getId());
            dto.setNumero(res.getNumero());
            dto.setCantidad(res.getCantidad());
            dto.setCodigoSap(res.getCodigoSap());
            dto.setDescripcion(res.getDescripcion());
            return dto;
        }).collect(Collectors.toList());
    }
}
