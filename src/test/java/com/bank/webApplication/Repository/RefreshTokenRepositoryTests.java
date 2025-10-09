package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.RefreshTokenEntity;
import com.bank.webApplication.Entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenRepositoryTests {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RefreshTokenEntity refreshTokenEntity;
    @Mock
    private AuthEntity authEntity;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        authEntity = AuthEntity.builder()
                .id(id)
                .username("testuser")
                .password("HashedPassword")
                .role(Role.USER)
                .build();
        refreshTokenEntity = RefreshTokenEntity.builder()
                .refreshToken("RefreshToken")
                .authEntity(authEntity)
                .expiry(Instant.now().plusSeconds(604800))
                .build();
    }

    @Test
    void testsaveandfindByRefreshToken() {
        when(refreshTokenRepository.save(refreshTokenEntity)).thenReturn(refreshTokenEntity);
        //mock behaviour for findByUsername
        when(refreshTokenRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken())).thenReturn(Optional.of(refreshTokenEntity));
        RefreshTokenEntity token=refreshTokenRepository.save(refreshTokenEntity);
        Optional<RefreshTokenEntity> foundtoken=refreshTokenRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken());
        //assert
        assertThat(token).isNotNull();
        assertThat(foundtoken).isPresent();
        assertThat(foundtoken.get().getRefreshToken()).isEqualTo("RefreshToken");
    }
    @Test
    void testfindByRefreshToken_NotFound(){
        //Mock Behaviour
        when(refreshTokenRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken())).thenReturn(Optional.empty());
        Optional<RefreshTokenEntity> foundtoken=refreshTokenRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken());
        //assert
        assertThat(foundtoken).isEmpty();
    }
    @Test
    void testdeleteByRefreshToken(){
        refreshTokenRepository.deleteByRefreshToken(refreshTokenEntity.getRefreshToken());
        Optional<RefreshTokenEntity> token=refreshTokenRepository.findByRefreshToken(refreshTokenEntity.getRefreshToken());
        assertThat(token).isNotPresent();
    }
}
