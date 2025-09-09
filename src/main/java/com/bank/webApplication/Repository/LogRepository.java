package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.LogEntity;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface LogRepository extends JpaRepository<LogEntity, UUID> {
    public List<LogEntity> findByAuthEntity_Id(UUID userid);
}
