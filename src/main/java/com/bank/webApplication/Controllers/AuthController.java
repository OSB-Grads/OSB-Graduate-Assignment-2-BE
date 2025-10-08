package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Dto.JwtResponseDto;
import com.bank.webApplication.Services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService authService;
    //mapping for signup
    @PostMapping("/register")
    public ResponseEntity<JwtResponseDto> register(@RequestBody AuthDto request){
        log.info("[AuthController] pinged register");
        return ResponseEntity.ok(authService.Signup(request));
    }
    //mapping for login
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody AuthDto request){
        log.info("[AuthController] pinged login");
        return ResponseEntity.ok(authService.Login(request));
    }
    //mapping for refresh token
    @PostMapping("/refreshtoken")
    public ResponseEntity<JwtResponseDto> RefreshAccessToken(@RequestParam String Refreshtoken){
        log.info("[AuthController] pinged RefreshAccessToken");
        return ResponseEntity.ok(authService.RefreshAccessToken(Refreshtoken));
    }
    //mapping for logout
    @PostMapping("/logout")
    public ResponseEntity<Boolean> LogOut (@RequestParam String Refreshtoken){
        log.info("[AuthController] pinged logout");
        return ResponseEntity.ok(authService.LogOut(Refreshtoken));
    }
}
