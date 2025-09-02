package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    AccountEntity findByAccount(String s);
    List<AccountEntity> findbyUserUserId(String id);
}
