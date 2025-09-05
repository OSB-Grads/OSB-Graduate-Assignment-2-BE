package com.bank.webApplication.Controller;


import com.bank.webApplication.Controllers.AccountController;
import com.bank.webApplication.CustomException.AccountNotFoundException;
import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Services.AccountService;
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

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class AccountControllerTests {
    @Mock
    private AccountService accountService;

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AccountController accountController;

    private  String userId = "123456238237";

    @BeforeEach
    void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(userId);

        SecurityContext context = mock(SecurityContext.class);
        lenient().when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }


    @Test
    void testCreateAccount() throws SQLException {
        // Given
        AccountDto requestDto = new AccountDto(); // Set fields if needed
        AccountDto responseDto = new AccountDto(); // Expected response

        String productId = "prod456";

        when(accountService.CreateAccount(requestDto, userId, productId)).thenReturn(responseDto);

        // When
        ResponseEntity<AccountDto> response = accountController.CreateAccount(requestDto, productId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void testGetAllAccounts() {
        // Given
        List<AccountDto> accounts = List.of(new AccountDto(), new AccountDto());
        when(accountService.getAllAccountsByUserId(userId)).thenReturn(accounts);

        // When
        ResponseEntity<List<AccountDto>> response = accountController.GetAllAccounts();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAccountByAccountNumber() {
        // Given
        String accountNumber = "1234554566";
        AccountDto account = new AccountDto(); // Set fields if needed

        when(accountService.getOneAccount(accountNumber)).thenReturn(account);

        // When
        ResponseEntity<AccountDto> response = accountController.GetAccountByAccountNumber(accountNumber);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(account, response.getBody());
    }

    @Test
    void testGetAccountByAccountNumber_Null_ShouldThrowException() {
        // Expect exception
        assertThrows(AccountNotFoundException.class, () -> {
            accountController.GetAccountByAccountNumber(null);
        });
    }


}
