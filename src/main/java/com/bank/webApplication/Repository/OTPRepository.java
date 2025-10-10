package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface OTPRepository  extends JpaRepository<OTPEntity, UUID> {

    Optional<OTPEntity> findByOtp(Integer otp);
    Optional<OTPEntity> findByUser(UserEntity user);

    void deleteByExpirationTimeBefore(Date now);
}
