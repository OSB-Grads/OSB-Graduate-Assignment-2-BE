package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.InvalidCredentialsException;
import com.bank.webApplication.Dto.MailBodyDTO;
import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.OTPRepository;
import com.bank.webApplication.Util.EmailService;
import com.bank.webApplication.Util.OTPGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OTPServiceTests {

    @InjectMocks
    private OTPService otpService;

    @Mock
    private OTPGenerator otpGenerator;

    @Mock
    private OTPRepository otpRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private MailBodyDTO mailBodyDTO;

    public OTPEntity otpEntity;
    public UserEntity testUser, testOtpUser;

    @BeforeEach
    void setup() {
        UserEntity testUser = new UserEntity();
        UUID userId = UUID.randomUUID();
        testUser.setId(userId);
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        testUser.setPhone("1234567890");

        UUID otpId = UUID.randomUUID();
        otpEntity = new OTPEntity(otpId, 111111, new Date(System.currentTimeMillis()), testUser);

        testOtpUser = new UserEntity();
    }

    // Test - sendOTP - Create OTP and Send Email
    @Test
    void testSendOtp_CreateSuccess() {
        testOtpUser.setEmail("otpUser@gmail.com");
        int generatedOtp = 123456;
        UUID otpId1 = UUID.randomUUID();

        when(otpGenerator.generateOtp()).thenReturn(generatedOtp);
        when(otpRepository.findByUser(testOtpUser)).thenReturn(Optional.empty());

        OTPEntity savedOtpEntity = new OTPEntity();
        savedOtpEntity.setOtpId(otpId1);
        when(otpRepository.save(any(OTPEntity.class))).thenReturn(savedOtpEntity);

        UUID result = otpService.sendOTP(testOtpUser.getEmail(), testOtpUser);

        assertEquals(otpId1, result);
        verify(otpGenerator, times(1)).generateOtp();
        verify(otpRepository, times(1)).findByUser(testOtpUser);
        verify(emailService, times(1)).sendMessage(any(MailBodyDTO.class));
        verify(otpRepository, times(1)).save(any(OTPEntity.class));
    }

    // Test - sendOTP - Send Email and Update OTP
    @Test
    void testSendOtp_UpdateSuccess() {
        Integer newOtp = 234567;

        when(otpGenerator.generateOtp()).thenReturn(newOtp);
        when(otpRepository.findByUser(testUser)).thenReturn(Optional.of(otpEntity));
        when(otpRepository.save(otpEntity)).thenReturn(otpEntity);

        UUID result = otpService.sendOTP(testOtpUser.getEmail(), testUser);

        assertEquals(otpEntity.getOtpId(), result);
        assertEquals(newOtp, otpEntity.getOtp());
        verify(otpGenerator, times(1)).generateOtp();
        verify(emailService, times(1)).sendMessage(any(MailBodyDTO.class));
        verify(otpRepository, times(1)).save(otpEntity);
    }

    //Test - sendOtp - Email Fails
    @Test
    void testSendOtp_EmailFails() {

        Integer newOtp = 777888;
        testOtpUser.setEmail("randomUser@gmail.com");

        when(otpGenerator.generateOtp()).thenReturn(newOtp);
        when(otpRepository.findByUser(testOtpUser)).thenReturn(Optional.empty());

        doThrow(new RuntimeException("Email sending Failed"))
                .when(emailService).sendMessage(any(MailBodyDTO.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> otpService.sendOTP(testOtpUser.getEmail(), testOtpUser));
        assertEquals("Email sending Failed", ex.getMessage());

        verify(otpRepository, never()).save(any());
    }

    // Test - verifyOtp - Success
    @Test
    void  tetsVerifyOtp_Success(){
        Integer newOtp = 777888;
        otpEntity.setOtp(newOtp);
        otpEntity.setExpirationTime(new Date(System.currentTimeMillis() + 60_000));

        when(otpRepository.findById(otpEntity.getOtpId())).thenReturn(Optional.of(otpEntity));

        boolean result = otpService.verifyOtp(otpEntity.getOtpId(),newOtp);

        assertTrue(result);
        verify(otpRepository).findById(otpEntity.getOtpId());
    }

    // Test - verifyOtp - Invalid OTP
    @Test
    void  tetsVerifyOtp_InvalidOtp(){

        UUID otpId = UUID.randomUUID();
        when(otpRepository.findById(otpId)).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> otpService.verifyOtp(otpId, 123456));

        assertEquals("Invalid OTP ID", exception.getMessage());
    }


    // Test - verifyOtp - Wrong OTP
    @Test
    void  tetsVerifyOtp_WrongOtp(){

        otpEntity.setExpirationTime(new Date(System.currentTimeMillis() + 60_000));

        when(otpRepository.findById(otpEntity.getOtpId())).thenReturn(Optional.of(otpEntity));

        boolean result = otpService.verifyOtp(otpEntity.getOtpId(),222333);

        assertFalse(result);
    }

    // Test - verifyOtp - Expired OTP
    @Test
    void  tetsVerifyOtp_ExpiredOtp(){
        otpEntity.setOtp(222333);
        otpEntity.setExpirationTime(new Date(System.currentTimeMillis() - 60_000));

        when(otpRepository.findById(otpEntity.getOtpId())).thenReturn(Optional.of(otpEntity));

        boolean result = otpService.verifyOtp(otpEntity.getOtpId(),222333);

        assertFalse(result);
    }


    //Test - isExpired - with past Date
    @Test
    void testIsExpired_pastDate() {

        Date pastDate = new Date(System.currentTimeMillis() - 2 * 60 * 1000);

        boolean result = otpService.isExpired(pastDate);

        assertTrue(result, "Expected true for an expiration time in the past");
    }

    //Test - isExpired - with Future date
    @Test
    void testIsExpired_futureDate() {
        Date futureDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);

        boolean result = otpService.isExpired(futureDate);

        assertFalse(result, "Expected false for an expiration time in the future");
    }

    //Test - isExpired - with current date
    @Test
    void testIsExpired_currentDate() {
        Date currentDate = new Date(System.currentTimeMillis());

        boolean result = otpService.isExpired(currentDate);

        assertFalse(result, "Expected false for an expiration time in the current");
    }

    //T

    // Test - cleanupExpiredOTP - otp Exists
    @Test
    void testCleanupExpiredOTP_WithOtp() {
        List<OTPEntity> otpList = new ArrayList<>();
        otpList.add(otpEntity);

        when(otpRepository.findAll()).thenReturn(otpList);

        otpService.cleanupExpiredOTP();

        verify(otpRepository, times(1)).findAll();
        verify(otpRepository, times(1)).deleteByExpirationTimeBefore(any(Date.class));
    }

    // Test - cleanupExpiredOTP - otp doesn't Exist
    @Test
    void testCleanupExpiredOTP_NoOtp() {
        List<OTPEntity> otpList = new ArrayList<>();

        when(otpRepository.findAll()).thenReturn(otpList);

        otpService.cleanupExpiredOTP();

        verify(otpRepository, times(1)).findAll();
        verify(otpRepository, never()).deleteByExpirationTimeBefore(any(Date.class));
    }
}
