package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.AccountNotFoundException;
import com.bank.webApplication.CustomException.InsufficientFundsException;
import com.bank.webApplication.CustomException.TransactionFailedException;
import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Dto.TransactionDTO;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Repository.TransactionRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TransactionService {

    private DepositWithdrawDTO depositWithdrawDTO;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private DtoEntityMapper dtoEntityMapper;

    @Autowired
    public TransactionService (AccountRepository accountRepository,TransactionRepository transactionRepository, DtoEntityMapper dtoEntityMapper){
        this.accountRepository = accountRepository;
        this.transactionRepository=transactionRepository;
        this.dtoEntityMapper = dtoEntityMapper;
    }
    public DepositWithdrawDTO depositAmount(String accountNumber, double amount){

        // amount exception
        if (amount <= 0){
            log.info("[Deposit Amount] Amount Insufficient " );
            throw new InsufficientFundsException("Amount should be greater than 0");
        }

        // FD Validation

        AccountEntity account = accountRepository.findById(accountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found.Not a valid AccountNumber"));
        int tenure = account.getProduct().getTenure();
        int fundingWindow = account.getProduct().getFundingWindow();
        int coolingPeriod = account.getProduct().getCoolingPeriod();

        if (! isLocked(tenure , fundingWindow, coolingPeriod)) {

            account.setBalance(amount + account.getBalance());
            try {
                accountRepository.save(account);
                log.info("[Deposit Operation] Account Balance Updation Successful ");
                return new DepositWithdrawDTO(accountNumber, "Deposit Operation: Account Balance Updation Successful ", amount, TransactionEntity.status.COMPLETED, TransactionEntity.type.DEPOSIT );
            } catch (RuntimeException e) {
                log.info("[Deposit Operation] Account Balance Updation Failed ");
                return new DepositWithdrawDTO(accountNumber, "Deposit Operation: Account Balance Updation Failed ", amount, TransactionEntity.status.FAILED, TransactionEntity.type.DEPOSIT );

            }
        }
        else {
            log.info("[Deposit Operation] Account is locked ");
            return new DepositWithdrawDTO(accountNumber, "Deposit Operation: Account Balance Updation Failed ", amount, TransactionEntity.status.FAILED, TransactionEntity.type.DEPOSIT );
        }
    }

    public boolean isLocked(int tenure, int coolingPeriod, int fundingWindow){
        int maturity = tenure - (coolingPeriod + fundingWindow);
        if (maturity >= fundingWindow && maturity <= (tenure - coolingPeriod)){
            return true;
        }
        return false;
    }

    public DepositWithdrawDTO withdrawAmount(String accountNumber, double amount){

        // amount exception
        if (amount <= 0){
            log.info("[Withdraw Amount] Amount Insufficient " );
            throw new InsufficientFundsException("Amount should be greater than 0");
        }

        // FD Validation

        AccountEntity account = accountRepository.findById(accountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found.Not a valid AccountNumber"));
        int tenure = account.getProduct().getTenure();
        int fundingWindow = account.getProduct().getFundingWindow();
        int coolingPeriod = account.getProduct().getCoolingPeriod();

        if (! isLocked(tenure , fundingWindow, coolingPeriod)){

            if (account.getBalance() < amount) throw new InsufficientFundsException(account.getBalance() + " is Insufficient to debit.");
            account.setBalance(account.getBalance() - amount);
            try {

                accountRepository.save(account);
                log.info("[Debit operation] Account Balance updated successfully ");
                return new DepositWithdrawDTO(accountNumber, "Debit Operation : Account Balance Updation Successful ", amount, TransactionEntity.status.COMPLETED, TransactionEntity.type.WITHDRAWAL);

            }
            catch (RuntimeException e){
                log.info("[Debit Operation] Account Balance Updation Failed ");
                return new DepositWithdrawDTO(accountNumber, " Debit Operation: Account Balance Updation Failed ", amount, TransactionEntity.status.FAILED, TransactionEntity.type.WITHDRAWAL );
            }
        }
        else {
            log.info("[Debit Operation] account is locked ");
            return new DepositWithdrawDTO(accountNumber, " Debit Operation: Account is Locked ", amount, TransactionEntity.status.FAILED, TransactionEntity.type.WITHDRAWAL );
        }
    }

    public void saveTransaction( String fromAccount, String toAccount , double amount,String description,TransactionEntity.type type,TransactionEntity.status status) {
        TransactionEntity transactionEntity = new TransactionEntity();

        transactionEntity.setFromAccount(accountRepository.findById(fromAccount).get());
        transactionEntity.setToAccount(accountRepository.findById(toAccount).get());
        transactionEntity.setAmount(amount);
        transactionEntity.setTransactionType(type);
        transactionEntity.setTransactionStatus(status);
        transactionEntity.setDescription(description);
        transactionEntity.setCreatedAt(LocalDateTime.now().toString());
        try {
            transactionRepository.save(transactionEntity);
        } catch (Exception e) {
            throw new RuntimeException("Save Transaction Failed" + e);
        }
    }


    public List<TransactionDTO> getTransactionsByAccountNumber(String accountNumber){

        List<TransactionEntity> resultTransactions = transactionRepository.findAllByAccountNumber(accountNumber);

        return resultTransactions.stream().map((transactionEntity)->dtoEntityMapper.convertToDto(transactionEntity, TransactionDTO.class)).collect(Collectors.toList());
    }
    

}
