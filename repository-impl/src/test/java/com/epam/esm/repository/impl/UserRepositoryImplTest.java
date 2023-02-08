package com.epam.esm.repository.impl;

import com.epam.esm.config.RepositoryTestConfig;
import com.epam.esm.config.TestUsers;
import com.epam.esm.domain.entity.User;
import com.epam.esm.repository.api.BaseRepository;
import com.epam.esm.repository.api.UserRepository;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepositoryTestConfig.class)
@Transactional
class UserRepositoryImplTest {

    @PersistenceContext
    protected EntityManager em;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(em);
    }

    /**
     * @see UserRepositoryImpl#findAll(LinkedMultiValueMap, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAll")
    void testFindAllShouldReturnSortedListUsersByIdAndLogin(LinkedMultiValueMap<String, String> fields,
                                                          Pageable pageable,
                                                          Comparator<User> userComparator) {
        List<User> userList = em.createQuery(
                "SELECT u FROM User u ORDER BY u.id ASC", User.class).getResultList();
        List<User> expected = List.of(userList.stream()
                .sorted(userComparator)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toArray(User[]::new));
        List<User> users = userRepository.findAll(fields, pageable);
        assertEquals(expected, users);
    }

    /**
     * @see UserRepositoryImpl#findAll(LinkedMultiValueMap, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllBySearchPartOfNameAndFilter")
    void testFindAllShouldReturnSortedListUsersBySearch(LinkedMultiValueMap<String, String> fields,
                                                       Pageable pageable,
                                                       Comparator<User> userComparator) {
        String searchQuery = fields.getOrDefault("search", List.of("")).get(0);
        String login = fields.getOrDefault("login", List.of("")).get(0);
        String andLoginEqualsUserLogin = (login.isEmpty()) ? "" : "and t.name = '" + login + "'";
        List<User> userListByBySearchPartOfName = em.createQuery(
                "SELECT u FROM User u WHERE " +
                        "u.login like '%" + searchQuery + "%' " + andLoginEqualsUserLogin +
                        " or u.email like '%" + searchQuery + "%' " + andLoginEqualsUserLogin +
                        " ORDER BY u.id ASC", User.class).getResultList();
        List<User> expected = List.of(userListByBySearchPartOfName.stream()
                .sorted(userComparator)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toArray(User[]::new));
        List<User> users = userRepository.findAll(fields, pageable);
        assertEquals(expected, users);
    }

    /**
     * @see UserRepositoryImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldReturnUserWithId() {
        TestUsers testUsers = new TestUsers();
        Optional<User> expected = Optional.of(testUsers.user2);
        Optional<User> user = userRepository.findById(2L);
        assertEquals(expected, user);
    }

    /**
     * @see UserRepositoryImpl#save(User)
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
     * @see BaseRepository#update(com.epam.esm.domain.entity.AbstractEntity)
     */
    @Test
    void testUpdateShouldUpdateEntityInDB() {
        User user = em.find(User.class, 2L);
        user.setLogin("changed login");
        User updatedUser = userRepository.update(user);
        User expected = userRepository.findById(2L).orElseThrow();
        assertEquals(expected, updatedUser);
    }

    /**
     * @see UserRepositoryImpl#delete(User)
     */
    @Test
    void testDeleteShouldDeleteEntityInDB() {
        Optional<User> userToDelete = userRepository.findById(1L);
        userRepository.delete(userToDelete.orElseThrow());
        Optional<User> user = userRepository.findById(1L);
        assertThat(user).isEmpty();
    }

    private static Stream<Arguments> testCasesForFindAll() {
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+id, -login"))),
                        PageRequest.of(2, 3),
                        Comparator.comparing(User::getId)
                                .thenComparing(User::getLogin, Comparator.reverseOrder())),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of(""))),
                        PageRequest.of(1, 5),
                        Comparator.comparing(User::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-id"))),
                        PageRequest.of(1, 5),
                        Comparator.comparing(User::getId, Comparator.reverseOrder())),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+login"))),
                        PageRequest.of(2, 3),
                        Comparator.comparing(User::getLogin)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-id, -login"))),
                        PageRequest.of(1, 5),
                        Comparator.comparing(User::getId, Comparator.reverseOrder())
                                .thenComparing(User::getLogin, Comparator.reverseOrder())),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("", List.of(""))),
                        PageRequest.of(1, 5),
                        Comparator.comparing(User::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("", List.of(""))),
                        PageRequest.of(1, 1),
                        Comparator.comparing(User::getId))
        );
    }

    private static Stream<Arguments> testCasesForFindAllBySearchPartOfNameAndFilter() {
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of(""))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(User::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of("Jon"))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(User::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of("d"))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(User::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "search", List.of("st"),
                                "sort", List.of("-id, +login"))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(User::getId, Comparator.reverseOrder())
                                .thenComparing(User::getLogin)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "search", List.of("n"),
                                "sort", List.of("+login, -id"))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(User::getLogin)
                                .thenComparing(User::getId), Comparator.reverseOrder())
        );
    }
}