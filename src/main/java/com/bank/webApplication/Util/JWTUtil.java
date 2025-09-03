package com.bank.webApplication.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${spring.jwt.secret}")
    private  String SECRET;
    private final long EXPIRATION_TIME=1000*60*15;
    private final Key key=Keys.hmacShaKeyFor(SECRET.getBytes());
    //method to generate JwtToken
    public String generateToken(String username) throws IllegalArgumentException,JwtException{
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }
    //method to extract UserName from the JwtToken
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    //method to validate JwtToken
    public boolean validateToken(String Token, UserDetails UserName){
        return UserName.equals(extractUsername(Token)) && !isTokenExpired(Token);
    }
    //method to check expiration of the token
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
