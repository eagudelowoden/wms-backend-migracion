package com.woden.wms_backend.util;

import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class EncryptUtil {

  public String encode(String clave) {
    String encoded = Base64.getEncoder().encodeToString(clave.getBytes());
    return encoded;
  }

  public String decode(String clave) {
    byte[] decodedBytes = Base64.getDecoder().decode(clave);
    String decoded = new String(decodedBytes);
    return decoded;
  }
}