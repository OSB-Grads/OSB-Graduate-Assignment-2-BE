package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Entity.*;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Util.PasswordHash;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.SQLOutput;
import java.time.Instant;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthFlowIntegrationTests {
    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private AuthRepository authRepository;


    private String jwtToken;
    private String refreshToken;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @BeforeEach
    void setup() {
        AuthEntity authUser = new AuthEntity(null, "testUser", PasswordHash.HashPass("testpass"), Role.USER);
        authRepository.save(authUser);
    }

    @Test
    void loginSuccess() throws Exception {
        String json = """
                {
                                  "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
        assertNotNull(jwtToken);
        refreshToken=JsonPath.parse(responseJson).read("$.refreshToken");
        assertNotNull(refreshToken);
    }
    @Test
    void loginfailure_invalidUsername() throws Exception{
        String json = """
                {
                                  "username": "unkowntestUser",
                                  "password": "testpass"
                 }
                """;
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Invalid UserName or User not found"))
                .andReturn();
    }
    @Test
    void loginfailure_invalidPassword() throws Exception{
        String json = """
                {
                                  "username": "testUser",
                                  "password": "wrongtestpass"
                 }
                """;
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Invalid PassWord"))
                .andReturn();
    }
}
