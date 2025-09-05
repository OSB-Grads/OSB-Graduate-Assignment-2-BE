package com.bank.webApplication.Controllers;
import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Dto.DepositWithdrawRequestDTO;
import com.bank.webApplication.Dto.TransactionDTO;
import com.bank.webApplication.Dto.TransactionRequestDTO;
import com.bank.webApplication.Orchestrator.DepositAndWithdrawalOrch;
import com.bank.webApplication.Orchestrator.TransactOrchestrator;
import com.bank.webApplication.Services.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTests {
    @Mock
    private DepositAndWithdrawalOrch depositAndWithdrawalOrch;

    @Mock
    private TransactOrchestrator transactOrchestrator;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private final String userId = UUID.randomUUID().toString();

    @BeforeEach
    void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userId);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void testDeposit_Success() {
        // Given
        DepositWithdrawRequestDTO request = new DepositWithdrawRequestDTO();
        request.setAccountnumber("ACC123");
        request.setAmount(500.0);

        DepositWithdrawDTO responseDto = new DepositWithdrawDTO();
        responseDto.setAccountnumber("ACC123");
        responseDto.setAmount(1500.0);

        when(depositAndWithdrawalOrch.depositHandler(UUID.fromString(userId), "ACC123", 500.0)).thenReturn(responseDto);

        // When
        ResponseEntity<?> response = transactionController.deposit(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof DepositWithdrawDTO);

        DepositWithdrawDTO returned = (DepositWithdrawDTO) response.getBody();
        assertEquals("ACC123", returned.getAccountnumber());
        assertEquals(1500.0, returned.getAmount());
        verify(depositAndWithdrawalOrch, times(1)).depositHandler(UUID.fromString(userId), "ACC123", 500.0);
    }

    @Test
    void testWithdraw_Success() {
        // Given
        DepositWithdrawRequestDTO request = new DepositWithdrawRequestDTO();
        request.setAccountnumber("ACC789");
        request.setAmount(200.0);

        DepositWithdrawDTO responseDto = new DepositWithdrawDTO();
        responseDto.setAccountnumber("ACC789");
        responseDto.setAmount(800.0);

        when(depositAndWithdrawalOrch.depositHandler(UUID.fromString(userId), "ACC789", 200.0)).thenReturn(responseDto);

        // When
        ResponseEntity<?> response = transactionController.withdraw(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof DepositWithdrawDTO);

        DepositWithdrawDTO returned = (DepositWithdrawDTO) response.getBody();
        assertEquals("ACC789", returned.getAccountnumber());
        assertEquals(800.0, returned.getAmount());

        verify(depositAndWithdrawalOrch, times(1)).depositHandler(UUID.fromString(userId), "ACC789", 200.0);
    }

    @Test
    void testTransfer_Success() {
        // Given
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setFromAccountNumber("ACC001");
        request.setToAccountNumber("ACC002");
        request.setAmount(100.0);

        TransactionDTO responseDto = new TransactionDTO();
        responseDto.setFromAccount("ACC001");
        responseDto.setToAccount("ACC002");
        responseDto.setAmount(100.0);

        when(transactOrchestrator.transactionBetweenAccounts(UUID.fromString(userId), "ACC001", "ACC002", 100.0)).thenReturn(responseDto);

        // When
        ResponseEntity<?> response = transactionController.transfer(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof TransactionDTO);

        TransactionDTO returned = (TransactionDTO) response.getBody();
        assertEquals("ACC001", returned.getFromAccount());
        assertEquals("ACC002", returned.getToAccount());
        assertEquals(100.0, returned.getAmount());

        verify(transactOrchestrator, times(1)).transactionBetweenAccounts(UUID.fromString(userId), "ACC001", "ACC002", 100.0);
    }

    @Test
    void testGetTransactionHistory_Success() {
        // Given
        String accountNumber = "ACC555";
        TransactionDTO tx1 = new TransactionDTO();
        tx1.setFromAccount("ACC555");
        tx1.setAmount(50.0);

        TransactionDTO tx2 = new TransactionDTO();
        tx2.setFromAccount("ACC555");
        tx2.setAmount(100.0);

        List<TransactionDTO> transactions = List.of(tx1, tx2);
        when(transactionService.getTransactionsByAccountNumber(accountNumber)).thenReturn(transactions);

        // When
        ResponseEntity<?> response = transactionController.getTransactionHistory(accountNumber);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List);

        List<TransactionDTO> returned = (List<TransactionDTO>) response.getBody();
        assertEquals(2, returned.size());
        assertEquals(50.0, returned.get(0).getAmount());
        assertEquals(100.0, returned.get(1).getAmount());

        verify(transactionService, times(1)).getTransactionsByAccountNumber(accountNumber);
    }

    @Test
    void testGetTransactionHistory_EmptyList() {
        // Given
        String accountNumber = "ACC000";
        when(transactionService.getTransactionsByAccountNumber(accountNumber)).thenReturn(List.of());

        // When
        ResponseEntity<?> response = transactionController.getTransactionHistory(accountNumber);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List);

        List<TransactionDTO> returned = (List<TransactionDTO>) response.getBody();
        assertTrue(returned.isEmpty());

        verify(transactionService, times(1)).getTransactionsByAccountNumber(accountNumber);
    }
}


