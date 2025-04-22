package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.CodigoSapModelDTO;
import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.repositories.ClienteRepositories.CodigoSapRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class CodigoSapService extends BaseService<CodigoSapModel, Integer> {
    public CodigoSapService(CodigoSapRepository codigoSapRepository) {
    }

    @Autowired
    private CodigoSapRepository codigoSapRepository;
    @Autowired
    private MaestroRepository maestroRepository;

    public List<Map<String, String>> getListDescriptionSapCode() {
        List<Object[]> results = codigoSapRepository.getListDescriptionSapCode();
        List<Map<String, String>> formattedResults = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, String> map = new HashMap<>();
            map.put("codigo", row[0].toString()); // Primera columna
            map.put("descripcion", row[1].toString()); // Segunda columna
            formattedResults.add(map);
        }

        return formattedResults;
    }

    public List<Map<String, String>> getListDescriptionSapCodeSerial(int id) {
        List<Object[]> results = codigoSapRepository.getListDescriptionSapCodeSerial(id);
        List<Map<String, String>> formattedResults = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, String> map = new HashMap<>();
            map.put("codigo", row[0].toString());
            map.put("descripcion", row[1].toString());
            formattedResults.add(map);
        }

        return formattedResults;
    }

    public List<Map<String, String>> getListDescriptionSapCodeNoSerial(int id) {
        List<Object[]> results = codigoSapRepository.getListDescriptionSapCodeNoSerial(id);
        List<Map<String, String>> formattedResults = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, String> map = new HashMap<>();
            map.put("codigo", row[0].toString());
            map.put("descripcion", row[1].toString());
            formattedResults.add(map);
        }

        return formattedResults;
    }

    public int getIdByCodigo(String codigo) {
        return codigoSapRepository.getIdByCodigo(codigo); // usa @Query
    }

    public Integer getIdSerial(String tipo) {
        return maestroRepository.getIdByCodigo(tipo, 12);
    }

    public Integer getIdNoSerial(String tipo) {
        return maestroRepository.getIdByCodigo(tipo, 12);
    }

    public int getIdComboPallet(String codigo, String descripcion) {
        return codigoSapRepository.getIdComboPallet(codigo, descripcion);
    }

    public CodigoSapModelDTO obtenerModeloPorCodigo(String codigo) {
        return codigoSapRepository.getModelByCodigo(codigo);
    }
}
