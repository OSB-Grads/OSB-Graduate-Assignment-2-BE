package com.bank.webApplication.Config;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BankUserDetailService implements UserDetailsService {
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthEntity auth=authRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User Not Found"));
        UserEntity user=userRepository.findById(auth.getId())
                .orElseThrow(()->new UsernameNotFoundException("User Not Found"));
        GrantedAuthority authority=new SimpleGrantedAuthority(user.getRole().name());
        return new org.springframework.security.core.userdetails.User(
                auth.getUserName(), auth.getPassWord(), Collections.singletonList(authority)
        );
    }
}
