package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.CodigoSapModelDTO;
import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.repositories.ClienteRepositories.CodigoSapRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.TipoMaestroRepository;
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
    @Autowired
    private TipoMaestroRepository tipoMaestroRepository;

    public List<Map<String, String>> getListDescriptionSapCode() {
        List<Object[]> results = codigoSapRepository.getListDescriptionSapCode();
        List<Map<String, String>> formattedResults = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, String> map = new HashMap<>();
            map.put("codigo", row[0].toString());
            map.put("descripcion", row[1].toString());
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
        return codigoSapRepository.getIdByCodigo(codigo);
    }

    public Integer getIdSerial(String tipo) {
        Integer idTipoCodigoSap = tipoMaestroRepository.getTipoCodigoSap("Tipo CodigoSap");
        Integer id = maestroRepository.getIdSerial(tipo, idTipoCodigoSap);
        return id != null ? id : 0;
    }

    public Integer getIdNoSerial(String tipo) {
        Integer idTipoCodigoSap = tipoMaestroRepository.getTipoCodigoSap("Tipo CodigoSap");
        Integer id = maestroRepository.getIdNoSerial(tipo, idTipoCodigoSap);
        return id != null ? id : 0;
    }

    public Integer getIdComboPallet(String codigo, String descripcion) {
        List<Integer> ids = codigoSapRepository.getIdComboPallet(codigo, descripcion);
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return ids.get(ids.size() - 1);
    }

    public CodigoSapModelDTO obtenerModeloPorCodigo(String codigo) {
        List<Object[]> results = codigoSapRepository.getModelByCodigo(codigo);
        if (results.isEmpty()) {
            return null;
        }
        Object[] row = results.get(0);
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

    public Integer getFamilyId(String codigo) {
        List<Integer> results = codigoSapRepository.getFamilyId(codigo);
        return results.get(0);
    }

    public List<Map<String, Object>> search(String codigo) {
        List<Object[]> results = codigoSapRepository.search(codigo);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("codigo", row[1]);
            map.put("descripcion", row[2]);
            map.put("familia", row[3]);
            map.put("validacion", row[4]);
            map.put("direccion", row[5]);
            map.put("largos", row[6]);
            map.put("recorte", row[7]);
            map.put("reingreso", row[8]);
            map.put("clasificacion", row[9]);
            map.put("numSerial", row[10]);
            map.put("activo", row[11]);
            map.put("tipo", row[12]);
            map.put("asignacion", row[13]);
            map.put("asignacionFa", row[14]);
            map.put("largosMac", row[15]);
            map.put("largosSerial3", row[16]);
            map.put("largosSerial4", row[17]);
            map.put("largosSerial5", row[18]);
            map.put("tipoEquipo", row[19]);
            map.put("area", row[20]);
            map.put("modelo", row[21]);
            map.put("proveedor", row[22]);
            map.put("validacionMac", row[23]);
            map.put("direccionMac", row[24]);
            map.put("recorteMac", row[25]);
            map.put("multimodelo", row[26]);
            map.put("cantidadCaja", row[27]);
            map.put("smartcard", row[28]);
            list.add(map);
        }
        return list;
    }

    public Map<String, List<MaestroModel>> getCatalogos() {
        Integer famTmId = tipoMaestroRepository.getTipoCodigoSap("Familias");
        Integer tipoTmId = tipoMaestroRepository.getTipoCodigoSap("Tipo CodigoSap");
        Integer clasTmId = tipoMaestroRepository.getTipoCodigoSap("Tipo Clasificacion");
        Integer tipoEquipoTmId = tipoMaestroRepository.getTipoCodigoSap("Tipo Equipo");
        Integer areaTmId = tipoMaestroRepository.getTipoCodigoSap("Area");
        Integer modeloProveedor_tmId = tipoMaestroRepository.getTipoCodigoSap("Modelos");
        Integer fabricanteTmId = tipoMaestroRepository.getTipoCodigoSap("Fabricantes");
        Integer fallasTmId = tipoMaestroRepository.getTipoCodigoSap("Fallas Funcionales");

        Map<String, List<MaestroModel>> result = new HashMap<>();
        result.put("familias", famTmId != null ? maestroRepository.findByTipoMaestroId(famTmId) : new ArrayList<>());
        result.put("tipos", tipoTmId != null ? maestroRepository.findByTipoMaestroId(tipoTmId) : new ArrayList<>());
        result.put("clasificaciones", clasTmId != null ? maestroRepository.findByTipoMaestroId(clasTmId) : new ArrayList<>());
        result.put("tiposEquipo", tipoEquipoTmId != null ? maestroRepository.findByTipoMaestroId(tipoEquipoTmId) : new ArrayList<>());
        result.put("areas", areaTmId != null ? maestroRepository.findByTipoMaestroId(areaTmId) : new ArrayList<>());
        result.put("modelos", modeloProveedor_tmId != null ? maestroRepository.findByTipoMaestroId(modeloProveedor_tmId) : new ArrayList<>());
        result.put("fabricantes", fabricanteTmId != null ? maestroRepository.findByTipoMaestroId(fabricanteTmId) : new ArrayList<>());
        result.put("fallas", fallasTmId != null ? maestroRepository.findByTipoMaestroId(fallasTmId) : new ArrayList<>());
        return result;
    }

    @Transactional
    public void create(CodigoSapModel model) {
        Integer tipoEquipoId = (model.getTipoEquipoId() != null && model.getTipoEquipoId() != 0)
                ? model.getTipoEquipoId() : null;
        Integer areaId = (model.getAreaId() != null && model.getAreaId() != 0)
                ? model.getAreaId() : null;
        Integer modeloId = (model.getModeloId() != null && model.getModeloId() != 0)
                ? model.getModeloId() : null;
        Integer proveedorId = (model.getProveedorId() != null && model.getProveedorId() != 0)
                ? model.getProveedorId() : null;

        codigoSapRepository.insert(
                model.getCodigo(), model.getDescripcion(),
                model.getFamiliaId(),
                model.getTipoId(),
                Boolean.TRUE.equals(model.getValidacion()) ? 1 : 0,
                model.getDireccion() != null ? model.getDireccion() : "NINGUNA",
                model.getLargos() != null ? model.getLargos() : "0",
                model.getRecorte() != null ? model.getRecorte() : 0,
                model.getReingreso() != null ? model.getReingreso() : 0,
                model.getClasificacionId(),
                model.getNumSerial() != null ? model.getNumSerial() : 0,
                model.getAsignar() != null ? model.getAsignar() : 0,
                model.getLargosMac() != null ? model.getLargosMac() : "0",
                model.getLargosSerial3() != null ? model.getLargosSerial3() : "0",
                model.getLargosSerial4() != null ? model.getLargosSerial4() : "0",
                model.getLargosSerial5() != null ? model.getLargosSerial5() : "0",
                tipoEquipoId, areaId, modeloId, proveedorId,
                model.getValidacionMac() != null ? model.getValidacionMac() : 0,
                model.getDireccionMac() != null ? model.getDireccionMac() : "NINGUNA",
                model.getRecorteMac() != null ? model.getRecorteMac() : 0,
                model.getAsignarFa() != null ? model.getAsignarFa() : 0,
                model.getMultimodelo() != null ? model.getMultimodelo() : 0,
                model.getCantidadCaja() != null ? model.getCantidadCaja() : 0,
                model.getSmartCard() != null ? model.getSmartCard() : 0);
    }

    @Transactional
    public Integer update(CodigoSapModel model) {
        Integer tipoEquipoId = (model.getTipoEquipoId() != null && model.getTipoEquipoId() != 0)
                ? model.getTipoEquipoId() : null;
        Integer areaId = (model.getAreaId() != null && model.getAreaId() != 0)
                ? model.getAreaId() : null;
        Integer modeloId = (model.getModeloId() != null && model.getModeloId() != 0)
                ? model.getModeloId() : null;
        Integer proveedorId = (model.getProveedorId() != null && model.getProveedorId() != 0)
                ? model.getProveedorId() : null;

        return codigoSapRepository.update(
                model.getCodigo(), model.getDescripcion(),
                model.getFamiliaId(),
                model.getTipoId(),
                Boolean.TRUE.equals(model.getValidacion()) ? 1 : 0,
                model.getDireccion() != null ? model.getDireccion() : "NINGUNA",
                model.getLargos() != null ? model.getLargos() : "0",
                model.getRecorte() != null ? model.getRecorte() : 0,
                model.getReingreso() != null ? model.getReingreso() : 0,
                model.getClasificacionId(),
                model.getNumSerial() != null ? model.getNumSerial() : 0,
                model.getId(),
                model.getAsignar() != null ? model.getAsignar() : 0,
                model.getLargosMac() != null ? model.getLargosMac() : "0",
                model.getLargosSerial3() != null ? model.getLargosSerial3() : "0",
                model.getLargosSerial4() != null ? model.getLargosSerial4() : "0",
                model.getLargosSerial5() != null ? model.getLargosSerial5() : "0",
                tipoEquipoId, areaId, modeloId, proveedorId,
                model.getValidacionMac() != null ? model.getValidacionMac() : 0,
                model.getDireccionMac() != null ? model.getDireccionMac() : "NINGUNA",
                model.getRecorteMac() != null ? model.getRecorteMac() : 0,
                model.getAsignarFa() != null ? model.getAsignarFa() : 0,
                model.getMultimodelo() != null ? model.getMultimodelo() : 0,
                model.getCantidadCaja() != null ? model.getCantidadCaja() : 0,
                model.getSmartCard() != null ? model.getSmartCard() : 0);
    }

    @Transactional
    public void delete(Integer id) {
        codigoSapRepository.delete(id);
    }

    @Transactional
    public Integer innactivate(Integer id, Integer estado) {
        return codigoSapRepository.innactivate(estado, id);
    }

    public Integer getCount(Integer id) {
        return codigoSapRepository.getCount(id);
    }

    public List<Map<String, Object>> searchAllCodigos() {
        List<Object[]> results = codigoSapRepository.searchSimpliCodeSap();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("codigo", row[1]);
            map.put("descripcion", row[2]);
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> searchAccesoriosAsignados(Integer codigoSapId) {
        List<Object[]> results = codigoSapRepository.searchCodeSapAccesorio(codigoSapId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("codigo", row[1]);
            map.put("descripcion", row[2]);
            list.add(map);
        }
        return list;
    }

    @Transactional
    public void asignarAccesorio(Integer codigoSapId, Integer accesorioId) {
        codigoSapRepository.insertAccCodigoSap(codigoSapId, accesorioId);
    }

    @Transactional
    public void removerAccesorio(Integer codigoSapId, Integer accesorioId) {
        codigoSapRepository.deleteAccCodigoSap(codigoSapId, accesorioId);
    }
}
