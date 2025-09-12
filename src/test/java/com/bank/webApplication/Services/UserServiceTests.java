package com.bank.webApplication.Services;

import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTests {


    @Mock
    private UserRepository userRepository;
    @Mock
    private DtoEntityMapper dtoEntityMapper;
    @Mock
    private LogService logService;

    @InjectMocks
    private UserService userService;

    private UUID id;
    private UserDto userDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        id=UUID.randomUUID();

        userDto=new UserDto();
        userDto.setName("zaid");
        userDto.setEmail("zargarzaid271@gmail.com");
        userDto.setPhone("9596781234");
        userDto.setRole(Role.USER);

        userEntity=new UserEntity();
        userEntity.setId(id);
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setRole(userDto.getRole());
    }

         //Test For "CreateUser" Method

    @Test
    void testCreateUser(){
        when(dtoEntityMapper.convertToEntity(userDto,UserEntity.class)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(dtoEntityMapper.convertToDto(userEntity,UserDto.class)).thenReturn(userDto);

        UserDto result=userService.CreateUser(userDto,String.valueOf(id));

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(userDto.getName());
        verify(logService,times(1)).logintoDB(eq(id),eq(LogEntity.Action.PROFILE_MANAGEMENT),
                anyString(),eq(result.getName()),eq(LogEntity.Status.SUCCESS));
    }


         //Test For "UpdateUser" Method

    @Test
    void testUpdateUser(){
         when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
         when(userRepository.save(userEntity)).thenReturn(userEntity);
         when(dtoEntityMapper.convertToDto(userEntity,UserDto.class)).thenReturn(userDto);

         UserDto result=userService.UpdateUser(String.valueOf(id),userDto);

         assertThat(result).isNotNull();
         assertThat(result.getName()).isEqualTo(userDto.getName());
         verify(logService,times(1)).logintoDB(eq(id),eq(LogEntity.Action.PROFILE_MANAGEMENT),
                anyString(),eq(result.getName()),eq(LogEntity.Status.SUCCESS));
    }


    //Test For "DisplayUserDetails/GetUserById"  Method

    @Test
    void testGetUserById(){
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(dtoEntityMapper.convertToDto(userEntity,UserDto.class)).thenReturn(userDto);
        UserDto result=userService.getUserById(String.valueOf(id));
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(userDto.getName());
        verify(logService,times(1)).logintoDB(eq(id),eq(LogEntity.Action.PROFILE_MANAGEMENT),
                anyString(),eq(result.getName()),eq(LogEntity.Status.SUCCESS));


    }

             //Testing If Random Id Is Sent It Throwa Exception For UpdateUser Method

    @Test
    void testUpdateUser_NotFound(){
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception=assertThrows(RuntimeException.class,()->
                userService.UpdateUser(String.valueOf(id),userDto));

        assertThat(exception.getMessage()).contains("User Not Found With Id");

    }

    //Testing If Random Id Is Sent It Throwa Exception For GetUserById Method

    @Test
    void testGetUserById_NotFound(){
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        RuntimeException exception=assertThrows(RuntimeException.class,()->
                userService.getUserById(String.valueOf(id)));

        assertThat(exception.getMessage()).contains("User Not Found With Id");


    }



}
