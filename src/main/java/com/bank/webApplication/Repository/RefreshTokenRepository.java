package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String Token);
    void delete(AuthEntity authEntity);
}
