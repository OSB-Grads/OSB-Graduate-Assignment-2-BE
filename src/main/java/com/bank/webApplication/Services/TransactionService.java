package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.AccountNotFoundException;
import com.bank.webApplication.CustomException.InsufficientFundsException;
import com.bank.webApplication.CustomException.TransactionFailedException;
import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Dto.TransactionDTO;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.TransactionRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

@Service
@Slf4j
public class TransactionService {
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final TransactionRepository transactionRepository;
    @Autowired
    private final DtoEntityMapper dtoEntityMapper;

    private AccountService accountService;


    @Autowired
    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              DtoEntityMapper dtoEntityMapper,AccountService accountService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.dtoEntityMapper = dtoEntityMapper;
        this.accountService=accountService;
    }


     // Checks if account is locked based on creation date, funding window,cooling period, and tenure.

    public boolean isLocked(AccountEntity account) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //String formattedDate = now.format(formatter);
        LocalDateTime createdAt = LocalDateTime.parse(account.getAccountCreated(),formatter);
        LocalDateTime now = LocalDateTime.now();

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = now.format(formatter);

        int tenure = account.getProduct().getTenure();
        int fundingWindow = account.getProduct().getFundingWindow();
        int coolingPeriod = account.getProduct().getCoolingPeriod();

        LocalDateTime tenureEndDate = createdAt.plusHours(tenure);
        LocalDateTime fundingEndDate = createdAt.plusHours(fundingWindow);
        LocalDateTime coolingStartDate = tenureEndDate.minusHours(coolingPeriod);

        // Funding period → deposits & withdrawals allowed
        if (now.isBefore(fundingEndDate)) {
            return false;
        }

        // Maturity period → locked
        if (now.isAfter(fundingEndDate) && now.isBefore(coolingStartDate)) {
            return true;
        }

        // Cooling period → deposits & withdrawals allowed
        if (now.isAfter(coolingStartDate) && now.isBefore(tenureEndDate)) {
            return false;
        }

        // After full tenure → unlocked
        if (now.isAfter(tenureEndDate)) {
            return false;
        }

        return true;
    }


    /**
     * Helper method to update account balance safely and return DTO
     */
    private DepositWithdrawDTO processTransaction(AccountEntity account, double amount, TransactionEntity.type type, String successMsg, String failureMsg) {
        try {
            accountRepository.save(account);
            log.info("[{}] {}", type, successMsg);
            return new DepositWithdrawDTO(account.getAccountNumber(), successMsg, amount, TransactionEntity.status.COMPLETED, type);
        }
        catch (RuntimeException e) {
            log.error("[{}] {}", type, failureMsg, e);
            return new DepositWithdrawDTO(account.getAccountNumber(), failureMsg, amount, TransactionEntity.status.FAILED, type);
        }
    }


     //  Deposit operation

    public DepositWithdrawDTO depositAmount(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new InsufficientFundsException("Amount should be greater than 0");
        }

        AccountEntity account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found. Not a valid AccountNumber"));

        if (isLocked(account)) {
            return new DepositWithdrawDTO(accountNumber, "Account is Locked",
                    amount, TransactionEntity.status.FAILED, TransactionEntity.type.DEPOSIT);
        }

        account.setBalance(account.getBalance() + amount);
        return processTransaction(account, amount, TransactionEntity.type.DEPOSIT,
                "Deposit Successful", "Deposit Failed");
    }


     // Withdraw operation

    public DepositWithdrawDTO withdrawAmount(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new InsufficientFundsException("Amount should be greater than 0");
        }

        AccountEntity account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found. Not a valid AccountNumber"));

        if (isLocked(account)) {
            return new DepositWithdrawDTO(accountNumber, "Account is Locked", amount, TransactionEntity.status.FAILED, TransactionEntity.type.WITHDRAWAL);
        }

        if (account.getBalance() < amount) {
            throw new InsufficientFundsException(account.getBalance() + " is Insufficient to debit.");
        }

        account.setBalance(account.getBalance() - amount);
        return processTransaction(account, amount, TransactionEntity.type.WITHDRAWAL, "Withdrawal Successful", "Withdrawal Failed");
    }

    /**
     * Save transaction history
     */
    public void saveTransaction(String fromAccount, String toAccount, double amount, String description, TransactionEntity.type type, TransactionEntity.status status) {
        TransactionEntity transactionEntity = new TransactionEntity();
        log.info("[SAVE RTRANSACTION] TRANSACTION FROM Account");
        transactionEntity.setFromAccount((fromAccount!=null)?accountRepository.findById(fromAccount).get():null);
        log.info("[SAVE RTRANSACTION] TRANSACTION TO Account");
        transactionEntity.setToAccount((toAccount!=null)?accountRepository.findById(toAccount).get():null);
        transactionEntity.setAmount(amount);
        transactionEntity.setTransactionType(type);
        transactionEntity.setTransactionStatus(status);
        transactionEntity.setDescription(description);
        transactionEntity.setCreatedAt(LocalDateTime.now().toString());
        try {
            transactionRepository.save(transactionEntity);
        }
        catch (Exception e) {
            throw new TransactionFailedException("Save Transaction Failed");
        }
    }

    //Get all transactions for an account

    public List<TransactionDTO> getTransactionsByAccountNumber(String accountNumber) {
        log.info("FROM TRANSACTION HISTORY");
        List<TransactionEntity> resultFromTransactions =
                transactionRepository.findAllByFromAccountAccountNumber(accountNumber);
        log.info("TO TRANSACTION HISTORY");
        List<TransactionEntity> resultToTransactions =
                transactionRepository.findAllByToAccountAccountNumber(accountNumber);

        return Stream.concat(resultFromTransactions.stream(), resultToTransactions.stream())
                .map(entity -> new TransactionDTO(entity.getFromAccount()!=null?entity.getFromAccount().getAccountNumber():null,entity.getToAccount()!=null?entity.getToAccount().getAccountNumber():null,entity.getDescription(),entity.getAmount(),entity.getTransactionStatus(),entity.getTransactionType(),entity.getCreatedAt()))
                .sorted(Comparator.comparing(TransactionDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionHistoryByUserId(String userId){
       log.info("[Transaction Service] Get Transaction History By userId");
        List<AccountDto> accountDtoList=accountService.getAllAccountsByUserId(userId);
        log.info("[Transaction Service] Retrieval of AccountDTOs from UserId is Successful");
        List<TransactionDTO> transactionHistory = accountDtoList.stream()
                .map(accountDto -> getTransactionsByAccountNumber(accountDto.getAccountNumber()))
                .flatMap(List::stream)
                .sorted(Comparator.comparing(TransactionDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());

        return transactionHistory;

    }
}
