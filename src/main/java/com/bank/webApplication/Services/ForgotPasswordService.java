package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.InvalidCredentialsException;
import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.MailBodyDTO;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.OTPRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Util.EmailService;
import com.bank.webApplication.Util.OTPGenerator;
import com.bank.webApplication.Util.PasswordHash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ForgotPasswordService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private LogService logService;


    public UUID verifyEmail(String email) {
        log.info("[Verify Email] entered");
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("[Verify Email] User doesn't exist in Database");
                    return new UserNotFoundException("Please provide a valid email");
                });

        log.info("[Verify Email] User found in Database");

        UUID otpId = otpService.sendOTP(email, user);
//        logService.logintoDB(user.getId(), LogEntity.Action.PROFILE_MANAGEMENT, "OTP Sent Successfully", user.getName(), LogEntity.Status.SUCCESS);
        return otpId;
    }


    public boolean resetPassword(String password, UUID otpId) {

        UserEntity user = otpRepository.findById(otpId)
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP ID"))
                .getUser();
        UUID userId = user.getId();

        authService.updatePassword(password, userId);
        log.info("[RESET PASSWORD] Password Reset SUCCESS");
//        logService.logintoDB(userId, LogEntity.Action.PROFILE_MANAGEMENT, "Password Reset SUCCESS", user.getEmail(), LogEntity.Status.SUCCESS);

        return true;
    }
}
