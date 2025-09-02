package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface LogRepository extends JpaRepository<LogEntity, UUID> {
}
