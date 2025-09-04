package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTests {

    @Mock
    private UserRepository userRepository;

    //Testing SaveUser And FindById
    @Test
    void testSaveAndFindById() {
        UUID id = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setName("zaid");
        user.setEmail("zargarzaid271@gmail.com");
        user.setPhone("7889689012");
        user.setRole(UserEntity.Role.USER);
        user.setCreated_At("2025-09-03");
        user.setUpdated_At("2025-09-03");

        // Mock behavior for save
        when(userRepository.save(user)).thenReturn(user);

        // Mock behavior for findById
        when(userRepository.findById(id)).thenReturn(Optional.of(user));


        UserEntity savedUser = userRepository.save(user);
        Optional<UserEntity> foundUser = userRepository.findById(id);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("zaid");
        assertThat(foundUser.get().getEmail()).isEqualTo("zargarzaid271@gmail.com");
    }



    //Testing If Random Id Is Sent It Returns EMPTY

    @Test
    void testFindById_NotFound() {
        UUID randomId = UUID.randomUUID();

        // Mock behavior: return empty when randomId is used
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        Optional<UserEntity> foundUser = userRepository.findById(randomId);

        assertThat(foundUser).isEmpty();
    }
}
