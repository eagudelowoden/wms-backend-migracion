package com.woden.wms_backend.controllers.WmsWdGeneral;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/general")
public class VersionController {

  @Value("${app.version}")
  private String appVersion;

  @GetMapping("/version")
  public ResponseEntity<String> getVersion() {
    System.out.println("getVersion: " + appVersion);
    return ResponseEntity.ok(appVersion);
  }
}
