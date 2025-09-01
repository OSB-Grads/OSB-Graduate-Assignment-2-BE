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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name="UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id" , updatable = false,nullable = false)
    private UUID id;
    private String name;
    private String email;
    private int phone;
    private String created_At;
    private String updated_At;

    private Role role;

    public enum Role{
        ADMIN,
        USER,
    }


}
