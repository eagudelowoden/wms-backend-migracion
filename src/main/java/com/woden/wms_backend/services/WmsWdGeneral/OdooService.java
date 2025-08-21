package com.woden.wms_backend.services.WmsWdGeneral;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.PqrsModel;
import com.woden.wms_backend.models.WmsWdGeneral.ConexionOdooModel;

@Service
public class OdooService {
  public int actualizarTicketsOdoo(List<PqrsModel> tickets, int odooPqrsON, ConexionOdooModel odooModel) {
    if (odooPqrsON != 1 || tickets.isEmpty() || odooModel == null)
      return 1;

    try {
      // Configuración inicial Odoo
      String url = odooModel.getServidor();
      String db = odooModel.getDb();
      String username = odooModel.getUserOdoo();
      String password = odooModel.getPassOdoo();

      // Conexión común
      XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
      commonConfig.setServerURL(new URL(url + "/xmlrpc/2/common"));
      XmlRpcClient commonClient = new XmlRpcClient();
      commonClient.setConfig(commonConfig);

      int uid = (Integer) commonClient.execute("authenticate",
          new Object[] { db, username, password, new HashMap<>() });

      if (uid == 0) {
        System.out.println("Error de autenticación");
        return 0;
      }

      System.out.println("Conexión Odoo exitosa");

      // Cliente para objetos
      XmlRpcClientConfigImpl modelsConfig = new XmlRpcClientConfigImpl();
      modelsConfig.setServerURL(new URL(url + "/xmlrpc/2/object"));
      XmlRpcClient modelsClient = new XmlRpcClient();
      modelsClient.setConfig(modelsConfig);

      for (PqrsModel pqrs : tickets) {
        Map<String, Object> valores = new HashMap<>();
        valores.put("stage_id", 4);
        valores.put("x_studio_observaciones", pqrs.getXStudioObservaciones());
        valores.put("x_studio_diagnstico_tcnico_woden", pqrs.getXStudioDiagnosticoTecnicoWoden());
        valores.put("x_studio_fecha_llegada_unidades", pqrs.getXStudioFechaLlegadaUnidades().replace(".0", ""));
        valores.put("x_studio_fecha_reporte", pqrs.getXStudioFechaReporte().replace(".0", ""));

        Object[] params = new Object[] {
            db, uid, password,
            "helpdesk.ticket", "write",
            new Object[] { Collections.singletonList(pqrs.getId()), valores }
        };

        try {
          boolean success = (Boolean) modelsClient.execute("execute_kw", params);
          if (success) {
            System.out.println("Ticket actualizado!");
          } else {
            System.out.println("Error al actualizar ticket: " + pqrs.getId());
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
