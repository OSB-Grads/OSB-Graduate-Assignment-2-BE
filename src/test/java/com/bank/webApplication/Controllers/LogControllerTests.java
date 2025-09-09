package com.bank.webApplication.Controllers;


import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Services.LogService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogControllerTests {

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    private UUID testLogId;
    private UUID testUserId;


    @BeforeEach
    public void setup(){
        testLogId=UUID.randomUUID();
        testUserId=UUID.randomUUID();
    }


    @Test
    void testForRetrieveLogMethodWithRecords(){
        LogDTO logDTO1=new LogDTO(UUID.randomUUID(),UUID.randomUUID(), LogEntity.Action.PROFILE_MANAGEMENT,"Test","TestIp Address", LogEntity.Status.SUCCESS,"25/09/2025");
        LogDTO logDTO2=new LogDTO(UUID.randomUUID(),UUID.randomUUID(), LogEntity.Action.PROFILE_MANAGEMENT,"Test2","TestIp2 Address", LogEntity.Status.SUCCESS,"30/09/2025");
        List<LogDTO> mockList=new ArrayList<>();
        mockList.add(logDTO1);
        mockList.add(logDTO2);

        when(logService.retrieveAllLogsFromDB()).thenReturn(mockList);

        ResponseEntity<List<LogDTO>> result=logController.retrieveLogs();

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(mockList);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void testForRetrieveLogMethodWithoutRecords(){
        List<LogDTO> mockList=new ArrayList<>();


        when(logService.retrieveAllLogsFromDB()).thenReturn(mockList);

        ResponseEntity<List<LogDTO>> result=logController.retrieveLogs();

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(new ArrayList<>());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void testForRetrieveLogsByLogId_NotNull(){
        LogDTO mockLogDTO=new LogDTO(testLogId,testUserId, LogEntity.Action.PROFILE_MANAGEMENT,"Test","TestIp Address", LogEntity.Status.SUCCESS,"25/09/2025");

        when(logService.retrieveLogByLogId(testLogId)).thenReturn(mockLogDTO);

        ResponseEntity<LogDTO> result=logController.retrieveLogByLogId(testLogId);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockLogDTO);
        assertThat(result.getBody().getDetails()).isEqualTo(mockLogDTO.getDetails());
    }
    @Test
    void testForRetrieveLogsByLogId_NullLogs(){
        when(logService.retrieveLogByLogId(testLogId)).thenReturn(new LogDTO());

        ResponseEntity<LogDTO> result=logController.retrieveLogByLogId(testLogId);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new LogDTO());
        assertThat(result.getBody().getDetails()).isEqualTo(null);
    }

    @Test
    void testForRetrieveLogsByUserId_NotNull(){
        LogDTO mockLogDTO=new LogDTO(testLogId,testUserId, LogEntity.Action.PROFILE_MANAGEMENT,"Test","TestIp Address", LogEntity.Status.SUCCESS,"25/09/2025");
        LogDTO mockLogDTO2=new LogDTO(testLogId,testUserId, LogEntity.Action.PROFILE_MANAGEMENT,"Test2","TestIp2 Address", LogEntity.Status.SUCCESS,"25/09/2025");
        List<LogDTO> mockList=List.of(mockLogDTO,mockLogDTO2);
        when(logService.retrieveAllLogsByUserId(testUserId)).thenReturn(mockList);

        ResponseEntity<List<LogDTO>> result=logController.retrieveLogsByUserId(testUserId);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockList);
    }






}
