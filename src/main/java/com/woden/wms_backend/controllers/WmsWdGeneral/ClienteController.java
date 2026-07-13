package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.config.DataSource.DynamicDataSourceConfig;
import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.ClientSwitchRequest;
import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.security.JwtUtil;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteService;

// import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/general/clientes")
public class ClienteController extends BaseController<ClienteModel, Integer> {
    private final ClienteService clienteService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private DynamicDataSourceConfig dynamicDataSourceConfig;

    public ClienteController(ClienteService service) {
        super(service);
        this.clienteService = service;
    }

    @GetMapping("/list/{usuarioId}")
    public List<String> getListClient(@PathVariable int usuarioId) {
        return clienteService.getListClient(usuarioId);
    }

    // Endpoint para obtener el ID del cliente por nombre
    @GetMapping("/getId")
    public Integer getIdClient(@RequestParam String nombre) {
        return clienteService.getIdClient(nombre);
    }

    @GetMapping("/getClienteById/{id}")
    public ClienteModel getIdClientByDbase(@PathVariable Integer id) {
        return clienteService.getById(id);
    }

    @PostMapping("/switch-client")

    public ResponseEntity<?> switchClient(@RequestBody ClientSwitchRequest request) {
        // Validar que el usuario tenga acceso a este cliente
        ClienteModel client = clienteService.getById(request.getClientId());

        if (client == null) {
            return ResponseEntity.badRequest().body("Cliente no encontrado");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        // Verificar que el cliente tenga una base de datos configurada
        if (client.getDbase() == null || client.getDbase().isEmpty()) {
            return ResponseEntity.badRequest().body("El cliente no tiene una base de datos configurada");
        }

        // Generar nuevo token con la BD del cliente
        String newToken = jwtUtil.generateToken(
                authentication.getName(),
                client.getNombre(),
                client.getDbase(),
                client.getId());

        try {
            dynamicDataSourceConfig.initializeClientDataSource(client.getDbase());
        } catch (Exception e) {
            System.out.println("Error al activar conexión: " + e.getMessage());
        }

        Map<String, String> response = new HashMap<>();
        response.put("clientToken", newToken);
        response.put("clientDb", client.getNombre());
        response.put("clientDbName", client.getDbase());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-db")
    public ResponseEntity<DatabaseInfo> getCurrentDatabase() {
        String currentDb = ClientDatabaseContext.getCurrentClientDb();
        String connectionUrl = "jdbc:sqlserver://wd-wms-prd.cyeyhpu1wzr0.us-east-2.rds.amazonaws.com:14331;" +
                "databaseName=" + currentDb + ";trustServerCertificate=true";

        return ResponseEntity.ok(new DatabaseInfo(currentDb, connectionUrl));
    }

    @GetMapping("/kitIngresoON/{id}")
    public Boolean getKitIngresoON(@PathVariable int id) {
        return clienteService.getKitIngresoValue(id);
    }

    @GetMapping("/getClientes")
    public List<Map<String, Object>> getClientes() {
        return clienteService.getClientes();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(required = false) String termino) {
        return ResponseEntity.ok(clienteService.search(termino));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getDetail(@PathVariable Integer id) {
        Map<String, Object> result = clienteService.findByIdMapped(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> create(@RequestBody ClienteModel model) {
        Integer newId = clienteService.create(model);
        return ResponseEntity.ok(Map.of("id", newId, "message", "Cliente creado."));
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, Object>> updateCliente(@PathVariable Integer id, @RequestBody ClienteModel model) {
        Integer filas = clienteService.update(id, model);
        if (filas == 0) {
            return ResponseEntity.status(404).body(Map.of("message", "Cliente no encontrado."));
        }
        return ResponseEntity.ok(Map.of("message", "Cliente actualizado.", "filas", filas));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, Object>> removeCliente(@PathVariable Integer id) {
        clienteService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Cliente eliminado."));
    }

    public static class DatabaseInfo {
        private final String databaseName;
        private final String connectionUrl;
        private final boolean isGeneral;

        public DatabaseInfo(String databaseName, String connectionUrl) {
            this.databaseName = databaseName;
            this.connectionUrl = connectionUrl;
            this.isGeneral = "WmsWdGeneral".equals(databaseName);
        }

        // Getters
        public String getDatabaseName() {
            return databaseName;
        }

        public String getConnectionUrl() {
            return connectionUrl;
        }

        public boolean isGeneral() {
            return isGeneral;
        }
    }

}
