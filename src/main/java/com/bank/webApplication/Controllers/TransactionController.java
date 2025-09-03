package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Dto.TransactionDTO;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Orchestrator.DepositAndWithdrawalOrch;
import com.bank.webApplication.Orchestrator.TransactOrchestrator;
import com.bank.webApplication.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/transactions")
public class TransactionController {
    @Autowired
    private DepositAndWithdrawalOrch depositAndWithdrawalOrch;
    private TransactOrchestrator transactOrchestrator;
    private TransactionService transactionService;


    @PostMapping (path = "/deposit")
    public ResponseEntity<?> deposit(@RequestBody UUID userId, String accountNumber, double amount){
        DepositWithdrawDTO depositWithdrawDTO = depositAndWithdrawalOrch.depositHandler(userId , accountNumber, amount);
        return ResponseEntity.ok(depositWithdrawDTO );
    }

    @PostMapping (path = "/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody UUID userId, String accountNumber, double amount){
        DepositWithdrawDTO depositWithdrawDTO = depositAndWithdrawalOrch.depositHandler(userId , accountNumber, amount);
        return ResponseEntity.ok(depositWithdrawDTO );
    }

    @PostMapping (path = "/transfer")
    public ResponseEntity<?> transfer(@RequestBody UUID userId, String fromAccountNumber, String toAccountNumber, double amount){
        TransactionDTO transactionDTO = transactOrchestrator.transactionBetweenAccounts(userId, fromAccountNumber, toAccountNumber, amount);
        return ResponseEntity.ok(transactionDTO);
    }

//    GET /api/v1/transactions/{accountId}— List transactions for an account
    @GetMapping(path = "/{accountNumber}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable ("accountNumber") String accountNumber){
        List<TransactionDTO> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

}

