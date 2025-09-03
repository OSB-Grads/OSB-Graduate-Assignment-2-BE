package com.bank.webApplication.Services;


import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.UserEntity;
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
    private UserRepository userRepository;
    private DtoEntityMapper dtoEntityMapper;
    private SensitiveDataValidator sensitiveDataValidator;

    @Autowired
    public LogService(LogRepository logRepository,UserRepository userRepository,SensitiveDataValidator sensitiveDataValidator,DtoEntityMapper dtoEntityMapper){
        this.logRepository=logRepository;
        this.userRepository=userRepository;
        this.sensitiveDataValidator=sensitiveDataValidator;
        this.dtoEntityMapper=dtoEntityMapper;
    }


    public void logintoDB(UUID user_id, LogEntity.Action action, String details, String ip_address, LogEntity.Status status){
        UserEntity userEntity=userRepository.findById(user_id).orElseThrow(()->new NullPointerException("User Not Found"));
        if(sensitiveDataValidator.containsSensitiveData(details)){
            details="Log Has  [SENSITIVE DATA]";
        }
        if(sensitiveDataValidator.containsSensitiveData(ip_address))ip_address="Log Has [SENSITIVE DATA]";
        if(userEntity!=null) {
            LogEntity logEntity = new LogEntity();
            logEntity.setUserEntity(userEntity);
            logEntity.setAction(action);
            logEntity.setDetails(details);
            logEntity.setIp_address(ip_address);
            logEntity.setStatus(status);
            logEntity.setTimestamp(LocalDateTime.now().toString());
            logRepository.save(logEntity);
            log.info("[LogService] Log had been Successful ");
        }
        else{
         log.error("User ID is invalid");
        }
    }

    public List<LogDTO> retrieveAllLogsFromDB(){
        List<LogEntity> logEntity=logRepository.findAll();
        log.info("[Log Service] Logs Retrieval Successful");
        return logEntity.stream().map((logEntityItem->dtoEntityMapper.convertToDto(logEntityItem,LogDTO.class))).toList();
    }

}
