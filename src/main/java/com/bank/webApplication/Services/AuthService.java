package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.InvalidCredentialsException;
import com.bank.webApplication.CustomException.UserAlreadyExistException;
import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Dto.JwtResponseDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Util.JWTUtil;
import com.bank.webApplication.Util.PasswordHash;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    @Autowired
    public LogService logService;
    @Autowired
    public  AuthRepository authrepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserService userService;
    public JwtResponseDto Login(AuthDto authdto){
        //Validates User by taking Username and Password
        AuthEntity user=authrepository.findByUsername(authdto.getUsername())
                .orElseThrow(()->new InvalidCredentialsException("Invalid UserName or User not found"));
        if(!passwordEncoder.matches(authdto.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("Invalid PassWord");
        }
        //Generates JwtToken for valid authenticated user
        String token= jwtUtil.generateToken(String.valueOf(user.getId()), String.valueOf(user.getRole()));
        //Invoke LogService
        logService.logintoDB(user.getId(), LogEntity.Action.AUTHENTICATION,"User Logged in Successfully",user.getUsername(),LogEntity.Status.SUCCESS);
        return new JwtResponseDto(token);
    }
    public JwtResponseDto Signup(AuthDto authdto){
        //Checks if user is already present in the database

        if(authrepository.findByUsername(authdto.getUsername()).isPresent()){
            throw new UserAlreadyExistException("User Already Exist");
        }

        //creates the hash of the password given by the user
        String hashedPassword= PasswordHash.HashPass(authdto.getPassword());
        //registers username and hashed password into database
        AuthEntity user=AuthEntity.builder()
                .username(authdto.getUsername())
                .password(hashedPassword)
                .role(Role.USER)
                .build();
        authrepository.save(user);
        //generates JwtToken
        String token =jwtUtil.generateToken(String.valueOf(user.getId()),user.getRole().name());
        //Invoke LogService
        logService.logintoDB(user.getId(), LogEntity.Action.AUTHENTICATION,"User Signup Successfull",user.getUsername(),LogEntity.Status.SUCCESS);
        return new JwtResponseDto(token);
    }
}
