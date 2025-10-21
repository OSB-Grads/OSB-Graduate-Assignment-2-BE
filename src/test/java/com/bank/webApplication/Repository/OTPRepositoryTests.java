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
import static org.mockito.Mockito.*;

//Optional<OTPEntity> findByOtp(Integer otp);
//Optional<OTPEntity> findByUser(UserEntity user);
//void deleteByExpirationTimeBefore(Date now);

@ExtendWith(MockitoExtension.class)
public class OTPRepositoryTests {

    @Mock
    private OTPRepository otpRepository;

    private OTPEntity createTestOtpEntity(Integer otp) {
        UserEntity user = new UserEntity();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPhone("1234567890");

        OTPEntity otpEntity = new OTPEntity();
        otpEntity.setOtpId(UUID.randomUUID());
        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); // 30 minutes ahead
        otpEntity.setUser(user);

        return otpEntity;
    }

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

    //Test findByUser - User Found
    @Test
    void testFindByUser_Found(){
        OTPEntity otpEntity = createTestOtpEntity(111111);

        when(otpRepository.findByUser(otpEntity.getUser())).thenReturn(Optional.of(otpEntity));

        Optional<OTPEntity> result = otpRepository.findByUser(otpEntity.getUser());

        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isEqualTo(otpEntity.getUser());
        assertThat(result.get().getUser().getName()).isEqualTo("Test User");
        assertThat(result.get().getUser().getEmail()).isEqualTo(otpEntity.getUser().getEmail());
    }

    //Test findByUser - User Not Found
    @Test
    void testFindByUser_NotFound(){
        UserEntity testUser = new UserEntity();
        testUser.setName("Unknown User");
        testUser.setEmail("unknown@example.com");

        when(otpRepository.findByUser(testUser)).thenReturn(Optional.empty());

        Optional<OTPEntity> result = otpRepository.findByUser(testUser);

        assertThat(result).isNotPresent();
    }

    //Test - deleteByExpirationTimeBefore(Date now);
    @Test
    void testDeleteByExpirationTimeBefore() {
        // Arrange
        Date now = new Date();

        otpRepository.deleteByExpirationTimeBefore(now);

        verify(otpRepository, times(1)).deleteByExpirationTimeBefore(now);
    }


}
