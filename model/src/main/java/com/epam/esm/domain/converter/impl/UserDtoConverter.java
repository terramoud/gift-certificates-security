package com.epam.esm.domain.converter.impl;


import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.UserDto;
import org.springframework.stereotype.Component;

/**
 * Converter class that converts a User entity to a UserDto object and vice versa.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Component
public class UserDtoConverter implements DtoConverter<User, UserDto> {

    @Override
    public User toEntity(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getLogin(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getRole()
        );
    }

    @Override
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}

