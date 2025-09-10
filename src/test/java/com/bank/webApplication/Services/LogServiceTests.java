package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.LogNotFoundException;
import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.LogRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import com.bank.webApplication.Util.SensitiveDataValidator;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogServiceTests {

    @Mock
    private LogRepository logRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private SensitiveDataValidator sensitiveDataValidator;

    @Mock
    private DtoEntityMapper dtoEntityMapper;

    @InjectMocks
    private LogService logService;

    private UUID userId;
    private AuthEntity authEntity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        authEntity = new AuthEntity();
    }

    @Test
    void testLogintoDB_successfulLogWithoutSensitiveData() {
        // Arrange
        when(authRepository.findById(userId)).thenReturn(Optional.of(authEntity));
        when(sensitiveDataValidator.containsSensitiveData(anyString())).thenReturn(false);

        // Act
        logService.logintoDB(userId, LogEntity.Action.AUTHENTICATION, "Normal details", "127.0.0.1", LogEntity.Status.SUCCESS);

        // Assert
        verify(logRepository, times(1)).save(any(LogEntity.class));
    }

    @Test
    void testLogintoDB_withSensitiveData() {
        // Arrange
        when(authRepository.findById(userId)).thenReturn(Optional.of(authEntity));
        when(sensitiveDataValidator.containsSensitiveData("sensitive")).thenReturn(true);

        // Act
        logService.logintoDB(userId, LogEntity.Action.AUTHENTICATION, "sensitive", "sensitive", LogEntity.Status.SUCCESS);

        // Assert
        ArgumentCaptor<LogEntity> captor = ArgumentCaptor.forClass(LogEntity.class);
        verify(logRepository).save(captor.capture());

        LogEntity savedLog = captor.getValue();
        assertEquals("Log Has  [SENSITIVE DATA]", savedLog.getDetails());
        assertEquals("Log Has [SENSITIVE DATA]", savedLog.getIp_address());
    }

    @Test
    void testLogintoDB_userNotFound() {
        // Arrange
        when(authRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                logService.logintoDB(userId, LogEntity.Action.AUTHENTICATION, "details", "127.0.0.1", LogEntity.Status.SUCCESS)
        );

        verify(logRepository, never()).save(any());
    }

    @Test
    void testRetrieveAllLogsFromDB() {
        // Arrange
        LogEntity log1 = new LogEntity();
        LogEntity log2 = new LogEntity();
        List<LogEntity> entities = List.of(log1, log2);

        LogDTO dto1 = new LogDTO();
        LogDTO dto2 = new LogDTO();

        when(logRepository.findAll()).thenReturn(entities);


        // Act
        List<LogDTO> result = logService.retrieveAllLogsFromDB();

        // Assert
        assertEquals(2, result.size());
        verify(logRepository).findAll();

    }
    @Test
    void testGetAllProducts_ThrowsException() {
        // Mock Behaviour
        when(logRepository.findAll()).thenThrow(new RuntimeException("Error: No Products Found in Database. "));

        //Logic
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            logService.retrieveAllLogsFromDB();
        });

        //Assertions
        verify(logRepository).findAll();
        verifyNoInteractions(dtoEntityMapper);
    }

    @Test
    void testToRetrievaAllLogsByUserId(){
        UUID testUserId=UUID.randomUUID();
        AuthEntity authEntity=new AuthEntity(testUserId,"test","123456", Role.USER);
        LogEntity logEntity1=new LogEntity(UUID.randomUUID(),authEntity, LogEntity.Action.PROFILE_MANAGEMENT,"Test","TestIp Address", LogEntity.Status.SUCCESS,"25/09/2025");
        LogEntity logEntity2=new LogEntity(UUID.randomUUID(),authEntity, LogEntity.Action.PROFILE_MANAGEMENT,"Test2","TestIp2 Address", LogEntity.Status.SUCCESS,"30/09/2025");
        List<LogEntity> mockList=new ArrayList<>();
        mockList.add(logEntity1);
        mockList.add(logEntity2);
        LogDTO logDTO1=new LogDTO(logEntity1.getId(),testUserId, LogEntity.Action.PROFILE_MANAGEMENT,"Test","TestIp Address", LogEntity.Status.SUCCESS,"25/09/2025");
        LogDTO logDTO2=new LogDTO(logEntity2.getId(),testUserId, LogEntity.Action.PROFILE_MANAGEMENT,"Test2","TestIp2 Address", LogEntity.Status.SUCCESS,"30/09/2025");
        List<LogDTO> expectedResult=new ArrayList<>();
        expectedResult.add(logDTO1);
        expectedResult.add(logDTO2);

        when(logRepository.findByAuthEntity_Id(testUserId)).thenReturn(mockList);

        List<LogDTO> resultFromFunction=logService.retrieveAllLogsByUserId(testUserId);

        assertThat(resultFromFunction).isNotNull();
        assertThat(resultFromFunction).isEqualTo(expectedResult);
        assertThat(resultFromFunction).hasSize(2);
    }

    @Test
    void testRetrieveAllLogsByUserId_WhenNoLogsExist() {
        UUID authId=UUID.randomUUID();
        when(logRepository.findByAuthEntity_Id(authId)).thenReturn(List.of());

        // Act
        List<LogDTO> result = logService.retrieveAllLogsByUserId(authId);

        // Assert
        assertThat(result).isEmpty();
        verify(logRepository, times(1)).findByAuthEntity_Id(authId);
    }

    @Test
    void testRetrieveAllLogsByUserId_WhenAuthEntityIsNull() {
        // Arrange

        LogEntity logEntity = new LogEntity();
        logEntity.setId(UUID.randomUUID());
        logEntity.setAuthEntity(null); // no user
        logEntity.setAction(LogEntity.Action.PROFILE_MANAGEMENT);
        logEntity.setDetails("No user found");
        logEntity.setIp_address("192.168.0.1");
        logEntity.setStatus(LogEntity.Status.FAILURE);
        logEntity.setTimestamp("2025-09-08T18:00:00.000");

        when(logRepository.findByAuthEntity_Id(null)).thenReturn(List.of(logEntity));

        // Act
        List<LogDTO> result = logService.retrieveAllLogsByUserId(null);

        // Assert
        assertThat(result).hasSize(1);
        LogDTO dto = result.get(0);
        assertThat(dto.getUser_id()).isNull(); // since authEntity was null
        assertThat(dto.getAction()).isEqualTo(LogEntity.Action.PROFILE_MANAGEMENT);
        assertThat(dto.getStatus()).isEqualTo(LogEntity.Status.FAILURE);

        verify(logRepository, times(1)).findByAuthEntity_Id(null);
    }

    @Test
    void testRetrieveLogByLogId_WhenLogExists() {
        // Arrange
        UUID userId=UUID.randomUUID();
        UUID logId=UUID.randomUUID();
        AuthEntity authEntity = new AuthEntity();
        authEntity.setId(userId);

        LogEntity logEntity = new LogEntity(
                logId,
                authEntity,
                LogEntity.Action.AUTHENTICATION,
                "User login success",
                "127.0.0.1",
                LogEntity.Status.SUCCESS,
                "2025-09-08T18:30:00"
        );

        when(logRepository.findById(logId)).thenReturn(Optional.of(logEntity));
        LogDTO expected=new LogDTO(logId,authEntity.getId(), LogEntity.Action.AUTHENTICATION,"User login success",
                "127.0.0.1",
                LogEntity.Status.SUCCESS,
                "2025-09-08T18:30:00");

        // Act
        LogDTO result = logService.retrieveLogByLogId(logId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expected);


        verify(logRepository, times(1)).findById(logId);
    }

    @Test
    void testRetrieveLogByUserId_WhenLogDoesNotExist() {
        // Arrange
        UUID logId=UUID.randomUUID();
        when(logRepository.findById(logId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(LogNotFoundException.class, () -> {
            logService.retrieveLogByLogId(logId);
        });

        // Then: Verify the exception message
        assertEquals("Log Has Not Found", exception.getMessage());

        verify(logRepository, times(1)).findById(logId);
    }



}
