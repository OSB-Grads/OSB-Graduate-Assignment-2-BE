package com.bank.webApplication.Dto;

import com.bank.webApplication.Entity.UserEntity;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private UserEntity.Role role;
}
