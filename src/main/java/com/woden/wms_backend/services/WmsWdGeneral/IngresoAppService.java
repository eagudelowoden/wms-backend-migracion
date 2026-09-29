package com.woden.wms_backend.services.WmsWdGeneral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.WmsWdGeneral.IngresoAppModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngresoAppService {

  @Autowired
  private final IngresoAppRepository repository;

  public IngresoAppModel guardarIngreso(IngresoAppModel data) {
    return repository.save(data);
  }
}