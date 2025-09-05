package com.bank.webApplication.Controllers;
import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Dto.JwtResponseDto;
import com.bank.webApplication.Services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    void testRegister(){
        AuthDto authDto=new AuthDto();
        authDto.setUsername("DummyUserName");
        authDto.setPassword("DummyPassword");
        //mock JWT Token
        JwtResponseDto mockResponse=new JwtResponseDto("DummyToken");
        when(authService.Signup(any(AuthDto.class))).thenReturn(mockResponse);
        //mock call to the controller("/register")
        ResponseEntity<JwtResponseDto> response=authController.register(authDto);
        //assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("DummyToken");
    }
    @Test
    void testLogin(){
        AuthDto authDto=new AuthDto();
        authDto.setUsername("DummyUserName");
        authDto.setPassword("DummyPassword");
        //mock JWT Token
        JwtResponseDto mockResponse=new JwtResponseDto("DummyToken");
        when(authService.Login(authDto)).thenReturn(mockResponse);
        //mock call to the controller("/login")
        ResponseEntity<JwtResponseDto> response=authController.login(authDto);
        //assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("DummyToken");
    }
}
