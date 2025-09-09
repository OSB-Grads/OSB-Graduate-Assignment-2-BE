package com.bank.webApplication.Repository;


import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.Role;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogRepositoryTests {


    @Mock
    private LogRepository logRepository;


    private UUID testUserId;
    private LogEntity log1, log2;

    @BeforeEach
    public void setup() {
         testUserId = UUID.randomUUID();  // Example user ID
        log1=new LogEntity(UUID.fromString(UUID.randomUUID().toString()),
                new AuthEntity(UUID.fromString(UUID.randomUUID().toString()),"Test1","password123", Role.USER),
                LogEntity.Action.CREATION_MANAGEMENT,
                "Testing Save","IP address", LogEntity.Status.SUCCESS,"25/9/2025");

        log2=new LogEntity(UUID.fromString(UUID.randomUUID().toString()),
                new AuthEntity(UUID.fromString(UUID.randomUUID().toString()),"Test1","password123", Role.USER),
                LogEntity.Action.CREATION_MANAGEMENT,
                "Testing Save","IP address", LogEntity.Status.SUCCESS,"25/9/2025");

    }


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

    @Test
    public void testFindByAuthEntityId_FoundLogs() {
        // Given: Mocking the repository to return logs for the user
        when(logRepository.findByAuthEntity_Id(testUserId)).thenReturn(Arrays.asList(log1, log2));

        // When: We call the method
        List<LogEntity> logs = logRepository.findByAuthEntity_Id(testUserId);

        // Then: Verify the results
        assertNotNull(logs);
        assertEquals(2, logs.size());  // Expecting 2 logs for this user
        assertEquals(log1.getDetails(), logs.get(0).getDetails());
        assertEquals(log2.getDetails(), logs.get(1).getDetails());
    }

    @Test
    public void testFindByAuthEntityId_NoLogsFound() {
        // Given: Mocking the repository to return an empty list when no logs are found for the user
        when(logRepository.findByAuthEntity_Id(testUserId)).thenReturn(Arrays.asList());

        // When: We call the method
        List<LogEntity> logs = logRepository.findByAuthEntity_Id(testUserId);

        // Then: Verify the results
        assertNotNull(logs);
        assertTrue(logs.isEmpty());  // Expecting an empty list
    }


    @Test
    public void testFindByAuthEntityId_UserNotFound() {
        // Given: Mocking the repository to return empty list if the user has no logs
        UUID nonExistingUserId = UUID.randomUUID();
        when(logRepository.findByAuthEntity_Id(nonExistingUserId)).thenReturn(Arrays.asList());

        // When: We call the method
        List<LogEntity> logs = logRepository.findByAuthEntity_Id(nonExistingUserId);

        // Then: Verify the results
        assertNotNull(logs);
        assertTrue(logs.isEmpty());  // Expecting empty list when no logs exist for the user
    }

    @Test
    public void testFindByAuthEntityId_NullUserId() {
        // Given: Mocking the repository for null user ID
        when(logRepository.findByAuthEntity_Id(null)).thenReturn(Arrays.asList());

        // When: We call the method with null UUID
        List<LogEntity> logs = logRepository.findByAuthEntity_Id(null);

        // Then: Verify the results
        assertNotNull(logs);
        assertTrue(logs.isEmpty());  // Expecting an empty list
    }


}
