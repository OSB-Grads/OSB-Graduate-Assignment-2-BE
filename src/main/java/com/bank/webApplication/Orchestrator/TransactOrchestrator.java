package com.bank.webApplication.Orchestrator;

import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Services.LogService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Slf4j
public class TransactOrchestrator {

    private TransactionService transactionService;
    private LogService logService;

    @Autowired
    public TransactOrchestrator(TransactionService transactionService,LogService logService){
        this.transactionService=transactionService;
        this.logService=logService;
    }

    @Transactional
    public boolean transactionBetweenAccounts(UUID user_id,String fromAccountNumber, String ToAccountNumber, double amount){
        boolean withdrawOperation=transactionService.withdrawAmount(fromAccountNumber,amount);
        if(withdrawOperation){
            log.info("[TransactOrchestrator ] Withdraw Operation is Successful ");
            boolean depositOperation=transactionService.depositAmount(ToAccountNumber,amount);
            if(depositOperation){
                log.info("[TransactOrchestrator ] Deposit Operation is Successful ");
                transactionService.saveTransaction(fromAccountNumber,ToAccountNumber,amount);
                log.info("[TransactOrchestrator ] Transaction Saved Successfully");
                logService.logintoDB(user_id, LogEntity.Action.TRANSACTIONS,"Transaction is Successful", String.valueOf(user_id), LogEntity.Status.SUCCESS);
                return true;
            }
            else{
                log.error("[TransactOrchestrator ] Deposit Operation Failed");
                logService.logintoDB(user_id, LogEntity.Action.TRANSACTIONS,"Transaction Failed", String.valueOf(user_id), LogEntity.Status.FAILURE);
                return false;
            }
        }
        logService.logintoDB(user_id, LogEntity.Action.TRANSACTIONS,"Transaction Failed", String.valueOf(user_id), LogEntity.Status.FAILURE);
        log.error("[TransactOrchestrator ] Withdraw Operation Failed");
      return false;
    }
}
