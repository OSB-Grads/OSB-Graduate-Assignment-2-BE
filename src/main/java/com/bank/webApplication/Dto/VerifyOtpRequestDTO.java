package com.bank.webApplication.Dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequestDTO {
    private UUID otpId;
    private Integer otp;
}
