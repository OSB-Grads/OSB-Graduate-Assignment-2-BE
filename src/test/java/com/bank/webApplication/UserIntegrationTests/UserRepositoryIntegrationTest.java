package com.bank.webApplication.UserIntegrationTests;

import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = "spring.sql.init.mode=never")
public class UserRepositoryIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Save and retrieve user by ID")
    void testSaveAndFindById() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhone("1234567890");
        user.setRole(Role.USER);

        userRepository.save(user);

        Optional<UserEntity> found = userRepository.findById(userId);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Update user entity")
    void testUpdateUser() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setPhone("0987654321");
        user.setRole(Role.USER);

        userRepository.save(user);

        user.setName("Jane Smith");
        userRepository.save(user);

        UserEntity updated = userRepository.findById(userId).get();
        assertThat(updated.getName()).isEqualTo("Jane Smith");
    }
}
