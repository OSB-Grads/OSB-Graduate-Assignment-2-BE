package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Services.AccountService;
import com.bank.webApplication.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    //Fetch all users for Admin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("[ADMIN Controller] pinged getAllUsers");
        List<UserDto> allUsersList = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsersList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/accounts")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        log.info("[ADMIN Controller] pinged getAllAccounts");
        List<AccountDto> allAccountsList = accountService.getAllAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(allAccountsList);
    }


}
