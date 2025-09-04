package com.bank.webApplication.Repository;
import static org.assertj.core.api.Assertions.assertThat;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthRepositoryTests {

    @Mock
    private AuthRepository authRepository;
    @Test
    void testSaveAndfindByUsername(){
      String username="DummyUserName";
      AuthEntity authEntity=new AuthEntity();
      authEntity.setId(UUID.randomUUID());
      authEntity.setUsername(username);
      authEntity.setPassword("DummyUserPassword");
      authEntity.setRole(Role.USER);
      //mock behaviour for save
      when(authRepository.save(authEntity)).thenReturn(authEntity);
     //mock behaviour for findByUsername
      when(authRepository.findByUsername(username)).thenReturn(Optional.of(authEntity));
      AuthEntity savedAuth=authRepository.save(authEntity);
      Optional<AuthEntity> foundUserName=authRepository.findByUsername(username);
      //assert
        assertThat(savedAuth).isNotNull();
        assertThat(foundUserName).isPresent();
        assertThat(foundUserName.get().getUsername()).isEqualTo("DummyUserName");


    }
    @Test
    void testfindByUsername_NotFound(){
        String username="DummyUserName";
        //Mock Behaviour
        when(authRepository.findByUsername(username)).thenReturn(Optional.empty());
        Optional<AuthEntity> foundUserName=authRepository.findByUsername(username);
        //assert
        assertThat(foundUserName).isEmpty();
    }
}
