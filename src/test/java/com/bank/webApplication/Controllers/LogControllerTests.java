package com.bank.webApplication.Controllers;


import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Services.LogService;
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



}
