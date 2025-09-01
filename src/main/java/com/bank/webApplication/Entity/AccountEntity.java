package com.bank.webApplication.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class AccountEntity {



    private String Account_number;
    @Column(columnDefinition = "VARCHAR(25)")
    private UUID id;
    @Enumerated(EnumType.STRING)
    private Account_type Account_type;
    private double Balance;
    private boolean is_locked;

    private String  Account_created;
    private String  Account_updated;


    public enum Account_type{
        SAVINGS,
        FIXED_DEPOSIT
    }
}
