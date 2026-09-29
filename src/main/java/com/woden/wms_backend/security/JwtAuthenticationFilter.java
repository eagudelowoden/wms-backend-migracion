package com.woden.wms_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteService;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, ClienteService clienteService) {
    this.jwtUtil = jwtUtil;
  }

  @SuppressWarnings("null")
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String authHeader = request.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      String token = authHeader.substring(7);

      String username;
      try {
        username = jwtUtil.extractUsername(token);
      } catch (io.jsonwebtoken.ExpiredJwtException e) {
        // Token vencido: situación normal, responder 401 sin ensuciar los logs
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"TOKEN_EXPIRED\",\"message\":\"La sesión ha expirado.\"}");
        return;
      } catch (io.jsonwebtoken.JwtException e) {
        // Token inválido/corrupto: también 401, sin stack trace
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"INVALID_TOKEN\",\"message\":\"Token inválido.\"}");
        return;
      }

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if (jwtUtil.validateToken(token)) {
          String clientName = jwtUtil.extractClientName(token);
          Integer clientId = jwtUtil.extractClientId(token);
          String clientDb = jwtUtil.extractClientDb(token);

          if (clientName != null && clientDb != null && clientId != null) {
            ClientDatabaseContext.setCurrentClient(clientName, clientDb, clientId);
          } else {
            ClientDatabaseContext.setCurrentClient("WmsWdGeneral", "WmsWdGeneral", 0);
            ClientDatabaseContext.useGeneralDatabase();
          }

          // Configurar autenticación Spring Security
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              new User(username, "", Collections.emptyList()),
              null,
              Collections.emptyList());
          authentication.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }

      filterChain.doFilter(request, response);
    } finally {
      // Limpieza garantizada del contexto
      ClientDatabaseContext.clear();
    }
  }
}