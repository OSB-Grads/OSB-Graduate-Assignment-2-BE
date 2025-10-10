package com.bank.webApplication.Controllers.IntegrationTests;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Repository.AuthRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeEach
    void setup() {
        authRepository.deleteAll();
//        AuthEntity a1=new AuthEntity()
    }
}
