package com.bank.webApplication.Services;


import com.bank.webApplication.CustomException.InvalidCredentialsException;
import com.bank.webApplication.Dto.MailBodyDTO;
import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.OTPRepository;
import com.bank.webApplication.Util.EmailService;
import com.bank.webApplication.Util.OTPGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class OTPService {
    @Autowired
    private OTPGenerator otpGenerator;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPRepository otpRepository;

    public boolean isExpired(Date expirationTime) {
        log.info("[OTPService]Entered into isExpired");
        return expirationTime.before(new Date());
    }

    @Transactional
    public UUID sendOTP(String email, UserEntity user) {
        log.info("[OTPService] Entered into sendOTP");
        Integer otp = otpGenerator.generateOtp();

        OTPEntity otpEntity = otpRepository.findByUser(user)
                .orElse(new OTPEntity());
        otpEntity.setUser(user);
        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(new Date(System.currentTimeMillis() + 240 * 1000));

        MailBodyDTO mailBodyDTO = MailBodyDTO.builder()
                .to(email)
                .subject("OTP for Forgot Password Request")
                .text("Welcome to the Banking Application .\n\n\n" +
                        "   We received a request to reset the password for your account associated with this email.\n " +
                        "Please use the following One-Time Password (OTP) to proceed with the password reset :  " + otp +
                        "\n\nThis OTP is valid for the next [5 minutes].\n" +
                        "If you did not request a password reset, please ignore this email. No changes have been made to your account. ").build();


        emailService.sendMessage(mailBodyDTO);
        OTPService.log.info("[OTPService] sendOTP SUCCESS ");

        System.out.println(otpEntity);

        OTPEntity savedOtp = otpRepository.save(otpEntity);
        return savedOtp.getOtpId();
    }

    public boolean verifyOtp(UUID otpId, Integer otp) {
        log.info("[OTPService] verifyOtp entered SUCCESS");
        OTPEntity otpEntity = otpRepository.findById(otpId)
                .orElseThrow(() -> {
                    log.error("[O`TPService] verifyOtp : Invalid OTP ID FAILURE ");
                    return new InvalidCredentialsException("Invalid OTP ID");
                });

        if (otpEntity.getOtp().equals(otp) && !isExpired(otpEntity.getExpirationTime())) {
            log.info("[OTPService] verifyOtp  SUCCESS ");
            return true;
        }
        log.error("[OTPService] verifyOtp  FAILURE ");
        return false;
    }

    @Scheduled(fixedRate = 600_000)
    @Transactional
    public void cleanupExpiredOTP() {
        if (! otpRepository.findAll().isEmpty()) {
            otpRepository.deleteByExpirationTimeBefore(new Date());
            log.info("[OTP Service] cleanupExpiredOTP SUCCESS");
        }
        else {
            log.info("[OTP Service]No OTPs found, skipping cleanup.");
        }
    }
}
