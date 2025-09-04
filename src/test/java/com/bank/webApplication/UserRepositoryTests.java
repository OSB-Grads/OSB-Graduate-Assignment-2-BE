package com.bank.webApplication;


import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    UserRepository userRepository;



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
        user.setUpdated_At("2025-09-03");
        user.setUpdated_At("2025-09-03");

        userRepository.save(user);

        Optional<UserEntity> foundUser=userRepository.findById(id);

        assertThat(foundUser).isPresent();
        assertThat (foundUser.get().getName()).isEqualTo("zaid");
        assertThat(foundUser.get().getEmail()).isEqualTo("zargarzaid271@gmail.com");

    }



    //Testing If Random Id Is Sent It Returns NULL

    @Test
    void testFindById_NotFound(){
        UUID randomId=UUID.randomUUID();


        Optional<UserEntity> foundUser=userRepository.findById(randomId);

        assertThat(foundUser).isEmpty();
    }


}
