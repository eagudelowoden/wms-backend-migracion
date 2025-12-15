package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.EtiquetadoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetadoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class EtiquetadoService extends BaseService<EtiquetadoModel, Integer> {
  @Autowired
  private EtiquetadoRepository etiquetadoRepository;

}
