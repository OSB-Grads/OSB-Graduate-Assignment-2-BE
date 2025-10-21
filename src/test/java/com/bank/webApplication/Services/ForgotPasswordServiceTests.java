package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Entity.*;
import com.bank.webApplication.Repository.OTPRepository;
import com.bank.webApplication.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordServiceTests {

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;

    @Mock
    private OTPRepository otpRepository;

    @Mock private AuthService authService;
    @Mock private LogService logService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OTPService otpService;

    public UserEntity user1;
    public UUID testOtpId = UUID.randomUUID() ;

    @BeforeEach
    void setup(){
        user1 = new UserEntity(UUID.randomUUID(),"TestUser","testUser@gmail.com","123456789","20/10/2025","25/10/2025","Bangalore", Role.USER);
    }

    // Test - verifyEmail - User exists
    @Test
    void testVerifyEmail_UserExists(){
        OTPEntity otpEntity = new OTPEntity(testOtpId,111222,new Date(System.currentTimeMillis()),user1);

       when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
       when(otpService.sendOTP(user1.getEmail(),user1)).thenReturn(testOtpId);

       UUID result = forgotPasswordService.verifyEmail(user1.getEmail());

       assertThat(result).isNotNull();
       assertEquals(testOtpId,result);
       verify(userRepository).findByEmail(user1.getEmail());
       verify(otpService).sendOTP(user1.getEmail(),user1);
    }

    // Test - verifyEmail - User doesnt exist
    @Test
    void testVerifyEmail_UserNotFound(){
        String testUserId = UUID.randomUUID().toString();
        String testEmail = "randomEmail@gmail.com";
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> forgotPasswordService.verifyEmail(testEmail));

        assertEquals("Please provide a valid email",ex.getMessage());

        verify(userRepository).findByEmail(testEmail);
        verifyNoInteractions(otpService);
    }

    // Test - verifyEmail - UserEmail is null
    @Test
    void testVerifyEmail_EmailNull(){

        String testEmail = "";
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> forgotPasswordService.verifyEmail(testEmail));
        assertEquals("Please provide a valid email",ex.getMessage());

        verify(userRepository).findByEmail(testEmail);
        verifyNoInteractions(otpService);
    }

    //Test - resetPassword - Success
    @Test
    void testResetPassword_Success(){
        String newPassword = "newPassword@123";
        OTPEntity otpEntity = new OTPEntity(testOtpId,112233,new Date(),user1);

        when(otpRepository.findById(testOtpId)).thenReturn(Optional.of(otpEntity));

        boolean result = forgotPasswordService.resetPassword(newPassword,testOtpId);

        assertTrue(result);
        verify(otpRepository).findById(testOtpId);
        verify(authService).updatePassword(newPassword,user1.getId());
        verify(logService).logintoDB(user1.getId(),
                LogEntity.Action.AUTHENTICATION,
                "Password Reset SUCCESS",
                user1.getEmail(),
                LogEntity.Status.SUCCESS);
    }

    //Test - resetPassword - Invalid OtpId
    @Test
    void testResetPassword_InvalidOtpId() {

        UUID invalidOtpId = UUID.randomUUID();
        String newPassword = "somePassword@123";

        when(otpRepository.findById(invalidOtpId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> forgotPasswordService.resetPassword(newPassword, invalidOtpId));

        assertEquals("Invalid or expired OTP ID", ex.getMessage());

        verify(otpRepository).findById(invalidOtpId);
        verifyNoInteractions(authService);
        verifyNoInteractions(logService);
    }

    //Test - resetPassword - User in OTPEntity is null
    @Test
    void testResetPassword_UserNull() {

        String newPassword = "testPassword";

        OTPEntity otpEntity = new OTPEntity(testOtpId, 123456, new Date(), null);

        when(otpRepository.findById(testOtpId)).thenReturn(Optional.of(otpEntity));

        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> forgotPasswordService.resetPassword(newPassword, testOtpId));

        assertThat(ex).isInstanceOf(NullPointerException.class);

        verify(otpRepository).findById(testOtpId);
        verifyNoInteractions(authService);
        verifyNoInteractions(logService);
    }

    //Test - resetPassword - Auth Service throws Exception
    @Test
    void testResetPassword_AuthServiceException(){

        String newPassword = "testPassword";
        OTPEntity otpEntity = new OTPEntity(testOtpId,222333,new Date(),user1);

        when(otpRepository.findById(testOtpId)).thenReturn(Optional.of(otpEntity));
        doThrow(new RuntimeException("AuthService failure"))
                .when(authService).updatePassword(newPassword,user1.getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                ()-> forgotPasswordService.resetPassword(newPassword,testOtpId));

        assertEquals("AuthService failure",ex.getMessage());
        verify(otpRepository).findById(testOtpId);
        verify(authService).updatePassword(newPassword,user1.getId());
        verifyNoInteractions(logService);

    }

}
