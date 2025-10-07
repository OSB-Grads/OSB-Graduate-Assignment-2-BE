package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.MailBodyDTO;
import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.OTPRepository;
import com.bank.webApplication.Util.EmailService;
import com.bank.webApplication.Util.OTPGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private boolean isExpired(Date expirationTime) {
        return expirationTime.before(new Date());
    }


    public UUID sendOTP(String email, UserEntity user){
        Integer otp = otpGenerator.generateOtp();

        MailBodyDTO mailBodyDTO = MailBodyDTO.builder()
                .to(email)
                .subject("OTP for Forgot Password Request")
                .text("Welcome to the Banking Application .You have forgot your Password :( . This is the OTP for your forgot Password Request. Please do not forget next time :|" + otp).build();

        OTPEntity otpEntity = OTPEntity.builder().otp(otp).expirationTime(new Date(System.currentTimeMillis() + 60 * 1000)) // 1 minute
                .user(user).build();

        emailService.sendMessage(mailBodyDTO);
        OTPService.log.info("[OTP SERVICE] OTP Sent Successfully");

        OTPEntity savedOtp = otpRepository.save(otpEntity);
        return savedOtp.getOtpId();
    }

    public boolean verifyOtp(UUID otpId, Integer otp) {

        OTPEntity otpEntity = otpRepository.findById(otpId)
                .orElseThrow(() -> new RuntimeException("Invalid OTP ID"));

        if (otpEntity.getOtp().equals(otp) && !isExpired(otpEntity.getExpirationTime())) {
            return true;
        }
        return false;
    }

}
