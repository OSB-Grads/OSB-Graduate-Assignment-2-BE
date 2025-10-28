package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import com.bank.webApplication.Util.PasswordHash;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductFlow_AdminIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
        productRepository.flush();
        // Create test user with encoded password
        AuthEntity user = new AuthEntity(null, "test12", PasswordHash.HashPass("test123"), Role.ADMIN);
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

    //test to fetch all products
    @Test
    void testFetchAllProducts() throws Exception {
        //mock call
        mockMvc.perform(get("/api/v1/product/fetch")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect success
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].productId", is("P001")))
                .andExpect(jsonPath("$[1].productName", is("Fixed Deposit")))
                .andExpect(jsonPath("$[2].description", is("Monthly deposit plan")));
    }

    //test to fetch product by id
    @Test
    void testFetchProductById_Valid() throws Exception {
        //mock call
        mockMvc.perform(get("/api/v1/product/fetch/P002")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect 200
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is("P002")))
                .andExpect(jsonPath("$.productName", is("Fixed Deposit")))
                .andExpect(jsonPath("$.interestRate", is(7.2)));
    }

    //test to fetch product by id failure
    @Test
    void testFetchProductById_Invalid() throws Exception {
        //mock call
        mockMvc.perform(get("/api/v1/product/fetch/INVALID")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect error
                .andExpect(status().is4xxClientError());

    }

    //test db state
    @Test
    void testDatabaseState_AfterSetup() {
        long count = productRepository.count();
        assertEquals(3, count);
    }

    //test to fetch all products with an empty list
    @Test
    void testFetchAllProducts_EmptyList() throws Exception {
        //delete mock data in repo
        productRepository.deleteAll();
        //mock call
        mockMvc.perform(get("/api/v1/product/fetch")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect 200
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    //test for CreateProduct_Success
    @Test
    @Transactional
    void testCreateProduct_Success() throws Exception {
        productRepository.deleteAll();
        //mock json input
        String json = """
                {
                  "productId": "P004",
                  "productName": "Fixed Deposit",
                  "interestRate": 1,
                  "fundingWindow": 3.0,
                  "coolingPeriod": 2,
                  "description": "Simple FD",
                  "tenure": 15
                }
                """;
        //mock call
        MvcResult result = mockMvc.perform(post("/api/v1/product/create")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //expect 200
                .andExpect(status().isOk())
                .andReturn();
        //response
        String responseJson = result.getResponse().getContentAsString();
        //convertion of response to dto
        ProductDto productDto = objectMapper.readValue(responseJson, ProductDto.class);
        //assert
        assertNotNull(productDto);
        assertEquals("P004", productDto.getProductId());
        assertEquals("Fixed Deposit", productDto.getProductName());
        assertEquals(1, productDto.getInterestRate());
        assertEquals(3.0, productDto.getFundingWindow());
        assertEquals(2, productDto.getCoolingPeriod());
        assertEquals("Simple FD", productDto.getDescription());
        assertEquals(15, productDto.getTenure());

    }

    //test for CreateProduct_Failure
    @Test
    void testCreateProduct_Failure() throws Exception {
        //mock json input
        String json = """
                {
                  "productId": "P002",
                  "productName": "Fixed Deposit",
                  "interestRate": 7.2,
                  "fundingWindow": 2,
                  "coolingPeriod": 1,
                  "description": "2 Year FD",
                  "tenure": 15
                }
                """;
        //mock call
        MvcResult result = mockMvc.perform(post("/api/v1/product/create")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //expect error
                .andExpect(status().is4xxClientError())
                //expect error message
                .andExpect(jsonPath("$.message").value("" +
                        "Product Already Exists"))
                .andReturn();


    }

    //test for UpdateProduct_Success
    @Test
    @Transactional
    void testUpdateProduct_Success() throws Exception {
        //mock json
        String json = """
                {
                  "productName": "Fixed Deposit",
                  "interestRate": 7.2,
                  "fundingWindow": 2,
                  "coolingPeriod": 1,
                  "description": "1 Year Simple FD",
                  "tenure": 15
                }
                """;
        MvcResult result = mockMvc.perform(put("/api/v1/product/update/P001")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //expect 200
                .andExpect(status().isOk())
                .andReturn();
        //response
        String responseJson = result.getResponse().getContentAsString();
        //convertion of response to dto
        ProductDto productDto = objectMapper.readValue(responseJson, ProductDto.class);
        //assert
        assertNotNull(productDto);
        assertEquals("P001", productDto.getProductId());
        assertEquals("Fixed Deposit", productDto.getProductName());
        assertEquals(7.2, productDto.getInterestRate());
        assertEquals(2, productDto.getFundingWindow());
        assertEquals(1, productDto.getCoolingPeriod());
        assertEquals("1 Year Simple FD", productDto.getDescription());
        assertEquals(15, productDto.getTenure());
    }

    //test for UpdateProduct_Failure
    @Test
    void testUpdateProduct_Failure() throws Exception {
        //mock input
        String json = """
                {
                  "productId": "FD004",
                  "productName": "Fixed Deposit",
                  "interestRate": 7.2,
                  "fundingWindow": 2,
                  "coolingPeriod": 1,
                  "description": "1 Year Simple FD",
                  "tenure": 15
                }
                """;
        //mock request
        MvcResult result = mockMvc.perform(put("/api/v1/product/update/FD004")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //expect error
                .andExpect(status().is4xxClientError())
                //expect error message
                .andExpect(jsonPath("$.message").value(" Product Not Found or does not exist in database"))
                .andReturn();
    }

    //test for deleteProduct_Success
    @Test
    void testDeleteProduct_Success() throws Exception {
        //mock call
        MvcResult result = mockMvc.perform(delete("/api/v1/product/delete/P002")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect 200
                .andExpect(status().isOk())
                .andReturn();
        //mock response
        String res = result.getResponse().getContentAsString();
        //assert
        assertEquals("Product deleted Successfully", res);

    }

    //test for deleteProduct_Failure
    @Test
    void testDeleteProduct_Failure() throws Exception {
        //mock call
        MvcResult result = mockMvc.perform(delete("/api/v1/product/delete/P005")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                //expect error
                .andExpect(status().is4xxClientError())
                .andReturn();

    }
}
