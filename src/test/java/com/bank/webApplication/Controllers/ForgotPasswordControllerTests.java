package com.bank.webApplication.Controllers;

import com.bank.webApplication.Entity.OTPEntity;
import com.bank.webApplication.Services.ForgotPasswordService;
import jakarta.persistence.ManyToOne;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordControllerTests {

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;

    @Mock
    private ForgotPasswordService forgotPasswordService;

    public OTPEntity otpEntity1, otpEntity2

    @BeforeEach
    public void setup(){
        otpEntity1 = new OTPEntity();
    }
}
