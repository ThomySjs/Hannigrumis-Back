package com.Hannigrumis.api.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
            .subject(username)
            .claim("type", "authorization")
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
            .signWith(key)
            .compact();
    }

    public String generateCustomToken(String email, String type, Integer expMs) {
        return Jwts.builder()
            .subject(email)
            .claim("type", type)
            .issuedAt(new Date())
            .expiration(new Date((new Date().getTime() + expMs)))
            .signWith(key)
            .compact();
    }

    public String getUsernameFromToken(String jwt) {
        return Jwts.parser()
            .verifyWith(key).build()
            .parseSignedClaims(jwt)
            .getPayload()
            .getSubject();
    }

    public Claims getClaimsFromToken(String jwt) {
        return Jwts.parser()
            .verifyWith(key).build()
            .parseSignedClaims(jwt)
            .getPayload();
    }

    public boolean isConfirmationToken(String jwt) {
        Claims claims = this.getClaimsFromToken(jwt);
        if (claims.get("type").equals("confirmation")) {
            return true;
        }
        return false;
    }

    public boolean isRecoveryToken(String jwt) {
        Claims claims = this.getClaimsFromToken(jwt);
        if (claims.get("type").equals("recovery")) {
            return true;
        }
        return false;
    }

    public String getEmailFronRecoveryToken(String jwt) {
        if (!this.isRecoveryToken(jwt)) {
            return null;
        }
        return this.getUsernameFromToken(jwt);
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if  (headerAuth != null && headerAuth.startsWith("Bearer")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
