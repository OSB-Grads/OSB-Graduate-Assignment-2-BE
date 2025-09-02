package com.bank.webApplication.Config;


import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Entity.AccountEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();


        mapper.typeMap(AccountEntity.class, AccountDto.class)
                .addMapping(src -> src.getUser().getId(), AccountDto::setUserId);

        return mapper;
    }

}
