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
import com.woden.wms_backend.util.TypeMapper;

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
        List<Object[]> results = codigoSapRepository.getModelByCodigo(codigo);

        if (results.isEmpty()) {
            return null;
        }

        Object[] row = results.get(0);
        // for (int i = 0; i < row.length; i++) {
        //     Object value = row[i];
        //     System.out.println("Posición " + i + ": tipo=" + (value != null ? value.getClass().getName() : "null")
        //             + ", valor=" + value);
        // }
        CodigoSapModelDTO codigoSap = new CodigoSapModelDTO();

        codigoSap.setId((Integer) row[0]);
        codigoSap.setCodigo((String) row[1]);
        codigoSap.setDescripcion((String) row[2]);
        codigoSap.setFamiliaId((Integer) row[3]);
        codigoSap.setFamilia((String) row[4]);
        codigoSap.setTipoId((Integer) row[5]);
        codigoSap.setTipo((String) row[6]);
        codigoSap.setValidacion(TypeMapper.toBoolean(row[7]));
        codigoSap.setDireccion((String) row[8]);
        codigoSap.setLargos((String) row[9]);
        codigoSap.setRecorte(TypeMapper.toBoolean(row[10]));
        codigoSap.setReingreso((Integer) row[11]);
        codigoSap.setNumSerial((Integer) row[12]);
        codigoSap.setAsignacionAcc(TypeMapper.toBoolean(row[13]));
        codigoSap.setLargosMac((String) row[14]);
        codigoSap.setLargosSerial3((String) row[15]);
        codigoSap.setLargosSerial4((String) row[16]);
        codigoSap.setLargosSerial5((String) row[17]);
        codigoSap.setClasificacion((String) row[18]);
        codigoSap.setValidacionMac(TypeMapper.toBoolean(row[19]));
        codigoSap.setDireccionMac((String) row[20]);
        codigoSap.setRecorteMac(TypeMapper.toBoolean(row[21]));
        codigoSap.setAsignacionFalla(TypeMapper.toBoolean(row[22]));
        codigoSap.setMultimodelo(TypeMapper.toBoolean(row[23]));
        codigoSap.setCantidadCaja((Integer) row[24]);
        return codigoSap;
    }
}
