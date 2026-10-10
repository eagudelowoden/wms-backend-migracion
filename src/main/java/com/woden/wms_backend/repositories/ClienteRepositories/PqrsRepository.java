package com.woden.wms_backend.repositories.ClienteRepositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.PqrsModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface PqrsRepository extends BaseRepository<PqrsModel, Integer> {
  @Transactional
  @Query(value = "SELECT 1 FROM pa_updatepqrs(CAST(:estadoDiagnosticoId AS integer), CAST(:fallaDiagnosticoId AS integer), CAST(:name AS character varying), CAST(:XStudioDiagnosticoTecnicoWoden AS character varying))", nativeQuery = true)
  void updatePqrs(
      @Param("estadoDiagnosticoId") Integer estadoDiagnosticoId,
      @Param("fallaDiagnosticoId") Integer fallaDiagnosticoId,
      @Param("name") String name,
      @Param("XStudioDiagnosticoTecnicoWoden") String XStudioDiagnosticoTecnicoWoden);

  @Query(value = "SELECT CAST(f.id AS integer), CAST(f.id_company AS integer), f.company_name, CAST(f.id_team AS integer), f.team_name, CAST(f.id_user AS integer), f.user_name, f.create_date, CAST(f.x_studio_area_responsable_id AS integer), f.x_studio_area_responsable_name, CAST(f.tipo_de_cliente_pqr_id AS integer), f.tipo_de_cliente_pqr_name, f.x_studio_placa, CAST(f.refurbish_pqr_id AS integer), f.refurbish_pqr_name, CAST(f.tecnologia_pqr_id AS integer), f.tecnologia_pqr_name, CAST(f.ticket_ref AS integer), f.description, f.name, CAST(f.x_studio_cantidad_de_equipos AS integer), CAST(f.clasificacion_refurbish_id AS integer), f.clasificacion_refurbish_name, f.x_studio_metodo_de_contacto, f.x_studio_fecha_llegada_unidades, f.x_studio_fecha_reporte, f.x_studio_metodo_de_contacto_final, f.x_studio_tramite, f.x_studio_nivel_pqr, CAST(f.partner_id AS integer), f.partner_name, f.partner_email, f.attachment_url, CAST(f.id_stage AS integer), f.stage_name, f.tag_ids, f.x_studio_pais, f.x_studio_observaciones, f.x_studio_diagnstico_tcnico_woden, CAST(f.\"Id_diagnostico\" AS integer), CAST(f.\"SerialId\" AS integer), CAST(f.\"CodigosapId\" AS integer), CAST(f.\"FalladiagnosticoId\" AS integer), CAST(f.\"EstadoDiagnosticoId\" AS integer), f.\"FechaDiagnostico\" FROM pa_getmodelpqrs(CAST(:serial AS character varying), CAST(:stage AS integer)) f", nativeQuery = true)
  PqrsModel getModelPqrs(@Param("serial") String serial, @Param("stage") Integer stage);

  @Transactional
  @Query(value = "SELECT pa_updateserialidandsapcodeidpqrs(CAST(:serialId AS integer), CAST(:serial AS character varying), CAST(:codigoSapId AS integer), CAST(NULL AS integer))", nativeQuery = true)
  void updateSerialIdAndSapCodeIdPqrs(
      @Param("serialId") Integer serialId,
      @Param("codigoSapId") Integer codigoSapId,
      @Param("serial") String serial,
      @Param("filas") Integer filas);

  @Transactional
  @Query(value = "SELECT pa_updateobservationpqrs(CAST(:serialId AS integer), CAST(:serial AS character varying), CAST(:observacion AS text), CAST(NULL AS integer))", nativeQuery = true)
  void updateObservationPqrs(
      @Param("serialId") Integer serialId,
      @Param("serial") String serial,
      @Param("observacion") String observacion,
      @Param("filas") Integer filas);
}
