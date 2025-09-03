package com.bank.webApplication.Dto;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto {

    private String username;
    private String password;
}
