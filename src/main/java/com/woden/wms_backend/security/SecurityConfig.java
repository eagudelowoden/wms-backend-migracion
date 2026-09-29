package com.woden.wms_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
      ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable()) // Deshabilita CSRF
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita CORS
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/general/users/login", "/api/general/users/login").permitAll()
            .requestMatchers("/general/version", "/api/general/version").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api-reference/**").permitAll()
            .requestMatchers("/general/ingreso/**").permitAll()
            .requestMatchers("/general/clientes/**").permitAll()
            .requestMatchers("/general/users/ping").permitAll()
            .requestMatchers("/general/avisos/activo").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            // Rutas de integración externa (sistema-a-sistema, sin JWT/usuario) —
            // permitAll acá porque quien las protege de verdad es
            // ApiKeyAuthenticationFilter (X-Api-Key), no este filtro de sesión.
            .requestMatchers("/evidencias/**").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(apiKeyAuthenticationFilter, JwtAuthenticationFilter.class)
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedOrigins(List.of("http://18.217.246.39:8081",
        "https://wms.woden.com.co", "https://wms.woden.com.co:9000", "http://wms.woden.com.co",
        "https://wmstest.woden.com.co", "https://wmstest.woden.com.co:9000", "http://wmstest.woden.com.co",
        "https://wmstestecu.woden.com.co", "https://wmstestecu.woden.com.co:9005", "http://wmstestecu.woden.com.co",
        "http://18.217.246.39:9000", "http://localhost:4200", "http://52.14.166.232:8443",
         "http://52.14.166.232:8084","http://52.14.166.232:8084", "http://52.14.166.232:9005",
        "https://woden-wts-dev.arkade.com.co","http://localhost:9001","http://localhost:8084",
        "https://wmstest.woden.com.co/admin",
        "http://woden-wts-dev.arkade.com.co", "http://52.14.166.232:*", "http://localhost:5173",
        "http://woden-wts-dev.arkade.com.co:443"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}