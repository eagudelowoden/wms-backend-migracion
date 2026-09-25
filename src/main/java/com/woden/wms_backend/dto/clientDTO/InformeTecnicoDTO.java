package com.woden.wms_backend.dto.clientDTO;

import java.util.List;

import lombok.Data;

/**
 * Payload del "Informe técnico PQRS" (dictamen de Garantías/TruckRolls) —
 * ver wms-backend/src/main/resources/reports/informe-tecnico-pqrs.html y
 * InformeTecnicoService. Se usa tanto para la vista previa (solo genera el
 * PDF) como para guardar (además lo registra en HojaVida vía DiagnosticoService).
 */
@Data
public class InformeTecnicoDTO {
  // ── Datos del equipo (ya resueltos por el frontend al abrir el modal) ──
  private String cliente;
  private String proceso;
  private String modelo;
  private Integer cantidad;
  private String serial;
  private String tecnico;
  private String fechaRevision;

  // ── Evaluación ──
  private String causaEncontrada;
  private String veredictoTexto;
  private String estadoAprobacion;
  private String fechaAprobacion;
  private String responsableTecnico;
  private String responsableOperacion;

  // ── Metadatos del documento controlado — opcionales, InformeTecnicoService
  // usa un valor fijo si vienen null (pendiente de confirmar con el cliente). ──
  private String codigo;
  private String version;
  private String fechaEmision;

  // ── Solo para /guardar (no se usan al generar la vista previa) ──
  private Integer usuarioId;
  private String modulo;

  private List<EvidenciaDTO> evidencias;

  @Data
  public static class EvidenciaDTO {
    /** Data URI completa (ej. "data:image/jpeg;base64,...") — ya comprimida por el frontend. */
    private String src;
    private String leyenda;
  }
}
