package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.InvalidCredentialsException;
import com.bank.webApplication.CustomException.UserAlreadyExistException;
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
        //Validates User by taking Username and Password
        AuthEntity user=authrepository.findByUsername(authdto.getUserName())
                .orElseThrow(()->new InvalidCredentialsException("Invalid UserName or User not found"));
        if(!passwordEncoder.matches(authdto.getPassWord(),user.getPassWord())){
            throw new InvalidCredentialsException("Invalid PassWord");
        }
        //Generates JwtToken for valid authenticated user
        String token= jwtUtil.generateToken(user.getUserName());
        return new JwtResponseDto(token);
    }
    public JwtResponseDto Signup(AuthDto authdto){
        //Checks if user is already present in the database
        if(authrepository.findByUsername(authdto.getUserName()).isPresent()){
            throw new UserAlreadyExistException("User Already Exist");
        }
        //creates the hash of the password given by the user
        String hashedPassword= PasswordHash.HashPass(authdto.getPassWord());
        //registers username and hashed password into database
        AuthEntity user=AuthEntity.builder()
                .UserName(authdto.getUserName())
                .PassWord(hashedPassword)
                .build();
        authrepository.save(user);
        //generates JwtToken
        String token =jwtUtil.generateToken(user.getUserName());
        return new JwtResponseDto(token);
    }

}
