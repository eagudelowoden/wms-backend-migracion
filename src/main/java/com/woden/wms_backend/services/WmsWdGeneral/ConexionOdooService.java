package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.WmsWdGeneral.ConexionOdooModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.ConexionOdooRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class ConexionOdooService extends BaseService<ConexionOdooModel, Integer> {
  @Autowired
  private ConexionOdooRepository repository;

  public ConexionOdooModel getConexionOdoo(Integer id) {
    List<Object[]> results = repository.getConexionOdoo(id);
    // ConexionOdooModel conexionesOdoo = new ConexionOdooModel();
    ConexionOdooModel conexion = new ConexionOdooModel();
    for (Object[] row : results) {
      conexion.setId((Integer) row[0]);
      conexion.setIdClienteWms((Integer) row[1]);
      conexion.setClienteWms((String) row[2]);
      conexion.setIdOdoo((Integer) row[3]);
      conexion.setClienteOdoo((String) row[4]);
      conexion.setServidor((String) row[5]);
      conexion.setDb((String) row[6]);
      conexion.setUserOdoo((String) row[7]);
      conexion.setPassOdoo((String) row[8]);
      conexion.setModelo((String) row[9]);
      conexion.setTeamId((Integer) row[10]);
      conexion.setCompanyId((Integer) row[11]);
      conexion.setMinutosRefresco((Integer) row[12]);
      // conexionesOdoo.add(conexion);
    }
    return conexion;
  }
}
