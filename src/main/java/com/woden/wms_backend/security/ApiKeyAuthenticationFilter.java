package com.woden.wms_backend.security;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.repositories.WmsWdGeneral.IntegracionApiKeyRepository;
import com.woden.wms_backend.models.WmsWdGeneral.IntegracionApiKeyModel;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteService;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;

/**
 * Autenticación por API Key para integraciones externas sistema-a-sistema
 * (sin JWT, sin usuario logueado) — solo se aplica a rutas bajo /evidencias/**
 * (ver SecurityConfig: esas rutas están en permitAll, este filtro es el que
 * realmente las protege). Genérico — no específico de Hoja de Vida, para
 * poder reutilizarse con la integración de Odoo más adelante.
 *
 * A diferencia de JwtAuthenticationFilter (que no rechaza nada si falta el
 * header, dejando que Spring Security bloquee después con .authenticated()),
 * este filtro SÍ rechaza directamente (401/429) porque su ruta está en
 * permitAll — si no lo hiciera, quedaría abierta sin protección real.
 */
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
  private static final String RUTA_PROTEGIDA = "/evidencias/";
  private static final int LIMITE_REQ_POR_MINUTO = 60;

  private final IntegracionApiKeyRepository apiKeyRepository;
  private final ClienteService clienteService;

  // Un bucket de rate-limit por ApiKey.Id — en memoria, suficiente con una
  // sola instancia de backend (sin Redis).
  private final Map<Integer, Bucket> buckets = new ConcurrentHashMap<>();

  public ApiKeyAuthenticationFilter(IntegracionApiKeyRepository apiKeyRepository, ClienteService clienteService) {
    this.apiKeyRepository = apiKeyRepository;
    this.clienteService = clienteService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!request.getRequestURI().contains(RUTA_PROTEGIDA)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String apiKey = request.getHeader("X-Api-Key");
      if (apiKey == null || apiKey.isBlank()) {
        responder401(response, "API_KEY_REQUERIDA", "Falta el header X-Api-Key.");
        return;
      }

      String hash = hashApiKey(apiKey);
      Optional<IntegracionApiKeyModel> apiKeyModel = apiKeyRepository.findByApiKeyHashAndActivoTrue(hash);
      if (apiKeyModel.isEmpty()) {
        logger.warn("[ApiKeyAuthenticationFilter] API Key inválida en {}", request.getRequestURI());
        responder401(response, "API_KEY_INVALIDA", "API Key inválida o inactiva.");
        return;
      }

      Bucket bucket = buckets.computeIfAbsent(apiKeyModel.get().getId(), id -> Bucket.builder()
          .addLimit(Bandwidth.classic(LIMITE_REQ_POR_MINUTO,
              Refill.greedy(LIMITE_REQ_POR_MINUTO, Duration.ofMinutes(1))))
          .build());

      if (!bucket.tryConsume(1)) {
        response.setStatus(429);
        response.setHeader("Retry-After", "60");
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Demasiadas solicitudes, intente de nuevo en un minuto.\"}");
        return;
      }

      String clienteIdParam = request.getParameter("clienteId");
      if (clienteIdParam == null || clienteIdParam.isBlank()) {
        responder401(response, "CLIENTE_ID_REQUERIDO", "Falta el parámetro clienteId.");
        return;
      }

      Integer clienteId;
      try {
        clienteId = Integer.parseInt(clienteIdParam);
      } catch (NumberFormatException e) {
        responder401(response, "CLIENTE_ID_INVALIDO", "clienteId debe ser numérico.");
        return;
      }

      Map<String, Object> cliente = clienteService.findByIdMapped(clienteId);
      if (cliente == null || cliente.get("dbase") == null) {
        responder401(response, "CLIENTE_NO_ENCONTRADO", "No se encontró el cliente indicado.");
        return;
      }

      String clientDb = (String) cliente.get("dbase");
      String clientName = (String) cliente.get("nombre");
      ClientDatabaseContext.setCurrentClient(clientName, clientDb, clienteId);

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          new User("integracion:" + apiKeyModel.get().getNombre(), "", Collections.emptyList()),
          null,
          Collections.emptyList());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);
    } finally {
      ClientDatabaseContext.clear();
    }
  }

  private void responder401(HttpServletResponse response, String codigo, String mensaje) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().write("{\"error\":\"" + codigo + "\",\"message\":\"" + mensaje + "\"}");
  }

  private String hashApiKey(String apiKey) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(apiKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : hash) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      // SHA-256 siempre está disponible en la JVM estándar — no debería pasar nunca.
      throw new IllegalStateException("SHA-256 no disponible", e);
    }
  }
}
