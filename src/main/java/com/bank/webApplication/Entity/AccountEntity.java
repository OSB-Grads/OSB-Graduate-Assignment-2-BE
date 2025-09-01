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
    private String account_number;

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
    private account_type account_type;

    private double balance;

    private boolean is_locked;

    private String  account_created;
    private String  account_updated;


    public enum account_type{
        SAVINGS,
        FIXED_DEPOSIT
    }
}
