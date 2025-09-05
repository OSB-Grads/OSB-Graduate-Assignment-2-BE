package com.bank.webApplication.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JWTUtil {
    @Value("${spring.jwt.secretkey}")
    private String SECRET;
    private final long EXPIRATION_TIME=1000*60*15;

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }



    public String generateToken(String userId, String role) throws IllegalArgumentException,JwtException{
        if(role == null) throw new IllegalArgumentException("Role should not be null");
        if(userId==null) throw new IllegalArgumentException("UserId Should not be null");
        return Jwts.builder()
                .setSubject(userId)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(getKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){

        return extractClaim(token,Claims::getSubject);
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //method to validate JwtToken
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String usernameFromToken = extractUsername(token);
            return usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }
        catch (JwtException e){
            return false;
        }
    }
    //method to check expiration of the token
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
