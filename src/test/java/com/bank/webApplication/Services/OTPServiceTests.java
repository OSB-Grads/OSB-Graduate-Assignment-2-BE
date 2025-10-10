package com.bank.webApplication.Services;

import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.OTPRepository;
import com.bank.webApplication.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OTPServiceTests {
    @Mock
    private OTPRepository otpRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;
    @Mock
    private UserEntity userEntity;
    @Mock
    private OTPEntity otpEntity;
    @Mock
    private OTPService otpService;
    @Mock
    private UserDto userDto;
    @Mock
    private ForgotPasswordService forgotPasswordService;
    private UUID id;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        otpEntity=new OTPEntity();
        otpEntity.setOtpId(id);
        otpEntity.setOtp(00000);
        otpEntity.setExpirationTime(new Date(System.currentTimeMillis() + 60 * 1000));
    }
    @Test
    void testverifyOtp(){
        when(otpRepository.findById(id)).thenReturn(Optional.of(otpEntity));
        boolean res=otpService.verifyOtp(id,00000);
        assertTrue(res);
    }
    @Test
    void testverifyOtp_expired(){
        otpEntity.setExpirationTime(new Date(System.currentTimeMillis() - 60 * 1000));
        lenient().when(otpRepository.findById(id)).thenReturn(Optional.of(otpEntity));
        boolean res=otpService.verifyOtp(id,00000);
        assertFalse(res);

    }
    @Test
    void testverifyOtp_invalid(){
        lenient().when(otpRepository.findById(id)).thenReturn(Optional.of(otpEntity));
        boolean res=otpService.verifyOtp(id,00000);
        assertFalse(res);
    }
    @Test
    void testverifyotp_invalidId(){
        when(otpRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> otpService.verifyOtp(id, 00000));
    }


}
