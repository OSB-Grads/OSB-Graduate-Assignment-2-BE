package com.bank.webApplication.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class TransactionEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name="UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "transactionId" , updatable = false,nullable = false, columnDefinition = "TEXT")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID transactionId;

    @ManyToOne
    @JoinColumn(
            name = "fromAccount",
            referencedColumnName = "accountNumber"
    )
    private AccountEntity fromAccount;

    @ManyToOne
    @JoinColumn(
            name="toAccount",
            referencedColumnName = "accountNumber"
    )
    private AccountEntity toAccount;
    private double amount;
    private String description;
    private String createdAt;
    @Enumerated(EnumType.STRING) private type transactionType;
    @Enumerated(EnumType.STRING) private status transactionStatus;

    public enum type{
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER
    }
    public enum status{
        COMPLETED ,PENDING, FAILED
    }
}
