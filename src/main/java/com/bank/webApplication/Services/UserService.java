package com.bank.webApplication.Services;


import com.bank.webApplication.Config.MapperConfig;
import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Dto.UserDto;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import jakarta.persistence.Temporal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private DtoEntityMapper dtoEntityMapper;
    @Autowired
    private final DtoEntityMapper mapper;
    private final LogService logService;

    // Common formatter for createdAt and updatedAt

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //Helper Method To Get TimeStamp As String

    private String getCurrentTimestampString() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public UserDto CreateUser(UserDto userDto, String userId) {
        log.info("[UserService] CreateUser entered SUCCESS");

        UserEntity userEntity = mapper.convertToEntity(userDto, UserEntity.class);

        String now = getCurrentTimestampString();
        userEntity.setCreated_At(now);
        userEntity.setUpdated_At(now);

        UUID userUUID = UUID.fromString(userId);

        userEntity.setId(userUUID);

        UserEntity savedUSer = userRepository.save(userEntity);
        logService.logintoDB(userUUID, LogEntity.Action.PROFILE_MANAGEMENT, "New User Created", userEntity.getName(), LogEntity.Status.SUCCESS);
        log.info("[UserService] CreateUser  SUCCESS");
        return (mapper.convertToDto(savedUSer, UserDto.class));
    }


    // Update  User Details

    public UserDto UpdateUser(String id, UserDto userDto) {
        log.info("[UserService] UpdateUser   entered SUCCESS");
        UserEntity existing = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.error("[UserService] UpdateUser: User Not Found FAILURE");
                    return new RuntimeException("User Not Found With Id :  " + id);
                });

        existing.setName(userDto.getName());
        existing.setEmail(userDto.getEmail());
        existing.setPhone(userDto.getPhone());
        existing.setRole(userDto.getRole());
        existing.setUpdated_At(getCurrentTimestampString());
        existing.setAddress(userDto.getAddress());

        UserEntity updated = userRepository.save(existing);

        //LOGGING
        logService.logintoDB(UUID.fromString(id), LogEntity.Action.PROFILE_MANAGEMENT, " User Updated", updated.getName(), LogEntity.Status.SUCCESS);
        log.info("[UserService] UpdateUser   SUCCESS");
        return (mapper.convertToDto(updated, UserDto.class));

    }


    //Display User Details

    public UserDto getUserById(String id) {
        log.info("[UserService] getUserById  entered SUCCESS");
        UserEntity getUser = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.error("[UserService] getUserById: User Not Found  FAILURE");
                    return new RuntimeException("User Not Found With Id : " + id);
                });

        //LOGGING
        logService.logintoDB(UUID.fromString(id), LogEntity.Action.PROFILE_MANAGEMENT, "User Details Displayed", getUser.getName(), LogEntity.Status.SUCCESS);
        log.info("[UserService] getUserById  SUCCESS");
        return (mapper.convertToDto(getUser, UserDto.class));
    }

    // Get all users for Admin
    public List<UserDto> getAllUsers() {
        log.info("[User Service] getAllAccounts entered SUCCESS");
        List<UserEntity> allUsers = userRepository.findAll();
        if(allUsers.isEmpty()){
            log.error("[User Service] No Users exist in Database");
            throw new UserNotFoundException("No Users Exist in Database");
        }
        List<UserDto> userDtos = allUsers.stream()
                .map(user -> dtoEntityMapper.convertToDto(user, UserDto.class))
                .collect(Collectors.toList());
        log.info("[User Service] getAllAccounts  SUCCESS");
        return userDtos;
    }


}
