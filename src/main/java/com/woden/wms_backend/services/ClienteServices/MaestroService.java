package com.woden.wms_backend.services.ClienteServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.ModeloDTO;
import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class MaestroService extends BaseService<MaestroModel, Integer> {

    private final MaestroRepository maestroRepository;

    public MaestroService(MaestroRepository maestroRepository) {
        this.maestroRepository = maestroRepository;
    }

    public List<MaestroModel> getByTipoMaestroId(int tipoMaestroId) {
        return maestroRepository.findByTipoMaestroId(tipoMaestroId);
    }

    public List<String> obtenerTipologias(String desc1, String desc2, String desc3, String desc4) {
        List<Object[]> result = maestroRepository.getTipologias(desc1, desc2, desc3, desc4);
        return result.stream()
                .map(r -> (String) r[0]) // extrae el código del resultado
                .collect(Collectors.toList());
    }

    public List<Integer> getIdMaster(String codigo, String tipo) {
        return maestroRepository.getIdMaster(codigo, tipo);
    }

    public List<String> getListByTipo(String tipo) {
        return maestroRepository.getListByTipo(tipo);
    }

    public List<String> obtenerOrigenes(String tipo) {
        return maestroRepository.getOrigenes(tipo);
    }

    public int getFamilyNumberPallet(String codigoSap) {
        return maestroRepository.getFamilyNumberPallet(codigoSap);
    }

    public int addCountPalletFamily(int familyId, String value, int filas) {
        return maestroRepository.addCountPalletFamily(value, familyId, filas);
    }

    public List<ModeloDTO> getModelMaster(String codigoSap) {
        List<Object[]> results = maestroRepository.getModelMaster(codigoSap);
        return results.stream().map(obj -> {
            ModeloDTO modelo = new ModeloDTO();
            modelo.setModelo((String) obj[0]);
            return modelo;
        }).collect(Collectors.toList());
    }

    public List<String> getLevelsClasification() {
        List<Object[]> results = maestroRepository.getLevelsClasification();
        return results.stream().map(obj -> (String) obj[0]).collect(Collectors.toList());
    }

    public List<Map<String, String>> getFallas(String nombre) {
        List<Object[]> results = maestroRepository.getFallas(nombre);

        return results.stream().map(obj -> {
            Map<String, String> map = new HashMap<>();
            map.put("codigo", (String) obj[0]);
            map.put("descripcion", (String) obj[1]);
            return map;
        }).collect(Collectors.toList());
    }

    public List<String> getDesctiption(String codigo) {
        List<Object[]> results = maestroRepository.getDesctiption(codigo);
        return results.stream().map(obj -> (String) obj[0]).collect(Collectors.toList());
    }

    public Integer getWarranty() {
        return maestroRepository.getWarranty();
    }

    public Integer searchLevelComponentsAsig(String serial) {
        return maestroRepository.searchLevelComponentsAsig(serial);
    }

    public Integer getLevelById(Integer levelId) {
        return maestroRepository.getLevelById(levelId);
    }

    public Integer getLevelId(String level) {
        return maestroRepository.getLevelId(level);
    }

    public Integer getIdLevelRepairMaster() {
        return maestroRepository.getIdLevelRepairMaster();
    }

    public void insertComponentsAsig(String serial, Integer componenteId) {
        maestroRepository.insertComponentsAsig(serial, componenteId);
    }

    public void deleteComponentsAsig(String serial, Integer componenteId) {
        maestroRepository.deleteComponentsAsig(serial, componenteId);
    }

    public List<Map<String, Object>> searchComponents(String serial, String falla) {
        List<Object[]> results = maestroRepository.searchComponents(serial, falla);
        return results.stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", (Integer) obj[0]);
            map.put("componente", (String) obj[1]);
            map.put("nivel", (String) obj[2]);
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> searchComponentsAsig(String serial, String falla) {
        int filas = 0; // Valor inicial para filas OUT
        List<Object[]> results = maestroRepository.searchComponentsAsig(serial, falla, filas);
        return results.stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", (Integer) obj[0]);
            map.put("componente", (String) obj[1]);
            map.put("nivel", (String) obj[2]);
            return map;
        }).collect(Collectors.toList());
    }

    public Integer getFamilyNumberBox(String codigoSap, String destino) {
        return maestroRepository.getFamilyNumberBox(codigoSap, destino);
    }

    public String getFamilyAcronyms(String codigoSap, String destino) {
        List<String> siglas = maestroRepository.getFamilyAcronyms(codigoSap, destino);
        return siglas.get(0);
    }

    public void addCountBoxFamily(int familyId, String value) {
        Integer filasOut = 4;
        maestroRepository.addCountBoxFamily(familyId, value, filasOut);
    }
}
