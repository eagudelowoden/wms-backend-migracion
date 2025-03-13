package com.woden.wms_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final MacAlgorithm ALGORITMO = Jwts.SIG.HS256; // Algoritmo de firma
    private final SecretKey SECRET_KEY = ALGORITMO.key().build(); // Genera una clave segura
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 horas

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // Establece el subject (usuario)
                .issuedAt(new Date()) // Establece la fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Establece la fecha de expiración
                .signWith(SECRET_KEY, ALGORITMO) // Firma el token con la clave y el algoritmo
                .compact(); // Genera el token
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY) // Verifica el token con la clave
                .build()
                .parseSignedClaims(token) // Parsea el token
                .getPayload() // Obtiene el payload
                .getSubject(); // Obtiene el subject (username)
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(SECRET_KEY) // Verifica el token con la clave
                    .build()
                    .parseSignedClaims(token); // Parsea el token
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}