package com.bank.webApplication.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="accounts")
@ToString
public class AccountEntity {


    @Id
    private String accountNumber;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private UserEntity user;

    @ManyToOne
    @JoinColumn(
            name="product_id",
            referencedColumnName = "productId"
    )
    private ProductEntity product;

    @Enumerated(EnumType.STRING)
    private accountType accountType;

    private double balance;

    private String  accountCreated;
    private String  accountUpdated;


    public enum accountType{
        SAVINGS,
        FIXED_DEPOSIT
    }
}
