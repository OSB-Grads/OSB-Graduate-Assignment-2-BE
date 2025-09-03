package com.bank.webApplication.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;


@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserEntity {
    @Id
    @Column(name = "id" , updatable = false,nullable = false ,columnDefinition = "VARCHAR(36)")

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String created_At;
    private String updated_At;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role{
        ADMIN,
        USER,
    }

//    @OneToOne
//    @MapsId
//    @JoinColumn(name = "id")
//    private AuthEntity auth;
}
