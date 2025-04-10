package com.ecommerce.ft_ecom.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${project.secretKey}")
    private String secretKey;

    @Value("${project.time}")
    private Long expirationTime;

    public String generateToken(String userDetails){
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expirationTime))
                .setSubject(userDetails)
                .signWith(secKey())
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String username = getUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return expiration(token).before(new Date());
    }

    private Date expiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    public String getUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private Claims getAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private <T> T extractClaims(String token, Function<Claims, T> resolver){
        Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    private Key secKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
