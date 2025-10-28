package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Entity.RefreshTokenEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Util.PasswordHash;
import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.UUID;

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
        // setting up a dummyAuthEntity
        AuthEntity authUser = new AuthEntity(null, "testUser", PasswordHash.HashPass("testpass"), Role.USER);
        authRepository.save(authUser);
    }

    //test for login
    @Test
    void loginSuccess() throws Exception {
        // mocking json input
        String json = """
                {
                                  "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        //mocking login  controller ping
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        //decoding the response
        String responseJson = result.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
        //assert
        assertNotNull(jwtToken);
        refreshToken = JsonPath.parse(responseJson).read("$.refreshToken");
        //assert
        assertNotNull(refreshToken);
    }

    //tyest for invalid username in login
    @Test
    void loginfailure_invalidUsername() throws Exception {
        //mock login json input with wrong username
        String json = """
                {
                                  "username": "unkowntestUser",
                                  "password": "testpass"
                 }
                """;
        //mock login controller ping
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Invalid UserName or User not found"))
                .andReturn();
    }

    //mock login json input with wrong password
    @Test
    void loginfailure_invalidPassword() throws Exception {
        String json = """
                {
                                  "username": "testUser",
                                  "password": "wrongtestpass"
                 }
                """;
        //mock login controller ping
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Invalid PassWord"))
                .andReturn();
    }

    //mock signup json input
    @Test
    void signup_success() throws Exception {
        authRepository.deleteAll();
        String json = """
                {
                                  "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        //mock signup controller ping
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
        //assert
        assertNotNull(jwtToken);
        refreshToken = JsonPath.parse(responseJson).read("$.refreshToken");
        //assert
        assertNotNull(refreshToken);
    }

    //mock signup json input with an exsisting user
    @Test
    void signup_UserAlreadyExist() throws Exception {
        String json = """
                {
                                 "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        //mock signup controller ping
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("User Already Exist"))
                .andReturn();
    }

    //test for refresh access token
    @Test
    void refreshAccessToken_Success() throws Exception {
        //mock login json input
        String json = """
                {
                                  "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        //mock ping to login controller
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
//decode response
        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
        //assert
        assertNotNull(jwtToken);
        JsonNode jsonNode = mapper.readTree(responseJson);
        refreshToken = jsonNode.get("refreshToken").asText();
        //assert
        assertNotNull(refreshToken);
        //mock refreshAccessToken ping
        MvcResult res = mockMvc.perform(post("/api/v1/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("Refreshtoken", refreshToken))
                .andExpect(status().isOk())
                .andReturn();
        String response = res.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(response).read("$.token");
        //assert
        assertNotNull(jwtToken);
        refreshToken = JsonPath.parse(response).read("$.refreshToken");
        //assert
        assertNotNull(refreshToken);


    }

    //test for refresh access token with invalid token
    @Test
    void refreshAccessToken_InvalidRefreshToken() throws Exception {
        //mock login json
        String json = """
                {
                                  "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        //mock login controller ping
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
//decode response
        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
        //assert
        assertNotNull(jwtToken);
        JsonNode jsonNode = mapper.readTree(responseJson);
        refreshToken = jsonNode.get("refreshToken").asText();
        //assert
        assertNotNull(refreshToken);
        //mock invalid refresh token
        String invalidToken = "invalid_refresh_token";
        //mock ping to refresh token
        MvcResult res = mockMvc.perform(post("/api/v1/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("Refreshtoken", invalidToken))
                .andExpect(status().is4xxClientError())
                .andExpect(result1 ->
                        assertTrue(result1.getResolvedException() instanceof RuntimeException))
                .andExpect(result1 ->
                        assertEquals("Invalid Refresh Token", result1.getResolvedException().getMessage()))
                .andReturn();

    }

    //test for refresh access tokenwith token exppired
    @Test
    void refreshAccessToken_RefreshTokenExpired() throws Exception {
        //login input json
        String json = """
                {
                                  "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        //mock login controller ping
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
//decode response
        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
        //assert
        assertNotNull(jwtToken);
        JsonNode jsonNode = mapper.readTree(responseJson);
        refreshToken = jsonNode.get("refreshToken").asText();
        //assert
        assertNotNull(refreshToken);
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.
                findByRefreshToken(refreshToken).orElseThrow();
        //set up mock expiry
        refreshTokenEntity.setExpiry(Instant.now().minusSeconds(20));
        refreshTokenRepository.save(refreshTokenEntity);
        //mock call to refreshAccessToken controller
        MvcResult res = mockMvc.perform(post("/api/v1/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("Refreshtoken", refreshToken))
                .andExpect(status().is4xxClientError())
                .andExpect(result1 ->
                        assertTrue(result1.getResolvedException() instanceof RuntimeException))
                .andExpect(result1 ->
                        assertEquals("Token Has Expired", result1.getResolvedException().getMessage()))
                .andReturn();
        //assert
        assertTrue(refreshTokenRepository.findByRefreshToken(refreshToken).isEmpty());
    }

    //test for logout
    @Test
    void testLogOut_success() throws Exception {
        //mock login json input
        String json = """
                {
                                  "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        //mock call for login
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
//decode response
        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseJson);
        jwtToken = jsonNode.get("token").asText();
        refreshToken = jsonNode.get("refreshToken").asText();
        //assert
        assertNotNull(refreshToken);
        //mock call for logout
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("Refreshtoken", refreshToken)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andReturn();
        //assert
        assertTrue(refreshTokenRepository.findByRefreshToken(refreshToken).isEmpty());
    }

    //test for logout failure
    @Test
    void testLogOut_failure() throws Exception {
        //login json input
        String json = """
                {
                                  "username": "testUser",
                                  "password": "testpass"
                 }
                """;
        //mock call for login
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
//decode response
        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
        //assert
        assertNotNull(jwtToken);
        JsonNode jsonNode = mapper.readTree(responseJson);
        refreshToken = jsonNode.get("refreshToken").asText();
        //assert
        assertNotNull(refreshToken);
        //delete
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        //mock call for logout
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("Refreshtoken", refreshToken)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("false"))
                .andReturn();
    }


}
