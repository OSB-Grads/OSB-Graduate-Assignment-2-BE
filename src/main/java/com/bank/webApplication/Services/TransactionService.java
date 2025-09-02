package com.bank.webApplication.Services;

import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class TransactionService {

    public AccountRepository accountRepository;
    // public ProductRepository productRepository;

    @Autowired
    public TransactionService (AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }
    public boolean depositAmount(String accountNumber, double amount){
        // account exception
        // amount exception

        // FD Validation

        AccountEntity account = accountRepository.findById(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        int tenure = account.getProduct().getTenure();
        int fundingWindow = account.getProduct().getFundingWindow();
        int coolingPeriod = account.getProduct().getCoolingPeriod();

        if (! isLocked(tenure , fundingWindow, coolingPeriod)) {

            account.setBalance(amount + account.getBalance());
            try {
                //log.info("[Deposit operation] Account Balance Updation Failed ");
                accountRepository.save(account);
                log.info("[Deposit operation] Account Balance Updation successful ");
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        else {
            log.info("[Deposit Operation] account is locked ");
            return false;
        }
    }

    public boolean isLocked(int tenure, int coolingPeriod, int fundingWindow){
        int maturity = tenure - (coolingPeriod + fundingWindow);
        if (maturity >= fundingWindow && maturity <= (tenure - coolingPeriod)){
            return true;
        }
        return false;
    }

    public boolean withdrawAmount(String accountNumber, double amount){
        // account exception
        // amount exception

        // FD Validation

        AccountEntity account = accountRepository.findById(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        int tenure = account.getProduct().getTenure();
        int fundingWindow = account.getProduct().getFundingWindow();
        int coolingPeriod = account.getProduct().getCoolingPeriod();

        if (! isLocked(tenure , fundingWindow, coolingPeriod)){

            if (account.getBalance() < amount) throw new  RuntimeException();
            account.setBalance(account.getBalance() - amount);
            try {
                //log.info("[Debit operation] account balance updation Failed ");
                accountRepository.save(account);
                log.info("[Debit operation] account balance updated successfully ");
                return true;

            }
            catch (RuntimeException e){
                return false;
            }
        }
        else {
            log.info("[Debit Operation] account is locked ");
            return false;
        }
    }

    public void saveTransaction( String fromAccount, String toAccount , double amount,String description,TransactionEntity.type type,TransactionEntity.status status){
    TransactionEntity transactionEntity = new TransactionEntity();

    transactionEntity.setFromAccount(accountRepository.findById(fromAccount).get());
    transactionEntity.setToAccount(accountRepository.findById(toAccount).get());
    transactionEntity.setAmount(amount);
    transactionEntity.setTransactionType(type);
    transactionEntity.setTransactionStatus(status);
    transactionEntity.setDescription(description);
    transactionEntity.setCreatedAt(LocalDateTime.now().toString());
    }

}
