package com.bank.webApplication.Controllers;


import com.bank.webApplication.CustomException.AccountNotFoundException;
import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Services.AccountService;
import com.bank.webApplication.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthRepository authRepository;

    @PostMapping()
    public ResponseEntity<AccountDto> CreateAccount(@RequestBody AccountDto accountDto
            ,@RequestParam String productId) throws SQLException {

        String Username= SecurityContextHolder.getContext().getAuthentication().getName();
        AuthEntity user =authRepository.findByUsername(Username)
                .orElseThrow(()->new UserNotFoundException("user not found"));
        String userId=user.getId().toString();
        AccountDto account=accountService.CreateAccount(accountDto,userId,productId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<AccountDto>> GetAllAccounts(@RequestParam String productId){
        String Username= SecurityContextHolder.getContext().getAuthentication().getName();
        AuthEntity user =authRepository.findByUsername(Username)
                .orElseThrow(()->new UserNotFoundException("user not found"));
        String userId=user.getId().toString();
        List<AccountDto> account=accountService.getAllAccountsByUserId(userId);
        return new ResponseEntity<>(account, HttpStatus.OK);

    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDto> GetAccountByAccountNumber(@PathVariable String accountNumber){
        if(accountNumber==null){
            throw new AccountNotFoundException("account is not found");
        }
        AccountDto accountDto=accountService.getOneAccount(accountNumber);
        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }


}
