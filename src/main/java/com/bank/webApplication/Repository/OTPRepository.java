package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OTPRepository  extends JpaRepository<OTPEntity, UUID> {
//    @Query("SELECT otpEntity from OTP_Storage otpEntity where otpEntity.otp = ?1 and otpEntity.user = ?2")
    Optional<OTPEntity> findByOtp(Integer otp);
}
