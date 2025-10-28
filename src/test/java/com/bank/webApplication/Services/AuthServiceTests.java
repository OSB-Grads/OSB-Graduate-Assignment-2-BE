package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.RefreshTokenExpired;
import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Dto.JwtResponseDto;
import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.*;
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
    private LogService logService;
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
        id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        authEntity = AuthEntity.builder()
                .id(id)
                .username("testuser")
                .password("HashedPassword")
                .role(Role.USER)
                .build();
        refreshTokenEntity = RefreshTokenEntity.builder()
                .refreshToken(UUID.randomUUID().toString())
                .authEntity(authEntity)
                .expiry(Instant.now().plusSeconds(2))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // Repository & JWT mocks
    }

    //test for signup
    @Test
    void testSignUp() {
        authDto = new AuthDto();
        authDto.setUsername("testuser");
        authDto.setPassword("testPassword");
        MockedStatic<PasswordHash> mockedStaticPassword = Mockito.mockStatic(PasswordHash.class);
        mockedStaticPassword.when(() -> PasswordHash.HashPass("testpassword"))
                .thenReturn("HashedPassword");
        // Static method mocking
//             Repository & JWT mocks
        when(authRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(any(String.class), eq(Role.USER.name()))).thenReturn("SignUpJwtToken");
        when(jwtUtil.generateRefreshToken(any(AuthEntity.class))).thenReturn(refreshTokenEntity);
        JwtResponseDto response = authService.Signup(authDto);
        // Assertions
        assertNotNull(response);
        assertEquals("SignUpJwtToken", response.getToken());
        assertNotNull(response.getRefreshToken());
        //verify
        verify(logService, times(1)).logintoDB(eq(id), eq(LogEntity.Action.AUTHENTICATION),
                eq("User Signup Successfull"), eq("testuser"), eq(LogEntity.Status.SUCCESS));


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
        // Mock repository
        when(authRepository.findByUsername("testuser")).thenReturn(Optional.of(authEntity));
        when(passwordEncoder.matches("PlainPassword", authEntity.getPassword())).thenReturn(true);
        // Mock JWT
        when(jwtUtil.generateToken(any(String.class), eq(Role.USER.name()))).thenReturn("LoginJwtToken");
        when(jwtUtil.generateRefreshToken(any(AuthEntity.class))).thenReturn(refreshTokenEntity);
        JwtResponseDto response = authService.Login(authDto);
        // Assert
        assertNotNull(response);
        assertEquals("LoginJwtToken", response.getToken());
        assertNotNull(response.getRefreshToken());
        //verify
        verify(logService, times(1)).logintoDB(any(), eq(LogEntity.Action.AUTHENTICATION),
                eq("User Logged in Successfully"), eq("testuser"), eq(LogEntity.Status.SUCCESS));
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
        refreshTokenEntity.setExpiry(Instant.now().minusSeconds(200));
        // Mock repository methods
        when(refreshTokenRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken()))
                .thenReturn(Optional.of(refreshTokenEntity));
        when(refreshTokenRepository.deleteByRefreshToken(refreshTokenEntity.getRefreshToken()))
                .thenReturn(1);
        assertThrows(RefreshTokenExpired.class,
                () -> authService.RefreshAccessToken(refreshTokenEntity.getRefreshToken()));
        verify(refreshTokenRepository, times(1))
                .deleteByRefreshToken(refreshTokenEntity.getRefreshToken());
        verify(refreshTokenRepository, times(1)).flush();
    }



    @Test
    void updatePassword() {
        when(authRepository.findById(id)).thenReturn(Optional.of(authEntity));
        MockedStatic<PasswordHash> mockedStaticPassword = Mockito.mockStatic(PasswordHash.class);
        mockedStaticPassword.when(() -> PasswordHash.HashPass("Updatepassword"))
                .thenReturn("updatedHashedPassword");
        authService.updatePassword("Updatepassword", id);
        assertEquals("updatedHashedPassword", authEntity.getPassword());
        //verify
        verify(logService, times(1)).logintoDB(eq(id), eq(LogEntity.Action.PROFILE_MANAGEMENT),
                eq("Password Updation SUCCESS"), eq(id.toString()), eq(LogEntity.Status.SUCCESS));

    }
}
