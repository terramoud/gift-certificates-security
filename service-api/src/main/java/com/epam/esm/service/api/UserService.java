package com.epam.esm.service.api;

import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.domain.payload.UserFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface represents the business logic for managing users.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public interface UserService extends BaseService<UserDto, Long> {
    /**
     * Returns a list of all users according to the specified
     * filter and paging criteria.
     *
     * @param filter the filter criteria to apply to the search
     * @param pageable the paging criteria to apply to the search
     * @return a list of user DTOs matching the search criteria
     */
    List<UserDto> findAll(UserFilterDto filter, Pageable pageable);
}
