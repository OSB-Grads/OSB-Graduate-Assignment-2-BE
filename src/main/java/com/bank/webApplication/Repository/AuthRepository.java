package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity, UUID> {
}
