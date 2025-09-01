package com.bank.webApplication.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name="Auth_table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name="UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id" , updatable = false,nullable = false)
    private UUID id;
    @Column(name="UserName",updatable = false,nullable = false,unique = true)
    private String UserName;
    @Column(name="PassWord",nullable = false)
    private String PassWord;

}
