package com.woden.wms_backend.services.ClienteServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.models.projections.PalletRowProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Map<String, Object>> buscarListado(String numero) {
        String filtro = (numero == null) ? "" : numero.trim();
        List<Object[]> rows = posicionRepository.searchAll(filtro);
        return rows.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("numero", row[1]);
            map.put("codigoSap", row[2]);
            map.put("descripcion", row[3]);
            Object activo = row[4];
            map.put("activo", activo instanceof Boolean ? (((Boolean) activo) ? 1 : 0) : activo);
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getListEnsamble() {
        return posicionRepository.listDisponibles().stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("numero", row[1]);
            return map;
        }).collect(Collectors.toList());
    }

    public Integer getCountPosition(Integer id) {
        return posicionRepository.getCountPosition(id);
    }

    @Transactional
    public void crear(String numero, Integer codigoSapId) {
        if (numero == null || numero.trim().isEmpty())
            throw new BusinessRuleException("El número de posición es obligatorio.");
        posicionRepository.insertPosicion(numero.trim(), codigoSapId == null ? 0 : codigoSapId);
    }

    @Transactional
    public void actualizar(Integer id, String numero, Integer codigoSapId) {
        if (numero == null || numero.trim().isEmpty())
            throw new BusinessRuleException("El número de posición es obligatorio.");
        posicionRepository.updatePosicion(id, numero.trim(), codigoSapId == null ? 0 : codigoSapId);
    }

    @Transactional
    public void eliminar(Integer id) {
        Integer cantidad = posicionRepository.getCountPosition(id);
        if (cantidad != null && cantidad > 0)
            throw new BusinessRuleException("La posición está siendo usada por pallets.");
        posicionRepository.deletePosicion(id);
    }

    @Transactional
    public void toggle(Integer id, Integer estado) {
        posicionRepository.innactivatePosicion(id, estado);
    }
}
