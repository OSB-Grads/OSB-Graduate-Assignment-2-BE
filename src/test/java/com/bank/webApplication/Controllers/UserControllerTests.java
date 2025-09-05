package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class UserControllerTests {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    //Creating a Dummy userId
    private final String userId="123e4567-e89b-12d3";


    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(userId,null,null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void testCreateUser(){
        UserDto inputDto=new UserDto();
        inputDto.setName("zaid");

        UserDto createdDto=new UserDto();
        createdDto.setName("zaid");
        createdDto.setEmail("zargarzaid271@gmail.com");

        when(userService.CreateUser(inputDto,userId)).thenReturn(createdDto);
        ResponseEntity<UserDto>response=userController.createUser(inputDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("zargarzaid271@gmail.com");

        verify(userService,times(1)).CreateUser(inputDto,userId);
    }

    @Test
    void testGetUserById(){
        UserDto mockDto=new UserDto();
        mockDto.setName("zaid");

        when(userService.getUserById(userId)).thenReturn(mockDto);

        ResponseEntity<UserDto> response=userController.getUserById();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("zaid");

        verify(userService,times(1)).getUserById(userId);

    }

    @Test
    void testUpdateUserDetails(){
        UserDto input=new UserDto();
        input.setName("zaid");

        UserDto updated=new UserDto();
        updated.setName("hello");
        updated.setEmail("zargarzaid271@gmail.com");

        when(userService.UpdateUser(userId,input)).thenReturn(updated);

        ResponseEntity<UserDto>response=userController.updateUserDetails(input);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("hello");

        verify(userService,times(1)).UpdateUser(userId,input);


    }
}
