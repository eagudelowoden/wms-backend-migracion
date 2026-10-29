package com.woden.wms_backend.config.DataSource;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.woden.wms_backend.services.WmsWdGeneral.ClienteService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Fija la base de datos del cliente para las rutas públicas de aprobación de
 * Autogestión (/public/autogestion/aprobacion/{clienteId}/...), igual que hace
 * ApiKeyAuthenticationFilter con las integraciones: el contexto se resuelve en el
 * filtro, ANTES de que el controlador abra su EntityManager, para que las
 * consultas vayan a la base del cliente y no a WmsWdGeneral.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class AutogestionPublicContextFilter extends OncePerRequestFilter {
  private static final Pattern RUTA = Pattern.compile("^.*/public/autogestion/aprobacion/(\\d+)/.*$");

  private final ClienteService clienteService;

  public AutogestionPublicContextFilter(ClienteService clienteService) {
    this.clienteService = clienteService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Matcher matcher = RUTA.matcher(request.getRequestURI());
    if (!matcher.matches()) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      Integer clienteId = Integer.valueOf(matcher.group(1));
      Map<String, Object> cliente = clienteService.findByIdMapped(clienteId);
      if (cliente == null || cliente.get("dbase") == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      }
      ClientDatabaseContext.setCurrentClient((String) cliente.get("nombre"), (String) cliente.get("dbase"), clienteId);
      filterChain.doFilter(request, response);
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } finally {
      ClientDatabaseContext.clear();
    }
  }
}
