package com.bank.webApplication.Dto;

import com.bank.webApplication.Entity.LogEntity;
import lombok.*;

import java.util.UUID;


@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class LogDTO {
    private UUID id;
    private UUID user_id;
    private LogEntity.Action action;           // enum: Profile Management, Authentication, Transactions, Creation Management
    private String details;
    private String ipAddress;
    private LogEntity.Status status;            // enum: SUCCESS, FAILURE, ERROR
    private String timestamp;
}
