package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.WmsWdGeneral.ConexionOdooModel;

@Repository
public interface ConexionOdooRepository extends JpaRepository<ConexionOdooModel, Integer> {
  @Query(value = "SELECT CAST(f.id AS integer), CAST(f.id_cliente_wms AS integer), f.cliente_wms, CAST(f.id_odoo AS integer), f.cliente_odoo, f.servidor, f.db, f.user_odoo, f.password, f.modelo, CAST(f.team_id AS integer), CAST(f.company_id AS integer) FROM general.pa_getconexionodoo(:id) f", nativeQuery = true)
  List<Object[]> getConexionOdoo(@Param("id") int id);
}
