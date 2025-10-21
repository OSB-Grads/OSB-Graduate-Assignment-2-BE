package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.AccountNotFoundException;
import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Entity.*;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension .class)
public class AccountServiceTests {
    @InjectMocks
    private AccountService accountService;

    @Mock private DtoEntityMapper dtoEntityMapper;

    @Mock private AccountRepository accountRepository;

    @Mock private UserRepository userRepository;

    @Mock private ProductRepository productRepository;

    @Mock
    private LogService logService;

    private AccountDto account1Dto, account2Dto, account3Dto;
    private AccountEntity account1, account2,account3;
    private final String accountId1 = UUID.randomUUID().toString(), accountId2 = UUID.randomUUID().toString(), accountId3 = UUID.randomUUID().toString();
    private UserEntity user1,user2;
    private ProductEntity product1;

    @BeforeEach
    void setUp() {

        product1 = new ProductEntity("SAV01","Savings",1.0,0,0,0,"Savings with interest rate 1%");

        user1 = new UserEntity(UUID.randomUUID(),"TestUser1","testUser1@gmail.com","123456789","20/10/2025","25/10/2025","Bangalore", Role.USER);
        user2 = new UserEntity(UUID.randomUUID(),"TestUser2","testUser2@gmail.com","1234567890","20/10/2025","25/10/2025","Bangalore", Role.USER);

        account1 =  new AccountEntity(accountId1,user1, product1, AccountEntity.accountType.SAVINGS,1000,"20/10/2025","25/10/2025");
        account2 =  new AccountEntity(accountId2,user2, product1, AccountEntity.accountType.SAVINGS,1000,"20/10/2025","25/10/2025");
        account3 =  new AccountEntity(accountId3,user1, product1, AccountEntity.accountType.SAVINGS,1000,"20/10/2025","25/10/2025");

        account1Dto =  new AccountDto("123456789", AccountEntity.accountType.SAVINGS,1000,"20/10/2025","25/10/2025");
        account2Dto =  new AccountDto("111222333", AccountEntity.accountType.SAVINGS,1000,"20/10/2025","25/10/2025");
        account3Dto =  new AccountDto("777888999", AccountEntity.accountType.SAVINGS,1111,"20/10/2025","25/10/2025");

    }

    @Test
    void testCreateAccount_Success() throws SQLException {
        String userId = user1.getId().toString();
        String productId = product1.getProductId();

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user1));
        when(productRepository.findByProductId(productId)).thenReturn(product1);
        when(dtoEntityMapper.convertToEntity(any(AccountDto.class), eq(AccountEntity.class))).thenReturn(account1);
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(account1);

        AccountDto result = accountService.CreateAccount(account1Dto, userId, productId);

        assertNotNull(result);
        verify(accountRepository, atLeastOnce()).save(any(AccountEntity.class));
        verify(logService).logintoDB(any(), eq(LogEntity.Action.TRANSACTIONS), contains("Account Created"), anyString(), eq(LogEntity.Status.SUCCESS));
    }

    //Test - createAccount - User Not found
    @Test
    void testCreateAccount_UserNotFound() {
        UUID userId = UUID.randomUUID();
        String userIdStr = userId.toString();
        String productId = "PROD123";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        AccountDto accountDto = new AccountDto();

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            accountService.CreateAccount(accountDto, userIdStr, productId);
        });

        assertEquals("user not found! create the profile before create the account", ex.getMessage());
        verify(userRepository).findById(userId);
    }

    //Test - generateUniqueAccountNumberUUID - Success , return 10 digit number
    @Test
    void testGenerateUniqueAccountNumberUUID_Success() {
        // Act
        String accountNumber = accountService.generateUniqueAccountNumberUUID();

        // Assert
        assertNotNull(accountNumber, "Generated account number should not be null");
        assertEquals(10, accountNumber.length(), "Account number should be exactly 10 digits long");
        assertTrue(accountNumber.matches("\\d+"), "Account number should contain only digits");
    }

    //Test - generateUniqueAccountNumberUUID - should return unique values
    @Test
    void testGenerateUniqueAccountNumberUUID_UniqueValues() {
        Set<String> generatedNumbers = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            String accountNumber = accountService.generateUniqueAccountNumberUUID();
            assertFalse(generatedNumbers.contains(accountNumber), "Duplicate account number found: " + accountNumber);
            generatedNumbers.add(accountNumber);
        }
        assertEquals(1000, generatedNumbers.size());
    }

    //Test - getOneAccount - Success
    @Test
    void testGetOneAccount_Success() {
        String accountNumber = "123456789";
        account1.setAccountNumber(accountNumber);

        UUID currentUserId = UUID.randomUUID(); // Different from account's userId
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(currentUserId.toString());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account1);
        when(dtoEntityMapper.convertToDto(account1, AccountDto.class)).thenReturn(account1Dto);

        AccountDto result = accountService.getOneAccount("123456789");

        assertNotNull(result);
        assertEquals(accountNumber, result.getAccountNumber());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(dtoEntityMapper, times(1)).convertToDto(account1, AccountDto.class);
    }

    //Test - getOneAccount - Account not Found
    @Test
    public void testGetOneAccount_AccountNotFound() {

        String accountNumber = "000111222";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(null);

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountService.getOneAccount(accountNumber);
        });

        assertEquals("Account not exit", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(dtoEntityMapper, never()).convertToDto(any(), eq(AccountDto.class));
    }

    //Test - getAllAccountsByUserId - When account exist
    @Test
    void testGetAllAccountsByUserId_success() {

        List<AccountEntity> mockList = List.of(account1,account3);

        when(accountRepository.findAllByUserId(user1.getId())).thenReturn(mockList);
        when(dtoEntityMapper.convertToDto(account1, AccountDto.class)).thenReturn(account1Dto);
        when(dtoEntityMapper.convertToDto(account3, AccountDto.class)).thenReturn(account3Dto);

        List<AccountDto> result = accountService.getAllAccountsByUserId(user1.getId().toString());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(account1Dto.getAccountNumber(), result.get(0).getAccountNumber());
        assertEquals(account3Dto.getAccountNumber(), result.get(1).getAccountNumber());

        verify(accountRepository, times(1)).findAllByUserId(user1.getId());
        verify(dtoEntityMapper, times(1)).convertToDto(account1, AccountDto.class);
        verify(dtoEntityMapper, times(1)).convertToDto(account3, AccountDto.class);

    }

    //Test - getAllAccountsByUserId - When no Account exist
    @Test
    void testGetAllAccountsByUserId_NoAccount() {

        when(accountRepository.findAllByUserId(user1.getId())).thenReturn(Collections.emptyList());

        List<AccountDto> result = accountService.getAllAccountsByUserId(user1.getId().toString());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository, times(1)).findAllByUserId(user1.getId());
        verify(dtoEntityMapper, never()).convertToDto(any(), eq(AccountDto.class));

    }

    //Test - getAllAccounts - When Records exist
    @Test
    public void testGetAllAccounts_WithRecords(){
        List<AccountEntity> mockList = new ArrayList<>();
        mockList.add(account1);
        mockList.add(account2);

        when(accountRepository.findAll()).thenReturn(mockList);
        when(dtoEntityMapper.convertToDto(account1, AccountDto.class)).thenReturn(account1Dto);
        when(dtoEntityMapper.convertToDto(account2, AccountDto.class)).thenReturn(account2Dto);

        List<AccountDto> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(account1Dto.getAccountNumber(), result.get(0).getAccountNumber());
        assertEquals(account2Dto.getAccountNumber(), result.get(1).getAccountNumber());

        verify(accountRepository, times(1)).findAll();
        verify(dtoEntityMapper, times(1)).convertToDto(account1, AccountDto.class);
        verify(dtoEntityMapper, times(1)).convertToDto(account2, AccountDto.class);
    }

    //Test - getAllAccounts - When no Records exist
    @Test
    public void testGetAllAccounts_WithoutRecords() {
        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
            accountService.getAllAccounts()
        );

        assertEquals("No Accounts exist in Database", exception.getMessage());
        verify(accountRepository, times(1)).findAll();
        verify(dtoEntityMapper, never()).convertToDto(any(), eq(AccountDto.class));
    }

}
