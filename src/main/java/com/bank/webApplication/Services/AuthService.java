package com.bank.webApplication.Services;

import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Dto.JwtResponseDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Util.JWTUtil;
import com.bank.webApplication.Util.PasswordHash;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bank.webApplication.Util.DtoEntityMapper;

@Service
@AllArgsConstructor
public class AuthService {
    @Autowired
    public final AuthRepository authrepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;
    public JwtResponseDto Login(AuthDto authdto){
        AuthEntity user=authrepository.findByUsername(authdto.getUserName())
                .orElseThrow(()->new RuntimeException("User not found"));
        if(!passwordEncoder.matches(authdto.getPassWord(),user.getPassWord())){
            throw new RuntimeException("Invalid credentials");
        }
        String token= jwtUtil.generateToken(user.getUserName());
        return new JwtResponseDto(token);
    }
    public JwtResponseDto Signup(AuthDto authdto){
        String hashedPassword= PasswordHash.HashPass(authdto.getPassWord());
        AuthEntity user=AuthEntity.builder()
                .UserName(authdto.getUserName())
                .PassWord(hashedPassword)
                .build();
        authrepository.save(user);
        String token =jwtUtil.generateToken(user.getUserName());
        return new JwtResponseDto(token);
    }

}
