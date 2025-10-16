package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Entity.*;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Util.PasswordHash;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AccountIntegrationTest_ADMIN {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID testUserId;
    private UserEntity testUser;
    private String jwtToken;


    @BeforeEach
    void setUp() throws Exception {
        // Clear any existing data
        accountRepository.deleteAll();
        userRepository.deleteAll();
        authRepository.deleteAll();
        productRepository.deleteAll();

        // Create and save AuthEntity first
        AuthEntity testAuth = AuthEntity.builder()
                .username("testuser")
                .password(PasswordHash.HashPass("password"))
                .role(Role.ADMIN)
                .build();
        AuthEntity savedAuth = authRepository.save(testAuth);
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        // Create and save UserEntity
        testUser = new UserEntity(
                id,
                "John Doe",
                "john.doe@example.com",
                "1234567890",
                "2023-01-01 10:00:00",
                "2023-01-01 10:00:00",
                "123 Main St",
                Role.USER
        );
        userRepository.save(testUser);
        // Create and save test product
        ProductEntity p1 = new ProductEntity("P001", "Savings Account", 2.0, 2, 1, 1, "Basic savings account");
        ProductEntity p2 = new ProductEntity("P002", "Fixed Deposit", 7.2, 2, 1, 5, "1 Year FD");
        ProductEntity p3 = new ProductEntity("P003", "Recurring Deposit", 6.5, 2, 1, 6, "Monthly deposit plan");
        productRepository.saveAll(List.of(p1, p2, p3));
        //create and save test account
        AccountEntity a1 = new AccountEntity("102837349", testUser, p1, AccountEntity.accountType.SAVINGS, 20000.00, "2023-01-01 10:00:00", "2023-01-01 10:00:00");
        AccountEntity a2 = new AccountEntity("1932193931", testUser, p1, AccountEntity.accountType.FIXED_DEPOSIT, 30000.00, "2023-01-01 10:00:00", "2023-01-01 10:00:00");
        accountRepository.saveAll(List.of(a1, a2));
        // Login with mock user to get JWT token
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

        String responseJson = result.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");

    }

    //test get all accounts
    @Test
    void testGetAllAccounts() throws Exception {
        //mock call accounts
        mockMvc.perform(get("/api/v1/admin/accounts")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect 200
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountNumber", is("102837349")))
                .andExpect(jsonPath("$[0].accountType", is("SAVINGS")))
                .andExpect(jsonPath("$[1].balance", is(30000.00)));

    }

    @Test
    void testGetAllAccounts_Failure() throws Exception {
        //delete accounts in database
        accountRepository.deleteAll();
        //mock call accounts
        mockMvc.perform(get("/api/v1/admin/accounts")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect 404
                .andExpect(status().is4xxClientError())
                //expect error message
                .andExpect(jsonPath("$.message").value("No Accounts exist in Database"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}