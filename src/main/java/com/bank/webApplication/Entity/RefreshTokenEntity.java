package com.bank.webApplication.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="RefreshToken_table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name="UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "Id" , updatable = false,nullable = false,columnDefinition = "TEXT")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;
    @OneToOne
    @JoinColumn(name="Auth_id",referencedColumnName = "id")
    private AuthEntity authEntity;
    @Column(nullable = false,unique = true)
    private String refreshToken;
    @Column(nullable = false)
    private Instant expiry;

}
