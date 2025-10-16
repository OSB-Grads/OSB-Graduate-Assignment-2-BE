package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Util.PasswordHash;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTests_ADMIN {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthRepository authRepository;
    private UUID testUserId;
    private UserEntity testUser;
    private String jwtToken;

    @BeforeEach
    void setup() throws Exception {


        // Clear any existing data
        userRepository.deleteAll();
        authRepository.deleteAll();

        // Create and save AuthEntity first
        AuthEntity testAuth = AuthEntity.builder()
                .username("testuser")
                .password(PasswordHash.HashPass("password"))
                .role(Role.ADMIN)
                .build();
        AuthEntity savedAuth = authRepository.save(testAuth);


        // Create and save UserEntity
        UUID id1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID id2 = UUID.fromString("123e4568-e89c-12e3-a356-226614174000");
        UserEntity u1 = new UserEntity(id1,
                "John Doe",
                "john.doe@example.com",
                "1234567890",
                "2023-01-01 10:00:00",
                "2023-01-01 10:00:00",
                "123 Main St",
                Role.USER);

        UserEntity u2 = new UserEntity(id2,
                "Tim",
                "tim123e@example.com",
                "0123456789",
                "2023-01-01 10:00:00",
                "2023-01-01 10:00:00",
                "456 Main St",
                Role.USER);
        userRepository.saveAll(List.of(u1, u2));


        //mock login input
        String loginJson = """
                {
                  "username": "testuser",
                  "password": "password"
                }
                """;
        //mock login
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        //extract jwt token from response
        String responseJson = result.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
    }

    //test for get all users
    @Test
    void testGetAllUsers() throws Exception {

        //mock call users
        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect 200
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].phone", is("1234567890")))
                .andExpect(jsonPath("$[1].email", is("tim123e@example.com")))
                .andExpect(jsonPath("$[1].address", is("456 Main St")));
    }

    //test for get all users Failure
    @Test
    void testGetAllUsers_Failure() throws Exception {

        //delete accounts in database
        userRepository.deleteAll();
        //mock call accounts
        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect 404
                .andExpect(status().is4xxClientError())
                //expect error message
                .andExpect(jsonPath("$.message").value("No Users Exist in Database"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
