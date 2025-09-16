package com.woden.wms_backend.services.ClienteServices;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.ClasificacionModel;
import com.woden.wms_backend.repositories.ClienteRepositories.ClasificacionRepository;
import com.woden.wms_backend.services.BaseService;

import jakarta.transaction.Transactional;

@Service
public class ClasificacionService extends BaseService<ClasificacionModel, Integer> {
  @Autowired
  private ClasificacionRepository repository;

  @Transactional
  public void create(List<List<String>> serialEstadoUsuario, Integer usuarioId) {
    String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    for (List<String> entry : serialEstadoUsuario) {
      String serial = entry.get(0);
      String estado = entry.get(1);
      Integer usuarioAsignado = Integer.parseInt(entry.get(2));
      repository.insertClasificacion(serial, estado, usuarioId, fecha, usuarioAsignado);
    }
  }

  public void insertClasificacionWeb(List<List<String>> seriales, Integer usuarioId) {
    for (List<String> entry : seriales) {
      String serial = entry.get(0);
      Integer usuarioAsignadoId = Integer.parseInt(entry.get(1));
      repository.insertClasificacionWeb(serial, usuarioId, usuarioAsignadoId);
    }
  }

  public void updateClasificacion(String serial, String estadoEnviado, Integer nivelId) {
    repository.updateClasificacion(serial, estadoEnviado, nivelId);
  }

  public void deleteClasificacion(String serial) {
    repository.deleteClasificacion(serial);
  }

  // @Autowired
  // private DataSource dataSource;
  // public int insertarLote(List<List<String>> serialEstadoUsuario, int
  // usuarioId) {
  // int status = 0;
  // String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
  // Date(status));

  // try (Connection conn = dataSource.getConnection();
  // CallableStatement cst = conn.prepareCall("{call
  // pa_InsertClasification(?,?,?,?,?)}")) {

  // for (List<String> entry : serialEstadoUsuario) {
  // cst.setString(1, entry.get(0)); // serial
  // cst.setString(2, entry.get(1)); // estado
  // cst.setInt(3, usuarioId); // usuario que ejecuta
  // cst.setTimestamp(4, Timestamp.valueOf(fecha));
  // cst.setInt(5, Integer.parseInt(entry.get(2))); // usuario asignado
  // cst.addBatch();
  // }

  // int[] filas = cst.executeBatch();
  // status = (filas.length > 0) ? 1 : 0;

  // } catch (SQLException ex) {
  // ex.printStackTrace();
  // }

  // return status;
  // }

}
