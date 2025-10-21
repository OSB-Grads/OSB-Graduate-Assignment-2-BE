package com.bank.webApplication.Controllers;

import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.ResetPasswordDTO;
import com.bank.webApplication.Dto.VerifyOtpRequestDTO;
import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Services.ForgotPasswordService;
import com.bank.webApplication.Services.OTPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordControllerTests {

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;

    @Mock private ForgotPasswordService forgotPasswordService;

    @Mock private UserRepository userRepository;

    @Mock private OTPService otpService;

    public OTPEntity otpEntity1, otpEntity2;
    public UUID otpId1 = UUID.randomUUID(), otpId2 = UUID.randomUUID();
    public UserEntity user1, user2;

    @BeforeEach
    public void setup() {
        user1 = new UserEntity(UUID.randomUUID(), "TestUser1", "test@gmail.com", "1234567890", "24/10/2025", "30/10/2025", "Mumbai", Role.USER);
        user2 = new UserEntity(UUID.randomUUID(), "TestUser2", "testUser@gmail.com", "1234567890", "24/10/2025", "30/10/2025", "Delhi", Role.USER);

        otpEntity1 = new OTPEntity(otpId1, 111111, new Date(System.currentTimeMillis() + 5 * 60 * 1000), user1);
        otpEntity2 = new OTPEntity(otpId2, 222222, new Date(System.currentTimeMillis() + 5 * 60 * 1000), user2);
    }

    //Test - verifyEmail - When user Exists
    @Test
    public void testVerifyEmail_Success() {
        when(forgotPasswordService.verifyEmail(user1.getEmail())).thenReturn(otpId1);

        ResponseEntity<?> result = forgotPasswordController.verifyEmail(user1.getEmail());

        assertThat(result).isNotNull();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertNotNull(body);
        assertEquals("OTP sent successfully to User email.", body.get("message"));
        assertEquals(otpId1, body.get("otpId"));
        verify(forgotPasswordService).verifyEmail(user1.getEmail());
    }

    //Test - verifyEmail - When user not found
    @Test
    public void testVerifyEmail_UserNotFound() {
        String testEmail = "randomUser@gmail.com";
        when(forgotPasswordService.verifyEmail(testEmail))
                .thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> {
            forgotPasswordController.verifyEmail(testEmail);
        });
        verify(forgotPasswordService).verifyEmail(testEmail);
    }

    //Test - verifyEmail - When email is null
    @Test
    public void testVerifyEmail_NullEmail() {

        when(forgotPasswordService.verifyEmail(null))
                .thenThrow(new UserNotFoundException("Email is Null"));

        assertThrows(UserNotFoundException.class, () -> {
            forgotPasswordController.verifyEmail(null);
        });
    }

    //Test - verifyEmail - When email is empty
    @Test
    public void testVerifyEmail_EmptyEmail() {
        String testEmail = "";
        when(forgotPasswordService.verifyEmail(testEmail))
                .thenThrow(new UserNotFoundException("Email cannot be Empty"));

        UserNotFoundException ex= assertThrows(UserNotFoundException.class, () -> {
            forgotPasswordController.verifyEmail(testEmail);
        });

        assertEquals("Email cannot be Empty",ex.getMessage());
    }

    //Test - resendOtp - success
    @Test
    public void testResendOtp_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(otpService.sendOTP(user1.getEmail(),user1)).thenReturn(otpId1);

        ResponseEntity<?> result = forgotPasswordController.resendOtp(user1.getEmail());

        assertThat(result).isNotNull();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertNotNull(body);
        assertEquals("OTP resent successfully", body.get("message"));
        assertEquals(otpId1, body.get("otpId"));
        verify(userRepository).findByEmail(user1.getEmail());
        verify(otpService).sendOTP(user1.getEmail(),user1);
    }

    //Test - resendOtp - When Email not found
    @Test
    public void testResendOtp_UserNotFound() {
        String testEmail = "randomUser@gmail.com";
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());


        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            forgotPasswordController.resendOtp(testEmail);
        });
        assertEquals("Please provide the valid email",ex.getMessage());
        verify(userRepository).findByEmail(testEmail);
        verifyNoInteractions(otpService);
    }

    // Test - ResendOtp - OTPService Exception
    @Test
    public void testResendOtp_OtpServiceFails(){
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(otpService.sendOTP(user1.getEmail(),user1))
                .thenThrow(new RuntimeException("Failed to send OTP"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                ()-> forgotPasswordController.resendOtp(user1.getEmail()));

        assertEquals("Failed to send OTP",ex.getMessage());
        verify(userRepository).findByEmail(user1.getEmail());
        verify(otpService).sendOTP(user1.getEmail(),user1);
    }

    //Test - verifyOtp - success
    @Test
    public void testVerifyOtp_Success() {
        Integer otp = 111111;
        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO();
        request.setOtpId(otpId1);
        request.setOtp(otp);

        when(otpService.verifyOtp(otpId1,otp)).thenReturn(true);

        ResponseEntity<?> result = forgotPasswordController.verifyOtp(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody());
        verify(otpService).verifyOtp(otpId1,otp);
    }

    //Test - verifyOtp - Wrong OTP
    @Test
    public void testVerifyOtp_WrongOtp() {
        Integer otp = 222333;
        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO();
        request.setOtpId(otpId1);
        request.setOtp(otp);

        when(otpService.verifyOtp(otpId1,otp)).thenReturn(false);

        ResponseEntity<?> result = forgotPasswordController.verifyOtp(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse((Boolean) result.getBody());
        verify(otpService).verifyOtp(otpId1,otp);
    }

    //Test - verifyOtp - OTP Service Exception
    @Test
    public void testVerifyOtp_OtpServiceFails() {
        Integer otp = 222333;

        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO();
        request.setOtpId(otpId1);
        request.setOtp(otp);

        when(otpService.verifyOtp(otpId1,otp)).thenThrow(new RuntimeException("OTP expired"));

        RuntimeException ex = assertThrows(RuntimeException.class ,
                ()-> forgotPasswordController.verifyOtp(request));

        assertEquals("OTP expired",ex.getMessage());
        verify(otpService).verifyOtp(otpId1,otp);
    }

    //Test - resetPassword - Success
    @Test
    public void testResetPassword_Success(){
        String newPassword = "newPassword@123";
        ResetPasswordDTO request = new ResetPasswordDTO();
        request.setOtpId(otpId1);
        request.setPassword(newPassword);

        when(forgotPasswordService.resetPassword(newPassword,otpId1)).thenReturn(true);

        ResponseEntity<?> result = forgotPasswordController.resetPass(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean)result.getBody());
        verify(forgotPasswordService).resetPassword(newPassword,otpId1);

    }

    //Test - resetPassword - User not found , OTP expired
    @Test
    public void testResetPassword_Fails(){
        String newPassword = "wrondPassword";
        ResetPasswordDTO request = new ResetPasswordDTO();
        request.setOtpId(otpId1);
        request.setPassword(newPassword);

        when(forgotPasswordService.resetPassword(newPassword,otpId1)).thenReturn(false);

        ResponseEntity<?> result = forgotPasswordController.resetPass(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse((Boolean)result.getBody());
        verify(forgotPasswordService).resetPassword(newPassword,otpId1);

    }

    //Test - resetPassword - OTPService Exception
    @Test
    public void testResetPassword_OtpServiceFails() {
        String newPassword = "newPassword@123";

        ResetPasswordDTO request = new ResetPasswordDTO();
        request.setOtpId(otpId1);
        request.setPassword(newPassword);

        when(forgotPasswordService.resetPassword(newPassword,otpId1))
                .thenThrow(new RuntimeException("Invalid or expired OTP"));

        RuntimeException ex = assertThrows(RuntimeException.class ,
                ()-> forgotPasswordController.resetPass(request));

        assertEquals("Invalid or expired OTP",ex.getMessage());
        verify(forgotPasswordService).resetPassword(newPassword,otpId1);
    }

    //Test - resetPassword - Null Password, Empty Password
    @Test
    public void testResetPassword_NullPassword(){
        ResetPasswordDTO request = new ResetPasswordDTO();
        request.setOtpId(otpId1);
        request.setPassword("");

        when(forgotPasswordService.resetPassword("",otpId1)).thenReturn(false);

        ResponseEntity<?> result = forgotPasswordController.resetPass(request);

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertFalse((Boolean) result.getBody());
    }
}
