package com.woden.wms_backend.repositories.WmsWdGeneral;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woden.wms_backend.models.WmsWdGeneral.IngresoAppModel;

public interface IngresoAppRepository extends JpaRepository<IngresoAppModel, Integer> {

  // boolean existsBySerial(String sn);

  // boolean existsByMac(String cmMac);
}