package com.bank.webApplication.Dto;

import lombok.Builder;

@Builder
public record MailBodyDTO(String to , String subject, String text) {
}
