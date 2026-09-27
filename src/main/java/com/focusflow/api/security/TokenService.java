package com.focusflow.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.focusflow.api.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret:}")
    private String secret;

    @Value("${api.security.token.expiration-hours:24}")
    private Long expirationHours = 24L;

    private static final String ISSUER = "focusflow-api";
    private static final String FALLBACK_SECRET = "focusflow-secret-key-for-development-purposes-1234567890";

    public String generateToken(Usuario usuario) {
        try {
            String key = (this.secret != null && !this.secret.isBlank()) ? this.secret : FALLBACK_SECRET;
            Algorithm algorithm = Algorithm.HMAC256(key);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getIdUsuario().toString())
                    .withClaim("nome", usuario.getNmUsuario())
                    .withClaim("role", usuario.getRole())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token) {
        try {
            String key = (this.secret != null && !this.secret.isBlank()) ? this.secret : FALLBACK_SECRET;
            Algorithm algorithm = Algorithm.HMAC256(key);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    private Instant generateExpirationDate() {
        long hours = (this.expirationHours != null) ? this.expirationHours : 24L;
        return LocalDateTime.now().plusHours(hours).toInstant(ZoneOffset.of("-03:00"));
    }
}
