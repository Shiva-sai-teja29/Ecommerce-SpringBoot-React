package com.ecommerce.ft_ecom.security.jwt;

import com.ecommerce.ft_ecom.security.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${project.jwtCookie}")
    private String jwtCookie;

    public String generateToken(String userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + expirationTime);
        System.out.println("Generating token: issuedAt=" + issuedAt + ", expiration=" + expiration);
        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setSubject(userDetails)
                .signWith(secKey())
                .compact();
    }

    public boolean validateToken(String token, UserDetailsImpl userDetails) {
        String username = getUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return expiration(token).before(new Date());
    }

    private Date expiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String getUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setAllowedClockSkewSeconds(60)
                .setSigningKey(secKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    private Key secKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}

//    public String getJwtFromCookies(HttpServletRequest request) {
//        if (request.getCookies() != null) {
//            return Arrays.stream(request.getCookies())
//                    .filter(cookie -> jwtCookie.equals(cookie.getName()))
//                    .map(Cookie::getValue)
//                    .findFirst()
//                    .orElse(null);
//        } else {
//          return null;
//        }
//    }
//
//    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
//        String jwt = generateToken(userPrincipal.getUsername());
//        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
//                .path("/api")
//                .maxAge(expirationTime/ 1000)
//                .httpOnly(true)
//                .sameSite("Strict")
//                .build();
//        return cookie;
//    }
//
//    public ResponseCookie getCleanJwtCookie() {
//        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
//                .path("/api")
//                .build();
//        return cookie;
//    }

