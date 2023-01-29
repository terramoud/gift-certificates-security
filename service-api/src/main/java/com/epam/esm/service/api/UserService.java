package com.epam.esm.service.api;

import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.UserDto;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;


public interface UserService {
    List<UserDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto);

    UserDto findById(Long id);

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    UserDto deleteById(Long id);

}
