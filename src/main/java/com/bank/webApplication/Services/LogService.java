package com.bank.webApplication.Services;


import com.bank.webApplication.CustomException.LogNotFoundException;
import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.LogRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import com.bank.webApplication.Util.SensitiveDataValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class LogService {


    private LogRepository logRepository;
    private AuthRepository authRepository;
    private DtoEntityMapper dtoEntityMapper;
    private SensitiveDataValidator sensitiveDataValidator;

    @Autowired
    public LogService(LogRepository logRepository, AuthRepository authRepository, SensitiveDataValidator sensitiveDataValidator, DtoEntityMapper dtoEntityMapper) {
        this.logRepository = logRepository;
        this.authRepository = authRepository;
        this.sensitiveDataValidator = sensitiveDataValidator;
        this.dtoEntityMapper = dtoEntityMapper;
    }


    public void logintoDB(UUID user_id, LogEntity.Action action, String details, String ip_address, LogEntity.Status status) {
        AuthEntity authEntity = authRepository.findById(user_id).orElseThrow(() -> new NullPointerException("User Not Found"));
        if (sensitiveDataValidator.containsSensitiveData(details)) {
            details = "Log Has  [SENSITIVE DATA]";
        }
        if (sensitiveDataValidator.containsSensitiveData(ip_address)) ip_address = "Log Has [SENSITIVE DATA]";
        if (authEntity != null) {
            LogEntity logEntity = new LogEntity();
            logEntity.setAuthEntity(authEntity);
            logEntity.setAction(action);
            logEntity.setDetails(details);
            logEntity.setIp_address(ip_address);
            logEntity.setStatus(status);
            logEntity.setTimestamp(LocalDateTime.now().toString());
            logRepository.save(logEntity);
            log.info("[LogService] Log had been Successful ");
        } else {
            log.error("User ID is invalid");
        }
    }

    public List<LogDTO> retrieveAllLogsFromDB() {
        List<LogEntity> logEntity = logRepository.findAll();
        log.info("[Log Service] Logs Retrieval Successful");
        return logEntity.stream()
                .map(entity -> {
                    LogDTO dto = new LogDTO();
                    dto.setId(entity.getId());
                    dto.setUser_id(entity.getAuthEntity() != null ? entity.getAuthEntity().getId() : null);
                    dto.setAction(entity.getAction());
                    dto.setDetails(entity.getDetails());
                    dto.setIpAddress(entity.getIp_address());
                    dto.setStatus(entity.getStatus());
                    dto.setTimestamp(entity.getTimestamp());
                    return dto;
                })
                .toList();    }

    public List<LogDTO> retrieveAllLogsByUserId(UUID authid) {
        List<LogEntity> logsByUserId = logRepository.findByAuthEntity_Id(authid);
        log.info("[Log Service] Logs Retrieval By UserId is Successful");
        return logsByUserId.stream()
                .map(entity -> {
                    LogDTO dto = new LogDTO();
                    dto.setId(entity.getId());
                    dto.setUser_id(entity.getAuthEntity() != null ? entity.getAuthEntity().getId() : null);
                    dto.setAction(entity.getAction());
                    dto.setDetails(entity.getDetails());
                    dto.setIpAddress(entity.getIp_address());
                    dto.setStatus(entity.getStatus());
                    dto.setTimestamp(entity.getTimestamp());
                    return dto;
                })
                .toList();
    }


    public LogDTO retrieveLogByLogId(UUID logId){
        LogEntity logEntity=logRepository.findById(logId).orElseThrow(()->new LogNotFoundException("Log Has Not Found"));
        log.info("[Log Service] Logs Retrieval By Log Id Successful");
        return   new LogDTO(logEntity.getId(),logEntity.getAuthEntity()!=null? logEntity.getAuthEntity().getId():null,logEntity.getAction(),
                            logEntity.getDetails(), logEntity.getIp_address(), logEntity.getStatus(),logEntity.getTimestamp());
    }


}
