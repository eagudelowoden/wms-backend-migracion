package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "PQRS", schema = "dbo")
public class PqrsModel {

  @Id
  @Column(name = "id")
  private Integer id;

  @Column(name = "id_company")
  private Integer idCompany;

  @Column(name = "company_name")
  private String companyName;

  @Column(name = "id_team")
  private Integer idTeam;

  @Column(name = "team_name")
  private String teamName;

  @Column(name = "id_user")
  private Integer idUser;

  @Column(name = "user_name")
  private String userName;

  @Column(name = "create_date")
  private String createDate;

  @Column(name = "x_studio_area_responsable_id")
  private Integer xStudioAreaResponsableId;

  @Column(name = "x_studio_area_responsable_name")
  private String xStudioAreaResponsableName;

  @Column(name = "tipo_de_cliente_pqr_id")
  private Integer tipoDeClientePqrId;

  @Column(name = "tipo_de_cliente_pqr_name")
  private String tipoDeClientePqrName;

  @Column(name = "x_studio_placa")
  private String xStudioPlaca;

  @Column(name = "refurbish_pqr_id")
  private Integer refurbishPqrId;

  @Column(name = "refurbish_pqr_name")
  private String refurbishPqrName;

  @Column(name = "tecnologia_pqr_id")
  private Integer tecnologiaPqrId;

  @Column(name = "tecnologia_pqr_name")
  private String tecnologiaPqrName;

  @Column(name = "ticket_ref")
  private Integer ticketRef;

  @Column(name = "description")
  private String description;

  @Column(name = "name")
  private String name;

  @Column(name = "x_studio_cantidad_de_equipos")
  private Integer xStudioCantidadDeEquipos;

  @Column(name = "clasificacion_refurbish_id")
  private Integer clasificacionRefurbishId;

  @Column(name = "clasificacion_refurbish_name")
  private String clasificacionRefurbishName;

  @Column(name = "x_studio_metodo_de_contacto")
  private String xStudioMetodoDeContacto;

  @Column(name = "x_studio_fecha_llegada_unidades")
  private String xStudioFechaLlegadaUnidades;

  @Column(name = "x_studio_fecha_reporte")
  private String xStudioFechaReporte;

  @Column(name = "x_studio_metodo_de_contacto_final")
  private String xStudioMetodoDeContactoFinal;

  @Column(name = "x_studio_tramite")
  private String xStudioTramite;

  @Column(name = "x_studio_nivel_pqr")
  private String xStudioNivelPqr;

  @Column(name = "partner_id")
  private Integer partnerId;

  @Column(name = "partner_name")
  private String partnerName;

  @Column(name = "partner_email")
  private String partnerEmail;

  @Column(name = "attachment_url")
  private String attachmentUrl;

  @Column(name = "id_stage")
  private Integer idStage;

  @Column(name = "stage_name")
  private String stageName;

  @Column(name = "tag_ids")
  private String tagIds;

  @Column(name = "x_studio_pais")
  private String xStudioPais;

  @Column(name = "x_studio_observaciones")
  private String xStudioObservaciones;

  @Column(name = "x_studio_diagnstico_tcnico_woden")
  private String xStudioDiagnosticoTecnicoWoden;

  @Column(name = "Id_diagnostico")
  private Integer idDiagnostico;

  @Column(name = "SerialId")
  private Integer serialId;

  @Column(name = "CodigosapId")
  private Integer codigosapId;

  @Column(name = "FalladiagnosticoId")
  private Integer falladiagnosticoId;

  @Column(name = "EstadoDiagnosticoId")
  private Integer estadoDiagnosticoId;

  @Column(name = "FechaDiagnostico")
  private String fechaDiagnostico;
}
