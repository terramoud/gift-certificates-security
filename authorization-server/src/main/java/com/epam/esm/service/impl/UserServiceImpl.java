package com.epam.esm.service.impl;

import com.epam.esm.dto.PasswordDto;
import com.epam.esm.dto.UserDetailsDto;
import com.epam.esm.exceptions.ErrorCodes;
import com.epam.esm.exceptions.ResourceNotFoundException;
import com.epam.esm.model.entity.User;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.epam.esm.config.ConstantsValidationMessages.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public UserDetailsDto create(UserDetailsDto userDetailsDto) {
        User newUser = userDetailsDto.toUser();
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        return UserDetailsDto.toDto(newUser);
    }

    @Override
    public UserDetailsDto changePassword(Long userId, PasswordDto passwordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        USER_NOT_FOUND, userId, ErrorCodes.NOT_FOUND_USER_RESOURCE));
        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException(BAD_PASSWORD_CREDENTIALS);
        }
        String encodedPassword = passwordEncoder.encode(passwordDto.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return UserDetailsDto.toDto(user);
    }
}
