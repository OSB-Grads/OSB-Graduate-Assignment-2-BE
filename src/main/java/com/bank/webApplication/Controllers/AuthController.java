package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Dto.JwtResponseDto;
import com.bank.webApplication.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService authService;
    //mapping for signup
    @PostMapping("/register")
    public ResponseEntity<JwtResponseDto> register(@RequestBody AuthDto request){
        return ResponseEntity.ok(authService.Signup(request));
    }
    //mapping for login
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody AuthDto request){
        return ResponseEntity.ok(authService.Login(request));
    }
}
