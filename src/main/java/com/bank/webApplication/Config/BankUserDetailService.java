package com.bank.webApplication.Config;

import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
import java.util.UUID;

@Slf4j
@Component
public class BankUserDetailService implements UserDetailsService {
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("[BankUserDetailService] enter loadUserByUsername SUCCESS");
        AuthEntity auth = authRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> {
                    log.info("[BankUserDetailService]  loadUserByUsername:User Not Found FAILURE ");
                    return new UsernameNotFoundException("User Not Found");
                });
        GrantedAuthority authority = new SimpleGrantedAuthority(auth.getRole().name());
        log.info("[BankUserDetailService]  loadUserByUsername SUCCESS");
        return new org.springframework.security.core.userdetails.User(
                auth.getId().toString(), auth.getPassword(), Collections.singletonList(authority)//instead of sending username changed it to userId
        );
    }
}
