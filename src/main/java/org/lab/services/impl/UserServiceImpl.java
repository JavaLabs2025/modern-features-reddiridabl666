package org.lab.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lab.entities.User;
import org.lab.services.UserService;

public class UserServiceImpl implements UserService {
    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> listUsers() {
        return users;
    }

    @Override
    public User register(String username) {
        var user = new User(UUID.randomUUID(), username);
        users.add(user);
        return user;
    }
}
