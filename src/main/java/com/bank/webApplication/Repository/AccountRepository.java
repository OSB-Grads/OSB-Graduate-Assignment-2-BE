package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    AccountEntity findByAccountNumber(String s);
    List<AccountEntity> findAllByUserId(UUID id);
}
