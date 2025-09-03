package com.bank.webApplication.Controllers;


import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthRepository authRepository;

    // Get UserProfile By id

    @GetMapping
    public ResponseEntity<UserDto> getUserById(){

        String userName= SecurityContextHolder.getContext().getAuthentication().getName();
        AuthEntity authEntity =authRepository.findByUsername(userName)
                .orElseThrow(()->new RuntimeException("user not found " +userName));

        UserDto userDto=userService.getUserById(String.valueOf(authEntity.getId()));

        return ResponseEntity.ok(userDto);

    }

     //Create User After Authentication

    @PutMapping
    public ResponseEntity<UserDto> createUser( @RequestBody UserDto userDto){

        String userName= SecurityContextHolder.getContext().getAuthentication().getName();
       AuthEntity authEntity =authRepository.findByUsername(userName)
                .orElseThrow(()->new RuntimeException("user not found " +userName));

       UserDto Created= userService.CreateUser(String.valueOf(authEntity.getId()),userDto);
       return ResponseEntity.ok(Created);

    }

    // Update User Details

    @PatchMapping
    public ResponseEntity<UserDto> updateUserDetails( @RequestBody UserDto userDto){

        String userName= SecurityContextHolder.getContext().getAuthentication().getName();
        AuthEntity authEntity =authRepository.findByUsername(userName)
                .orElseThrow(()->new RuntimeException("user not found " +userName));

         UserDto Updated=userService.UpdateUser(String.valueOf(authEntity.getId()),userDto);

         return ResponseEntity.ok(userDto);
    }



}
