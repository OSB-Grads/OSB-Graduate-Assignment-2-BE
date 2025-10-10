package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OTPRepositoryTests {

    @Mock
    private OTPRepository otpRepository;

    // Test findByOtp - OTP Found
    @Test
    void testFindByOtp_Found() {
        Integer otp = 123456;
        OTPEntity otpEntity = createTestOtpEntity(otp);

        when(otpRepository.findByOtp(otp)).thenReturn(Optional.of(otpEntity));

        Optional<OTPEntity> foundOtp = otpRepository.findByOtp(otp);

        assertThat(foundOtp).isPresent();
        assertThat(foundOtp.get().getOtp()).isEqualTo(otp);
        assertThat(foundOtp.get().getUser().getEmail()).isEqualTo("testuser@example.com");
    }

    // Test findByOtp - OTP Not Found
    @Test
    void testFindByOtp_NotFound() {
        Integer invalidOtp = 999999;

        when(otpRepository.findByOtp(invalidOtp)).thenReturn(Optional.empty());

        Optional<OTPEntity> foundOtp = otpRepository.findByOtp(invalidOtp);

        assertThat(foundOtp).isEmpty();
    }

    // Helper method to create OTPEntity for testing
    private OTPEntity createTestOtpEntity(Integer otp) {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPhone("1234567890");
        // set other user fields if needed

        OTPEntity otpEntity = new OTPEntity();
        otpEntity.setOtpId(UUID.randomUUID());
        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); // 30 minutes ahead
        otpEntity.setUser(user);

        return otpEntity;
    }
}
