package com.epam.esm.service.api;

import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.domain.payload.UserFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserService extends BaseService<UserDto, Long> {

    List<UserDto> findAll(UserFilterDto filter, Pageable pageable);
}
