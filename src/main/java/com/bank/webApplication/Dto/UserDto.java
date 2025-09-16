package com.bank.webApplication.Dto;

import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String name;
    private String email;
    private String phone;
    private Role role;
    private String Address;
}
