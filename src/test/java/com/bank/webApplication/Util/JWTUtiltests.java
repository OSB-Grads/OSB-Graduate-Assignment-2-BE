package com.bank.webApplication.Util;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.RefreshTokenEntity;
import com.bank.webApplication.Entity.Role;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static java.security.KeyRep.Type.SECRET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;




@ExtendWith(MockitoExtension.class)
class JWTUtiltests {
    private AuthEntity authentity;
    JWTUtil jwtUtil;
    String SECRET;

    @BeforeEach
    void setup() {
        jwtUtil = new JWTUtil();
        SECRET = "testseceretkey1234567890123456789012";
        ReflectionTestUtils.setField(jwtUtil, "SECRET", SECRET);
    }

    @Test
    void testgeneratetoken() {
        String token = jwtUtil.generateToken("testuserid", Role.USER.name());
        //assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        var claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
        assertEquals("testuserid", claims.getSubject());
        assertEquals(Role.USER.name(), claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void  testgenerateRefreshToken() {
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        authentity = AuthEntity.builder()
                .id(id)
                .username("testuser")
                .password("existingPassword")
                .role(Role.USER)
                .build();
        RefreshTokenEntity Refreshtoken = jwtUtil.generateRefreshToken(authentity);
        String token=Refreshtoken.toString();
        assertNotNull(Refreshtoken);
        assertFalse(token.isEmpty());
        var claims= RefreshTokenEntity.builder()
                .refreshToken("SampleRefreshToken")
                .authEntity(authentity)
                .expiry(Instant.now().plusSeconds(1000 * 60*2))//expiry time of 1 day
//        1 * 24 * 60 * 60
                .build();
        assertEquals("SampleRefreshToken", claims.getRefreshToken());
    }

    @Test
    void testgeneratetokenillegalArgument_idNull() {
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken(null, Role.USER.name()));
    }

    @Test
    void testgeneratetokenillegalArgument_RoleisNull() {
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken("testuserid", null));
    }

    @Test
    void testgeneratetoken_jwtexception() {
        String token = jwtUtil.generateToken("testuserid", Role.USER.name());
        assertThrows(JwtException.class, () -> {
            Jwts.parser()
                    .setSigningKey("wrongkey")
                    .parseClaimsJws(token);
        });
    }

    @Test
    void testExtractUserName() {
        String expected = "testname";
        String token = jwtUtil.generateToken(expected, Role.USER.name());
        String actual = jwtUtil.extractUsername(token);
        //assert
        assertEquals(expected, actual);
    }

    private UserDetails userDetails(String Username) {
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
    void testValidateToken() {
        UserDetails user = userDetails("testuserid");
        String token = generateToken("testuserid", Role.USER.name(), false);

        boolean isValid = jwtUtil.validateToken(token, user);
        //assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_Invalid() {
        UserDetails user = userDetails("testuserid");
        String token = generateToken("wronguserid", Role.USER.name(), false);

        boolean isValid = jwtUtil.validateToken(token, user);
        //assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_Expired() {
        UserDetails user = userDetails("testuserid");
        String token = generateToken("testuserid", Role.USER.name(), true);
        boolean isValid = jwtUtil.validateToken(token, user);
        //assert
        assertFalse(isValid);
    }
}



