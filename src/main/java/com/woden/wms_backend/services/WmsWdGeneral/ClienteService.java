package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.ClienteRepository;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.util.EncryptUtil;

@Service
public class ClienteService extends BaseService<ClienteModel, Integer> {

    private final ClienteRepository clienteRepository;
    private final EncryptUtil encryptUtil;

    public ClienteService(ClienteRepository clienteRepository, EncryptUtil encryptUtil) {
        this.clienteRepository = clienteRepository;
        this.encryptUtil = encryptUtil;
    }

    public List<String> getListClient(int usuarioId) {
        return clienteRepository.getListClient(usuarioId);
    }

    public int getIdClient(String nombre) {
        return clienteRepository.getIdClient(nombre);
    }

    public Boolean getKitIngresoValue(int id) {
        return clienteRepository.getKitIngresoON(id);
    }

    public String getDbaseById(int id) {
        return clienteRepository.getDbaseById(id);
    }

    public List<Map<String, Object>> getClientes() {
        List<Object[]> results = clienteRepository.getClientes();
        List<Map<String, Object>> clientes = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> cliente = new HashMap<>();
            cliente.put("id", (Integer) result[0]);
            cliente.put("nombre", (String) result[1]);
            clientes.add(cliente);
        }
        return clientes;
    }

    public List<Map<String, Object>> search(String termino) {
        List<Object[]> results = clienteRepository.searchSP(termino);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("nombre", row[1]);
            map.put("colorCorporativo", row[2]);
            map.put("activo", row[3]);
            map.put("dbase", row[4]);
            map.put("ipServer", row[5]);
            map.put("bandera", row[6]);
            list.add(map);
        }
        return list;
    }

    public Map<String, Object> findByIdMapped(Integer id) {
        List<Object[]> results = clienteRepository.findByIdSP(id);
        if (results.isEmpty()) return null;
        Object[] r = results.get(0);
        Map<String, Object> map = new HashMap<>();
        map.put("id", r[0]);
        map.put("nombre", r[1]);
        map.put("colorCorporativo", r[2]);
        map.put("activo", r[3]);
        map.put("conn", r[4]);
        map.put("manApp", r[5]);
        map.put("imagenesEtiquetado", r[6]);
        map.put("imagenesEmpaque", r[7]);
        map.put("imagenesIngreso", r[8]);
        map.put("prnEtiquetado", r[9]);
        map.put("prnEmpaque", r[10]);
        map.put("prnIngreso", r[11]);
        map.put("lblEtiquetado", r[12]);
        map.put("lblEmpaque", r[13]);
        map.put("lblIngreso", r[14]);
        map.put("archivos", r[15]);
        map.put("pallet", r[16]);
        map.put("codigoSap", r[17]);
        map.put("ipServer", r[18]);
        map.put("dbase", r[19]);
        map.put("dbUser", r[20]);
        map.put("dbPass", r[21]);
        map.put("bandera", r[22]);
        map.put("recogidaON", r[23]);
        map.put("baseIngresoON", r[24]);
        map.put("baseNoDisponibleON", r[25]);
        map.put("loteEmpaqueON", r[26]);
        map.put("largoGuia", r[27]);
        map.put("kitIngresoON", r[28]);
        map.put("componenteON", r[29]);
        map.put("tipoOrigenUsuarioON", r[30]);
        map.put("nivelClasificacionON", r[31]);
        map.put("calidadON", r[32]);
        map.put("smartCardInfoON", r[33]);
        map.put("bloqueoReimpresionON", r[34]);
        map.put("etiquetaUnitariaON", r[35]);
        map.put("prealerta", r[36]);
        map.put("adicionPrealertaON", r[37]);
        map.put("odooPqrsON", r[38]);
        return map;
    }

    @Transactional
    public Integer create(ClienteModel model) {
        String ipServer = model.getIp_server();
        String dbPass = model.getDb_pass();
        if (ipServer != null && !ipServer.isBlank()) {
            ipServer = encryptUtil.encode(ipServer);
        }
        if (dbPass != null && !dbPass.isBlank()) {
            dbPass = encryptUtil.encode(dbPass);
        }
        return clienteRepository.insertSP(
                model.getNombre(),
                model.getColorCorporativo(),
                model.getActivo() != null ? model.getActivo() : 1,
                model.getConn(),
                model.getMan_app() != null ? model.getMan_app() : 0,
                model.getImagenesEtiquetado(),
                model.getImagenesEmpaque(),
                null,
                model.getPrnEtiquetado(),
                model.getPrnEmpaque(),
                null,
                model.getLblEtiquetado(),
                model.getLblEmpaque(),
                null,
                model.getArchivos(),
                model.getPallet(),
                model.getCodigoSap(),
                ipServer,
                model.getDbase(),
                model.getDb_user(),
                dbPass,
                model.getBandera(),
                model.getRecogidaON() != null ? model.getRecogidaON() : 0,
                model.getBaseIngresoON() != null ? model.getBaseIngresoON() : 0,
                model.getBaseNoDisponibleON() != null ? model.getBaseNoDisponibleON() : 0,
                model.getLoteEmpaqueON() != null ? model.getLoteEmpaqueON() : 0,
                model.getLargoGuia(),
                model.getKitIngresoON() != null ? model.getKitIngresoON() : 0,
                model.getComponenteON() != null ? model.getComponenteON() : 0,
                model.getTipoOrigenUsuarioON() != null ? model.getTipoOrigenUsuarioON() : 0,
                model.getNivelClasificacionON() != null ? model.getNivelClasificacionON() : 0,
                model.getCalidadON() != null ? model.getCalidadON() : 0,
                model.getSmartCardInfoON() != null ? model.getSmartCardInfoON() : 0,
                model.getBloqueoReimpresionON() != null ? model.getBloqueoReimpresionON() : 0,
                model.getEtiquetaUnitariaON() != null ? model.getEtiquetaUnitariaON() : 0,
                model.getPrealerta(),
                model.getAdicionPrealertaON() != null ? model.getAdicionPrealertaON() : 0,
                model.getOdooPqrsON() != null ? model.getOdooPqrsON() : false);
    }

    @Transactional
    public Integer update(Integer id, ClienteModel model) {
        return clienteRepository.updateSP(
                id,
                model.getNombre(),
                model.getColorCorporativo(),
                model.getActivo(),
                model.getConn(),
                model.getMan_app(),
                model.getImagenesEtiquetado(),
                model.getImagenesEmpaque(),
                model.getPrnEtiquetado(),
                model.getPrnEmpaque(),
                model.getLblEtiquetado(),
                model.getLblEmpaque(),
                model.getArchivos(),
                model.getPallet(),
                model.getCodigoSap(),
                model.getIp_server(),
                model.getDbase(),
                model.getDb_user(),
                model.getDb_pass(),
                model.getBandera(),
                model.getRecogidaON(),
                model.getBaseIngresoON(),
                model.getBaseNoDisponibleON(),
                model.getLoteEmpaqueON(),
                model.getLargoGuia(),
                model.getKitIngresoON(),
                model.getComponenteON(),
                model.getTipoOrigenUsuarioON(),
                model.getNivelClasificacionON(),
                model.getCalidadON(),
                model.getSmartCardInfoON(),
                model.getBloqueoReimpresionON(),
                model.getEtiquetaUnitariaON(),
                model.getPrealerta(),
                model.getAdicionPrealertaON(),
                model.getOdooPqrsON());
    }

    @Transactional
    public void delete(Integer id) {
        clienteRepository.deleteSP(id);
    }

    @Transactional
    public void toggle(Integer id, Integer estado) {
        clienteRepository.toggle(id, estado);
    }

    public boolean hasMovements(Integer id) {
        Integer count = clienteRepository.countMovements(id);
        return count != null && count > 0;
    }
}
