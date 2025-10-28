package com.bank.webApplication.Controllers.IntegrationTests;


import com.bank.webApplication.Controllers.LogController;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.LogRepository;
import com.bank.webApplication.Util.PasswordHash;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogControllerIntegrationTests {

    @Autowired
    private LogController logController;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private MockMvc mockMvc;

    String jwtToken;
    UUID userid;

    @BeforeEach
    void setup() throws Exception {
        // Clear DB

        authRepository.deleteAll();
        logRepository.deleteAll();

        AuthEntity user = new AuthEntity(null, "test", PasswordHash.HashPass("test123"), Role.ADMIN);
        authRepository.save(user);
        user.setId(user.getId());
        userid=user.getId();

        // Insert test data
        LogEntity l1=new LogEntity(null,user, LogEntity.Action.PROFILE_MANAGEMENT,"Testing Log 1", user.getUsername(), LogEntity.Status.SUCCESS,"25/09/2025");
        LogEntity l2=new LogEntity(null,user, LogEntity.Action.CREATION_MANAGEMENT,"Testing Log 2", user.getUsername(), LogEntity.Status.ERROR,"25/09/2025");
        LogEntity l3=new LogEntity(null,user, LogEntity.Action.PROFILE_MANAGEMENT,"Testing Log 3", user.getUsername(), LogEntity.Status.FAILURE,"28/09/2025");

        logRepository.saveAll(List.of(l1,l2,l3));

        // Create test user with encoded password


        // Login to get JWT
        String loginJson = """
            {
              "username": "test",
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
    void testFetchAllLogsThatContainsLogs() throws Exception {

        mockMvc.perform(get("/api/v1/logs")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].details", is("Testing Log 1")))
                .andExpect(jsonPath("$[1].details", is("Testing Log 2")))
                .andExpect(jsonPath("$[2].timestamp", is("28/09/2025")));

    }

    @Test
    void testFetchAllLogsThatContainsNoLogs() throws Exception {
        logRepository.deleteAll();
        mockMvc.perform(get("/api/v1/logs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }


    @Test
    void testFetchAllLogsByUserId() throws Exception {

        mockMvc.perform(get("/api/v1/logs/{userid}",userid)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].details", is("Testing Log 1")))
                .andExpect(jsonPath("$[1].details", is("Testing Log 2")))
                .andExpect(jsonPath("$[2].timestamp", is("28/09/2025")));

    }

    @Test
    void testFetchAllLogsByUserIdThatHasNoLogs() throws Exception {
        logRepository.deleteAll();
        mockMvc.perform(get("/api/v1/logs/{userid}",userid)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    void testFetchLogByLogId() throws Exception {
        UUID logid=logRepository.save(new LogEntity(null,new AuthEntity(), LogEntity.Action.PROFILE_MANAGEMENT,"Testing Log 1", "124", LogEntity.Status.SUCCESS,"25/09/2025")).getId();

        mockMvc.perform(get("/api/v1/logs/logId/{logId}",logid)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details", is("Testing Log 1")))
                .andExpect(jsonPath("$.status",is("SUCCESS")))
                .andExpect(jsonPath("$.timestamp", is("25/09/2025")));

    }


    @Test
    void testFetchLogByLogIdContainsNoLogs() throws Exception {

        mockMvc.perform(get("/api/v1/logs/logId/{logId}",UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

}
