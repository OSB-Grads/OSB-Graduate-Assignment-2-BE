package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Dto.DepositWithdrawRequestDTO;
import com.bank.webApplication.Dto.TransactionDTO;
import com.bank.webApplication.Dto.TransactionRequestDTO;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Orchestrator.DepositAndWithdrawalOrch;
import com.bank.webApplication.Orchestrator.TransactOrchestrator;
import com.bank.webApplication.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/transactions")
public class TransactionController {
    @Autowired
    private DepositAndWithdrawalOrch depositAndWithdrawalOrch;
    @Autowired
    private TransactOrchestrator transactOrchestrator;
    @Autowired
    private TransactionService transactionService;


    @PostMapping (path = "/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositWithdrawRequestDTO depositWithdrawRequestDTO){
        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        DepositWithdrawDTO depositWithdrawDTO = depositAndWithdrawalOrch.depositHandler(UUID.fromString(userId),depositWithdrawRequestDTO.getAccountNumber(), depositWithdrawRequestDTO.getAmount());
        return ResponseEntity.ok(depositWithdrawDTO );
    }

    @PostMapping (path = "/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody DepositWithdrawRequestDTO depositWithdrawRequestDTO){
        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        DepositWithdrawDTO depositWithdrawDTO = depositAndWithdrawalOrch.WithdrawalHandler(UUID.fromString(userId), depositWithdrawRequestDTO.getAccountNumber(), depositWithdrawRequestDTO.getAmount());
        return ResponseEntity.ok(depositWithdrawDTO );
    }

    @PostMapping (path = "/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionRequestDTO transactionRequestDTO){
        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        TransactionDTO transactionDTO = transactOrchestrator.transactionBetweenAccounts(UUID.fromString(userId),transactionRequestDTO.getFromAccountNumber(),transactionRequestDTO.getToAccountNumber(),transactionRequestDTO.getAmount());
        return ResponseEntity.ok(transactionDTO);
    }

//    GET /api/v1/transactions/{accountId}— List transactions for an account
    @GetMapping(path = "/{accountNumber}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable ("accountNumber") String accountNumber){
        List<TransactionDTO> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

}

