package com.epam.esm.service.impl;

import com.epam.esm.config.ServiceTestConfig;
import com.epam.esm.domain.converter.impl.OrderDtoConverter;
import com.epam.esm.domain.converter.impl.UserDtoConverter;
import com.epam.esm.domain.entity.Role;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.domain.payload.UserFilterDto;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.ResourceNotFoundException;
import com.epam.esm.repository.api.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestConfig.class})
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceImplTest {

    private static final UserDto USER_TO_UPDATE = new UserDto(
            1L,
            "updated",
            "updated@email.com",
            "pass",
            Role.USER);

    private static final UserDto EXPECTED = new UserDto(
            1L,
            "updated",
            "updated@email.com",
            "pass",
            Role.USER);

    /**
     * Create a mock implementation of the TagRepository
     */
    @Mock
    public final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @Mock
    private final UserDtoConverter converter = Mockito.mock(UserDtoConverter.class);

    @Mock
    private final OrderDtoConverter orderConverter = Mockito.mock(OrderDtoConverter.class);

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, converter, orderConverter);
    }

    @AfterEach
    void tearDown() {
        reset(userRepository);
        reset(converter);
        reset(orderConverter);
    }

    /**
     * @see UserServiceImpl#findAll(UserFilterDto, Pageable)
     */
    @Test
    void testFindAllShouldReturnSortedListUsersByNameAndId() {
        List<User> users = LongStream.range(0, 9L)
                .mapToObj(i -> new User(i, "userLogin" + i,
                        i + "admin@gmail.com",
                        "password",
                        Role.USER))
                .sorted(Comparator.comparing(User::getLogin)
                        .thenComparing(User::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        List<UserDto> expectedUsers = LongStream.range(0, 9L)
                .mapToObj(i -> new UserDto(i, "userLogin" + i,
                        i + "admin@gmail.com",
                        "password",
                        Role.USER))
                .sorted(Comparator.comparing(UserDto::getLogin)
                        .thenComparing(UserDto::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        when(userRepository.findAll(any(UserFilterDto.class), any(Pageable.class)))
                .thenReturn(users);
        when(converter.toDto(anyList())).thenReturn(expectedUsers);
        assertEquals(expectedUsers,
                userService.findAll(new UserFilterDto(), Pageable.unpaged()));
    }

    /**
     * @see UserServiceImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldReturnUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(converter.toDto(any(User.class))).thenReturn(EXPECTED);
        assertEquals(EXPECTED, userService.findById(anyLong()));
    }

    /**
     * @see UserServiceImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldThrowExceptionWhenUserIsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
    }

    /**
     * @see UserServiceImpl#create(UserDto)
     */
    @Test
    void testCreateShouldCreateNewUserAndReturnUserDto() {
        when(converter.toEntity(any(UserDto.class))).thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());
        when(converter.toDto(any(User.class))).thenReturn(EXPECTED);
        assertEquals(EXPECTED, userService.create(new UserDto()));
    }

    /**
     * @see UserServiceImpl#deleteById(Long)
     */
    @Test
    void testDeleteByIdShouldDeleteUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(converter.toDto(any(User.class))).thenReturn(EXPECTED);
        assertEquals(EXPECTED, userService.deleteById(anyLong()));
    }

    /**
     * @see UserServiceImpl#deleteById(Long)
     */
    @Test
    void testDeleteByIdShouldThrowExceptionWhenUserIsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(1L));
    }

    /**
     * @see UserServiceImpl#update(Long, UserDto)
     */
    @Test
    void testUpdateShouldReturnUpdatedUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(converter.toEntity(any(UserDto.class))).thenReturn(new User());
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(converter.toDto(any(User.class))).thenReturn(EXPECTED);
        assertEquals(EXPECTED, userService.update(1L, USER_TO_UPDATE));
    }

    /**
     * @see UserServiceImpl#update(Long, UserDto)
     */
    @Test
    void testUpdateShouldThrowExceptionWhenUserIsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> userService.update(1L, USER_TO_UPDATE));
    }

    /**
     * @see UserServiceImpl#update(Long, UserDto)
     */
    @Test
    void testUpdateShouldThrowExceptionWhenUserIsNotEquals() {
        assertThrows(InvalidResourcePropertyException.class,
                () -> userService.update(999L, USER_TO_UPDATE));
    }
}