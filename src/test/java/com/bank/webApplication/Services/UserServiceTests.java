package com.bank.webApplication.Services;

import com.bank.webApplication.Controllers.UserControllerTests;
import com.bank.webApplication.CustomException.UserNotFoundException;
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

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private UUID userId1 = UUID.randomUUID() , userId2 = UUID.randomUUID();
    private UserDto userDto1, userDto2;
    private UserEntity userEntity1, userEntity2;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        userDto2 =new UserDto();
        userDto2.setName("Test");
        userDto2.setEmail("testUser@gmail.com");
        userDto2.setPhone("123456789");
        userDto2.setRole(Role.USER);
        userDto2.setAddress("dummy address");

        userEntity1 =new UserEntity();
        userEntity1.setId(userId2);
        userEntity1.setName(userDto2.getName());
        userEntity1.setEmail(userDto2.getEmail());
        userEntity1.setPhone(userDto2.getPhone());
        userEntity1.setRole(userDto2.getRole());

        userDto1 =new UserDto();
        userDto1.setName("zaid");
        userDto1.setEmail("zargarzaid271@gmail.com");
        userDto1.setPhone("9596781234");
        userDto1.setRole(Role.USER);
        userDto1.setAddress("dummy address");

        userEntity1 =new UserEntity();
        userEntity1.setId(userId1);
        userEntity1.setName(userDto1.getName());
        userEntity1.setEmail(userDto1.getEmail());
        userEntity1.setPhone(userDto1.getPhone());
        userEntity1.setRole(userDto1.getRole());

    }

         //Test For "CreateUser" Method

    @Test
    void testCreateUser(){
        when(dtoEntityMapper.convertToEntity(userDto1,UserEntity.class)).thenReturn(userEntity1);
        when(userRepository.save(userEntity1)).thenReturn(userEntity1);
        when(dtoEntityMapper.convertToDto(userEntity1,UserDto.class)).thenReturn(userDto1);

        UserDto result=userService.CreateUser(userDto1,String.valueOf(userId1));

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(userDto1.getName());
        verify(logService,times(1)).logintoDB(eq(userId1),eq(LogEntity.Action.PROFILE_MANAGEMENT),
                anyString(),eq(result.getName()),eq(LogEntity.Status.SUCCESS));
    }


         //Test For "UpdateUser" Method

    @Test
    void testUpdateUser(){
         when(userRepository.findById(userId1)).thenReturn(Optional.of(userEntity1));
         when(userRepository.save(userEntity1)).thenReturn(userEntity1);
         when(dtoEntityMapper.convertToDto(userEntity1,UserDto.class)).thenReturn(userDto1);

         UserDto result=userService.UpdateUser(String.valueOf(userId1), userDto1);

         assertThat(result).isNotNull();
         assertThat(result.getName()).isEqualTo(userDto1.getName());
         verify(logService,times(1)).logintoDB(eq(userId1),eq(LogEntity.Action.PROFILE_MANAGEMENT),
                anyString(),eq(result.getName()),eq(LogEntity.Status.SUCCESS));
    }


    //Test For "DisplayUserDetails/GetUserById"  Method

    @Test
    void testGetUserById(){
        when(userRepository.findById(userId1)).thenReturn(Optional.of(userEntity1));
        when(dtoEntityMapper.convertToDto(userEntity1,UserDto.class)).thenReturn(userDto1);
        UserDto result=userService.getUserById(String.valueOf(userId1));
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(userDto1.getName());
        verify(logService,times(1)).logintoDB(eq(userId1),eq(LogEntity.Action.PROFILE_MANAGEMENT),
                anyString(),eq(result.getName()),eq(LogEntity.Status.SUCCESS));


    }

             //Testing If Random Id Is Sent It Throws Exception For UpdateUser Method

    @Test
    void testUpdateUser_NotFound(){
        when(userRepository.findById(userId1)).thenReturn(Optional.empty());

        RuntimeException exception=assertThrows(RuntimeException.class,()->
                userService.UpdateUser(String.valueOf(userId1), userDto1));

        assertThat(exception.getMessage()).contains("User Not Found With Id");

    }

    //Testing If Random Id Is Sent It Throws Exception For GetUserById Method

    @Test
    void testGetUserById_NotFound(){
        when(userRepository.findById(userId1)).thenReturn(Optional.empty());
        RuntimeException exception=assertThrows(RuntimeException.class,()->
                userService.getUserById(String.valueOf(userId1)));

        assertThat(exception.getMessage()).contains("User Not Found With Id");


    }

    // Test getAllUsers - when Records exist
    @Test
    void testGetAllUsers_WithRecords(){

        List<UserEntity> mockList = new ArrayList<>();
        mockList.add(userEntity1);
        mockList.add(userEntity2);

        when(userRepository.findAll()).thenReturn(mockList);
        when(dtoEntityMapper.convertToDto(userEntity1, UserDto.class)).thenReturn(userDto1);
        when(dtoEntityMapper.convertToDto(userEntity2, UserDto.class)).thenReturn(userDto2);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("zaid", result.get(0).getName());
        assertEquals("Test", result.get(1).getName());

        verify(userRepository, times(1)).findAll();
        verify(dtoEntityMapper, times(1)).convertToDto(userEntity1, UserDto.class);
        verify(dtoEntityMapper, times(1)).convertToDto(userEntity2, UserDto.class);
    }

    // Test getAllUsers - when no Records exist
    @Test
    void testGetAllUsers_WithoutRecords(){
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getAllUsers()
        );

        assertEquals("No Users Exist in Database", exception.getMessage());

        verify(userRepository, times(1)).findAll();
        verifyNoInteractions(dtoEntityMapper);
    }

}
