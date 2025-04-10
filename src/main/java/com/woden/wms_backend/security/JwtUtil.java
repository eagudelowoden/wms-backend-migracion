package com.woden.wms_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final MacAlgorithm ALGORITMO = Jwts.SIG.HS256; // Algoritmo de firma
    private final SecretKey SECRET_KEY = ALGORITMO.key().build(); // Genera una clave segura
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 horas

    // public String generateToken(String username) {
    // return Jwts.builder()
    // .subject(username) // Establece el subject (usuario)
    // .issuedAt(new Date()) // Establece la fecha de emisión
    // .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //
    // Establece la fecha de expiración
    // .signWith(SECRET_KEY, ALGORITMO) // Firma el token con la clave y el
    // algoritmo
    // .compact(); // Genera el token
    // }

    public String generateToken(String username, String clientName, String clientDb, Integer clientId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("clientDb", clientDb); // Añadimos la BD del cliente al token
        claims.put("clientName", clientName);
        claims.put("clientId", clientId);

        return Jwts.builder()
                .claims(claims) // Añadimos los claims adicionales
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, ALGORITMO)
                .compact();
    }

    // public String extractUsername(String token) {
    // return Jwts.parser()
    // .verifyWith(SECRET_KEY) // Verifica el token con la clave
    // .build()
    // .parseSignedClaims(token) // Parsea el token
    // .getPayload() // Obtiene el payload
    // .getSubject(); // Obtiene el subject (username)
    // }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // método para extraer la BD del cliente del token
    public String extractClientDb(String token) {
        return extractClaim(token, claims -> claims.get("clientDb", String.class));
    }

    public String extractClientName(String token) {
        return extractClaim(token, claims -> claims.get("clientName", String.class));
    }

    public Integer extractClientId(String token) {
        return extractClaim(token, claims -> claims.get("clientId", Integer.class));
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

    ////////// Métodos adicionales para extraer claims específicos /////////
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}