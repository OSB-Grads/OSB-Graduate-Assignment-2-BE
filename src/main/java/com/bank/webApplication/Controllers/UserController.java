package com.bank.webApplication.Controllers;


import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Get UserProfile By id

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id){
        UserDto userDto=userService.getUserById(id);
        return ResponseEntity.ok(userDto);

    }

     //Create User After Authentication

    @PutMapping ("/{id}")
    public ResponseEntity<UserDto> createUser(@PathVariable String id , @RequestBody UserDto userDto){
       UserDto Created= userService.CreateUser(id,userDto);
       return ResponseEntity.ok(Created);

    }

    // Update User Details

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUserDetails(@PathVariable String id , @RequestBody UserDto userDto){
         UserDto Updated=userService.UpdateUser(id,userDto);
         return ResponseEntity.ok(userDto);
    }



}
