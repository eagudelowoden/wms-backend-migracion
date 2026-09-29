package com.woden.wms_backend.services.ClienteServices;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Base64;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.PqrsModel;
import com.woden.wms_backend.models.WmsWdGeneral.ConexionOdooModel;
import com.woden.wms_backend.repositories.ClienteRepositories.PqrsRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class OdooService extends BaseService<PqrsModel, Integer> {
  @Autowired
  private PqrsRepository pqrsRepository;

  public int actualizarTicketsOdoo(List<PqrsModel> tickets, int odooPqrsON, ConexionOdooModel odooModel) {
    if (odooPqrsON != 1 || tickets.isEmpty() || odooModel == null)
      return 1;

    try {
      // Configuración inicial Odoo
      String url = odooModel.getServidor();
      String db = odooModel.getDb();
      String username = odooModel.getUserOdoo();
      String password = odooModel.getPassOdoo();

      String decodedPassword = new String(Base64.getDecoder().decode(password));
      // Conexión común
      XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
      commonConfig.setServerURL(new URL(url + "/xmlrpc/2/common"));
      XmlRpcClient commonClient = new XmlRpcClient();
      commonClient.setConfig(commonConfig);

      int uid = (Integer) commonClient.execute("authenticate",
          new Object[] { db, username, decodedPassword, new HashMap<>() });

      if (uid == 0) {
        System.out.println("Error de autenticación");
        return 0;
      }

      // Cliente para objetos
      XmlRpcClientConfigImpl modelsConfig = new XmlRpcClientConfigImpl();
      modelsConfig.setServerURL(new URL(url + "/xmlrpc/2/object"));
      XmlRpcClient modelsClient = new XmlRpcClient();
      modelsClient.setConfig(modelsConfig);

      for (PqrsModel pqrs : tickets) {
        PqrsModel pqrsModel = pqrsRepository.getModelPqrs(pqrs.getName(), 4);
        Integer ticketId = pqrsModel.getId();
        Map<String, Object> valores = new HashMap<>();
        valores.put("stage_id", 4);
        valores.put("x_studio_observaciones", pqrsModel.getXStudioObservaciones());
        valores.put("x_studio_diagnstico_tcnico_woden", pqrsModel.getXStudioDiagnosticoTecnicoWoden());
        valores.put("x_studio_fecha_llegada_unidades", pqrsModel.getXStudioFechaLlegadaUnidades().replace(".0", ""));
        valores.put("x_studio_fecha_reporte", pqrsModel.getXStudioFechaReporte().replace(".0", ""));

        Object[] params = new Object[] {
            db, uid, decodedPassword,
            "helpdesk.ticket", "write",
            new Object[] { Arrays.asList(ticketId), valores }
        };

        try {
          boolean success = (Boolean) modelsClient.execute("execute_kw", params);
          if (success) {
            System.out.println("Ticket actualizado!");
            pqrsModel = null;
          } else {
            System.out.println("Error al actualizar ticket: " + pqrs.getId());
            pqrsModel = null;
            return 0;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      return 1;

    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }
}
