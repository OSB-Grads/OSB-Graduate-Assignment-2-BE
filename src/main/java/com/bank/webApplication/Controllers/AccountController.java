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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController()
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthRepository authRepository;

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ResponseEntity<AccountDto> CreateAccount(@RequestBody AccountDto accountDto
            , @RequestParam String productId) throws SQLException {
        log.info("[AccountController] Pinged CreateAccount");

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        AccountDto account = accountService.CreateAccount(accountDto, userId, productId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<List<AccountDto>> GetAllAccounts() {
        log.info("[AccountController] Pinged GetAllAccounts");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<AccountDto> account = accountService.getAllAccountsByUserId(userId);
        return new ResponseEntity<>(account, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDto> GetAccountByAccountNumber(@PathVariable String accountNumber) {
        log.info("[AccountController] Pinged GetAccountByAccountNumber");
        if (accountNumber == null) {
            log.error("[AccountController]  GetAccountByAccountNumber: account is not found FAILURE ");
            throw new AccountNotFoundException("account is not found");
        }
        AccountDto accountDto = accountService.getOneAccount(accountNumber);
        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }


}
