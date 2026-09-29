package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.models.WmsWdGeneral.IngresoAppModel;
import com.woden.wms_backend.services.WmsWdGeneral.IngresoAppService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/general/ingreso")
@RequiredArgsConstructor
public class IngresoAppController {

  private final IngresoAppService service;

  @PostMapping("/guardar")
  public IngresoAppModel guardar(@RequestBody IngresoAppModel data) {
    return service.guardarIngreso(data);
  }

  // @GetMapping("/listar")
  // public List<IngresoModel> listarTodo() {
  // return service.listarTodo();
  // }
}