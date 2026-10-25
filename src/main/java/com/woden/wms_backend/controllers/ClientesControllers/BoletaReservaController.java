package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.BoletaMovimientoDTO;
import com.woden.wms_backend.services.ClienteServices.BoletaReservaService;

/** Boletas de movimiento guardadas en Despacho: reservar, listar, abrir, despachar y cancelar. */
@RestController
@RequestMapping("/client/boletas")
public class BoletaReservaController {

  @Autowired
  private BoletaReservaService boletaReservaService;

  @GetMapping("/config")
  public ResponseEntity<Map<String, Object>> config(@RequestParam(defaultValue = "") String zonaHoraria) {
    return ResponseEntity.ok(boletaReservaService.config(zonaHoraria));
  }

  @PostMapping("/reservar")
  public ResponseEntity<Map<String, Object>> reservar(@RequestBody BoletaMovimientoDTO request) {
    return ResponseEntity.ok(boletaReservaService.reservar(request));
  }

  @GetMapping
  public ResponseEntity<List<Map<String, Object>>> listar(
      @RequestParam(defaultValue = "") String estado,
      @RequestParam(defaultValue = "") String texto) {
    return ResponseEntity.ok(boletaReservaService.listar(estado, texto));
  }

  @GetMapping("/reservados")
  public ResponseEntity<List<Map<String, Object>>> reservados() {
    return ResponseEntity.ok(boletaReservaService.reservados());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Map<String, Object>> detalle(@PathVariable Integer id) {
    return ResponseEntity.ok(boletaReservaService.detalle(id));
  }

  @PostMapping("/{id}/iniciar-despacho")
  public ResponseEntity<Map<String, Object>> iniciarDespacho(@PathVariable Integer id) {
    return ResponseEntity.ok(boletaReservaService.iniciarDespacho(id));
  }

  @PostMapping("/{id}/finalizar-despacho")
  public ResponseEntity<Map<String, Object>> finalizarDespacho(@PathVariable Integer id,
      @RequestParam(defaultValue = "0") Integer usuarioId) {
    return ResponseEntity.ok(boletaReservaService.finalizarDespacho(id, usuarioId));
  }

  @PostMapping("/{id}/cancelar")
  public ResponseEntity<Map<String, Object>> cancelar(@PathVariable Integer id,
      @RequestParam(defaultValue = "0") Integer usuarioId) {
    return ResponseEntity.ok(boletaReservaService.cancelar(id, usuarioId));
  }
}
