package com.portal.solicitacoes.internas.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import com.portal.solicitacoes.internas.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String createToken(User user) {

        return createToken(
                user.getId(),
                user.getEmail()
        );
    }


    public String createToken(UUID id, String email) {

        SecretKey key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.builder()
                .subject(email)
                .claim("userId", id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {

        try {

            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(
                            secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            return null;
        }
    }

    public String validationToken(String token) {

        Claims claims = getClaims(token);

        if (claims == null) {
            return null;
        }

        return claims.getSubject();
    }

    public String getRole(String token) {

        Claims claims = getClaims(token);

        if (claims == null) {
            return null;
        }

        return claims.get("role", String.class);
    }
}