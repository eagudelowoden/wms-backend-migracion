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
  @Column(name = "Id")
  private int id;
  private int idCompany;
  private String companyName;
  private int idTeam;
  private String teamName;
  private int idUser;
  private String userName;
  private String createDate;
  private int xStudioAreaResponsableId;
  private String xStudioAreaResponsableName;
  private int tipoDeClientePqrId;
  private String tipoDeClientePqrName;
  private String xStudioPlaca;
  private int refurbishPqrId;
  private String refurbishPqrName;
  private int tecnologiaPqrId;
  private String tecnologiaPqrName;
  private int ticketRef;
  private String description;
  private String name;
  private int xStudioCantidadDeEquipos;
  private int clasificacionRefurbishId;
  private String clasificacionRefurbishName;
  private String xStudioMetodoDeContacto;
  private String xStudioFechaLlegadaUnidades;
  private String xStudioFechaReporte;
  private String xStudioMetodoDeContactoFinal;
  private String xStudioTramite;
  private String xStudioNivelPqr;
  private int partnerId;
  private String partnerName;
  private String partnerEmail;
  private String attachmentUrl;
  private int idStage;
  private String stageName;
  private String tagIds;
  private String xStudioPais;
  private String xStudioObservaciones;
  private String xStudioDiagnosticoTecnicoWoden;
  private int idDiagnostico;
  private int serialId;
  private int codigosapId;
  private int falladiagnosticoId;
  private int estadoDiagnosticoId;
  private String fechaDiagnostico;
}
