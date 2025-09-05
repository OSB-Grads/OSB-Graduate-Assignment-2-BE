package com.bank.webApplication.Services;

import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Entity.UserEntity;
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

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension .class)
public class AccountServiceTests {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private DtoEntityMapper dtoEntityMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LogService logService;

    private AccountDto accountDto;
    private AccountEntity accountEntity;
    private UserEntity userEntity;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        accountDto = new AccountDto();
        accountDto.setBalance(1000);

        accountEntity = new AccountEntity();
        accountEntity.setBalance(1000);

        userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());

        productEntity = new ProductEntity();
        productEntity.setProductId("SAV01");
        productEntity.setInterestRate(6.4);
    }

    @Test
    void testCreateAccount_success() throws SQLException {
        String userId = userEntity.getId().toString();
        String productId = productEntity.getProductId();

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(userEntity));
        when(productRepository.findByProductId(productId)).thenReturn(productEntity);
        when(dtoEntityMapper.convertToEntity(any(AccountDto.class), eq(AccountEntity.class))).thenReturn(accountEntity);

        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        AccountDto result = accountService.CreateAccount(accountDto, userId, productId);

        assertNotNull(result);
        verify(accountRepository, atLeastOnce()).save(any(AccountEntity.class));
        verify(logService).logintoDB(any(), eq(LogEntity.Action.TRANSACTIONS), contains("Account Created"), anyString(), eq(LogEntity.Status.SUCCESS));
    }

    @Test
    void testGetOneAccount_success() {
        accountEntity.setAccountNumber("1234567890");
        accountEntity.setUser(userEntity);
        accountEntity.setProduct(productEntity);

        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(accountEntity);
        when(dtoEntityMapper.convertToDto(any(AccountEntity.class), eq(AccountDto.class))).thenReturn(accountDto);

        AccountDto result = accountService.getOneAccount("1234567890");

        assertNotNull(result);
        verify(logService).logintoDB(eq(userEntity.getId()), eq(LogEntity.Action.TRANSACTIONS), contains("Account Created"), anyString(), eq(LogEntity.Status.SUCCESS));
    }

    @Test
    void testGetAllAccountsByUserId_success() {
        UUID userId = UUID.randomUUID();
        accountEntity.setUser(userEntity);
        List<AccountEntity> entityList = List.of(accountEntity);

        when(accountRepository.findAllByUserId(userId)).thenReturn(entityList);
        when(dtoEntityMapper.convertToDto(any(AccountEntity.class), eq(AccountDto.class))).thenReturn(accountDto);

        List<AccountDto> result = accountService.getAllAccountsByUserId(userId.toString());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(logService).logintoDB(eq(userId), eq(LogEntity.Action.TRANSACTIONS), contains("Account Created"), anyString(), eq(LogEntity.Status.SUCCESS));
    }
}
