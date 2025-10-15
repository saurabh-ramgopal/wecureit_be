package com.example.wecureit_be.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:change_this_to_a_long_random_value_at_least_32_bytes}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpirationInMs;

    private Key getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception ex) {
            keyBytes = secret.getBytes();
        }
        // JJWT requires HMAC keys to be at least 256 bits (32 bytes) for HS256.
        // If the provided secret is shorter, derive a 32-byte key by hashing it with SHA-256.
        if (keyBytes.length < 32) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
                keyBytes = md.digest(keyBytes);
            } catch (Exception e) {
                // fallback: if hashing unexpectedly fails, pad the array to 32 bytes
                byte[] padded = new byte[32];
                System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
                keyBytes = padded;
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    public String getSubjectFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // reuse parsing helper which handles multiple jjwt versions via reflection
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        try {
            // Try the modern API: Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            java.lang.reflect.Method parserBuilderMethod = Jwts.class.getMethod("parserBuilder");
            Object parserBuilder = parserBuilderMethod.invoke(null);
            java.lang.reflect.Method setSigningKeyMethod = parserBuilder.getClass().getMethod("setSigningKey", Key.class);
            Object configuredBuilder = setSigningKeyMethod.invoke(parserBuilder, getSigningKey());
            java.lang.reflect.Method buildMethod = configuredBuilder.getClass().getMethod("build");
            Object parser = buildMethod.invoke(configuredBuilder);
            java.lang.reflect.Method parseClaimsJwsMethod = parser.getClass().getMethod("parseClaimsJws", String.class);
            Object jws = parseClaimsJwsMethod.invoke(parser, token);
            java.lang.reflect.Method getBodyMethod = jws.getClass().getMethod("getBody");
            return (Claims) getBodyMethod.invoke(jws);
        } catch (NoSuchMethodException nsme) {
            try {
                // Try legacy API reflectively: Jwts.parser().setSigningKey(key).parseClaimsJws(token)
                java.lang.reflect.Method parserMethod = Jwts.class.getMethod("parser");
                Object parser = parserMethod.invoke(null);
                java.lang.reflect.Method setSigningKeyMethod2 = parser.getClass().getMethod("setSigningKey", Key.class);
                Object configuredParser = setSigningKeyMethod2.invoke(parser, getSigningKey());
                java.lang.reflect.Method parseClaimsJwsMethod2 = configuredParser.getClass().getMethod("parseClaimsJws", String.class);
                Object jws = parseClaimsJwsMethod2.invoke(configuredParser, token);
                java.lang.reflect.Method getBodyMethod2 = jws.getClass().getMethod("getBody");
                return (Claims) getBodyMethod2.invoke(jws);
            } catch (NoSuchMethodException | IllegalStateException e) {
                throw new RuntimeException("No compatible jjwt parser available", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
