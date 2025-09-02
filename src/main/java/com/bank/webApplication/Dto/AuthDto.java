package com.bank.webApplication.Dto;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto {

    private String UserName;
    private String PassWord;
}
