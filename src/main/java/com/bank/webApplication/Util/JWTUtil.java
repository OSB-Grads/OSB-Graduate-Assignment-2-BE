package com.bank.webApplication.Util;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {
    private final String SECRET="supersecretkey";
    private final long EXPIRATION_TIME=1000*60*15;
    private final Key key=Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String username) throws IllegalArgumentException,JwtException{
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateToken(String Token, UserDetails UserName){
        return UserName.equals(extractUsername(Token)) && !isTokenExpired(Token);
    }
    public boolean isTokenExpired(String Token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(Token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

}
