package com.bank.webApplication.UserIntegrationTests;

import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Services.LogService;
import com.bank.webApplication.Services.UserService;
import com.bank.webApplication.Util.DtoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DtoEntityMapper mapper;

    @Autowired
    private LogService logService;

    private UUID testUserId;
    private UserEntity existingUser;

    @BeforeEach
    void setUp() {
        // Create and save AuthEntity first (let Hibernate generate the ID)
        AuthEntity testAuth = AuthEntity.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build();
        AuthEntity savedAuth = authRepository.save(testAuth);
        testUserId = savedAuth.getId(); // Get the generated ID

        // Create and save UserEntity with the same ID
        existingUser = new UserEntity(
                testUserId,
                "Test User",
                "test@example.com",
                "1234567890",
                "2023-01-01 10:00:00",
                "2023-01-01 10:00:00",
                "Test Address",
                Role.USER
        );
        userRepository.save(existingUser);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        // Create AuthEntity first and get the generated ID
        AuthEntity newAuth = AuthEntity.builder()
                .username("newuser")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build();
        AuthEntity savedAuth = authRepository.save(newAuth);
        UUID newUserId = savedAuth.getId();

        UserDto userDto = UserDto.builder()
                .name("New User")
                .email("new.user@example.com")
                .phone("9876543210")
                .role(Role.USER)
                .Address("New User Address")
                .build();


        UserDto result = userService.CreateUser(userDto, newUserId.toString());


        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("new.user@example.com");
        assertThat(result.getPhone()).isEqualTo("9876543210");
        assertThat(result.getAddress()).isEqualTo("New User Address");

        // Verify user was saved in database
        UserEntity savedUser = userRepository.findById(newUserId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getCreated_At()).isNotNull();
        assertThat(savedUser.getUpdated_At()).isNotNull();
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // When
        UserDto result = userService.getUserById(testUserId.toString());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getPhone()).isEqualTo("1234567890");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.getAddress()).isEqualTo("Test Address");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Given
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .phone("9998887777")
                .role(Role.ADMIN)
                .Address("Updated Address")
                .build();

        // When
        UserDto result = userService.UpdateUser(testUserId.toString(), updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getPhone()).isEqualTo("9998887777");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.getAddress()).isEqualTo("Updated Address");

        // Verify database was updated
        UserEntity updatedUser = userRepository.findById(testUserId).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getUpdated_At()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForGet() {
        // Create auth entity and get the generated ID
        AuthEntity nonExistentAuth = AuthEntity.builder()
                .username("nonexistent")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build();
        AuthEntity savedAuth = authRepository.save(nonExistentAuth);
        UUID nonExistentUserId = savedAuth.getId();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserById(nonExistentUserId.toString()));

        assertThat(exception.getMessage()).contains("User Not Found With Id");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForUpdate() {
        // Create auth entity and get the generated ID
        AuthEntity nonExistentAuth = AuthEntity.builder()
                .username("nonexistent2")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build();
        AuthEntity savedAuth = authRepository.save(nonExistentAuth);
        UUID nonExistentUserId = savedAuth.getId();

        UserDto updateDto = UserDto.builder()
                .name("Some Name")
                .email("some@example.com")
                .build();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.UpdateUser(nonExistentUserId.toString(), updateDto));

        assertThat(exception.getMessage()).contains("User Not Found With Id");
    }

    @Test
    void shouldHandlePartialUpdate() {
        // Given - Update only name and email
        UserDto partialUpdateDto = UserDto.builder()
                .name("Partially Updated")
                .email("partial@example.com")
                .build();


        UserDto result = userService.UpdateUser(testUserId.toString(), partialUpdateDto);


        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Partially Updated");
        assertThat(result.getEmail()).isEqualTo("partial@example.com");

        // Other fields should remain unchanged or have default values

        assertThat(result.getPhone()).isNull(); // Since not provided in DTO
        assertThat(result.getRole()).isNull(); // Since not provided in DTO
    }
}