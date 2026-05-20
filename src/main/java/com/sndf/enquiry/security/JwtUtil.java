package com.sndf.enquiry.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final String SECRET =
            "mysupersecretkeymysupersecretkey";

    private final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

   
    
    public String generateToken(
            String username,
            String role,
            String department) {

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("department", department)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                + 1000 * 60 * 60 * 10))
                .signWith(
                        key,
                        SignatureAlgorithm.HS256)
                .compact();
    }
    

    public String extractUsername(String token) {

        return extractClaims(token)
                .getSubject();
    }

    public boolean validateToken(
            String token,
            String username) {

        String extractedUsername =
                extractUsername(token);

        return extractedUsername
                .equals(username);
    }

    public Claims extractClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    
    
    
    
    
}