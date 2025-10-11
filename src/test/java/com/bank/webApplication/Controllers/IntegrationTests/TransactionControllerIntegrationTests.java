package com.bank.webApplication.Controllers.IntegrationTests;


import com.bank.webApplication.Entity.*;
import com.bank.webApplication.Orchestrator.TransactOrchestrator;
import com.bank.webApplication.Repository.*;
import com.bank.webApplication.Services.TransactionService;
import com.bank.webApplication.Util.PasswordHash;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactOrchestrator transactOrchestrator;

    private String jwtToken;
    private AccountEntity account1;
    private AccountEntity account2;
    private UUID userId;

    @BeforeAll
    void setup() throws Exception {

        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        authRepository.deleteAll();
        productRepository.deleteAll();


        AuthEntity authUser = new AuthEntity(null, "txnUser", PasswordHash.HashPass("txn123"), Role.USER);
        authRepository.save(authUser);

        UserEntity user = new UserEntity();
        user.setId(authUser.getId());
        user.setName("txnUser");
        user.setEmail("txnUser@example.com");
        userRepository.save(user);

        userId = user.getId();

        ProductEntity product = new ProductEntity();
        product.setProductId("P001");
        product.setProductName("Integration Savings");
        product.setInterestRate(2.5);
        product.setTenure(2);
        product.setFundingWindow(1);
        product.setCoolingPeriod(1);
        product.setDescription("Used for integration test");
        productRepository.save(product);


        account1 = new AccountEntity();
        account1.setAccountNumber("ACC001");
        account1.setUser(user);
        account1.setBalance(1000.00);
        account1.setProduct(product);
        account1.setAccountCreated(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        account2 = new AccountEntity();
        account2.setAccountNumber("ACC002");
        account2.setUser(user);
        account2.setBalance(500.00);
        account2.setProduct(product);
        account2.setAccountCreated(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        accountRepository.saveAll(List.of(account1, account2));


        String loginJson = """
                {
                  "username": "txnUser",
                  "password": "txn123"
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
    void testWithdrawAmount_Success() throws Exception {

        ProductEntity product = new ProductEntity("P002", "Withdraw Savings", 4.5, 2, 1, 12, "For withdraw testing");
        productRepository.save(product);

        AccountEntity account = new AccountEntity();
        account.setAccountNumber("ACC_WITHDRAW_001");
        account.setBalance(1000.0);
        account.setProduct(product);
        account.setUser(userRepository.findById(userId).get());
        account.setAccountCreated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        accountRepository.save(account);

        // Prepare request JSON
        Map<String, Object> withdrawRequest = new HashMap<>();
        withdrawRequest.put("accountNumber", "ACC_WITHDRAW_001");
        withdrawRequest.put("amount", 200.0);

        // Perform withdraw API call
        MvcResult result = mockMvc.perform(post("/api/v1/transactions/withdraw")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Withdrawal Successful")))
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andReturn();

        //Verify updated balance
        AccountEntity updatedAccount = accountRepository.findByAccountNumber("ACC_WITHDRAW_001");
        System.out.println("[DEBUG] Updated Withdraw Balance: " + updatedAccount.getBalance());

        assertEquals(800.0, updatedAccount.getBalance(), 0.01);
    }

    @Test
    void testDepositAmount_Success() throws Exception {

        ProductEntity product = new ProductEntity("P003", "Deposit Savings", 4.5, 2, 1, 12, "For deposit testing");
        productRepository.save(product);

        //  Create account for deposit
        AccountEntity account = new AccountEntity();
        account.setAccountNumber("ACC_DEPOSIT_001");
        account.setBalance(500.0);
        account.setProduct(product);
        account.setUser(userRepository.findById(userId).get());
        account.setAccountCreated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        accountRepository.save(account);

        // Prepare request JSON
        Map<String, Object> depositRequest = new HashMap<>();
        depositRequest.put("accountNumber", "ACC_DEPOSIT_001");
        depositRequest.put("amount", 200.0);

        // Perform deposit API call
        MvcResult result = mockMvc.perform(post("/api/v1/transactions/deposit")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Deposit Successful")))
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andReturn();


        // Verify updated balance
        AccountEntity updatedAccount = accountRepository.findByAccountNumber("ACC_DEPOSIT_001");
        System.out.println("[DEBUG] Updated Deposit Balance: " + updatedAccount.getBalance());

        assertEquals(700.0, updatedAccount.getBalance(), 0.01);
    }

    @Test
    void testTransferAmount_Success() throws Exception {
        // Create product
        ProductEntity product = new ProductEntity("P001", "Transfer Savings", 5.0, 2, 1, 12, "For transfer testing");
        productRepository.save(product);

        // Create source account
        AccountEntity fromAccount = new AccountEntity();
        fromAccount.setAccountNumber("ACC_FROM_001");
        fromAccount.setBalance(1000.0);
        fromAccount.setProduct(product);
        fromAccount.setUser(userRepository.findById(userId).get());
        fromAccount.setAccountCreated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        accountRepository.save(fromAccount);

        // Create destination account
        AccountEntity toAccount = new AccountEntity();
        toAccount.setAccountNumber("ACC_TO_001");
        toAccount.setBalance(300.0);
        toAccount.setProduct(product);
        toAccount.setUser(userRepository.findById(userId).get());
        toAccount.setAccountCreated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        accountRepository.save(toAccount);

        // Prepare transfer request
        Map<String, Object> transferRequest = new HashMap<>();
        transferRequest.put("fromAccountNumber", "ACC_FROM_001");
        transferRequest.put("toAccountNumber", "ACC_TO_001");
        transferRequest.put("amount", 200.0);

        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Transaction Successful")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        // Verify balances
        AccountEntity updatedFrom = accountRepository.findByAccountNumber("ACC_FROM_001");
        AccountEntity updatedTo = accountRepository.findByAccountNumber("ACC_TO_001");

        assertEquals(800.0, updatedFrom.getBalance(), 0.01);
        assertEquals(500.0, updatedTo.getBalance(), 0.01);
    }

    @Test
    void testGetTransactionHistoryByAccountNumber() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/{accountNumber}", "ACC001")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetTransactionHistoryByUserId() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testInvalidAccountDeposit() throws Exception {
        String depositJson = """
            {
              "accountNumber": "INVALID",
              "amount": 200.00
            }
            """;

        mockMvc.perform(post("/api/v1/transactions/deposit")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDatabaseState_AfterSetup() {
        long count = accountRepository.count();
        assertEquals(2, count);
    }
}
