package com.bank.webApplication.Controllers;


import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Services.LogService;
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


    // Get UserProfile By id

    @GetMapping
    public ResponseEntity<UserDto> getUserById(){

        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto=userService.getUserById(userId);
        return ResponseEntity.ok(userDto);

    }

     //Create User After Authentication

    @PutMapping
    public ResponseEntity<UserDto> createUser( @RequestBody UserDto userDto){

        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto Created= userService.CreateUser(userId, userDto);
        return ResponseEntity.ok(Created);

    }

    // Update User Details

    @PatchMapping
    public ResponseEntity<UserDto> updateUserDetails( @RequestBody UserDto userDto){

         String userId= SecurityContextHolder.getContext().getAuthentication().getName();
         UserDto Updated=userService.UpdateUser(userId,userDto);
         return ResponseEntity.ok(Updated);
    }



}
