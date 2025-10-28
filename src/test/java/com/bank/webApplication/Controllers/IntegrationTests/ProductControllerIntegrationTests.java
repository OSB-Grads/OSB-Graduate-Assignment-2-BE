package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.ProductEntity;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Transactional
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthRepository authRepository;



    private String jwtToken;

    @BeforeEach
     void setup() throws Exception {
        // Clear DB
        productRepository.deleteAll();
        authRepository.deleteAll();

        // Insert test data
        ProductEntity p1 = new ProductEntity("P001", "Savings Account", 2.0, 2, 1, 1, "Basic savings account");
        ProductEntity p2 = new ProductEntity("P002", "Fixed Deposit", 7.2, 2, 1, 5, "1 Year FD");
        ProductEntity p3 = new ProductEntity("P003", "Recurring Deposit", 6.5, 2, 1, 6, "Monthly deposit plan");
        productRepository.saveAll(List.of(p1, p2, p3));

        // Create test user with encoded password
        AuthEntity user = new AuthEntity(null, "test12", PasswordHash.HashPass("test123"), Role.USER);
        authRepository.save(user);

        // Login to get JWT
        String loginJson = """
            {
              "username": "test12",
              "password": "test123"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                        .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
    }

    @Test
    void testFetchAllProducts() throws Exception {
        mockMvc.perform(get("/api/v1/product/fetch")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].productId", is("P001")))
                .andExpect(jsonPath("$[1].productName", is("Fixed Deposit")))
                .andExpect(jsonPath("$[2].description", is("Monthly deposit plan")));
    }

    @Test
    void testFetchProductById_Valid() throws Exception {
        mockMvc.perform(get("/api/v1/product/fetch/P002")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is("P002")))
                .andExpect(jsonPath("$.productName", is("Fixed Deposit")))
                .andExpect(jsonPath("$.interestRate", is(7.2)));
    }

    @Test
    void testFetchProductById_Invalid() throws Exception {
        mockMvc.perform(get("/api/v1/product/fetch/INVALID")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void testDatabaseState_AfterSetup() {
        long count = productRepository.count();
        assertEquals(3, count);
    }

    @Test
    void testFetchAllProducts_EmptyList() throws Exception {

        productRepository.deleteAll();

        mockMvc.perform(get("/api/v1/product/fetch")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }


}
