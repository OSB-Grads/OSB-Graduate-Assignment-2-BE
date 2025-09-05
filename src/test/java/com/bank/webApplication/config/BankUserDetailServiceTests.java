package com.bank.webApplication.config;

import com.bank.webApplication.Config.BankUserDetailService;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankUserDetailServiceTests {
    @Mock
    private AuthRepository authRepository;
    @InjectMocks
    private BankUserDetailService userDetailsService;
    private UUID testid;
    private AuthEntity authEntity;
    @BeforeEach
    void setup(){
        testid=UUID.randomUUID();
        authEntity=new AuthEntity();
        authEntity.setId(testid);
        authEntity.setPassword("dummyPassword");
        authEntity.setRole(Role.USER);
    }
    @Test
    void loadUserByUsername(){
        when(authRepository.findById(testid)).thenReturn(Optional.of(authEntity));
        UserDetails userDetails= userDetailsService.loadUserByUsername(testid.toString());
        //assert
        assertEquals(testid.toString(),userDetails.getUsername());
        assertEquals("dummyPassword",userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("USER")));
    }
    @Test
    void loadUserByUsername_UserNotFound(){
        when(authRepository.findById(testid)).thenReturn(Optional.empty());
        //assert
         assertThrows(UsernameNotFoundException.class,()->{
             userDetailsService.loadUserByUsername(testid.toString());
         });
    }
}
