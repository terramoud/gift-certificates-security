package com.epam.esm.service;

import com.epam.esm.dto.UserDetailsDto;
import com.epam.esm.entity.User;
import com.epam.esm.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));
    }

    @Override
    public UserDetails create(UserDetailsDto userDetailsDto) {
        User newUser = userDetailsDto.toUser();
        newUser.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
        userRepository.save(newUser);
        return newUser;
    }
}
