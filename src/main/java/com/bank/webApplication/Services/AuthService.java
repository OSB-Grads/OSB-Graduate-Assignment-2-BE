package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.InvalidCredentialsException;
import com.bank.webApplication.CustomException.UserAlreadyExistException;
import com.bank.webApplication.Dto.AuthDto;
import com.bank.webApplication.Dto.JwtResponseDto;
import com.bank.webApplication.Entity.AuthEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.RefreshTokenEntity;
import com.bank.webApplication.Entity.Role;
import com.bank.webApplication.Repository.AuthRepository;
import com.bank.webApplication.Repository.RefreshTokenRepository;
import com.bank.webApplication.Util.JWTUtil;
import com.bank.webApplication.Util.PasswordHash;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserService userService;


    public JwtResponseDto Login(AuthDto authdto) {
        log.info("[AuthService] Entered Login SUCCESS");
        //Validates User by taking Username and Password
        AuthEntity user = authrepository.findByUsername(authdto.getUsername())
                .orElseThrow(() ->
                {
                    log.error("[AuthService] Login: Invalid UserName or User not found FAILED");
                    return new InvalidCredentialsException("Invalid UserName or User not found");
                });
        if (!passwordEncoder.matches(authdto.getPassword(), user.getPassword())) {
            log.error("[AuthService] Login: Invalid PassWord FAILED");
            throw new InvalidCredentialsException("Invalid PassWord");
        }
        //Generates JwtToken for valid authenticated user
        String token = jwtUtil.generateToken(String.valueOf(user.getId()), String.valueOf(user.getRole()));
        RefreshTokenEntity refreshToken = jwtUtil.generateRefreshToken(user);
        refreshTokenRepository.save(refreshToken);
        //Invoke LogService
//        logService.logintoDB(user.getId(), LogEntity.Action.AUTHENTICATION, "User Logged in Successfully", user.getUsername(), LogEntity.Status.SUCCESS);
        log.info("[AuthService]  Login SUCCESS");
        return new JwtResponseDto(token, refreshToken.getRefreshToken());
    }


    public JwtResponseDto Signup(AuthDto authdto) {
        log.info("[AuthService] Entered Signup SUCCESS");
        //Checks if user is already present in the database
        Optional<AuthEntity> a = authrepository.findByUsername(authdto.getUsername());
        if (authrepository.findByUsername(authdto.getUsername()).isPresent()) {
            log.error("[AuthService] Signup: User Already Exist FAILURE");
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
        log.info("[AuthService]  User Saved To DataBase SUCCESS");
        //generates JwtToken
        String token = jwtUtil.generateToken(String.valueOf(user.getId()), user.getRole().name());
        //generates RefreshToken
        RefreshTokenEntity refreshToken = jwtUtil.generateRefreshToken(user);
        //saves the refreshtoken in database
        refreshTokenRepository.save(refreshToken);
        log.info("[AuthService]  RefreshToken Saved To DataBase SUCCESS");
        //Invoke LogService
//        logService.logintoDB(user.getId(), LogEntity.Action.AUTHENTICATION, "User Signup Successfull", user.getUsername(), LogEntity.Status.SUCCESS);
        log.info("[AuthService]  SignUp SUCCESS");
        return new JwtResponseDto(token, refreshToken.getRefreshToken());
    }

    public void updatePassword(String password, UUID userId){
        String hashedPassword= PasswordHash.HashPass(password);
        authrepository.findById(userId).get().setPassword(hashedPassword);
        log.info("[RESET PASSWORD] Password Updation SUCCESS");
//        logService.logintoDB(userId, LogEntity.Action.PROFILE_MANAGEMENT, "Password Updation SUCCESS",userId.toString()
//                ,LogEntity.Status.SUCCESS);
    }


    public JwtResponseDto RefreshAccessToken(String refreshToken) {
        log.info("[AuthService] Entered RefreshAccessToken SUCCESS");
        //check refreshtoken is present in db and matches with the one sent by the frontend
        RefreshTokenEntity RefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.error("[AuthService] RefreshAccessToken: Invalid Refresh Token FAILURE");
                    return new RuntimeException("Invalid Refresh Token");
                });
        //checks for expiry of refresh token
        if (RefreshToken.getExpiry().isBefore(Instant.now())) {
            //deletes the refresh token from db
            refreshTokenRepository.delete(RefreshToken);
            log.error("[AuthService] RefreshAccessToken: Token Has Expired FAILURE");
            throw new RuntimeException("Token Has Expired");
        }
        //generates a new access token if it is expired
        AuthEntity data = RefreshToken.getAuthEntity();
        //generates new accesstoken
        String newToken = jwtUtil.generateToken(String.valueOf(data.getId()), data.getRole().name());
//        logService.logintoDB(data.getId(), LogEntity.Action.AUTHENTICATION, "Refresh token Generated Successfully", data.getUsername(), LogEntity.Status.SUCCESS);
        log.info("[AuthService] RefreshAccessToken  SUCCESS");
        //sends back the new accesstoken along with refresh token
        return new JwtResponseDto(newToken, refreshToken);
    }

    @Transactional
    public Boolean LogOut(String RefreshToken) {
        //logsout the user and deletes the refresh token from db
        //deletes the refresh token from db
        int res = refreshTokenRepository.deleteByRefreshToken(RefreshToken);
        log.info("[AuthService] LogOut  SUCCESS");
        if (res == 0) {
            log.error("[AuthService] LogOut  FAILURE");
            return false;
        }
        return true;
    }

}
