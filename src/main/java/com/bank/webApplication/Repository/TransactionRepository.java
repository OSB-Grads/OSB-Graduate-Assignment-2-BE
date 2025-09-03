package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByToAccountAccountNumber(String accountNumber);
    List<TransactionEntity> findAllByFromAccountAccountNumber(String accountNumber);

//    @Query("SELECT t FROM TransactionEntity t WHERE t.fromAccount.accountNumber = :accountNumber OR t.toAccount.accountNumber = :accountNumber")
//    List<TransactionEntity> findByAccountNumber(@Param("accountNumber") String accountNumber);

}
