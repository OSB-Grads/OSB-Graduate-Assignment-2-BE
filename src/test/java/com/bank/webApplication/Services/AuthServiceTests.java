package com.bank.webApplication.Services;

import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Dto.JwtResponseDto;
import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.RefreshTokenEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.RefreshTokenRepository;
import com.bank.webApplication.Util.JWTUtil;
import com.bank.webApplication.Util.PasswordHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @Mock
    private AuthRepository authRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordHash passwordHash;
    @Mock
    private RefreshTokenEntity refreshTokenEntity;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
//    @Autowired
    public AuthService authService;

    private UUID id;
    @Mock
    private AuthDto authDto;
    @Mock
    private AuthEntity authEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        authEntity = AuthEntity.builder()
                .id(id)
                .username("testuser")
                .password("HashedPassword")
                .role(Role.USER)
                .build();
        refreshTokenEntity = RefreshTokenEntity.builder()
                .refreshToken(UUID.randomUUID().toString())
                .authEntity(authEntity)
                .expiry(Instant.now().plusSeconds(604800))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // Repository & JWT mocks
    }

    //test for signup
    @Test
    void testSignUp() {
        authDto = new AuthDto();
        authDto.setUsername(authEntity.getUsername());
        authDto.setPassword(authEntity.getPassword());
        MockedStatic<PasswordHash> mockedStaticPassword = Mockito.mockStatic(PasswordHash.class);
        // Static method mocking
//             Repository & JWT mocks
        when(authRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        mockedStaticPassword.when(() -> PasswordHash.HashPass("testpassword"))
                .thenReturn("HashedPassword");
        JwtResponseDto response = authService.Signup(authDto);
        // Assertions
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());


    }

    @Test
    void testSignUp_UserAlreadyExists() {
        authDto = new AuthDto();
        authDto.setUsername("testuser");
        authDto.setPassword("password");
        // Mock repository to return existing user
        when(authRepository.findByUsername("testuser")).thenReturn(Optional.of(authEntity));
        // Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.Signup(authDto);
        });
        //  verify exception message
        assertEquals("User Already Exist", exception.getMessage());
    }
    @Test
    void testLogin() {
        authDto = new AuthDto();
        authDto.setUsername("testuser");
        authDto.setPassword("PlainPassword");
        when(passwordEncoder.matches("PlainPassword", "HashedPassword")).thenReturn(true);
        when(authRepository.findByUsername("testuser")).thenReturn(Optional.of(authEntity));
        when(jwtUtil.generateToken(any(String.class), eq(Role.USER.name()))).thenReturn("LoginJwtToken");
        when(jwtUtil.generateRefreshToken(authEntity)).thenReturn(refreshTokenEntity);
        JwtResponseDto response = authService.Login(authDto);
        assertNotNull(response);
        assertEquals("LoginJwtToken", response.getToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void testLogin_UserNotfound() {
        authDto = new AuthDto();
        authDto.setUsername("testuser");
        authDto.setPassword("plainpassword");

        when(authRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Exception e = assertThrows(RuntimeException.class, () -> {
            authService.Login(authDto);
        });
        assertEquals("Invalid UserName or User not found", e.getMessage());
    }

    @Test
    void testLogin_InvalidPassword() {
        authDto = new AuthDto();
        authDto.setUsername("testuser");
        authDto.setPassword("invalidpassword");
        when(authRepository.findByUsername("testuser")).thenReturn(Optional.of(authEntity));
        when(passwordEncoder.matches("invalidpassword", "HashedPassword")).thenReturn(false);
        Exception e = assertThrows(RuntimeException.class, () -> {
            authService.Login(authDto);
        });
        assertEquals("Invalid PassWord", e.getMessage());
    }

    @Test
    void LogOut() {

        when(refreshTokenRepository.deleteByRefreshToken(refreshTokenEntity.getRefreshToken())).thenReturn(1);
        authService.LogOut(refreshTokenEntity.getRefreshToken());
    }

    @Test
    void RefreshAccessToken_shouldProvideNewToken() {
        when(refreshTokenRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken())).thenReturn(Optional.of(refreshTokenEntity));
        when(jwtUtil.generateToken(authEntity.getId().toString(), authEntity.getRole().toString())).thenReturn("new-jwt-access-token");
        JwtResponseDto response = authService.RefreshAccessToken(refreshTokenEntity.getRefreshToken());
        assertEquals("new-jwt-access-token", response.getToken());
        assertEquals(refreshTokenEntity.getRefreshToken(), response.getRefreshToken());
    }

    @Test
    void RefreshAccessToken_tokenexpired() {
        refreshTokenEntity.setExpiry(Instant.now().minusSeconds(10));
        when(refreshTokenRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken())).thenReturn(Optional.of(refreshTokenEntity));
        assertThrows(RuntimeException.class, () -> authService.RefreshAccessToken(refreshTokenEntity.getRefreshToken()));
    }
}
