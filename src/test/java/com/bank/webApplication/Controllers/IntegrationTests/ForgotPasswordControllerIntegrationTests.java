package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Entity.*;
import com.bank.webApplication.Repository.*;
import com.bank.webApplication.Util.PasswordHash;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ForgotPasswordControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private OTPRepository otpRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserEntity testUser;
    private AuthEntity authUser;
    private OTPEntity testOtp;
    private String jwtToken;
    private String refreshToken;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        authRepository.deleteAll();

        authUser = new AuthEntity(UUID.randomUUID(), "testUser", PasswordHash.HashPass("oldPassword123"), Role.USER);
        authRepository.save(authUser);

        testUser = new UserEntity();
        testUser.setId(authUser.getId());
        testUser.setName("Forgot User");
        testUser.setEmail(authUser.getUsername() + "@example.com");
        testUser = userRepository.save(testUser);

        // Create OTP
        testOtp = new OTPEntity();
        testOtp.setOtp(123456);
        testOtp.setExpirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        testOtp.setUser(testUser);
        testOtp = otpRepository.save(testOtp);

        // Login to get JWT
        String loginJson = String.format("""
                {
                  "username": "%s",
                  "password": "oldPassword123"
                }
                """, authUser.getUsername());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = loginResult.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
    }

    // Test sending OTP for valid email
    @Test
    void testVerifyEmail_Success() throws Exception {
        mockMvc.perform(post("/api/v1/forgotPassword/{email}", testUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OTP sent successfully to User email.")))
                .andExpect(jsonPath("$.otpId", notNullValue()));
    }

    //Invalid email
    @Test
    void testVerifyEmail_UserNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/forgotPassword/{email}", "unknown@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    // Resend OTP
    @Test
    void testResendOtp_Success() throws Exception {
        mockMvc.perform(put("/api/v1/forgotPassword/resendOtp/{email}", testUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OTP resent successfully")))
                .andExpect(jsonPath("$.otpId", notNullValue()));
    }

    // Resend OTP for unknown email
    @Test
    void testResendOtp_UserNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/forgotPassword/resendOtp/{email}", "unknown@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Verify OTP success
    @Test
    void testVerifyOtp_Success() throws Exception {
        String verifyOtpJson = String.format("""
                {
                    "otpId": "%s",
                    "otp": 123456
                }
                """, testOtp.getOtpId());

        mockMvc.perform(post("/api/v1/forgotPassword/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(verifyOtpJson))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    // Invalid OTP value
    @Test
    void testVerifyOtp_Failure() throws Exception {
        String verifyOtpJson = String.format("""
                {
                    "otpId": "%s",
                    "otp": 999999
                }
                """, testOtp.getOtpId());

        mockMvc.perform(post("/api/v1/forgotPassword/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(verifyOtpJson))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // Reset password success
    @Test
    void testResetPassword_Success() throws Exception {
        String resetPasswordJson = String.format("""
                {
                    "password": "newSecurePassword123",
                    "otpId": "%s"
                }
                """, testOtp.getOtpId());

        mockMvc.perform(put("/api/v1/forgotPassword/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetPasswordJson))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Verify password updated in DB
        AuthEntity updatedAuth = authRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newSecurePassword123", updatedAuth.getPassword()));
    }

    // Invalid OTP ID during reset
    @Test
    void testResetPassword_InvalidOtp() throws Exception {
        String resetPasswordJson = """
                {
                    "password": "newSecurePassword123",
                    "otpId": "00000000-0000-0000-0000-000000000000"
                }
                """;

        mockMvc.perform(put("/api/v1/forgotPassword/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetPasswordJson))
                .andExpect(status().is5xxServerError());
    }


}
