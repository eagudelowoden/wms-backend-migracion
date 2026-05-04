package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.services.ClienteServices.MaestroService;

@RestController
@RequestMapping("/client/maestros")
public class MaestroController extends BaseController<MaestroModel, Integer> {

    private final MaestroService maestroService;

    public MaestroController(MaestroService maestroService) {
        super(maestroService);
        this.maestroService = maestroService;
    }

    @GetMapping("/tipologias")
    public List<String> getTipologias(
            @RequestParam String desc1,
            @RequestParam String desc2,
            @RequestParam String desc3,
            @RequestParam String desc4) {
        return maestroService.obtenerTipologias(desc1, desc2, desc3, desc4);
    }

    @GetMapping("/tipoMaestro/{tipoMaestroId}")
    public List<MaestroModel> getByTipoMaestroId(@PathVariable int tipoMaestroId) {
        return maestroService.getByTipoMaestroId(tipoMaestroId);
    }

    @GetMapping("/getId")
    public ResponseEntity<Integer> getIdMaster(
            @RequestParam String codigo,
            @RequestParam String tipo) {
        Integer id = maestroService.getIdMaster(codigo, tipo);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<String>> getListByTipo(@RequestParam String tipo) {
        List<String> maestros = maestroService.getListByTipo(tipo);
        return ResponseEntity.ok(maestros);
    }

    @GetMapping("/origenes")
    public ResponseEntity<List<String>> getOrigenes(@RequestParam String tipo) {
        List<String> origenes = maestroService.obtenerOrigenes(tipo);
        return ResponseEntity.ok(origenes);
    }

    @GetMapping("/getModelMaster")
    public ResponseEntity<List<String>> getModelMaster(@RequestParam String codigoSap) {
        List<String> results = maestroService.getModelMaster(codigoSap);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/getLevelsClasification")
    public ResponseEntity<List<String>> getLevelsClasification() {
        List<String> results = maestroService.getLevelsClasification();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/getFallas")
    public List<Map<String, String>> getFallas(@RequestParam String nombre) {
        return maestroService.getFallas(nombre);
    }

    @GetMapping("/getDescription")
    public ResponseEntity<List<String>> getDesctiption(@RequestParam String codigo) {
        return ResponseEntity.ok(maestroService.getDesctiption(codigo));
    }

    @GetMapping("/getWarranty")
    public ResponseEntity<Integer> getWarranty() {
        return ResponseEntity.ok(maestroService.getWarranty());
    }

    @GetMapping("/searchLevelComponentsAsig")
    public ResponseEntity<Integer> searchLevelComponentsAsig(@RequestParam String serial) {
        return ResponseEntity.ok(maestroService.searchLevelComponentsAsig(serial));
    }

    @GetMapping("/getLevelById")
    public ResponseEntity<Integer> getLevelById(@RequestParam Integer levelId) {
        return ResponseEntity.ok(maestroService.getLevelById(levelId));
    }

    @GetMapping("/getLevelId")
    public ResponseEntity<Integer> getLevelId(@RequestParam String level) {
        return ResponseEntity.ok(maestroService.getLevelId(level));
    }

    @GetMapping("/getIdLevelRepairMaster")
    public ResponseEntity<Integer> getIdLevelRepairMaster() {
        return ResponseEntity.ok(maestroService.getIdLevelRepairMaster());
    }

    @PostMapping("/insertComponentsAsig")
    public ResponseEntity<Integer> insertComponentsAsig(@RequestBody Map<String, Object> body) {
        String serial = (String) body.get("serial");
        Object componenteIdObj = body.get("componenteId");
        List<Integer> componenteId;
        if (componenteIdObj instanceof List<?>) {
            componenteId = ((List<?>) componenteIdObj).stream()
                    .filter(item -> item instanceof Number)
                    .map(item -> ((Number) item).intValue())
                    .toList();
        } else {
            componenteId = List.of();
        }
        componenteId.forEach(id -> maestroService.insertComponentsAsig(serial, id));
        return ResponseEntity.ok(1);
    }

    @PostMapping("/deleteComponentsAsig")
    public ResponseEntity<Integer> deleteComponentsAsig(@RequestBody Map<String, Object> body) {
        String serial = (String) body.get("serial");
        Object componenteIdObj = body.get("componenteId");
        List<Integer> componenteId;
        if (componenteIdObj instanceof List<?>) {
            componenteId = ((List<?>) componenteIdObj).stream()
                    .filter(item -> item instanceof Number)
                    .map(item -> ((Number) item).intValue())
                    .toList();
        } else {
            componenteId = List.of();
        }
        componenteId.forEach(id -> maestroService.deleteComponentsAsig(serial, id));
        return ResponseEntity.ok(1);
    }

    @GetMapping("/searchComponents")
    public ResponseEntity<List<Map<String, Object>>> searchComponents(@RequestParam String serial,
            @RequestParam String falla) {
        List<Map<String, Object>> componentes = maestroService.searchComponents(serial, falla);
        return ResponseEntity.ok(componentes);
    }

    @GetMapping("/searchComponentsAsig")
    public ResponseEntity<List<Map<String, Object>>> searchComponentsAsig(@RequestParam String serial,
            @RequestParam String falla) {
        List<Map<String, Object>> componentes = maestroService.searchComponentsAsig(serial, falla);
        return ResponseEntity.ok(componentes);
    }

    @GetMapping("/getFamilyNumberBox")
    public ResponseEntity<Integer> getFamilyNumberBox(@RequestParam String codigoSap, @RequestParam String destino) {
        Integer result = maestroService.getFamilyNumberBox(codigoSap, destino);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getFamilyAcronyms")
    public ResponseEntity<String> getFamilyAcronyms(@RequestParam String codigoSap, @RequestParam String destino) {
        String result = maestroService.getFamilyAcronyms(codigoSap, destino);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/addCountBoxFamily")
    public ResponseEntity<Integer> addCountBoxFamily(@RequestParam int familyId, @RequestParam String value) {
        try {
            maestroService.addCountBoxFamily(familyId, value);
            return ResponseEntity.ok(1);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
        }
    }

    @GetMapping("/getFamilyMaster")
    public ResponseEntity<String> getFamilyMaster(@RequestParam String codigoSap) {
        String result = maestroService.getFamilyMaster(codigoSap);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getProviderMaster")
    public ResponseEntity<String> getProviderMaster(@RequestParam String codigoSap) {
        String result = maestroService.getProviderMaster(codigoSap);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getProviderDescriptionMaster")
    public ResponseEntity<String> getProviderDescriptionMaster(@RequestParam String codigoSap) {
        String result = maestroService.getProviderDescriptionMaster(codigoSap);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getMasterNameById")
    public ResponseEntity<String> getMasterNameById(@RequestParam Integer id) {
        String result = maestroService.getMasterNameById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getMasterDescriptionById")
    public ResponseEntity<String> getMasterDescriptionById(@RequestParam Integer id) {
        String result = maestroService.getMasterDescriptionById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getMasterDetailById")
    public ResponseEntity<String> getMasterDetailById(@RequestParam Integer id) {
        String result = maestroService.getMasterDetailById(id);
        return ResponseEntity.ok(result);
    }
}
