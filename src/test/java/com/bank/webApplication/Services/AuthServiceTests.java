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
    private  RefreshTokenEntity refreshTokenEntity;

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
    void setUp(){
        MockitoAnnotations.openMocks(this);
//        id=UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
//        when(authDto.getUsername()).thenReturn("testuser");
//        when(authDto.getPassword()).thenReturn("testpassword");
//
//
//        when(authEntity.getId()).thenReturn(id);
//        when(authEntity.getRole()).thenReturn(Role.USER);
    }

    //test for signup
    @Test
    void testSignUp(){


            id=UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            authEntity = AuthEntity.builder().role(Role.ADMIN).id(id).build();
            authDto = new AuthDto();
            authDto.setUsername("testuser");
            authDto.setPassword("password");
            MockedStatic<PasswordHash> mockedStaticPassword = Mockito.mockStatic(PasswordHash.class);
            // Static method mocking
            mockedStaticPassword.when(() -> PasswordHash.HashPass("testpassword"))
                    .thenReturn("BcryptHashedPassword");
            // Repository & JWT mocks
            when(authRepository.findByUsername("testuser")).thenReturn(Optional.empty());
            when(jwtUtil.generateToken(any(String.class), eq(Role.USER.name()) )).thenReturn("SignUpJWTtoken");
            JwtResponseDto response = authService.Signup(authDto);
            // Assertions
            assertNotNull(response);
            assertEquals("SignUpJWTtoken", response.getToken());


    }
    @Test
    void testSignUp_UserAlreadyExists() {


            UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            authEntity = AuthEntity.builder()
                    .id(id)
                    .username("testuser")
                    .password("existingPassword")
                    .role(Role.USER)
                    .build();
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
    void testLogin(){

            UUID id=UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            authEntity=AuthEntity.builder()
                    .id(id)
                    .username("testuser")
                    .password("HashedPassword")
                    .role(Role.USER)
                    .build();
            authDto = new AuthDto();
            authDto.setUsername("testuser");
            authDto.setPassword("PlainPassword");
            when(passwordEncoder.matches("PlainPassword","HashedPassword")).thenReturn(true);
            when(authRepository.findByUsername("testuser")).thenReturn(Optional.of(authEntity));
            when(jwtUtil.generateToken(any(String.class), eq(Role.USER.name()) )).thenReturn("LoginJwtToken");
            JwtResponseDto response=authService.Login(authDto);
            assertNotNull(response);
            assertEquals("LoginJwtToken",response.getToken());

    }
    @Test
    void testLogin_UserNotfound(){
        authDto=new AuthDto();
        authDto.setUsername("testuser");
        authDto.setPassword("plainpassword");

        when(authRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Exception e=assertThrows(RuntimeException.class,()->{
            authService.Login(authDto);
        });
        assertEquals("Invalid UserName or User not found",e.getMessage());
    }
    @Test
    void testLogin_InvalidPassword(){
        authDto=new AuthDto();
        authDto.setUsername("testuser");
        authDto.setPassword("invalidpassword");
        authEntity=AuthEntity.builder()
                .id(id)
                .username("testuser")
                .password("correctHashedPassword")
                .role(Role.USER)
                .build();
        when(authRepository.findByUsername("testuser")).thenReturn(Optional.of(authEntity));
        when(passwordEncoder.matches("invalidpassword","correctHashedPassword")).thenReturn(false);
        Exception e=assertThrows(RuntimeException.class,()->{
            authService.Login(authDto);
        });
        assertEquals("Invalid PassWord",e.getMessage());
    }
    @Test
    void  LogOut(){
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        authEntity = AuthEntity.builder()
                .id(id)
                .username("testuser")
                .password("existingPassword")
                .role(Role.USER)
                .build();
        RefreshTokenEntity Refreshtoken = jwtUtil.generateRefreshToken(authEntity);
        var claims= RefreshTokenEntity.builder()
                .refreshToken("SampleRefreshToken")
                .authEntity(authEntity)
                .expiry(Instant.now().plusSeconds(1000 * 60*2))//expiry time of 1 day
//        1 * 24 * 60 * 60
                .build();
        when(refreshTokenRepository.deleteByRefreshToken("SampleRefreshToken")).thenReturn(1);
    }
}
