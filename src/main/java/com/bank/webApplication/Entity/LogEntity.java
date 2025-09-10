package com.bank.webApplication.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;


@Entity
@Table(name="logs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LogEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name="UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id" , updatable = false,nullable = false,columnDefinition = "TEXT")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private AuthEntity authEntity;
    @Enumerated(EnumType.STRING)
    private Action action;
    private String details;
    private String ip_address;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String timestamp;

    public enum Action{
        PROFILE_MANAGEMENT,
        AUTHENTICATION,
        TRANSACTIONS,
        CREATION_MANAGEMENT
    }

    public enum Status{
        SUCCESS,
        FAILURE,
        ERROR
    }
}
