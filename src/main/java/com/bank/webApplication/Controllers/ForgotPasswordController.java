package com.bank.webApplication.Controllers;

import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.ResetPasswordDTO;
import com.bank.webApplication.Dto.VerifyOtpRequestDTO;

import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Services.ForgotPasswordService;
import com.bank.webApplication.Services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/v1/forgotPassword")
public class ForgotPasswordController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ForgotPasswordService forgotPasswordService;


    // send otp for user mail and store otpId in FE
    @PostMapping(path = "/{email}")
    public ResponseEntity<?> verifyEmail(@PathVariable String email) {
        System.out.println("Entering into controller");
        UUID otpId = forgotPasswordService.verifyEmail(email);
        return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully to User email.",
                "otpId", otpId));
    }

    // resend otp
    @PutMapping(path = "/resendOtp/{email}")
    public ResponseEntity<?> resendOtp(@PathVariable String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Please provide the valid email"));
        UUID otpId = otpService.sendOTP(email, user);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "OTP resent successfully",
                "otpId", otpId));
    }

    // verify otp
    @PostMapping(path = "/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO) {
        boolean result = otpService.verifyOtp(
                verifyOtpRequestDTO.getOtpId(),
                verifyOtpRequestDTO.getOtp());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // reset password
    @PutMapping(path = "/resetPassword")
    public ResponseEntity<?> resetPass(@RequestBody ResetPasswordDTO resetPasswordDTO) {

        boolean result = forgotPasswordService.resetPassword(
                resetPasswordDTO.getPassword(),
                resetPasswordDTO.getOtpId());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
