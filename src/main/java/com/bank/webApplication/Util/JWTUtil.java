package com.bank.webApplication.Util;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.RefreshTokenEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;


@Slf4j
@Component
public class JWTUtil {
    @Value("${spring.jwt.secretkey}")
    private String SECRET;
    private final long EXPIRATION_TIME = 3000 * 60 ;
//1000 * 60 * 15

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }


    //method to generate refresh token
    public RefreshTokenEntity generateRefreshToken(AuthEntity authEntity) {
        log.info("[JWTUtil] generateRefreshToken SUCCESS");
        return RefreshTokenEntity.builder()
                .refreshToken(UUID.randomUUID().toString())
                .authEntity(authEntity)
                .expiry(Instant.now().plusSeconds( 60*5))//expiry time of 1 day
//        1 * 24 * 60 * 60
                .build();

    }


    //method to generate JWT token
    public String generateToken(String userId, String role) throws IllegalArgumentException, JwtException {
        if (role == null) {
            log.info("[JWTUtil] generateToken:Role should not be null FAILURE");
            throw new IllegalArgumentException("Role should not be null");
        }
        if (userId == null) {
            log.info("[JWTUtil] generateToken:UserId Should not be null FAILURE");
            throw new IllegalArgumentException("UserId Should not be null");
        }
        log.info("[JWTUtil] generateToken SUCCESS");
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //method to extractUsername
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    //method to extractClaims
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    //method to extractAllclaims
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
            log.info("[JWTUtil] validateToken SUCCESS");
            return usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.info("[JWTUtil] validateToken FAILURE");
            return false;
        }
    }


    //method to check expiration of the token
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
