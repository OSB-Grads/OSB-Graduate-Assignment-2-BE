package com.bank.webApplication.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Types;
import java.util.UUID;


@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserEntity {
    @Id
    @Column(name = "id" , updatable = false,nullable = false ,columnDefinition = "TEXT")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String created_At;
    private String updated_At;

    @Enumerated(EnumType.STRING)
    private Role role;

}
