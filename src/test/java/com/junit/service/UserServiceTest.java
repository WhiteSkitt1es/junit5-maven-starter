package com.junit.service;

import com.junit.TestBase;
import com.junit.dto.User;
import com.junit.extension.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.*;

@Tag("fast")
@Tag("user")
@ExtendWith({
//        UserServiceParamResolver.class,
//        PostProcessingExtension.class,
//        ConditionalExtension.class,
//        ThrowableExtension.class
//        GlobalExtension.class
})
//@TestMethodOrder(MethodOrderer.Random.class) Random execution of tested methods
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class) Executing tested methods in a given order
//@TestMethodOrder(MethodOrderer.MethodName.class) Execution of tested methods in alphabetical order
//@TestMethodOrder(MethodOrderer.DisplayName.class) Display a method under a different name
@TestInstance(Lifecycle.PER_CLASS)
public class UserServiceTest extends TestBase {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PAVEL = User.of(2, "Pavel", "321");
    private UserService userService;

    UserServiceTest() {

    }

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each: " + this);
        this.userService = userService;
    }

    @Test
    @Order(1)
    void usersEmptyIfNoUserAdded() throws IOException {
        if (true) {
            throw new IOException();
        }
        System.out.println("Test 1: " + this);
        List<User> users = userService.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    @Order(2)
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PAVEL);

        List<User> users = userService.getAll();
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("Conversion to map where the key is id")
    void usersConvertedToMapById() {
        userService.add(IVAN, PAVEL);
        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PAVEL.getId()),
                () -> assertThat(users).containsValues(IVAN, PAVEL)
        );
    }

    @AfterEach
    void deleteDataFromDataBase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }

    @Nested
    @Tag("login")
    @DisplayName("Testing user login functionality")
    class LoginTest {
        @Test
        @Disabled("flaky, need to see")
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUserName(), "1234");
            assertTrue(maybeUser.isEmpty());
        }

        @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
        void loginFailIfUserDoesNotExist() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login("Vanya", IVAN.getPassword());
            assertTrue(maybeUser.isEmpty());
        }

        @Test
//        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void checkLoginFunctionalityPerformance() {
            assertTimeout(Duration.ofMillis(200L), () -> {
                Thread.sleep(100L);
                return userService.login("dummy", IVAN.getPassword());
            });
        }

        @Test
        void loginSuccessIfUserExist() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUserName(), IVAN.getPassword());

            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
        }

        @Test
        void throwExceptionIfUsernameOePasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );
        }

        @ParameterizedTest
//        @ArgumentsSource()
//        @NullSource
//        @EmptySource
//        @NullAndEmptySource
//        @ValueSource(strings = {
//                "Ivan", "Pavel"
//        })
//        @EnumSource
        @MethodSource("com.junit.service.UserServiceTest#getArgumentsForLoginTest")
//        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
        void loginParameterizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PAVEL);
            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }
    }
    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Pavel", "321", Optional.of(PAVEL))
        );
    }
}
