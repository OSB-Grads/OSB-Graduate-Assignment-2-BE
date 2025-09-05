package com.bank.webApplication.Orchestrator;

import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Dto.TransactionDTO;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import com.bank.webApplication.Services.LogService;
import com.bank.webApplication.Services.TransactionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TransactOrchestrator {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private LogService logService;

    @Autowired
    public TransactOrchestrator(TransactionService transactionService,LogService logService){
        this.transactionService=transactionService;
        this.logService=logService;
    }


    @Transactional
    public TransactionDTO transactionBetweenAccounts(UUID user_id, String fromAccountNumber, String ToAccountNumber, double amount){

        if (fromAccountNumber == null || ToAccountNumber == null || fromAccountNumber.equals(ToAccountNumber)) {
            log.error("[TransactOrchestrator ] Cannot transfer to the same account or account is null");
            logService.logintoDB(user_id, LogEntity.Action.TRANSACTIONS,
                    "Transaction Failed", String.valueOf(user_id), LogEntity.Status.FAILURE);
            return new TransactionDTO(fromAccountNumber, ToAccountNumber,
                    "Cannot transfer to the same account", amount,
                    TransactionEntity.status.FAILED, TransactionEntity.type.TRANSFER);
        }

        DepositWithdrawDTO withdrawOperation=transactionService.withdrawAmount(fromAccountNumber,amount);
        if(withdrawOperation != null){
            log.info("[TransactOrchestrator ] Withdraw Operation is Successful ");
            DepositWithdrawDTO depositOperation=transactionService.depositAmount(ToAccountNumber,amount);
            if(depositOperation !=null){
                log.info("[TransactOrchestrator ] Deposit Operation is Successful ");
                transactionService.saveTransaction(fromAccountNumber,ToAccountNumber,amount,"Transaction Saved Successfully", TransactionEntity.type.TRANSFER, TransactionEntity.status.COMPLETED);
                log.info("[TransactOrchestrator ] Transaction Saved Successfully");
                logService.logintoDB(user_id, LogEntity.Action.TRANSACTIONS,"Transaction is Successful", String.valueOf(user_id), LogEntity.Status.SUCCESS);
                return new TransactionDTO(fromAccountNumber,ToAccountNumber,"Transaction Successful",amount, TransactionEntity.status.COMPLETED, TransactionEntity.type.TRANSFER);
            }
            else{
                log.error("[TransactOrchestrator ] Deposit Operation Failed");
                logService.logintoDB(user_id, LogEntity.Action.TRANSACTIONS,"Transaction Failed", String.valueOf(user_id), LogEntity.Status.FAILURE);
                return new TransactionDTO(fromAccountNumber,ToAccountNumber,"Transaction Failed at Deposit Level.",amount, TransactionEntity.status.FAILED, TransactionEntity.type.TRANSFER);

            }
        }
        logService.logintoDB(user_id, LogEntity.Action.TRANSACTIONS,"Transaction Failed", String.valueOf(user_id), LogEntity.Status.FAILURE);
        log.error("[TransactOrchestrator ] Withdraw Operation Failed");
        return new TransactionDTO(fromAccountNumber,ToAccountNumber,"Transaction Failed at Withdrawal Level",amount, TransactionEntity.status.FAILED, TransactionEntity.type.TRANSFER);

    }
}
