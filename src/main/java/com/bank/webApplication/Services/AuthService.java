package com.bank.webApplication.Services;

import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Util.PasswordHash;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bank.webApplication.Util.DtoEntityMapper;

import java.security.AuthProvider;

@Service
@AllArgsConstructor
public class AuthService {
    @Autowired
    public final AuthRepository authrepository;
    @Autowired
    public DtoEntityMapper dtoEntityMapper;


    public boolean Login(AuthDto authdto){
        return authrepository.findByUsername(authdto.getUserName())
                .map(user->PasswordEncoder.matches(authdto.getPassWord(),user.getPassWord()))
                .orElse(false);
    }
    public AuthDto Signup(AuthDto authdto){
        String hashedPassword= PasswordHash.HashPass(authdto.getPassWord());
        AuthEntity user=AuthEntity.builder()
                .UserName(authdto.getUserName())
                .PassWord(hashedPassword)
                .build();
        authrepository.save(user);
        AuthDto authDto=dtoEntityMapper.convertToDto(user,AuthDto.class);
        return authDto;
    }

}
