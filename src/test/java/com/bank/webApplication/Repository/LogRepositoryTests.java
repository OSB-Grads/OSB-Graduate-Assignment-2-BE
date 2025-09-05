package com.bank.webApplication.Repository;


import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.Role;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogRepositoryTests {


    @Mock
    private LogRepository logRepository;


    @Test
    void testsToCheckSave(){
        LogEntity logEntity=new LogEntity(UUID.fromString(UUID.randomUUID().toString()),
                                            new AuthEntity(UUID.fromString(UUID.randomUUID().toString()),"Test1","password123", Role.USER),
                                            LogEntity.Action.CREATION_MANAGEMENT,
                                            "Testing Save","IP address", LogEntity.Status.SUCCESS,"25/9/2025");

        when(logRepository.save(logEntity)).thenReturn(logEntity);

        LogEntity result=logRepository.save(logEntity);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(logEntity);

    }

    @Test
    void testToCheckfindAll(){
        LogEntity logEntity=new LogEntity(UUID.fromString(UUID.randomUUID().toString()),
                new AuthEntity(UUID.fromString(UUID.randomUUID().toString()),"Test1","password123", Role.USER),
                LogEntity.Action.CREATION_MANAGEMENT,
                "Testing Save","IP address", LogEntity.Status.SUCCESS,"25/9/2025");
        List<LogEntity> mocklist=new ArrayList<>();
        mocklist.add(logEntity);

        when(logRepository.findAll()).thenReturn(mocklist);

        List<LogEntity> result=logRepository.findAll();

        assertThat(result).isNotNull();
        assertThat(result.getFirst()).isEqualTo(logEntity);
        assertThat(result).isEqualTo(mocklist);

    }

}
