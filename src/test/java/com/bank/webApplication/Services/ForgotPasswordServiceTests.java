package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.RefreshTokenEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.OTPRepository;
import com.bank.webApplication.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordServiceTests {
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
        UUID Uid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        userDto = new UserDto();
        userDto.setName("testuser");
        userDto.setEmail("dummyemail");
        userDto.setPhone("0123456789");
        userDto.setRole(Role.USER);
        userDto.setAddress("dummy address");
        userEntity.setId(Uid);
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setRole(userDto.getRole());

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        otpEntity.setOtpId(id);
        otpEntity.setOtp(00000);
        otpEntity.setExpirationTime(new Date(1000L));
        otpEntity.setUser(userEntity);

    }

    @Test
    public void testverifyEmail() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(userEntity));
        when(otpService.sendOTP(userDto.getEmail(), userEntity)).thenReturn(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        UUID OTPID = forgotPasswordService.verifyEmail(userEntity.getEmail());
        assertEquals((UUID.fromString("123e4567-e89b-12d3-a456-426614174000")), OTPID);

    }

    @Test
    public void testverifyEmail_invalidEmail() {
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.empty());
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            forgotPasswordService.verifyEmail(userEntity.getEmail());
        });
        assertEquals("Please provide a valid email", e.getMessage());
    }

    @Test
    public void testresetPassword() {
        when(otpRepository.findById(otpEntity.getOtpId())).thenReturn(Optional.of(otpEntity));
        authService.updatePassword("newPassword", otpEntity.getUser().getId());
        Boolean res=forgotPasswordService.resetPassword("newPassword",otpEntity.getOtpId());
        assertEquals(true,res);
    }
    @Test
    public void testresetPassword_expiredOtp(){
        when(otpRepository.findById(otpEntity.getOtpId())).thenReturn(Optional.empty());
        Exception e = assertThrows(RuntimeException.class, () -> {
            forgotPasswordService.verifyEmail(userEntity.getEmail());
        });
        assertEquals("Invalid or expired OTP ID", e.getMessage());
    }

}
