package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ClienteConfigService {

  private static final Logger logger = LoggerFactory.getLogger(ClienteConfigService.class);
  private static final long CACHE_MS = 30_000;

  private record Lectura(String valor, long vence) {}

  private static final Map<String, Lectura> CACHE = new ConcurrentHashMap<>();

  @PersistenceContext
  private EntityManager em;

  public String get(String clave, String porDefecto) {
    int cliente = cliente();
    String k = cliente + "|" + clave;
    Lectura l = CACHE.get(k);
    if (l != null && l.vence() > System.currentTimeMillis()) {
      return l.valor() == null ? porDefecto : l.valor();
    }
    String valor = null;
    try {
      if (tabla()) {
        List<?> rs = em.createNativeQuery("SELECT Valor FROM general.ClienteConfig WHERE ClienteId = ?1 "
            + "AND Clave = ?2").setParameter(1, cliente).setParameter(2, clave).getResultList();
        valor = rs.isEmpty() || rs.get(0) == null ? null : String.valueOf(rs.get(0));
      }
    } catch (Exception e) {
      logger.warn("[ClienteConfig] {} del cliente {} no se pudo leer: {}", clave, cliente, e.getMessage());
    }
    CACHE.put(k, new Lectura(valor, System.currentTimeMillis() + CACHE_MS));
    return valor == null ? porDefecto : valor;
  }

  @Transactional(rollbackFor = Exception.class)
  public void set(String clave, String valor, String usuario) {
    if (!tabla()) {
      throw new BusinessRuleException("Falta el script 2026-10-29_parametros_wms.sql en WmsWdGeneral.");
    }
    int cliente = cliente();
    em.createNativeQuery("INSERT INTO general.ClienteConfig (ClienteId, Clave, Valor, Usuario, Fecha) "
        + "VALUES (?1, ?2, ?3, ?4, LOCALTIMESTAMP) "
        + "ON CONFLICT (ClienteId, Clave) DO UPDATE SET Valor = EXCLUDED.Valor, Usuario = EXCLUDED.Usuario, "
        + "Fecha = LOCALTIMESTAMP")
        .setParameter(1, cliente).setParameter(2, clave).setParameter(3, valor == null ? "" : valor)
        .setParameter(4, usuario == null ? "" : usuario).executeUpdate();
    CACHE.remove(cliente + "|" + clave);
  }

  private boolean tabla() {
    Object r = em.createNativeQuery("SELECT CASE WHEN to_regclass('general.ClienteConfig') IS NULL THEN 0 "
        + "ELSE 1 END").getSingleResult();
    return r instanceof Number n && n.intValue() == 1;
  }

  private static int cliente() {
    Integer id = ClientDatabaseContext.getCurrentClientId();
    if (id == null || id <= 0) {
      throw new BusinessRuleException("No se identificó el cliente de la sesión.");
    }
    return id;
  }
}
