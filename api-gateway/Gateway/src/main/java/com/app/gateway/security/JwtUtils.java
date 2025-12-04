package com.app.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    private final SecretKey secretKey;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }

    public String getUserId(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public String getJti(String token) {
        Object jti = parseToken(token).getBody().get("jti");
        return jti != null ? jti.toString() : null;
    }

    public boolean isExpired(String token) {
        Date exp = parseToken(token).getBody().getExpiration();
        return exp.before(new Date());
    }
}
