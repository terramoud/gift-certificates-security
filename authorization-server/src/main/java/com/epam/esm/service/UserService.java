package com.epam.esm.service;

import com.epam.esm.dto.UserDetailsDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDetails create(UserDetailsDto userDetailsDto);
}
