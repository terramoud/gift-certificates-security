package com.epam.esm.service;

import com.epam.esm.dto.PasswordDto;
import com.epam.esm.dto.UserDetailsDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDetailsDto create(UserDetailsDto userDetailsDto);

    UserDetailsDto changePassword(Long userId, PasswordDto passwordDto);
}
