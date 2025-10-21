package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Services.AccountService;
import com.bank.webApplication.Services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AdminControllerTests {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    @Mock
    private AccountService accountService;

    public String accountId1= UUID.randomUUID().toString(), accountId2 = UUID.randomUUID().toString();
    public UserDto user1, user2;
    public  AccountDto account1, account2;

    @BeforeEach
    public void setup(){
        account1 = new AccountDto(accountId1, AccountEntity.accountType.SAVINGS,100000,"25/10/2025","30/10/2025");
        account2 = new AccountDto(accountId2, AccountEntity.accountType.FIXED_DEPOSIT,100000,"24/10/2025","28/10/2025");

        user1 = new UserDto("testUser1","test@gmail.com","1234567890", Role.ADMIN,"Delhi");
        user2 =  new UserDto("testUser2","test@gmail.com","1234567890", Role.ADMIN,"Pune");
    }

    //Test - getAllUsers - Records Found
    @Test
    void testGetAllUsers_WithRecords(){
        List<UserDto> mockList = new ArrayList<>();
        mockList.add(user1);
        mockList.add(user2);

        when(userService.getAllUsers()).thenReturn(mockList);

        ResponseEntity<List<UserDto>> result = adminController.getAllUsers();

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(mockList);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    //Test - getAllUsers - Records not Found
    @Test
    void testGetAllUsers_WithoutRecords(){
        List<UserDto> mockList = new ArrayList<>();

        when(userService.getAllUsers()).thenReturn(mockList);

        ResponseEntity<List<UserDto>> result = adminController.getAllUsers();

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(new ArrayList<>());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    //Test getAllAccounts - Records Found
    @Test
    void testGetAllAccounts_WithRecords(){
        List<AccountDto> mockList = new ArrayList<>();
        mockList.add(account1);
        mockList.add(account2);

        when(accountService.getAllAccounts()).thenReturn(mockList);

        ResponseEntity<List<AccountDto>> result = adminController.getAllAccounts();

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(mockList);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    //Test - getAllAccounts - Records not Found
    @Test
    void testGetAllAccounts_WithoutRecords(){
        List<AccountDto> mockList = new ArrayList<>();

        when(accountService.getAllAccounts()).thenReturn(mockList);

        ResponseEntity<List<AccountDto>> result = adminController.getAllAccounts();

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(new ArrayList<>());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    }


}
