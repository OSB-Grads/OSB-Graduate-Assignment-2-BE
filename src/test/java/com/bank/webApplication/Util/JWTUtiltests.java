package com.bank.webApplication.Util;
import com.bank.webApplication.Entity.Role;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import static java.security.KeyRep.Type.SECRET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
@ExtendWith(MockitoExtension.class)
public class JWTUtiltests {
    JWTUtil jwtUtil;
    String SECRET;
    @BeforeEach
    void setup(){
        jwtUtil=new JWTUtil();
         SECRET="testseceretkey1234567890123456789012";
        ReflectionTestUtils.setField(jwtUtil,"SECRET",SECRET);
    }
    @Test
    void testgeneratetoken(){
        String token= jwtUtil.generateToken("testuserid",Role.USER.name());
        //assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        var claims=Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
        assertEquals("testuserid",claims.getSubject());
        assertEquals(Role.USER.name(),claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
    @Test
    void testgeneratetokenillegalArgument_idNull() {
        assertThrows(IllegalArgumentException.class,()-> jwtUtil.generateToken(null,Role.USER.name()));
    }
    @Test
    void testgeneratetokenillegalArgument_RoleisNull(){
        assertThrows(IllegalArgumentException.class,()-> jwtUtil.generateToken("testuserid",null));
    }
    @Test
    void testgeneratetoken_jwtexception(){
        String token=jwtUtil.generateToken("testuserid",Role.USER.name());
        assertThrows(JwtException.class,()->{
            Jwts.parser()
                    .setSigningKey("wrongkey")
                    .parseClaimsJws(token);
        });
    }
    @Test
    void testExtractUserName(){
            String expected="testname";
            String token= jwtUtil.generateToken(expected,Role.USER.name());
            String actual= jwtUtil.extractUsername(token);
            //assert
            assertEquals(expected,actual);
    }
    private UserDetails userDetails(String Username){
        return User.withUsername(Username)
                .password("dummyPassword")
                .roles(Role.USER.name())
                .build();
    }
    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
    private String generateToken(String userId, String role, boolean expired) {
        Date now = new Date();
        Date expiryDate = expired
                ? new Date(now.getTime() - 1000)
                : new Date(now.getTime() + 1000 * 60 * 10);

        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, getKey())
                .compact();
    }
    @Test
    void testValidateToken(){
        UserDetails user=userDetails("testuserid");
        String token=generateToken("testuserid",Role.USER.name(), false);

        boolean isValid= jwtUtil.validateToken(token,user);
        //assert
        assertTrue(isValid);
    }
    @Test
    void testValidateToken_Invalid(){
        UserDetails user=userDetails("testuserid");
        String token=generateToken("wronguserid",Role.USER.name(), false);

        boolean isValid= jwtUtil.validateToken(token,user);
        //assert
        assertFalse(isValid);
    }
    @Test
    void testValidateToken_Expired(){
        UserDetails user=userDetails("testuserid");
        String token=generateToken("testuserid",Role.USER.name(), true);
        boolean isValid= jwtUtil.validateToken(token,user);
        //assert
        assertFalse(isValid);
    }
}



