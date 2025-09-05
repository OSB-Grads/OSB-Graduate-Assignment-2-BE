package com.bank.webApplication.Services;

import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.LogRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import com.bank.webApplication.Util.SensitiveDataValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(dtoEntityMapper.convertToDto(log1, LogDTO.class)).thenReturn(dto1);
        when(dtoEntityMapper.convertToDto(log2, LogDTO.class)).thenReturn(dto2);

        // Act
        List<LogDTO> result = logService.retrieveAllLogsFromDB();

        // Assert
        assertEquals(2, result.size());
        verify(logRepository).findAll();
        verify(dtoEntityMapper, times(2)).convertToDto(any(), eq(LogDTO.class));
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
}
