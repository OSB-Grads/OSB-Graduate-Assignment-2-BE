package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.Role;
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

    // Helper function for create user
    private UserEntity createTestUser(UUID id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setName("zaid");
        user.setEmail("zargarzaid271@gmail.com");
        user.setPhone("7889689012");
        user.setRole(Role.USER);
        user.setCreated_At("2025-09-03");
        user.setUpdated_At("2025-09-03");
        user.setAddress("Delhi");
        return user;
    }

    //Testing SaveUser And FindById
    @Test
    void testSaveAndFindById() {


        UUID id = UUID.randomUUID();
        UserEntity user = createTestUser(id);

        // Mock behavior for save
        when(userRepository.save(user)).thenReturn(user);

        // Mock behavior for findById
        when(userRepository.findById(id)).thenReturn(Optional.of(user));


        UserEntity savedUser = userRepository.save(user);
        Optional<UserEntity> foundUser = userRepository.findById(id);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(id);
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
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


// FindByEmail  - User Exists
    @Test
    void testFindByEmail() {
        UUID id = UUID.randomUUID();
        UserEntity user = createTestUser(id);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<UserEntity> foundUser = userRepository.findByEmail(user.getEmail());

        assertThat(foundUser).isPresent();
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.get().getId()).isEqualTo(id);
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    // FindByEmail - User doesn't exist
    @Test
    void testFindByEmail_NotFound(){

        String userMail = "testUser@gmail.com";
        when(userRepository.findByEmail(userMail)).thenReturn(Optional.empty());

        Optional<UserEntity> user = userRepository.findByEmail(userMail);

        assertThat(user).isEmpty();

    }



}
