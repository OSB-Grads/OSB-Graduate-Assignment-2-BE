package com.bank.webApplication.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name="Auth_table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name="UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id" , updatable = false,nullable = false,columnDefinition = "TEXT")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;
    @Column(name="UserName",updatable = false,nullable = false,unique = true)
    private String username;
    @Column(name="PassWord",nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;



}
