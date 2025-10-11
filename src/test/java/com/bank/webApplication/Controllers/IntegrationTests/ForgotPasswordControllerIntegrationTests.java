package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.OTPRepository;
import com.bank.webApplication.Repository.UserRepository;
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
    private AuthEntity testAuth;
    private OTPEntity testOtp;
    private String jwtToken;

    @BeforeAll
    void setup() throws Exception {
        otpRepository.deleteAll();
        userRepository.deleteAll();
        authRepository.deleteAll();

        testAuth = new AuthEntity();
        testAuth.setUsername("forgotUser");
        testAuth.setPassword(PasswordHash.HashPass("oldPassword123"));
        testAuth.setRole(Role.USER);
        testAuth = authRepository.saveAndFlush(testAuth);

        testUser = new UserEntity();
        testUser.setId(testAuth.getId());
        testUser.setName("Forgot User");
        testUser.setEmail("forgotUser@example.com");
        testUser = userRepository.saveAndFlush(testUser);

        testOtp = new OTPEntity();
        testOtp.setOtp(123456);
        testOtp.setExpirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        testOtp.setUser(testUser);
        testOtp = otpRepository.saveAndFlush(testOtp);

        String loginJson = """
                {
                  "username": "forgotUser",
                  "password": "oldPassword123"
                }
                """;

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = loginResult.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
    }

    // ----------------------------------------------------------
    // ✅ Test Cases
    // ----------------------------------------------------------

    @Test
    void testVerifyEmail_Success() throws Exception {
        mockMvc.perform(post("/api/v1/forgotPassword/{email}", testUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OTP sent successfully to User email.")))
                .andExpect(jsonPath("$.otpId", notNullValue()));
    }

    @Test
    void testVerifyEmail_UserNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/forgotPassword/{email}", "unknown@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testResendOtp_Success() throws Exception {
        mockMvc.perform(put("/api/v1/forgotPassword/resendOtp/{email}", testUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OTP resent successfully")))
                .andExpect(jsonPath("$.otpId", notNullValue()));
    }

    @Test
    void testResendOtp_UserNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/forgotPassword/resendOtp/{email}", "unknown@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

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

        // Verify password updated
        AuthEntity updatedAuth = authRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newSecurePassword123", updatedAuth.getPassword()));
    }

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

    @Test
    void testDatabaseState_AfterSetup() {
        long authCount = authRepository.count();
        long userCount = userRepository.count();
        long otpCount = otpRepository.count();
        Assertions.assertEquals(1, authCount);
        Assertions.assertEquals(1, userCount);
        Assertions.assertEquals(1, otpCount);
    }
}
