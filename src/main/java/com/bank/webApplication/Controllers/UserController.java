package com.bank.webApplication.Controllers;


import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Services.LogService;
import com.bank.webApplication.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // Get UserProfile By id

    @GetMapping
    public ResponseEntity<UserDto> getUserById() {
        log.info("[UserController] pinged getUserById");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("user controller user id " + userId);
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);

    }

    //Create User After Authentication

    @PutMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        log.info("[UserController] pinged createUser");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("user controller user id " + userId);


        UserDto Created = userService.CreateUser(userDto, userId);
        return ResponseEntity.ok(Created);

    }

    // Update User Details

    @PatchMapping
    public ResponseEntity<UserDto> updateUserDetails(@RequestBody UserDto userDto) {
        log.info("[UserController] pinged updateUserDetails");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto Updated = userService.UpdateUser(userId, userDto);
        return ResponseEntity.ok(Updated);
    }


}
