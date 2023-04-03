package com.epam.esm.repository.api;

import com.epam.esm.config.RepositoryTestConfig;
import com.epam.esm.config.TestUsers;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.UserFilterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepositoryTestConfig.class)
@Transactional
class UserRepositoryTest {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

    }

    /**
     * @see UserRepository#findAll(UserFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAll")
    void testFindAllShouldReturnSortedListUsersByIdAndLogin(UserFilterDto userFilterDto,
                                                            Pageable pageable,
                                                            List<User> expected) {
        List<User> users = userRepository.findAll(userFilterDto, pageable);
        assertEquals(expected, users);
    }

    /**
     * @see UserRepository#findAll(UserFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllBySearchPartOfNameAndFilter")
    void testFindAllShouldReturnSortedListUsersBySearch(UserFilterDto userFilterDto,
                                                        Pageable pageable,
                                                        List<User> expected) {
        List<User> users = userRepository.findAll(userFilterDto, pageable);
        assertEquals(expected, users);
    }

    /**
     * @see UserRepository#findById(Object)
     */
    @Test
    void testFindByIdShouldReturnUserWithId() {
        TestUsers testUsers = new TestUsers();
        Optional<User> expected = Optional.of(testUsers.user2);
        Optional<User> user = userRepository.findById(2L);
        assertEquals(expected, user);
    }

    /**
     * @see UserRepository#save(Object)
     */
    @Test
    void testSaveShouldCreateEntityInDB() {
        TestUsers testUsers = new TestUsers();
        User newUser = testUsers.user1;
        testUsers.user1.setId(null);
        newUser.setLogin("new User");
        newUser.setEmail("new Email");
        User savedUser = userRepository.save(newUser);
        Optional<User> expected = userRepository.findById(savedUser.getId());
        assertEquals(expected.orElseThrow(), newUser);
    }

    /**
     * @see BaseRepository#save(Object)
     */
    @Test
    void testUpdateShouldUpdateEntityInDB() {
        User user = em.find(User.class, 2L);
        user.setLogin("changed login");
        User updatedUser = userRepository.save(user);
        User expected = userRepository.findById(2L).orElseThrow();
        assertEquals(expected, updatedUser);
    }

    /**
     * @see UserRepository#delete(Object)
     */
    @Test
    void testDeleteShouldDeleteEntityInDB() {
        Optional<User> userToDelete = userRepository.findById(1L);
        userRepository.delete(userToDelete.orElseThrow());
        Optional<User> user = userRepository.findById(1L);
        assertThat(user).isEmpty();
    }

    private static Stream<Arguments> testCasesForFindAll() {
        TestUsers ts = new TestUsers();
        return Stream.of(
                Arguments.of(
                        new UserFilterDto(),
                        PageRequest.of(2, 3, Sort.by("id").ascending()
                                .and(Sort.by("login").descending())),
                        List.of(ts.user7,
                                ts.user8,
                                ts.user9)),
                Arguments.of(
                        new UserFilterDto(),
                        PageRequest.of(1, 5, Sort.by("id")),
                        List.of(ts.user6,
                                ts.user7,
                                ts.user8,
                                ts.user9,
                                ts.user10)),
                Arguments.of(
                        new UserFilterDto(),
                        PageRequest.of(1, 5, Sort.by("id").descending()),
                        List.of(ts.user7,
                                ts.user6,
                                ts.user5,
                                ts.user4,
                                ts.user3)),
                Arguments.of(
                        new UserFilterDto(),
                        PageRequest.of(2, 3, Sort.by("login")),
                        List.of(ts.user11,
                                ts.user9,
                                ts.user12)),
                Arguments.of(
                        new UserFilterDto(),
                        PageRequest.of(1, 5, Sort.by("id").descending()
                                .and(Sort.by("login").descending())),
                        List.of(ts.user7,
                                ts.user6,
                                ts.user5,
                                ts.user4,
                                ts.user3)),
                Arguments.of(
                        new UserFilterDto(),
                        PageRequest.of(1, 5, Sort.by("id")),
                        List.of(ts.user6,
                                ts.user7,
                                ts.user8,
                                ts.user9,
                                ts.user10)),
                Arguments.of(
                        new UserFilterDto(),
                        PageRequest.of(1, 1, Sort.by("id")),
                        List.of(ts.user2))
        );
    }

    private static Stream<Arguments> testCasesForFindAllBySearchPartOfNameAndFilter() {
        TestUsers ts = new TestUsers();
        return Stream.of(
                Arguments.of(
                        new UserFilterDto(),
                        PageRequest.of(0, 10, Sort.by("id")),
                        List.of(ts.user1,
                                ts.user2,
                                ts.user3,
                                ts.user4,
                                ts.user5,
                                ts.user6,
                                ts.user7,
                                ts.user8,
                                ts.user9,
                                ts.user10)),
                Arguments.of(
                        UserFilterDto.builder()
                                .loginContaining("Jon")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("id")),
                        List.of(ts.user4)),
                Arguments.of(
                        UserFilterDto.builder()
                                .loginContaining("d")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("id")),
                        List.of(ts.user1)),
                Arguments.of(
                        UserFilterDto.builder()
                                .loginContaining("st")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("id").descending()
                                .and(Sort.by("login").ascending())),
                        List.of(ts.user11,
                                ts.user9,
                                ts.user3)),
                Arguments.of(
                        UserFilterDto.builder()
                                .loginContaining("n")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("login").ascending()
                                .and(Sort.by("id").descending())),
                        List.of(ts.user10,
                                ts.user4,
                                ts.user6,
                                ts.user9,
                                ts.user12,
                                ts.user1)),
                Arguments.of(
                        UserFilterDto.builder()
                                .login("Peter")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("login").ascending()
                                .and(Sort.by("id").descending())),
                        List.of(ts.user2)),
                Arguments.of(
                        UserFilterDto.builder()
                                .email("Stepan@gmail.com")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("login").ascending()
                                .and(Sort.by("id").descending())),
                        List.of(ts.user9)),
                Arguments.of(
                        UserFilterDto.builder()
                                .emailContaining("r@gmail")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("login").ascending()
                                .and(Sort.by("id").descending())),
                        List.of(ts.user8,
                                ts.user2,
                                ts.user3))
        );
    }
}
