package org.lab;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lab.entities.User;
import org.lab.services.UserService;
import org.lab.services.impl.UserServiceImpl;

public class UserServiceTest {
    static UserService userService;

    @BeforeAll
    static void setup() {
        userService = new UserServiceImpl();
    }

    @Test
    void smoke() {
        var users = userService.listUsers();
        assertThat(users).hasSize(0);

        var user = userService.register("new user");
        assertThat(user).extracting(User::getName).isEqualTo("new user");

        users = userService.listUsers();
        assertThat(users).hasSize(1);
        assertThat(users).first().extracting(User::getName).isEqualTo("new user");
    }
}
